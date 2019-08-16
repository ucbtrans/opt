package opt.data;

import jaxb.ModelParams;
import profiles.Profile1D;
import utils.OTMUtils;

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
                            link = new LinkOfframp(jlink, road_params.get(jlink.getRoadparam()));
                            break;
                        case "onramp":
                            link = new LinkOnramp(jlink, road_params.get(jlink.getRoadparam()));
                            break;
                        case "freeway":
                            link = new LinkFreeway(jlink, road_params.get(jlink.getRoadparam()));
                            break;
                        case "connector":
                            link = new LinkConnector(jlink, road_params.get(jlink.getRoadparam()));
                            break;
                        default:
                            throw new Exception("Bad road type");
                    }
                    links.put(jlink.getId(),link);
                    nodes.get(jlink.getEndNodeId()).in_links.add(link.id);
                    nodes.get(jlink.getStartNodeId()).out_links.add(link.id);
                }
        }

        // commodities
        if(scenario.getCommodities()!=null)
            for(jaxb.Commodity comm : scenario.getCommodities().getCommodity())
                this.commodities.put(comm.getId(),new Commodity(comm.getId(),comm.getName()));

    }

    protected Scenario clone() {
        Scenario jscn_cpy = new Scenario();

        for (Map.Entry<Long, Node> e : nodes.entrySet())
            jscn_cpy.nodes.put(e.getKey(), e.getValue().clone());

        for (Map.Entry<Long, AbstractLink> e : links.entrySet())
            jscn_cpy.links.put(e.getKey(), e.getValue().clone());

        for (Map.Entry<Long,Commodity> e : commodities.entrySet())
            jscn_cpy.commodities.put(e.getKey(),e.getValue().clone());

        return jscn_cpy;
    }

    /////////////////////////////////////
    // protected and private
    /////////////////////////////////////

    protected jaxb.Scenario to_jaxb(Collection<Segment> segments){
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
        Map<Long,LinkParameters> link_params = get_link_params();
        for(Map.Entry<Long,LinkParameters> e : link_params.entrySet()){
            Long id = e.getKey();
            LinkParameters param = e.getValue();
            jaxb.Roadparam jaxbrp = new jaxb.Roadparam();
            jaxbrp.setId(id);
            jaxbrp.setCapacity(param.capacity_vphpl);
            jaxbrp.setSpeed(param.ff_speed_kph);
            jaxbrp.setJamDensity(param.jam_density_vpkpl);
            jRoadParams.getRoadparam().add(jaxbrp);
        }

        // links
        jaxb.Links jLinks = new jaxb.Links();
        jNet.setLinks(jLinks);
        for(AbstractLink link : links.values()){
            jaxb.Link jaxbLink = new jaxb.Link();
            jLinks.getLink().add(jaxbLink);

            jaxbLink.setId(link.id);
            jaxbLink.setLength((float) link.get_length_meters());
            jaxbLink.setFullLanes(link.get_gp_lanes());
            jaxbLink.setEndNodeId(link.end_node_id);
            jaxbLink.setStartNodeId(link.start_node_id);
            jaxbLink.setRoadType(link.get_type().toString());

            // road params
            Set<Long> param_ids = link_params.entrySet().stream()
                    .filter(e->e.getValue().equals(link.params))
                    .map(e->e.getKey())
                    .collect(Collectors.toSet());
            jaxbLink.setRoadparam(param_ids.iterator().next());

        }

        // demands
        jaxb.Demands jdemands = new jaxb.Demands();
        jScn.setDemands(jdemands);
        for(AbstractLink link : links.values()) {
            for (Map.Entry<Long, Profile1D> e : link.demands.entrySet()) {
                Long comm_id = e.getKey();
                Profile1D profile = e.getValue();

                jaxb.Demand jdemand = new jaxb.Demand();
                jdemands.getDemand().add(jdemand);
                jdemand.setCommodityId(comm_id);
                jdemand.setDt(profile.dt);
                jdemand.setStartTime(profile.start_time);
                jdemand.setLinkId(link.id);
                jdemand.setContent(OTMUtils.comma_format(profile.get_values()));
            }
        }

        // splits
        jaxb.Splits jsplits = new jaxb.Splits();
        jScn.setSplits(jsplits);

        for(Node node : nodes.values() ) {

            boolean has_splits = node.out_links.stream()
                    .map(link_id->links.get(link_id))
                    .anyMatch(link->!link.splits.isEmpty());

            if(!has_splits)
                continue;

            Set<LinkFreeway> in_links = node.in_links.stream()
                    .map(link_id->links.get(link_id))
                    .filter(link->link instanceof LinkFreeway)
                    .map(link-> (LinkFreeway) link)
                    .collect(Collectors.toSet());

            if(in_links.size()>1)
                System.err.println("90443j2f");

            LinkFreeway in_link = in_links.iterator().next();

            Set<Long> comm_ids = node.out_links.stream()
                    .map(link_id->links.get(link_id))
                    .flatMap(link->link.splits.keySet().stream())
                    .collect(Collectors.toSet());

            for(Long comm_id : comm_ids){

                // get unique dt and start_time
                Set<Profile1D> profiles = node.out_links.stream()
                        .map(link_id->links.get(link_id))
                        .map(link->link.splits.get(comm_id))
                        .collect(Collectors.toSet());

                Set<Float> dts = profiles.stream().map(prof->prof.dt).collect(Collectors.toSet());
                if(dts.size()!=1)
                    System.err.println("RG)@J$G 2-43j");

                Set<Float> start_times = profiles.stream().map(prof->prof.start_time).collect(Collectors.toSet());
                if(start_times.size()!=1)
                    System.err.println("535h3");

                // to jaxb
                jaxb.SplitNode jsplitnode = new jaxb.SplitNode();
                jsplits.getSplitNode().add(jsplitnode);
                jsplitnode.setCommodityId(comm_id);
                jsplitnode.setNodeId(node.id);
                jsplitnode.setLinkIn(in_link.id);
                jsplitnode.setDt(dts.iterator().next());
                jsplitnode.setStartTime(start_times.iterator().next());

                // assumes that *all* out links have splits defined
                // and they sum up to 1.

                for( Long outlink_id : node.out_links ){

                    jaxb.Split jsplit = new jaxb.Split();
                    jsplitnode.getSplit().add(jsplit);

                    AbstractLink outlink = links.get(outlink_id);
                    jsplit.setLinkOut(outlink_id);
                    jsplit.setContent(OTMUtils.comma_format(outlink.splits.get(comm_id).values));
                }

            }

        }

        return jScn;
    }

    private Map<Long,LinkParameters> get_link_params(){
        Set<LinkParameters> link_params_set = links.values().stream()
                .map(link->link.params)
                .collect(Collectors.toSet());

        // set ids
        Map<Long,LinkParameters> link_params_map = new HashMap<>();
        long id = 0;
        for(LinkParameters link_param : link_params_set)
            link_params_map.put(id++,link_param);
        return link_params_map;
    }

    /////////////////////////////////////
    // override
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
