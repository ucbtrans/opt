package opt.data;

import java.util.Objects;

public class LinkParameters {

    protected float capacity_vphpl;
    protected float jam_density_vpkpl;
    protected float ff_speed_kph;

    public LinkParameters(float capacity_vphpl, float jam_density_vpkpl, float ff_speed_kph) {
        this.capacity_vphpl = capacity_vphpl;
        this.jam_density_vpkpl = jam_density_vpkpl;
        this.ff_speed_kph = ff_speed_kph;
    }


    public LinkParameters(jaxb.Roadparam rp) {
        this(rp.getCapacity(),rp.getJamDensity(),rp.getSpeed());
    }

//    public LinkParameters deep_copy(){
//
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkParameters that = (LinkParameters) o;
        return Float.compare(that.capacity_vphpl, capacity_vphpl) == 0 &&
                Float.compare(that.jam_density_vpkpl, jam_density_vpkpl) == 0 &&
                Float.compare(that.ff_speed_kph, ff_speed_kph) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
    }

}
