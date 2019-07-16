package opt.data;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class jScenario {

    protected Map<Long,jNode> nodes = new HashMap<>();
    protected Map<Long,jLink> links = new HashMap<>();
    protected Map<Long,jCommodity> commodities = new HashMap<>();

    // commodity/link -> demand profile
    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    protected jScenario(){}

    protected jScenario(jaxb.Scenario scenario){

        // network
        Map<Long,jaxb.Roadparam> road_params = new HashMap<>();
        for(jaxb.Roadparam rp: scenario.getNetwork().getRoadparams().getRoadparam())
            road_params.put(rp.getId(),rp);

        for(jaxb.Node jnode : scenario.getNetwork().getNodes().getNode())
            nodes.put(jnode.getId(),new jNode(jnode));

        for(jaxb.Link jlink : scenario.getNetwork().getLinks().getLink()) {
            jLink link = new jLink(jlink,road_params.get(jlink.getRoadparam()));
            links.put(jlink.getId(),link);
            nodes.get(jlink.getEndNodeId()).in_links.add(link);
            nodes.get(jlink.getStartNodeId()).out_links.add(link);
        }

        for(jLink link : links.values())
            link.is_source = nodes.get(link.start_node_id).in_links.isEmpty();

        // commodities
        for(jaxb.Commodity comm : scenario.getCommodities().getCommodity())
            this.commodities.put(comm.getId(),new jCommodity(comm.getId(),comm.getName()));

    }

    /////////////////////////////////////
    // getters
    /////////////////////////////////////

    protected jLink get_ml_source() throws Exception {
        Set<jLink> source_links = links.values().stream()
                .filter(link -> link.is_mainline && link.is_source)
                .collect(toSet());
        if ( source_links.size()!=1 )
            throw new Exception("The network should have exactly one mainline source link.");
        return source_links.iterator().next();
    }

    protected Set<jRoadParam> get_road_params(){
        Set<jRoadParam> road_params = new HashSet<>();
        for(jLink link : links.values())
            road_params.add(new jRoadParam(link.capacity_vphpl,link.ff_speed_kph,link.jam_density_vpkpl));

        // set ids
        long id = 0;
        for(jRoadParam roadParam : road_params)
            roadParam.id = id++;
        return road_params;
    }

}
