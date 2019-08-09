package opt.data;

import utils.OTMUtils;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class Segment implements Comparable {

    protected FreewayScenario fwy_scenario;
    protected long id;
    public String name;
    public LinkFreewayOrConnector fwy;
    protected List<LinkOnramp> in_ors = new ArrayList<>();
    protected List<LinkOnramp> out_ors = new ArrayList<>();
    protected List<LinkOfframp> in_frs = new ArrayList<>();
    protected List<LinkOfframp> out_frs = new ArrayList<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Segment(){}

    // used by FreewayScenario jaxb constructor
    public Segment(FreewayScenario fwy_scenario,long id, jaxbopt.Sgmt sgmt) {

        this.id = id;
        this.fwy_scenario = fwy_scenario;
        this.name = sgmt.getName();

        this.fwy = (LinkFreewayOrConnector) fwy_scenario.scenario.links.get(Long.valueOf(sgmt.getFwy()));

        // onramps and offramps
        if(sgmt.getInOrs()!=null && !sgmt.getInOrs().isEmpty())
            for (Long orid : OTMUtils.csv2longlist(sgmt.getInOrs()))
                in_ors.add((LinkOnramp)fwy_scenario.scenario.links.get(orid));

        if(sgmt.getOutOrs()!=null && !sgmt.getOutOrs().isEmpty())
            for(Long orid : OTMUtils.csv2longlist(sgmt.getOutOrs()))
                out_ors.add((LinkOnramp)fwy_scenario.scenario.links.get(orid));

        if(sgmt.getInFrs()!=null && !sgmt.getInFrs().isEmpty())
            for(Long frid : OTMUtils.csv2longlist(sgmt.getInFrs()))
                in_frs.add((LinkOfframp)fwy_scenario.scenario.links.get(frid));

        if(sgmt.getOutFrs()!=null && !sgmt.getOutFrs().isEmpty())
            for(Long frid : OTMUtils.csv2longlist(sgmt.getOutFrs()))
                out_frs.add((LinkOfframp)fwy_scenario.scenario.links.get(frid));

        // assign segment to links
        for(AbstractLink link : get_links())
            link.mysegment = this;
    }

    public Segment clone() {
        Segment new_seg = new Segment();
        new_seg.id = id;
        new_seg.name = name;
        new_seg.fwy = (LinkFreewayOrConnector) fwy.clone();
        for(LinkOnramp x : in_ors)
            new_seg.in_ors.add((LinkOnramp) x.clone());

        for(LinkOnramp x : out_ors)
            new_seg.out_ors.add((LinkOnramp) x.clone());

        for(LinkOfframp x : in_frs)
            new_seg.in_frs.add((LinkOfframp) x.clone());

        for(LinkOfframp x : out_frs)
            new_seg.out_frs.add((LinkOfframp) x.clone());

        return new_seg;
    }

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

    public LinkOnramp in_ors(int i){
        return in_ors.get(i);
    }

    public LinkOnramp out_ors(int i){
        return out_ors.get(i);
    }

    public LinkOfframp in_frs(int i){
        return in_frs.get(i);
    }

    public LinkOfframp out_frs(int i){
        return out_frs.get(i);
    }

    ////////////////////////////////////////
    // add ramps
    ////////////////////////////////////////

    public LinkOnramp add_in_or(LinkParameters params,int gp_lanes,float length){
        LinkOnramp link = create_onramp(params,gp_lanes,length);
        in_ors.add(link);
        return link;
    }

    public LinkOnramp add_out_or(LinkParameters params,int gp_lanes,float length){
        LinkOnramp link = create_onramp(params,gp_lanes,length);
        out_ors.add(link);
        return link;
    }

    public LinkOfframp add_in_fr(LinkParameters params,int gp_lanes,float length){
        LinkOfframp link = create_offramp(params,gp_lanes,length);
        in_frs.add(link);
        return link;
    }

    public LinkOfframp add_out_fr(LinkParameters params,int gp_lanes,float length){
        LinkOfframp link = create_offramp(params,gp_lanes,length);
        out_frs.add(link);
        return link;
    }

    ////////////////////////////////////////
    // delete ramps
    ////////////////////////////////////////

    public boolean delete_in_or(LinkOnramp link){
        if( in_ors.contains(link) ){
            fwy_scenario.delete_link(link);
            in_ors.remove(link);
            return true;
        } else
            return false;
    }

    public boolean delete_out_or(LinkOnramp link){
        if( out_ors.contains(link) ){
            fwy_scenario.delete_link(link);
            out_ors.remove(link);
            return true;
        } else
            return false;
    }

    public boolean delete_in_fr(LinkOfframp link){
        if( in_frs.contains(link) ){
            fwy_scenario.delete_link(link);
            in_frs.remove(link);
            return true;
        } else
            return false;
    }

    public boolean delete_out_fr(LinkOfframp link){
        if( out_frs.contains(link) ){
            fwy_scenario.delete_link(link);
            out_frs.remove(link);
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
        X.remove(null);
        return X;
    }

    public Set<AbstractLink> get_upstrm_links(){
        Set<AbstractLink> X = new HashSet<>();
        if(fwy.up_link!=null)
            X.add(fwy.up_link);
        X.addAll(in_ors.stream().map(x->x.up_link).collect(toSet()));
        X.addAll(out_ors.stream().map(x->x.up_link).collect(toSet()));
        X.remove(null);
        return X;
    }

    public Set<Segment> get_dnstrm_segments(){
        Set<Segment> X = new HashSet<>();
        if(fwy.dn_link!=null & fwy.dn_link.mysegment!=null)
            X.add(fwy.dn_link.mysegment);
        X.addAll(in_frs.stream().map(x->x.get_dn_segment()).collect(toSet()));
        X.addAll(out_frs.stream().map(x->x.get_dn_segment()).collect(toSet()));
        X.remove(null);
        return X;
    }

    public Set<AbstractLink> get_dnstrm_links(){
        Set<AbstractLink> X = new HashSet<>();
        X.add(fwy.dn_link);
        X.addAll(in_frs.stream().map(x->x.dn_link).collect(toSet()));
        X.addAll(out_frs.stream().map(x->x.dn_link).collect(toSet()));
        X.remove(null);
        return X;
    }

    /////////////////////////////////////
    // protected and private
    /////////////////////////////////////

    private LinkOnramp create_onramp(LinkParameters params,int gp_lanes,float length){

        Long link_id = fwy_scenario.new_link_id();
        Node start_node = new Node(fwy_scenario.new_node_id());
        long end_node_id = this.fwy.start_node_id;

        LinkOnramp link = new LinkOnramp(link_id,
                start_node.id,
                end_node_id,
                gp_lanes,
                length,
                params.capacity_vphpl,
                params.jam_density_vpkpl,
                params.ff_speed_kph,
                this);

        start_node.in_links.add(link.id);
        fwy_scenario.scenario.nodes.put(start_node.id,start_node);
        fwy_scenario.scenario.links.put(link.id,link);

        return link;
    }

    private LinkOfframp create_offramp(LinkParameters params,int gp_lanes,float length){

        Long link_id = fwy_scenario.new_link_id();
        Node end_node = new Node(fwy_scenario.new_node_id());
        long start_node_id = this.fwy.end_node_id;

        LinkOfframp link = new LinkOfframp(link_id,
                start_node_id,
                end_node.id,
                gp_lanes,
                length,
                params.capacity_vphpl,
                params.jam_density_vpkpl,
                params.ff_speed_kph,
                this);

        end_node.out_links.add(link.id);
        fwy_scenario.scenario.nodes.put(end_node.id,end_node);
        fwy_scenario.scenario.links.put(link.id,link);

        return link;
    }

    private String get_type(){
        switch (fwy.get_type()){
            case freeway:
                return "fwy";
            case connector:
                return "con";
            default:
                return "";
        }
    }

    protected jaxbopt.Sgmt to_jaxb(){
        jaxbopt.Sgmt sgmt = new jaxbopt.Sgmt();
        sgmt.setName(name);
        sgmt.setType(get_type());

        sgmt.setFwy(String.format("%d",fwy.id));

        if(!out_ors.isEmpty())
            sgmt.setOutOrs(OTMUtils.comma_format(out_ors.stream().map(x -> x.id).collect(toSet())));

        if(!in_ors.isEmpty())
            sgmt.setInOrs(OTMUtils.comma_format(in_ors.stream().map(x -> x.id).collect(toSet())));

        if(!out_frs.isEmpty())
            sgmt.setOutFrs(OTMUtils.comma_format(out_frs.stream().map(x -> x.id).collect(toSet())));

        if(!in_frs.isEmpty())
            sgmt.setInFrs(OTMUtils.comma_format(in_frs.stream().map(x -> x.id).collect(toSet())));

        return sgmt;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public int compareTo(Object that) {
        return this.name.compareTo(((Segment) that).name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Segment segment = (Segment) o;
        return id == segment.id &&
                name.equals(segment.name) &&
                fwy.equals(segment.fwy) &&
                in_ors.equals(segment.in_ors) &&
                out_ors.equals(segment.out_ors) &&
                in_frs.equals(segment.in_frs) &&
                out_frs.equals(segment.out_frs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, fwy, in_ors, out_ors, in_frs, out_frs);
    }







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


}
