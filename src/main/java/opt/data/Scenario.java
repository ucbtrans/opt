package opt.data;

import java.util.*;

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
                        case "ramp":
                            link = new LinkRamp(jlink,road_params.get(jlink.getRoadparam()));
                            break;
                        case "mainline":
                            link = new LinkMainline(jlink,road_params.get(jlink.getRoadparam()));
                            break;
                        default:
                            throw new Exception("Bad road type");
                    }
                    links.put(jlink.getId(),link);
                    nodes.get(jlink.getEndNodeId()).in_links.add(link);
                    nodes.get(jlink.getStartNodeId()).out_links.add(link);
                }
        }


        for(AbstractLink link : links.values())
            link.is_source = nodes.get(link.start_node_id).in_links.isEmpty();

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
            for(AbstractLink link_org : node_org.out_links)
                node_cpy.out_links.add(jscn_cpy.links.get(link_org.id));
            for(AbstractLink link_org : node_org.in_links)
                node_cpy.in_links.add(jscn_cpy.links.get(link_org.id));
        }

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
            road_params.add(new RoadParam(link.capacity_vphpl,link.ff_speed_kph,link.jam_density_vpkpl));

        // set ids
        long id = 0;
        for(RoadParam roadParam : road_params)
            roadParam.id = id++;
        return road_params;
    }

    /////////////////////////////////////
    // private statics
    /////////////////////////////////////

}
