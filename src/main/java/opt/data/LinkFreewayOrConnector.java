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
    public LinkFreewayOrConnector(long id, Long start_node_id, Long end_node_id, AbstractParameters params){
        super(id,start_node_id,end_node_id,params);
        this.params.set_aux_lanes(((ParametersFreeway)params).aux_lanes);
    }

    public float get_aux_capacity_vphpl(){
        return ((ParametersFreeway)params).aux_fd.capacity_vphpl;
    }

    public float get_aux_jam_density_vpkpl(){
        return ((ParametersFreeway)params).aux_fd.jam_density_vpkpl;
    }

    public float get_aux_freespeed_kph(){
        return ((ParametersFreeway)params).aux_fd.ff_speed_kph;
    }

    public void set_aux_capacity_vphpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive capacity");
        ((ParametersFreeway)params).aux_fd.capacity_vphpl = x;
    }

    public void set_aux_jam_density_vpkpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive jam density");
        ((ParametersFreeway)params).aux_fd.jam_density_vpkpl = x;
    }

    public void set_aux_freespeed_kph(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive free speed");
        ((ParametersFreeway)params).aux_fd.ff_speed_kph = x;
    }

}
