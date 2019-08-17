package opt.data;

import jaxb.Link;
import jaxb.Roadparam;
import profiles.Profile1D;

public class LinkFreeway extends LinkFreewayOrConnector {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkFreeway(Link link, Roadparam rp) {
        super(link, rp);
    }

    public LinkFreeway(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, ParametersFreeway params) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);
    }

//    public LinkFreeway(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, String name, Boolean is_inner,Integer gp_lanes, Integer managed_lanes, Integer aux_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
//        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, name, is_inner, gp_lanes, managed_lanes, aux_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
//    }

    // used by clone
    public LinkFreeway(long id, Long start_node_id, Long end_node_id, AbstractParameters params){
        super(id,start_node_id,end_node_id,params);
    }

    @Override
    public Type get_type() {
        return Type.freeway;
    }

    @Override
    public boolean is_ramp() {
        return false;
    }

    /////////////////////////////////////
    // demands and splits
    /////////////////////////////////////

    @Override
    public void set_split(Long comm_id, Profile1D profile) throws Exception {
        this.splits.put(comm_id,profile);
    }

    /////////////////////////////////////
    // insert
    /////////////////////////////////////

    @Override
    public Segment insert_up_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {

        assert(ramp_params==null);

        Segment up_segment = get_up_segment();

        // create new upstream link
        LinkFreeway fwy_link = (LinkFreeway) create_up_FwyOrConnLink(Type.freeway,fwy_params);

        // wrap in a segment
        Segment new_segment = create_segment(fwy_link,seg_name);

        // connect upstream segment to new node
        if(up_segment!=null) {
            connect_segments_dwnstr_node_to(up_segment, fwy_link.start_node_id);
            fwy_link.up_link = up_segment.fwy;
            up_segment.fwy.dn_link = fwy_link;
        }

        return new_segment;
    }

    @Override
    public Segment insert_dn_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {

        assert(ramp_params==null);

        Segment dn_segment = get_dn_segment();

        // create new dnstream link
        LinkFreeway fwy_link = (LinkFreeway) create_dn_FwyOrConnLink(Type.freeway,fwy_params);

        // wrap in a segment
        Segment new_segment = create_segment(fwy_link,seg_name);

        // connect dnstream segment to new node
        if(dn_segment!=null) {
            connect_segments_upstr_node_to(dn_segment, fwy_link.end_node_id);
            fwy_link.dn_link = dn_segment.fwy;
            dn_segment.fwy.up_link = fwy_link;
        }

        return new_segment;
    }

    @Override
    protected boolean is_permitted_uplink(AbstractLink link) {
        return link instanceof LinkFreeway;
    }

    @Override
    protected boolean is_permitted_dnlink(AbstractLink link) {
        return link instanceof LinkFreeway;
    }

}
