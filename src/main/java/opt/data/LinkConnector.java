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

        Segment segment = create_isolated_segment(seg_name,fwy_name);
        LinkFreeway fwy = (LinkFreeway) segment.fwy;

        // create the offramp
        Node conn_start_node = mysegment.fwy_scenario.scenario.nodes.get(this.start_node_id);
        LinkOfframp fr = new LinkOfframp(mysegment.fwy_scenario.new_link_id(),
                "Unnamed offramp",
                fwy.end_node_id,      // start_node_id
                conn_start_node.id,   // end_node_id
                1,
                0,
                0,
                100f,
                param.capacity_vphpl,
                param.jam_density_vpkpl,
                param.ff_speed_kph,
                segment);

        mysegment.fwy_scenario.scenario.links.put(fr.id,fr);
        conn_start_node.in_links.add(fr.id);
        mysegment.fwy_scenario.scenario.nodes.get(fwy.end_node_id).out_links.add(fr.id);
        segment.out_frs.add(fr);

        fr.up_link = fwy;
        fr.dn_link = this;
        this.up_link = fr;

        return segment;
    }

    @Override
    public Segment insert_dn_segment(String seg_name,String fwy_name) {

        if(dn_link!=null)
            return null;

        Segment segment = create_isolated_segment(seg_name,fwy_name);
        LinkFreeway fwy = (LinkFreeway) segment.fwy;

        // create the onramp
        Node conn_end_node = mysegment.fwy_scenario.scenario.nodes.get(this.end_node_id);
        LinkOnramp or = new LinkOnramp(mysegment.fwy_scenario.new_link_id(),
                "Unnamed onramp",
                conn_end_node.id,      // start_node_id
                fwy.start_node_id,      // end_node_id
                1,
                0,
                0,
                100f,
                param.capacity_vphpl,
                param.jam_density_vpkpl,
                param.ff_speed_kph,
                segment);

        mysegment.fwy_scenario.scenario.links.put(or.id,or);
        conn_end_node.out_links.add(or.id);
        mysegment.fwy_scenario.scenario.nodes.get(fwy.start_node_id).in_links.add(or.id);
        segment.out_ors.add(or);

        or.dn_link = fwy;
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

    private Segment create_isolated_segment(String seg_name,String fwy_name) {

        FreewayScenario fwy_scenario = this.mysegment.fwy_scenario;

        // create a segment
        Long segment_id = fwy_scenario.new_seg_id();
        Segment segment = new Segment();
        segment.id = segment_id;
        segment.name = seg_name;
        segment.fwy_scenario = fwy_scenario;
        fwy_scenario.segments.put(segment_id,segment);

        // create nodes and freeway link
        Node fwy_start_node = new Node(fwy_scenario.new_node_id());
        fwy_scenario.scenario.nodes.put(fwy_start_node.id,fwy_start_node);
        Node fwy_end_node = new Node(fwy_scenario.new_node_id());
        fwy_scenario.scenario.nodes.put(fwy_end_node.id,fwy_end_node);

        LinkFreeway fwy = new LinkFreeway(
                fwy_scenario.new_link_id(),
                fwy_name,
                fwy_start_node.id,
                fwy_end_node.id,
                1,
                0,
                0,
                500f,
                param.capacity_vphpl,
                param.jam_density_vpkpl,
                param.ff_speed_kph,
                segment);

        fwy_scenario.scenario.links.put(fwy.id,fwy);
        fwy_start_node.out_links.add(fwy.id);
        fwy_end_node.in_links.add(fwy.id);
        segment.fwy = fwy;

        return segment;
    }
}
