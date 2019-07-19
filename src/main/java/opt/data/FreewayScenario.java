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

    protected String name;

    // similar to the jax scenario, but with extended functionality
    protected Scenario scenario;

    // interface to the ui
    protected Map<Long,Segment> segments = new HashMap<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public FreewayScenario(){}

    public FreewayScenario(String name,jaxb.Lnks jaxb_lnks,jaxb.Sgmts jaxb_segments,jaxb.Scenario jaxb_scenario) throws Exception {

        this.name = name;

        // create Scenario object
        this.scenario = new Scenario(jaxb_scenario);

        // attach link names
        if (jaxb_lnks!=null){
            for(jaxb.Lnk lnk : jaxb_lnks.getLnk()){
                if (scenario.links.containsKey(lnk.getId())){
                    AbstractLink link = scenario.links.get(lnk.getId());
                    if( link.name!=null )
                        throw new Exception("Link named twice");
                    link.name = lnk.getName();
                }
            }
        }

        // create segments
        long max_sgmt_id = 0;
        if(jaxb_segments!=null) {
            for (jaxb.Sgmt sgmt : jaxb_segments.getSgmt()) {
                long sgmt_id = max_sgmt_id++;
                Segment segment = new Segment(this,sgmt_id, sgmt);
                segments.put(sgmt_id,segment);

                // assign segments to links
                if (segment.ml().mysegment!=null)
                    throw new Exception("Link " + segment.ml_id + " assigned to multiple segments");
                segment.ml().mysegment = segment;

                if (segment.has_onramp()){
                    if (segment.or().mysegment!=null)
                        throw new Exception("Link " + segment.or_id + " assigned to multiple segments");
                    segment.or().mysegment = segment;
                }

                if (segment.has_offramp()){
                    if (segment.fr().mysegment!=null)
                        throw new Exception("Link " + segment.fr_id + " assigned to multiple segments");
                    segment.fr().mysegment = segment;
                }

            }
        }

        // segment adjacency mappings ..................................
        for(Segment segment : segments.values()){

            // upstream mainline segment ..............
            Node start_node = scenario.nodes.get(segment.ml().start_node_id);
            Set<Long> segments_ml_up = start_node.in_links.stream()
                    .map(id->scenario.links.get(id))
                    .filter(link->!(link instanceof LinkRamp))
                    .map(link -> link.mysegment.id)
                    .filter(seg->seg!=segment.id)
                    .collect(toSet());

            if (segments_ml_up.size()>1)
                throw new Exception("Level 3 networks has not been implemented");

            if (segments_ml_up.size()==1)
                segment.segment_ml_up_id = segments_ml_up.iterator().next();

            // downstream mainline segment ..............
            Node end_node = scenario.nodes.get(segment.ml().end_node_id);
            Set<Long> segments_ml_dn = end_node.out_links.stream()
                    .map(id->scenario.links.get(id))
                    .filter(link->!(link instanceof LinkRamp))
                    .map(link -> link.mysegment.id)
                    .filter(seg->seg!=segment.id)
                    .collect(toSet());

            if (segments_ml_dn.size()>1)
                throw new Exception("Level 3 networks has not been implemented");

            if (segments_ml_dn.size()==1)
                segment.segment_ml_dn_id = segments_ml_dn.iterator().next();

            // downstream offramp connector segment ...........
            if(segment.has_offramp()){
                end_node = scenario.nodes.get(segment.fr().end_node_id);
                Set<Long> segments_fr_dn = end_node.out_links.stream()
                        .map(id->scenario.links.get(id))
                        .filter(link->link instanceof LinkConnector)
                        .map(link -> link.mysegment.id)
                        .filter(seg->seg!=segment.id)
                        .collect(toSet());

                if (segments_fr_dn.size()>1)
                    throw new Exception("Level 3 networks has not been implemented");

                if (segments_fr_dn.size()==1)
                    segment.segment_fr_dn_id = segments_fr_dn.iterator().next();
            }

            // upstream onramp connector segment ...........
            if(segment.has_onramp()){
                start_node = scenario.nodes.get(segment.or().start_node_id);
                Set<Long> segments_or_up = start_node.in_links.stream()
                        .map(id->scenario.links.get(id))
                        .filter(link->link instanceof LinkConnector)
                        .map(link -> link.mysegment.id)
                        .filter(seg->seg!=segment.id)
                        .collect(toSet());

                if (segments_or_up.size()>1)
                    throw new Exception("Level 3 networks has not been implemented");

                if (segments_or_up.size()==1)
                    segment.segment_or_up_id = segments_or_up.iterator().next();
            }

        }

        // assign demands
        if (jaxb_scenario.getDemands()!=null)
            for(jaxb.Demand dem : jaxb_scenario.getDemands().getDemand()){

                long comm_id = dem.getCommodityId();

                if( !get_commodities().containsKey(comm_id))
                    throw new Exception("Bad commodity id in demand profile");

                if(dem.getLinkId()==null)
                    throw new Exception("Link not specified in demand profile for commodity " + comm_id);

                if(!scenario.links.containsKey(dem.getLinkId()))
                    throw new Exception("Bad link id in demand profile.");

                AbstractLink or = scenario.links.get(dem.getLinkId());

                if(!or.is_source())
                    throw new Exception("Demand assigned to invalid link.");

                Profile1D profile = new Profile1D(
                        dem.getStartTime(),
                        dem.getDt(),
                        OTMUtils.csv2list(dem.getContent()));

                or.get_segment().set_or_demand_vph(profile,comm_id);
            }

        // max ids
        reset_max_ids();

    }

    /**
     * Create a deep copy of a given scenario
     * @return new FreewayScenario object
     */
    public FreewayScenario deep_copy(){
        FreewayScenario scn_cpy = new FreewayScenario();
        scn_cpy.name = name;
        scn_cpy.scenario = scenario.deep_copy();
        scn_cpy.segments = new HashMap<>();
        for(Map.Entry<Long,Segment> e : segments.entrySet())
            scn_cpy.segments.put(e.getKey(),e.getValue().deep_copy(scn_cpy));
        scn_cpy.reset_max_ids();
        return scn_cpy;
    }

    /////////////////////////////////////
    // scenario getters
    /////////////////////////////////////

    /**
     * Get the name of the scenario
     * @return String
     */
    public String get_name(){
        return name;
    }

    public Set<AbstractLink> get_links(){
        return segments.values().stream()
                .flatMap(sgmt->sgmt.get_links().stream())
                .collect(Collectors.toSet());
    }

    /////////////////////////////////////
    // segment getters and delete
    /////////////////////////////////////

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

    /**
     * Delete a segment
     * @param segment
     */
    public void delete_segment(Segment segment) throws Exception {

        if(segments.size()==1)
            throw new Exception("Removing the sole segment is not allowed.");

        if(segment.segment_or_up_id!=null || segment.segment_fr_dn_id!=null)
            throw new Exception("Removing a segment with a ramp connector is not allowed.");

        Node start_node = scenario.nodes.get(segment.ml().start_node_id);
        Node end_node   = scenario.nodes.get(segment.ml().end_node_id);

        // connect upstream mainline segment to end node
        if(segment.segment_ml_up_id!=null){

            Segment segup = segments.get(segment.segment_ml_up_id);
            segup.segment_ml_dn_id = segment.segment_ml_dn_id;

            // ml link
            segup.ml().end_node_id = end_node.id;
            start_node.in_links.remove(segup.ml().id);
            end_node.in_links.add(segup.ml().id);

            if(segup.has_onramp() && segup.or().end_node_id==start_node.id) {
                segup.or().end_node_id = end_node.id;
                start_node.in_links.remove(segup.or().id);
                end_node.in_links.add(segup.or().id);
            }

            if(segup.has_offramp() && segup.fr().start_node_id==start_node.id){
                segup.fr().start_node_id = end_node.id;
                start_node.out_links.remove(segup.fr().id);
                end_node.out_links.add(segup.fr().id);
            }

        }

        // fix downstream mainline segment
        if(segment.segment_ml_dn_id!=null){
            Segment segdn = segments.get(segment.segment_ml_dn_id);
            segdn.segment_ml_up_id = segment.segment_ml_up_id;
        }

        // delete the start node
        scenario.nodes.remove(segment.ml().start_node_id);

        // delete segment links
        segment.delete_offramp();
        segment.delete_onramp();
        scenario.links.remove(segment.ml_id);
        end_node.in_links.remove(segment.ml_id);

        // remove the segment
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
     * Get the commodity for a given id
     * @param name
     * @return Commodity
     */
    public Commodity get_commodity_by_name(String name){
        return scenario.commodities.containsKey(name) ? scenario.commodities.get(name) : null;
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
    // run
    /////////////////////////////////////

    public void run_on_new_thread() throws Exception {
        throw new Exception("NOT IMPLEMENTED!");
    }

    /////////////////////////////////////
    // protected
    /////////////////////////////////////

    protected jaxb.Scn to_jaxb(){
        jaxb.Scn scn = new jaxb.Scn();
        scn.setName(name);

        jaxb.Sgmts sgmts = new jaxb.Sgmts();
        scn.setSgmts(sgmts);
        for(Segment segment : segments.values())
            sgmts.getSgmt().add(segment.to_jaxb());

        jaxb.Lnks lnks = new jaxb.Lnks();
        scn.setLnks(lnks);
        for(AbstractLink link : get_links()){
            jaxb.Lnk lnk = new jaxb.Lnk();
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
