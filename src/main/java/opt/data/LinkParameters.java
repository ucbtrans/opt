package opt.data;

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

}
