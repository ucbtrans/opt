package opt.data;

import jaxb.Roadparam;

public class ParametersRamp extends AbstractParameters {

    public Boolean is_inner;

    public ParametersRamp(String name, Boolean is_inner, Integer gp_lanes, Integer managed_lanes, Boolean managed_lanes_barrier, Boolean managed_lanes_separated,Float length,
                          Float gp_capacity_vphpl, Float gp_jam_density_vpkpl, Float gp_ff_speed_kph,
                          Float mng_capacity_vphpl, Float mng_jam_density_vpkpl, Float mng_ff_speed_kph) {
        super(name, gp_lanes, managed_lanes, managed_lanes_barrier, managed_lanes_separated,length,
                gp_capacity_vphpl, gp_jam_density_vpkpl, gp_ff_speed_kph,
                mng_capacity_vphpl, mng_jam_density_vpkpl, mng_ff_speed_kph);
        this.is_inner = is_inner;
    }

    public ParametersRamp(Roadparam rp) {
        super(rp);
    }

    public ParametersRamp(Float gp_capacity_vphpl, Float gp_jam_density_vpkpl, Float gp_ff_speed_kph , Float mng_capacity_vphpl, Float mng_jam_density_vpkpl, Float mng_ff_speed_kph) {
        super("", 0, 0,false,false, 0f, gp_capacity_vphpl, gp_jam_density_vpkpl, gp_ff_speed_kph,mng_capacity_vphpl,mng_jam_density_vpkpl,mng_ff_speed_kph);
    }

    // used by clone
    public ParametersRamp(String name, Integer gp_lanes, Integer managed_lanes,Boolean managed_lanes_barrier, Boolean managed_lanes_separated, Float length,
                          Float gp_capacity_vphpl, Float gp_jam_density_vpkpl, Float gp_ff_speed_kph,
                          Float mng_capacity_vphpl, Float mng_jam_density_vpkpl, Float mng_ff_speed_kph) {
        super(name,gp_lanes,managed_lanes, managed_lanes_barrier, managed_lanes_separated,length,
                gp_capacity_vphpl,gp_jam_density_vpkpl,gp_ff_speed_kph,
                mng_capacity_vphpl,mng_jam_density_vpkpl,mng_ff_speed_kph);
    }

    @Override
    public boolean get_is_inner(){
        return is_inner;
    }

    @Override
    public void set_is_inner(boolean x) {
        is_inner = x;
    }

    @Override
    public int get_aux_lanes(){
        return 0;
    }

    @Override
    public void set_aux_lanes(int x) {
        //
    }


    @Override
    public AbstractParameters clone() {
        AbstractParameters new_params = super.clone();
        ((ParametersRamp) new_params).is_inner = this.is_inner;
        return new_params;
    }
}
