package opt.data;

import common.Link;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class FreewayScenario {

    private runner.Scenario otm_scenario;
    private List<Segment> segments = new ArrayList<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public FreewayScenario(runner.Scenario otm_scenario) throws Exception {
        this.otm_scenario = otm_scenario;

        // get upstream mainline source
        Set<Link> ml_sources = otm_scenario.network.links.values().stream()
                .filter(link->link.is_source && link.road_type.equals(Link.RoadType.mainline))
                .collect(toSet());

        if(ml_sources.size()!=1)
            throw new Exception("A freeway network must have exactly one mainline source");

        // start at the mainline source
        Link ml_link = ml_sources.iterator().next();

        Set<Link> all_links = new HashSet<>(otm_scenario.network.links.values());

        while (true){

            // get onramp ........................
            Set<Link> onramps = ml_link.start_node.in_links.values().stream()
                    .filter(link->link.is_source && link.road_type.equals(Link.RoadType.ramp))
                    .collect(toSet());

            if(onramps.size()>1)
                throw new Exception("Multiple onramps on a single node.");

            Link onramp = onramps.isEmpty() ? null : onramps.iterator().next();

            // get offramp ........................
            Set<Link> offramps = ml_link.end_node.out_links.values().stream()
                    .filter(link->link.road_type.equals("offramp"))
                    .collect(toSet());

            if(offramps.size()>1)
                throw new Exception("Multiple offramps on a single node.");

            Link offramp = offramps.isEmpty() ? null : offramps.iterator().next() ;

            // create the segment ................
            segments.add( new Segment(onramp,ml_link,offramp) );

            // remove from list ...................
            all_links.remove(ml_link);
            if(onramp!=null)
                all_links.remove(onramp);
            if(offramp!=null)
                all_links.remove(offramp);

            // next link .........................
            Set<Link> dn_links = ml_link.end_node.out_links.values().stream()
                    .filter(link->link.road_type.equals("freeway"))
                    .collect(toSet());

            if(dn_links.size()>1)
                throw new Exception("No mainline bifurcations allowed.");

            ml_link = dn_links.isEmpty() ? null : dn_links.iterator().next() ;

            // break conditions................
            if(all_links.isEmpty() || ml_link==null)
                break;
        }

    }

    /////////////////////////////////////
    // getters
    /////////////////////////////////////

    public List<Segment> get_segments(){
        return segments;
    }

    /////////////////////////////////////
    // modify
    /////////////////////////////////////

    public void add_segment(){
        System.err.println("NOT IMPLEMENTED!!");
    }

}
