package opt.data;

import profiles.Profile1D;
import utils.OTMUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Segment {

    ///////////////////////////
    private final static float KmPerMile = 1.609344f;
    private final static float FeetPerMeter = 3.28084f;
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
    protected LinkMainline ml = null;
    protected LinkRamp or = null;
    protected LinkRamp fr = null;

    // maps to adjacent segments
    protected Segment dnstrm_segment;
    protected Segment upstrm_segment;
    protected Segment fr_connector_segment = null;     // NOT IMPLEMENTED
    protected Segment or_connector_segment = null;     // NOT IMPLEMENTED

    protected Map<Long,Profile1D> or_demands = new HashMap<>();
    protected Map<Long, Split> fr_splits = new HashMap<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Segment(long id){
        this.id = id;
    }

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
                .filter(link->link instanceof LinkMainline)
                .collect(Collectors.toSet());

        // check exactly one mailine link
        if( ml_links.size()!=1 )
            throw new Exception("All segments must contain exactly one mainline link");

        ml = (LinkMainline) ml_links.iterator().next();
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
            or = (LinkRamp) ors.iterator().next();
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
            fr = (LinkRamp) frs.iterator().next();
            links.remove(fr);
        }

        // there should be nothing left over
        assert(links.isEmpty());
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
        return ml.length_meters;
    }

    /**
     * Set the length of this segment in meters
     * @param newlength
     */
    public void set_length_meters(float newlength) throws Exception {
        if (newlength<=0.0001)
            throw new Exception("Attempted to set a non-positive segment length");
        ml.length_meters = newlength;
    }

    /////////////////////////////////////
    // network
    /////////////////////////////////////

    public Set<AbstractLink> get_links(){
        Set<AbstractLink> x = new HashSet<>();
        x.add(ml);
        if(or!=null)
            x.add(or);
        if(fr!=null)
            x.add(fr);
        return x;
    }

    /**
     * Get the segments that are immediately upstream from this one
     * @return Set<Segment>
     */
    public Set<Segment> get_upstrm_segments(){
        Set<Segment> x = new HashSet<>();
        x.add(upstrm_segment);
        return x;
    }

    /**
     * Get the links that are immediately upstream from this one
     * @return Set<Link>
     */
    public Set<AbstractLink> get_upstrm_links(){
        return get_upstrm_segments().stream()
                .flatMap(sgmt->sgmt.get_links().stream())
                .filter(link->link.end_node_id==ml.end_node_id || link.end_node_id==ml.start_node_id)
                .collect(Collectors.toSet());
    }

    /**
     * Get the segments that are immediately downstream from this one
     * @return Set<Segment>
     */
    public Set<Segment> get_dnstrm_segments() {
        Set<Segment> x = new HashSet<>();
        x.add(dnstrm_segment);
        return x;
    }

    /**
     * Get the links that are immediately downstream from this one
     * @return Set<Link>
     */
    public Set<AbstractLink> get_dnstrm_links(){
        return get_dnstrm_segments().stream()
                .flatMap(sgmt->sgmt.get_links().stream())
                .filter(link->link.start_node_id==ml.end_node_id || link.start_node_id==ml.start_node_id)
                .collect(Collectors.toSet());
    }

    public Segment insert_upstrm_hov_segment(){
        System.out.println("NOT IMPLEMENTED!!!");
        return null;
    }

    public Segment insert_upstrm_mainline_segment(){
        System.out.println("NOT IMPLEMENTED!!!");
        return null;
    }

    public Segment insert_upstrm_onramp_segment(){
        System.out.println("NOT IMPLEMENTED!!!");
        return null;
    }


    public Segment insert_dnstrm_hov_segment(){
        System.out.println("NOT IMPLEMENTED!!!");
        return null;
    }

    public Segment insert_dnstrm_mainline_segment(){
        System.out.println("NOT IMPLEMENTED!!!");
        return null;
    }

    public Segment insert_dnstrm_onramp_segment(){
        System.out.println("NOT IMPLEMENTED!!!");
        return null;
    }

    /////////////////////////////////////
    // offramp
    /////////////////////////////////////

    public boolean has_offramp(){
        return fr!=null;
    }

    public String get_fr_name(){
        return "NOT IMPLEMENTED";
    }

    public int get_fr_lanes(){
        return has_offramp() ? fr.full_lanes : 0;
    }

    public float get_fr_capacity_vphpl(){
        return has_offramp() ? fr.capacity_vphpl : Float.NaN;
    }

    public double get_fr_max_vehicles(){
        return has_offramp() ? fr.jam_density_vpkpl * fr.full_lanes * fr.length_meters / 1000f : 0d;
    }

    public void set_fr_name(String newname) {
        if(fr!=null)
            fr.name = newname;
    }

    public void set_fr_lanes(int x) throws Exception {
        if (x<=0)
            throw new Exception("Invalid number of lanes");
        if(fr==null)
            add_offramp();
        fr.full_lanes = x;
    }

    public void set_fr_capacity_vphpl(float x) throws Exception {
        if (x<=0)
            throw new Exception("Invalid capacity");
        if(fr==null)
            add_offramp();
        fr.capacity_vphpl = x;
    }

    public void set_fr_max_vehicles(float x) throws Exception {
        if (x<=0)
            throw new Exception("Invalid max vehicles");
        if(fr==null)
            add_offramp();
        fr.jam_density_vpkpl = x / (fr.length_meters /1000f) / fr.full_lanes;
    }

    /**
     * Delete the offramp from this segment
     * @return success value
     */
    public boolean delete_offramp(){
        if(fr==null)
            return false;
        fwy_scenario.scenario.nodes.remove(fr.end_node_id);
        fwy_scenario.scenario.links.remove(fr.id);
        fr = null;
        fr_splits = new HashMap<>();
        return true;
    }

    public void add_offramp(){
        if(fr!=null)
            return;
        long id = fwy_scenario.new_link_id();
        long start_node_id = ml.start_node_id;
        Node end_node = new Node(fwy_scenario.new_node_id());
        long end_node_id = end_node.id;
        int full_lanes = 1;
        float length = 100f;
        boolean is_source = false;
        float capacity_vphpl = default_fr_capacity_vphpl;
        float jam_density_vpkpl = default_fr_jam_density_vpkpl;
        float ff_speed_kph = default_fr_ff_speed_kph;

        fr = new LinkRamp(id,start_node_id,end_node_id,full_lanes,length,is_source,capacity_vphpl, jam_density_vpkpl,ff_speed_kph,this);
        fr.mysegment = this;
        end_node.in_links.add(fr);
        fwy_scenario.scenario.nodes.put(end_node.id,end_node);
        fwy_scenario.scenario.links.put(fr.id,fr);
    }

    /////////////////////////////////////
    // onramp
    /////////////////////////////////////

    public boolean has_onramp(){
        return or!=null;
    }

    public String get_or_name(){
        return "NOT IMPLEMENTED";
    }

    public int get_or_lanes(){
        return has_onramp() ? or.full_lanes : 0;
    }

    public float get_or_capacity_vphpl(){
        return has_onramp() ? or.capacity_vphpl: Float.NaN;
    }

    public double get_or_max_vehicles(){
        return has_onramp() ? or.jam_density_vpkpl * or.full_lanes * or.length_meters / 1000f : 0d;
    }

    public void set_or_name(String newname) {
        if(or!=null)
            or.name = newname;
    }

    public void set_or_lanes(int x){
        if(or==null)
            add_onramp();
        or.full_lanes = x;
    }

    public void set_or_capacity_vphpl(float x){
        if(or==null)
            add_onramp();
        or.capacity_vphpl = x;
    }

    public void set_or_max_vehicles(float x){
        if(or==null)
            add_onramp();
        or.jam_density_vpkpl = x / (or.length_meters /1000f) / or.full_lanes;
    }


    /**
     * Delete the onramp from this segment
     * @return success value
     */
    public boolean delete_onramp(){
        if(or==null)
            return false;
        fwy_scenario.scenario.nodes.remove(or.start_node_id);
        fwy_scenario.scenario.links.remove(or.id);
        or = null;
        or_demands = new HashMap<>();
        return true;
    }

    public void add_onramp(){
        if(or!=null)
            return;
        long id = fwy_scenario.new_link_id();
        Node start_node = new Node(fwy_scenario.new_node_id());
        long start_node_id = start_node.id;
        long end_node_id = ml.end_node_id;
        int full_lanes = 1;
        float length = 100f;
        boolean is_source = true;
        float capacity_vphpl = default_or_capacity_vphpl;
        float jam_density_vpkpl = default_or_jam_density_vpkpl;
        float ff_speed_kph = default_or_ff_speed_kph;
        or = new LinkRamp(id,start_node_id,end_node_id,full_lanes,length,is_source,capacity_vphpl, jam_density_vpkpl,ff_speed_kph,this);
        or.mysegment = this;
        start_node.out_links.add(or);
        fwy_scenario.scenario.nodes.put(start_node.id,start_node);
        fwy_scenario.scenario.links.put(or.id,or);
    }

    /**
     * Get the demand for this onramp, for a particular commodity, or the total.
     * @param comm_id Pass a commodity id, or null to obtain the total
     * @return An OTM Profile1D object if demand is defined for this commodity. null otherwise.
     */
    public Profile1D get_or_demand_vph(Long comm_id){

        // TODO: return total if comm_id == null

        if (or_demands.containsKey(comm_id))
            return or_demands.get(comm_id);
        else return null;
    }

    /**
     * Set the onramp demand in vehicles per hour.
     * @param demand_vph Demand in veh/hr as a Profile1D object
     * @param comm_id ID for the commodity
     */
    public void set_or_demand_vph(Profile1D demand_vph, long comm_id)throws Exception {

        // TODO: check values, throw exception

        this.or_demands.put(comm_id,demand_vph);
    }

    /////////////////////////////////////
    // mainline
    /////////////////////////////////////

    public String get_ml_name(){
        return "NOT IMPLEMENTED";
    }

    public int get_mixed_lanes(){
        return ml.full_lanes;
    }

    public int get_hov_lanes(){
        System.out.println("NOT IMPLEMENTED!");
        return 0;
    }

    public float get_capacity_vphpl(){
        return ml.capacity_vphpl;
    }

    public double get_jam_density_vpmpl(){
        return ml.jam_density_vpkpl * KmPerMile;
    }

    public double get_freespeed_mph(){
        return ml.ff_speed_kph / KmPerMile;
    }

    public void set_ml_name(String newname) {
        ml.name = newname;
    }

    public void set_mixed_lanes(int x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive number of lanes");
        ml.full_lanes = x;
    }

    public void set_hov_lanes(int x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive number of lanes");
        System.out.println("NOT IMPLEMENTED!");
    }

    public void set_capacity_vphpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive capacity");
        ml.capacity_vphpl = x;
    }

    public void set_jam_density_vpmpl(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive jam density");
        ml.jam_density_vpkpl = x/KmPerMile;
    }

    public void set_freespeed_mph(float x) throws Exception {
        if(x<=0)
            throw new Exception("Non-positive free speed");
        ml.ff_speed_kph = x * KmPerMile;
    }

    /////////////////////////////////////
    // protected
    /////////////////////////////////////

    protected void set_start_node(long new_start_node){
        ml.start_node_id = new_start_node;
        if(fr!=null)
            fr.start_node_id = new_start_node;
    }

    protected void set_end_node(long new_end_node){
        ml.end_node_id = new_end_node;
        if(or!=null)
            or.end_node_id = new_end_node;
    }

    protected AbstractLink get_ml(){
        return ml;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        String str = String.format("name\t%s\nfr\t%s\nml:\t%s\nor:\t%s",
                name,
                fr==null?"null":fr.toString(),
                ml.toString(),
                or==null?"null":or.toString());
        return str;
    }

}
