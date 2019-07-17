package opt.data;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class Scenario {

    protected Map<Long, Node> nodes = new HashMap<>();
    protected Map<Long, Link> links = new HashMap<>();
    protected Map<Long, Commodity> commodities = new HashMap<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    protected Scenario(){}

    protected Scenario(jaxb.Scenario scenario){

        // network
        Map<Long,jaxb.Roadparam> road_params = new HashMap<>();
        for(jaxb.Roadparam rp: scenario.getNetwork().getRoadparams().getRoadparam())
            road_params.put(rp.getId(),rp);

        for(jaxb.Node jnode : scenario.getNetwork().getNodes().getNode())
            nodes.put(jnode.getId(),new Node(jnode));

        for(jaxb.Link jlink : scenario.getNetwork().getLinks().getLink()) {
            Link link = new Link(jlink,road_params.get(jlink.getRoadparam()));
            links.put(jlink.getId(),link);
            nodes.get(jlink.getEndNodeId()).in_links.add(link);
            nodes.get(jlink.getStartNodeId()).out_links.add(link);
        }

        for(Link link : links.values())
            link.is_source = nodes.get(link.start_node_id).in_links.isEmpty();

        // commodities
        for(jaxb.Commodity comm : scenario.getCommodities().getCommodity())
            this.commodities.put(comm.getId(),new Commodity(comm.getId(),comm.getName()));

    }

    /////////////////////////////////////
    // getters
    /////////////////////////////////////

    protected Link get_ml_source() throws Exception {
        Set<Link> source_links = links.values().stream()
                .filter(link -> link.is_mainline && link.is_source)
                .collect(toSet());
        if ( source_links.size()!=1 )
            throw new Exception("The network should have exactly one mainline source link.");
        return source_links.iterator().next();
    }

    protected Set<RoadParam> get_road_params(){
        Set<RoadParam> road_params = new HashSet<>();
        for(Link link : links.values())
            road_params.add(new RoadParam(link.capacity_vphpl,link.ff_speed_kph,link.jam_density_vpkpl));

        // set ids
        long id = 0;
        for(RoadParam roadParam : road_params)
            roadParam.id = id++;
        return road_params;
    }

}
