package opt.data;

import jaxb.Link;
import jaxb.Roadparam;

public class LinkOnramp extends LinkRamp {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkOnramp(Link link, Roadparam rp,int mng_lanes,FDparams mng_fd,boolean mng_barrier,boolean mng_separated) {
        super(link,rp,mng_lanes,mng_fd,mng_barrier,mng_separated);
    }

    public LinkOnramp(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, ParametersRamp params) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);
    }

    // used by clone
    public LinkOnramp(long id, Long start_node_id, Long end_node_id, AbstractParameters params) throws Exception {
        super(id,start_node_id,end_node_id,params);
        this.set_is_inner(((ParametersRamp)params).is_inner);
    }

    /////////////////////////////////////
    // up and dn segment
    /////////////////////////////////////

    @Override
    public Segment get_dn_segment(){
        return dn_link.get_dn_segment();
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

    /////////////////////////////////////
    // get set
    /////////////////////////////////////

    @Override
    public Type get_type() {
        return Type.onramp;
    }

}
