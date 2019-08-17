package opt.data;

import jaxb.Link;
import jaxb.Roadparam;
import profiles.Profile1D;

public class LinkOnramp extends AbstractLink {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkOnramp(Link link, Roadparam rp) {
        super(link, rp);
        this.params = new ParametersRamp(
                "",
                false,
                link.getFullLanes(),
                0,
                link.getLength(),
                Float.NaN,
                Float.NaN,
                Float.NaN );
    }

    public LinkOnramp(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, AbstractParameters params) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);
    }

//    public LinkOnramp(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, String name, Boolean is_inner,Integer gp_lanes, Integer managed_lanes, Integer aux_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
//        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, name, is_inner, gp_lanes, managed_lanes, aux_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
//        this.params = new ParametersRamp(name, is_inner,gp_lanes, managed_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
//    }

    // used by clone
    public LinkOnramp(long id, Long start_node_id, Long end_node_id, AbstractParameters params){
        super(id,start_node_id,end_node_id,params);
    }

    @Override
    public Type get_type() {
        return Type.onramp;
    }

    @Override
    public boolean is_ramp() {
        return true;
    }

    /////////////////////////////////////
    // up and dn segment
    /////////////////////////////////////

    @Override
    public Segment get_dn_segment(){
        return dn_link.get_dn_segment();
    }


    @Override
    public void set_demand_vph(Long comm_id, Profile1D profile) throws Exception {
        this.demands.put(comm_id,profile);
    }

    /////////////////////////////////////
    // insert
    /////////////////////////////////////

    @Override
    public Segment insert_up_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {

        if(up_link!=null)
            return null;
        assert(ramp_params==null);

        Segment up_segment = get_up_segment();

        // create new upstream link
        LinkConnector new_link = (LinkConnector) create_up_FwyOrConnLink(Type.connector,fwy_params);

        // wrap in a segment
        Segment new_segment = create_segment(new_link,seg_name);

        // connect upstream segment to new node
        if(up_segment!=null) {
            connect_segments_dwnstr_node_to(up_segment, new_link.start_node_id);
            new_link.up_link = up_segment.fwy;
            up_segment.fwy.dn_link = new_link;
        }

        return new_segment;
    }

    @Override
    public Segment insert_dn_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {
        return null;
    }

    @Override
    protected boolean is_permitted_uplink(AbstractLink link) {
        return link instanceof LinkConnector;
    }

    @Override
    protected boolean is_permitted_dnlink(AbstractLink link) {
        return link instanceof LinkFreeway;
    }
}
