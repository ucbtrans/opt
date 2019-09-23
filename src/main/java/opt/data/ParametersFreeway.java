package opt.data;

import jaxb.Roadparam;

public class ParametersFreeway extends AbstractParameters  {

    public Integer aux_lanes;
    public FDparams aux_fd;

    public ParametersFreeway(String name, Integer gp_lanes, Integer mng_lanes,Boolean mng_barrier, Boolean mng_separated,Integer aux_lanes, Float length,
                             Float gp_capacity_vphpl, Float gp_jam_density_vpkpl, Float gp_ff_speed_kph,
                             Float mng_capacity_vphpl, Float mng_jam_density_vpkpl, Float mng_ff_speed_kph,
                             Float aux_capacity_vphpl, Float aux_jam_density_vpkpl, Float aux_ff_speed_kph) {
        super(name, gp_lanes, mng_lanes, mng_barrier, mng_separated,length,
                gp_capacity_vphpl, gp_jam_density_vpkpl, gp_ff_speed_kph,
                mng_capacity_vphpl, mng_jam_density_vpkpl, mng_ff_speed_kph);
        this.aux_lanes = aux_lanes;
        this.aux_fd = new FDparams(aux_capacity_vphpl,aux_jam_density_vpkpl,aux_ff_speed_kph);
    }

    public ParametersFreeway(Roadparam rp) {
        super(rp);
    }

    public ParametersFreeway(Float gp_capacity_vphpl, Float gp_jam_density_vpkpl, Float gp_ff_speed_kph,
                             Float mng_capacity_vphpl, Float mng_jam_density_vpkpl, Float mng_ff_speed_kph,
                             Float aux_capacity_vphpl, Float aux_jam_density_vpkpl, Float aux_ff_speed_kph) {
        super("", 0, 0, false,false,0f, gp_capacity_vphpl, gp_jam_density_vpkpl, gp_ff_speed_kph,mng_capacity_vphpl,mng_jam_density_vpkpl,mng_ff_speed_kph);
        this.aux_fd = new FDparams(aux_capacity_vphpl,aux_jam_density_vpkpl,aux_ff_speed_kph);
    }

    // used by clone
    public ParametersFreeway(String name, Integer gp_lanes, Integer mng_lanes, Boolean mng_barrier, Boolean mng_separated,Float length,
                             Float gp_capacity_vphpl, Float gp_jam_density_vpkpl, Float gp_ff_speed_kph,
                             Float mng_capacity_vphpl, Float mng_jam_density_vpkpl, Float mng_ff_speed_kph,
                             Float aux_capacity_vphpl, Float aux_jam_density_vpkpl, Float aux_ff_speed_kph) {
        super(name,gp_lanes,mng_lanes, mng_barrier, mng_separated,length,gp_capacity_vphpl,gp_jam_density_vpkpl,gp_ff_speed_kph,mng_capacity_vphpl,mng_jam_density_vpkpl,mng_ff_speed_kph);
        this.aux_fd = new FDparams(aux_capacity_vphpl,aux_jam_density_vpkpl,aux_ff_speed_kph);
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
