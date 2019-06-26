package opt.data;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class FreewayScenario {

    private Long max_link_id;
    private Long max_node_id;

    private jScenario jscenario;

    private List<Segment> segments = new ArrayList<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public FreewayScenario(jaxb.Scenario jaxb_scenario) throws Exception {
        this.jscenario = new jScenario(jaxb_scenario);

        // get upstream mainline source
        jLink ml_link = jscenario.get_ml_source();

        Set<jLink> all_links = new HashSet<>(jscenario.links.values());

        while (true){

            jNode start_node = jscenario.nodes.get(ml_link.start_node_id);
            jNode end_node = jscenario.nodes.get(ml_link.end_node_id);

            // get offramp ........................
            Set<jLink> offramps = start_node.out_links.stream()
                    .filter(link->link.is_ramp)
                    .collect(toSet());

            if(offramps.size()>1)
                throw new Exception("Multiple offramps on a single node.");

            jLink offramp = offramps.isEmpty() ? null : offramps.iterator().next() ;

            // get onramp ........................
            Set<jLink> onramps = end_node.in_links.stream()
                    .filter(link->link.is_source && link.is_ramp)
                    .collect(toSet());

            if(onramps.size()>1)
                throw new Exception("Multiple onramps on a single node.");

            jLink onramp = onramps.isEmpty() ? null : onramps.iterator().next();


            // create the segment ................
            segments.add( new Segment(onramp,ml_link,offramp) );

            // remove from list ...................
            all_links.remove(ml_link);
            if(onramp!=null)
                all_links.remove(onramp);
            if(offramp!=null)
                all_links.remove(offramp);

            // next link .........................
            Set<jLink> dn_links = end_node.out_links.stream()
                    .filter(link->link.is_mainline)
                    .collect(toSet());

            if(dn_links.size()>1)
                throw new Exception("No mainline bifurcations allowed.");

            ml_link = dn_links.isEmpty() ? null : dn_links.iterator().next() ;

            // break conditions................
            if(all_links.isEmpty() || ml_link==null)
                break;
        }

        // max id
        max_link_id = jscenario.links.keySet().stream()
                .max(Comparator.comparing(Long::valueOf))
                .get();

        max_node_id = jscenario.nodes.keySet().stream()
                .max(Comparator.comparing(Long::valueOf))
                .get();

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

    /**
     * Create a new segment upstream of a given index
     * Caution: This method alters the indices of all segments downstream of the new segment.
     * @param dn_index integer in [0,num segments-1]
     */
    public Segment insert_segment_upstream_from_index(int dn_index) throws Exception {

        assert(dn_index>=0 && dn_index<segments.size());

        Segment dn_segment = segments.get(dn_index);
        Segment up_segment = dn_index>0 ? segments.get(dn_index-1) : null;

        jLink dn_ml = dn_segment.get_ml();

        jNode start_node = new jNode(new_node_id());
        jNode end_node = jscenario.nodes.get(dn_ml.start_node_id);

        jLink ml = new jLink(new_link_id(),start_node.id,end_node.id,dn_ml.full_lanes,dn_ml.length,
                true, false, dn_index==0, dn_ml.capacity_vphpl, dn_ml.jam_density_vpkpl,
                dn_ml.ff_speed_kph);

        Segment new_segment = new Segment(null,ml,null);
        this.segments.add(dn_index,new_segment);

        // fix network
        if(up_segment!=null){
            // TODO GG THIS FIX HAS TO BE ON THE PURE GRAPH, PRIOR TO CREATING LANE GROUPS.
            // upsegment's end node = start_node

            // fix road connections
        }

        return new_segment;
    }

    /**
     * Create a new segment downstream of a given index.
     * Caution: This method alters the indices of all segments downstream of the new segment.
     * @param up_index integer in [0,num segments-1]
     */
    public Segment insert_segment_downstream_from_index(int up_index) throws Exception {

        assert(up_index>=0 && up_index<segments.size());

        // TODO COMPLETE THIS

        return null;
    }

    /////////////////////////////////////
    // run
    /////////////////////////////////////

    public void run(){

//        if(!ready_to_run)
//            make_ready_to_run();
    }

    /////////////////////////////////////
    // private
    /////////////////////////////////////

    private long new_link_id(){
        return ++max_link_id;
    }

    private long new_node_id(){
        return ++max_node_id;
    }

}
