package opt.data;

import jaxb.Link;
import jaxb.Roadparam;

public class LinkConnector extends LinkFreewayOrConnector {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkConnector(Link link, Roadparam rp) {
        super(link, rp);
    }

    public LinkConnector(Long id, String name, Long start_node_id, Long end_node_id, Integer full_lanes, Integer managed_lanes, Integer aux_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        super(id, name, start_node_id, end_node_id, full_lanes, managed_lanes, aux_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph, mysegment);
    }

    @Override
    public Type get_type() {
        return Type.connector;
    }

    @Override
    public boolean is_ramp() {
        return false;
    }

    /////////////////////////////////////
    // lanes
    /////////////////////////////////////

    @Override
    public void set_aux_lanes(int x) throws Exception {
        throw new Exception("Attempted to set aux lanes on an connector");
    }

    /////////////////////////////////////
    // insert
    /////////////////////////////////////

    @Override
    public Segment insert_up_segment(String seg_name,String fwy_name) {
        if(up_link!=null)
            return null;
        Segment segment = mysegment.fwy_scenario.create_isolated_segment(seg_name,fwy_name,this.param,Type.freeway);
        LinkOfframp fr = segment.add_out_fr("Unnamed offramp",param,full_lanes,managed_lanes,100f);
        fr.dn_link = this;
        this.up_link = fr;
        return segment;
    }

    @Override
    public Segment insert_dn_segment(String seg_name,String link_name) {
        if(dn_link!=null)
            return null;
        Segment segment = mysegment.fwy_scenario.create_isolated_segment(seg_name,link_name,this.param,Type.freeway);
        LinkOnramp or = segment.add_out_or("Unnamed onramp",param,full_lanes,managed_lanes,100f);
        or.up_link = this;
        this.dn_link = or;
        return segment;
    }

    @Override
    protected boolean is_permitted_uplink(AbstractLink link) {
        return link instanceof LinkOfframp;
    }

    @Override
    protected boolean is_permitted_dnlink(AbstractLink link) {
        return link instanceof LinkOnramp;
    }

}
