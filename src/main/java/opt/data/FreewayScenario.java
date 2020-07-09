package opt.data;

import opt.UserSettings;
import opt.data.control.*;
import profiles.Profile1D;
import utils.OTMUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class FreewayScenario {

    private Long max_link_id;
    private Long max_node_id;
    private Long max_seg_id;
    private Long max_controller_id;
    private Long max_sensor_id;
    private Long max_actuator_id;
    private Long max_rc_id;

    public String name;
    public String description;
    protected Scenario scenario;
    protected Map<Long,Segment> segments = new HashMap<>();
    protected Map<Long, Route> routes = new HashMap<>();

    // simulation parameters
    protected float sim_start_time = 0f;
    protected float sim_duration = 86400f;
    protected float sim_dt = 2f;

    protected GhostPieces ghost_pieces;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public FreewayScenario() {
    }

    public FreewayScenario(String scnname,String description, String segmentname, ParametersFreeway params){
        reset_max_ids();
        this.name = scnname;
        this.description = description;
        scenario = new Scenario(this);
        create_isolated_segment(segmentname,params, AbstractLink.Type.freeway);
        scenario.commodities.put(0l,new Commodity(0l,"Unnamed commodity",1f));
        reset_max_ids();
    }

    public FreewayScenario(String name,String description,jaxb.Sim sim,jaxb.Lnks jaxb_lnks,jaxb.Sgmts jaxb_segments,jaxb.Routes jaxb_routes,jaxb.Scenario jaxb_scenario) throws Exception {

        reset_max_ids();

        this.name = name;
        this.description = description;

        if(sim!=null){
            this.sim_start_time = sim.getStarttime();
            this.sim_duration = sim.getDuration();
        }

        // create Scenario object
        this.scenario = new Scenario(this,jaxb_scenario);

        // attach link names
        if (jaxb_lnks!=null)
            for(jaxb.Lnk lnk : jaxb_lnks.getLnk())
                if (scenario.links.containsKey(lnk.getId())) {
                    AbstractLink link = scenario.links.get(lnk.getId());
                    link.set_name(lnk.getName());

                    // TODO REMOVE THESE AFTER UPDATING EXISTING FILES.
                    if(!link.has_mng()){
                        link.set_mng_lanes(lnk.getManagedLanes()==null ? 0 : lnk.getManagedLanes().intValue());
                        link.set_mng_barrier(lnk.isManagedLanesBarrier()==null ? false : lnk.isManagedLanesBarrier());
                        link.set_mng_separated(lnk.isManagedLanesSeparated()==null ? false : lnk.isManagedLanesSeparated());
                    }
                    if(!link.has_aux()){
                        link.set_aux_lanes(lnk.getAuxLanes()==null ? 0 : lnk.getAuxLanes().intValue());
                    }
                    link.set_is_inner(lnk.isIsInner()==null ? false : lnk.isIsInner());

                    // TODO REMOVE THIS
                    // HACK SET MNG AND AUX LANE PARAMETERS IF THEY ARE NOT SET
                    // ---------------------------------------------------------------------------
                    if(link.has_mng()){
                        if(link.params.mng_fd==null)
                            link.params.mng_fd = new FDparams(
                                    (float)UserSettings.defaultManagedLaneCapacityVph,
                                    (float)UserSettings.defaultManagedLaneJamDensityVpk,
                                    (float)UserSettings.defaultManagedLaneFreeFlowSpeedKph);
                        if(Float.isNaN(link.params.mng_fd.capacity_vphpl))
                            link.params.mng_fd.capacity_vphpl = (float)UserSettings.defaultManagedLaneCapacityVph;
                        if(Float.isNaN(link.params.mng_fd.jam_density_vpkpl))
                            link.params.mng_fd.jam_density_vpkpl = (float)UserSettings.defaultManagedLaneJamDensityVpk;
                        if(Float.isNaN(link.params.mng_fd.ff_speed_kph))
                            link.params.mng_fd.ff_speed_kph = (float)UserSettings.defaultManagedLaneFreeFlowSpeedKph;
                    }
                    if(link.has_aux()){
                        FDparams aux_fd = ((ParametersFreeway) link.params).aux_fd;
                        if(aux_fd==null)
                            aux_fd = new FDparams(
                                    (float)UserSettings.defaultAuxLaneCapacityVph,
                                    (float)UserSettings.defaultAuxLaneJamDensityVpk,
                                    (float)UserSettings.defaultAuxLaneFreeFlowSpeedKph);
                        if(Float.isNaN(aux_fd.capacity_vphpl))
                            aux_fd.capacity_vphpl = (float)UserSettings.defaultManagedLaneCapacityVph;
                        if(Float.isNaN(aux_fd.jam_density_vpkpl))
                            aux_fd.jam_density_vpkpl = (float)UserSettings.defaultManagedLaneJamDensityVpk;
                        if(Float.isNaN(aux_fd.ff_speed_kph))
                            aux_fd.ff_speed_kph = (float)UserSettings.defaultManagedLaneFreeFlowSpeedKph;
                    }
                    // ------------------------------------------------------------------

                }

        // create segments
        if(jaxb_segments!=null)
            for (jaxb.Sgmt sgmt : jaxb_segments.getSgmt()) {
                Segment segment = new Segment(this, sgmt);
                segments.put(segment.id,segment);
            }

        // create routes
        if(jaxb_routes!=null)
            for (jaxb.Route jroute : jaxb_routes.getRoute()) {
                Route route = new Route(this, jroute);
                routes.put(route.id,route);
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
        if (jaxb_scenario.getDemands()!=null) {
            for (jaxb.Demand dem : jaxb_scenario.getDemands().getDemand())
                scenario.links.get(dem.getLinkId()).set_demand_vph(
                        dem.getCommodityId(),
                        new Profile1D(
                                dem.getStartTime(),
                                dem.getDt(),
                                OTMUtils.csv2list(dem.getContent())));
        }

        // assign splits
        if (jaxb_scenario.getSplits()!=null) {
            for (jaxb.SplitNode jsplitnode : jaxb_scenario.getSplits().getSplitNode())
                for (jaxb.Split split : jsplitnode.getSplit()) {
                    AbstractLink link = scenario.links.get(split.getLinkOut());
                    if (link instanceof LinkOfframp)
                        ((LinkOfframp) link).set_split(
                                jsplitnode.getCommodityId(),
                                new Profile1D(
                                        jsplitnode.getStartTime(),
                                        jsplitnode.getDt(),
                                        OTMUtils.csv2list(split.getContent())));
                }
        }

        // create actuator and sensor maps
        Map<Long,jaxb.Actuator> actuators = new HashMap<>();
        if(jaxb_scenario.getActuators()!=null)
            for(jaxb.Actuator x : jaxb_scenario.getActuators().getActuator())
                actuators.put(x.getId(),x);

        Map<Long,jaxb.Sensor> sensors = new HashMap<>();
        if(jaxb_scenario.getSensors()!=null)
            for(jaxb.Sensor x : jaxb_scenario.getSensors().getSensor())
                sensors.put(x.getId(),x);

        // read controllers
        if(jaxb_scenario.getControllers()!=null){
            for(jaxb.Controller jcnt : jaxb_scenario.getControllers().getController()){

                if(jcnt.getType().compareTo("schedule")!=0)
                    throw new Exception("Uknown controller type: " + jcnt.getType());

                // actuator
                long act_id = Long.parseLong(jcnt.getTargetActuators().getIds());
                jaxb.Actuator jact = actuators.get(act_id);

                // target
                AbstractLink link = scenario.links.get(jact.getActuatorTarget().getId());
                int [] lanes = OTMUtils.read_lanes(jact.getActuatorTarget().getLanes(),link.get_lanes());
                LaneGroupType lgtype = link.lane2lgtype().get(lanes[0]-1);

                // TODO FIX THIS WHEN WE GET TO HOV/HOT POLICIES
                AbstractController.Type cntr_type = AbstractController.Type.RampMetering;

//                ControlSchedule sch = new ControlSchedule(jcnt.getId(),link,lgtype,cntr_type,jact.getId());
                ControlSchedule sch = link.get_controller_schedule(lgtype,cntr_type);

                for(jaxb.Entry jentry : jcnt.getSchedule().getEntry()){
                    control.AbstractController.Algorithm  algorithm = control.AbstractController.Algorithm.valueOf(jentry.getType());

                    AbstractController ctrl = null;
                    switch( algorithm ){

                        case open:
                            ctrl = ControlFactory.create_controller_open(null,0l);
                            break;

                        case closed:
                            ctrl = ControlFactory.create_controller_closed(null,0l);
                            break;

                        case alinea:

                            // feedback sensors
                            jaxb.Sensor jsns = null;
                            if(jentry.getFeedbackSensors()!=null){
                                long sensor_id = OTMUtils.csv2longlist(jentry.getFeedbackSensors().getIds()).get(0);
                                jsns = sensors.get(sensor_id);
                            }

                            ctrl = ControlFactory.create_controller_alinea(jentry,jsns);
                            break;

                        case fixed_rate:
                            ctrl = ControlFactory.create_controller_fixed_rate(jentry);

                            break;


                    }

                    sch.update(jentry.getStartTime(),ctrl);

                }

            }
        }

        // max ids
        reset_max_ids();

    }

    public FreewayScenario clone(){
        FreewayScenario scn_cpy = new FreewayScenario();
        scn_cpy.name = name;
        scn_cpy.description = description;
        scn_cpy.max_link_id = max_link_id;
        scn_cpy.max_node_id = max_node_id;
        scn_cpy.max_seg_id = max_seg_id;
        scn_cpy.max_controller_id = max_controller_id;
        scn_cpy.max_sensor_id = max_sensor_id;
        scn_cpy.max_actuator_id = max_actuator_id;
        scn_cpy.max_rc_id = max_rc_id;
        scn_cpy.scenario = scenario.clone();
        for(Map.Entry<Long,Segment> e : segments.entrySet()) {
            Segment new_segment = e.getValue().clone();
            new_segment.my_fwy_scenario = scn_cpy;
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

//        for(AbstractController ctrl : controller_schedule.items)
//            scn_cpy.controller_schedule.items.add(ctrl);

        return scn_cpy;
    }

    /////////////////////////////////////
    // API scenario
    /////////////////////////////////////

    public Scenario get_scenario(){
        return scenario;
    }

    public float get_start_time(){
        return sim_start_time;
    }

    public float get_sim_duration(){
        return sim_duration;
    }

    public float get_sim_dt_sec(){
        return sim_dt;
    }

    public void set_start_time(float start_time){
        this.sim_start_time = start_time;
    }

    public void set_sim_duration(float duration){
        this.sim_duration = duration;
    }

    public void set_sim_dt_sec(float sim_dt){
        this.sim_dt = sim_dt;
    }

    /////////////////////////////////////
    // API controller
    /////////////////////////////////////

//    public Schedule get_controller_schedule(){
//        return controller_schedule;
//    }

    /////////////////////////////////////
    // API network
    /////////////////////////////////////

    public List<AbstractLink> get_links(){
        return segments.values().stream()
                .flatMap(sgmt->sgmt.get_links().stream())
                .collect(toList());
    }

    public Map<Long,Node> get_nodes(){
        return scenario.nodes;
    }

    /////////////////////////////////////
    // API routes
    /////////////////////////////////////

    public Route create_route(String name){
        long newid;
        if (routes.keySet().isEmpty()) 
            newid = 1;
        else
            newid = routes.keySet().stream().max(Long::compare).get() + 1;
        Route route = new Route(this,newid,name);
        routes.put(newid,route);
        return route;
    }

    public void delete_route(long routeid) throws Exception {
        if(!routes.containsKey(routeid))
            throw new Exception("Tried to delete a non-existent route");
        routes.remove(routeid);
    }

    public Collection<Route> get_routes(){
        return routes.values();
    }

    public Route get_route(long routeid){
        return routes.get(routeid);
    }

    /////////////////////////////////////
    // API segment
    /////////////////////////////////////

    public List<List<Segment>> get_linear_freeway_segments(){

        List<List<Segment>> result = new ArrayList<>();

        // collect all freeway segments (ignore connectors)
        List<Segment> all_segments = segments.values().stream()
                .filter(sgmt->sgmt.fwy instanceof LinkFreeway )
                .collect(toList());

        // first extract linear freeways
        List<Segment> source_segments = segments.values().stream()
                .filter(sgmt->sgmt.fwy.is_source())
                .collect(toList());

        for(Segment source_segment : source_segments){
            List<Segment> this_fwy = build_freeway_from_segment(source_segment);
            result.add(this_fwy);
            all_segments.removeAll(this_fwy);
        }

        // the remaining segments should all be circular

        // sort all remaining segments by id
        Collections.sort(all_segments,Comparator.comparing(Segment::get_id));
        while(!all_segments.isEmpty()){
            List<Segment> this_fwy = build_freeway_from_segment(all_segments.get(0));
            result.add(this_fwy);
            all_segments.removeAll(this_fwy);
        }

        // sort all freeways by the id of their first segment
        result.sort((List<Segment> o1,List<Segment> o2)->(int) (o1.get(0).get_id()-o2.get(0).get_id()));

        return result;
    }

    public List<LinkConnector> get_connectors(){
        List<LinkConnector> result = segments.values().stream()
                .filter(sgmt->sgmt.fwy instanceof LinkConnector )
                .map(sgmt -> (LinkConnector) sgmt.fwy )
                .collect(toList());
        Collections.sort(result,Comparator.comparing(AbstractLink::get_id));
        return result;
    }

    public Set<Segment> get_segments(){
        return new HashSet<>(segments.values());
    }

    public Set<String> get_segment_names(){
        return segments.values().stream().map(segment->segment.name).collect(Collectors.toSet());
    }

    public Segment get_segment_by_name(String name){
        Set<Segment> xsegments = segments.values().stream()
                .filter(seg->seg.name.equals(name))
                .collect(toSet());
        return xsegments.size()==0 ? null : xsegments.iterator().next();
    }

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
    public Segment create_isolated_segment(String segment_name, ParametersFreeway params, AbstractLink.Type linktype){

        if(linktype!=AbstractLink.Type.freeway && linktype!= AbstractLink.Type.connector)
            return null;

        // create a segment
        Long segment_id = ++max_seg_id;
        Segment segment = new Segment(segment_id);
        segment.name = segment_name;
        segment.my_fwy_scenario = this;
        segments.put(segment_id,segment);

        // create nodes and freeway link
        Node start_node = new Node(++max_node_id);
        Node end_node = new Node(++max_node_id);

        LinkFreewayOrConnector link = null;
        switch(linktype){
            case freeway:

                link = new LinkFreeway(
                        ++max_link_id,   // id,
                        segment,// mysegment,
                        null,// up_link,
                        null,// dn_link,
                        start_node.id,// start_node_id,
                        end_node.id,// end_node_id,
                        params);

                break;
            case connector:

                link = new LinkConnector(
                        ++max_link_id,// id,
                        segment,// mysegment,
                        null,// up_link,
                        null,// dn_link,
                        start_node.id,// start_node_id,
                        end_node.id,// end_node_id,
                        params);

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
            if(link instanceof LinkOfframp)
                ((LinkOfframp)link).delete_splits();
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
    public Commodity create_commodity(String name, float pvequiv){
        long max_id;
        if(scenario.commodities.isEmpty())
            max_id = 0;
        else
            max_id = scenario.commodities.values().stream().mapToLong(x->x.id).max().getAsLong() + 1;

        Commodity new_comm = new Commodity(max_id,name,pvequiv);
        scenario.commodities.put(new_comm.id,new_comm);
        return new_comm;
    }

    /**
     * Delete a commodity with a given name
     * @param name
     * @return
     */
    public boolean delete_commodity_with_name(String name){
        Optional<Long> comm_id = scenario.commodities.values().stream()
                .filter(c->c.name.equals(name))
                .map(c->c.id)
                .findFirst();

        if(comm_id.isPresent() && scenario.commodities.containsKey(comm_id.get())) {
            remove_commodity(comm_id.get());
            return true;
        }
        else
            return false;
    }

    /**
     * Delete a commodity with a given id
     * @param comm_id
     * @return
     */
    public boolean delete_commodity_with_id(Long comm_id){
        if( scenario.commodities.containsKey(comm_id) ){
            remove_commodity(comm_id);
            return true;
        }
        else
            return false;
    }

    private void remove_commodity(long comm_id){

        // remove from commodities
        scenario.commodities.remove(comm_id);

        // remove demands and splits
        for(AbstractLink link : scenario.links.values()) {
            if (link.demands != null && link.demands.containsKey(comm_id))
                link.demands.remove(comm_id);
            if(link instanceof LinkOfframp)
                ((LinkOfframp) link).remove_split_for_commodity(comm_id);
        }

    }

    /////////////////////////////////////
    // utilities
    /////////////////////////////////////

    public boolean is_valid_segment_name(String name){
        return !segments.values().stream().anyMatch(seg->seg.name.equals(name));
    }

    public boolean is_valid_link_name(String name){
        return !scenario.links.values().stream()
                .anyMatch(link->link.get_name()!=null && link.get_name().equals(name));
    }

    /////////////////////////////////////
    // run
    /////////////////////////////////////

    public void run_on_new_thread() throws Exception {
        throw new Exception("NOT IMPLEMENTED!");
    }

    public jaxb.Scn to_jaxb() throws Exception {

        jaxb.Scn scn = new jaxb.Scn();

        scn.setScenario(scenario.to_jaxb());

        scn.setName(name);
        scn.setDescription(description);

        jaxb.Sim sim = new jaxb.Sim();
        scn.setSim(sim);
        sim.setStarttime(sim_start_time);
        sim.setDuration(sim_duration);

        jaxb.Sgmts sgmts = new jaxb.Sgmts();
        scn.setSgmts(sgmts);
        for(Segment segment : segments.values())
            sgmts.getSgmt().add(segment.to_jaxb());

        jaxb.Routes rts = new jaxb.Routes();
        scn.setRoutes(rts);
        for(Route rt : routes.values())
            rts.getRoute().add(rt.to_jaxb());

        jaxb.Lnks lnks = new jaxb.Lnks();
        scn.setLnks(lnks);
        List<AbstractLink> all_links = get_links();
        Collections.sort(all_links);
        for(AbstractLink link : all_links ){
            if(link==null)
                continue;
            jaxb.Lnk lnk = new jaxb.Lnk();
            lnk.setId(link.id);
            lnk.setName(link.get_name());
//            lnk.setManagedLanes(BigInteger.valueOf(link.get_mng_lanes()));
//            lnk.setManagedLanesBarrier(Boolean.valueOf(link.get_mng_barrier()));
//            lnk.setManagedLanesSeparated(Boolean.valueOf(link.get_mng_separated()));
//            lnk.setAuxLanes(BigInteger.valueOf(link.get_aux_lanes()));
            if(link.get_is_inner())
                lnk.setIsInner(true);
            lnks.getLnk().add(lnk);
        }

        return scn;
    }

    /////////////////////////////////////
    // protected and private
    /////////////////////////////////////

    protected AbstractLink get_link(Long id){
        return scenario.links.get(id);
    }

    protected void reset_max_ids(){

        if(scenario==null) {
            max_link_id = 0l;
            max_node_id = 0l;
            max_seg_id = 0l;
            max_controller_id = 0l;
            max_sensor_id = 0l;
            max_actuator_id = 0l;
            max_rc_id = 0l;
            return;
        }

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

        // controller
        Optional<Long> opt_max_cntrl_id = scenario.links.values().stream()
                .flatMap(link->link.get_controller_ids().stream())
                .max(Comparator.comparing(Long::valueOf));
        max_controller_id = opt_max_cntrl_id.isPresent() ? opt_max_cntrl_id.get() : 0l;

        // actuator
        Optional<Long> opt_max_act_id = scenario.links.values().stream()
                .flatMap(link->link.get_actuator_ids().stream())
                .max(Comparator.comparing(Long::valueOf));
        max_actuator_id = opt_max_act_id.isPresent() ? opt_max_act_id.get() : 0l;

        // sensor
        Optional<Long> opt_max_sens_id = scenario.links.values().stream()
                .flatMap(link->link.get_sensor_ids().stream())
                .max(Comparator.comparing(Long::valueOf));
        max_sensor_id = opt_max_sens_id.isPresent() ? opt_max_sens_id.get() : 0l;

        // rc
        max_rc_id = 0l;
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

    public long new_controller_id(){
        return ++max_controller_id;
    }

    public long new_actuator_id(){
        return ++max_actuator_id;
    }

    public long new_sensor_id(){
        return ++max_sensor_id;
    }

    public long new_rc_id(){
        return ++max_rc_id;
    }

    private List<Segment> build_freeway_from_segment(Segment first){
        List<Segment> this_fwy = new ArrayList<>();
        Segment curr_segment = first;
        this_fwy.add(first);
        while(curr_segment.fwy.dn_link!=null && !this_fwy.contains(curr_segment.fwy.dn_link.mysegment) ){
            curr_segment = curr_segment.fwy.dn_link.mysegment;
            this_fwy.add(curr_segment);
        }
        return this_fwy;
    }

    public void add_ghost_pieces() {

        this.ghost_pieces = new GhostPieces();

        // ghost sources................................................
        Set<AbstractLink> source_links = scenario.links.values().stream().filter(link -> link.is_source()).collect(toSet());
        for (AbstractLink link : source_links) {

            // create a new segment
            Segment newsegment = new Segment(++max_seg_id);
            newsegment.name = "";
            newsegment.my_fwy_scenario = this;
            ghost_pieces.segments.add(newsegment);
            segments.put(newsegment.id,newsegment);                                               // ADDED GHOST SEGMENT

            // create new node
            Node newnode = new Node(++max_node_id);
            ghost_pieces.nodes.add(newnode);
            scenario.nodes.put(newnode.id,newnode);                                                  // ADDED GHOST NODE

            // create params
            ParametersFreeway newparams = new ParametersFreeway("",link.get_lanes(),
                    0,false,false,0,
                    link.get_length_meters(),
                    link.get_gp_capacity_vphpl(),
                    link.get_gp_jam_density_vpkpl(),
                    link.get_gp_freespeed_kph(),
                    Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);

            // create new link
            LinkGhost newlink = new LinkGhost(
                    ++max_link_id,
                    newsegment,
                    null,
                    link,
                    newnode.id,
                    link.start_node_id,
                    newparams);

            ghost_pieces.links.add(newlink);
            scenario.links.put(newlink.id,newlink);                                                  // ADDED GHOST LINK
            newsegment.fwy = newlink;
            link.up_link = newlink;                                                            // MODIFIED EXISTING LINK

            newnode.out_links.add(newlink.id);
            scenario.nodes.get(link.start_node_id).in_links.add(newlink.id);                   // MODIFIED EXISTING NODE

            // transfer demands to new link
            newlink.demands = link.demands;
            link.demands = new HashMap<>();                                                 // MODIFIED EXISTING DEMANDS

        }

        // ghost sinks................................................
        Set<Segment> sink_segments_with_offramps = this.segments.values().stream()
                .filter(s->s.fwy.get_dn_segment()==null)
                .filter(s->!s.get_frs().isEmpty())
                .collect(toSet());

        for (Segment segment : sink_segments_with_offramps) {

            AbstractLink link = segment.fwy;

            // create a new segment
            Segment newsegment = new Segment(++max_seg_id);
            newsegment.name = "";
            newsegment.my_fwy_scenario = this;
            ghost_pieces.segments.add(newsegment);
            segments.put(newsegment.id,newsegment);                                               // ADDED GHOST SEGMENT

            // create new node
            Node newnode = new Node(++max_node_id);
            ghost_pieces.nodes.add(newnode);
            scenario.nodes.put(newnode.id,newnode);                                                  // ADDED GHOST NODE

            // create params
            ParametersFreeway newparams = new ParametersFreeway("",link.get_lanes(),
                    0,false,false,0,
                    link.get_length_meters(),
                    link.get_gp_capacity_vphpl(),
                    link.get_gp_jam_density_vpkpl(),
                    link.get_gp_freespeed_kph(),
                    Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);

            // create new link
            LinkGhost newlink = new LinkGhost(
                    ++max_link_id,
                    newsegment,
                    link,
                    null,
                    link.end_node_id,
                    newnode.id,
                    newparams);

            ghost_pieces.links.add(newlink);
            scenario.links.put(newlink.id,newlink);                                                  // ADDED GHOST LINK
            newsegment.fwy = newlink;
            link.dn_link = newlink;                                                            // MODIFIED EXISTING LINK

            newnode.in_links.add(newlink.id);
            scenario.nodes.get(link.end_node_id).out_links.add(newlink.id);                    // MODIFIED EXISTING NODE

        }

    }

    public void remove_ghost_pieces(){

        // undo modifications
        for(LinkGhost gl : ghost_pieces.links){

            if(gl.is_source()){
                AbstractLink link = gl.dn_link;
                link.up_link = null;
                scenario.nodes.get(link.start_node_id).in_links.remove(gl.id);
                link.demands = gl.demands;
            }

            if(gl.is_sink()){
                AbstractLink link = gl.up_link;
                link.dn_link = null;
                scenario.nodes.get(link.end_node_id).out_links.remove(gl.id);
            }

        }

        // remove ghost pieces
        ghost_pieces.segments.forEach(g->segments.remove(g.id));
        ghost_pieces.nodes.forEach(g->scenario.nodes.remove(g.id));
        ghost_pieces.links.forEach(g->scenario.links.remove(g.id));

        ghost_pieces = null;

        reset_max_ids();
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
                description.equals(that.description) &&
                scenario.equals(that.scenario) &&
                segments.equals(that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, scenario, segments);
    }

    /////////////////////////////////////
    // class
    /////////////////////////////////////

    public class GhostPieces {
        public Set<Node> nodes = new HashSet<>();
        public Set<LinkGhost> links = new HashSet<>();
        public Set<Segment> segments = new HashSet<>();
    }

}
