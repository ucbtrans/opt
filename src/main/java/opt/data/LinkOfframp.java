package opt.data;

import jaxb.Link;
import jaxb.Roadparam;
import profiles.Profile1D;

public class LinkOfframp extends AbstractLink {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkOfframp(Link link, Roadparam rp) {
        super(link, rp);
    }

    public LinkOfframp(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, AbstractParameters params) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);
    }

    public LinkOfframp(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, String name, Boolean is_inner,Integer gp_lanes, Integer managed_lanes, Integer aux_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, name, is_inner, gp_lanes, managed_lanes, aux_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
    }

    // used by clone
    public LinkOfframp(long id, Long start_node_id, Long end_node_id, AbstractParameters params){
        super(id,start_node_id,end_node_id,params);
    }

    @Override
    public Type get_type() {
        return Type.offramp;
    }

    @Override
    public boolean is_ramp() {
        return true;
    }

    /////////////////////////////////////
    // up and dn segment
    /////////////////////////////////////

    @Override
    public Segment get_up_segment(){
        return up_link.get_up_segment();
    }

    /////////////////////////////////////
    // lanes
    /////////////////////////////////////

    @Override
    public void set_aux_lanes(int x) throws Exception {
        throw new Exception("Attempted to set aux lanes on an offramp");
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
    public Segment insert_dn_segment(String seg_name, AbstractParameters fwy_params, AbstractParameters ramp_params) {

        if(dn_link!=null)
            return null;
        assert(ramp_params==null);

        Segment dn_segment = get_dn_segment();

        // create new dnstream link
        LinkConnector new_link = (LinkConnector) create_dn_FwyOrConnLink(Type.connector,fwy_params);

        // wrap in a segment
        Segment new_segment = create_segment(new_link,seg_name);

        // connect dnstream segment to new node
        if(dn_segment!=null) {
            connect_segments_upstr_node_to(dn_segment, new_link.end_node_id);
            new_link.dn_link = dn_segment.fwy;
            dn_segment.fwy.up_link = new_link;
        }

        return new_segment;
    }

    @Override
    public Segment insert_up_segment(String seg_name, AbstractParameters fwy_params, AbstractParameters ramp_params) {
        return null;
    }

    @Override
    protected boolean is_permitted_uplink(AbstractLink link) {
        return link instanceof LinkFreeway;
    }

    @Override
    protected boolean is_permitted_dnlink(AbstractLink link) {
        return link instanceof LinkConnector;
    }
}
