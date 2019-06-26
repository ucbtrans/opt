package opt.data;

public class Segment {

    private final static float KmPerMile = 1.609344f;
    private final static float FeetPerMeter = 3.28084f;

    private jLink ml;
    private jLink or;
    private jLink fr;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Segment(jLink or, jLink ml, jLink fr) throws Exception {

        if(ml==null)
            throw new Exception("A segment is not allowed to have a null mainline.");

        this.or = or;
        this.ml = ml;
        this.fr = fr;
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
        return has_onramp() ? or.get_max_vehicles() : 0d;
    }

    /////////////////////////////////////
    // onramp setters
    /////////////////////////////////////

    private void add_onramp(){

    }

    public void set_or_lanes(int num_lanes){
        System.err.println("NOT IMPLEMENTED.");
//        if(or==null)
//            add_onramp();
//        or.set_lanes(num_lanes);
    }

    public void set_or_capacity_vphpl(float x){
        System.err.println("NOT IMPLEMENTED.");
//        if(or==null)
//            add_onramp();
//        or.set_capacity_vphpl(x);
    }

    public void set_or_max_vehicles(float x){
        System.err.println("NOT IMPLEMENTED.");
//        if(or==null)
//            add_onramp();
//        or.set_max_vehicles(x);
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
        return has_onramp() ? fr.get_max_vehicles() : 0d;
    }

    /////////////////////////////////////
    // offramp setters
    /////////////////////////////////////

    private void add_offramp(){
    }

    public void set_fr_lanes(int x){
        System.err.println("NOT IMPLEMENTED.");
//        if(fr==null)
//            add_onramp();
//        fr.set_lanes(x);
    }

    public void set_fr_capacity_vphpl(float x){
        System.err.println("NOT IMPLEMENTED.");
//        if(fr==null)
//            add_onramp();
//        fr.set_capacity_vphpl(x);
    }

    public void set_fr_max_vehicles(float x){
        System.err.println("NOT IMPLEMENTED.");
//        if(fr==null)
//            add_onramp();
//        fr.set_max_vehicles(x);
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
        System.err.println("NOT IMPLEMENTED.");
//        ml.set_lanes(x);
    }

    public void set_ml_length_feet(float x){
        System.err.println("NOT IMPLEMENTED.");
//        return ml.set_length_meters(x / FeetPerMeter);
    }

    public void set_ml_capacity_vphpl(float x){
        System.err.println("NOT IMPLEMENTED.");
//        return ml.set_capacity_vphpl(x);
    }

    public void set_ml_jam_density_vpmpl(float x){
        System.err.println("NOT IMPLEMENTED.");
//        return ml.set_jam_density_vpkpl(x/KmPerMile);
    }

    public void set_ml_freespeed_mph(float x){
        System.err.println("NOT IMPLEMENTED.");
//        return ml.set_ffspeed_kph(x * KmPerMile);
    }

    /////////////////////////////////////
    // protected
    /////////////////////////////////////

    protected jLink get_ml(){
        return ml;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        String str = String.format("ml\t%s\nor:\t%s\nfr:\t%s",
                ml.toString(),
                or==null?"null":or.toString(),
                fr==null?"null":fr.toString());
        return str;
    }

}
