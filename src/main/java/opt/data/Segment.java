package opt.data;

import utils.OTMUtils;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class Segment {

    protected FreewayScenario fwy_scenario;

    protected long id;

    public String name;
    public LinkFreewayOrConnector fwy;
    protected List<Onramp> in_ors = new ArrayList<>();
    protected List<Onramp> out_ors = new ArrayList<>();
    protected List<Offramp> in_frs = new ArrayList<>();
    protected List<Offramp> out_frs = new ArrayList<>();

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
    public Segment(FreewayScenario fwy_scenario,long id, jaxbopt.Sgmt sgmt) {

        this.id = id;
        this.fwy_scenario = fwy_scenario;
        this.name = sgmt.getName();

        // onramps and offramps
        if(!sgmt.getInOrs().isEmpty())
            for (Long orid : OTMUtils.csv2longlist(sgmt.getInOrs()))
                in_ors.add((Onramp)fwy_scenario.scenario.links.get(orid));

        if(!sgmt.getOutOrs().isEmpty())
            for(Long orid : OTMUtils.csv2longlist(sgmt.getOutOrs()))
                out_ors.add((Onramp)fwy_scenario.scenario.links.get(orid));

        if(!sgmt.getInFrs().isEmpty())
            for(Long frid : OTMUtils.csv2longlist(sgmt.getInFrs()))
                in_frs.add((Offramp)fwy_scenario.scenario.links.get(frid));

        if(!sgmt.getOutFrs().isEmpty())
            for(Long frid : OTMUtils.csv2longlist(sgmt.getOutFrs()))
                out_frs.add((Offramp)fwy_scenario.scenario.links.get(frid));
    }

    // used by Segment.create_new_segment
//    public Segment(FreewayScenario fwy_scenario,long id,String name,Long fwy_id){
//        this.fwy_scenario = fwy_scenario;
//        this.id = id;
//        this.name = name;
//        this.fwy_id = fwy_id;
//    }

//    public Segment deep_copy(FreewayScenario scenario){
//        Segment seg_cpy = new Segment(id,name,scenario);
//        seg_cpy.fwy_id = fwy_id;
//
//        seg_cpy.in_ors = new ArrayList<>(in_ors);
//        seg_cpy.out_ors = new ArrayList<>(out_ors);
//        seg_cpy.in_frs = new ArrayList<>(in_frs);
//        seg_cpy.out_frs = new ArrayList<>(out_frs);
//
//        seg_cpy.segment_fwy_dn_id = segment_fwy_dn_id;
//        seg_cpy.segment_fwy_up_id = segment_fwy_up_id;
//
//        for(Map.Entry<Long,Profile1D> e : fwy_demands.entrySet())
//            seg_cpy.fwy_demands.put(e.getKey(),e.getValue().clone());
//
//        return seg_cpy;
//    }

    /////////////////////////////////////
    // getters / setters
    /////////////////////////////////////

    /**
     * Get the length of this segment in meters
     * @return float
     */
    public float get_length_meters(){
        return fwy.length_meters;
    }

    /**
     * Set the length of this segment in meters
     * @param newlength
     */
    public void set_length_meters(float newlength) throws Exception {
        if (newlength<=0.0001)
            throw new Exception("Attempted to set a non-positive segment length");
        fwy.length_meters = newlength;
    }

    public List<AbstractLink> get_links(){
        List<AbstractLink> X = new ArrayList<>();
        X.add(fwy);
        X.addAll(in_ors);
        X.addAll(out_ors);
        X.addAll(in_frs);
        X.addAll(out_frs);
        return X;
    }

    public int num_in_ors(){
        return in_ors.size();
    }

    public int num_out_ors(){
        return out_ors.size();
    }

    public int num_in_frs(){
        return in_frs.size();
    }

    public int num_out_frs(){
        return out_frs.size();
    }

    public Onramp in_ors(int i){
        return in_ors.get(i);
    }

    public Onramp out_ors(int i){
        return out_ors.get(i);
    }

    public Offramp in_frs(int i){
        return in_frs.get(i);
    }

    public Offramp out_frs(int i){
        return out_frs.get(i);
    }

    ////////////////////////////////////////
    // add ramps
    ////////////////////////////////////////

    public void add_in_or(Onramp x){
        if(x==null)
            return;
        in_ors.add(x);

        // TODO FINISH THIS
    }

    public void add_out_or(Onramp x){
        if(x==null)
            return;
        out_ors.add(x);

        // TODO FINISH THIS

//        long id = fwy_scenario.new_link_id();
//        Node start_node = new Node(fwy_scenario.new_node_id());
//        long start_node_id = start_node.id;
//        long end_node_id = fwy().end_node_id;
//        int full_lanes = 1;
//        float length = 100f;
//        AbstractLink or = new AbstractLink(id, AbstractLink.Type.onramp,start_node_id,end_node_id,full_lanes,length,
//                params.capacity_vphpl,
//                params.jam_density_vpkpl,
//                params.ff_speed_kph,this);
//        or_id = or.id;
//        or.mysegment = this;
//        start_node.out_links.add(or_id);
//        fwy_scenario.scenario.nodes.put(start_node.id,start_node);
//        fwy_scenario.scenario.links.put(or.id,or);
    }

    public void add_in_fr(Offramp x){
        if(x==null)
            return;
        in_frs.add(x);

        // TODO FINISH THIS

//        long id = fwy_scenario.new_link_id();
//        AbstractLink fwy = fwy();
//        long start_node_id = fwy.start_node_id;
//        Node end_node = new Node(fwy_scenario.new_node_id());
//        long end_node_id = end_node.id;
//        int full_lanes = 1;
//        float length = 100f;
//
//        AbstractLink fr = new AbstractLink(id, AbstractLink.Type.offramp,start_node_id,end_node_id,full_lanes,length,
//                params.capacity_vphpl,
//                params.jam_density_vpkpl,
//                params.ff_speed_kph,
//                this);
//
//        fr_id = fr.id;
//        fr.mysegment = this;
//        end_node.in_links.add(fr_id);
//        fwy_scenario.scenario.nodes.put(end_node.id,end_node);
//        fwy_scenario.scenario.links.put(fr.id,fr);
    }

    public void add_out_fr(Offramp x){
        if(x==null)
            return;
        out_frs.add(x);

        // TODO FINISH THIS
    }

    ////////////////////////////////////////
    // delete ramps
    ////////////////////////////////////////

    public boolean delete_in_or(Onramp x){
        if( in_ors.contains(x) ){

            in_ors.remove(x);

            // TODO FINISH THIS

            return true;
        } else
            return false;

    }

    public boolean delete_out_or(Onramp x){

        if( out_ors.contains(x) ){

            out_ors.remove(x);

            // TODO FINISH THIS
//            AbstractLink or = or();
//            fwy_scenario.scenario.nodes.remove(or.start_node_id);
//            fwy_scenario.scenario.links.remove(or.id);
//            if(fwy_scenario.scenario.nodes.containsKey(or.end_node_id)) {
//                Node end_node = fwy_scenario.scenario.nodes.get(or.end_node_id);
//                end_node.in_links.remove(or.id);
//            }
//            or_id = null;
//            or_demands = new HashMap<>();
//            return true;

            return true;
        } else
            return false;

    }

    public boolean delete_in_fr(Offramp x){

        if( in_frs.contains(x) ){

            in_frs.remove(x);

            // TODO FINISH THIS

//            fwy_scenario.scenario.nodes.remove(fr.end_node_id);
//            fwy_scenario.scenario.links.remove(fr.id);
//            if(fwy_scenario.scenario.nodes.containsKey(fr.start_node_id)){
//                Node start_node = fwy_scenario.scenario.nodes.get(fr.start_node_id);
//                start_node.out_links.remove(fr.id);
//            }
//            fr_id = null;
//            fr_splits = new HashMap<>();
//            return true;


            return true;
        } else
            return false;

    }

    public boolean delete_out_fr(Offramp x){

        if( out_frs.contains(x) ){

            out_frs.remove(x);

            // TODO FINISH THIS

            return true;
        } else
            return false;

    }

    /////////////////////////////////////
    // segment and link getters
    /////////////////////////////////////

    public Set<Segment> get_upstrm_segments(){
        Set<Segment> X = new HashSet<>();
        X.add(fwy.get_up_segment());
        X.addAll(in_ors.stream().map(x->x.get_up_segment()).collect(toSet()));
        X.addAll(out_ors.stream().map(x->x.get_up_segment()).collect(toSet()));
        return X;
    }

    public Set<AbstractLink> get_upstrm_links(){
        Set<AbstractLink> X = new HashSet<>();
        X.add(fwy.up_link);
        X.addAll(in_ors.stream().map(x->x.up_link).collect(toSet()));
        X.addAll(out_ors.stream().map(x->x.up_link).collect(toSet()));
        return X;
    }

    public Set<Segment> get_dnstrm_segments(){
        Set<Segment> X = new HashSet<>();
        X.add(fwy.get_dn_segment());
        X.addAll(in_frs.stream().map(x->x.get_dn_segment()).collect(toSet()));
        X.addAll(out_frs.stream().map(x->x.get_dn_segment()).collect(toSet()));
        return X;
    }

    public Set<AbstractLink> get_dnstrm_links(){
        Set<AbstractLink> X = new HashSet<>();
        X.add(fwy.dn_link);
        X.addAll(in_frs.stream().map(x->x.dn_link).collect(toSet()));
        X.addAll(out_frs.stream().map(x->x.dn_link).collect(toSet()));
        return X;
    }

    /////////////////////////////////////
    // protected and private
    /////////////////////////////////////


//    protected void set_start_node(long new_start_node){
//        fwy().start_node_id = new_start_node;
//        if(has_offramp())
//            fr().start_node_id = new_start_node;
//    }
//
//    protected void set_end_node(long new_end_node){
//        fwy().end_node_id = new_end_node;
//        if(has_onramp())
//            or().end_node_id = new_end_node;
//    }
//
//    private static void connect_segment_to_downstream_node(Segment segment, Node new_dwn_node){
//
//        if(segment==null)
//            return;
//
//        Node old_dwn_node = segment.fwy_scenario.scenario.nodes.get(segment.fwy().end_node_id);
//        segment.fwy().end_node_id = new_dwn_node.id;
//        old_dwn_node.in_links.remove(segment.fwy_id);
//        new_dwn_node.in_links.add(segment.fwy_id);
//
//        if(segment.has_onramp() && segment.or().end_node_id==old_dwn_node.id) {
//            segment.or().end_node_id = new_dwn_node.id;
//            old_dwn_node.in_links.remove(segment.or_id);
//            new_dwn_node.in_links.add(segment.or_id);
//        }
//
//        if(segment.has_offramp() && segment.fr().start_node_id==old_dwn_node.id) {
//            segment.fr().start_node_id = new_dwn_node.id;
//            old_dwn_node.out_links.remove(segment.fr_id);
//            new_dwn_node.out_links.add(segment.fr_id);
//        }
//    }
//
//    private static void connect_segment_to_upstream_node(Segment segment, Node new_up_node){
//
//        if(segment==null)
//            return;
//
//        Node old_up_node = segment.fwy_scenario.scenario.nodes.get(segment.fwy().start_node_id);
//        segment.fwy().start_node_id = new_up_node.id;
//        old_up_node.out_links.remove(segment.fwy_id);
//        new_up_node.out_links.add(segment.fwy_id);
//
//        if(segment.has_onramp() && segment.or().end_node_id==old_up_node.id) {
//            segment.or().end_node_id = new_up_node.id;
//            old_up_node.out_links.remove(segment.or_id);
//            new_up_node.out_links.add(segment.or_id);
//        }
//
//        if(segment.has_offramp() && segment.fr().start_node_id==old_up_node.id) {
//            segment.fr().start_node_id = new_up_node.id;
//            old_up_node.in_links.remove(segment.fr_id);
//            new_up_node.in_links.add(segment.fr_id);
//        }
//    }
//
//    private Segment create_new_segment(AbstractLink newfwy){
//        Long new_seg_id = fwy_scenario.new_seg_id();
//        String new_seg_name = String.format("segment %d",new_seg_id);
//        Segment newseg = new Segment(fwy_scenario,new_seg_id,new_seg_name,newfwy.id);
//        newfwy.mysegment = newseg;
//        newseg.segment_fwy_dn_id = this.id;
//        return newseg;
//    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    public jaxbopt.Sgmt to_jaxb(){
        jaxbopt.Sgmt sgmt = new jaxbopt.Sgmt();
//        sgmt.setName(name);
//        sgmt.setLinks(OTMUtils.comma_format(
//                get_links().stream()
//                        .filter(x->x!=null)
//                        .map(link->link.get_id())
//                        .collect(toSet())));
        return sgmt;
    }

//    @Override
//    public String toString() {
//        return String.format("name\t%s\nfr\t%s\nfwy:\t%s\nor:\t%s", name, fr_id, fwy_id, or_id);
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Segment segment = (Segment) o;
//        return id == segment.id &&
//                name.equals(segment.name) &&
//                fwy_id.equals(segment.fwy_id) &&
//                Objects.equals(or_id, segment.or_id) &&
//                Objects.equals(fr_id, segment.fr_id) &&
//                Objects.equals(segment_fwy_dn_id, segment.segment_fwy_dn_id) &&
//                Objects.equals(segment_fwy_up_id, segment.segment_fwy_up_id) &&
//                Objects.equals(segment_fr_dn_id, segment.segment_fr_dn_id) &&
//                Objects.equals(segment_or_up_id, segment.segment_or_up_id) &&
//                fwy_demands.equals(segment.fwy_demands) &&
//                or_demands.equals(segment.or_demands) &&
//                fr_splits.equals(segment.fr_splits);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(name, id, fwy_id, or_id, fr_id, segment_fwy_dn_id, segment_fwy_up_id, segment_fr_dn_id, segment_or_up_id, fwy_demands, or_demands, fr_splits);
//    }
}
