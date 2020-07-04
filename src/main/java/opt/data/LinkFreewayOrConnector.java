package opt.data;

import jaxb.Link;
import jaxb.Roadparam;

public abstract class LinkFreewayOrConnector extends AbstractLink {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkFreewayOrConnector(Link link, Roadparam rp,int mng_lanes,FDparams mng_fd,boolean mng_barrier,boolean mng_separated,int aux_lanes,FDparams aux_fd) {
        super(link);

        this.params = new ParametersFreeway(
                "",
                link.getFullLanes(),
                mng_lanes,
                mng_barrier,
                mng_separated,
                aux_lanes,
                link.getLength(),
                rp.getCapacity(),
                rp.getJamDensity(),
                rp.getSpeed() ,
                mng_fd==null ? Float.NaN : mng_fd.capacity_vphpl,
                mng_fd==null ? Float.NaN : mng_fd.jam_density_vpkpl,
                mng_fd==null ? Float.NaN : mng_fd.ff_speed_kph,
                aux_fd==null ? Float.NaN : aux_fd.capacity_vphpl,
                aux_fd==null ? Float.NaN : aux_fd.jam_density_vpkpl,
                aux_fd==null ? Float.NaN : aux_fd.ff_speed_kph);
    }

    public LinkFreewayOrConnector(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, ParametersFreeway params) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);
    }

    // used by clone
    public LinkFreewayOrConnector(long id, Long start_node_id, Long end_node_id, AbstractParameters params) throws Exception {
        super(id,start_node_id,end_node_id,params);
        this.set_aux_lanes(((ParametersFreeway)params).aux_lanes);
    }

    /////////////////////////////////////
    // get set
    /////////////////////////////////////

    @Override
    public boolean is_ramp() {
        return false;
    }

    @Override
    public boolean has_aux() {
        ParametersFreeway p = (ParametersFreeway) params;
        return p.aux_lanes > 0 && p.aux_fd != null;
    }

    @Override
    public boolean get_is_inner() {
        return false;
    }

    @Override
    public int get_aux_lanes() {
        return ((ParametersFreeway) params).aux_lanes;
    }

    @Override
    public float get_aux_capacity_vphpl() {
        return ((ParametersFreeway) params).aux_fd.capacity_vphpl;
    }

    @Override
    public float get_aux_jam_density_vpkpl() {
        return ((ParametersFreeway) params).aux_fd.jam_density_vpkpl;
    }

    @Override
    public float get_aux_ff_speed_kph() {
        return ((ParametersFreeway) params).aux_fd.ff_speed_kph;
    }

    @Override
    public void set_is_inner(boolean value) throws Exception {
//        throw new Exception("Cant call set_is_inner on Freeway or Connector");
    }

    @Override
    public void set_aux_lanes(int value) throws Exception {
        if(value<0)
            throw new Exception("Negative lanes");
        ((ParametersFreeway) params).aux_lanes = value;
    }

    @Override
    public void set_aux_capacity_vphpl(float value) throws Exception {
        if(value<=0)
            throw new Exception("Non-positive value");
        ((ParametersFreeway) params).aux_fd.capacity_vphpl = value;
    }

    @Override
    public void set_aux_jam_density_vpkpl(float value) throws Exception {
        if(value<=0)
            throw new Exception("Non-positive value");
        ((ParametersFreeway) params).aux_fd.jam_density_vpkpl = value;
    }

    @Override
    public void set_aux_ff_speed_kph(float value) throws Exception {
        if(value<=0)
            throw new Exception("Non-positive value");
        ((ParametersFreeway) params).aux_fd.ff_speed_kph = value;
    }

}
