package opt.tests;

import opt.data.*;
import opt.OTMTask;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.fail;

public class TestSimData extends AbstractTest {

    final long car = 0l;
    final long truck = 1l;
    final long linkid = 3l;
    final long routeA = 3l;
    final long routeB = 2l;
    SimDataScenario simdata;
    SimDataLink simdatalink;

    @Before
    public void test_setup(){

        // load
        OTMTask task = null;
//        String project_file_name = get_test_fullpath("project.opt");
        String project_file_name = "/home/gomes/Desktop/xxx/opt_line.opt";
        boolean validate = true;
        try {
            Project project = ProjectFactory.load_project(project_file_name,validate);
            FreewayScenario fwyscenario = project.get_scenarios().iterator().next();
            fwyscenario.set_start_time(0f);
            fwyscenario.set_sim_duration(3600f);
            task = new OTMTask(null,fwyscenario,300f,10, false,true,null);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // run and retrieve data
        simdata = task.run_simulation(null,true,true);
        simdatalink = simdata.linkdata.get(linkid);
    }

    /////////////////////////////////
    // scenario level
    /////////////////////////////////

    @Test
    public void network_vht(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_vht_for_network(car).get_XYSeries("cars"));
        A.addSeries(simdata.get_vht_for_network(truck).get_XYSeries("truck"));
        A.addSeries(simdata.get_vht_for_network(null).get_XYSeries("all"));
        TestPlot.plot(A,
                "network VHT",
                "veh hours",
                "temp/net_vht.png");
    }

    @Test
    public void network_vmt(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_vmt_for_network(car).get_XYSeries("cars"));
        A.addSeries(simdata.get_vmt_for_network(truck).get_XYSeries("truck"));
        A.addSeries(simdata.get_vmt_for_network(null).get_XYSeries("all"));
        TestPlot.plot(A,
                "network VMT",
                "veh miles",
                "temp/net_vmt.png");
    }

    @Test
    public void network_delay(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_delay_for_network(20f).get_XYSeries("20 mph"));
        A.addSeries(simdata.get_delay_for_network(35f).get_XYSeries("35 mph"));
        A.addSeries(simdata.get_delay_for_network(50f).get_XYSeries("50 mph"));
        TestPlot.plot(A,
                "network delay",
                "veh hours",
                "temp/net_delay.png");
    }

    @Test
    public void network_delay_sources(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_delay_for_network_sources(20f).get_XYSeries("20 mph"));
        A.addSeries(simdata.get_delay_for_network_sources(35f).get_XYSeries("35 mph"));
        A.addSeries(simdata.get_delay_for_network_sources(50f).get_XYSeries("50 mph"));
        TestPlot.plot(A,
                "network delay",
                "veh hours",
                "temp/net_delay_sources.png");
    }


    @Test
    public void network_delay_nonsources(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_delay_for_network_nonsources(20f).get_XYSeries("20 mph"));
        A.addSeries(simdata.get_delay_for_network_nonsources(35f).get_XYSeries("35 mph"));
        A.addSeries(simdata.get_delay_for_network_nonsources(50f).get_XYSeries("50 mph"));
        TestPlot.plot(A,
                "network delay",
                "veh hours",
                "temp/net_delay_nonsources.png");
    }

    @Test
    public void network_vehicles(){
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_vehs_for_network(car).get_XYSeries("cars"));
        A.addSeries(simdata.get_vehs_for_network(truck).get_XYSeries("truck"));
        A.addSeries(simdata.get_vehs_for_network(null).get_XYSeries("all"));
        TestPlot.plot(A,
                "network vehicles",
                "vehs",
                "temp/net_veh.png");
    }

    @Test
    public void route_vehicles(){

        // cars and truck on route A GP lanes
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_vehs_for_route(routeA,LaneGroupType.gp, car).get_XYSeries("cars route A"));
        A.addSeries(simdata.get_vehs_for_route(routeA,LaneGroupType.gp, truck).get_XYSeries("trucks route A"));
        TestPlot.plot(A,
                "route A GP lanes",
                "vehs",
                "temp/routeA_veh.png");

        // cars and truck on route B GP lanes
        A = new XYSeriesCollection();
        A.addSeries(simdata.get_vehs_for_route(routeB,LaneGroupType.gp, car).get_XYSeries("cars route B"));
        A.addSeries(simdata.get_vehs_for_route(routeB,LaneGroupType.gp, truck).get_XYSeries("trucks route B"));
        TestPlot.plot(A,
                "route B GP lanes",
                "vehs",
                "temp/routeB_veh.png");

        // all vehicles on route A in all lanes
        A = new XYSeriesCollection();
        A.addSeries(simdata.get_vehs_for_route(routeA,null,null).get_XYSeries("all vehicles"));
        TestPlot.plot(A,
                "route A in all lanes",
                "vehs",
                "temp/routeA_veh_agg.png");

    }

    @Test
    public void network_speeds(){
        TestPlot.plot(simdata.get_speed_for_network().get_XYSeries(""),
                "network speeds",
                "speed [mph]",
                "temp/net_speed.png");
    }

    @Test
    public void route_speeds(){

        // get speed on route A for various lane group types
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdata.get_speed_for_route(routeA,LaneGroupType.gp).get_XYSeries("gp lanes"));
        A.addSeries(simdata.get_speed_for_route(routeA,LaneGroupType.mng).get_XYSeries("mng lanes"));
        A.addSeries(simdata.get_speed_for_route(routeA,LaneGroupType.aux).get_XYSeries("aux lanes"));
        TestPlot.plot(A,
                "route A speed",
                "speed [mph]",
                "temp/routeA_speed.png");

        // get speed on route for all lane group types
        A = new XYSeriesCollection();
        A.addSeries(simdata.get_speed_for_route(routeA,null).get_XYSeries("all lanes"));
        TestPlot.plot(A,
                "route A speed",
                "speed [mph]",
                "temp/routeA_speed_all.png");
    }

    @Test
    public void route_contour(){
        TimeSeriesList X = simdata.get_speed_contour_for_route(routeA,LaneGroupType.mng);

        System.out.println(X.print_time());
        System.out.println(X.print_space());
        System.out.println(X.print_values());
    }

    @Test
    public void route_delay(){
//        // get delay on route A for various lane group types
//        XYSeriesCollection A = new XYSeriesCollection();
//        A.addSeries(simdata.get_speed_for_route(routeA,LaneGroupType.gp).get_XYSeries("gp lanes"));
//        A.addSeries(simdata.get_speed_for_route(routeA,LaneGroupType.mng).get_XYSeries("mng lanes"));
//        A.addSeries(simdata.get_speed_for_route(routeA,LaneGroupType.aux).get_XYSeries("aux lanes"));
//        TestPlot.plot(A,
//                "route A speed",
//                "speed [mph]",
//                "temp/routeA_speed.png");
//
//        // get speed on route for all lane group types
//        A = new XYSeriesCollection();
//        A.addSeries(simdata.get_speed_for_route(routeA,null).get_XYSeries("all lanes"));
//        TestPlot.plot(A,
//                "route A speed",
//                "speed [mph]",
//                "temp/routeA_speed_all.png");
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
                    String.format("temp/link%d_vht.png",linkid));
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
                    String.format("temp/link%d_vmt.png",linkid));
        }
    }

    @Test
    public void link_delay(){
        for(Map.Entry<Long,SimDataLink> e :  simdata.linkdata.entrySet()){
            Long linkid = e.getKey();
            SimDataLink data = e.getValue();
            XYSeriesCollection A = new XYSeriesCollection();
            A.addSeries(data.get_delay(null,30f).get_XYSeries("all"));
            TestPlot.plot(A,
                    String.format("Link %d",linkid),
                    "veh hours",
                    String.format("temp/link%d_delay.png",linkid));
        }
    }

    @Test
    public void link_vehicles(){
        for(Map.Entry<Long,SimDataLink> e :  simdata.linkdata.entrySet()){
            Long linkid = e.getKey();
            SimDataLink data = e.getValue();
            XYSeriesCollection A = new XYSeriesCollection();
            A.addSeries(data.get_veh(null,null).get_XYSeries("all"));
            TestPlot.plot(A,
                    String.format("Link %d",linkid),
                    "vehicles",
                    String.format("temp/link%d_veh.png",linkid));
        }
    }

    @Test
    public void link_flows(){
        for(Map.Entry<Long,SimDataLink> e :  simdata.linkdata.entrySet()){
            Long linkid = e.getKey();
            SimDataLink data = e.getValue();
            XYSeriesCollection A = new XYSeriesCollection();
            A.addSeries(data.get_flw_exiting(null,car).get_XYSeries("cars"));
            A.addSeries(data.get_flw_exiting(null,truck).get_XYSeries("trucks"));
            TestPlot.plot(A,
                    String.format("Link %d",linkid),
                    "flw [vph]",
                    String.format("temp/link%d_flw.png",linkid));
        }
    }

    @Test
    public void link_speeds(){
        for(Map.Entry<Long,SimDataLink> e :  simdata.linkdata.entrySet()){
            Long linkid = e.getKey();
            SimDataLink data = e.getValue();
            XYSeriesCollection A = new XYSeriesCollection();
            A.addSeries(data.get_speed(null).get_XYSeries(""));
            TestPlot.plot(A,
                    String.format("Link %d",linkid),
                    "speed [mph]",
                    String.format("temp/link%d_spd.png",linkid));
        }
    }

    @Test
    public void link1_vehicles(){

        // get cars on various lane group types
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_veh(LaneGroupType.gp,car).get_XYSeries("cars in gp lanes"));
        A.addSeries(simdatalink.get_veh(LaneGroupType.mng,car).get_XYSeries("cars in mng lanes"));
        A.addSeries(simdatalink.get_veh(LaneGroupType.aux,car).get_XYSeries("cars in aux lanes"));
        TestPlot.plot(A,
                "link 1 cars vehicles",
                "veh",
                "temp/link1_veh_car.png");

        // get VEHICLES on given lane group type and all commodities
        A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_veh(LaneGroupType.gp,null).get_XYSeries("gp lanes"));
        A.addSeries(simdatalink.get_veh(LaneGroupType.mng,null).get_XYSeries("mng lanes"));
        A.addSeries(simdatalink.get_veh(LaneGroupType.aux,null).get_XYSeries("aux lanes"));
        TestPlot.plot(A,
                "link 1 all vehicles",
                "veh",
                "temp/link1_veh_agg1.png");

        // get VEHICLES on all lane groups and commodities
        A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_veh(null,null).get_XYSeries("all lanes"));
        TestPlot.plot(A,
                "link 1 all vehicles",
                "veh",
                "temp/link1_veh_agg2.png");

    }

    @Test
    public void link1_flows(){

        // get FLOWS on given lane group type and commodity
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_flw_exiting(LaneGroupType.gp,car).get_XYSeries("cars in gp lanes"));
        A.addSeries(simdatalink.get_flw_exiting(LaneGroupType.mng,car).get_XYSeries("cars in mng lanes"));
        A.addSeries(simdatalink.get_flw_exiting(LaneGroupType.aux,car).get_XYSeries("cars in aux lanes"));
        TestPlot.plot(A,
                "link 1 car flows",
                "flw [vph]",
                "temp/link1_flw_car.png");

        // get FLOWS on given lane group type and all commodities
        A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_flw_exiting(LaneGroupType.gp,null).get_XYSeries("gp lanes"));
        A.addSeries(simdatalink.get_flw_exiting(LaneGroupType.mng,null).get_XYSeries("mng lanes"));
        A.addSeries(simdatalink.get_flw_exiting(LaneGroupType.aux,null).get_XYSeries("aux lanes"));
        TestPlot.plot(A,
                "link 1 all flows",
                "flw [vph]",
                "temp/link1_flw_agg1.png");

        // get FLOWS on all lane groups and commodities
        A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_flw_exiting(null,null).get_XYSeries("all lanes"));
        TestPlot.plot(A,
                "link 1 all flows",
                "flw [vph]",
                "temp/link1_flw_agg2.png");
    }

    @Test
    public void link1_speeds(){

        // get SPEEDS on given lane group type
        XYSeriesCollection A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_speed(LaneGroupType.gp).get_XYSeries("gp lanes"));
        A.addSeries(simdatalink.get_speed(LaneGroupType.mng).get_XYSeries("mng lanes"));
        A.addSeries(simdatalink.get_speed(LaneGroupType.aux).get_XYSeries("aux lanes"));
        TestPlot.plot(A,
                "link 1 speed",
                "speed [mph]",
                "temp/link1_speed.png");

        // get SPEEDS on all lane group types
        A = new XYSeriesCollection();
        A.addSeries(simdatalink.get_speed(null).get_XYSeries("all lanes"));
        TestPlot.plot(A,
                "link 1 speed",
                "speed [mph]",
                "temp/link1_speed_agg.png");
    }

//    @Test
//    public void test_parallel(){
//        Thread th = new Thread(load_task());
//        th.setDaemon(true);
//        th.start();
//    }

}
