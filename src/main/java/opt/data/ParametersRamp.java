package opt.data;

import jaxb.Roadparam;

public class ParametersRamp extends AbstractParameters {


    public ParametersRamp(String name, Boolean is_inner, Integer gp_lanes, Integer managed_lanes, Integer aux_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        super(name, is_inner, gp_lanes, managed_lanes, aux_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
    }

    public ParametersRamp(Roadparam rp) {
        super(rp);
    }

    public ParametersRamp(Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        super(capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
    }

    @Override
    public AbstractParameters clone() {
        return null;
    }

}
