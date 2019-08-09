package opt.data;

import jaxb.ModelParams;

import java.util.*;
import java.util.stream.Collectors;

public class Scenario {

    protected Map<Long, Node> nodes = new HashMap<>();
    protected Map<Long, AbstractLink> links = new HashMap<>();
    protected Map<Long, Commodity> commodities = new HashMap<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    protected Scenario(){}

    protected Scenario(jaxb.Scenario scenario) throws Exception {

        // network
        Map<Long,jaxb.Roadparam> road_params = new HashMap<>();
        if (scenario.getNetwork()!=null){

            jaxb.Network network = scenario.getNetwork();

            if (network.getRoadparams()!=null)
                for(jaxb.Roadparam rp: network.getRoadparams().getRoadparam())
                    road_params.put(rp.getId(),rp);

            if (network.getNodes()!=null)
                for(jaxb.Node jnode : network.getNodes().getNode())
                    nodes.put(jnode.getId(),new Node(jnode));

            if (network.getLinks()!=null)
                for(jaxb.Link jlink : network.getLinks().getLink()) {
                    AbstractLink link;
                    switch(jlink.getRoadType()){
                        case "offramp":
                            link = new LinkOfframp(jlink,road_params.get(jlink.getRoadparam()));
                            break;
                        case "onramp":
                            link = new LinkOnramp(jlink,road_params.get(jlink.getRoadparam()));
                            break;
                        case "freeway":
                            link = new LinkFreeway(jlink,road_params.get(jlink.getRoadparam()));
                            break;
                        case "connector":
                            link = new LinkConnector(jlink,road_params.get(jlink.getRoadparam()));
                            break;
                        default:
                            throw new Exception("Bad road type");
                    }
                    links.put(jlink.getId(),link);
                    nodes.get(jlink.getEndNodeId()).in_links.add(link.id);
                    nodes.get(jlink.getStartNodeId()).out_links.add(link.id);
                }
        }

        // make link connections
        for(AbstractLink abslink : links.values()){

            Set<AbstractLink> up_links = nodes.get(abslink.start_node_id).in_links.stream()
                    .map(link_id -> links.get(link_id))
                    .collect(Collectors.toSet());

            Set<AbstractLink> dn_links = nodes.get(abslink.end_node_id).out_links.stream()
                    .map(link_id -> links.get(link_id))
                    .collect(Collectors.toSet());

            Set<AbstractLink> up_links_f = null;
            Set<AbstractLink> dn_links_f = null;
            switch(abslink.type){

                case freeway:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    break;

                case connector:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkOfframp)
                            .map(link -> (LinkOfframp)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkOnramp)
                            .map(link -> (LinkOnramp)link)
                            .collect(Collectors.toSet());

                    break;

                case onramp:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkConnector)
                            .map(link -> (LinkConnector)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    break;

                case offramp:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkConnector)
                            .map(link -> (LinkConnector)link)
                            .collect(Collectors.toSet());

                    break;
            }

            if(up_links_f!=null && !up_links_f.isEmpty())
                abslink.up_link = up_links_f.iterator().next();

            if(dn_links_f!=null && !dn_links_f.isEmpty())
                abslink.dn_link = dn_links_f.iterator().next();

        }

        // commodities
        if(scenario.getCommodities()!=null)
            for(jaxb.Commodity comm : scenario.getCommodities().getCommodity())
                this.commodities.put(comm.getId(),new Commodity(comm.getId(),comm.getName()));

    }

    public Scenario deep_copy() {
        Scenario jscn_cpy = new Scenario();

        // create new nodes
        for (Map.Entry<Long, Node> e : nodes.entrySet())
            jscn_cpy.nodes.put(e.getKey(), new Node(e.getKey()));

        // create new links
        for (Map.Entry<Long, AbstractLink> e : links.entrySet())
            jscn_cpy.links.put(e.getKey(),e.getValue().deep_copy());

        // set node inlinks and outlinks
        for (Node node_cpy : jscn_cpy.nodes.values()){
            Node node_org = nodes.get(node_cpy.id);
            node_cpy.out_links.addAll(node_org.out_links);
            node_cpy.in_links.addAll(node_org.in_links);
        }

        // commodities
        for (Map.Entry<Long,Commodity> e : commodities.entrySet())
            jscn_cpy.commodities.put(e.getKey(),e.getValue().deep_copy());


//        // set road parameters
//        for(Map.Entry<Long,jaxb.Roadparam> e : jscn_org.road_params.entrySet()) {
//            long rp_id = e.getKey();
//            jaxb.Roadparam rp = e.getValue();
//            jaxb.Roadparam rp_cpy = new Roadparam();
//            rp_cpy.setId(rp_id);
//            rp_cpy.setName(rp.getName());
//            rp_cpy.setCapacity(rp.getCapacity());
//            rp_cpy.setJamDensity(rp.getJamDensity());
//            rp_cpy.setSpeed(rp.getSpeed());
//            jscn_cpy.road_params.put(rp_id, rp_cpy);
//        }

        return jscn_cpy;
    }

    /////////////////////////////////////
    // getters
    /////////////////////////////////////

    protected Set<RoadParam> get_road_params(){
        Set<RoadParam> road_params = new HashSet<>();
        for(AbstractLink link : links.values())
            road_params.add(new RoadParam(link.param.capacity_vphpl,link.param.ff_speed_kph,link.param.jam_density_vpkpl));

        // set ids
        long id = 0;
        for(RoadParam roadParam : road_params)
            roadParam.id = id++;
        return road_params;
    }

    public jaxb.Scenario to_jaxb(Collection<Segment> segments){
        jaxb.Scenario jScn = new jaxb.Scenario();

        // models : Hard code single CTM model
        jaxb.Models models = new jaxb.Models();
        jScn.setModels(models);
        jaxb.Model model = new jaxb.Model();
        models.getModel().add(model);

        model.setIsDefault(true);
        model.setName("ctm");
        model.setType("ctm");
        ModelParams params = new ModelParams();
        params.setMaxCellLength(100f);
        params.setSimDt(2f);
        model.setModelParams(params);

        // commodities
        jaxb.Commodities jComms = new jaxb.Commodities();
        jScn.setCommodities(jComms);

        for(Map.Entry<Long, Commodity> e : commodities.entrySet()){
            jaxb.Commodity jcomm = new jaxb.Commodity();
            jComms.getCommodity().add(jcomm);
            Commodity comm = e.getValue();
            jcomm.setId(comm.id);
            jcomm.setName(comm.name);
            jcomm.setPathfull(false);
        }

        // network
        jaxb.Network jNet = new jaxb.Network();
        jScn.setNetwork(jNet);

        // nodes
        jaxb.Nodes jNodes = new jaxb.Nodes();
        jNet.setNodes(jNodes);
        for(Node node : nodes.values()) {
            jaxb.Node jaxbNode = new jaxb.Node();
            jaxbNode.setId(node.id);
            jNodes.getNode().add(jaxbNode);
        }

        jaxb.Roadparams jRoadParams = new jaxb.Roadparams();
        jNet.setRoadparams(jRoadParams);
        Set<RoadParam> road_params = get_road_params();
        for(RoadParam jrp : road_params){
            jaxb.Roadparam jaxbrp = new jaxb.Roadparam();
            jaxbrp.setId(jrp.id);
            jaxbrp.setCapacity(jrp.capacity);
            jaxbrp.setSpeed(jrp.speed);
            jaxbrp.setJamDensity(jrp.jam_density);
            jRoadParams.getRoadparam().add(jaxbrp);
        }

        // links
        jaxb.Links jLinks = new jaxb.Links();
        jNet.setLinks(jLinks);
        for(AbstractLink link : links.values()){
            jaxb.Link jaxbLink = new jaxb.Link();
            jaxbLink.setId(link.id);
            jaxbLink.setLength(link.length_meters);
            jaxbLink.setFullLanes(link.full_lanes);
            jaxbLink.setEndNodeId(link.end_node_id);
            jaxbLink.setStartNodeId(link.start_node_id);
            jaxbLink.setRoadType(link.type.toString());

            // road params
            RoadParam link_rp = new RoadParam(link.param.capacity_vphpl,link.param.ff_speed_kph,link.param.jam_density_vpkpl);
            long rp_id = road_params.stream().filter(rp->rp.equals(link_rp)).findFirst().get().id;

            jaxbLink.setRoadparam(rp_id);
            jLinks.getLink().add(jaxbLink);
        }

        // demands and splits
        jaxb.Demands jdemands = new jaxb.Demands();
        jScn.setDemands(jdemands);
        jaxb.Splits jsplits = new jaxb.Splits();
        jScn.setSplits(jsplits);

        // TODO PUT THIS BACK IN
//        for(Segment segment : segments){
//
//            if(!segment.fr_splits.isEmpty()) {
//
//
//                for (Map.Entry<Long, Profile1D> e : segment.fr_splits.entrySet()) {
//                    jaxb.SplitNode jsplitnode = new jaxb.SplitNode();
//                    jsplits.getSplitNode().add(jsplitnode);
//                    jaxb.Split jsplit = new jaxb.Split();
//                    jsplitnode.getSplit().add(jsplit);
//
//                    jsplitnode.setCommodityId(e.getKey());
//                    Profile1D profile = e.getValue();
//
//                    if(profile.dt!=null)
//                        jsplitnode.setDt(profile.dt);
//                    jsplitnode.setStartTime(profile.start_time);
//                    jsplitnode.setLinkIn(segment.fwy_id);
//                    jsplitnode.setNodeId(segment.fwy().end_node_id);
//                    jsplit.setLinkOut(segment.fr_id);
//                    jsplit.setContent(OTMUtils.comma_format(profile.values));
//                }
//            }
//
//            if(!segment.or_demands.isEmpty()) {
//                for (Map.Entry<Long, Profile1D> e : segment.or_demands.entrySet()) {
//                    jaxb.Demand jdemand = new jaxb.Demand();
//                    jdemands.getDemand().add(jdemand);
//                    jdemand.setCommodityId(e.getKey());
//                    Profile1D profile = e.getValue();
//                    jdemand.setDt(profile.dt);
//                    jdemand.setStartTime(profile.start_time);
//                    jdemand.setLinkId(segment.or_id);
//                    jdemand.setContent(OTMUtils.comma_format(profile.get_values()));
//                }
//            }
//
//            if(!segment.fwy_demands.isEmpty()) {
//                for (Map.Entry<Long, Profile1D> e : segment.fwy_demands.entrySet()) {
//                    jaxb.Demand jdemand = new jaxb.Demand();
//                    jdemands.getDemand().add(jdemand);
//                    jdemand.setCommodityId(e.getKey());
//                    Profile1D profile = e.getValue();
//                    if(profile.dt!=null)
//                        jdemand.setDt(profile.dt);
//                    jdemand.setStartTime(profile.start_time);
//                    jdemand.setLinkId(segment.fwy_id);
//                    jdemand.setContent(OTMUtils.comma_format(profile.get_values()));
//                }
//            }
//
//        }

        return jScn;
    }


    /////////////////////////////////////
    // Override
    /////////////////////////////////////


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scenario scenario = (Scenario) o;
        return nodes.equals(scenario.nodes) &&
                links.equals(scenario.links) &&
                commodities.equals(scenario.commodities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, links, commodities);
    }
}
