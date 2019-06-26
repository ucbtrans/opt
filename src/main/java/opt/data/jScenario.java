package opt.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class jScenario {

    public jaxb.Scenario scenario;
    public Map<Long,jNode> nodes = new HashMap<>();
    public Map<Long,jLink> links = new HashMap<>();
    public Map<Long,jaxb.Roadparam> road_params = new HashMap<>();

    public jScenario(jaxb.Scenario scenario){
        this.scenario = scenario;

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

    }

    public jLink get_ml_source() throws Exception {
        Set<jLink> source_links = links.values().stream()
                .filter(link -> link.is_mainline && link.is_source)
                .collect(toSet());
        if ( source_links.size()!=1 )
            throw new Exception("The network should have exactly one mainline source link.");
        return source_links.iterator().next();
    }

}
