package opt.data;

import jaxb.Roadparam;
import profiles.Profile1D;

public class LinkOnramp extends AbstractLink {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkOnramp(jaxb.Link link, Roadparam rp) {
        super(link, AbstractLink.Type.onramp, rp);
    }

    public LinkOnramp(Long id, Long start_node_id, Long end_node_id, Integer full_lanes, Integer managed_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        super(id, AbstractLink.Type.onramp, start_node_id, end_node_id, full_lanes, managed_lanes, 0, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph, mysegment);
    }

    /////////////////////////////////////
    // up and dn segment
    /////////////////////////////////////

    @Override
    public Segment get_dn_segment(){
        return dn_link.get_dn_segment();
    }

    /////////////////////////////////////
    // lanes
    /////////////////////////////////////

    @Override
    public void set_aux_lanes(int x) throws Exception {
        throw new Exception("Attempted to set aux lanes on an onramp");
    }

    @Override
    public void set_demand_vph(Long comm_id, Profile1D profile) throws Exception {
        this.demands.put(comm_id,profile);
    }

    /////////////////////////////////////
    // insert
    /////////////////////////////////////

    @Override
    public Segment insert_up_segment() {

        // create new upstream link
        LinkConnector new_link = (LinkConnector) create_up_FwyOrConnLink(Type.connector);

        // wrap in a segment
        Segment new_segment = create_segment(new_link);

        // connect upstream segment to new node
        Segment up_segment = get_up_segment();
        if(up_segment!=null) {
            connect_segments_dwnstr_node_to(up_segment, new_link.start_node_id);
            new_link.up_link = up_segment.fwy;
            up_segment.fwy.dn_link = new_link;
        }

        return new_segment;
    }

    @Override
    public Segment insert_dn_segment() {
        return null;
    }

}
