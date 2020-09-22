package opt.tests;

import opt.data.*;
import opt.OTMTask;
import opt.utils.Misc;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.fail;

public class TestSimData extends AbstractTest {

    final long car = 0l;
    final long truck = 1l;
    final long linkid = 3l;
    final long routeA = 1l;
    final long routeB = 2l;
    SimDataScenario simdata;
    SimDataLink simdatalink;
    boolean celloutput = true;

    @Before
    public void test_setup(){

        // load
        OTMTask task = null;
//        String project_file_name = get_test_fullpath("project.opt");
        String project_file_name = "/home/gomes/Downloads/issue_165.opt";
        boolean validate = true;
        try {
            Project project = ProjectFactory.load_project(project_file_name,validate);
            FreewayScenario fwyscenario = project.get_scenarios().iterator().next();
            fwyscenario.set_start_time(0f);
            fwyscenario.set_sim_duration(5400f);
            task = new OTMTask(null,fwyscenario,300f,10, celloutput,!celloutput,null);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // run and retrieve data
        simdata = task.run_simulation(null);
        simdatalink = simdata.linkdata.get(linkid);
    }

    /////////////////////////////////
    // scenario level
    /////////////////////////////////

    @Test
    public void network_vht(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_vht_for_network(Misc.hashset(car)).get_XYSeries("cars"));
        A.addSeries(simdata.get_vht_for_network(Misc.hashset(truck)).get_XYSeries("truck"));
        A.addSeries(simdata.get_vht_for_network(null).get_XYSeries("all"));
        TestPlot.plot(A,
                "network VHT",
                "veh hours",
                String.format("temp/%s_net_vht.png",celloutput));
    }

    @Test
    public void network_vmt(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_vmt_for_network(Misc.hashset(car)).get_XYSeries("cars"));
        A.addSeries(simdata.get_vmt_for_network(Misc.hashset(truck)).get_XYSeries("truck"));
        A.addSeries(simdata.get_vmt_for_network(null).get_XYSeries("all"));
        TestPlot.plot(A,
                "network VMT",
                "veh miles",
                String.format("temp/%s_net_vmt.png",celloutput));

    }

    @Test
    public void network_delay(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_delay_for_network(null,20f).get_XYSeries("20 mph"));
        A.addSeries(simdata.get_delay_for_network(null,35f).get_XYSeries("35 mph"));
        A.addSeries(simdata.get_delay_for_network(null,50f).get_XYSeries("50 mph"));
        TestPlot.plot(A,
                "network delay",
                "veh hours",
                String.format("temp/%s_net_delay.png",celloutput));
    }

    @Test
    public void network_delay_sources(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_delay_for_network_sources(null,20f).get_XYSeries("20 mph"));
        A.addSeries(simdata.get_delay_for_network_sources(null,35f).get_XYSeries("35 mph"));
        A.addSeries(simdata.get_delay_for_network_sources(null,50f).get_XYSeries("50 mph"));
        TestPlot.plot(A,
                "network delay",
                "veh hours",
                String.format("temp/%s_net_delay_sources.png",celloutput));
    }

    @Test
    public void network_delay_nonsources(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_delay_for_network_nonsources(null,20f).get_XYSeries("20 mph"));
        A.addSeries(simdata.get_delay_for_network_nonsources(null,35f).get_XYSeries("35 mph"));
        A.addSeries(simdata.get_delay_for_network_nonsources(null,50f).get_XYSeries("50 mph"));
        TestPlot.plot(A,
                "network delay",
                "veh hours",
                String.format("temp/%s_net_delay_nonsources.png",celloutput));
    }

    @Test
    public void network_vehicles(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_vehs_for_network(Misc.hashset(car)).get_XYSeries("cars"));
        A.addSeries(simdata.get_vehs_for_network(Misc.hashset(truck)).get_XYSeries("truck"));
        A.addSeries(simdata.get_vehs_for_network(null).get_XYSeries("all"));
        TestPlot.plot(A,
                "network vehicles",
                "vehs",
                String.format("temp/%s_net_veh.png",celloutput));
    }

    @Test
    public void route_vehicles(){

        // cars and truck on route A GP lanes
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_vehs_for_route(routeA, Misc.hashset(LaneGroupType.gp), Misc.hashset(car)).get_XYSeries("cars route A"));
        A.addSeries(simdata.get_vehs_for_route(routeA, Misc.hashset(LaneGroupType.gp), Misc.hashset(truck)).get_XYSeries("trucks route A"));
        TestPlot.plot(A,
                "route A GP lanes",
                "vehs",
                String.format("temp/%s_routeA_veh.png",celloutput));

        // cars and truck on route B GP lanes
        A = new XYSeriesCollection();
        A.addSeries(simdata.get_vehs_for_route(routeB,Misc.hashset(LaneGroupType.gp), Misc.hashset(car)).get_XYSeries("cars route B"));
        A.addSeries(simdata.get_vehs_for_route(routeB,Misc.hashset(LaneGroupType.gp), Misc.hashset(truck)).get_XYSeries("trucks route B"));
        TestPlot.plot(A,
                "route B GP lanes",
                "vehs",
                String.format("temp/%s_routeB_veh.png",celloutput));

        // all vehicles on route A in all lanes
        A = new XYSeriesCollection();
        A.addSeries(simdata.get_vehs_for_route(routeA,null,null).get_XYSeries("all vehicles"));
        TestPlot.plot(A,
                "route A in all lanes",
                "vehs",
                String.format("temp/%s_routeA_veh_agg.png",celloutput));

    }

    @Test
    public void network_speeds(){
        TestPlot.plot(simdata.get_speed_for_network().get_XYSeries(""),
                "network speeds",
                "speed [mph]",
                String.format("temp/%s_net_speed.png",celloutput));

    }

    @Test
    public void route_speeds(){

        // get speed on route A for various lane group types
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_speed_for_route(routeA,Misc.hashset(LaneGroupType.gp)).get_XYSeries("gp lanes"));
        A.addSeries(simdata.get_speed_for_route(routeA,Misc.hashset(LaneGroupType.mng)).get_XYSeries("mng lanes"));
        A.addSeries(simdata.get_speed_for_route(routeA,Misc.hashset(LaneGroupType.aux)).get_XYSeries("aux lanes"));
        TestPlot.plot(A,
                "route A speed",
                "speed [mph]",
                String.format("temp/%s_routeA_speed.png",celloutput));

        // get speed on route for all lane group types
        A = new XYSeriesCollection();
        A.addSeries(simdata.get_speed_for_route(routeA,null).get_XYSeries("all lanes"));
        TestPlot.plot(A,
                "route A speed",
                "speed [mph]",
                String.format("temp/%s_routeA_speed_all.png",celloutput));
    }

    @Test
    public void route_contours(){

        long routeid = 3l;

        Set<LaneGroupType> gplgset = new HashSet<>();
        gplgset.add(LaneGroupType.gp);
        gplgset.add(LaneGroupType.aux);

        Set<LaneGroupType> mnglgset = new HashSet<>();
        mnglgset.add(LaneGroupType.mng);

//        TimeMatrix DTYgp = simdata.get_density_contour_for_route(routeid, gplgset,null);
//        TimeMatrix FLWgp = simdata.get_flow_contour_for_route(routeid,gplgset,null);
//        TimeMatrix SPDgp = simdata.get_speed_contour_for_route(routeid, gplgset);
        TimeMatrix DTYmng = simdata.get_density_contour_for_route(routeid, mnglgset,null);
//        TimeMatrix FLWmng = simdata.get_flow_contour_for_route(routeid,mnglgset,null);
//        TimeMatrix SPDmng = simdata.get_speed_contour_for_route(routeid, mnglgset);

//        System.out.println(String.format("DTYgp\t%d\t%d",DTYgp.time.length,DTYgp.space.size()));
//        System.out.println(String.format("FLWgp\t%d\t%d",FLWgp.time.length,FLWgp.space.size()));
//        System.out.println(String.format("SPDgp\t%d\t%d",SPDgp.time.length,SPDgp.space.size()));
//        System.out.println(String.format("DTYmng\t%d\t%d",DTYmng.time.length,DTYmng.space.size()));
//        System.out.println(String.format("FLWmng\t%d\t%d",FLWmng.time.length,FLWmng.space.size()));
//        System.out.println(String.format("SPDmng\t%d\t%d",SPDmng.time.length,SPDmng.space.size()));

    }


    /////////////////////////////////
    // link level
    /////////////////////////////////

    @Test
    public void link_vht(){
        for(Map.Entry<Long,SimDataLink> e :  simdata.linkdata.entrySet()){
            Long linkid = e.getKey();
            SimDataLink data = e.getValue();
            XYSeriesCollection A = new XYSeriesCollection();
            A.addSeries(data.get_vht(null,null).get_XYSeries("all"));
            TestPlot.plot(A,
                    String.format("Link %d",linkid),
                    "veh hours",
                    String.format("temp/%s_link%d_vht.png",celloutput,linkid));

        }
    }

    @Test
    public void link_vmt(){
        for(Map.Entry<Long,SimDataLink> e :  simdata.linkdata.entrySet()){
            Long linkid = e.getKey();
            SimDataLink data = e.getValue();
            XYSeriesCollection A = new XYSeriesCollection();
            A.addSeries(data.get_vmt(null,null).get_XYSeries("all"));
            TestPlot.plot(A,
                    String.format("Link %d",linkid),
                    "veh miles",
                    String.format("temp/%s_link%d_vmt.png",celloutput,linkid));
        }
    }

    @Test
    public void link_delay(){
        for(Map.Entry<Long,SimDataLink> e :  simdata.linkdata.entrySet()){
            Long linkid = e.getKey();
            SimDataLink data = e.getValue();
            XYSeriesCollection A = new XYSeriesCollection();
            A.addSeries(data.get_delay(null,null,30f).get_XYSeries("all"));
            TestPlot.plot(A,
                    String.format("Link %d",linkid),
                    "veh hours",
                    String.format("temp/%s_link%d_delay.png",celloutput,linkid));
        }
    }

    @Test
    public void link_vehicles(){
        for(Map.Entry<Long,SimDataLink> e :  simdata.linkdata.entrySet()){
            Long linkid = e.getKey();

            if(linkid!=4l)
                continue;

            SimDataLink data = e.getValue();
            XYSeriesCollection A = new XYSeriesCollection();
            A.addSeries(data.get_veh(null,null).get_XYSeries("all"));
            TestPlot.plot(A,
                    String.format("Link %d",linkid),
                    "vehicles",
                    String.format("temp/%s_link%d_veh.png",celloutput,linkid));
        }
    }

    @Test
    public void link_flow_exiting(){
        for(Map.Entry<Long,SimDataLink> e :  simdata.linkdata.entrySet()){
            Long linkid = e.getKey();

            if(linkid!=4l)
                continue;


            SimDataLink data = e.getValue();
            XYSeriesCollection A = new XYSeriesCollection();
            A.addSeries(data.get_flw(null,Misc.hashset(car)).get_XYSeries("cars"));
            A.addSeries(data.get_flw(null,Misc.hashset(truck)).get_XYSeries("trucks"));
            TestPlot.plot(A,
                    String.format("Link %d",linkid),
                    "flw [vph]",
                    String.format("temp/%s_link%d_flw.png",celloutput, linkid));
        }
    }

    @Test
    public void link_speeds(){
        for(Map.Entry<Long,SimDataLink> e :  simdata.linkdata.entrySet()){
            Long linkid = e.getKey();

            if(linkid!=4l)
                continue;


            SimDataLink data = e.getValue();
            XYSeriesCollection A = new XYSeriesCollection();
            A.addSeries(data.get_speed(null).get_XYSeries(""));
            TestPlot.plot(A,
                    String.format("Link %d",linkid),
                    "speed [mph]",
                    String.format("temp/%s_link%d_spd.png",celloutput,linkid));
        }
    }

    @Test
    public void link1_vehicles(){

        // get cars on various lane group types
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_veh(Misc.hashset(LaneGroupType.gp),Misc.hashset(car)).get_XYSeries("cars in gp lanes"));
        A.addSeries(simdatalink.get_veh(Misc.hashset(LaneGroupType.mng),Misc.hashset(car)).get_XYSeries("cars in mng lanes"));
        A.addSeries(simdatalink.get_veh(Misc.hashset(LaneGroupType.aux),Misc.hashset(car)).get_XYSeries("cars in aux lanes"));
        TestPlot.plot(A,
                "link 1 cars vehicles",
                "veh",
                String.format("temp/%s_link1_veh_car.png",celloutput,linkid));

        // get VEHICLES on given lane group type and all commodities
        A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_veh(Misc.hashset(LaneGroupType.gp),null).get_XYSeries("gp lanes"));
        A.addSeries(simdatalink.get_veh(Misc.hashset(LaneGroupType.mng),null).get_XYSeries("mng lanes"));
        A.addSeries(simdatalink.get_veh(Misc.hashset(LaneGroupType.aux),null).get_XYSeries("aux lanes"));
        TestPlot.plot(A,
                "link 1 all vehicles",
                "veh",
                String.format("temp/%s_link1_veh_agg1.png",celloutput,linkid));

        // get VEHICLES on all lane groups and commodities
        A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_veh(null,null).get_XYSeries("all lanes"));
        TestPlot.plot(A,
                "link 1 all vehicles",
                "veh",
                String.format("temp/%s_link1_veh_agg2.png",celloutput,linkid));

    }

    @Test
    public void link1_flows(){

        // get FLOWS on given lane group type and commodity
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_flw(Misc.hashset(LaneGroupType.gp),Misc.hashset(car)).get_XYSeries("cars in gp lanes"));
        A.addSeries(simdatalink.get_flw(Misc.hashset(LaneGroupType.mng),Misc.hashset(car)).get_XYSeries("cars in mng lanes"));
        A.addSeries(simdatalink.get_flw(Misc.hashset(LaneGroupType.aux),Misc.hashset(car)).get_XYSeries("cars in aux lanes"));
        TestPlot.plot(A,
                "link 1 car flows",
                "flw [vph]",
                String.format("temp/%s_link1_flw_car.png",celloutput,linkid));

        // get FLOWS on given lane group type and all commodities
        A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_flw(Misc.hashset(LaneGroupType.gp),null).get_XYSeries("gp lanes"));
        A.addSeries(simdatalink.get_flw(Misc.hashset(LaneGroupType.mng),null).get_XYSeries("mng lanes"));
        A.addSeries(simdatalink.get_flw(Misc.hashset(LaneGroupType.aux),null).get_XYSeries("aux lanes"));
        TestPlot.plot(A,
                "link 1 all flows",
                "flw [vph]",
                String.format("temp/%s_link1_flw_agg1.png",celloutput,linkid));

        // get FLOWS on all lane groups and commodities
        A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_flw(null,null).get_XYSeries("all lanes"));
        TestPlot.plot(A,
                "link 1 all flows",
                "flw [vph]",
                String.format("temp/%s_link1_flw_agg2.png",celloutput,linkid));
    }

    @Test
    public void link1_speeds(){

        // get SPEEDS on given lane group type
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_speed(Misc.hashset(LaneGroupType.gp)).get_XYSeries("gp lanes"));
        A.addSeries(simdatalink.get_speed(Misc.hashset(LaneGroupType.mng)).get_XYSeries("mng lanes"));
        A.addSeries(simdatalink.get_speed(Misc.hashset(LaneGroupType.aux)).get_XYSeries("aux lanes"));
        TestPlot.plot(A,
                "link 1 speed",
                "speed [mph]",
                String.format("temp/%s_link1_speed.png",celloutput,linkid));

        // get SPEEDS on all lane group types
        A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_speed(null).get_XYSeries("all lanes"));
        TestPlot.plot(A,
                "link 1 speed",
                "speed [mph]",
                String.format("temp/%s_link1_speed_agg.png",celloutput,linkid));
    }

//    @Test
//    public void test_parallel(){
//        Thread th = new Thread(load_task());
//        th.setDaemon(true);
//        th.start();
//    }

}
