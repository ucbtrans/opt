package opt.data;

import java.util.Objects;

public class ParametersRamp extends AbstractParameters {

    public Boolean is_inner;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public ParametersRamp(String name,Boolean is_inner, Integer gp_lanes, Integer managed_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        super(name,gp_lanes,managed_lanes,length, capacity_vphpl, jam_density_vpkpl,ff_speed_kph);
        this.is_inner = is_inner;
    }

    public ParametersRamp(jaxb.Roadparam rp) {
        this(null,false,0,0,0f,rp.getCapacity(),rp.getJamDensity(),rp.getSpeed());
    }

    // UI LEGACY, TRY TO REMOVE
    public ParametersRamp(Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        this("",false, 0 ,0,0f,  capacity_vphpl,  jam_density_vpkpl,  ff_speed_kph);
    }

    public ParametersRamp clone() {
        return new ParametersRamp(name,is_inner,gp_lanes,managed_lanes,length,capacity_vphpl,jam_density_vpkpl,ff_speed_kph);
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParametersRamp that = (ParametersRamp) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(is_inner, that.is_inner) &&
                Objects.equals(gp_lanes, that.gp_lanes) &&
                Objects.equals(managed_lanes, that.managed_lanes) &&
                Objects.equals(length, that.length) &&
                Objects.equals(capacity_vphpl, that.capacity_vphpl) &&
                Objects.equals(jam_density_vpkpl, that.jam_density_vpkpl) &&
                Objects.equals(ff_speed_kph, that.ff_speed_kph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, is_inner, gp_lanes, managed_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
    }
}
