package opt.data;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class FreewayScenario {

    private Long max_link_id;
    private Long max_node_id;

    protected jScenario jscenario;
    protected List<Segment> segments = new ArrayList<>();

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
            segments.add( new Segment(this,onramp,ml_link,offramp) );

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

    public int get_num_segments(){
        return segments.size();
    }

    public Segment get_segment(int i){
        return i<0 || i>=get_num_segments() ? null : segments.get(i);
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
        jscenario.nodes.put(start_node.id,start_node);

        jNode end_node = jscenario.nodes.get(dn_ml.start_node_id);

        jLink ml = new jLink(new_link_id(),start_node.id,end_node.id,dn_ml.full_lanes,dn_ml.length,
                true, false, dn_index==0, dn_ml.capacity_vphpl, dn_ml.jam_density_vpkpl,
                dn_ml.ff_speed_kph);
        jscenario.links.put(ml.id,ml);

        Segment new_segment = new Segment(this,null,ml,null);
        this.segments.add(dn_index,new_segment);

        // fix adjacent segments ..............

        // dn_segment is no longer a source
        dn_segment.get_ml().is_source = false;

        // up_segment's end node = start_node
        if(up_segment!=null)
            up_segment.set_end_node(start_node.id);

        return new_segment;
    }

    /**
     * Create a new segment downstream of a given index.
     * Caution: This method alters the indices of all segments downstream of the new segment.
     * @param up_index integer in [0,num segments-1]
     */
    public Segment insert_segment_downstream_from_index(int up_index) throws Exception {

        assert(up_index>=0 && up_index<segments.size());

        Segment up_segment = segments.get(up_index);
        Segment dn_segment = up_index<segments.size()-1 ? segments.get(up_index+1) : null;

        jLink up_ml = up_segment.get_ml();

        jNode end_node = new jNode(new_node_id());
        jscenario.nodes.put(end_node.id,end_node);

        jNode start_node = jscenario.nodes.get(up_ml.end_node_id);

        jLink ml = new jLink(new_link_id(),start_node.id,end_node.id,up_ml.full_lanes,up_ml.length,
                true, false, false, up_ml.capacity_vphpl, up_ml.jam_density_vpkpl,
                up_ml.ff_speed_kph);
        jscenario.links.put(ml.id,ml);

        Segment new_segment = new Segment(this,null,ml,null);
        this.segments.add(up_index+1,new_segment);

        // fix adjacent segments ..............

        // dn_segment's start node = end_node
        if(dn_segment!=null)
            dn_segment.set_start_node(end_node.id);

        return new_segment;
    }

    /**
     * Delete the segment at the given index
     * @param index
     */
    public void delete_segment(int index) throws Exception {

        if(index<0 || index>=segments.size())
            throw new Exception("Out of bounds index.");

        if(get_num_segments()==1)
            throw new Exception("Removing the sole segment is not allowed.");

        Segment segment = segments.get(index);
        segment.delete_offramp();
        segment.delete_onramp();
        jscenario.links.remove(segment.get_ml().id);

        // fix adjacent segments
        if(index==0)
            // make the 1st segment a source
            segments.get(1).get_ml().is_source = true;
        else
            // attach upstream segment to the end node
            segments.get(index-1).set_end_node(segment.get_ml().end_node_id);


        // remove the start node
        jscenario.nodes.remove(segment.get_ml().start_node_id);

        // remove the segment
        segments.remove(index);
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

    protected long new_link_id(){
        return ++max_link_id;
    }

    protected long new_node_id(){
        return ++max_node_id;
    }

}
