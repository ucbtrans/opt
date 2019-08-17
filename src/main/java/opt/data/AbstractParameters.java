package opt.data;

import java.lang.reflect.InvocationTargetException;

import java.util.Objects;

public abstract class AbstractParameters {

    public String name;
    public Integer gp_lanes;
    public Integer managed_lanes;
    public Float length;
    public Float capacity_vphpl;
    public Float jam_density_vpkpl;
    public Float ff_speed_kph;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public AbstractParameters(String name, Integer gp_lanes, Integer managed_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        this.name = name;
        this.gp_lanes = gp_lanes;
        this.managed_lanes = managed_lanes;
        this.length = length;
        this.capacity_vphpl = capacity_vphpl;
        this.jam_density_vpkpl = jam_density_vpkpl;
        this.ff_speed_kph = ff_speed_kph;
    }

    public AbstractParameters(jaxb.Roadparam rp) {
        this(null,0,0,0f,rp.getCapacity(),rp.getJamDensity(),rp.getSpeed());
    }

    public AbstractParameters clone() {

        AbstractParameters new_params = null;
        try {
            new_params = this.getClass()
                    .getConstructor(String.class,Integer.class,Integer.class,Float.class,Float.class,Float.class,Float.class)
                    .newInstance( name, gp_lanes, managed_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
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
                Objects.equals(length, that.length) &&
                Objects.equals(capacity_vphpl, that.capacity_vphpl) &&
                Objects.equals(jam_density_vpkpl, that.jam_density_vpkpl) &&
                Objects.equals(ff_speed_kph, that.ff_speed_kph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name,gp_lanes, managed_lanes,length,capacity_vphpl,jam_density_vpkpl,ff_speed_kph);
    }
}
