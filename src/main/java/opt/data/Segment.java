package opt.data;

public class Segment {

    private final static float KmPerMile = 1.609344f;
    private final static float FeetPerMeter = 3.28084f;
    private final static float default_or_capacity_vphpl = 1500f;
    private final static float default_or_jam_density_vpkpl = 100f;
    private final static float default_or_ff_speed_kph = 40f;
    private final static float default_fr_capacity_vphpl = 1500f;
    private final static float default_fr_jam_density_vpkpl = 100f;
    private final static float default_fr_ff_speed_kph = 40f;

    protected FreewayScenario fwy_scenario;
    protected jLink ml;
    protected jLink or;
    protected jLink fr;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Segment(){}

    public Segment(FreewayScenario fwy_scenario,jLink or, jLink ml, jLink fr) throws Exception {

        if(ml==null)
            throw new Exception("A segment is not allowed to have a null mainline.");

        this.fwy_scenario = fwy_scenario;
        this.or = or;
        this.ml = ml;
        this.fr = fr;
    }

    /////////////////////////////////////
    // deletion
    /////////////////////////////////////

    /**
     * Delete the offramp from this segment
     * @return success value
     */
    public boolean delete_offramp(){
        if(fr==null)
            return false;
        fwy_scenario.jscenario.nodes.remove(fr.end_node_id);
        fwy_scenario.jscenario.links.remove(fr.id);
        fr = null;
        return true;
    }

    /**
     * Delete the onramp from this segment
     * @return success value
     */
    public boolean delete_onramp(){
        if(or==null)
            return false;
        fwy_scenario.jscenario.nodes.remove(or.start_node_id);
        fwy_scenario.jscenario.links.remove(or.id);
        or = null;
        return true;
    }

    /////////////////////////////////////
    // onramp getters
    /////////////////////////////////////

    public boolean has_onramp(){
        return or!=null;
    }

    public int get_or_lanes(){
        return has_onramp() ? or.full_lanes : 0;
    }

    public float get_or_capacity_vphpl(){
        return has_onramp() ? or.capacity_vphpl: Float.NaN;
    }

    public double get_or_max_vehicles(){
        return has_onramp() ? or.jam_density_vpkpl * or.full_lanes * or.length / 1000f : 0d;
    }

    /////////////////////////////////////
    // onramp setters
    /////////////////////////////////////

    private void add_onramp(){
        if(or!=null)
            return;
        long id = fwy_scenario.new_link_id();
        jNode start_node = new jNode(fwy_scenario.new_node_id());
        long start_node_id = start_node.id;
        long end_node_id = ml.end_node_id;
        int full_lanes = 1;
        float length = 100f;
        boolean is_mainline = false;
        boolean is_ramp = true;
        boolean is_source = true;
        float capacity_vphpl = default_or_capacity_vphpl;
        float jam_density_vpkpl = default_or_jam_density_vpkpl;
        float ff_speed_kph = default_or_ff_speed_kph;
        or = new jLink(id,start_node_id,end_node_id,full_lanes,length,is_mainline,is_ramp,is_source,capacity_vphpl, jam_density_vpkpl,ff_speed_kph);
        start_node.out_links.add(or);
        fwy_scenario.jscenario.nodes.put(start_node.id,start_node);
        fwy_scenario.jscenario.links.put(or.id,or);
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
        or.jam_density_vpkpl = x / (or.length/1000f) / or.full_lanes;

    }

    /////////////////////////////////////
    // offramp getters
    /////////////////////////////////////

    public boolean has_offramp(){
        return fr!=null;
    }

    public int get_fr_lanes(){
        return has_offramp() ? fr.full_lanes : 0;
    }

    public float get_fr_capacity_vphpl(){
        return has_offramp() ? fr.capacity_vphpl : Float.NaN;
    }

    public double get_fr_max_vehicles(){
        return has_offramp() ? fr.jam_density_vpkpl * fr.full_lanes * fr.length / 1000f : 0d;
    }

    /////////////////////////////////////
    // offramp setters
    /////////////////////////////////////

    private void add_offramp(){
        if(fr!=null)
            return;
        long id = fwy_scenario.new_link_id();
        long start_node_id = ml.start_node_id;
        jNode end_node = new jNode(fwy_scenario.new_node_id());
        long end_node_id = end_node.id;
        int full_lanes = 1;
        float length = 100f;
        boolean is_mainline = false;
        boolean is_ramp = true;
        boolean is_source = false;
        float capacity_vphpl = default_fr_capacity_vphpl;
        float jam_density_vpkpl = default_fr_jam_density_vpkpl;
        float ff_speed_kph = default_fr_ff_speed_kph;

        fr = new jLink(id,start_node_id,end_node_id,full_lanes,length,is_mainline,is_ramp,is_source,capacity_vphpl, jam_density_vpkpl,ff_speed_kph);
        end_node.in_links.add(fr);
        fwy_scenario.jscenario.nodes.put(end_node.id,end_node);
        fwy_scenario.jscenario.links.put(fr.id,fr);
    }

    public void set_fr_lanes(int x){
        if(fr==null)
            add_offramp();
        fr.full_lanes = x;
    }

    public void set_fr_capacity_vphpl(float x){
        if(fr==null)
            add_offramp();
        fr.capacity_vphpl = x;
    }

    public void set_fr_max_vehicles(float x){
        if(fr==null)
            add_offramp();
        fr.jam_density_vpkpl = x / (fr.length/1000f) / fr.full_lanes;
    }

    /////////////////////////////////////
    // mainline getters
    /////////////////////////////////////

    public int get_ml_lanes(){
        return ml.full_lanes;
    }

    public float get_ml_length_feet(){
        return ml.length * FeetPerMeter;
    }

    public float get_ml_capacity_vphpl(){
        return ml.capacity_vphpl;
    }

    public double get_ml_jam_density_vpmpl(){
        return ml.jam_density_vpkpl * KmPerMile;
    }

    public double get_ml_freespeed_mph(){
        return ml.ff_speed_kph / KmPerMile;
    }

    /////////////////////////////////////
    // mainline setters
    /////////////////////////////////////

    public void set_ml_lanes(int x){
        ml.full_lanes = x;
    }

    public void set_ml_length_feet(float x){
        ml.length = x / FeetPerMeter;
    }

    public void set_ml_capacity_vphpl(float x){
        ml.capacity_vphpl = x;
    }

    public void set_ml_jam_density_vpmpl(float x){
        ml.jam_density_vpkpl = x/KmPerMile;
    }

    public void set_ml_freespeed_mph(float x){
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

    protected jLink get_ml(){
        return ml;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        String str = String.format("fr\t%s\nml:\t%s\nor:\t%s",
                fr==null?"null":fr.toString(),
                ml.toString(),
                or==null?"null":or.toString());
        return str;
    }

}
