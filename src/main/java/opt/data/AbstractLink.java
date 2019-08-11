package opt.data;

import profiles.Profile1D;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractLink implements Comparable {

    public enum Type {freeway,offramp,onramp,connector}

    // refs
    protected Segment mysegment;
    protected AbstractLink up_link;
    protected AbstractLink dn_link;

    protected final Type type;
    protected long start_node_id;
    protected long end_node_id;

    protected int managed_lanes;
    protected int aux_lanes;

    public final long id;

    public String name;
    public int full_lanes;
    public float length_meters;
    public LinkParameters param;
    protected Map<Long, Profile1D> demands = new HashMap<>();    // commodity -> Profile1D
    protected Map<Long, Profile1D> splits = new HashMap<>();     // commodity -> Profile1D

    /////////////////////////////////////
    // abstract methods
    /////////////////////////////////////

    abstract public Segment insert_up_segment();
    abstract public Segment insert_dn_segment();

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

        // TODO GG : IMPLEMENT MANAGED LANES AND AUX LANES IN XML
        this.managed_lanes = 0;
        this.aux_lanes = 0;
    }

    public AbstractLink(Long id, Type type, Long start_node_id, Long end_node_id, Integer full_lanes, Integer managed_lanes, Integer aux_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        this.id = id;
        this.type = type;
        this.start_node_id = start_node_id;
        this.end_node_id = end_node_id;
        this.full_lanes = full_lanes;
        this.managed_lanes = managed_lanes;
        this.aux_lanes = aux_lanes;
        this.length_meters = length;
        this.param = new LinkParameters(capacity_vphpl,jam_density_vpkpl,ff_speed_kph);
        this.mysegment = mysegment;
    }

    @Override
    public AbstractLink clone(){
        AbstractLink new_link = null;
        try {
            new_link = this.getClass()
                    .getConstructor(Long.class,Type.class,Long.class,Long.class,Integer.class,Integer.class,Integer.class,Float.class,Float.class,Float.class,Float.class,Segment.class)
                    .newInstance(
                            id,
                            type,
                            start_node_id,
                            end_node_id,
                            full_lanes,
                            managed_lanes,
                            aux_lanes,
                            length_meters,
                            param.capacity_vphpl,
                            param.jam_density_vpkpl,
                            param.ff_speed_kph,
                            null);
            new_link.name = name;

            for(Map.Entry<Long, Profile1D> e : demands.entrySet())
                new_link.demands.put(e.getKey(),e.getValue().clone());
            for(Map.Entry<Long, Profile1D> e : splits.entrySet())
                new_link.splits.put(e.getKey(),e.getValue().clone());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new_link;
    }

    /////////////////////////////////////
    // basic getters
    /////////////////////////////////////
    
    public final AbstractLink get_up_link() {
        return up_link;
    }
    
    public final AbstractLink get_dn_link() {
        return dn_link;
    }
    
    public final double get_length_meters() {
        return length_meters;
    }
    
    public final void set_length_meters(float newlength) throws Exception {
        if (newlength<=0.0001)
            throw new Exception("Attempted to set a non-positive segment length");
        length_meters = newlength;
    }

    public final AbstractLink.Type get_type(){
        return this.type;
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

    public final Segment get_segment(){
        return mysegment;
    }

    /////////////////////////////////////
    // lanes
    /////////////////////////////////////

    public int get_gp_lanes(){
        return full_lanes;
    }

    public void set_gp_lanes(int x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive number of lanes");
        full_lanes = x;
    }

    public int get_managed_lanes() {
        return managed_lanes;
    }

    public void set_managed_lanes(int x) throws Exception {
        if(x<0)
            throw new Exception("Attempted to set negative number of lanes");
        managed_lanes = x;
    }

    public int get_aux_lanes(){
        return aux_lanes;
    }

    public void set_aux_lanes(int x) throws Exception{
        if(x<0)
            throw new Exception("Attempted to set negative number of lanes");
        aux_lanes = x;
    }

    /////////////////////////////////////
    // segment getters
    /////////////////////////////////////

    public Segment get_up_segment(){
        return up_link==null ? null : up_link.mysegment;
    }

    public Segment get_dn_segment(){
        return dn_link==null ? null : dn_link.mysegment;
    }

    /////////////////////////////////////
    // link parameters
    /////////////////////////////////////

    public float get_capacity_vphpl(){
        return param.capacity_vphpl;
    }

    public float get_jam_density_vpkpl(){
        return param.jam_density_vpkpl;
    }

    public float get_freespeed_kph(){
        return param.ff_speed_kph;
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
    // demands and splits
    /////////////////////////////////////

    public void set_demand_vph(Long comm_id, Profile1D profile) throws Exception {
        throw new Exception("Invalid call");
    }

    public void set_split(Long comm_id,Profile1D profile) throws Exception {
        throw new Exception("Invalid call");
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
    public int compareTo(Object that) {
        return Long.compare(this.id,((AbstractLink) that).id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractLink that = (AbstractLink) o;
        return start_node_id == that.start_node_id &&
                end_node_id == that.end_node_id &&
                id == that.id &&
                full_lanes == that.full_lanes &&
                managed_lanes == that.managed_lanes &&
                aux_lanes == that.aux_lanes &&
                Float.compare(that.length_meters, length_meters) == 0 &&
                type == that.type &&
                Objects.equals(name, that.name) &&
                param.equals(that.param) &&
                demands.equals(that.demands) &&
                splits.equals(that.splits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, start_node_id, end_node_id, id, name, full_lanes, managed_lanes, aux_lanes, length_meters, param, demands, splits);
    }

    /////////////////////////////////////
    // protected and private
    /////////////////////////////////////

    protected LinkFreewayOrConnector create_up_FwyOrConnLink(Type linktype){

        FreewayScenario fwy_scenario = mysegment.fwy_scenario;

        // create new upstream node
        Node existing_node = fwy_scenario.scenario.nodes.get(start_node_id);
        Node new_node = new Node(fwy_scenario.new_node_id());
        fwy_scenario.scenario.nodes.put(new_node.id,new_node);

        // create new freeway link
        LinkFreewayOrConnector new_link=null;
        switch(linktype){
            case freeway:
                new_link = new LinkFreeway(
                        fwy_scenario.new_link_id(),
                        new_node.id,
                        existing_node.id,
                        full_lanes,
                        managed_lanes,
                        0,
                        length_meters,
                        get_capacity_vphpl(),
                        get_jam_density_vpkpl(),
                        get_freespeed_kph(),
                        null);
                break;
            case connector:
                new_link = new LinkConnector(
                        fwy_scenario.new_link_id(),
                        new_node.id,
                        existing_node.id,
                        full_lanes,
                        managed_lanes,
                        length_meters,
                        get_capacity_vphpl(),
                        get_jam_density_vpkpl(),
                        get_freespeed_kph(),
                        null);
                break;
            default:
                System.err.println("3409gj");
        }

        fwy_scenario.scenario.links.put(new_link.id,new_link);

        new_link.dn_link = this;
        this.up_link = new_link;

        new_node.out_links.add(new_link.id);
        existing_node.in_links.add(new_link.id);


        return new_link;
    }

    protected LinkFreewayOrConnector create_dn_FwyOrConnLink(Type linktype){

        FreewayScenario fwy_scenario = mysegment.fwy_scenario;

        // create new dnstream node
        Node existing_node = fwy_scenario.scenario.nodes.get(end_node_id);
        Node new_node = new Node(fwy_scenario.new_node_id());
        fwy_scenario.scenario.nodes.put(new_node.id,new_node);

        // create new freeway link
        LinkFreewayOrConnector new_link=null;
        switch(linktype){
            case freeway:
                new_link = new LinkFreeway(
                        fwy_scenario.new_link_id(),
                        existing_node.id,
                        new_node.id,
                        full_lanes,
                        managed_lanes,
                        0,
                        length_meters,
                        get_capacity_vphpl(),
                        get_jam_density_vpkpl(),
                        get_freespeed_kph(),
                        null);
                break;
            case connector:
                new_link = new LinkConnector(
                        fwy_scenario.new_link_id(),
                        existing_node.id,
                        new_node.id,
                        full_lanes,
                        managed_lanes,
                        length_meters,
                        get_capacity_vphpl(),
                        get_jam_density_vpkpl(),
                        get_freespeed_kph(),
                        null);
                break;
            default:
                System.err.println("3409gj");
        }

        fwy_scenario.scenario.links.put(new_link.id,new_link);

        new_link.up_link = this;
        this.dn_link = new_link;

        new_node.in_links.add(new_link.id);
        existing_node.out_links.add(new_link.id);

        return new_link;
    }

    protected Segment create_segment(LinkFreewayOrConnector fwy){

        FreewayScenario fwy_scenario = mysegment.fwy_scenario;

        // create new segment
        Segment newseg = new Segment();
        newseg.fwy_scenario = fwy_scenario;
        newseg.id = fwy_scenario.new_seg_id();
        newseg.name = String.format("Segment %d",newseg.id);
        newseg.fwy = fwy;
        fwy.mysegment = newseg;
        fwy_scenario.segments.put(newseg.id,newseg);
        return newseg;
    }

    protected static void connect_segments_dwnstr_node_to(Segment segment, Long new_node_id){

        if(segment==null)
            return;

        FreewayScenario fwy_scenario = segment.fwy_scenario;

        Node new_node = fwy_scenario.scenario.nodes.get(new_node_id);

        Node old_dwn_node = fwy_scenario.scenario.nodes.get(segment.fwy.end_node_id);
        segment.fwy.end_node_id = new_node.id;
        old_dwn_node.in_links.remove(segment.fwy.id);
        new_node.in_links.add(segment.fwy.id);

        Set<LinkOfframp> all_frs = new HashSet<>();
        all_frs.addAll(segment.in_frs);
        all_frs.addAll(segment.out_frs);
        for(LinkOfframp fr : all_frs){
            fr.start_node_id = new_node.id;
            old_dwn_node.out_links.remove(fr.id);
            new_node.out_links.add(fr.id);
        }
    }

    protected static void connect_segments_upstr_node_to(Segment segment, Long new_node_id){

        if(segment==null)
            return;

        FreewayScenario fwy_scenario = segment.fwy_scenario;
        Node new_node = fwy_scenario.scenario.nodes.get(new_node_id);

        Node old_up_node = fwy_scenario.scenario.nodes.get(segment.fwy.start_node_id);
        segment.fwy.start_node_id = new_node.id;
        old_up_node.out_links.remove(segment.fwy.id);
        new_node.out_links.add(segment.fwy.id);

        Set<LinkOnramp> all_ors = new HashSet<>();
        all_ors.addAll(segment.in_ors);
        all_ors.addAll(segment.out_ors);
        for(LinkOnramp or : all_ors){
            or.end_node_id = new_node.id;
            old_up_node.in_links.remove(or.id);
            new_node.in_links.add(or.id);
        }
    }

}
