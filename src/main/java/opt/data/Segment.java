package opt.data;

import error.OTMErrorLog;
import profiles.Profile1D;
import utils.OTMUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Segment {

    protected FreewayScenario fwy_scenario;

    protected String name;
    protected final long id;
    protected Long fwy_id = null;
    protected Long or_id = null;
    protected Long fr_id = null;

    // maps to adjacent segments
    protected Long segment_fwy_dn_id;
    protected Long segment_fwy_up_id;
    protected Long segment_fr_dn_id;
    protected Long segment_or_up_id;

    protected Map<Long,Profile1D> fwy_demands = new HashMap<>();     // commodity -> Profile1D
    protected Map<Long,Profile1D> or_demands = new HashMap<>();     // commodity -> Profile1D
    protected Map<Long,Profile1D> fr_splits = new HashMap<>();      // commodity -> Profile1D

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    // used in deep copy
    public Segment(long id,String name, FreewayScenario fwy_scenario){
        this.id = id;
        this.name = name;
        this.fwy_scenario = fwy_scenario;
    }

    // used by FreewayScenario jaxb constructor
    public Segment(FreewayScenario fwy_scenario,long id, jaxbopt.Sgmt sgmt) throws Exception {

        this.id = id;
        this.fwy_scenario = fwy_scenario;
        this.name = sgmt.getName();

        List<Long> link_ids = OTMUtils.csv2longlist(sgmt.getLinks());
        List<Link> links = link_ids.stream()
                .map(link_id->fwy_scenario.scenario.links.get(link_id))
                .collect(Collectors.toList());

        // find freeway link
        Set<Link> fwy_links = links.stream()
                .filter(link->link.type==Link.Type.freeway || link.type== Link.Type.connector)
                .collect(Collectors.toSet());

        // check exactly one mailine link
        if( fwy_links.size()!=1 )
            throw new Exception("All segments must contain exactly one freeway link");

        Link fwy = fwy_links.iterator().next();
        fwy_id = fwy.id;
        links.remove(fwy);

        // check that what is remaining are at most two and all ramps
        if (links.size()>2)
            throw new Exception("Segment has too many links");
        if (!links.stream().allMatch(link->link.is_ramp()))
            throw new Exception("Links in a segment must be either freeway or ramp");

        // find onramps
        Set<Link> ors = links.stream()
                .filter(link-> link.is_ramp())
                .filter(link-> link.end_node_id==fwy.start_node_id || link.end_node_id==fwy.end_node_id)
                .collect(Collectors.toSet());

        // at most one onramp
        if (!ors.isEmpty()){
            if(ors.size()>1)
                throw new Exception("At most one onramp per segment");
            Link or = ors.iterator().next();
            or_id = or.id;
            links.remove(or);
        }

        // find offramps
        Set<Link> frs = links.stream()
                .filter(link-> link.is_ramp())
                .filter(link-> link.start_node_id==fwy.start_node_id || link.start_node_id==fwy.end_node_id)
                .collect(Collectors.toSet());

        // at most one offramp
        if (!frs.isEmpty()){
            if(frs.size()>1)
                throw new Exception("At most one offramp per segment");
            Link fr = frs.iterator().next();
            fr_id = fr.id;
            links.remove(fr);
        }

        // there should be nothing left over
        assert(links.isEmpty());
    }

    // used by Segment.create_new_segment
    public Segment(FreewayScenario fwy_scenario,long id,String name,Long fwy_id){
        this.fwy_scenario = fwy_scenario;
        this.id = id;
        this.name = name;
        this.fwy_id = fwy_id;
    }

    public Segment deep_copy(FreewayScenario scenario){
        Segment seg_cpy = new Segment(id,name,scenario);
        seg_cpy.fwy_id = fwy_id;
        seg_cpy.or_id = or_id;
        seg_cpy.fr_id = fr_id;
        seg_cpy.segment_fwy_dn_id = segment_fwy_dn_id;
        seg_cpy.segment_fwy_up_id = segment_fwy_up_id;
        seg_cpy.segment_fr_dn_id = segment_fr_dn_id;
        seg_cpy.segment_or_up_id = segment_or_up_id;

        for(Map.Entry<Long,Profile1D> e : fwy_demands.entrySet())
            seg_cpy.fwy_demands.put(e.getKey(),e.getValue().clone());

        for(Map.Entry<Long,Profile1D> e : or_demands.entrySet())
            seg_cpy.or_demands.put(e.getKey(), e.getValue().clone());

        for(Map.Entry<Long,Profile1D> e : fr_splits.entrySet())
            seg_cpy.fr_splits.put(e.getKey(),e.getValue().clone());

        return seg_cpy;
    }

    /////////////////////////////////////
    // name and length
    /////////////////////////////////////

    /**
     * Get the name of this segment
     * @return String name
     */
    public String get_name(){
        return name;
    }

    /**
     * Set the name of this segment.
     * @param new_name
     */
    public void set_name(String new_name) throws Exception {
        if (new_name.contains("|"))
            throw new Exception("Invalid name");
        this.name = new_name;
    }

    /**
     * Get the length of this segment in meters
     * @return float
     */
    public float get_length_meters(){
        return fwy().length_meters;
    }

    /**
     * Set the length of this segment in meters
     * @param newlength
     */
    public void set_length_meters(float newlength) throws Exception {
        if (newlength<=0.0001)
            throw new Exception("Attempted to set a non-positive segment length");
        fwy().length_meters = newlength;
    }

    /////////////////////////////////////
    // insert
    /////////////////////////////////////

    public Segment insert_upstrm_managed_segment(){
        System.err.println("NOT IMPLEMENTED!!!");
        return null;
    }

    public Segment insert_upstrm_mainline_segment(){

        // create new upstream node
        Node existing_node = fwy_scenario.scenario.nodes.get(fwy().start_node_id);
        Node new_node = new Node(fwy_scenario.new_node_id());

        // connect upstream links to new node
        connect_segment_to_downstream_node(get_upstrm_fwy_segment(),new_node);

        // create new freeway link
        Link new_link = new Link(
                fwy_scenario.new_link_id(),
                Link.Type.freeway,
                new_node.id,
                existing_node.id,
                get_mixed_lanes(),
                get_length_meters(),
                get_capacity_vphpl(),
                get_jam_density_vpkpl(),
                get_freespeed_kph(),null);

        // connect new link to start and end nodes
        existing_node.in_links.add(new_link.id);
        new_node.out_links.add(new_link.id);

        // add link to scenario
        fwy_scenario.scenario.links.put(new_link.id,new_link);

        // create new segment
        Segment newseg = create_new_segment(new_link);
        newseg.segment_fwy_dn_id = this.id;
        newseg.segment_fwy_up_id = this.segment_fwy_up_id;
        this.segment_fwy_up_id = newseg.id;

        // add to fwy scenario
        fwy_scenario.segments.put(newseg.id,newseg);

        return newseg;
    }

    public Segment insert_upstrm_onramp_segment() {

        if (!has_onramp())
            return null;

        Link or = or();

        // existing node and new node
        Node existing_node = fwy_scenario.scenario.nodes.get(or.end_node_id);
        Node new_node = new Node(fwy_scenario.new_node_id());

        // connect upstream links to new node
        connect_segment_to_downstream_node(get_upstrm_or_segment(),new_node);

        // create new freeway link
        Link new_link = new Link(
                fwy_scenario.new_link_id(),
                Link.Type.connector,
                new_node.id,
                existing_node.id,
                get_mixed_lanes(),
                get_length_meters(),
                get_capacity_vphpl(),
                get_jam_density_vpkpl(),
                get_freespeed_kph(),null);

        // connect new link to start and end nodes
        existing_node.in_links.add(new_link.id);
        new_node.out_links.add(new_link.id);

        // add link to scenario
        fwy_scenario.scenario.links.put(new_link.id,new_link);

        // create new segment
        Segment newseg = create_new_segment(new_link);
        newseg.segment_fwy_dn_id = this.id;
        newseg.segment_fwy_up_id = this.segment_or_up_id;
        this.segment_or_up_id = newseg.id;

        // add to fwy scenario
        fwy_scenario.segments.put(newseg.id,newseg);

        return newseg;
    }

    public Segment insert_dnstrm_managed_segment(){
        System.err.println("NOT IMPLEMENTED!!!");
        return null;
    }

    public Segment insert_dnstrm_mainline_segment(){

        // existing node and new node
        Node existing_node = fwy_scenario.scenario.nodes.get(fwy().end_node_id);
        Node new_node = new Node(fwy_scenario.new_node_id());

        // connect downstream links to new node
        connect_segment_to_upstream_node(get_dnstrm_fwy_segment(),new_node);

        // create new freeway link
        Link new_link = new Link(
                fwy_scenario.new_link_id(),
                Link.Type.freeway,
                existing_node.id,
                new_node.id,
                get_mixed_lanes(),
                get_length_meters(),
                get_capacity_vphpl(),
                get_jam_density_vpkpl(),
                get_freespeed_kph(),null);

        // connect new link to start and end nodes
        existing_node.out_links.add(new_link.id);
        new_node.in_links.add(new_link.id);

        // add link to scenario
        fwy_scenario.scenario.links.put(new_link.id,new_link);

        // create new segment
        Segment newseg = create_new_segment(new_link);
        newseg.segment_fwy_dn_id = this.segment_fwy_dn_id;
        this.segment_fwy_dn_id = newseg.id;

        // add to fwy scenario
        fwy_scenario.segments.put(newseg.id,newseg);

        return newseg;
    }

    public Segment insert_dnstrm_offramp_segment(){

        if (!has_offramp())
            return null;

        Link fr = fr();

        // existing node and new node
        Node existing_node = fwy_scenario.scenario.nodes.get(fr.start_node_id);
        Node new_node = new Node(fwy_scenario.new_node_id());

        // connect upstream links to new node
        connect_segment_to_upstream_node(get_dnstrm_fr_segment(),new_node);

        // create new freeway link
        Link new_link = new Link(
                fwy_scenario.new_link_id(),
                Link.Type.connector,
                existing_node.id,
                new_node.id,
                get_mixed_lanes(),
                get_length_meters(),
                get_capacity_vphpl(),
                get_jam_density_vpkpl(),
                get_freespeed_kph(),null);

        // connect new link to start and end nodes
        existing_node.out_links.add(new_link.id);
        new_node.in_links.add(new_link.id);

        // add link to scenario
        fwy_scenario.scenario.links.put(new_link.id,new_link);

        // create new segment
        Segment newseg = create_new_segment(new_link);
        newseg.segment_fwy_up_id = this.id;
        newseg.segment_fwy_dn_id = this.segment_fr_dn_id;
        this.segment_fr_dn_id = newseg.id;

        // add to fwy scenario
        fwy_scenario.segments.put(newseg.id,newseg);

        return newseg;
    }

    /////////////////////////////////////
    // connect
    /////////////////////////////////////

//    public boolean connect_dnstrm_onramp(Link onramp) throws Exception {
//
//        // checks
//        if(!fwy().type.equals(Link.Type.connector))
//            throw new Exception("This method can only be called on connector segments");
//        if(segment_fwy_dn_id!=null)
//            throw new Exception("This segment is already connected downstream");
//
//
//        Segment dnstrm_segment = onramp.mysegment;
//        if(dnstrm_segment.fwy().type.equals(Link.Type.connector))
//            throw new Exception("The downstream segment cannot be a connector");
//        if(!onramp.type.equals(Link.Type.onramp))
//            throw new Exception("The provided link is not an onramp");
//        if(!dnstrm_segment.or_demands.isEmpty())
//            throw new Exception("Please delete onramp demands before calling this method");
//        if(dnstrm_segment.segment_or_up_id!=null)
//            throw new Exception("The onramp segment is already connected upstream");
//
//
//
//        Node node = fwy_scenario.scenario.nodes.get(onramp.start_node_id);
//
//        if(!node.in_links.isEmpty())
//            throw new Exception("The onramp already has an upstream connection");
//
//
//
//        return false;
//    }
//
//    public boolean connect_upstream_offramp(Link offramp){
//
//    }




    /////////////////////////////////////
    // network getter
    /////////////////////////////////////

    /** Returns a list with exactly three items.
     * 0: the onramp or null
     * 1: the freeway
     * 2: the offramp or null
     * @return
     */
    public List<Link> get_links(){
        List<Link> x = new ArrayList<>();
        x.add(or());
        x.add(fwy());
        x.add(fr());
        return x;
    }

    public Segment get_upstrm_fwy_segment(){
        return segment_fwy_up_id ==null ? null : fwy_scenario.segments.get(segment_fwy_up_id);
    }

    public Segment get_upstrm_or_segment(){
        return segment_or_up_id==null ? null : fwy_scenario.segments.get(segment_or_up_id);
    }

    public Segment get_dnstrm_fwy_segment(){
        return segment_fwy_dn_id ==null ? null : fwy_scenario.segments.get(segment_fwy_dn_id);
    }

    public Segment get_dnstrm_fr_segment(){
        return segment_fr_dn_id==null ? null : fwy_scenario.segments.get(segment_fr_dn_id);
    }

    /**
     * Get the segments that are immediately upstream from this one
     * @return Set<Segment>
     */
    public Set<Segment> get_upstrm_segments(){
        Set<Segment> x = new HashSet<>();
        if (segment_fwy_up_id !=null)
            x.add(fwy_scenario.segments.get(segment_fwy_up_id));
        if (segment_or_up_id!=null)
            x.add(fwy_scenario.segments.get(segment_or_up_id));
        return x;
    }

    /**
     * Get the links that are immediately upstream from this one
     * @return Set<Link>
     */
    public Set<Link> get_upstrm_links(){
        Link fwy = fwy();
        Set<Link> x = new HashSet<>();
        for (Segment seg : get_upstrm_segments()){
            for (Link link : seg.get_links()){
                if (link==null)
                    continue;
                if (link.end_node_id==fwy.start_node_id )
                    x.add(link);
                if( has_onramp() && link.end_node_id==or().start_node_id)
                    x.add(link);
            }
        }
        return x;
    }

    /**
     * Get the segments that are immediately downstream from this one
     * @return Set<Segment>
     */
    public Set<Segment> get_dnstrm_segments() {
        Set<Segment> x = new HashSet<>();
        if(segment_fwy_dn_id !=null)
            x.add(fwy_scenario.segments.get(segment_fwy_dn_id));
        if(segment_fr_dn_id!=null)
            x.add(fwy_scenario.segments.get(segment_fr_dn_id));
        return x;
    }

    /**
     * Get the links that are immediately downstream from this one
     * @return Set<Link>
     */
    public Set<Link> get_dnstrm_links(){
        Link fwy = fwy();
        Set<Link> x = new HashSet<>();
        for (Segment seg : get_dnstrm_segments()){
            for (Link link : seg.get_links()){
                if (link==null)
                    continue;
                if (link.start_node_id==fwy.end_node_id )
                    x.add(link);
                if( has_offramp() && link.start_node_id==fr().end_node_id)
                    x.add(link);
            }
        }
        return x;
    }

    /////////////////////////////////////
    // offramp
    /////////////////////////////////////

    public boolean has_offramp(){
        return fr_id!=null;
    }

    public float get_fr_length_meters(){
        return has_offramp() ? fr().length_meters : Float.NaN;
    }

    public String get_fr_name(){
        return has_offramp() ? fr().name : null;
    }

    public int get_fr_lanes(){
        return has_offramp() ? fr().full_lanes : 0;
    }

    public float get_fr_capacity_vphpl(){
        return has_offramp() ? fr().param.capacity_vphpl : Float.NaN;
    }

    public double get_fr_max_vehicles(){
        if(has_offramp()){
            Link fr = fr();
            return fr.param.jam_density_vpkpl * fr.full_lanes * fr.length_meters / 1000f;
        }
        else return 0d;
    }

    public void set_fr_length_meters(float x){
        if( has_offramp() )
            fr().length_meters = x;
    }

    public void set_fr_name(String newname) {
        if(has_offramp())
            fr().name = newname;
    }

    public void set_fr_lanes(int x) throws Exception {
        if (x<=0)
            throw new Exception("Invalid number of lanes");
        if(!has_offramp())
            throw new Exception("No offramp");
        fr().full_lanes = x;
    }

    public void set_fr_capacity_vphpl(float x) throws Exception {
        if (x<=0)
            throw new Exception("Invalid capacity");
        if(!has_offramp())
            throw new Exception("No offramp");
        fr().param.capacity_vphpl = x;
    }

    public void set_fr_max_vehicles(float x) throws Exception {
        if (x<=0)
            throw new Exception("Invalid max vehicles");
        if(!has_offramp())
            throw new Exception("No offramp");
        Link fr = fr();
        fr.param.jam_density_vpkpl = x / (fr.length_meters /1000f) / fr.full_lanes;
    }

    /**
     * Delete the offramp from this segment
     * @return success value
     */
    public boolean delete_offramp(){
        if(!has_offramp() || segment_fr_dn_id!=null)
            return false;
        Link fr = fr();
        fwy_scenario.scenario.nodes.remove(fr.end_node_id);
        fwy_scenario.scenario.links.remove(fr.id);
        if(fwy_scenario.scenario.nodes.containsKey(fr.start_node_id)){
            Node start_node = fwy_scenario.scenario.nodes.get(fr.start_node_id);
            start_node.out_links.remove(fr.id);
        }
        fr_id = null;
        fr_splits = new HashMap<>();
        return true;
    }

    public void add_offramp(LinkParameters params){
        if(has_offramp())
            return;
        long id = fwy_scenario.new_link_id();
        Link fwy = fwy();
        long start_node_id = fwy.start_node_id;
        Node end_node = new Node(fwy_scenario.new_node_id());
        long end_node_id = end_node.id;
        int full_lanes = 1;
        float length = 100f;

        Link fr = new Link(id,Link.Type.offramp,start_node_id,end_node_id,full_lanes,length,
                params.capacity_vphpl,
                params.jam_density_vpkpl,
                params.ff_speed_kph,
                this);

        fr_id = fr.id;
        fr.mysegment = this;
        end_node.in_links.add(fr_id);
        fwy_scenario.scenario.nodes.put(end_node.id,end_node);
        fwy_scenario.scenario.links.put(fr.id,fr);
    }

    /////////////////////////////////////
    // onramp
    /////////////////////////////////////

    public boolean has_onramp(){
        return or_id!=null;
    }

    public float get_or_length_meters() {
        return has_onramp() ? or().length_meters : Float.NaN;
    }

    public String get_or_name() {
        return has_onramp() ? or().name : null;
    }

    public int get_or_lanes() {
        return has_onramp() ? or().full_lanes : 0;
    }

    public float get_or_capacity_vphpl() {
        return has_onramp() ? or().param.capacity_vphpl : Float.NaN;
    }

    public double get_or_max_vehicles() {
        if(!has_onramp())
            return Float.NaN;
        Link or = or();
        return or.param.jam_density_vpkpl * or.full_lanes * or.length_meters / 1000f;
    }

    public void set_or_length_meters(float x) throws Exception {
        if(!has_onramp())
            throw new Exception("No onramp");
        or().length_meters = x;
    }

    public void set_or_name(String newname) throws Exception {
        if(!has_onramp())
            throw new Exception("No onramp");
        or().name = newname;
    }

    public void set_or_lanes(int x) throws Exception {
        if(!has_onramp())
            throw new Exception("No onramp");
        or().full_lanes = x;
    }

    public void set_or_capacity_vphpl(float x) throws Exception {
        if(!has_onramp())
            throw new Exception("No onramp");
        or().param.capacity_vphpl = x;
    }

    public void set_or_max_vehicles(float x) throws Exception {
        if(!has_onramp())
            throw new Exception("No onramp");
        Link or = or();
        or.param.jam_density_vpkpl = x / (or.length_meters /1000f) / or.full_lanes;
    }

    /**
     * Delete the onramp from this segment
     * @return success value
     */
    public boolean delete_onramp(){
        if(!has_onramp() || segment_or_up_id!=null)
            return false;
        Link or = or();
        fwy_scenario.scenario.nodes.remove(or.start_node_id);
        fwy_scenario.scenario.links.remove(or.id);
        if(fwy_scenario.scenario.nodes.containsKey(or.end_node_id)) {
            Node end_node = fwy_scenario.scenario.nodes.get(or.end_node_id);
            end_node.in_links.remove(or.id);
        }
        or_id = null;
        or_demands = new HashMap<>();
        return true;
    }

    /**
     * Add an onramp if there is none.
     */
    public void add_onramp(LinkParameters params){
        if(has_onramp())
            return;
        long id = fwy_scenario.new_link_id();
        Node start_node = new Node(fwy_scenario.new_node_id());
        long start_node_id = start_node.id;
        long end_node_id = fwy().end_node_id;
        int full_lanes = 1;
        float length = 100f;
        Link or = new Link(id,Link.Type.onramp,start_node_id,end_node_id,full_lanes,length,
                params.capacity_vphpl,
                params.jam_density_vpkpl,
                params.ff_speed_kph,this);
        or_id = or.id;
        or.mysegment = this;
        start_node.out_links.add(or_id);
        fwy_scenario.scenario.nodes.put(start_node.id,start_node);
        fwy_scenario.scenario.links.put(or.id,or);
    }

    /////////////////////////////////////
    // freeway
    /////////////////////////////////////

    public String get_fwy_name(){
        return fwy().name;
    }

    public int get_mixed_lanes(){
        return fwy().full_lanes;
    }

    public int get_managed_lanes(){
        System.out.println("NOT IMPLEMENTED!");
        return 0;
    }

    public float get_capacity_vphpl(){
        return fwy().param.capacity_vphpl;
    }

    public float get_jam_density_vpkpl(){
        return fwy().param.jam_density_vpkpl;
    }

    public float get_freespeed_kph(){
        return fwy().param.ff_speed_kph;
    }

    public void set_fwy_name(String newname) {
        fwy().name = newname;
    }

    public void set_mixed_lanes(int x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive number of lanes");
        fwy().full_lanes = x;
    }

    public void set_managed_lanes(int x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive number of lanes");
        System.out.println("NOT IMPLEMENTED!");
    }

    public void set_capacity_vphpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive capacity");
        fwy().param.capacity_vphpl = x;
    }

    public void set_jam_density_vpkpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive jam density");
        fwy().param.jam_density_vpkpl = x;
    }

    public void set_freespeed_kph(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive free speed");
        fwy().param.ff_speed_kph = x;
    }

    /////////////////////////////////////
    // demands
    /////////////////////////////////////

    /**
     * Get the freeway demand for this segment, for a particular commodity.
     * @param comm_id ID for the commodity
     * @return Profile1D object if demand is defined for this commodity. null otherwise.
     */
    public Profile1D get_fwy_demand_vph(long comm_id){
        return fwy_demands.containsKey(comm_id) ? fwy_demands.get(comm_id) : null;
    }

    /**
     * Set the freeway demand in vehicles per hour.
     * @param comm_id ID for the commodity
     * @param demand_vph Demand in veh/hr as a Profile1D object
     */
    public void set_fwy_demand_vph(long comm_id, Profile1D demand_vph)throws Exception {
        OTMErrorLog errorLog = new OTMErrorLog();
        demand_vph.validate(errorLog);
        if (errorLog.haserror())
            throw new Exception(errorLog.format_errors());
        this.fwy_demands.put(comm_id,demand_vph);
    }

    /**
     * Get the onramp demand for this segment, for a particular commodity.
     * @param comm_id ID for the commodity
     * @return Profile1D object if demand is defined for this commodity. null otherwise.
     */
    public Profile1D get_or_demand_vph(long comm_id){
        return or_demands.containsKey(comm_id) ? or_demands.get(comm_id) : null;
    }

    /**
     * Set the onramp demand in vehicles per hour.
     * @param comm_id ID for the commodity
     * @param demand_vph Demand in veh/hr as a Profile1D object
     */
    public void set_or_demand_vph(long comm_id,Profile1D demand_vph)throws Exception {
        OTMErrorLog errorLog = new OTMErrorLog();
        demand_vph.validate(errorLog);
        if (errorLog.haserror())
            throw new Exception(errorLog.format_errors());
        this.or_demands.put(comm_id,demand_vph);
    }

    /////////////////////////////////////
    // splits
    /////////////////////////////////////

    /**
     * Get the offramp split ratio for this segment, for a particular commodity.
     * @param comm_id ID for the commodity
     * @return Profile1D object if split is defined for this commodity. null otherwise.
     */
    public Profile1D get_fr_split(long comm_id){
        return fr_splits.containsKey(comm_id) ? fr_splits.get(comm_id) : null;
    }

    /**
     * Set the offramp split ratio for this segment, for a particular commodity.
     * @param comm_id ID for the commodity
     * @param splits DSplit ratio as a Profile1D object
     */
    public void set_fr_split(long comm_id,Profile1D splits)throws Exception {
        OTMErrorLog errorLog = new OTMErrorLog();
        splits.validate(errorLog);
        if (Collections.max(splits.values) > 1.0D)
            errorLog.addError("Collections.max(values)>1");
        if (errorLog.haserror())
            throw new Exception(errorLog.format_errors());
        this.fr_splits.put(comm_id,splits);
    }

    /////////////////////////////////////
    // protected and private
    /////////////////////////////////////

    protected Link fwy(){
        return fwy_scenario.get_link(fwy_id);
    }

    protected Link or(){
        return has_onramp() ? fwy_scenario.get_link(or_id) : null;
    }

    protected Link fr(){
        return has_offramp() ? fwy_scenario.get_link(fr_id) : null;
    }

    protected void set_start_node(long new_start_node){
        fwy().start_node_id = new_start_node;
        if(has_offramp())
            fr().start_node_id = new_start_node;
    }

    protected void set_end_node(long new_end_node){
        fwy().end_node_id = new_end_node;
        if(has_onramp())
            or().end_node_id = new_end_node;
    }

    private static void connect_segment_to_downstream_node(Segment segment, Node new_dwn_node){

        if(segment==null)
            return;

        Node old_dwn_node = segment.fwy_scenario.scenario.nodes.get(segment.fwy().end_node_id);
        segment.fwy().end_node_id = new_dwn_node.id;
        old_dwn_node.in_links.remove(segment.fwy_id);
        new_dwn_node.in_links.add(segment.fwy_id);

        if(segment.has_onramp() && segment.or().end_node_id==old_dwn_node.id) {
            segment.or().end_node_id = new_dwn_node.id;
            old_dwn_node.in_links.remove(segment.or_id);
            new_dwn_node.in_links.add(segment.or_id);
        }

        if(segment.has_offramp() && segment.fr().start_node_id==old_dwn_node.id) {
            segment.fr().start_node_id = new_dwn_node.id;
            old_dwn_node.out_links.remove(segment.fr_id);
            new_dwn_node.out_links.add(segment.fr_id);
        }
    }

    private static void connect_segment_to_upstream_node(Segment segment, Node new_up_node){

        if(segment==null)
            return;

        Node old_up_node = segment.fwy_scenario.scenario.nodes.get(segment.fwy().start_node_id);
        segment.fwy().start_node_id = new_up_node.id;
        old_up_node.out_links.remove(segment.fwy_id);
        new_up_node.out_links.add(segment.fwy_id);

        if(segment.has_onramp() && segment.or().end_node_id==old_up_node.id) {
            segment.or().end_node_id = new_up_node.id;
            old_up_node.out_links.remove(segment.or_id);
            new_up_node.out_links.add(segment.or_id);
        }

        if(segment.has_offramp() && segment.fr().start_node_id==old_up_node.id) {
            segment.fr().start_node_id = new_up_node.id;
            old_up_node.in_links.remove(segment.fr_id);
            new_up_node.in_links.add(segment.fr_id);
        }
    }

    private Segment create_new_segment(Link newfwy){
        Long new_seg_id = fwy_scenario.new_seg_id();
        String new_seg_name = String.format("segment %d",new_seg_id);
        Segment newseg = new Segment(fwy_scenario,new_seg_id,new_seg_name,newfwy.id);
        newfwy.mysegment = newseg;
        newseg.segment_fwy_dn_id = this.id;
        return newseg;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    public jaxbopt.Sgmt to_jaxb(){
        jaxbopt.Sgmt sgmt = new jaxbopt.Sgmt();
        sgmt.setName(name);
        sgmt.setLinks(OTMUtils.comma_format(
                get_links().stream()
                        .filter(x->x!=null)
                        .map(link->link.get_id())
                        .collect(Collectors.toSet())));
        return sgmt;
    }

    @Override
    public String toString() {
        return String.format("name\t%s\nfr\t%s\nfwy:\t%s\nor:\t%s", name, fr_id, fwy_id, or_id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Segment segment = (Segment) o;
        return id == segment.id &&
                name.equals(segment.name) &&
                fwy_id.equals(segment.fwy_id) &&
                Objects.equals(or_id, segment.or_id) &&
                Objects.equals(fr_id, segment.fr_id) &&
                Objects.equals(segment_fwy_dn_id, segment.segment_fwy_dn_id) &&
                Objects.equals(segment_fwy_up_id, segment.segment_fwy_up_id) &&
                Objects.equals(segment_fr_dn_id, segment.segment_fr_dn_id) &&
                Objects.equals(segment_or_up_id, segment.segment_or_up_id) &&
                fwy_demands.equals(segment.fwy_demands) &&
                or_demands.equals(segment.or_demands) &&
                fr_splits.equals(segment.fr_splits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, fwy_id, or_id, fr_id, segment_fwy_dn_id, segment_fwy_up_id, segment_fr_dn_id, segment_or_up_id, fwy_demands, or_demands, fr_splits);
    }
}
