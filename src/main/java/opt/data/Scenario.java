package opt.data;

import geometry.Side;
import jaxb.ModelParams;
import jaxb.Roadgeom;
import jaxb.Roadparam;
import opt.data.control.*;
import profiles.Profile1D;
import utils.OTMUtils;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class Scenario {

    protected FreewayScenario my_fwy_scenario;
    protected Map<Long, Node> nodes = new HashMap<>();
    protected Map<Long, AbstractLink> links = new HashMap<>();
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

    public jaxb.Scenario to_jaxb() throws Exception {
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
        params.setMaxCellLength(100f);
        params.setSimDt(2f);
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
        Map<Long,AddlanesAndRoadParams> link2addlanesAndParams = new HashMap<>();
        Set<FDparams> unique_roadparams = new HashSet<>();
        Set<RoadGeom> unique_roadgeoms = new HashSet<>();
        for(AbstractLink link : links.values()) {
            AddlanesAndRoadParams x = link.get_addlanes_and_roadparams();
            link2addlanesAndParams.put(link.id,x);

            // store gp road parameters
            unique_roadparams.add(x.gpparams) ;
            if(x.roadGeom.mng_fdparams!=null)
                unique_roadparams.add( x.roadGeom.mng_fdparams);
            if(x.roadGeom.aux_fdparams!=null)
                unique_roadparams.add(  x.roadGeom.aux_fdparams);

            if(x.roadGeom.notEmpty())
                unique_roadgeoms.add(x.roadGeom);
        }

        // map from params to its id ...................
        Map<FDparams,Long> param2id = new HashMap<>();
        long c = 0;
        for(FDparams rp : unique_roadparams)
            param2id.put(rp,c++);

        // map from roadgeom to its id ...................
        Map<RoadGeom,Long> geom2id = new HashMap<>();
        c = 0;
        for(RoadGeom rg : unique_roadgeoms)
            geom2id.put(rg,c++);

        // write road params .............................
        jaxb.Roadparams jRoadParams = new jaxb.Roadparams();
        jNet.setRoadparams(jRoadParams);
        for(FDparams fd : unique_roadparams){
            jaxb.Roadparam rp = fd.to_jaxb();
            rp.setId(param2id.get(fd));
            jRoadParams.getRoadparam().add(rp);
        }

        // write road geoms ................................
        jaxb.Roadgeoms jRoadGeoms = new jaxb.Roadgeoms();
        jNet.setRoadgeoms(jRoadGeoms);
        for(RoadGeom rg : unique_roadgeoms){
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
            jaxbLink.setRoadType(link.get_type().toString());

            // road params
            AddlanesAndRoadParams x = link2addlanesAndParams.get(link.id);
            jaxbLink.setRoadparam(param2id.get(x.gpparams));

            // road geoms
            if(x.roadGeom.notEmpty())
                jaxbLink.setRoadgeom(geom2id.get(x.roadGeom));
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
        for(Node node : nodes.values() ) {

            // get offramp links ........................
            Set<AbstractLink> outlinks = node.out_links.stream()
                    .map(link_id->links.get(link_id))
                    .collect(toSet());

            Set<LinkOfframp> frs = outlinks.stream()
                    .filter(link->link instanceof LinkOfframp)
                    .map(link-> (LinkOfframp) link)
                    .collect(toSet());

            if(frs.isEmpty())
                continue;

            // get upstream mainline ........................
            Set<LinkFreeway> in_links = node.in_links.stream()
                    .map(link_id->links.get(link_id))
                    .filter(link->link instanceof LinkFreeway)
                    .map(link-> (LinkFreeway) link)
                    .collect(toSet());

            if(in_links.isEmpty() || in_links.size()>1)
                System.err.println("90443j2f");

            LinkFreeway up_ml = in_links.iterator().next();

            // get downstream mainline ........................
            Set<LinkFreeway> outfreeway = outlinks.stream()
                    .filter(link->link instanceof LinkFreeway)
                    .map(link-> (LinkFreeway) link)
                    .collect(toSet());

            if(outfreeway.size()>1)
                System.err.println("h45-oh35g");

            LinkFreeway dn_ml = outfreeway.isEmpty() ? null : outfreeway.iterator().next();

            Set<Long> comm_ids = frs.stream()
                    .flatMap(fr->fr.splits.keySet().stream())
                    .collect(toSet());

            for(Long comm_id : comm_ids){

                // collect the split profiles we have
                Map<Long,Profile1D> outlink2Profile = new HashMap<>();
                Set<Float> dts = new HashSet<>();
                Set<Integer> prof_sizes = new HashSet<>();
                for(LinkOfframp fr : frs)
                    if(fr.splits.containsKey(comm_id)) {
                        Profile1D prof = fr.splits.get(comm_id);
                        outlink2Profile.put(fr.id,prof);
                        dts.add(prof.dt);
                        prof_sizes.add(prof.get_length());
                    }

                if(dts.size()!=1)
                    System.err.println("RG)@J$G 2-43j");
                if(prof_sizes.size()!=1)
                    System.err.println("RG)@J$G 2-43j");
                float dt = dts.iterator().next();
                int prof_size = prof_sizes.iterator().next();

                // profile for downstream mainline
                Profile1D ml_prof = new Profile1D(0f,dt);
                for(int i=0;i<prof_size;i++){
                    float value = 1f;
                    for(Profile1D fr_prof : outlink2Profile.values())
                        value -= fr_prof.get_ith_value(i);
                    if(value<0)
                        throw new Exception(String.format("One node %d, commodity %d, offramp splits add up to more than 1.0",node.id,comm_id));
                    ml_prof.add(value);
                }
                outlink2Profile.put(dn_ml.id,ml_prof);

                // to jaxb
                jaxb.SplitNode jsplitnode = new jaxb.SplitNode();
                jsplits.getSplitNode().add(jsplitnode);
                jsplitnode.setCommodityId(comm_id);
                jsplitnode.setNodeId(node.id);
                jsplitnode.setLinkIn(up_ml.id);
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
        // controllers
        List<AbstractController> controllers = my_fwy_scenario.controller_schedule.items;
        jaxb.Controllers jcntrls = new jaxb.Controllers();
        if(!controllers.isEmpty())
            jScn.setControllers(jcntrls);
        for(AbstractController cntrl : controllers)
            jcntrls.getController().add(cntrl.to_jaxb());

        /////////////////////////////////////////////////////
        // actuators
        jaxb.Actuators jacts = new jaxb.Actuators();
        Set<AbstractActuator> actuators = controllers.stream()
                .flatMap(x->x.get_actuators().values().stream())
                .collect(toSet());

        if(!actuators.isEmpty())
            jScn.setActuators(jacts);
        for(AbstractActuator act : actuators)
            jacts.getActuator().add(act.to_jaxb());

        /////////////////////////////////////////////////////
        // sensors
        jaxb.Sensors jsens = new jaxb.Sensors();
        Set<Sensor> sensors = controllers.stream()
                .flatMap(x->x.get_sensors().values().stream())
                .collect(toSet());
        if(!sensors.isEmpty())
            jScn.setSensors(jsens);
        for(Sensor sns : sensors)
            jsens.getSensor().add(sns.to_jaxb());

        return jScn;

    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scenario scenario = (Scenario) o;
        return nodes.equals(scenario.nodes) &&
                links.equals(scenario.links) &&
                commodities.equals(scenario.commodities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, links, commodities);
    }

}
