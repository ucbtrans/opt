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
        super("", 0, 0, 0f, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
    }

    // used by clone
    public ParametersFreeway(String name, Integer gp_lanes, Integer managed_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        super(name,gp_lanes,managed_lanes,length,capacity_vphpl,jam_density_vpkpl,ff_speed_kph);
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
    public void set_aux_lanes(int x){
        aux_lanes = x;
    }

    @Override
    public AbstractParameters clone() {
        AbstractParameters new_params = super.clone();
        ((ParametersFreeway) new_params).aux_lanes = this.aux_lanes;
        return new_params;
    }
}
