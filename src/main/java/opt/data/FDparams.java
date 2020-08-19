package opt.data;

import java.util.Objects;

public class FDparams {
    public Float capacity_vphpl;
    public Float jam_density_vpkpl;
    public Float ff_speed_kph;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        FDparams fDparams = (FDparams) o;
//        return capacity_vphpl.equals(fDparams.capacity_vphpl) &&
//                jam_density_vpkpl.equals(fDparams.jam_density_vpkpl) &&
//                ff_speed_kph.equals(fDparams.ff_speed_kph);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
//    }

    public FDparams(Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        this.capacity_vphpl = capacity_vphpl;
        this.jam_density_vpkpl = jam_density_vpkpl;
        this.ff_speed_kph = ff_speed_kph;
    }

    public jaxb.Roadparam to_jaxb(){
        jaxb.Roadparam rp = new jaxb.Roadparam();
        rp.setCapacity(capacity_vphpl);
        rp.setJamDensity(jam_density_vpkpl);
        rp.setSpeed(ff_speed_kph);
        return rp;
    }

}
