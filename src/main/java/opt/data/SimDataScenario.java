package opt.data;

import api.OTMdev;
import models.fluid.FluidLaneGroup;
import output.*;
import profiles.Profile1D;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class SimDataScenario {

    public FreewayScenario fwyscenario;
    public float outdt;
    public List<Float> time;
    public Map<Long,SimDataLink> linkdata;

    public SimDataScenario(FreewayScenario fwyscenario, OTMdev otmdev,float outdt){
        this.fwyscenario = fwyscenario;
        this.outdt = outdt;

        // initialize linkdata
        Set<Long> commids = otmdev.scenario.commodities.keySet();
        this.linkdata = new HashMap<>();
        for(AbstractLink optlink : fwyscenario.get_links()) {
            if(optlink.get_type()== AbstractLink.Type.ghost)
                continue;
            linkdata.put(optlink.id, new SimDataLink(this, optlink, otmdev.scenario.network.links.get(optlink.id), commids));
        }

        float start_time = fwyscenario.get_start_time();
        float sim_dt = fwyscenario.get_sim_dt_sec();
        float duration = fwyscenario.get_sim_duration();

        this.time= new ArrayList();
        List<Integer> time_index = new ArrayList<>();
        float currtime = start_time;
        int currindex = 0;
        int step = (int) (outdt/sim_dt);

        float end_time = start_time + duration;
        while(currtime<=end_time) {
            time.add(currtime);
            time_index.add(currindex);
            currtime += outdt;
            currindex += step;
        }

        Set<OutputCellFlow> flws = otmdev.otm.output.get_data().stream()
                .filter(s->s.type==AbstractOutput.Type.cell_flw)
                .map(s->(OutputCellFlow)s)
                .collect(toSet());

        Set<OutputCellVehicles> vehs = otmdev.otm.output.get_data().stream()
                .filter(s->s.type==AbstractOutput.Type.cell_veh)
                .map(s->(OutputCellVehicles)s)
                .collect(toSet());

        for(Long commid : commids){
            OutputCellFlow flw = flws.stream().filter(s->s.get_commodity_id()==commid).findFirst().get();
            OutputCellVehicles veh = vehs.stream().filter(s->s.get_commodity_id()==commid).findFirst().get();

            ArrayList<FluidLaneGroup> lgs = flw.ordered_lgs;

            for(FluidLaneGroup flg : lgs) {

                if(!linkdata.containsKey(flg.link.getId()))
                    continue;

                SimDataLanegroup lgdata = linkdata.get(flg.link.getId()).lgData.get(flg.id);
                List<AbstractOutputTimedCell.CellProfile> flw_cellprofs = flw.lgprofiles.get(flg.id);
                List<AbstractOutputTimedCell.CellProfile> veh_cellprofs = veh.lgprofiles.get(flg.id);
                for(int i=0;i<flw_cellprofs.size();i++)
                    lgdata.celldata.get(i).set(commid,time_index,flw_cellprofs.get(i).profile.values,veh_cellprofs.get(i).profile.values,sim_dt);
            }

        }


    }

    protected double [] nan(){
        double [] X = new double[numtime()];
        for(int k=0;k<X.length;k++)
            X[k] = Double.NaN;
        return X;
    }

    protected double[] get_vehs_for_network_array(Long commid){
        double[] vehs = new double[numtime()];
        for(SimDataLink lkdata : linkdata.values())
            for (SimDataLanegroup lgdata : lkdata.lgData.values())
                for (SimCellData celldata : lgdata.celldata) {
                    if(commid==null){
                        for(List<Double> list : celldata.vehs.values()){
                            for (int k = 0; k < list.size(); k++)
                                vehs[k] += list.get(k);
                        }
                    } else {
                        if (celldata.vehs.containsKey(commid)) {
                            List<Double> list = celldata.vehs.get(commid);
                            for (int k = 0; k < list.size(); k++)
                                vehs[k] += list.get(k);
                        }
                    }
                }
        return vehs;
    }

    protected double[] get_vehs_for_route_array(List<AbstractLink> links,LaneGroupType globallgtype,Long commid){
        double [] route_vehs = new double[numtime()];
        for(AbstractLink link : links){
            SimDataLink lkdata = linkdata.get(link.id);
            LaneGroupType lgtype = lkdata.lgtype2id.containsKey(globallgtype) ? globallgtype : LaneGroupType.gp;
            double [] link_veh = lkdata.get_veh_array(lgtype,commid);
            for(int k=0;k<numtime();k++)
                route_vehs[k] += link_veh[k];
        }
        return route_vehs;
    }

    protected double[] get_speed_for_network_array(){

        double[] vehs = new double[numtime()];
        double[] flw_length = new double[numtime()];

        for(SimDataLink lkdata : linkdata.values()) {
            if(lkdata.is_source)
                continue;
            double cell_length_miles = lkdata.cell_length();
            for (SimDataLanegroup lgdata : lkdata.lgData.values()) {
                for (SimCellData celldata : lgdata.celldata) {
                    for (List<Double> v : celldata.vehs.values())
                        for (int k = 0; k < numtime() ; k++)
                            vehs[k] += v.get(k);

                    for (List<Double> f : celldata.flws.values())
                        for (int k = 0; k < numtime() ; k++)
                            flw_length[k] += f.get(k) * cell_length_miles;
                }
            }
        }

        double[] speed = new double[numtime()];
        for (int k = 0; k < numtime(); k++)
            speed[k] = vehs[k]<1 || flw_length[k]<1 ? Double.NaN : flw_length[k]/vehs[k];

        return speed;
    }

    protected double[] get_speed_for_route_array(List<AbstractLink> links,LaneGroupType globallgtype){

        double[] vehs = new double[numtime()];
        double[] flw_length = new double[numtime()];

        for(AbstractLink link : links) {
            if(link.is_source())
                continue;
            SimDataLink lkdata = linkdata.get(link.id);

            double cell_length_miles = lkdata.cell_length();

            if(globallgtype==null){
                for (SimDataLanegroup lgdata : lkdata.lgData.values()) {
                    for (SimCellData celldata : lgdata.celldata) {
                        for (List<Double> v : celldata.vehs.values())
                            for (int k = 0; k < numtime() ; k++)
                                vehs[k] += v.get(k);

                        for (List<Double> f : celldata.flws.values())
                            for (int k = 0; k < numtime() ; k++)
                                flw_length[k] += f.get(k) * cell_length_miles;
                    }
                }
            } else {
                LaneGroupType lgtype = lkdata.lgtype2id.containsKey(globallgtype) ? globallgtype : LaneGroupType.gp;
                for (SimCellData celldata : lkdata.lgData.get(lkdata.lgtype2id.get(lgtype)).celldata) {
                    for (List<Double> v : celldata.vehs.values())
                        for (int k = 0; k < numtime() ; k++)
                            vehs[k] += v.get(k);

                    for (List<Double> f : celldata.flws.values())
                        for (int k = 0; k < numtime() ; k++)
                            flw_length[k] += f.get(k) * cell_length_miles;
                }
            }
        }

        double[] speed = new double[numtime()];
        for (int k = 0; k < numtime(); k++)
            speed[k] = vehs[k]<1 || flw_length[k]<1 ? Double.NaN : flw_length[k]/vehs[k];

        return speed;
    }

    /////////////////////////////////////////////////
    // API
    /////////////////////////////////////////////////

    public int numtime(){
        return time.size();
    }

    public double get_dt_sec(){
        return time.size()<2 ? Double.NaN : time.get(1)-time.get(0);
    }

    public TimeSeries get_vht_for_network(Long commid){
        TimeSeries X = get_vehs_for_network(commid);
        X.mult(X.get_dt());
        return X;
    }

    public TimeSeries get_vmt_for_network(Long commid){
        TimeSeries vmt = new TimeSeries(time);
        try {
            for(SimDataLink lkdata : linkdata.values())
                vmt.add(lkdata.get_vmt(null,commid));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vmt;
    }

    public TimeSeries get_delay_for_network(float speed_threshold_mph){
        TimeSeries delay = new TimeSeries(time);
        try {
            for(SimDataLink lkdata : linkdata.values())
                delay.add(lkdata.get_delay(null,speed_threshold_mph));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delay;
    }

    public TimeSeries get_vehs_for_network(Long commid){
        return new TimeSeries(time,get_vehs_for_network_array(commid));
    }

    /** To get VHT just apply X.mult(X.get_dt()) to this **/
    public TimeSeries get_vehs_for_route(long routeid,LaneGroupType lgtype,Long commid){
        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        return new TimeSeries(time,get_vehs_for_route_array(routelinks,lgtype,commid));
    }

    /** ....... **/
    public TimeSeries get_speed_for_network(){
        return new TimeSeries(time,get_speed_for_network_array());
    }

    /** ....... **/
    public TimeSeries get_speed_for_route(long routeid,LaneGroupType lgtype){
        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        return new TimeSeries(time,get_speed_for_route_array(routelinks,lgtype));
    }

    public TimeSeriesList get_speed_contour_for_route(long routeid,LaneGroupType globallgtype) {
        assert(globallgtype!=null);

        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        TimeSeriesList X = new TimeSeriesList(time);

        for(AbstractLink link : routelinks) {

            if(link.is_source()){
                X.add_entry(link,LaneGroupType.gp,0,nan());
                continue;
            }

            SimDataLink lkdata = linkdata.get(link.id);

            LaneGroupType lgtype = lkdata.lgtype2id.containsKey(globallgtype) ? globallgtype : LaneGroupType.gp;
            SimDataLanegroup lg = lkdata.lgData.get(lkdata.lgtype2id.get(lgtype));
            double cell_length_miles = lkdata.link_length_miles/lg.celldata.size();

            List<double []> cell_speeds =  lg.get_cell_speeds(lkdata.ffspeed_mph,cell_length_miles);

            for(int i=0;i<cell_speeds.size();i++)
                X.add_entry(link,lgtype,i,cell_speeds.get(i));
        }

        return X;
    }

    public TimeSeries get_delay_for_route(long routeid,LaneGroupType lgtype,float speed_threshold_kph){
        TimeSeries delay = new TimeSeries(time);
        try {
            for(AbstractLink link : fwyscenario.routes.get(routeid).get_link_sequence())
                delay.add(linkdata.get(link.id).get_delay(lgtype,speed_threshold_kph));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delay;
    }

}

