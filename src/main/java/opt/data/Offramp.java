package opt.data;

import error.OTMErrorLog;
import jaxb.Roadparam;
import profiles.Profile1D;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Offramp extends AbstractLink {
    protected Long id;
    protected Map<Long, Profile1D> fr_splits = new HashMap<>();      // commodity -> Profile1D
    protected Long seg_dn_id = null;

//    public Offramp(Long id) {
//        this.id = id;
//    }


    public Offramp(jaxb.Link link, Roadparam rp) {
        super(link, AbstractLink.Type.offramp, rp);
    }

    public Offramp(Long id, Long start_node_id, Long end_node_id, Integer full_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        super(id, AbstractLink.Type.offramp, start_node_id, end_node_id, full_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph, mysegment);
    }

    @Override
    public int get_managed_lanes() {
        System.out.println("NOT IMPLEMENTED!");
        return 0;
    }

    @Override
    public int get_aux_lanes() {
        return 0;
    }

    /////////////////////////////////////
    // offramp
    /////////////////////////////////////

//    public boolean has_offramp(){
//        return !frs.isEmpty();
//    }

//    public float get_fr_length_meters(){
//        return has_offramp() ? fr().length_meters : Float.NaN;
//    }
//
//    public String get_fr_name(){
//        return has_offramp() ? fr().name : null;
//    }
//
//    public int get_fr_lanes(){
//        return has_offramp() ? fr().full_lanes : 0;
//    }
//
//    public float get_fr_capacity_vphpl(){
//        return has_offramp() ? fr().param.capacity_vphpl : Float.NaN;
//    }
//
//    public double get_fr_max_vehicles(){
//        if(has_offramp()){
//            AbstractLink fr = fr();
//            return fr.param.jam_density_vpkpl * fr.full_lanes * fr.length_meters / 1000f;
//        }
//        else return 0d;
//    }
//
//    public void set_fr_length_meters(float x){
//        if( has_offramp() )
//            fr().length_meters = x;
//    }
//
//    public void set_fr_name(String newname) {
//        if(has_offramp())
//            fr().name = newname;
//    }
//
//    public void set_fr_lanes(int x) throws Exception {
//        if (x<=0)
//            throw new Exception("Invalid number of lanes");
//        if(!has_offramp())
//            throw new Exception("No offramp");
//        fr().full_lanes = x;
//    }
//
//    public void set_fr_capacity_vphpl(float x) throws Exception {
//        if (x<=0)
//            throw new Exception("Invalid capacity");
//        if(!has_offramp())
//            throw new Exception("No offramp");
//        fr().param.capacity_vphpl = x;
//    }
//
//    public void set_fr_max_vehicles(float x) throws Exception {
//        if (x<=0)
//            throw new Exception("Invalid max vehicles");
//        if(!has_offramp())
//            throw new Exception("No offramp");
//        AbstractLink fr = fr();
//        fr.param.jam_density_vpkpl = x / (fr.length_meters /1000f) / fr.full_lanes;
//    }

//    public Segment insert_dnstrm_offramp_segment(){
//
//        if (!has_offramp())
//            return null;
//
//        AbstractLink fr = fr();
//
//        // existing node and new node
//        Node existing_node = fwy_scenario.scenario.nodes.get(fr.start_node_id);
//        Node new_node = new Node(fwy_scenario.new_node_id());
//
//        // connect upstream links to new node
//        connect_segment_to_upstream_node(get_dnstrm_fr_segment(),new_node);
//
//        // create new freeway link
//        AbstractLink new_link = new AbstractLink(
//                fwy_scenario.new_link_id(),
//                AbstractLink.Type.connector,
//                existing_node.id,
//                new_node.id,
//                get_mixed_lanes(),
//                get_length_meters(),
//                get_capacity_vphpl(),
//                get_jam_density_vpkpl(),
//                get_freespeed_kph(),null);
//
//        // connect new link to start and end nodes
//        existing_node.out_links.add(new_link.id);
//        new_node.in_links.add(new_link.id);
//
//        // add link to scenario
//        fwy_scenario.scenario.links.put(new_link.id,new_link);
//
//        // create new segment
//        Segment newseg = create_new_segment(new_link);
//        newseg.segment_fwy_up_id = this.id;
//        newseg.segment_fwy_dn_id = this.segment_fr_dn_id;
//        this.segment_fr_dn_id = newseg.id;
//
//        // add to fwy scenario
//        fwy_scenario.segments.put(newseg.id,newseg);
//
//        return newseg;
//    }

//    /**
//     * Get the offramp split ratio for this segment, for a particular commodity.
//     * @param comm_id ID for the commodity
//     * @return Profile1D object if split is defined for this commodity. null otherwise.
//     */
//    public Profile1D get_fr_split(long comm_id){
//        return fr_splits.containsKey(comm_id) ? fr_splits.get(comm_id) : null;
//    }
//
//    /**
//     * Set the offramp split ratio for this segment, for a particular commodity.
//     * @param comm_id ID for the commodity
//     * @param splits DSplit ratio as a Profile1D object
//     */
//    public void set_fr_split(long comm_id,Profile1D splits)throws Exception {
//        OTMErrorLog errorLog = new OTMErrorLog();
//        splits.validate(errorLog);
//        if (Collections.max(splits.values) > 1.0D)
//            errorLog.addError("Collections.max(values)>1");
//        if (errorLog.haserror())
//            throw new Exception(errorLog.format_errors());
//        this.fr_splits.put(comm_id,splits);
//    }

}
