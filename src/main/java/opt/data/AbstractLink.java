package opt.data;

import java.util.Objects;

public abstract class AbstractLink {

    public enum Type {freeway,offramp,onramp,connector}

    protected final Type type;
    protected Segment mysegment;
    protected AbstractLink up_link;
    protected AbstractLink dn_link;

    protected long start_node_id;
    protected long end_node_id;

    public final long id;
    public String name;
    public int full_lanes;
    public float length_meters;
    public LinkParameters param;

    /////////////////////////////////////
    // abstract methods
    /////////////////////////////////////

    abstract public int get_managed_lanes();
    abstract public int get_aux_lanes();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public AbstractLink(jaxb.Link link, Type type, jaxb.Roadparam rp){
        this.id = link.getId();
        this.type = type;
        this.start_node_id = link.getStartNodeId();
        this.end_node_id = link.getEndNodeId();
        this.full_lanes = link.getFullLanes();
        this.length_meters = link.getLength();
        this.param = new LinkParameters(rp);
    }

    public AbstractLink(Long id, Type type, Long start_node_id, Long end_node_id, Integer full_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        this.id = id;
        this.type = type;
        this.start_node_id = start_node_id;
        this.end_node_id = end_node_id;
        this.full_lanes = full_lanes;
        this.length_meters = length;
        this.param = new LinkParameters(capacity_vphpl,jam_density_vpkpl,ff_speed_kph);
        this.mysegment = mysegment;
    }

    public AbstractLink deep_copy(){
        AbstractLink new_link = null;
//        try {
//            Constructor<AbstractLink> constr = (Constructor<AbstractLink>)getClass().getConstructor(Long.class, AbstractLink.Type.class, Long.class, Long.class, Integer.class, Float.class, Float.class, Float.class, Float.class, Segment.class);
//            new_link = constr.newInstance(
//                    id,
//                    type,
//                    start_node_id,
//                    end_node_id,
//                    full_lanes,
//                    length_meters,
//                    param.capacity_vphpl,
//                    param.jam_density_vpkpl,
//                    param.ff_speed_kph,
//                    mysegment);
//            new_link.name = name;
//        } catch (InstantiationException ex) {
//            ex.printStackTrace();
//        } catch (IllegalAccessException ex) {
//            ex.printStackTrace();
//        } catch (InvocationTargetException ex) {
//            ex.printStackTrace();
//        } catch (NoSuchMethodException ex) {
//            ex.printStackTrace();
//        }
        return new_link;

    }

    /////////////////////////////////////
    // getters
    /////////////////////////////////////

    public int get_gp_lanes(){
        return full_lanes;
    }

    public final Segment get_up_segment(){
        return up_link==null ? null : up_link.mysegment;
    }

    public final Segment get_dn_segment(){
        return dn_link==null ? null : dn_link.mysegment;
    }

    public final AbstractLink.Type get_type(){
        return this.type;
    }

    public final Segment get_segment(){
        return mysegment;
    }

    public final boolean is_source(){
        return mysegment.fwy_scenario.scenario.nodes.get(start_node_id).in_links.isEmpty();
    }

    public final boolean is_sink(){
        return mysegment.fwy_scenario.scenario.nodes.get(end_node_id).out_links.isEmpty();
    }


    public final boolean is_ramp(){
        return this.type== Type.onramp || type==Type.offramp;
    }


    public float get_capacity_vphpl(){
        return param.capacity_vphpl;
    }

    public float get_jam_density_vpkpl(){
        return param.jam_density_vpkpl;
    }

    public float get_freespeed_kph(){
        return param.ff_speed_kph;
    }

    public void set_gp_lanes(int x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive number of lanes");
        full_lanes = x;
    }

    public void set_managed_lanes(int x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive number of lanes");
        System.out.println("NOT IMPLEMENTED!");
    }

    public void set_capacity_vphpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive capacity");
        param.capacity_vphpl = x;
    }

    public void set_jam_density_vpkpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive jam density");
        param.jam_density_vpkpl = x;
    }

    public void set_freespeed_kph(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive free speed");
        param.ff_speed_kph = x;
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
        AbstractLink that = (AbstractLink) o;
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
