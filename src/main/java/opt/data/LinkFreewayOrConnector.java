package opt.data;

import jaxb.Link;
import jaxb.Roadparam;
import profiles.Profile1D;

public abstract class LinkFreewayOrConnector extends AbstractLink {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkFreewayOrConnector(Link link, Roadparam rp) {
        super(link);
        this.params = new ParametersFreeway(
                "",
                link.getFullLanes(),
                0,
                false,
                false,
                0,
                link.getLength(),
                rp.getCapacity(),
                rp.getJamDensity(),
                rp.getSpeed() ,
                null,null,null,
                null,null,null);
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

    /////////////////////////////////////
    // demands and splits
    /////////////////////////////////////

    @Override
    public void set_demand_vph(Long comm_id, Profile1D profile) throws Exception {
        this.demands.put(comm_id,profile);
    }

}
