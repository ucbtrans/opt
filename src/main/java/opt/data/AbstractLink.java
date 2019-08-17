package opt.data;

import profiles.Profile1D;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractLink implements Comparable {

    public enum Type {freeway,offramp,onramp,connector}

    public final long id;

    protected Segment mysegment;
    protected AbstractLink up_link;
    protected AbstractLink dn_link;

    protected long start_node_id;
    protected long end_node_id;

    public AbstractParameters params;

    protected Map<Long, Profile1D> demands = new HashMap<>();    // commodity -> Profile1D
    protected Map<Long, Profile1D> splits = new HashMap<>();     // commodity -> Profile1D

    /////////////////////////////////////
    // abstract methods
    /////////////////////////////////////

    abstract public AbstractLink.Type get_type();
    abstract public boolean is_ramp();
    abstract public Segment insert_up_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params);
    abstract public Segment insert_dn_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params);
    abstract protected boolean is_permitted_uplink(AbstractLink link);
    abstract protected boolean is_permitted_dnlink(AbstractLink link);

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public AbstractLink(jaxb.Link link){
        this.id = link.getId();
        this.start_node_id = link.getStartNodeId();
        this.end_node_id = link.getEndNodeId();
    }

    // used by clone
    public AbstractLink(long id, Long start_node_id, Long end_node_id, AbstractParameters params){
        this.id = id;
        this.mysegment = null;
        this.up_link = null;
        this.dn_link = null;
        this.start_node_id = start_node_id;
        this.end_node_id = end_node_id;
        this.params = params;
    }

    public AbstractLink(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, AbstractParameters params){
        this.id = id;
        this.mysegment = mysegment;
        this.up_link = up_link;
        this.dn_link = dn_link;
        this.start_node_id = start_node_id;
        this.end_node_id = end_node_id;
        this.params = params;
    }

    public AbstractLink(long id,Segment mysegment,AbstractLink up_link,AbstractLink dn_link,Long start_node_id,Long end_node_id,String name,Boolean is_inner,Integer gp_lanes,Integer managed_lanes,Integer aux_lanes,Float length,Float capacity_vphpl,Float jam_density_vpkpl,Float ff_speed_kph){
        this.id = id;
        this.mysegment = mysegment;
        this.up_link = up_link;
        this.dn_link = dn_link;
        this.start_node_id = start_node_id;
        this.end_node_id = end_node_id;
    }

    @Override
    public AbstractLink clone(){
        AbstractLink new_link = null;
        try {
            new_link = this.getClass()
                    .getConstructor(long.class,Long.class,Long.class, AbstractParameters.class)
                    .newInstance(
                            id,
                            start_node_id,
                            end_node_id,
                            params.clone());

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

    public final boolean is_source(){
        return mysegment.fwy_scenario.scenario.nodes.get(start_node_id).in_links.isEmpty();
    }

    public final boolean is_sink(){
        return mysegment.fwy_scenario.scenario.nodes.get(end_node_id).out_links.isEmpty();
    }

    public final Segment get_segment(){
        return mysegment;
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

    public final String get_name(){
        return params.name;
    }

    public final void set_name(String name){
        params.name = name;
    }

    public int get_gp_lanes(){
        return params.gp_lanes;
    }

    public void set_gp_lanes(int x) {
        params.gp_lanes = x;
    }

    public int get_managed_lanes() {
        return params.managed_lanes;
    }

    public void set_managed_lanes(int x) {
        params.managed_lanes = x;
    }

    public boolean get_barrier(){
        return params.managed_lanes_barrier;
    }

    public void set_barrier(boolean x){
        params.managed_lanes_barrier = x;
    }

    public boolean get_separated(){
        return params.managed_lanes_separated;
    }

    public void set_separated(boolean x){
        params.managed_lanes_separated = x;
    }

    public int get_aux_lanes() {
        return (this.params instanceof ParametersFreeway) ? params.get_aux_lanes() : 0;
    }

    public void set_aux_lanes(int x) {
        if(this.params instanceof ParametersFreeway)
            params.set_aux_lanes(x);
    }


    public boolean get_is_inner () {
        return (this.params instanceof ParametersRamp) ? params.get_is_inner() : false;

    }

    public void set_is_inner(boolean x) {
        if(this.params instanceof ParametersRamp)
            params.set_is_inner(x);
    }

    public final float get_length_meters() {
        return params.length;
    }

    public final void set_length_meters(float newlength) throws Exception {
        if (newlength<=0.0001)
            throw new Exception("Attempted to set a non-positive segment length");
        params.length = newlength;
    }

    public float get_capacity_vphpl(){
        return params.capacity_vphpl;
    }

    public float get_jam_density_vpkpl(){
        return params.jam_density_vpkpl;
    }

    public float get_freespeed_kph(){
        return params.ff_speed_kph;
    }

    public void set_capacity_vphpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive capacity");
        params.capacity_vphpl = x;
    }

    public void set_jam_density_vpkpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive jam density");
        params.jam_density_vpkpl = x;
    }

    public void set_freespeed_kph(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive free speed");
        params.ff_speed_kph = x;
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
    // connect
    /////////////////////////////////////

    public boolean connect_to_upstream(AbstractLink conn_up_link){

        if(up_link!=null || conn_up_link==null || conn_up_link.dn_link!=null)
            return false;

        if(!is_permitted_uplink(conn_up_link))
            return false;

        FreewayScenario fwy_scenario = mysegment.fwy_scenario;
        Node my_start_node = fwy_scenario.scenario.nodes.get(start_node_id);
        Node their_end_node = fwy_scenario.scenario.nodes.get(conn_up_link.end_node_id);

        // assumptions
        assert(my_start_node.out_links.size()==1 && my_start_node.out_links.iterator().next()==this.id);
        assert(their_end_node.in_links.size()==1 && their_end_node.in_links.iterator().next()==conn_up_link.id);

        // connect link references
        up_link = conn_up_link;
        conn_up_link.dn_link = this;

        // remove my start node
        fwy_scenario.scenario.nodes.remove(my_start_node.id);

        // move to new startnode
        this.start_node_id = their_end_node.id;
        their_end_node.out_links.add(this.id);

        // delete demands
        demands = new HashMap<>();

        return true;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        return String.format("%d",id);
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
        return id == that.id &&
                start_node_id == that.start_node_id &&
                end_node_id == that.end_node_id &&
                Objects.equals(params, that.params) &&
                demands.equals(that.demands) &&
                splits.equals(that.splits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start_node_id, end_node_id, params, demands, splits);
    }

    /////////////////////////////////////
    // protected and private
    /////////////////////////////////////

    protected LinkFreewayOrConnector create_up_FwyOrConnLink(Type linktype, ParametersFreeway link_params){

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
                        fwy_scenario.new_link_id(), // id,
                        null, // mysegment,
                        null,       // up_link
                        null,       // dn_link
                        new_node.id,    // start_node_id
                        existing_node.id, // end_node_id
                        link_params );

                break;
            case connector:

                new_link = new LinkConnector(
                        fwy_scenario.new_link_id(), // id,
                        null,// mysegment,
                        null,       // up_link
                        null,       // dn_link
                        new_node.id,   // start_node_id
                        existing_node.id,   // end_node_id
                        link_params );

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

    protected LinkFreewayOrConnector create_dn_FwyOrConnLink(Type linktype, ParametersFreeway link_params){

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
                        fwy_scenario.new_link_id(),   // id,
                        null, // mysegment,
                        null, // up_link,
                        null, // dn_link,
                        existing_node.id, // start_node_id,
                        new_node.id, // end_node_id,
                        link_params) ; // params

                break;
            case connector:

                new_link = new LinkConnector(
                    fwy_scenario.new_link_id(), // id,
                    null, // mysegment,
                    null, // up_link,
                    null, // dn_link,
                    existing_node.id,// start_node_id,
                    new_node.id,// end_node_id,
                    link_params ); // params

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

    protected Segment create_segment(LinkFreewayOrConnector fwy,String seg_name){

        FreewayScenario fwy_scenario = mysegment.fwy_scenario;

        // create new segment
        Segment newseg = new Segment();
        newseg.fwy_scenario = fwy_scenario;
        newseg.id = fwy_scenario.new_seg_id();
        newseg.name = seg_name;
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
