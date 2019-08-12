package opt.data;

import profiles.Profile1D;
import utils.OTMUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class FreewayScenario {

    private Long max_link_id;
    private Long max_node_id;
    private Long max_seg_id;

    public String name;
    protected Scenario scenario;
    protected Map<Long,Segment> segments = new HashMap<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public FreewayScenario() {
    }

    public FreewayScenario(String scnname,String segmentname,String linkname,LinkParameters params){
        this.name = scnname;
        max_link_id = -1l;
        max_node_id = -1l;
        max_seg_id = -1l;
        scenario = new Scenario();
        create_isolated_segment(segmentname,linkname,params, AbstractLink.Type.freeway);
    }

    public FreewayScenario(String name,jaxbopt.Lnks jaxb_lnks,jaxbopt.Sgmts jaxb_segments,jaxb.Scenario jaxb_scenario) throws Exception {

        this.name = name;

        // create Scenario object
        this.scenario = new Scenario(jaxb_scenario);

        // attach link names
        if (jaxb_lnks!=null)
            for(jaxbopt.Lnk lnk : jaxb_lnks.getLnk())
                if (scenario.links.containsKey(lnk.getId()))
                    scenario.links.get(lnk.getId()).name = lnk.getName();

        // create segments
        long max_sgmt_id = 0;
        if(jaxb_segments!=null)
            for (jaxbopt.Sgmt sgmt : jaxb_segments.getSgmt()) {
                Segment segment = new Segment(this,max_sgmt_id++, sgmt);
                segments.put(segment.id,segment);
            }

        // make link connections
        for(AbstractLink abslink : scenario.links.values()){

            Set<AbstractLink> up_links = scenario.nodes.get(abslink.start_node_id).in_links.stream()
                    .map(link_id -> scenario.links.get(link_id))
                    .filter(link -> link.mysegment!=null )
                    .collect(Collectors.toSet());

            Set<AbstractLink> dn_links = scenario.nodes.get(abslink.end_node_id).out_links.stream()
                    .map(link_id -> scenario.links.get(link_id))
                    .filter(link -> link.mysegment!=null )
                    .collect(Collectors.toSet());

            Set<AbstractLink> up_links_f = null;
            Set<AbstractLink> dn_links_f = null;
            switch(abslink.get_type()){

                case freeway:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    break;

                case connector:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkOfframp)
                            .map(link -> (LinkOfframp)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkOnramp)
                            .map(link -> (LinkOnramp)link)
                            .collect(Collectors.toSet());

                    break;

                case onramp:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkConnector)
                            .map(link -> (LinkConnector)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    break;

                case offramp:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkConnector)
                            .map(link -> (LinkConnector)link)
                            .collect(Collectors.toSet());

                    break;
            }

            if(up_links_f!=null && !up_links_f.isEmpty())
                abslink.up_link = up_links_f.iterator().next();

            if(dn_links_f!=null && !dn_links_f.isEmpty())
                abslink.dn_link = dn_links_f.iterator().next();

        }

        // assign demands
        if (jaxb_scenario.getDemands()!=null)
            for(jaxb.Demand dem : jaxb_scenario.getDemands().getDemand())
                scenario.links.get(dem.getLinkId()).set_demand_vph(
                        dem.getCommodityId(),
                        new Profile1D(
                                dem.getStartTime(),
                                dem.getDt(),
                                OTMUtils.csv2list(dem.getContent())));

        // assign splits
        if (jaxb_scenario.getSplits()!=null)
            for(jaxb.SplitNode jsplitnode : jaxb_scenario.getSplits().getSplitNode())
                for(jaxb.Split split : jsplitnode.getSplit())
                    scenario.links.get(split.getLinkOut()).set_split(
                            jsplitnode.getCommodityId(),
                            new Profile1D(
                                    jsplitnode.getStartTime(),
                                    jsplitnode.getDt(),
                                    OTMUtils.csv2list(split.getContent())));

        // max ids
        reset_max_ids();

    }

    public FreewayScenario clone(){
        FreewayScenario scn_cpy = new FreewayScenario();
        scn_cpy.name = name;
        scn_cpy.max_link_id = max_link_id;
        scn_cpy.max_node_id = max_node_id;
        scn_cpy.max_seg_id = max_seg_id;
        scn_cpy.scenario = scenario.clone();
        scn_cpy.segments = new HashMap<>();
        for(Map.Entry<Long,Segment> e : segments.entrySet()) {
            Segment new_segment = e.getValue().clone();
            new_segment.fwy_scenario = scn_cpy;
            scn_cpy.segments.put(e.getKey(),new_segment );
        }

        // make link connections
        for(AbstractLink link : scenario.links.values()){
            AbstractLink new_link = scn_cpy.scenario.links.get(link.id);
            if(link.mysegment!=null)
                new_link.mysegment = scn_cpy.segments.get(link.mysegment.id);
            if(link.up_link!=null)
                new_link.up_link = scn_cpy.scenario.links.get(link.up_link.id);
            if(link.dn_link!=null)
                new_link.dn_link = scn_cpy.scenario.links.get(link.dn_link.id);
        }

        return scn_cpy;
    }

    /////////////////////////////////////
    // scenario getters
    /////////////////////////////////////

    public List<AbstractLink> get_links(){
        return segments.values().stream()
                .flatMap(sgmt->sgmt.get_links().stream())
                .collect(Collectors.toList());
    }

    public Map<Long,Node> get_nodes(){
        return scenario.nodes;
    }

    /////////////////////////////////////
    // segment getters
    /////////////////////////////////////

    /**
     * get all segments in this scenario
     * @return Collection of segments
     */
    public List<List <Segment>> get_segments_tree(){
        List<List <Segment>> result = new ArrayList<>();

//        Set<Segment> source_segments = this.segments.values().stream()
//                .map(s->s.fwy)
//                .filter(link->link.is_source())
//                .map(link->link.mysegment)
//                .collect(toSet());
//
//        Set<Long> all_segment_ids = new HashSet<>();
//        all_segment_ids.addAll(segments.keySet());
//
//        for(Segment source_segment : source_segments){
//            List<Segment> thisfreeway = new ArrayList<>();
//            Segment curr_segment = source_segment;
//            thisfreeway.add(curr_segment);
//            all_segment_ids.remove(curr_segment.id);
//            if (curr_segment.segment_fr_dn_id!=null){
//                thisfreeway.add(segments.get(curr_segment.segment_fr_dn_id));
//                all_segment_ids.remove(curr_segment.segment_fr_dn_id);
//            }
//            while(curr_segment.segment_fwy_dn_id!=null){
//                curr_segment = segments.get(curr_segment.segment_fwy_dn_id);
//                thisfreeway.add(curr_segment);
//                all_segment_ids.remove(curr_segment.id);
//                if (curr_segment.segment_fr_dn_id!=null){
//                    thisfreeway.add(segments.get(curr_segment.segment_fr_dn_id));
//                    all_segment_ids.remove(curr_segment.segment_fr_dn_id);
//                }
//            }
//            result.add(thisfreeway);
//        }
//
//        assert(all_segment_ids.isEmpty());
//
//        result.sort(Comparator.comparing(List::size));

        return result;
    }

    public List<List<AbstractLink>> get_links_tree(){
        List<List <Segment>> segments_tree = get_segments_tree();
        List<List<AbstractLink>> result = new ArrayList<>();
        for(List<Segment> seg_list : segments_tree){
            List<AbstractLink> fwy_links = new ArrayList<>();
            result.add(fwy_links);
            for(Segment segment : seg_list)
                fwy_links.addAll(segment.get_links().stream().filter(x->x!=null).collect(Collectors.toList()));
        }
        return result;
    }

    /**
     * get all segments in this scenario
     * @return Collection of segments
     */
    public Set<Segment> get_segments(){
        return new HashSet<>(segments.values());
    }

    /**
     * Get an order list of the names of segments
     * @return
     */
    public Set<String> get_segment_names(){
        return segments.values().stream().map(segment->segment.name).collect(Collectors.toSet());
    }

    public Segment get_segment_by_name(String name){
        Set<Segment> xsegments = segments.values().stream()
                .filter(seg->seg.name.equals(name))
                .collect(toSet());
        return xsegments.size()==0 ? null : xsegments.iterator().next();
    }

    /**
     * Get a segment with its id
     * @return
     */
    public Segment get_segment_with_id(Long id){
        return segments.containsKey(id) ? segments.get(id) : null;
    }

    /////////////////////////////////////
    // segment create / delete
    /////////////////////////////////////

    /**
     * Create an isolated segment
     * @return A new segment
     */
    public Segment create_isolated_segment(String segment_name,String linkname,LinkParameters params,AbstractLink.Type linktype){

        if(linktype!=AbstractLink.Type.freeway && linktype!= AbstractLink.Type.connector)
            return null;

        // create a segment
        Long segment_id = ++max_seg_id;
        Segment segment = new Segment();
        segment.id = segment_id;
        segment.name = segment_name;
        segment.fwy_scenario = this;
        segments.put(segment_id,segment);

        // create nodes and freeway link
        Node start_node = new Node(++max_node_id);
        Node end_node = new Node(++max_node_id);

        LinkFreewayOrConnector link = null;
        switch(linktype){
            case freeway:

                link = new LinkFreeway(
                        ++max_link_id,
                        linkname,
                        start_node.id,end_node.id,
                        1,
                        0,
                        0,
                        500f,
                        params.capacity_vphpl,params.jam_density_vpkpl,params.ff_speed_kph,
                        segment);
                break;
            case connector:
                link = new LinkConnector(
                        ++max_link_id,
                        linkname,
                        start_node.id,end_node.id,
                        1,
                        0,
                        0,
                        500f,
                        params.capacity_vphpl,params.jam_density_vpkpl,params.ff_speed_kph,
                        segment);
                break;
        }

        start_node.out_links.add(link.id);
        end_node.in_links.add(link.id);

        segment.fwy = link;

        // add to the scenario
        scenario.nodes.put(start_node.id,start_node);
        scenario.nodes.put(end_node.id,end_node);
        scenario.links.put(link.id,link);

        return segment;
    }

    /**
     * Delete a segment
     * @param segment
     */
    public void delete_segment(Segment segment) throws Exception {

        if(segments.size()==1)
            throw new Exception("Removing the sole segment is not allowed.");

        // disconnect ramps from connectors, or delete nodes
        // delete links
        for(LinkOnramp or : segment.get_ors())
            if (or.up_link == null)
                scenario.nodes.remove(or.start_node_id);
            else {
                or.up_link.dn_link = null;
                scenario.nodes.get(or.up_link.end_node_id).out_links.remove(or.id);
            }

        for(LinkOfframp fr : segment.get_frs())
            if (fr.dn_link == null)
                scenario.nodes.remove(fr.end_node_id);
            else {
                fr.dn_link.up_link = null;
                scenario.nodes.get(fr.dn_link.start_node_id).in_links.remove(fr.id);
            }

        // disconnect fwy, or delete nodes
        if(segment.fwy.up_link==null)
            scenario.nodes.remove(segment.fwy.start_node_id);
        else {
            segment.fwy.up_link.dn_link = null;
            scenario.nodes.get(segment.fwy.up_link.end_node_id).out_links.remove(segment.fwy.id);
        }

        if(segment.fwy.dn_link==null)
            scenario.nodes.remove(segment.fwy.end_node_id);
        else {
            segment.fwy.dn_link.up_link = null;
            scenario.nodes.get(segment.fwy.dn_link.start_node_id).in_links.remove(segment.fwy.id);
        }

        // delete all links
        for(AbstractLink link : segment.get_links()) {
            link.demands = null;
            link.splits = null;
            scenario.links.remove(link.id);
        }

        // delete the segment
        segment.in_ors = null;
        segment.out_ors = null;
        segment.in_frs = null;
        segment.out_frs = null;
        segments.remove(segment.id);

    }

    /////////////////////////////////////
    // commodity getters and setters
    /////////////////////////////////////

    /**
     * Retrieve a map of id -> commodity
     * @return Map<String,Commodity>
     */
    public Map<Long, Commodity> get_commodities(){
        return scenario.commodities;
    }

    /**
     * Get the commodity for a given name
     * @param name
     * @return Commodity or null
     */
    public Commodity get_commodity_by_name(String name){
        Optional<Commodity> x = scenario.commodities.values().stream()
                .filter(c->c.name.equals(name))
                .findFirst();
        return x.isPresent() ? x.get() : null;
    }

    /**
     * Create a new commodity
     * @param name
     * @return
     */
    public Commodity create_commodity(String name){
        long max_id;
        if(scenario.commodities.isEmpty())
            max_id = 0;
        else
            max_id = scenario.commodities.values().stream().mapToLong(x->x.id).max().getAsLong() + 1;

        Commodity new_comm = new Commodity(max_id,name);
        scenario.commodities.put(new_comm.id,new_comm);
        return new_comm;
    }

    /////////////////////////////////////
    // utilities
    /////////////////////////////////////

    public boolean is_valid_segment_name(String name){
        return !segments.values().stream().anyMatch(seg->seg.name.equals(name));
    }

    public boolean is_valid_link_name(String name){
        return !scenario.links.values().stream()
                .anyMatch(link->link.name!=null && link.name.equals(name));
    }

    /////////////////////////////////////
    // run
    /////////////////////////////////////

    public void run_on_new_thread() throws Exception {
        throw new Exception("NOT IMPLEMENTED!");
    }

    /////////////////////////////////////
    // protected and private
    /////////////////////////////////////

    protected jaxbopt.Scn to_jaxb(){
        jaxbopt.Scn scn = new jaxbopt.Scn();
        scn.setName(name);

        jaxbopt.Sgmts sgmts = new jaxbopt.Sgmts();
        scn.setSgmts(sgmts);
        for(Segment segment : segments.values())
            sgmts.getSgmt().add(segment.to_jaxb());

        jaxbopt.Lnks lnks = new jaxbopt.Lnks();
        scn.setLnks(lnks);
        List<AbstractLink> all_links = get_links();
        Collections.sort(all_links);
        for(AbstractLink link : all_links ){
            if(link==null)
                continue;
            jaxbopt.Lnk lnk = new jaxbopt.Lnk();
            lnk.setId(link.id);
            lnk.setName(link.name);
            lnks.getLnk().add(lnk);
        }
        return scn;
    }

    protected AbstractLink get_link(Long id){
        return scenario.links.get(id);
    }

    protected void reset_max_ids(){

        // link
        Optional<Long> opt_max_link_id = scenario.links.keySet().stream()
                .max(Comparator.comparing(Long::valueOf));
        max_link_id = opt_max_link_id.isPresent() ? opt_max_link_id.get() : 0l;

        // node
        Optional<Long> opt_max_node_id = scenario.nodes.keySet().stream()
                .max(Comparator.comparing(Long::valueOf));
        max_node_id = opt_max_node_id.isPresent() ? opt_max_node_id.get() : 0l;

        // segment
        Optional<Long> opt_max_seg_id = segments.keySet().stream()
                .max(Comparator.comparing(Long::valueOf));
        max_seg_id = opt_max_seg_id.isPresent() ? opt_max_seg_id.get() : 0l;

    }

    protected long new_link_id(){
        return ++max_link_id;
    }

    protected long new_node_id(){
        return ++max_node_id;
    }

    protected long new_seg_id(){
        return ++max_seg_id;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FreewayScenario that = (FreewayScenario) o;
        return name.equals(that.name) &&
                scenario.equals(that.scenario) &&
                segments.equals(that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, scenario, segments);
    }
}
