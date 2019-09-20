package opt.data;

import java.lang.reflect.InvocationTargetException;

import java.util.Objects;

public abstract class AbstractParameters {

    public String name;
    public Integer gp_lanes;
    public Integer managed_lanes;
    public Boolean managed_lanes_barrier;
    public Boolean managed_lanes_separated;
    public Float length;

    public FDparams gp_fd;
    public FDparams mng_fd;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public AbstractParameters(String name, Integer gp_lanes, Integer managed_lanes, Boolean managed_lanes_barrier,Boolean managed_lanes_separated, Float length,
                              Float gp_capacity_vphpl, Float gp_jam_density_vpkpl, Float gp_ff_speed_kph,
                              Float mng_capacity_vphpl, Float mng_jam_density_vpkpl, Float mng_ff_speed_kph) {
        this.name = name;
        this.gp_lanes = gp_lanes;
        this.managed_lanes = managed_lanes;
        this.managed_lanes_barrier = managed_lanes_barrier;
        this.managed_lanes_separated = managed_lanes_separated;
        this.length = length;
        this.gp_fd = new FDparams(gp_capacity_vphpl,gp_jam_density_vpkpl,gp_ff_speed_kph);
        this.mng_fd = new FDparams(mng_capacity_vphpl,mng_jam_density_vpkpl,mng_ff_speed_kph);
    }

    public AbstractParameters(jaxb.Roadparam rp) {
        this(null,0,0,false,false,0f,rp.getCapacity(),rp.getJamDensity(),rp.getSpeed(),null,null,null);
    }

    public AbstractParameters clone() {

        AbstractParameters new_params = null;
        try {
            new_params = this.getClass()
                    .getConstructor(String.class,Integer.class,Integer.class,Boolean.class,Boolean.class,Float.class,Float.class,Float.class,Float.class,Float.class,Float.class,Float.class)
                    .newInstance( name, gp_lanes, managed_lanes, managed_lanes_barrier,managed_lanes_separated,length,
                            gp_fd.capacity_vphpl, gp_fd.jam_density_vpkpl, gp_fd.ff_speed_kph,
                            mng_fd.capacity_vphpl, mng_fd.jam_density_vpkpl, mng_fd.ff_speed_kph);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new_params;
    }

    // TODO : These are just so that Alex does not have to check for type.
    // TODO WONT BE NECESSARY IF WE USE PROPER CASTING ON THE UI SIDE
    abstract public boolean get_is_inner();
    abstract public void set_is_inner(boolean x);
    abstract public int get_aux_lanes();
    abstract public void set_aux_lanes(int x);

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractParameters that = (AbstractParameters) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(gp_lanes, that.gp_lanes) &&
                Objects.equals(managed_lanes, that.managed_lanes) &&
                managed_lanes_barrier==that.managed_lanes_barrier &&
                managed_lanes_separated==that.managed_lanes_separated &&
                Objects.equals(length, that.length) &&
                Objects.equals(gp_fd.capacity_vphpl, that.gp_fd.capacity_vphpl) &&
                Objects.equals(gp_fd.jam_density_vpkpl, that.gp_fd.jam_density_vpkpl) &&
                Objects.equals(gp_fd.ff_speed_kph, that.gp_fd.ff_speed_kph) &&
                Objects.equals(mng_fd.capacity_vphpl, that.mng_fd.capacity_vphpl) &&
                Objects.equals(mng_fd.jam_density_vpkpl, that.mng_fd.jam_density_vpkpl) &&
                Objects.equals(mng_fd.ff_speed_kph, that.mng_fd.ff_speed_kph) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name,gp_lanes, managed_lanes,managed_lanes_barrier,managed_lanes_separated,length,
                gp_fd.capacity_vphpl, gp_fd.jam_density_vpkpl, gp_fd.ff_speed_kph,
                mng_fd.capacity_vphpl, mng_fd.jam_density_vpkpl, mng_fd.ff_speed_kph);
    }
}
