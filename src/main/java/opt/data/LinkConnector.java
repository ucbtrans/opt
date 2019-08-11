package opt.data;

import jaxb.Roadparam;

public class LinkConnector extends LinkFreewayOrConnector {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkConnector(jaxb.Link link, Roadparam rp) {
        super(link, Type.connector, rp);
    }

    public LinkConnector(Long id, Long start_node_id, Long end_node_id, Integer full_lanes, Integer managed_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        super(id, Type.connector, start_node_id, end_node_id, full_lanes, managed_lanes, 0, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph, mysegment);
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
    public Segment insert_up_segment() {
        Segment segment = mysegment.fwy_scenario.create_isolated_segment("New segment",this.param,Type.freeway);
        LinkOfframp fr = segment.add_out_fr(param,full_lanes,managed_lanes,100f);
        fr.dn_link = this;
        this.up_link = fr;
        return segment;
    }

    @Override
    public Segment insert_dn_segment() {
        Segment segment = mysegment.fwy_scenario.create_isolated_segment("New segment",this.param,Type.freeway);
        LinkOnramp or = segment.add_out_or(param,full_lanes,managed_lanes,100f);
        or.up_link = this;
        this.dn_link = or;
        return segment;
    }

}
