package opt.data;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class FreewayScenario {

    private Long max_link_id;
    private Long max_node_id;

    protected String name;

    // similar to the jax scenario, but with extended functionality
    protected Scenario scenario;

    // interface to the ui
    protected Map<Long,Segment> segments = new HashMap<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public FreewayScenario(){}

    public FreewayScenario(String name,jaxb.Sgmts jaxb_segments,jaxb.Scenario jaxb_scenario) throws Exception {

        this.name = name;

        // create Scenario object
        this.scenario = new Scenario(jaxb_scenario);

        // create FreewaySegment network
        long max_sgmt_id = 0;
        if(jaxb_segments!=null) {
            for (jaxb.Sgmt jaxb_sgmt : jaxb_segments.getSgmt()) {
                long sgmt_id = max_sgmt_id++;
                Segment segment = new Segment(this,sgmt_id, jaxb_sgmt);
                segments.put(sgmt_id,segment);

                // assign segments to links
                if (segment.ml.mysegment!=null)
                    throw new Exception("Link " + segment.ml.id + " assigned to multiple segments");
                segment.ml.mysegment = segment;

                if (segment.or!=null){
                    if (segment.or.mysegment!=null)
                        throw new Exception("Link " + segment.or.id + " assigned to multiple segments");
                    segment.or.mysegment = segment;
                }

                if (segment.fr!=null){
                    if (segment.fr.mysegment!=null)
                        throw new Exception("Link " + segment.fr.id + " assigned to multiple segments");
                    segment.fr.mysegment = segment;
                }

            }
        }

        // segment adjacency mappings ..................................
        for(Segment segment : segments.values()){

            // upstream segments
            Node start_node = scenario.nodes.get(segment.ml.start_node_id);
            Set<Segment> upstream_segments = start_node.in_links.stream()
                    .filter(link->!(link instanceof LinkRamp))
                    .map(link -> link.mysegment)
                    .filter(seg->seg!=segment)
                    .collect(toSet());

            if (upstream_segments.size()>1)
                throw new Exception("Level 3 networks has not been implemented");

            if (upstream_segments.size()==1)
                segment.upstrm_segment = upstream_segments.iterator().next();

            // downstream segments
            Node end_node = scenario.nodes.get(segment.ml.end_node_id);
            Set<Segment> downstream_segments = end_node.out_links.stream()
                    .filter(link->!(link instanceof LinkRamp))
                    .map(link -> link.mysegment)
                    .filter(seg->seg!=segment)
                    .collect(toSet());

            if (downstream_segments.size()>1)
                throw new Exception("Level 3 networks has not been implemented");

            if (downstream_segments.size()==1)
                segment.dnstrm_segment = downstream_segments.iterator().next();

            System.out.println("WARNING: Network class II mappings have not been implemented.");
        }


//        // assign demands
//        if (jaxb_scenario.getDemands()!=null)
//            for(jaxb.Demand dem : jaxb_scenario.getDemands().getDemand()){
//
//                System.out.println("WARNING: DEMANDS NOT IMPLEMENTED");
//
//    //            long comm_id = dem.getCommodityId();
//    //
//    //            if( !get_commodities().containsKey(comm_id))
//    //                throw new Exception("Bad commodity id in demand profile");
//    //
//    //            if(dem.getLinkId()==null)
//    //                throw new Exception("AbstractLink not specified in demand profile for commodity " + comm_id);
//    //
//    //            AbstractLink or = network.links.get(jd.getLinkId());
//    //            if(link==null)
//    //                throw new OTMException("Bad link id (" + jd.getLinkId() + ") in demand for commodity " + comm.getId());
//    //
//    //
//    //            this.link = link;
//    //            this.path = null;
//    //            this.commodity = commodity;
//    //
//    //            // create a source and add it to the origin
//    //            AbstractLink origin = get_origin();
//    //            source = origin.model.create_source(origin,this,commodity,null);
//    //            origin.sources.add(source);
//    //
//    //            // assume the content to be given in veh/hr
//    //            profile = new Profile1D(start_time,dt,values);
//    //            profile.multiply(1.0/3600.0);
//            }

        // max ids
        reset_max_ids();

    }

    /////////////////////////////////////
    // getters
    /////////////////////////////////////

//    /**
//     * Get the name of the scenario
//     * @return String
//     */
//    public String get_name(){
//        return name;
//    }

    /**
     * get all segments in this scenario
     * @return Collection of segments
     */
    public Set<Segment> get_segments(){
        return new HashSet<>(segments.values());
    }

//    /**
//     * Get an order list of the names of segments
//     * @return
//     */
//    public List<String> get_segment_names(){
//        return segments.values().stream().map(segment->segment.name).collect(Collectors.toList());
//    }

    public Set<AbstractLink> get_links(){
        return segments.values().stream()
                .flatMap(sgmt->sgmt.get_links().stream())
                .collect(Collectors.toSet());
    }

    /////////////////////////////////////
    // commodity getters and setters
    /////////////////////////////////////

    /**
     * Retrieve a map of id -> commodity
     * @return Map<Long,Commodity>
     */
    public Map<Long, Commodity> get_commodities(){
        return scenario.commodities;
    }

    /**
     * Get the commodity for a given id
     * @param id
     * @return Commodity
     */
    public Commodity get_commodity_by_id(long id){
        return scenario.commodities.containsKey(id) ? scenario.commodities.get(id) : null;
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
            max_id = scenario.commodities.keySet().stream().mapToLong(x->x).max().getAsLong() + 1;

        Commodity new_comm = new Commodity(max_id,name);
        scenario.commodities.put(max_id,new_comm);
        return new_comm;
    }

    /////////////////////////////////////
    // modify
    /////////////////////////////////////

    /**
     * Create a new segment upstream of a given index
     * Caution: This method alters the indices of all segments downstream of the new segment.
     * @param dn_index integer in [0,num segments-1]
     */
    public Segment insert_segment_upstream_from_index(int dn_index) throws Exception {
        throw new Exception("NOT IMPLEMENTED!");

//        assert(dn_index>=0 && dn_index<segments.size());
//
//        Segment dn_segment = segments.get(dn_index);
//        Segment up_segment = dn_index>0 ? segments.get(dn_index-1) : null;
//
//        AbstractLink dn_ml = dn_segment.get_ml();
//
//        Node start_node = new Node(new_node_id());
//        scenario.nodes.put(start_node.id,start_node);
//
//        Node end_node = scenario.nodes.get(dn_ml.start_node_id);
//
//        AbstractLink ml = new AbstractLink(new_link_id(),start_node.id,end_node.id,dn_ml.full_lanes,dn_ml.length_meters,
//                true, false, dn_index==0, dn_ml.capacity_vphpl, dn_ml.jam_density_vpkpl,
//                dn_ml.ff_speed_kph);
//        scenario.links.put(ml.id,ml);
//
//        Segment new_segment = new Segment(this,null,ml,null);
//        this.segments.add(dn_index,new_segment);
//
//        // fix adjacent segments ..............
//
//        // dn_segment is no longer a source
//        dn_segment.get_ml().is_source = false;
//
//        // up_segment's end node = start_node
//        if(up_segment!=null)
//            up_segment.set_end_node(start_node.id);
//
//        return new_segment;
    }

    /**
     * Create a new segment downstream of a given index.
     * Caution: This method alters the indices of all segments downstream of the new segment.
     * @param up_index integer in [0,num segments-1]
     */
    public Segment insert_segment_downstream_from_index(int up_index) throws Exception {
        throw new Exception("NOT IMPLEMENTED!");

//        assert(up_index>=0 && up_index<segments.size());
//
//        Segment up_segment = segments.get(up_index);
//        Segment dn_segment = up_index<segments.size()-1 ? segments.get(up_index+1) : null;
//
//        AbstractLink up_ml = up_segment.get_ml();
//
//        Node end_node = new Node(new_node_id());
//        scenario.nodes.put(end_node.id,end_node);
//
//        Node start_node = scenario.nodes.get(up_ml.end_node_id);
//
//        AbstractLink ml = new AbstractLink(new_link_id(),start_node.id,end_node.id,up_ml.full_lanes,up_ml.length_meters,
//                true, false, false, up_ml.capacity_vphpl, up_ml.jam_density_vpkpl,
//                up_ml.ff_speed_kph);
//        scenario.links.put(ml.id,ml);
//
//        Segment new_segment = new Segment(this,null,ml,null);
//        this.segments.add(up_index+1,new_segment);
//
//        // fix adjacent segments ..............
//
//        // dn_segment's start node = end_node
//        if(dn_segment!=null)
//            dn_segment.set_start_node(end_node.id);
//
//        return new_segment;
    }

    /**
     * Delete the segment at the given index
     * @param index
     */
    public void delete_segment(int index) throws Exception {

        if(index<0 || index>=segments.size())
            throw new Exception("Out of bounds index.");

        if(segments.size()==1)
            throw new Exception("Removing the sole segment is not allowed.");

        Segment segment = segments.get(index);
        segment.delete_offramp();
        segment.delete_onramp();
        scenario.links.remove(segment.get_ml().id);

        // fix adjacent segments
        if(index==0)
            // make the 1st segment a source
            segments.get(1).get_ml().is_source = true;
        else
            // attach upstream segment to the end node
            segments.get(index-1).set_end_node(segment.get_ml().end_node_id);


        // remove the start node
        scenario.nodes.remove(segment.get_ml().start_node_id);

        // remove the segment
        segments.remove(index);
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

    public jaxb.Scenario to_jaxb(){
        jaxb.Scenario jScn = new jaxb.Scenario();

        // commodities
        jaxb.Commodities jComms = new jaxb.Commodities();
        jScn.setCommodities(jComms);

        for(Map.Entry<Long, Commodity> e : this.get_commodities().entrySet()){
            jaxb.Commodity jcomm = new jaxb.Commodity();
            jComms.getCommodity().add(jcomm);
            jcomm.setId(e.getKey());
            jcomm.setName(e.getValue().name);
            jcomm.setPathfull(false);
        }

        // network
        jaxb.Network jNet = new jaxb.Network();
        jScn.setNetwork(jNet);

        // nodes
        jaxb.Nodes jNodes = new jaxb.Nodes();
        jNet.setNodes(jNodes);
        for(Node node : scenario.nodes.values()) {
            jaxb.Node jaxbNode = new jaxb.Node();
            jaxbNode.setId(node.id);
            jNodes.getNode().add(jaxbNode);
        }

        jaxb.Roadparams jRoadParams = new jaxb.Roadparams();
        jNet.setRoadparams(jRoadParams);
        Set<RoadParam> road_params = scenario.get_road_params();
        for(RoadParam jrp : road_params){
            jaxb.Roadparam jaxbrp = new jaxb.Roadparam();
            jaxbrp.setId(jrp.id);
            jaxbrp.setCapacity(jrp.capacity);
            jaxbrp.setSpeed(jrp.speed);
            jaxbrp.setJamDensity(jrp.jam_density);
            jRoadParams.getRoadparam().add(jaxbrp);
        }

        // links
        jaxb.Links jLinks = new jaxb.Links();
        jNet.setLinks(jLinks);
        for(AbstractLink link : scenario.links.values()){
            jaxb.Link jaxbLink = new jaxb.Link();
            jaxbLink.setId(link.id);
            jaxbLink.setLength(link.length_meters);
            jaxbLink.setFullLanes(link.full_lanes);
            jaxbLink.setEndNodeId(link.end_node_id);
            jaxbLink.setStartNodeId(link.start_node_id);

            if(link instanceof LinkMainline)
                jaxbLink.setRoadType("mainline");
            if(link instanceof LinkRamp)
                jaxbLink.setRoadType("ramp");

            // road params
            RoadParam link_rp = new RoadParam(link.capacity_vphpl,link.ff_speed_kph,link.jam_density_vpkpl);
            long rp_id = road_params.stream().filter(rp->rp.equals(link_rp)).findFirst().get().id;

            jaxbLink.setRoadparam(rp_id);
            jLinks.getLink().add(jaxbLink);
        }

        return jScn;
    }

    protected void reset_max_ids(){

        Optional<Long> opt_max_link_id = scenario.links.keySet().stream()
                .max(Comparator.comparing(Long::valueOf));

        max_link_id = opt_max_link_id.isPresent() ? opt_max_link_id.get() : 0l;

        Optional<Long> opt_max_node_id = scenario.nodes.keySet().stream()
                .max(Comparator.comparing(Long::valueOf));

        max_node_id = opt_max_node_id.isPresent() ? opt_max_node_id.get() : 0l;
    }

    protected long new_link_id(){
        return ++max_link_id;
    }

    protected long new_node_id(){
        return ++max_node_id;
    }



}
