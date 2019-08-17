package opt.data;

import jaxb.Roadparam;

public class ParametersFreeway extends AbstractParameters  {

    public Integer aux_lanes;

    public ParametersFreeway(String name, Integer gp_lanes, Integer managed_lanes, Integer aux_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        super(name, gp_lanes, managed_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
        this.aux_lanes = aux_lanes;
    }

    public ParametersFreeway(Roadparam rp) {
        super(rp);
    }

    public ParametersFreeway(Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        super(capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
    }

    @Override
    public boolean get_is_inner() {
        return false;
    }

    @Override
    public void set_is_inner(boolean x) {
        //
    }

    @Override
    public int get_aux_lanes(){
        return aux_lanes;
    }

    @Override
    public void set_aux_lanes(int x) throws Exception{
        if(x<0)
            throw new Exception("Attempted to set negative number of lanes");
        aux_lanes = x;
    }

    @Override
    public AbstractParameters clone() {
        return null;
    }

}
