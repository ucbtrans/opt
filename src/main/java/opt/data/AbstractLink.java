package opt.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public abstract class AbstractLink {
    protected long id;
    protected String name;
    protected long start_node_id;
    protected long end_node_id;
    protected int full_lanes;
    protected float length_meters;

    protected float capacity_vphpl;
    protected float jam_density_vpkpl;
    protected float ff_speed_kph;

    protected Segment mysegment;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public AbstractLink(jaxb.Link link, jaxb.Roadparam rp){
        this.id = link.getId();
        this.start_node_id = link.getStartNodeId();
        this.end_node_id = link.getEndNodeId();
        this.full_lanes = link.getFullLanes();
        this.length_meters = link.getLength();
        this.capacity_vphpl = rp.getCapacity();
        this.jam_density_vpkpl = rp.getJamDensity();
        this.ff_speed_kph = rp.getSpeed();
    }

    public AbstractLink(Long id, Long start_node_id, Long end_node_id, Integer full_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph,Segment mysegment) {

        this.id = id;
        this.start_node_id = start_node_id;
        this.end_node_id = end_node_id;
        this.full_lanes = full_lanes;
        this.length_meters = length;

        this.capacity_vphpl = capacity_vphpl;
        this.jam_density_vpkpl = jam_density_vpkpl;
        this.ff_speed_kph = ff_speed_kph;
        this.mysegment = mysegment;

    }

    public AbstractLink deep_copy(){
        AbstractLink new_link = null;
        try {
            Constructor<AbstractLink> constr = (Constructor<AbstractLink>)getClass().getConstructor(Long.class, Long.class, Long.class, Integer.class, Float.class, Float.class, Float.class, Float.class, Segment.class);
            new_link = constr.newInstance(
                    id,
                    start_node_id,
                    end_node_id,
                    full_lanes,
                    length_meters,
                    capacity_vphpl,
                    jam_density_vpkpl,
                    ff_speed_kph,
                    mysegment);
            new_link.name = name;
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        return new_link;

    }
    /////////////////////////////////////
    // getters
    /////////////////////////////////////

    public Long get_id(){
        return id;
    }

    public Segment get_segment(){
        return mysegment;
    }

    public boolean is_source(){
        return mysegment.fwy_scenario.scenario.nodes.get(start_node_id).in_links.isEmpty();
    }

    public boolean is_sink(){
        return mysegment.fwy_scenario.scenario.nodes.get(end_node_id).out_links.isEmpty();
    }


    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        String str = String.format(
                "\tid\t%d\n" +
                        "\tstart_node_id\t%d\n" +
                        "\tend_node_id\t%d\n" +
                        "\tfull_lanes\t%d\n" +
                        "\tlength_meters\t%f\n" +
                        "\tcapacity_vphpl\t%f\n" +
                        "\tjam_density_vpkpl\t%f\n" +
                        "\tff_speed_kph\t%f",
                id,start_node_id,end_node_id,full_lanes, length_meters,capacity_vphpl,jam_density_vpkpl,ff_speed_kph);
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractLink that = (AbstractLink) o;
        return id == that.id &&
                start_node_id == that.start_node_id &&
                end_node_id == that.end_node_id &&
                full_lanes == that.full_lanes &&
                Float.compare(that.length_meters, length_meters) == 0 &&
                Float.compare(that.capacity_vphpl, capacity_vphpl) == 0 &&
                Float.compare(that.jam_density_vpkpl, jam_density_vpkpl) == 0 &&
                Float.compare(that.ff_speed_kph, ff_speed_kph) == 0 &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, start_node_id, end_node_id, full_lanes, length_meters, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
    }
}
