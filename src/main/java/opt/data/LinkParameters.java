package opt.data;

import java.util.Objects;

public class LinkParameters {

    public String name;
    public Integer gp_lanes;
    public Integer managed_lanes;
    public Integer aux_lanes;
    public Float length;
    public Float capacity_vphpl;
    public Float jam_density_vpkpl;
    public Float ff_speed_kph;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkParameters(String name, Integer gp_lanes, Integer managed_lanes, Integer aux_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        this.name = name;
        this.gp_lanes = gp_lanes;
        this.managed_lanes = managed_lanes;
        this.aux_lanes = aux_lanes;
        this.length = length;
        this.capacity_vphpl = capacity_vphpl;
        this.jam_density_vpkpl = jam_density_vpkpl;
        this.ff_speed_kph = ff_speed_kph;
    }

    public LinkParameters(jaxb.Roadparam rp) {
        this(null,0,0,0,0f,rp.getCapacity(),rp.getJamDensity(),rp.getSpeed());
    }

    // UI LEGACY, TRY TO REMOVE
    public LinkParameters(Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        this("", 0 ,0,0,0f,  capacity_vphpl,  jam_density_vpkpl,  ff_speed_kph);
    }

    public LinkParameters clone() {
        return new LinkParameters(name,gp_lanes,managed_lanes,aux_lanes,length,capacity_vphpl,jam_density_vpkpl,ff_speed_kph);
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkParameters that = (LinkParameters) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(gp_lanes, that.gp_lanes) &&
                Objects.equals(managed_lanes, that.managed_lanes) &&
                Objects.equals(aux_lanes, that.aux_lanes) &&
                Objects.equals(length, that.length) &&
                Objects.equals(capacity_vphpl, that.capacity_vphpl) &&
                Objects.equals(jam_density_vpkpl, that.jam_density_vpkpl) &&
                Objects.equals(ff_speed_kph, that.ff_speed_kph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, gp_lanes, managed_lanes, aux_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
    }
}
