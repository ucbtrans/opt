package opt.data;

import jaxb.Link;
import jaxb.Roadparam;

public class LinkConnector extends LinkFreewayOrConnector {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkConnector(Link link, Roadparam rp,int mng_lanes,FDparams mng_fd,boolean mng_barrier,boolean mng_separated,int aux_lanes,FDparams aux_fd) {
        super(link, rp,mng_lanes, mng_fd, mng_barrier, mng_separated, aux_lanes, aux_fd);
    }

    public LinkConnector(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, ParametersFreeway params) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);
    }

    /////////////////////////////////////
    // insert
    /////////////////////////////////////

    @Override
    public Segment insert_up_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {
        if(up_link!=null)
            return null;
        assert(fwy_params!=null && ramp_params!=null);

        Segment segment = create_isolated_segment(seg_name,fwy_params);
        LinkFreeway fwy = (LinkFreeway) segment.fwy;

        // create the offramp
        Node conn_start_node = mysegment.my_fwy_scenario.scenario.nodes.get(this.start_node_id);

        LinkOfframp fr = new LinkOfframp(
                mysegment.my_fwy_scenario.new_link_id(), // id,
                segment,// mysegment,
                null,       // up_link
                null,       // dn_link
                fwy.end_node_id,   // start_node_id
                conn_start_node.id,   // end_node_id
                ramp_params );

        mysegment.my_fwy_scenario.scenario.links.put(fr.id,fr);
        conn_start_node.in_links.add(fr.id);
        mysegment.my_fwy_scenario.scenario.nodes.get(fwy.end_node_id).out_links.add(fr.id);

        if(ramp_params.is_inner)
            segment.in_frs.add(fr);
        else
            segment.out_frs.add(fr);


        fr.up_link = fwy;
        fr.dn_link = this;
        this.up_link = fr;

        return segment;
    }

    @Override
    public Segment insert_dn_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {

        if(dn_link!=null)
            return null;
        assert(fwy_params!=null && ramp_params!=null);

        Segment segment = create_isolated_segment(seg_name,fwy_params);
        LinkFreeway fwy = (LinkFreeway) segment.fwy;

        // create the onramp
        Node conn_end_node = mysegment.my_fwy_scenario.scenario.nodes.get(this.end_node_id);

        LinkOnramp or = new LinkOnramp(
                mysegment.my_fwy_scenario.new_link_id(), // id,
                segment, // mysegment,
                null, // up_link,
                null, // dn_link,
                conn_end_node.id,      // start_node_id
                fwy.start_node_id,      // end_node_id
                ramp_params);

        mysegment.my_fwy_scenario.scenario.links.put(or.id,or);
        conn_end_node.out_links.add(or.id);
        mysegment.my_fwy_scenario.scenario.nodes.get(fwy.start_node_id).in_links.add(or.id);

        if(ramp_params.is_inner)
            segment.in_ors.add(or);
        else
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

    /////////////////////////////////////
    // get set
    /////////////////////////////////////

    @Override
    public Type get_type() {
        return Type.connector;
    }

    /////////////////////////////////////
    // private
    /////////////////////////////////////

    private Segment create_isolated_segment(String seg_name, ParametersFreeway fwy_params) {

        FreewayScenario fwy_scenario = this.mysegment.my_fwy_scenario;

        // create a segment
        Long segment_id = fwy_scenario.new_seg_id();
        Segment segment = new Segment(segment_id);
        segment.name = seg_name;
        segment.my_fwy_scenario = fwy_scenario;
        fwy_scenario.segments.put(segment_id,segment);

        // create nodes and freeway link
        Node fwy_start_node = new Node(fwy_scenario.new_node_id());
        fwy_scenario.scenario.nodes.put(fwy_start_node.id,fwy_start_node);
        Node fwy_end_node = new Node(fwy_scenario.new_node_id());
        fwy_scenario.scenario.nodes.put(fwy_end_node.id,fwy_end_node);

        LinkFreeway fwy = new LinkFreeway(
                fwy_scenario.new_link_id(), // id,
                segment, // mysegment,
                null, // up_link,
                null, // dn_link,
                fwy_start_node.id,
                fwy_end_node.id,
                fwy_params);

        fwy_scenario.scenario.links.put(fwy.id,fwy);
        fwy_start_node.out_links.add(fwy.id);
        fwy_end_node.in_links.add(fwy.id);
        segment.fwy = fwy;

        return segment;
    }
}
