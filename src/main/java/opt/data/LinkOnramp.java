package opt.data;

import jaxb.Roadparam;
import profiles.Profile1D;

public class LinkOnramp extends AbstractLink {

    public LinkOnramp(jaxb.Link link, Roadparam rp) {
        super(link, AbstractLink.Type.onramp, rp);
    }

    public LinkOnramp(Long id, Long start_node_id, Long end_node_id, Integer full_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        super(id, AbstractLink.Type.onramp, start_node_id, end_node_id, full_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph, mysegment);
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

    @Override
    public void set_demand_vph(Long comm_id, Profile1D profile) throws Exception {
        this.demands.put(comm_id,profile);
    }

//    public Segment insert_upstrm_segment() {
//
//        if (!has_onramp())
//            return null;
//
//        AbstractLink or = or();
//
//        // existing node and new node
//        Node existing_node = fwy_scenario.scenario.nodes.get(or.end_node_id);
//        Node new_node = new Node(fwy_scenario.new_node_id());
//
//        // connect upstream links to new node
//        connect_segment_to_downstream_node(get_upstrm_or_segment(),new_node);
//
//        // create new freeway link
//        AbstractLink new_link = new AbstractLink(
//                fwy_scenario.new_link_id(),
//                AbstractLink.Type.connector,
//                new_node.id,
//                existing_node.id,
//                get_mixed_lanes(),
//                get_length_meters(),
//                get_capacity_vphpl(),
//                get_jam_density_vpkpl(),
//                get_freespeed_kph(),null);
//
//        // connect new link to start and end nodes
//        existing_node.in_links.add(new_link.id);
//        new_node.out_links.add(new_link.id);
//
//        // add link to scenario
//        fwy_scenario.scenario.links.put(new_link.id,new_link);
//
//        // create new segment
//        Segment newseg = create_new_segment(new_link);
//        newseg.segment_fwy_dn_id = this.id;
//        newseg.segment_fwy_up_id = this.segment_or_up_id;
//        this.segment_or_up_id = newseg.id;
//
//        // add to fwy scenario
//        fwy_scenario.segments.put(newseg.id,newseg);
//
//        return newseg;
//    }



//    public boolean has_onramp(){
//        return or_id!=null;
//    }
//
//    public float get_or_length_meters() {
//        return has_onramp() ? or().length_meters : Float.NaN;
//    }
//
//    public String get_or_name() {
//        return has_onramp() ? or().name : null;
//    }
//
//    public int get_or_lanes() {
//        return has_onramp() ? or().full_lanes : 0;
//    }
//
//    public float get_or_capacity_vphpl() {
//        return has_onramp() ? or().param.capacity_vphpl : Float.NaN;
//    }
//
//    public double get_or_max_vehicles() {
//        if(!has_onramp())
//            return Float.NaN;
//        AbstractLink or = or();
//        return or.param.jam_density_vpkpl * or.full_lanes * or.length_meters / 1000f;
//    }
//
//    public void set_or_length_meters(float x) throws Exception {
//        if(!has_onramp())
//            throw new Exception("No onramp");
//        or().length_meters = x;
//    }
//
//    public void set_or_name(String newname) throws Exception {
//        if(!has_onramp())
//            throw new Exception("No onramp");
//        or().name = newname;
//    }
//
//    public void set_or_lanes(int x) throws Exception {
//        if(!has_onramp())
//            throw new Exception("No onramp");
//        or().full_lanes = x;
//    }
//
//    public void set_or_capacity_vphpl(float x) throws Exception {
//        if(!has_onramp())
//            throw new Exception("No onramp");
//        or().param.capacity_vphpl = x;
//    }
//
//    public void set_or_max_vehicles(float x) throws Exception {
//        if(!has_onramp())
//            throw new Exception("No onramp");
//        AbstractLink or = or();
//        or.param.jam_density_vpkpl = x / (or.length_meters /1000f) / or.full_lanes;
//    }


//    /**
//     * Get the onramp demand for this segment, for a particular commodity.
//     * @param comm_id ID for the commodity
//     * @return Profile1D object if demand is defined for this commodity. null otherwise.
//     */
//    public Profile1D get_or_demand_vph(long comm_id){
//        return or_demands.containsKey(comm_id) ? or_demands.get(comm_id) : null;
//    }
//
//    /**
//     * Set the onramp demand in vehicles per hour.
//     * @param comm_id ID for the commodity
//     * @param demand_vph Demand in veh/hr as a Profile1D object
//     */
//    public void set_or_demand_vph(long comm_id,Profile1D demand_vph)throws Exception {
//        OTMErrorLog errorLog = new OTMErrorLog();
//        demand_vph.validate(errorLog);
//        if (errorLog.haserror())
//            throw new Exception(errorLog.format_errors());
//        this.or_demands.put(comm_id,demand_vph);
//    }

}
