package opt.data;

import opt.UserSettings;
import opt.data.control.*;
import opt.data.event.*;
import opt.utils.DataUtils;
import profiles.Profile1D;
import utils.OTMUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class FreewayScenario {

    private Long max_link_id;
    private Long max_node_id;
    private Long max_seg_id;
    private Long max_schedule_id;
    private Long max_rc_id;
    private Long max_event_id;

    public String name;
    public String description;
    protected Scenario scenario;
    protected Map<Long,Segment> segments = new HashMap<>();
    protected Map<Long, Route> routes = new HashMap<>();
    protected LaneChangeModel lcmodel;

    // simulation parameters
    protected float sim_start_time = 0f;
    protected float sim_duration = (float) UserSettings.defaultSimulationDuration;
    protected float max_celllength_meters = UserSettings.defaultMaxCellLength;
    protected float sim_dt = Float.NaN;

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
        scenario.commodities.put(0l,new Commodity(0l,"Car",1f));
        this.lcmodel = new LaneChangeModel(UserSettings.defaultLaneChoice_keep, UserSettings.defaultLaneChoice_rhovpmplane);
        reset_max_ids();
    }

    public FreewayScenario(String name,String description,jaxb.Sim sim,jaxb.Lnks jaxb_lnks,jaxb.Sgmts jaxb_segments,jaxb.Routes jaxb_routes,jaxb.Schds jaxb_schds,jaxb.Scenario jaxb_scenario) throws Exception {

        reset_max_ids();

        this.name = name;
        this.description = description;

        if (sim != null) {
            this.sim_start_time = sim.getStarttime();
            this.sim_duration = sim.getDuration();
        }

        // model
        if (jaxb_scenario.getModels() != null) {
            jaxb.Model model = jaxb_scenario.getModels().getModel().get(0);

            // lane change model
            if (model.getLanechanges()!=null && !model.getLanechanges().getLanechange().isEmpty()) {
                assert (model.getLanechanges().getLanechange().size() == 1);
                jaxb.Lanechange lc = model.getLanechanges().getLanechange().get(0);
                double keep = UserSettings.defaultLaneChoice_keep;
                double rho_vpkmplane = UserSettings.defaultLaneChoice_rhovpmplane / 1.609;
                for (jaxb.Parameter p : lc.getParameters().getParameter()) {
                    switch (p.getName()) {
                        case "keep":
                            keep = Double.parseDouble(p.getValue());
                            break;
                        case "rho_vpkmplane":
                            rho_vpkmplane = Double.parseDouble(p.getValue());
                            break;
                    }
                }
                this.lcmodel = new LaneChangeModel(keep, rho_vpkmplane * 1.609);

            } else {
                this.lcmodel = new LaneChangeModel(UserSettings.defaultLaneChoice_keep, UserSettings.defaultLaneChoice_rhovpmplane);
            }

            set_max_celllength_meters(model.getModelParams().getMaxCellLength());
        }

        // create Scenario object
        this.scenario = new Scenario(this, jaxb_scenario);

        // attach link names
        if (jaxb_lnks != null)
            for (jaxb.Lnk lnk : jaxb_lnks.getLnk())
                if (scenario.links.containsKey(lnk.getId())) {
                    AbstractLink link = scenario.links.get(lnk.getId());
                    link.set_name(lnk.getName());

                    // TODO REMOVE THESE AFTER UPDATING EXISTING FILES.
                    if (!link.has_mng()) {
                        link.set_mng_lanes(lnk.getManagedLanes() == null ? 0 : lnk.getManagedLanes().intValue());
                        link.set_mng_barrier(lnk.isManagedLanesBarrier() == null ? false : lnk.isManagedLanesBarrier());
                        link.set_mng_separated(lnk.isManagedLanesSeparated() == null ? false : lnk.isManagedLanesSeparated());
                    }
                    if (!link.has_aux()) {
                        link.set_aux_lanes(lnk.getAuxLanes() == null ? 0 : lnk.getAuxLanes().intValue());
                    }
                    link.set_is_inner(lnk.isIsInner() == null ? false : lnk.isIsInner());

                    // TODO REMOVE THIS
                    // HACK SET MNG AND AUX LANE PARAMETERS IF THEY ARE NOT SET
                    // ---------------------------------------------------------------------------
                    if (link.has_mng()) {
                        if (link.params.mng_fd == null)
                            link.params.mng_fd = new FDparams(
                                    (float) UserSettings.defaultManagedLaneCapacityVph,
                                    (float) UserSettings.defaultManagedLaneJamDensityVpk,
                                    (float) UserSettings.defaultManagedLaneFreeFlowSpeedKph);
                        if (Float.isNaN(link.params.mng_fd.capacity_vphpl))
                            link.params.mng_fd.capacity_vphpl = (float) UserSettings.defaultManagedLaneCapacityVph;
                        if (Float.isNaN(link.params.mng_fd.jam_density_vpkpl))
                            link.params.mng_fd.jam_density_vpkpl = (float) UserSettings.defaultManagedLaneJamDensityVpk;
                        if (Float.isNaN(link.params.mng_fd.ff_speed_kph))
                            link.params.mng_fd.ff_speed_kph = (float) UserSettings.defaultManagedLaneFreeFlowSpeedKph;
                    }
                    if (link.has_aux()) {
                        FDparams aux_fd = ((ParametersFreeway) link.params).aux_fd;
                        if (aux_fd == null)
                            aux_fd = new FDparams(
                                    (float) UserSettings.defaultAuxLaneCapacityVph,
                                    (float) UserSettings.defaultAuxLaneJamDensityVpk,
                                    (float) UserSettings.defaultAuxLaneFreeFlowSpeedKph);
                        if (Float.isNaN(aux_fd.capacity_vphpl))
                            aux_fd.capacity_vphpl = (float) UserSettings.defaultManagedLaneCapacityVph;
                        if (Float.isNaN(aux_fd.jam_density_vpkpl))
                            aux_fd.jam_density_vpkpl = (float) UserSettings.defaultManagedLaneJamDensityVpk;
                        if (Float.isNaN(aux_fd.ff_speed_kph))
                            aux_fd.ff_speed_kph = (float) UserSettings.defaultManagedLaneFreeFlowSpeedKph;
                    }
                    // ------------------------------------------------------------------

                }

        // create segments
        if (jaxb_segments != null)
            for (jaxb.Sgmt sgmt : jaxb_segments.getSgmt()) {
                Segment segment = new Segment(this, sgmt);
                segments.put(segment.id, segment);
            }

        // create routes
        if (jaxb_routes != null)
            for (jaxb.Route jroute : jaxb_routes.getRoute()) {
                Route route = new Route(this, jroute);
                routes.put(route.id, route);
            }

        // make link connections
        for (AbstractLink abslink : scenario.links.values()) {

            Set<AbstractLink> up_links = scenario.nodes.get(abslink.start_node_id).in_links.stream()
                    .map(link_id -> scenario.links.get(link_id))
                    .filter(link -> link.mysegment != null)
                    .collect(Collectors.toSet());

            Set<AbstractLink> dn_links = scenario.nodes.get(abslink.end_node_id).out_links.stream()
                    .map(link_id -> scenario.links.get(link_id))
                    .filter(link -> link.mysegment != null)
                    .collect(Collectors.toSet());

            Set<AbstractLink> up_links_f = null;
            Set<AbstractLink> dn_links_f = null;
            switch (abslink.get_type()) {

                case freeway:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway) link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway) link)
                            .collect(Collectors.toSet());

                    break;

                case connector:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkOfframp)
                            .map(link -> (LinkOfframp) link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkOnramp)
                            .map(link -> (LinkOnramp) link)
                            .collect(Collectors.toSet());

                    break;

                case onramp:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkConnector)
                            .map(link -> (LinkConnector) link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway) link)
                            .collect(Collectors.toSet());

                    break;

                case offramp:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway) link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkConnector)
                            .map(link -> (LinkConnector) link)
                            .collect(Collectors.toSet());

                    break;
            }

            if (up_links_f != null && !up_links_f.isEmpty())
                abslink.up_link = up_links_f.iterator().next();

            if (dn_links_f != null && !dn_links_f.isEmpty())
                abslink.dn_link = dn_links_f.iterator().next();

        }

        // assign demands
        if (jaxb_scenario.getDemands() != null) {
            for (jaxb.Demand dem : jaxb_scenario.getDemands().getDemand())
                scenario.links.get(dem.getLinkId()).set_demand_vph(
                        dem.getCommodityId(),
                        new Profile1D(
                                dem.getStartTime(),
                                dem.getDt(),
                                OTMUtils.csv2list(dem.getContent())));
        }

        // assign splits
        if (jaxb_scenario.getSplits() != null) {
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
        Map<Long, jaxb.Actuator> actuators = new HashMap<>();
        if (jaxb_scenario.getActuators() != null)
            for (jaxb.Actuator x : jaxb_scenario.getActuators().getActuator())
                actuators.put(x.getId(), x);

        Map<Long, jaxb.Sensor> sensors = new HashMap<>();
        if (jaxb_scenario.getSensors() != null)
            for (jaxb.Sensor x : jaxb_scenario.getSensors().getSensor())
                sensors.put(x.getId(), x);

        // put schedule names into a HashMap
        Map<Long, String> sch_names = new HashMap<>();
        if (jaxb_schds != null)
            for (jaxb.Schd jschd : jaxb_schds.getSchd())
                sch_names.put(jschd.getId(), jschd.getName());

        if (jaxb_scenario.getControllers() != null) {
            for (jaxb.Controller jcnt : jaxb_scenario.getControllers().getController()) {

                switch (jcnt.getType()) {
                    case "schedule":
                        read_schedule(jcnt, actuators, sensors, sch_names);
                        break;
                    case "frflow":              // TODO remove this
                    case "linkflow":
                        read_frflow(jcnt, actuators);
                        break;
                    default:
                        throw new Exception("Unknown controller type.");
                }
            }
        }

        // events .........
        AbstractLink link;
        List<AbstractLink> links;
        Set<LaneGroupType> lgtypes;
        List<LaneGroupType> lane2lgtype;
        LaneGroupType lgtype0,lgtype1;

        if(jaxb_scenario.getEvents()!=null)
            for(jaxb.Event jev : jaxb_scenario.getEvents().getEvent()) {
                AbstractEvent event;

                long id = jev.getId();
                String type = jev.getType();
                float timestamp = jev.getTimestamp();
                String jname = jev.getName();

                switch(jev.getType()){

                    case "linktgl":

                        if(!jev.getEventTarget().getType().equals("links"))
                            throw new Exception("Bad event target type");

                        links = new ArrayList<>();
                        for(long link_id : OTMUtils.csv2longlist(jev.getEventTarget().getIds())){
                            if(!scenario.links.containsKey(link_id))
                                throw new Exception("Bad link id in event target.");
                            links.add(scenario.links.get(link_id));
                        }

                        boolean isopen = true;
                        if(jev.getParameters()!=null)
                            for(jaxb.Parameter p : jev.getParameters().getParameter())
                                if(p.getName().equals("isopen"))
                                    isopen = Boolean.parseBoolean(p.getValue());

                        event = new EventLinkToggle(id,type,timestamp,jname,links,isopen);
                        break;

                    case "controltgl":

                        if(!jev.getEventTarget().getType().equals("controllers"))
                            throw new Exception("Bad event target type");

                        // get the controllers
                        List<ControlSchedule> controllers = new ArrayList<>();
                        for(long cntrl_id : OTMUtils.csv2longlist(jev.getEventTarget().getIds())){
                            Optional<ControlSchedule> ocntrl = scenario.links.values().stream()
                                    .flatMap(lnk->lnk.schedules.values().stream())
                                    .flatMap(m->m.values().stream())
                                    .filter(c->c.getId()==cntrl_id)
                                    .findFirst();

                            if(!ocntrl.isPresent())
                                throw new Exception("Bad controller id in event target.");

                            controllers.add( ocntrl.get() );
                        }

                        boolean ison = true;
                        if(jev.getParameters()!=null)
                            for(jaxb.Parameter p : jev.getParameters().getParameter())
                                if(p.getName().equals("ison"))
                                    ison = Boolean.parseBoolean(p.getValue());

                        event = new EventControlToggle(id,type,timestamp,jname,controllers,ison);
                        break;

                    case "lglanes":

                        if(!jev.getEventTarget().getType().equals("lanegroups"))
                            throw new Exception("Bad event target type");

                        links = new ArrayList<>();
                        lgtypes = new HashSet<>();
                        for(LaneGroup lg : DataUtils.read_lanegroups(jev.getEventTarget().getLanegroups(),scenario.links)){
                            if(!scenario.links.containsKey(lg.linkid))
                                throw new Exception("Bad linkid in target lane group");
                            link = scenario.links.get(lg.linkid);
                            links.add(link);
                            lane2lgtype = link.lane2lgtype();
                            lgtype0 = lane2lgtype.get(lg.lanes[0]-1);
                            lgtype1 = lane2lgtype.get(lg.lanes[1]-1);
                            if(lgtype0!=lgtype1)
                                throw new Exception("Bad lanes in target lane group");
                            lgtypes.add(lgtype0);
                        }

                        if(lgtypes.size()!=1)
                            throw new Exception("lgtypes.size()!=1");

                        Integer deltalanes = null;
                        if(jev.getParameters()!=null)
                            for(jaxb.Parameter p : jev.getParameters().getParameter())
                                if(p.getName().equals("lanes"))
                                    deltalanes = Integer.parseInt(p.getValue());
                        event = new EventLanegroupLanes(id,type,timestamp,jname,links,lgtypes.iterator().next(),deltalanes);
                        break;

                    case "lgfd":

                        if(!jev.getEventTarget().getType().equals("lanegroups"))
                            throw new Exception("Bad event target type");

                        links = new ArrayList<>();
                        lgtypes = new HashSet<>();
                        for(LaneGroup lg : DataUtils.read_lanegroups(jev.getEventTarget().getLanegroups(),scenario.links)){
                            if(!scenario.links.containsKey(lg.linkid))
                                throw new Exception("Bad linkid in target lane group");
                            link = scenario.links.get(lg.linkid);
                            links.add(link);
                            lane2lgtype = link.lane2lgtype();
                            lgtype0 = lane2lgtype.get(lg.lanes[0]-1);
                            lgtype1 = lane2lgtype.get(lg.lanes[1]-1);
                            if(lgtype0!=lgtype1)
                                throw new Exception("Bad lanes in target lane group");
                            lgtypes.add(lgtype0);
                        }

                        Float capacity_vphpl = null;
                        Float jam_density_vpkpl = null;
                        Float ff_speed_kph = null;
                        for(jaxb.Parameter p : jev.getParameters().getParameter()){
                            switch(p.getName()){
                                case "capacity":
                                    capacity_vphpl = Float.parseFloat(p.getValue());
                                    break;
                                case "jam_density":
                                    jam_density_vpkpl = Float.parseFloat(p.getValue());
                                    break;
                                case "speed":
                                    ff_speed_kph = Float.parseFloat(p.getValue());
                                    break;
                            }
                        }
                        FDparams fd_mult = new FDparams(capacity_vphpl,jam_density_vpkpl,ff_speed_kph);
                        event = new EventLanegroupFD(id,type,timestamp,jname,links,lgtypes.iterator().next(),fd_mult);
                        break;

                    default:
                        throw new Exception("Bad type in events.");
                }

                scenario.events.put(jev.getId(),event);
            }

        // max ids
        reset_max_ids();

        if(!validate())
            throw new Exception("Validation failure.");

    }

    private boolean validate(){

        // link ids in the scenario and segments are identical
        Set<Long> segment_link_ids = segments.values().stream()
                .flatMap(sgmt->sgmt.get_links().stream())
                .map(link->link.get_id())
                .collect(toSet());

        Set<Long> scenario_link_ids = scenario.links.keySet();

        if (!segment_link_ids.containsAll(scenario_link_ids) || !scenario_link_ids.containsAll(segment_link_ids)) {
            System.err.println("Scenario and segment link ids are not identical");
            return false;
        }

        return true;

    }

    public FreewayScenario clone(){
        FreewayScenario scn_cpy = new FreewayScenario();
        scn_cpy.name = name;
        scn_cpy.description = description;
        scn_cpy.max_link_id = max_link_id;
        scn_cpy.max_node_id = max_node_id;
        scn_cpy.max_seg_id = max_seg_id;
        scn_cpy.max_schedule_id = max_schedule_id;
        scn_cpy.max_rc_id = max_rc_id;
        scn_cpy.max_event_id = max_event_id;
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

    private void read_schedule(jaxb.Controller jcnt,Map<Long,jaxb.Actuator> actuators,Map<Long,jaxb.Sensor> sensors,Map<Long,String> sch_names) throws Exception {

        // actuator
        long act_id = Long.parseLong(jcnt.getTargetActuators().getIds());
        jaxb.Actuator jact = actuators.get(act_id);

        List<AbstractLink> links = new ArrayList<>();
        List<LaneGroupType> lgtypes = new ArrayList<>();
        for(String e : jact.getActuatorTarget().getLanegroups().split(",")){

            String [] a1 = e.split("[(]");
            Long linkid = Long.parseLong(a1[0]);
            AbstractLink link = scenario.links.get(linkid);
            links.add(link);

            String [] a2 = a1[1].split("[)]");
            int [] lanes = OTMUtils.read_lanes(a2[0],link.get_lanes());
            lgtypes.add( link.lane2lgtype().get(lanes[0]-1) );
        }

        // determine the controller type from the entry types
        Set<control.AbstractController.Algorithm> entry_types = jcnt.getSchedule().getEntry().stream()
                .map(e->control.AbstractController.Algorithm.valueOf(e.getType()))
                .collect(toSet());

        AbstractController.Type cntr_type = null;
        LaneGroupType lg_type = null;
        if( entry_types.stream().allMatch(e->AbstractController.is_ramp_metering(e) ) ) {
            cntr_type = AbstractController.Type.RampMetering;
            assert(links.size()==1);
            lg_type = lgtypes.get(0);
        }
        else if ( entry_types.stream().allMatch(e->AbstractController.is_lg_restrict(e) ) ) {
            cntr_type = AbstractController.Type.LgRestrict;

            // all lane groups should be mng
            assert(lgtypes.stream().allMatch(t->t==LaneGroupType.mng));
            lg_type = LaneGroupType.mng;
        }
        else
            throw new Exception("Incompatible control algorithms in schedule.");

        // create the schedule
        long sch_id = jcnt.getId();
        String sch_name = sch_names.containsKey(sch_id) ? sch_names.get(sch_id) : "";
        ControlSchedule sch = new ControlSchedule(sch_id,sch_name,links,lg_type,cntr_type);

        for(jaxb.Entry jentry : jcnt.getSchedule().getEntry()){

            control.AbstractController.Algorithm  algorithm = control.AbstractController.Algorithm.valueOf(jentry.getType());

            AbstractController ctrl = null;
            switch( algorithm ){

                case rm_open:
                    ctrl = ControlFactory.create_controller_rmopen(this);
                    break;

                case rm_closed:
                    ctrl = ControlFactory.create_controller_rmclosed(this);
                    break;

                case rm_alinea:

                    // feedback sensors
                    jaxb.Sensor jsns = null;
                    if(jentry.getFeedbackSensors()!=null){
                        long sensor_id = OTMUtils.csv2longlist(jentry.getFeedbackSensors().getIds()).get(0);
                        jsns = sensors.get(sensor_id);
                    }

                    ctrl = ControlFactory.create_controller_alinea(this,jentry,jsns);
                    break;

                case rm_fixed_rate:
                    ctrl = ControlFactory.create_controller_fixed_rate(this,jentry);
                    break;

                case lg_toll:
                case lg_restrict:
                    ctrl = ControlFactory.create_controller_hovhot(this,jentry);
                    break;

            }

            sch.update(jentry.getStartTime(),ctrl);

        }

        // add the schedule to all of its links
        for(AbstractLink link : links)
            link.add_schedule(sch);

    }

    private void read_frflow(jaxb.Controller jcnt,Map<Long,jaxb.Actuator> actuators) throws Exception {

        // actuator ..........................
        long target_act_id = Long.parseLong(jcnt.getTargetActuators().getIds());
        jaxb.Actuator jact = actuators.get(target_act_id);
//        List<Long> linksout = null;
//        Long linkin;
        Long commid = null;
        for(jaxb.Parameter p : jact.getParameters().getParameter()){
            switch(p.getName()){
//                case "linksout":
//                    linksout = OTMUtils.csv2longlist(p.getValue());
//                    break;
//                case "linkin":
//                    linkin = Long.parseLong(p.getValue());
//                    break;
                case "comm":
                    commid = Long.parseLong(p.getValue());
                    break;
            }
        }

        // controller ................................
//        float ctrl_start_time = jcnt.getStartTime();
//        boolean usefrflows = ctrl_start_time!=100000;

        if(jcnt.getProfiles()!=null) {
            for (jaxb.Profile p : jcnt.getProfiles().getProfile()) {

                long frid = p.getId();
                float prof_start_time = p.getStartTime();
                float prof_dt = p.getDt();
                List<Double> prof_flow = OTMUtils.csv2list(p.getContent());

                Profile1D profile = new Profile1D(prof_start_time, prof_dt, prof_flow);
                LinkOfframp fr = (LinkOfframp) scenario.links.get(frid);
                fr.set_use_fr_flows(profile.start_time!=100000);
                fr.set_frflow(commid,profile);

            }
        }

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

    public float get_max_celllength_meters(){
        return max_celllength_meters;
    }

    public float get_sim_dt_sec(){
        // compute it if it is not computed
        if(Float.isNaN(this.sim_dt))
            sim_dt = (float) Math.floor(scenario.compute_max_simdt_sec(max_celllength_meters));
        return sim_dt;
    }

    public void set_start_time(float start_time){
        this.sim_start_time = start_time;
    }

    public void set_sim_duration(float duration){
        this.sim_duration = duration;
    }

    public void set_max_celllength_meters(float new_max_celllength_meters){
        if(new_max_celllength_meters<=0f)
            new_max_celllength_meters = UserSettings.defaultMaxCellLength;
        if(new_max_celllength_meters!=max_celllength_meters){
            sim_dt = Float.NaN;
            this.max_celllength_meters = new_max_celllength_meters;
        }
    }

    public float set_sim_dt_sec(float new_sim_dt){
        // compute it if it is not computed
        float max_sim_dt = Float.isNaN(this.sim_dt) ?
                (float) Math.floor(scenario.compute_max_simdt_sec(max_celllength_meters)) :
                sim_dt;
        sim_dt = Math.min(max_sim_dt,new_sim_dt);
        return sim_dt;
    }

    /////////////////////////////////////
    // API lane change model
    /////////////////////////////////////

    public double get_lc_keep(){
        return lcmodel.keep;
    }

    public double get_lc_density_vpmilepl(){
        return lcmodel.density_vpmileplane;
    }

    public void set_lc_keep(double x){
        lcmodel.keep = x;
    }

    public void set_lc_density_vpmilepl(double x){
        lcmodel.density_vpmileplane = x;
    }

    /////////////////////////////////////
    // API controller
    /////////////////////////////////////

    public Set<ControlSchedule> get_all_schedules(){
        Set<ControlSchedule> X = new HashSet<>();
        for(Segment segment : segments.values())
            for (AbstractLink link : segment.get_links())
                X.addAll(link.get_all_schedules());
        return X;
    }

    public List<ControlSchedule> get_schedules_for_controltype(AbstractController.Type cntrltype) {
        Set<ControlSchedule> X = new HashSet<>();
        for(Segment segment : segments.values())
            for (AbstractLink link : segment.get_links())
                for (LaneGroupType lgtype : LaneGroupType.values()) {
                    ControlSchedule sch = scenario.links.get(link.id).get_controller_schedule(lgtype, cntrltype);
                    if(sch!=null)
                        X.add(sch);
                }
        List<ControlSchedule> sortedX = new ArrayList<>();
        sortedX.addAll(X);
        Collections.sort(sortedX);
        return sortedX;
    }

    public void delete_schedule(ControlSchedule sch){
        LaneGroupType lgtype = sch.get_lgtype();
        AbstractController.Type cntrltype = sch.get_controlType();
        for(AbstractLink link : sch.get_links())
            link.remove_schedule(lgtype,cntrltype);
    }

    public Map<Long,ControlSchedule> get_controller_schedules_for_links(Collection<Long> link_ids,LaneGroupType lgtype, AbstractController.Type cntrl_type){
        Map<Long,ControlSchedule> X = new HashMap<>();
        for(Long link_id : link_ids){
            if(!scenario.links.containsKey(link_id))
                continue;
            X.put(link_id,scenario.links.get(link_id).get_controller_schedule(lgtype,cntrl_type));
        }
        return X;
    }

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
    public void delete_segment(Segment segment, boolean connect_adjacent) throws Exception {

        if(segments.size()==1)
            throw new Exception("Removing the sole segment is not allowed.");

        // modify schedules that refer to this segment
        List<ControlSchedule> schedules = segment.get_links().stream()
                .flatMap(link->link.get_all_schedules().stream())
                .collect(Collectors.toList());

        for(ControlSchedule schedule : schedules)
            for(AbstractLink link : segment.get_links())
                schedule.remove_link(link);

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
            if(link instanceof LinkOfframp) {
                ((LinkOfframp) link).delete_splits();
                ((LinkOfframp) link).delete_frflows();
            }
            scenario.links.remove(link.id);
        }

        // delete the segment
        segment.in_ors = null;
        segment.out_ors = null;
        segment.in_frs = null;
        segment.out_frs = null;
        segments.remove(segment.id);

        // connect adjacent
        if(connect_adjacent){
            AbstractLink up_link = segment.fwy.up_link;
            AbstractLink dn_link = segment.fwy.dn_link;
            if ((up_link != null) && (dn_link != null) && (dn_link.get_type() == AbstractLink.Type.freeway) )
                dn_link.connect_to_upstream(up_link);
        }

        // if connecting, then remove segment from routes
        // otherwise, remove routes with segment
        if(connect_adjacent)
            routes.values().stream()
                    .filter(route->route.segments.contains(segment))
                    .forEach(route->route.segments.remove(segment));
        else
            routes.values().removeIf(route->route.segments.contains(segment));

    }

    /////////////////////////////////////
    // API commodity
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
        
        List<ControlSchedule> lane_policies = get_schedules_for_controltype(AbstractController.Type.LgRestrict);
        lane_policies.forEach((cs) -> { cs.add_commodity(new_comm.id); });
        
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
            List<ControlSchedule> lane_policies = get_schedules_for_controltype(AbstractController.Type.LgRestrict);
            lane_policies.forEach((cs) -> { cs.remove_commodity(comm_id.get()); });
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
            List<ControlSchedule> lane_policies = get_schedules_for_controltype(AbstractController.Type.LgRestrict);
            lane_policies.forEach((cs) -> { cs.remove_commodity(comm_id); });
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
            if(link instanceof LinkOfframp) {
                ((LinkOfframp) link).remove_split_for_commodity(comm_id);
                ((LinkOfframp) link).remove_frlow_for_commodity(comm_id);
            }


            // remove commodity from mng lane policies
            if(link.schedules!=null && link.schedules.containsKey(LaneGroupType.mng)){
                Map<AbstractController.Type, ControlSchedule> schmap = link.schedules.get(LaneGroupType.mng);
                if(schmap.containsKey(AbstractController.Type.LgRestrict))
                    for(ScheduleEntry entry : (schmap.get(AbstractController.Type.LgRestrict)).get_entries())
                        ((ControllerLgRestrict)entry.get_cntrl()).remove_commodity(comm_id);
            }
        }

    }

    /////////////////////////////////////
    // API events
    /////////////////////////////////////

    public EventLinkToggle add_event_linktoggle(float timestamp, String name, List<AbstractLink> links, boolean isopen) throws Exception {
        EventLinkToggle event = new EventLinkToggle(new_event_id(),"linktgl",timestamp,name,links,isopen);
        if(event_conflicts(event))
            throw new Exception("Event conflicts.");
        scenario.events.put(event.id,event);
        return event;
    }

    public EventControlToggle add_event_controltoggle(float timestamp, String name, List<ControlSchedule> controllers, boolean ison) throws Exception {
        EventControlToggle event = new EventControlToggle(new_event_id(),"controltgl",timestamp,name,controllers,ison);
        if(event_conflicts(event))
            throw new Exception("Event conflicts.");
        scenario.events.put(event.id,event);
        return event;
    }

    public EventLanegroupLanes add_event_lglanes(float timestamp, String name, List<AbstractLink> links, LaneGroupType lgtype, Integer delta_lanes) throws Exception {
        EventLanegroupLanes event = new EventLanegroupLanes(new_event_id(),"lglanes",timestamp,name,links,lgtype,delta_lanes);
        if(event_conflicts(event))
            throw new Exception("Event conflicts.");
        scenario.events.put(event.id,event);
        return event;
    }

    public EventLanegroupFD add_event_lgfd(float timestamp, String name,List<AbstractLink> links,LaneGroupType lgtype,FDparams fd_mult) throws Exception {
        EventLanegroupFD event = new EventLanegroupFD(new_event_id(),"lgfd",timestamp,name,links,lgtype,fd_mult);
        if(event_conflicts(event))
            throw new Exception("Event conflicts.");
        scenario.events.put(event.id,event);
        return event;
    }

    public void delete_event_by_id(long eventid){
        scenario.events.remove(eventid);
    }

    public boolean event_conflicts(AbstractEvent aevent){

        Set<AbstractEvent> conflicts = new HashSet<>();

        if(aevent instanceof EventControlToggle){
            EventControlToggle event = (EventControlToggle) aevent;
            conflicts = scenario.events.values().stream()
                    .filter(x->x.timestamp==event.timestamp)
                    .filter(x->x instanceof EventControlToggle)
                    .map(x -> (EventControlToggle) x)
                    .filter( x-> !Collections.disjoint(x.get_controller_ids(),event.get_controller_ids()))
                    .collect(toSet());
        }

        if(aevent instanceof EventLinkToggle){
            EventLinkToggle event = (EventLinkToggle) aevent;
            conflicts = scenario.events.values().stream()
                    .filter(x->x.timestamp==event.timestamp)
                    .filter(x->x instanceof EventLinkToggle)
                    .map(x->(EventLinkToggle) x)
                    .filter(x->!Collections.disjoint(x.get_link_ids(),event.get_link_ids()))
                    .collect(toSet());
        }

        if(aevent instanceof AbstractEventLaneGroup){
            AbstractEventLaneGroup event = (AbstractEventLaneGroup) aevent;
            conflicts = scenario.events.values().stream()
                    .filter(x->x.timestamp==event.timestamp)
                    .filter(x->x instanceof AbstractEventLaneGroup)
                    .map(x->(AbstractEventLaneGroup) x)
                    .filter(x->x.get_lgtype()==event.get_lgtype())
                    .filter(x->!Collections.disjoint(x.get_link_ids(),event.get_link_ids()))
                    .collect(toSet());
        }

        return !conflicts.isEmpty();
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

        jaxb.Schds schds = new jaxb.Schds();
        scn.setSchds(schds);
        for(ControlSchedule sch : get_all_schedules()) {
            Set<AbstractLink> links_to_write = sch.links_to_write();
            if(sch.ignore(links_to_write))
                continue;
            schds.getSchd().add(sch.to_jaxb());
        }

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
            max_schedule_id = 0l;
//            max_sensor_id = 0l;
            max_rc_id = 0l;
            max_event_id = 0l;
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
                .flatMap(link->link.get_schedule_ids().stream())
                .max(Comparator.comparing(Long::valueOf));
        max_schedule_id = opt_max_cntrl_id.isPresent() ? opt_max_cntrl_id.get() : 0l;

//        // sensor
//        Optional<Long> opt_max_sens_id =
//                scenario.links.values().stream()
//                .flatMap(link->link.get_sensor_ids().stream())
//                .max(Comparator.comparing(Long::valueOf));
//        max_sensor_id = opt_max_sens_id.isPresent() ? opt_max_sens_id.get() : 0l;

        // rc
        max_rc_id = 0l;


        // events
        Optional<Long> opt_max_event_id = scenario.events.keySet().stream()
                .max(Comparator.comparing(Long::valueOf));
        max_event_id = opt_max_event_id.isPresent() ? opt_max_event_id.get() : 0l;

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

    protected long new_event_id(){
        return ++max_event_id;
    }

    public long new_schedule_id(){
        return ++max_schedule_id;
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
        List<AbstractLink> source_links = scenario.links.values().stream().filter(link -> link.is_source()).collect(Collectors.toList());

        // sort by id. This is so that the test harness is repeatable.
        Collections.sort(source_links);

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
                    link.get_mng_lanes(),
                    false,false,
                    link.get_aux_lanes(),
                    link.get_length_meters(),
                    link.get_gp_capacity_vphpl(),
                    link.get_gp_jam_density_vpkpl(),
                    link.get_gp_freespeed_kph(),
                    link.get_mng_capacity_vphpl(),
                    link.get_mng_jam_density_vpkpl(),
                    link.get_mng_freespeed_kph(),
                    link.get_aux_capacity_vphpl(),
                    link.get_aux_jam_density_vpkpl(),
                    link.get_aux_ff_speed_kph());

            // create new link
            LinkGhost newlink = new LinkGhost(
                    ++max_link_id,
                    newsegment,
                    null,
                    link,
                    newnode.id,
                    link.start_node_id,
                    newparams,
                    link.demands);

            ghost_pieces.links.add(newlink);

            scenario.links.put(newlink.id,newlink);                                                  // ADDED GHOST LINK
            newsegment.fwy = newlink;
            link.up_link = newlink;                                                            // MODIFIED EXISTING LINK

            newnode.out_links.add(newlink.id);
            scenario.nodes.get(link.start_node_id).in_links.add(newlink.id);                   // MODIFIED EXISTING NODE

            // transfer demands to new link
            link.demands = new HashMap<>();                                                   // REMOVE EXISTING DEMANDS

        }

        // ghost sinks................................................
        List<Segment> sink_segments_with_offramps = this.segments.values().stream()
                .filter(s->s.fwy.get_dn_segment()==null)
                .filter(s->!s.get_frs().isEmpty())
                .collect(toList());

        // sort by name. This is so that the test harness is repeatable.
        Collections.sort(sink_segments_with_offramps);

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
                    link.get_mng_lanes(),
                    false,false,
                    link.get_aux_lanes(),
                    link.get_length_meters(),
                    link.get_gp_capacity_vphpl(),
                    link.get_gp_jam_density_vpkpl(),
                    link.get_gp_freespeed_kph(),
                    link.get_mng_capacity_vphpl(),
                    link.get_mng_jam_density_vpkpl(),
                    link.get_mng_freespeed_kph(),
                    link.get_aux_capacity_vphpl(),
                    link.get_aux_jam_density_vpkpl(),
                    link.get_aux_ff_speed_kph());

            // create new link
            LinkGhost newlink = new LinkGhost(
                    ++max_link_id,
                    newsegment,
                    link,
                    null,
                    link.end_node_id,
                    newnode.id,
                    newparams,
                    null);

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
    // class
    /////////////////////////////////////

    public class GhostPieces {
        public Set<Node> nodes = new HashSet<>();
        public Set<LinkGhost> links = new HashSet<>();
        public Set<Segment> segments = new HashSet<>();
    }

}
