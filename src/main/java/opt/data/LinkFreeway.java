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

    public LinkFreeway(Long id, String name, Long start_node_id, Long end_node_id, Integer full_lanes, Integer managed_lanes, Integer aux_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        super(id, name, start_node_id, end_node_id, full_lanes, managed_lanes, aux_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph, mysegment);
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
    public Segment insert_up_segment(String seg_name,String link_name) {

        Segment up_segment = get_up_segment();

        // create new upstream link
        LinkFreeway new_link = (LinkFreeway) create_up_FwyOrConnLink(Type.freeway,link_name);

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
    public Segment insert_dn_segment(String seg_name,String link_name) {

        Segment dn_segment = get_dn_segment();

        // create new dnstream link
        LinkFreeway new_link = (LinkFreeway) create_dn_FwyOrConnLink(Type.freeway,link_name);

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

}
