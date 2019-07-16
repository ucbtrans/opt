package opt.data;

import commodity.Commodity;
import commodity.Path;
import commodity.Subnetwork;
import common.Link;
import common.Network;
import error.OTMException;
import profiles.Profile1D;
import utils.OTMUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class FreewayScenario {

    private Long max_link_id;
    private Long max_node_id;

    protected jScenario jscenario;
    protected List<Segment> segments = new ArrayList<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public FreewayScenario(){}

    public FreewayScenario(jaxb.Scenario jaxb_scenario, String [] segment_names) throws Exception {
        this.jscenario = new jScenario(jaxb_scenario);

        // get upstream mainline source
        jLink ml_link = jscenario.get_ml_source();

        Set<jLink> all_links = new HashSet<>(jscenario.links.values());

        while (true){

            jNode start_node = jscenario.nodes.get(ml_link.start_node_id);
            jNode end_node = jscenario.nodes.get(ml_link.end_node_id);

            // get offramp ........................
            Set<jLink> offramps = start_node.out_links.stream()
                    .filter(link->link.is_ramp)
                    .collect(toSet());

            if(offramps.size()>1)
                throw new Exception("Multiple offramps on a single node.");

            jLink offramp = offramps.isEmpty() ? null : offramps.iterator().next() ;

            // get onramp ........................
            Set<jLink> onramps = end_node.in_links.stream()
                    .filter(link->link.is_source && link.is_ramp)
                    .collect(toSet());

            if(onramps.size()>1)
                throw new Exception("Multiple onramps on a single node.");

            jLink onramp = onramps.isEmpty() ? null : onramps.iterator().next();

            // create the segment ................
            segments.add( new Segment(this,onramp,ml_link,offramp) );

            // remove from list ...................
            all_links.remove(ml_link);
            if(onramp!=null)
                all_links.remove(onramp);
            if(offramp!=null)
                all_links.remove(offramp);

            // next link .........................
            Set<jLink> dn_links = end_node.out_links.stream()
                    .filter(link->link.is_mainline)
                    .collect(toSet());

            if(dn_links.size()>1)
                throw new Exception("No mainline bifurcations allowed.");

            ml_link = dn_links.isEmpty() ? null : dn_links.iterator().next() ;

            // break conditions................
            if(all_links.isEmpty() || ml_link==null)
                break;
        }

        // assign names
        if(segment_names!=null && segment_names.length!=segments.size())
            throw new Exception("The defined segment names do not cover all segments");

        for(int i=0;i<segments.size();i++)
            segments.get(i).set_name(segment_names==null ? String.format("segment %d",i) : segment_names[i]);

        // assign demands
        for(jaxb.Demand dem : jaxb_scenario.getDemands().getDemand()){

            long comm_id = dem.getCommodityId();

            if( !get_commodities().containsKey(comm_id))
                throw new Exception("Bad commodity id in demand profile");

            if(dem.getLinkId()==null)
                throw new Exception("Link not specified in demand profile for commodity " + comm_id);

            Link or = network.links.get(jd.getLinkId());
            if(link==null)
                throw new OTMException("Bad link id (" + jd.getLinkId() + ") in demand for commodity " + comm.getId());


            this.link = link;
            this.path = null;
            this.commodity = commodity;

            // create a source and add it to the origin
            Link origin = get_origin();
            source = origin.model.create_source(origin,this,commodity,null);
            origin.sources.add(source);

            // assume the content to be given in veh/hr
            profile = new Profile1D(start_time,dt,values);
            profile.multiply(1.0/3600.0);
        }

        // max ids
        reset_max_ids();

    }

    /////////////////////////////////////
    // segment getters
    /////////////////////////////////////

    /**
     * Number of segments in this scenario
     * @return integer number of segments.
     */
    public int get_num_segments(){
        return segments.size();
    }

    /**
     * Get segment by index.
     * @param i index in [0...num segments-1]
     * @return segment object if the index is valid. null otherwise.
     */
    public Segment get_segment(int i){
        return i<0 || i>=get_num_segments() ? null : segments.get(i);
    }

    /**
     * Get an order list of the names of segments
     * @return
     */
    public List<String> get_segment_names(){
        return segments.stream().map(segment->segment.name).collect(Collectors.toList());
    }


    /////////////////////////////////////
    // commodity getters and setters
    /////////////////////////////////////

    /**
     * Retrieve a map of id -> commodity
     * @return Map<Long,jCommodity>
     */
    public Map<Long,jCommodity> get_commodities(){
        return jscenario.commodities;
    }

    /**
     * Get the commodity for a given id
     * @param id
     * @return jCommodity
     */
    public jCommodity get_commodity_by_id(long id){
        return jscenario.commodities.containsKey(id) ? jscenario.commodities.get(id) : null;
    }

    /**
     * Create a new commodity
     * @param name
     * @return
     */
    public jCommodity create_commodity(String name){
        long max_id;
        if(jscenario.commodities.isEmpty())
            max_id = 0;
        else
            max_id = jscenario.commodities.keySet().stream().mapToLong(x->x).max().getAsLong() + 1;

        jCommodity new_comm = new jCommodity(max_id,name);
        jscenario.commodities.put(max_id,new_comm);
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

        assert(dn_index>=0 && dn_index<segments.size());

        Segment dn_segment = segments.get(dn_index);
        Segment up_segment = dn_index>0 ? segments.get(dn_index-1) : null;

        jLink dn_ml = dn_segment.get_ml();

        jNode start_node = new jNode(new_node_id());
        jscenario.nodes.put(start_node.id,start_node);

        jNode end_node = jscenario.nodes.get(dn_ml.start_node_id);

        jLink ml = new jLink(new_link_id(),start_node.id,end_node.id,dn_ml.full_lanes,dn_ml.length,
                true, false, dn_index==0, dn_ml.capacity_vphpl, dn_ml.jam_density_vpkpl,
                dn_ml.ff_speed_kph);
        jscenario.links.put(ml.id,ml);

        Segment new_segment = new Segment(this,null,ml,null);
        this.segments.add(dn_index,new_segment);

        // fix adjacent segments ..............

        // dn_segment is no longer a source
        dn_segment.get_ml().is_source = false;

        // up_segment's end node = start_node
        if(up_segment!=null)
            up_segment.set_end_node(start_node.id);

        return new_segment;
    }

    /**
     * Create a new segment downstream of a given index.
     * Caution: This method alters the indices of all segments downstream of the new segment.
     * @param up_index integer in [0,num segments-1]
     */
    public Segment insert_segment_downstream_from_index(int up_index) throws Exception {

        assert(up_index>=0 && up_index<segments.size());

        Segment up_segment = segments.get(up_index);
        Segment dn_segment = up_index<segments.size()-1 ? segments.get(up_index+1) : null;

        jLink up_ml = up_segment.get_ml();

        jNode end_node = new jNode(new_node_id());
        jscenario.nodes.put(end_node.id,end_node);

        jNode start_node = jscenario.nodes.get(up_ml.end_node_id);

        jLink ml = new jLink(new_link_id(),start_node.id,end_node.id,up_ml.full_lanes,up_ml.length,
                true, false, false, up_ml.capacity_vphpl, up_ml.jam_density_vpkpl,
                up_ml.ff_speed_kph);
        jscenario.links.put(ml.id,ml);

        Segment new_segment = new Segment(this,null,ml,null);
        this.segments.add(up_index+1,new_segment);

        // fix adjacent segments ..............

        // dn_segment's start node = end_node
        if(dn_segment!=null)
            dn_segment.set_start_node(end_node.id);

        return new_segment;
    }

    /**
     * Delete the segment at the given index
     * @param index
     */
    public void delete_segment(int index) throws Exception {

        if(index<0 || index>=segments.size())
            throw new Exception("Out of bounds index.");

        if(get_num_segments()==1)
            throw new Exception("Removing the sole segment is not allowed.");

        Segment segment = segments.get(index);
        segment.delete_offramp();
        segment.delete_onramp();
        jscenario.links.remove(segment.get_ml().id);

        // fix adjacent segments
        if(index==0)
            // make the 1st segment a source
            segments.get(1).get_ml().is_source = true;
        else
            // attach upstream segment to the end node
            segments.get(index-1).set_end_node(segment.get_ml().end_node_id);


        // remove the start node
        jscenario.nodes.remove(segment.get_ml().start_node_id);

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

        for(Map.Entry<Long,jCommodity> e : this.get_commodities().entrySet()){
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
        for(jNode node : jscenario.nodes.values()) {
            jaxb.Node jaxbNode = new jaxb.Node();
            jaxbNode.setId(node.id);
            jNodes.getNode().add(jaxbNode);
        }

        jaxb.Roadparams jRoadParams = new jaxb.Roadparams();
        jNet.setRoadparams(jRoadParams);
        Set<jRoadParam> road_params = jscenario.get_road_params();
        for(jRoadParam jrp : road_params){
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
        for(jLink link : jscenario.links.values()){
            jaxb.Link jaxbLink = new jaxb.Link();
            jaxbLink.setId(link.id);
            jaxbLink.setLength(link.length);
            jaxbLink.setFullLanes(link.full_lanes);
            jaxbLink.setEndNodeId(link.end_node_id);
            jaxbLink.setStartNodeId(link.start_node_id);

            if(link.is_mainline)
                jaxbLink.setRoadType("mainline");
            if(link.is_ramp)
                jaxbLink.setRoadType("ramp");

            // road params
            jRoadParam link_rp = new jRoadParam(link.capacity_vphpl,link.ff_speed_kph,link.jam_density_vpkpl);
            long rp_id = road_params.stream().filter(rp->rp.equals(link_rp)).findFirst().get().id;

            jaxbLink.setRoadparam(rp_id);
            jLinks.getLink().add(jaxbLink);
        }

        return jScn;
    }

    protected void reset_max_ids(){
        max_link_id = jscenario.links.keySet().stream()
                .max(Comparator.comparing(Long::valueOf))
                .get();

        max_node_id = jscenario.nodes.keySet().stream()
                .max(Comparator.comparing(Long::valueOf))
                .get();
    }

    protected long new_link_id(){
        return ++max_link_id;
    }

    protected long new_node_id(){
        return ++max_node_id;
    }



}
