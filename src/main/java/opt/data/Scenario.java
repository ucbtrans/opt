package opt.data;

import geometry.Side;
import jaxb.*;
import opt.UserSettings;
import opt.data.control.*;
import opt.data.control.Sensor;
import profiles.Profile1D;
import utils.OTMUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Scenario {

    protected FreewayScenario my_fwy_scenario;

    protected Map<Long, Node> nodes = new HashMap<>();
    public Map<Long, AbstractLink> links = new HashMap<>();
    protected Map<Long, Commodity> commodities = new HashMap<>();

	/////////////////////////////////////
    // construction
    /////////////////////////////////////

    protected Scenario(FreewayScenario my_fwy_scenario){
        this.my_fwy_scenario = my_fwy_scenario;
    }

    protected Scenario(FreewayScenario my_fwy_scenario,jaxb.Scenario scenario) throws Exception {

        this.my_fwy_scenario = my_fwy_scenario;

        // network
        Map<Long,jaxb.Roadparam> road_params = new HashMap<>();
        Map<Long,jaxb.Roadgeom> road_geoms = new HashMap<>();
        if (scenario.getNetwork()!=null){

            jaxb.Network network = scenario.getNetwork();

            if (network.getRoadparams()!=null)
                for(jaxb.Roadparam rp: network.getRoadparams().getRoadparam())
                    road_params.put(rp.getId(),rp);

            if (network.getRoadgeoms()!=null)
                for(jaxb.Roadgeom rg: network.getRoadgeoms().getRoadgeom())
                    road_geoms.put(rg.getId(),rg);

            if (network.getNodes()!=null)
                for(jaxb.Node jnode : network.getNodes().getNode())
                    nodes.put(jnode.getId(),new Node(jnode));

            if (network.getLinks()!=null)
                for(jaxb.Link jlink : network.getLinks().getLink()) {
                    AbstractLink link;

                    Roadparam rp = road_params.get(jlink.getRoadparam());
                    Roadgeom rg = road_geoms.get(jlink.getRoadgeom());

                    // extract managed lane and aux lane information from the road geometry

                    FDparams mng_fd = null;
                    FDparams aux_fd = null;
                    int aux_lanes = 0;
                    int mng_lanes = 0;
                    boolean mng_barrier = false;
                    boolean mng_separated = false;

                    if(rg!=null) {
                        for (jaxb.AddLanes addlane : rg.getAddLanes()) {
                            // inside->managed lane, outside->aux lane
                            jaxb.Roadparam alrp = road_params.get(addlane.getRoadparam());

                            Side side = Side.valueOf(addlane.getSide());
                            switch (side) {
                                case in:    // managed lane
                                    mng_fd = new FDparams(alrp.getCapacity(), alrp.getJamDensity(), alrp.getSpeed());
                                    mng_lanes = addlane.getLanes();
                                    mng_barrier = !addlane.isIsopen();
                                    break;
                                case out:   // aux lane
                                    aux_fd = new FDparams(alrp.getCapacity(), alrp.getJamDensity(), alrp.getSpeed());
                                    aux_lanes = addlane.getLanes();
                                    break;
                                default:
                                    break;

                            }
                        }
                    }

                    switch(jlink.getRoadType()){
                        case "offramp":
                            link = new LinkOfframp(jlink,rp,mng_lanes,mng_fd,mng_barrier,mng_separated);
                            break;
                        case "onramp":
                            link = new LinkOnramp(jlink,rp,mng_lanes,mng_fd,mng_barrier,mng_separated);
                            break;
                        case "freeway":
                            link = new LinkFreeway(jlink,rp,mng_lanes,mng_fd,mng_barrier,mng_separated,aux_lanes,aux_fd);
                            break;
                        case "connector":
                            link = new LinkConnector(jlink,rp,mng_lanes,mng_fd,mng_barrier,mng_separated,aux_lanes,aux_fd);
                            break;
                        default:
                            throw new Exception("Bad road type");
                    }

                    links.put(jlink.getId(),link);
                    nodes.get(jlink.getEndNodeId()).in_links.add(link.id);
                    nodes.get(jlink.getStartNodeId()).out_links.add(link.id);
                }
        }

        // commodities
        if(scenario.getCommodities()!=null)
            for(jaxb.Commodity comm : scenario.getCommodities().getCommodity())
                this.commodities.put(comm.getId(),new Commodity(comm.getId(),comm.getName(),comm.getPvequiv()));

  }

    protected Scenario clone() {
        Scenario jscn_cpy = new Scenario(this.my_fwy_scenario);

        for (Map.Entry<Long, Node> e : nodes.entrySet())
            jscn_cpy.nodes.put(e.getKey(), e.getValue().clone());

        for (Map.Entry<Long, AbstractLink> e : links.entrySet())
            jscn_cpy.links.put(e.getKey(), e.getValue().clone());

        for (Map.Entry<Long,Commodity> e : commodities.entrySet())
            jscn_cpy.commodities.put(e.getKey(),e.getValue().clone());

        return jscn_cpy;
    }

    /////////////////////////////////////
    // network
    /////////////////////////////////////

    public AbstractLink get_link_with_id(long id){
        return links.get(id);
    }

    public float compute_max_simdt_sec(float maxcelllength_meters){
        float simdt = Float.POSITIVE_INFINITY;
        for(AbstractLink link : links.values()){
            float r = link.params.length/maxcelllength_meters;
            int num_cells = OTMUtils.approximately_equals(r%1.0,0.0) ? (int) r :  1+((int) r);
            float cell_length_meters = link.params.length/num_cells;
            float vc_mps = link.get_fastest_ffspeed_kph() * 1000f / 3600f;
            float simdtl = cell_length_meters / vc_mps;
            simdt = Math.min(simdt ,simdtl );
        }
        return simdt;
    }

    public jaxb.Scenario to_jaxb() throws Exception {

        float simdt = my_fwy_scenario.get_sim_dt_sec();

        jaxb.Scenario jScn = new jaxb.Scenario();

        /////////////////////////////////////////////////////
        // models : Hard code single CTM model
        jaxb.Models models = new jaxb.Models();
        jScn.setModels(models);
        jaxb.Model model = new jaxb.Model();
        models.getModel().add(model);

        model.setIsDefault(true);
        model.setName("ctm");
        model.setType("ctm");
        ModelParams params = new ModelParams();
        params.setMaxCellLength(my_fwy_scenario.get_max_celllength_meters());
        params.setSimDt(simdt);
        model.setModelParams(params);

        /////////////////////////////////////////////////////
        // commodities
        jaxb.Commodities jComms = new jaxb.Commodities();
        jScn.setCommodities(jComms);
        for(Map.Entry<Long, Commodity> e : commodities.entrySet()){
            jaxb.Commodity jcomm = new jaxb.Commodity();
            jComms.getCommodity().add(jcomm);
            Commodity comm = e.getValue();
            jcomm.setId(comm.id);
            jcomm.setName(comm.name);
            jcomm.setPathfull(false);
            jcomm.setPvequiv(comm.pvequiv);
        }

        /////////////////////////////////////////////////////
        // network
        jaxb.Network jNet = new jaxb.Network();
        jScn.setNetwork(jNet);

        // nodes ........................
        jaxb.Nodes jNodes = new jaxb.Nodes();
        jNet.setNodes(jNodes);
        for(Node node : nodes.values()) {
            jaxb.Node jaxbNode = new jaxb.Node();
            jaxbNode.setId(node.id);
            jNodes.getNode().add(jaxbNode);
        }

        // read road parameters and geometries from the links ...........
        Map<Long, FDparamsAndRoadGeoms> link2paramsandgeoms = new HashMap<>();
        Set<FDparams> unique_params = new HashSet<>();
        Set<RoadGeom> unique_geoms = new HashSet<>();
        for(AbstractLink link : links.values()) {

            FDparamsAndRoadGeoms x = link.get_fdparams_and_roadgeoms();
            link2paramsandgeoms.put(link.id,x);

            // store gp road parameters
            unique_params.add( x.gpparams ) ;
            if(x.roadGeom.mng_fdparams!=null)
                unique_params.add( x.roadGeom.mng_fdparams );
            if(x.roadGeom.aux_fdparams!=null)
                unique_params.add( x.roadGeom.aux_fdparams );

            if(x.roadGeom.notEmpty())
                unique_geoms.add( x.roadGeom );
        }

        // map from params to its id ...................
        Map<FDparams,Long> param2id = new HashMap<>();
        long c = 0;
        for(FDparams rp : unique_params)
            param2id.put(rp,c++);

        // map from roadgeom to its id ...................
        Map<RoadGeom,Long> geom2id = new HashMap<>();
        c = 0;
        for(RoadGeom rg : unique_geoms)
            geom2id.put(rg,c++);

        // write road params .............................
        jaxb.Roadparams jRoadParams = new jaxb.Roadparams();
        jNet.setRoadparams(jRoadParams);
        for(FDparams fd : unique_params){
            jaxb.Roadparam rp = fd.to_jaxb();
            rp.setId(param2id.get(fd));
            jRoadParams.getRoadparam().add(rp);
        }

        // write road geoms ................................
        jaxb.Roadgeoms jRoadGeoms = new jaxb.Roadgeoms();
        jNet.setRoadgeoms(jRoadGeoms);
        for(RoadGeom rg : unique_geoms){
            jaxb.Roadgeom jrg = rg.to_jaxb(param2id);
            jrg.setId(geom2id.get(rg));
            jRoadGeoms.getRoadgeom().add(jrg);
        }

        // links .........................................
        jaxb.Links jLinks = new jaxb.Links();
        jNet.setLinks(jLinks);
        for(AbstractLink link : links.values()){
            jaxb.Link jaxbLink = new jaxb.Link();
            jLinks.getLink().add(jaxbLink);

            jaxbLink.setId(link.id);
            jaxbLink.setLength(link.get_length_meters());
            jaxbLink.setFullLanes(link.get_gp_lanes());
            jaxbLink.setEndNodeId(link.end_node_id);
            jaxbLink.setStartNodeId(link.start_node_id);
            jaxbLink.setRoadType(link.get_type()== AbstractLink.Type.ghost ?
                    AbstractLink.Type.freeway.toString() :
                    link.get_type().toString());

            // road params
            FDparamsAndRoadGeoms x = link2paramsandgeoms.get(link.id);
            jaxbLink.setRoadparam(param2id.get(x.gpparams));

            // road geoms
            if(x.roadGeom.notEmpty())
                jaxbLink.setRoadgeom(geom2id.get(x.roadGeom));
        }

        // road connections .....................................
        jaxb.Roadconnections jRCs = new jaxb.Roadconnections();
        jNet.setRoadconnections(jRCs);
        List<jaxb.Roadconnection> rcs =jRCs. getRoadconnection();

        Set<LinkGhost> ghostlinks = my_fwy_scenario.ghost_pieces==null?null:my_fwy_scenario.ghost_pieces.links;

        // for each link, generate road connections leaving
        for(AbstractLink up_link : links.values()){

            Segment up_segment = up_link.get_segment();
            AbstractLink dn_link = up_link.get_dn_link();

            if(dn_link==null)
                continue;

            // gp->gp and mng->mng for offramps, freeways, and connectors
            if( (up_link instanceof LinkFreewayOrConnector) || (up_link instanceof LinkOfframp) ) {

                // I) mng->mng
                if( up_link.has_mng() & dn_link.has_mng())
                    rcs.add( make_road_connection(my_fwy_scenario,up_link,LaneGroupType.mng,dn_link,LaneGroupType.mng) );

                // II) add gp-> gp
                if(ghostlinks!=null && (ghostlinks.contains(up_link) || ghostlinks.contains(dn_link)))
                    rcs.add( make_road_connection(my_fwy_scenario,up_link,null,dn_link,null) );
                else
                    rcs.add( make_road_connection(my_fwy_scenario,up_link,LaneGroupType.gp,dn_link,LaneGroupType.gp) );

            }

            // case freeway
            if(up_link instanceof LinkFreeway){

                // III) aux->aux
                if( (dn_link instanceof LinkFreeway) && up_link.has_aux() && dn_link.has_aux())
                    rcs.add( make_road_connection(my_fwy_scenario,up_link,LaneGroupType.aux,dn_link,LaneGroupType.aux) );

                // IV) mng->inner offramp or gp->inner offramp
                for(LinkOfframp offramp : up_segment.in_frs)
                    if( up_link.has_mng())
                        rcs.add( make_road_connection(my_fwy_scenario,up_link,LaneGroupType.mng,offramp,null) );
                    else
                        rcs.add( make_road_connection(my_fwy_scenario,up_link,LaneGroupType.gp,offramp,null) );


                // V) gp->outer offramp or aux->outer offramp
                for(LinkOfframp offramp : up_segment.out_frs)
                    if( up_link.has_aux() )
                        rcs.add( make_road_connection(my_fwy_scenario,up_link,LaneGroupType.aux,offramp,null) );
                    else
                        rcs.add( make_road_connection(my_fwy_scenario,up_link,LaneGroupType.gp,offramp,null) );

            }

            // case onramp
            if(up_link instanceof LinkOnramp) {

                assert (dn_link instanceof LinkFreeway);

                // VI) inner or -> fwy mng OR inner or ->fwy gp
                if (up_link.get_is_inner())
                    if (dn_link.has_mng())
                        rcs.add( make_road_connection(my_fwy_scenario, up_link, null, dn_link, LaneGroupType.mng) );
                    else
                        rcs.add( make_road_connection(my_fwy_scenario, up_link, null, dn_link, LaneGroupType.gp) );

                // VII) outer or -> fwy aux OR outer or->fwy gp
                if (!up_link.get_is_inner())
                    if (dn_link.has_aux())
                        rcs.add( make_road_connection(my_fwy_scenario, up_link, null, dn_link, LaneGroupType.aux) );
                    else
                        rcs.add( make_road_connection(my_fwy_scenario, up_link, null, dn_link, LaneGroupType.gp) );

            }

        }

        /////////////////////////////////////////////////////
        // demands
        jaxb.Demands jdemands = new jaxb.Demands();
        jScn.setDemands(jdemands);
        for(AbstractLink link : links.values()) {
            for (Map.Entry<Long, Profile1D> e : link.demands.entrySet()) {
                Long comm_id = e.getKey();
                Profile1D profile = e.getValue();

                jaxb.Demand jdemand = new jaxb.Demand();
                jdemands.getDemand().add(jdemand);
                jdemand.setCommodityId(comm_id);
                if(profile.dt!=null)
                    jdemand.setDt(profile.dt);
                jdemand.setStartTime(profile.start_time);
                jdemand.setLinkId(link.id);
                jdemand.setContent(OTMUtils.comma_format(profile.get_values()));
            }
        }

        /////////////////////////////////////////////////////
        // splits
        jaxb.Splits jsplits = new jaxb.Splits();
        jScn.setSplits(jsplits);

        for(Segment segment : my_fwy_scenario.segments.values()){

            if(segment.fwy.get_type()!= AbstractLink.Type.freeway)
                continue;

            List<LinkOfframp> frs = segment.get_frs();

            if(frs.isEmpty())
                continue;

            LinkFreeway up_ml = (LinkFreeway) segment.fwy;

            LinkFreewayOrConnector dn_ml = (LinkFreewayOrConnector) up_ml.dn_link;  // actually Freeway or Ghost

            Node node = this.nodes.get(up_ml.end_node_id);

            for(Commodity comm : commodities.values()){

                // collect the split profiles we have
                Map<Long,Profile1D> outlink2Profile = new HashMap<>();
                Set<Float> dts = new HashSet<>();
                int prof_size = Integer.MIN_VALUE;
                for(LinkOfframp fr : frs) {
                    Profile1D prof = fr.get_splits(comm.id, UserSettings.defaultSRDtMinutes*60);
                    outlink2Profile.put(fr.id, prof);
                    dts.add(prof.dt);
                    prof_size = Math.max(prof_size,prof.get_length());
                }

                if(dts.size()!=1)
                    System.err.println("RG)@J$G 2-43j");
                Float dt = dts.iterator().next();

                // pad all offramp profiles
                for(Profile1D profile : outlink2Profile.values())
                    profile.pad_to_length(prof_size);

                // profile for downstream mainline
                if(dn_ml!=null) {
                    Profile1D ml_prof = new Profile1D(0f, dt);
                    for (int i = 0; i < prof_size; i++) {
                        float value = 1f;
                        for (Profile1D fr_prof : outlink2Profile.values())
                            value -= fr_prof.get_ith_value(i);
                        if (value < 0)
                            throw new Exception(String.format("One node %d, commodity %d, offramp splits add up to more than 1.0", node.id, comm.id));
                        ml_prof.add_entry(value);
                    }
                    outlink2Profile.put(dn_ml.id, ml_prof);
                }

                // to jaxb: offramps
                jaxb.SplitNode jsplitnode = new jaxb.SplitNode();
                jsplits.getSplitNode().add(jsplitnode);
                jsplitnode.setCommodityId(comm.id);
                jsplitnode.setNodeId(node.id);
                jsplitnode.setLinkIn(up_ml.id);
                if(dt!=null)
                    jsplitnode.setDt(dt);
                for( Map.Entry<Long,Profile1D> e : outlink2Profile.entrySet() ){
                    jaxb.Split jsplit = new jaxb.Split();
                    jsplitnode.getSplit().add(jsplit);
                    jsplit.setLinkOut(e.getKey());
                    jsplit.setContent(OTMUtils.comma_format(e.getValue().get_values()));
                }

            }
        }

        /////////////////////////////////////////////////////
        // controllers, actuators, sensors

        // collect controllers and filter out invalid ones
        Set<ControlSchedule> schedules = links.values().stream()
                .flatMap(link->link.get_all_schedules().stream())
                .collect(Collectors.toSet());

        jaxb.Controllers jcntrls = new jaxb.Controllers();
        jScn.setControllers(jcntrls);

        jaxb.Actuators jacts = new jaxb.Actuators();
        jScn.setActuators(jacts);

        Map<AbstractController,Set<Sensor>> sensors = generate_sensors(schedules);

        for(ControlSchedule schedule : schedules){

            Set<AbstractLink> links_to_write = schedule.links_to_write();

            if(schedule.ignore(links_to_write))
                continue;

            // schedule controller
            jaxb.Controller jschcntrl = new jaxb.Controller();
            jcntrls.getController().add(jschcntrl);
            jschcntrl.setType("schedule");
            jschcntrl.setId(schedule.getId());

            // target actuator
            jaxb.Actuator jact = schedule.get_jaxb_actuator(links_to_write);
            jacts.getActuator().add(jact);
            long actuator_id = jact.getId();

            jaxb.TargetActuators tacts = new jaxb.TargetActuators();
            tacts.setIds(String.format("%d",actuator_id));
            jschcntrl.setTargetActuators(tacts);

            // entries
            jaxb.Schedule jsch = new jaxb.Schedule();
            jschcntrl.setSchedule(jsch);
            for(ScheduleEntry entry : schedule.get_entries()){
                float start_time = entry.get_start_time();
                float end_time = entry.get_end_time();

                jaxb.Entry jentry = new jaxb.Entry();
                jsch.getEntry().add(jentry);

                // dt
                if(entry.get_cntrl().getDt()!=null && !entry.get_cntrl().getDt().isInfinite())
                    jentry.setDt(entry.get_cntrl().getDt());

                // start time and end time
                jentry.setStartTime(start_time);
                if(Float.isFinite(end_time))
                    jentry.setEndTime(end_time);

                // controller
                AbstractController cntrl = entry.get_cntrl();
                jentry.setType(cntrl.getAlgorithm().toString());

                // sensors
                Set<Sensor> mysensors = sensors.get(cntrl);

                if(!mysensors.isEmpty()){
                    jaxb.FeedbackSensors jsns = new jaxb.FeedbackSensors();
                    jentry.setFeedbackSensors(jsns);
                    Set<Long> sensor_ids = mysensors.stream().map(s->s.id).collect(Collectors.toSet());
                    jsns.setIds(OTMUtils.comma_format(sensor_ids));
                }

                // controller parameters
                Collection<Parameter> jparamslist = cntrl.jaxb_parameters();
                if(!jparamslist.isEmpty()){
                    jaxb.Parameters jparams = new jaxb.Parameters();
                    jentry.setParameters(jparams);
                    jparams.getParameter().addAll(jparamslist);
                }

            }
        }

        jaxb.Sensors jsnss = new jaxb.Sensors();
        jScn.setSensors(jsnss);
        for(Set<Sensor> ss : sensors.values())
            for(Sensor s : ss)
                jsnss.getSensor().add(s.to_jaxb());

        /////////////////////////////////////////////////////
        // offramp flows
        for(Segment segment : my_fwy_scenario.segments.values()) {

            if (segment.fwy.get_type() != AbstractLink.Type.freeway)
                continue;

            LinkFreeway up_ml = (LinkFreeway) segment.fwy;

            List<LinkOfframp> all_frs = segment.get_frs();
            Set<Long> commids = all_frs.stream().flatMap(f->f.frflows.keySet().stream()).collect(Collectors.toSet());

            if(commids.isEmpty())
                continue;

            for(Long commid : commids){

                long ctrlid = my_fwy_scenario.new_schedule_id();

                Set<LinkOfframp> frs = all_frs.stream().filter(f->f.frflows.containsKey(commid)).collect(Collectors.toSet());

                // actuator ............................
                jaxb.Actuator act = new jaxb.Actuator();
                jacts.getActuator().add(act);
                act.setId(ctrlid);
                act.setType("split");

                jaxb.Parameters aps = new jaxb.Parameters();
                act.setParameters(aps);

                jaxb.Parameter ap1 = new jaxb.Parameter();
                aps.getParameter().add(ap1);
                ap1.setName("linkin");
                ap1.setValue(String.format("%d",up_ml.id));

                jaxb.Parameter ap2 = new jaxb.Parameter();
                aps.getParameter().add(ap2);
                ap2.setName("linksout");
                ap2.setValue(OTMUtils.comma_format(frs.stream().map(f->f.id).collect(Collectors.toSet())));

                jaxb.Parameter ap3 = new jaxb.Parameter();
                aps.getParameter().add(ap3);
                ap3.setName("comm");
                ap3.setValue(String.format("%d",commid));

                // controller ..............................
                jaxb.Controller ctrl = new jaxb.Controller();
                jcntrls.getController().add(ctrl);
                ctrl.setId(ctrlid);
                ctrl.setType("frflow");
                ctrl.setDt(simdt);
                ctrl.setStartTime(0f);

                jaxb.TargetActuators ta = new jaxb.TargetActuators();
                ctrl.setTargetActuators(ta);
                ta.setIds(String.format("%d",ctrlid));

                jaxb.Profiles profs = new jaxb.Profiles();
                ctrl.setProfiles(profs);

                for(LinkOfframp fr : frs){
                    if(!fr.frflows.containsKey(commid))
                        continue;
                    jaxb.Profile prof = new jaxb.Profile();
                    profs.getProfile().add(prof);
                    prof.setStartTime(fr.get_use_fr_flows()?30f:100000f);
                    prof.setDt(300f);
                    prof.setId(fr.id);
                    prof.setContent(OTMUtils.comma_format(fr.frflows.get(commid).values));
                }
            }
        }

        /////////////////////////////////////////////////////
        // lane change model
        jScn.setLanechanges(this.my_fwy_scenario.lcmodel.to_jaxb());

        return jScn;
    }

    /////////////////////////////////////
    // private
    /////////////////////////////////////

    private Map<AbstractController,Set<Sensor>> generate_sensors(Set<ControlSchedule> schs){
        Map<AbstractController,Set<Sensor>> X = new HashMap<>();
        long sensor_id = 0;
        for(ControlSchedule sch : schs){
            for(ScheduleEntry entry : sch.get_entries()){
                Set<Sensor> cntr_sensors = entry.get_cntrl().get_sensors();
                for(Sensor sensor : cntr_sensors)
                    sensor.id =sensor_id++;
                X.put(entry.get_cntrl(),cntr_sensors);
            }

        }
        return X;

    }

    private jaxb.Roadconnection make_road_connection(FreewayScenario scn, AbstractLink in_link, LaneGroupType in_lg, AbstractLink out_link, LaneGroupType out_lg){
        jaxb.Roadconnection rc = new jaxb.Roadconnection();

        rc.setId(scn.new_rc_id());
        rc.setInLink(in_link.id);
        if(in_lg!=null)
            rc.setInLinkLanes(lanestring(in_link,in_lg));
        rc.setOutLink(out_link.id);
        if(out_lg!=null)
            rc.setOutLinkLanes(lanestring(out_link,out_lg));

        return rc;
    }

    private String lanestring(AbstractLink link ,LaneGroupType lg){
        int[] lanes = link.lgtype2lanes(lg);
        return String.format("%d#%d",lanes[0],lanes[1]);
    }

}
