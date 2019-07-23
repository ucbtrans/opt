package opt.data;

import error.OTMErrorLog;
import profiles.Profile1D;
import utils.OTMUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Segment {

    ///////////////////////////
    private final static float default_or_capacity_vphpl = 1500f;
    private final static float default_or_jam_density_vpkpl = 100f;
    private final static float default_or_ff_speed_kph = 40f;
    private final static float default_fr_capacity_vphpl = 1500f;
    private final static float default_fr_jam_density_vpkpl = 100f;
    private final static float default_fr_ff_speed_kph = 40f;
    ///////////////////////////

    protected FreewayScenario fwy_scenario;

    protected String name;
    protected final long id;
    protected Long ml_id = null;
    protected Long or_id = null;
    protected Long fr_id = null;

    // maps to adjacent segments
    protected Long segment_ml_dn_id;
    protected Long segment_ml_up_id;
    protected Long segment_fr_dn_id;
    protected Long segment_or_up_id;

    protected Map<Long,Profile1D> ml_demands = new HashMap<>();     // commodity -> Profile1D
    protected Map<Long,Profile1D> or_demands = new HashMap<>();     // commodity -> Profile1D
    protected Map<Long,Profile1D> fr_splits = new HashMap<>();      // commodity -> Profile1D

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    // used in deep copy
    public Segment(long id){
        this.id = id;
    }

    // used by FreewayScenario jaxb constructor
    public Segment(FreewayScenario fwy_scenario,long id, jaxb.Sgmt sgmt) throws Exception {

        this.id = id;
        this.fwy_scenario = fwy_scenario;
        this.name = sgmt.getName();

        List<Long> link_ids = OTMUtils.csv2longlist(sgmt.getLinks());
        List<AbstractLink> links = link_ids.stream()
                .map(link_id->fwy_scenario.scenario.links.get(link_id))
                .collect(Collectors.toList());

        // find mainline link
        Set<AbstractLink> ml_links = links.stream()
                .filter(link->link instanceof LinkMainline || link instanceof LinkConnector)
                .collect(Collectors.toSet());

        // check exactly one mailine link
        if( ml_links.size()!=1 )
            throw new Exception("All segments must contain exactly one mainline link");

        AbstractLink ml = ml_links.iterator().next();
        ml_id = ml.id;
        links.remove(ml);

        // check that what is remaining are at most two and all ramps
        if (links.size()>2)
            throw new Exception("Segment has too many links");
        if (!links.stream().allMatch(link->link instanceof LinkRamp))
            throw new Exception("Links in a segment must be either mainline or ramp");

        // find onramps
        Set<AbstractLink> ors = links.stream()
                .filter(link-> link instanceof LinkRamp)
                .filter(link-> link.end_node_id==ml.start_node_id || link.end_node_id==ml.end_node_id)
                .collect(Collectors.toSet());

        // at most one onramp
        if (!ors.isEmpty()){
            if(ors.size()>1)
                throw new Exception("At most one onramp per segment");
            AbstractLink or = ors.iterator().next();
            or_id = or.id;
            links.remove(or);
        }

        // find offramps
        Set<AbstractLink> frs = links.stream()
                .filter(link-> link instanceof LinkRamp)
                .filter(link-> link.start_node_id==ml.start_node_id || link.start_node_id==ml.end_node_id)
                .collect(Collectors.toSet());

        // at most one offramp
        if (!frs.isEmpty()){
            if(frs.size()>1)
                throw new Exception("At most one offramp per segment");
            AbstractLink fr = frs.iterator().next();
            fr_id = fr.id;
            links.remove(fr);
        }

        // there should be nothing left over
        assert(links.isEmpty());
    }

    // used by Segment.create_new_segment
    public Segment(FreewayScenario fwy_scenario,long id,String name,Long ml_id){
        this.fwy_scenario = fwy_scenario;
        this.id = id;
        this.name = name;
        this.ml_id = ml_id;
    }

    public Segment deep_copy(FreewayScenario scenario){
        Segment seg_cpy = new Segment(id);
        seg_cpy.name = name;
        seg_cpy.fwy_scenario = scenario;
        seg_cpy.ml_id = ml_id;
        seg_cpy.or_id = or_id;
        seg_cpy.fr_id = fr_id;
        seg_cpy.segment_ml_dn_id = segment_ml_dn_id;
        seg_cpy.segment_ml_up_id = segment_ml_up_id;
        seg_cpy.segment_fr_dn_id = segment_fr_dn_id;
        seg_cpy.segment_or_up_id = segment_or_up_id;

        for(Map.Entry<Long,Profile1D> e : ml_demands.entrySet())
            seg_cpy.ml_demands.put(e.getKey(),e.getValue().clone());

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
        return ml().length_meters;
    }

    /**
     * Set the length of this segment in meters
     * @param newlength
     */
    public void set_length_meters(float newlength) throws Exception {
        if (newlength<=0.0001)
            throw new Exception("Attempted to set a non-positive segment length");
        ml().length_meters = newlength;
    }

    /////////////////////////////////////
    // network
    /////////////////////////////////////

    public Set<AbstractLink> get_links(){
        Set<AbstractLink> x = new HashSet<>();
        x.add(ml());
        if(or_id!=null)
            x.add(or());
        if(fr_id!=null)
            x.add(fr());
        return x;
    }

    public Segment get_upstrm_ml_segment(){
        return segment_ml_up_id==null ? null : fwy_scenario.segments.get(segment_ml_up_id);
    }

    public Segment get_upstrm_or_segment(){
        return segment_or_up_id==null ? null : fwy_scenario.segments.get(segment_or_up_id);
    }

    public Segment get_dnstrm_ml_segment(){
        return segment_ml_dn_id==null ? null : fwy_scenario.segments.get(segment_ml_dn_id);
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
        if (segment_ml_up_id!=null)
            x.add(fwy_scenario.segments.get(segment_ml_up_id));
        if (segment_or_up_id!=null)
            x.add(fwy_scenario.segments.get(segment_or_up_id));
        return x;
    }

    /**
     * Get the links that are immediately upstream from this one
     * @return Set<Link>
     */
    public Set<AbstractLink> get_upstrm_links(){
        AbstractLink ml = ml();
        Set<AbstractLink> x = new HashSet<>();
        for (Segment seg : get_upstrm_segments()){
            for (AbstractLink link : seg.get_links()){
                if (link.end_node_id==ml.start_node_id )
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
        if(segment_ml_dn_id!=null)
            x.add(fwy_scenario.segments.get(segment_ml_dn_id));
        if(segment_fr_dn_id!=null)
            x.add(fwy_scenario.segments.get(segment_fr_dn_id));
        return x;
    }

    /**
     * Get the links that are immediately downstream from this one
     * @return Set<Link>
     */
    public Set<AbstractLink> get_dnstrm_links(){
        AbstractLink ml = ml();
        Set<AbstractLink> x = new HashSet<>();
        for (Segment seg : get_dnstrm_segments()){
            for (AbstractLink link : seg.get_links()){
                if (link.start_node_id==ml.end_node_id )
                    x.add(link);
                if( has_offramp() && link.start_node_id==fr().end_node_id)
                    x.add(link);
            }
        }
        return x;
    }

    public Segment insert_upstrm_hov_segment(){
        System.err.println("NOT IMPLEMENTED!!!");
        return null;
    }

    public Segment insert_upstrm_mainline_segment(){

        // create new upstream node
        Node existing_node = fwy_scenario.scenario.nodes.get(ml().start_node_id);
        Node new_node = new Node(fwy_scenario.new_node_id());

        // connect upstream links to new node
        connect_segment_to_downstream_node(get_upstrm_ml_segment(),new_node);

        // create new mainline link
        LinkMainline new_link = new LinkMainline(
                fwy_scenario.new_link_id(),
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
        newseg.segment_ml_dn_id = this.id;
        newseg.segment_ml_up_id = this.segment_ml_up_id;
        this.segment_ml_up_id = newseg.id;

        // add to fwy scenario
        fwy_scenario.segments.put(newseg.id,newseg);

        return newseg;
    }

    public Segment insert_upstrm_onramp_segment() {

        if (!has_onramp())
            return null;

        LinkRamp or = or();

        // existing node and new node
        Node existing_node = fwy_scenario.scenario.nodes.get(or.end_node_id);
        Node new_node = new Node(fwy_scenario.new_node_id());

        // connect upstream links to new node
        connect_segment_to_downstream_node(get_upstrm_or_segment(),new_node);

        // create new mainline link
        LinkConnector new_link = new LinkConnector(
                fwy_scenario.new_link_id(),
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
        newseg.segment_ml_dn_id = this.id;
        newseg.segment_ml_up_id = this.segment_or_up_id;
        this.segment_or_up_id = newseg.id;

        // add to fwy scenario
        fwy_scenario.segments.put(newseg.id,newseg);

        return newseg;
    }

    public Segment insert_dnstrm_hov_segment(){
        System.err.println("NOT IMPLEMENTED!!!");
        return null;
    }

    public Segment insert_dnstrm_mainline_segment(){

        // existing node and new node
        Node existing_node = fwy_scenario.scenario.nodes.get(ml().end_node_id);
        Node new_node = new Node(fwy_scenario.new_node_id());

        // connect downstream links to new node
        connect_segment_to_upstream_node(get_dnstrm_ml_segment(),new_node);

        // create new mainline link
        LinkMainline new_link = new LinkMainline(
                fwy_scenario.new_link_id(),
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
        newseg.segment_ml_dn_id = this.segment_ml_dn_id;
        this.segment_ml_dn_id = newseg.id;

        // add to fwy scenario
        fwy_scenario.segments.put(newseg.id,newseg);

        return newseg;
    }

    public Segment insert_dnstrm_offramp_segment(){

        if (!has_offramp())
            return null;

        LinkRamp fr = fr();

        // existing node and new node
        Node existing_node = fwy_scenario.scenario.nodes.get(fr.start_node_id);
        Node new_node = new Node(fwy_scenario.new_node_id());

        // connect upstream links to new node
        connect_segment_to_upstream_node(get_dnstrm_fr_segment(),new_node);

        // create new mainline link
        LinkConnector new_link = new LinkConnector(
                fwy_scenario.new_link_id(),
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
        newseg.segment_ml_up_id = this.id;
        newseg.segment_ml_dn_id = this.segment_fr_dn_id;
        this.segment_fr_dn_id = newseg.id;

        // add to fwy scenario
        fwy_scenario.segments.put(newseg.id,newseg);

        return newseg;
    }

    /////////////////////////////////////
    // offramp
    /////////////////////////////////////

    public boolean has_offramp(){
        return fr_id!=null;
    }

    public String get_fr_name(){
        return has_offramp() ? fr().name : null;
    }

    public int get_fr_lanes(){
        return has_offramp() ? fr().full_lanes : 0;
    }

    public float get_fr_capacity_vphpl(){
        return has_offramp() ? fr().capacity_vphpl : Float.NaN;
    }

    public double get_fr_max_vehicles(){
        if(has_offramp()){
            AbstractLink fr = fr();
            return fr.jam_density_vpkpl * fr.full_lanes * fr.length_meters / 1000f;
        }
        else return 0d;
    }

    public void set_fr_name(String newname) {
        if(has_offramp())
            fr().name = newname;
    }

    public void set_fr_lanes(int x) throws Exception {
        if (x<=0)
            throw new Exception("Invalid number of lanes");
        if(!has_offramp())
            add_offramp();
        fr().full_lanes = x;
    }

    public void set_fr_capacity_vphpl(float x) throws Exception {
        if (x<=0)
            throw new Exception("Invalid capacity");
        if(!has_offramp())
            add_offramp();
        fr().capacity_vphpl = x;
    }

    public void set_fr_max_vehicles(float x) throws Exception {
        if (x<=0)
            throw new Exception("Invalid max vehicles");
        if(!has_offramp())
            add_offramp();
        AbstractLink fr = fr();
        fr.jam_density_vpkpl = x / (fr.length_meters /1000f) / fr.full_lanes;
    }

    /**
     * Delete the offramp from this segment
     * @return success value
     */
    public boolean delete_offramp(){
        if(!has_offramp() || segment_fr_dn_id!=null)
            return false;
        AbstractLink fr = fr();
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

    public void add_offramp(){
        if(has_offramp())
            return;
        long id = fwy_scenario.new_link_id();
        AbstractLink ml = ml();
        long start_node_id = ml.start_node_id;
        Node end_node = new Node(fwy_scenario.new_node_id());
        long end_node_id = end_node.id;
        int full_lanes = 1;
        float length = 100f;
        float capacity_vphpl = default_fr_capacity_vphpl;
        float jam_density_vpkpl = default_fr_jam_density_vpkpl;
        float ff_speed_kph = default_fr_ff_speed_kph;

        LinkRamp fr = new LinkRamp(id,start_node_id,end_node_id,full_lanes,length,capacity_vphpl, jam_density_vpkpl,ff_speed_kph,this);
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

    public String get_or_name(){
        return has_onramp() ? or().name : null;
    }

    public int get_or_lanes(){
        return has_onramp() ? or().full_lanes : 0;
    }

    public float get_or_capacity_vphpl(){
        return has_onramp() ? or().capacity_vphpl: Float.NaN;
    }

    public double get_or_max_vehicles(){
        if (has_onramp()){
            AbstractLink or = or();
            return or.jam_density_vpkpl * or.full_lanes * or.length_meters / 1000f;
        }
        else return 0d;
    }

    public void set_or_name(String newname) {
        if(has_onramp())
            or().name = newname;
    }

    public void set_or_lanes(int x){
        if(!has_onramp())
            add_onramp();
        or().full_lanes = x;
    }

    public void set_or_capacity_vphpl(float x){
        if(!has_onramp())
            add_onramp();
        or().capacity_vphpl = x;
    }

    public void set_or_max_vehicles(float x){
        if(!has_onramp())
            add_onramp();
        AbstractLink or = or();
        or.jam_density_vpkpl = x / (or.length_meters /1000f) / or.full_lanes;
    }

    /**
     * Delete the onramp from this segment
     * @return success value
     */
    public boolean delete_onramp(){
        if(!has_onramp() || segment_or_up_id!=null)
            return false;
        AbstractLink or = or();
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
    public void add_onramp(){
        if(has_onramp())
            return;
        long id = fwy_scenario.new_link_id();
        Node start_node = new Node(fwy_scenario.new_node_id());
        long start_node_id = start_node.id;
        long end_node_id = ml().end_node_id;
        int full_lanes = 1;
        float length = 100f;
        float capacity_vphpl = default_or_capacity_vphpl;
        float jam_density_vpkpl = default_or_jam_density_vpkpl;
        float ff_speed_kph = default_or_ff_speed_kph;
        AbstractLink or = new LinkRamp(id,start_node_id,end_node_id,full_lanes,length,capacity_vphpl, jam_density_vpkpl,ff_speed_kph,this);
        or_id = or.id;
        or.mysegment = this;
        start_node.out_links.add(or_id);
        fwy_scenario.scenario.nodes.put(start_node.id,start_node);
        fwy_scenario.scenario.links.put(or.id,or);
    }

    /////////////////////////////////////
    // mainline
    /////////////////////////////////////

    public String get_ml_name(){
        return ml().name;
    }

    public int get_mixed_lanes(){
        return ml().full_lanes;
    }

    public int get_hov_lanes(){
        System.out.println("NOT IMPLEMENTED!");
        return 0;
    }

    public float get_capacity_vphpl(){
        return ml().capacity_vphpl;
    }

    public float get_jam_density_vpkpl(){
        return ml().jam_density_vpkpl;
    }

    public float get_freespeed_kph(){
        return ml().ff_speed_kph;
    }

    public void set_ml_name(String newname) {
        ml().name = newname;
    }

    public void set_mixed_lanes(int x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive number of lanes");
        ml().full_lanes = x;
    }

    public void set_hov_lanes(int x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive number of lanes");
        System.out.println("NOT IMPLEMENTED!");
    }

    public void set_capacity_vphpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive capacity");
        ml().capacity_vphpl = x;
    }

    public void set_jam_density_vpkpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive jam density");
        ml().jam_density_vpkpl = x;
    }

    public void set_freespeed_kph(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive free speed");
        ml().ff_speed_kph = x;
    }

    /////////////////////////////////////
    // demands
    /////////////////////////////////////

    /**
     * Get the mainline demand for this segment, for a particular commodity.
     * @param comm_id ID for the commodity
     * @return Profile1D object if demand is defined for this commodity. null otherwise.
     */
    public Profile1D get_ml_demand_vph(long comm_id){
        return ml_demands.containsKey(comm_id) ? ml_demands.get(comm_id) : null;
    }

    /**
     * Set the mainline demand in vehicles per hour.
     * @param comm_id ID for the commodity
     * @param demand_vph Demand in veh/hr as a Profile1D object
     */
    public void set_ml_demand_vph(long comm_id,Profile1D demand_vph)throws Exception {
        OTMErrorLog errorLog = new OTMErrorLog();
        demand_vph.validate(errorLog);
        if (errorLog.haserror())
            throw new Exception(errorLog.format_errors());
        this.ml_demands.put(comm_id,demand_vph);
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

    protected AbstractLink ml(){
        return fwy_scenario.get_link(ml_id);
    }

    protected LinkRamp or(){
        return has_onramp() ? (LinkRamp) fwy_scenario.get_link(or_id) : null;
    }

    protected LinkRamp fr(){
        return has_offramp() ? (LinkRamp) fwy_scenario.get_link(fr_id) : null;
    }

    protected void set_start_node(long new_start_node){
        ml().start_node_id = new_start_node;
        if(has_offramp())
            fr().start_node_id = new_start_node;
    }

    protected void set_end_node(long new_end_node){
        ml().end_node_id = new_end_node;
        if(has_onramp())
            or().end_node_id = new_end_node;
    }

    private static void connect_segment_to_downstream_node(Segment segment, Node new_dwn_node){

        if(segment==null)
            return;

        Node old_dwn_node = segment.fwy_scenario.scenario.nodes.get(segment.ml().end_node_id);
        segment.ml().end_node_id = new_dwn_node.id;
        old_dwn_node.in_links.remove(segment.ml_id);
        new_dwn_node.in_links.add(segment.ml_id);

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

        Node old_up_node = segment.fwy_scenario.scenario.nodes.get(segment.ml().start_node_id);
        segment.ml().start_node_id = new_up_node.id;
        old_up_node.out_links.remove(segment.ml_id);
        new_up_node.out_links.add(segment.ml_id);

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

    private Segment create_new_segment(AbstractLink newml){
        Long new_seg_id = fwy_scenario.new_seg_id();
        String new_seg_name = String.format("segment %d",new_seg_id);
        Segment newseg = new Segment(fwy_scenario,new_seg_id,new_seg_name,newml.id);
        newml.mysegment = newseg;
        newseg.segment_ml_dn_id = this.id;
        return newseg;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    public jaxb.Sgmt to_jaxb(){
        jaxb.Sgmt sgmt = new jaxb.Sgmt();
        sgmt.setName(name);
        sgmt.setLinks(OTMUtils.comma_format(get_links().stream().map(link->link.get_id()).collect(Collectors.toSet())));
        return sgmt;
    }

    @Override
    public String toString() {
        return String.format("name\t%s\nfr\t%s\nml:\t%s\nor:\t%s", name, fr_id, ml_id, or_id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Segment segment = (Segment) o;
        return id == segment.id &&
                name.equals(segment.name) &&
                ml_id.equals(segment.ml_id) &&
                Objects.equals(or_id, segment.or_id) &&
                Objects.equals(fr_id, segment.fr_id) &&
                Objects.equals(segment_ml_dn_id, segment.segment_ml_dn_id) &&
                Objects.equals(segment_ml_up_id, segment.segment_ml_up_id) &&
                Objects.equals(segment_fr_dn_id, segment.segment_fr_dn_id) &&
                Objects.equals(segment_or_up_id, segment.segment_or_up_id) &&
                ml_demands.equals(segment.ml_demands) &&
                or_demands.equals(segment.or_demands) &&
                fr_splits.equals(segment.fr_splits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, ml_id, or_id, fr_id, segment_ml_dn_id, segment_ml_up_id, segment_fr_dn_id, segment_or_up_id, ml_demands, or_demands, fr_splits);
    }
}
