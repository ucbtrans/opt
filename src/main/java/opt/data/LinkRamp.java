package opt.data;

import jaxb.Link;
import jaxb.Roadparam;

public abstract class LinkRamp extends AbstractLink {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkRamp(Link link, Roadparam rp, int mng_lanes, FDparams mng_fd, boolean mng_barrier, boolean mng_separated) {
        super(link);
        this.params = new ParametersRamp(
                "",
                false,
                link.getFullLanes(),
                mng_lanes,
                mng_barrier,
                mng_separated,
                link.getLength(),
                rp.getCapacity(),
                rp.getJamDensity(),
                rp.getSpeed() ,
                mng_fd==null ? Float.NaN : mng_fd.capacity_vphpl,
                mng_fd==null ? Float.NaN : mng_fd.jam_density_vpkpl,
                mng_fd==null ? Float.NaN : mng_fd.ff_speed_kph);
    }

    public LinkRamp(Link link) {
        super(link);
    }

    public LinkRamp(long id, Long start_node_id, Long end_node_id, AbstractParameters params) {
        super(id, start_node_id, end_node_id, params);
    }

    public LinkRamp(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, AbstractParameters params) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);
    }

    /////////////////////////////////////
    // get set
    /////////////////////////////////////

    @Override
    public boolean is_ramp() {
        return true;
    }

    @Override
    public boolean get_is_inner() {
        return ((ParametersRamp)params).is_inner;
    }

    @Override
    public boolean has_aux() {
        return false;
    }

    @Override
    public int get_aux_lanes() {
        return 0;
    }

    @Override
    public float get_aux_capacity_vphpl() {
        return Float.NaN;
    }

    @Override
    public float get_aux_jam_density_vpkpl() {
        return Float.NaN;
    }

    @Override
    public float get_aux_ff_speed_kph() {
        return Float.NaN;
    }

    @Override
    public void set_is_inner(boolean value) throws Exception {
        ((ParametersRamp)params).is_inner = value;
    }

    @Override
    public void set_aux_lanes(int value) throws Exception {
//        throw new Exception("set_aux_lanes should not be called on ramps.");
    }

    @Override
    public void set_aux_capacity_vphpl(float value) throws Exception {
//        throw new Exception("set_aux_capacity_vphpl should not be called on ramps.");
    }

    @Override
    public void set_aux_jam_density_vpkpl(float value) throws Exception {
//        throw new Exception("set_aux_jam_density_vpkpl should not be called on ramps.");
    }

    @Override
    public void set_aux_ff_speed_kph(float value) throws Exception {
//        throw new Exception("set_aux_ff_speed_kph should not be called on ramps.");
    }


}
