package opt.data;

public abstract class AbstractParameters {

    public String name;
    public Integer gp_lanes;
    public Integer managed_lanes;
    public Float length;
    public Float capacity_vphpl;
    public Float jam_density_vpkpl;
    public Float ff_speed_kph;

    public AbstractParameters(String name, Integer gp_lanes, Integer managed_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        this.name = name;
        this.gp_lanes = gp_lanes;
        this.managed_lanes = managed_lanes;
        this.length = length;
        this.capacity_vphpl = capacity_vphpl;
        this.jam_density_vpkpl = jam_density_vpkpl;
        this.ff_speed_kph = ff_speed_kph;
    }
}
