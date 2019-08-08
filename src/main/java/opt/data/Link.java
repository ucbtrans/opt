package opt.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class Link {

    public enum Type {freeway,offramp,onramp,connector}

    protected long id;
    protected String name;
    protected Type type;
    protected long start_node_id;
    protected long end_node_id;
    protected int full_lanes;
    protected float length_meters;

//    protected float capacity_vphpl;
//    protected float jam_density_vpkpl;
//    protected float ff_speed_kph;
    protected LinkParameters param;

    protected Segment mysegment;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Link(jaxb.Link link, Type type, jaxb.Roadparam rp){
        this.id = link.getId();
        this.type = type;
        this.start_node_id = link.getStartNodeId();
        this.end_node_id = link.getEndNodeId();
        this.full_lanes = link.getFullLanes();
        this.length_meters = link.getLength();
        this.param = new LinkParameters(rp);
    }

    public Link(Long id, Type type, Long start_node_id, Long end_node_id, Integer full_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        this.id = id;
        this.type = type;
        this.start_node_id = start_node_id;
        this.end_node_id = end_node_id;
        this.full_lanes = full_lanes;
        this.length_meters = length;
        this.param = new LinkParameters(capacity_vphpl,jam_density_vpkpl,ff_speed_kph);
        this.mysegment = mysegment;

    }

    public Link deep_copy(){
        Link new_link = null;
        try {
            Constructor<Link> constr = (Constructor<Link>)getClass().getConstructor(Long.class,Link.Type.class, Long.class, Long.class, Integer.class, Float.class, Float.class, Float.class, Float.class, Segment.class);
            new_link = constr.newInstance(
                    id,
                    type,
                    start_node_id,
                    end_node_id,
                    full_lanes,
                    length_meters,
                    param.capacity_vphpl,
                    param.jam_density_vpkpl,
                    param.ff_speed_kph,
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
    
    public String get_name(){
        return name;
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

    public Link.Type get_type(){
        return this.type;
    }

    public boolean is_ramp(){
        return this.type== Type.onramp || type==Type.offramp;
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
                id,start_node_id,end_node_id,full_lanes, length_meters,param.capacity_vphpl,param.jam_density_vpkpl,param.ff_speed_kph);
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link that = (Link) o;
        return id == that.id &&
                type== that.type &&
                start_node_id == that.start_node_id &&
                end_node_id == that.end_node_id &&
                full_lanes == that.full_lanes &&
                Float.compare(that.length_meters, length_meters) == 0 &&
                Float.compare(that.param.capacity_vphpl, param.capacity_vphpl) == 0 &&
                Float.compare(that.param.jam_density_vpkpl, param.jam_density_vpkpl) == 0 &&
                Float.compare(that.param.ff_speed_kph, param.ff_speed_kph) == 0 &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, start_node_id, end_node_id, full_lanes, length_meters,param.capacity_vphpl, param.jam_density_vpkpl, param.ff_speed_kph);
    }
}
