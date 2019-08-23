package opt.data;

import utils.OTMUtils;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public final class Segment implements Comparable {

    protected FreewayScenario fwy_scenario;
    protected long id;
    public String name;
    protected LinkFreewayOrConnector fwy;
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

    public Long get_id(){
        return id;
    }

    public String get_name(){
        return name;
    }

    /**
     * Get scenario, to which this segment belongs.
     */
    public FreewayScenario get_scenario() {
        return fwy_scenario;
    }

    /**
     * Get the length of this segment in meters
     * @return float
     */
    public float get_length_meters(){
        return fwy.get_length_meters();
    }

    /**
     * Set the length of this segment in meters
     * @param newlength
     */
    public void set_length_meters(float newlength) throws Exception {
        if (newlength<=0.0001)
            throw new Exception("Attempted to set a non-positive segment length");
        fwy.set_length_meters( newlength );
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

    public List<LinkOnramp> get_ors(){
        List<LinkOnramp> X = new ArrayList<>();
        X.addAll(in_ors);
        X.addAll(out_ors);
        return X;
    }

    public List<LinkOfframp> get_frs(){
        List<LinkOfframp> X = new ArrayList<>();
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

    public LinkFreewayOrConnector fwy(){
        return fwy;
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
    // insert / delete segments
    ////////////////////////////////////////

    public Segment insert_up_segment(String seg_name, ParametersFreeway params){
        return fwy.insert_up_segment(seg_name,params,null);
    }

    public Segment insert_dn_segment(String seg_name, ParametersFreeway params){
        return fwy.insert_dn_segment(seg_name,params,null);
    }

    ////////////////////////////////////////
    // add / delete ramps
    ////////////////////////////////////////

//    public LinkOnramp add_in_or(ParametersRamp params){
//        LinkOnramp link = create_onramp(params);
//        in_ors.add(link);
//        return link;
//    }

    public LinkOnramp add_or(ParametersRamp params){
        LinkOnramp or = create_onramp(params);
        if(params.is_inner)
            in_ors.add(or);
        else
            out_ors.add(or);
        return or;
    }

//    public LinkOfframp add_in_fr(ParametersRamp params){
//        LinkOfframp link = create_offramp(params);
//        in_frs.add(link);
//        return link;
//    }

    public LinkOfframp add_fr(ParametersRamp params){
        LinkOfframp fr = create_offramp(params);
        if(params.is_inner)
            in_frs.add(fr);
        else
            out_frs.add(fr);
        return fr;
    }

    public boolean delete_in_or(LinkOnramp or){
        if( in_ors.contains(or) ){
            delete_ramp(or);
            in_ors.remove(or);
            return true;
        } else
            return false;
    }

    public boolean delete_out_or(LinkOnramp or){
        if( out_ors.contains(or) ){
            delete_ramp(or);
            out_ors.remove(or);
            return true;
        } else
            return false;
    }

    public boolean delete_in_fr(LinkOfframp link){
        if( in_frs.contains(link) ){
            delete_ramp(link);
            in_frs.remove(link);

            // TODO UPDATE SPLITS
            return true;
        } else
            return false;
    }

    public boolean delete_out_fr(LinkOfframp link){
        if( out_frs.contains(link) ){
            delete_ramp(link);
            out_frs.remove(link);

            // TODO UPDATE SPLITS
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
        if(fwy.dn_link!=null)
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

    private void delete_ramp(AbstractLink link){

        if(!(link instanceof LinkOfframp) && !(link instanceof LinkOnramp))
            return;

        Scenario scenario = fwy_scenario.scenario;

        // disconnect from start and end nodes
        Node start_node = scenario.nodes.get(link.start_node_id);
        start_node.out_links.remove(link.id);

        Node end_node = scenario.nodes.get(link.end_node_id);
        end_node.in_links.remove(link.id);

        // disconnect from adjacent links

        // link is onramp => up_link is a connector, dn_link is a freeway
        if( link instanceof LinkOnramp )
            if(link.up_link!=null)
                link.up_link.dn_link = null;

        // link is offramp => up_link is a freeway, dn_link is a connector
        if( link instanceof LinkOfframp )
            if(link.dn_link!=null)
                link.dn_link.up_link = null;

        // remove the link from the scenario
        scenario.links.remove(link.id);
    }

    private LinkOnramp create_onramp(ParametersRamp params){

        Long link_id = fwy_scenario.new_link_id();
        Node or_start_node = new Node(fwy_scenario.new_node_id());
        fwy_scenario.scenario.nodes.put(or_start_node.id,or_start_node);
        Node or_end_node = fwy_scenario.scenario.nodes.get(fwy.start_node_id);

        LinkOnramp or = new LinkOnramp(
                link_id, // id,
                this, // mysegment,
                null, // up_link,
                null, // dn_link,
                or_start_node.id,// start_node_id,
                or_end_node.id,// end_node_id,
                params); // params)

        fwy_scenario.scenario.links.put(or.id,or);
        or.dn_link = fwy;
        or_start_node.out_links.add(or.id);

        return or;
    }

    private LinkOfframp create_offramp(ParametersRamp params){

        Long link_id = fwy_scenario.new_link_id();
        Node fr_start_node = fwy_scenario.scenario.nodes.get(fwy.end_node_id);
        Node fr_end_node = new Node(fwy_scenario.new_node_id());
        fwy_scenario.scenario.nodes.put(fr_end_node.id,fr_end_node);

        LinkOfframp fr = new LinkOfframp(
                link_id, // id,
                this, // mysegment,
                null, // up_link,
                null, // dn_link,
                fr_start_node.id,// start_node_id,
                fr_end_node.id,// end_node_id,
                params); // params)

        fwy_scenario.scenario.links.put(fr.id,fr);
        fr.up_link = fwy;
        fr_end_node.in_links.add(fr.id);

        return fr;
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

}
