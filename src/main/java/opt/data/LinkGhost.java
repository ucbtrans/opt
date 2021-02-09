package opt.data;

import profiles.Profile1D;

import java.util.Map;

public class LinkGhost extends LinkFreewayOrConnector {

    public LinkGhost(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, ParametersFreeway params, Map<Long, Profile1D> demands) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);

        if(demands!=null)
            this.demands = demands;
    }

    ///////////////////////////////
    // unused implementations
    ///////////////////////////////

    @Override
    public Segment insert_up_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {
        return null;
    }

    @Override
    public Segment insert_dn_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {
        return null;
    }

    @Override
    protected boolean is_permitted_uplink(AbstractLink link) {
        return false;
    }

    @Override
    protected boolean is_permitted_dnlink(AbstractLink link) {
        return false;
    }

    @Override
    public Type get_type() {
        return Type.ghost;
    }

}
