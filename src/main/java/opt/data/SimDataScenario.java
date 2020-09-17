package opt.data;

import api.OTMdev;
import common.AbstractLaneGroup;
import models.fluid.FluidLaneGroup;
import output.*;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class SimDataScenario {

    public FreewayScenario fwyscenario;
    public float outdt;
    public float [] time;
    public Map<Long,SimDataLink> linkdata;
    public boolean hascelldata;
    public boolean haslgdata;

    public SimDataScenario(FreewayScenario fwyscenario, OTMdev otmdev,float outdt,boolean storecelldata,boolean storelgdata){
        this.fwyscenario = fwyscenario;
        this.outdt = outdt;
        this.hascelldata = storecelldata;
        this.haslgdata = storelgdata;

        // time vector
        float start_time = fwyscenario.get_start_time();
        float sim_dt = fwyscenario.get_sim_dt_sec();
        float duration = fwyscenario.get_sim_duration();

        int numtime = ((int)(duration/outdt)) + 1;
        time = new float[numtime];
        for(int i=0;i<numtime;i++){
            time[i] = start_time + i*outdt;
        }

        // initialize linkdata
        Set<Long> commids = otmdev.scenario.commodities.keySet();
        this.linkdata = new HashMap<>();
        for(AbstractLink optlink : fwyscenario.get_links()) {
            if(optlink.get_type()== AbstractLink.Type.ghost)
                continue;
            boolean is_source = fwyscenario.ghost_pieces.links.contains(optlink.get_up_link());
            linkdata.put(optlink.id, new SimDataLink(this, optlink, otmdev.scenario.network.links.get(optlink.id), commids,is_source,storecelldata,storelgdata,numtime));
        }

        if(hascelldata)
            read_cell_data(otmdev,commids,sim_dt);

        if(haslgdata)
            read_lg_data(otmdev,commids,sim_dt);

    }

    private void read_lg_data(OTMdev otmdev,Set<Long> commids,float sim_dt){

        Set<OutputLaneGroupFlow> flws = otmdev.otm.output.get_data().stream()
                .filter(s->s.type==AbstractOutput.Type.lanegroup_flw)
                .map(s->(OutputLaneGroupFlow)s)
                .collect(toSet());

        Set<OutputLaneGroupSumVehicles> vehs = otmdev.otm.output.get_data().stream()
                .filter(s->s.type==AbstractOutput.Type.lanegroup_sumveh)
                .map(s->(OutputLaneGroupSumVehicles)s)
                .collect(toSet());

        for(Long commid : commids){

            Optional<OutputLaneGroupFlow> oflw = flws.stream().filter(s->s.get_commodity_id()==commid).findFirst();
            Optional<OutputLaneGroupSumVehicles> oveh = vehs.stream().filter(s->s.get_commodity_id()==commid).findFirst();

            if(!oflw.isPresent() || !oveh.isPresent())
                continue;

            OutputLaneGroupFlow flw = oflw.get();
            OutputLaneGroupSumVehicles veh = oveh.get();

            for(AbstractLaneGroup alg : veh.ordered_lgs) {

                FluidLaneGroup flg = (FluidLaneGroup) alg;

                if(!linkdata.containsKey(flg.link.getId()))
                    continue;

                SimDataLanegroup lgdata = linkdata.get(flg.link.getId()).lgData.get(flg.id);
                lgdata.set_lg_data(commid,
                        flw.lgprofiles.get(alg.id).profile.values,
                        veh.lgprofiles.get(alg.id).profile.values,
                        sim_dt,outdt);

            }
        }

    }

    private void read_cell_data(OTMdev otmdev,Set<Long> commids,float sim_dt){

        Set<OutputCellFlow> flws = otmdev.otm.output.get_data().stream()
                .filter(s->s.type==AbstractOutput.Type.cell_flw)
                .map(s->(OutputCellFlow)s)
                .collect(toSet());

        Set<OutputCellSumVehicles> vehs = otmdev.otm.output.get_data().stream()
                .filter(s->s.type==AbstractOutput.Type.cell_sumveh)
                .map(s->(OutputCellSumVehicles)s)
                .collect(toSet());

        for(Long commid : commids){
            Optional<OutputCellFlow> oflw = flws.stream().filter(s->s.get_commodity_id()==commid).findFirst();
            Optional<OutputCellSumVehicles> oveh = vehs.stream().filter(s->s.get_commodity_id()==commid).findFirst();

            if(!oflw.isPresent() || !oveh.isPresent())
                continue;

            OutputCellFlow flw = oflw.get();
            OutputCellSumVehicles veh = oveh.get();

            for(FluidLaneGroup flg : flw.ordered_lgs) {

                if(!linkdata.containsKey(flg.link.getId()))
                    continue;

                SimDataLanegroup lgdata = linkdata.get(flg.link.getId()).lgData.get(flg.id);
                List<AbstractOutputTimedCell.CellProfile> flw_cellprofs = flw.lgprofiles.get(flg.id);
                List<AbstractOutputTimedCell.CellProfile> veh_cellprofs = veh.lgprofiles.get(flg.id);
                for(int i=0;i<flw_cellprofs.size();i++)
                    lgdata.celldata.get(i).set(commid,
                            flw_cellprofs.get(i).profile.values,
                            veh_cellprofs.get(i).profile.values,
                            sim_dt,outdt);
            }

        }
    }

    protected double [] nan(){
        double [] X = new double[numtime()];
        for(int k=0;k<X.length;k++)
            X[k] = Double.NaN;
        return X;
    }

    protected double[] get_vehs_for_network_array(Set<Long> c){
        Set<Long> commids = c==null ? fwyscenario.get_commodities().keySet() : c;
        double[] X = new double[numtime()];
        for (int k = 0; k < numtime(); k++)
            for(SimDataLink lkdata : linkdata.values())
                for (SimDataLanegroup lgdata : lkdata.lgData.values())
                    X[k] += lgdata.get_sum_veh(commids,  k, haslgdata);
        return X;
    }

    protected double[] get_vehs_for_route_array(List<AbstractLink> links,LaneGroupType globallgtype,Set<Long> c){
        Set<Long> commids = c==null ? fwyscenario.get_commodities().keySet() : c;
        double [] X = new double[numtime()];
        for(int k=0;k<numtime();k++)
            for(AbstractLink link : links){
                SimDataLink lkdata = linkdata.get(link.id);
                LaneGroupType lgtype = lkdata.lgtype2id.containsKey(globallgtype) ? globallgtype : LaneGroupType.gp;
                SimDataLanegroup lgdata = lkdata.lgData.get(lkdata.lgtype2id.get(lgtype));
                X[k] += lgdata.get_sum_veh(commids,  k, haslgdata);
            }
        return X;
    }

    protected double[] get_speed_for_network_array(){

        double[] speed = new double[numtime()];
        for (int k = 0; k < numtime() ; k++){
            double vehs = 0d;
            double flw_length = 0d;
            for(SimDataLink lkdata : linkdata.values()) {
                double length_miles = haslgdata ? lkdata.link_length_miles : lkdata.cell_length();
                for (SimDataLanegroup lgdata : lkdata.lgData.values()) {
                    vehs += lgdata.get_sum_veh(null,k,haslgdata);
                    flw_length += lgdata.get_sum_flw(null,k,haslgdata) * length_miles;
                }
            }
            speed[k] = vehs<1 || flw_length<1 ? Double.NaN : flw_length/vehs;
        }

        return speed;
    }

    protected double[] get_speed_for_route_array(List<AbstractLink> links,LaneGroupType globallgtype){

        double[] vehs = new double[numtime()];
        double[] flw_length = new double[numtime()];

        for(AbstractLink link : links) {
            if (link.is_source())
                continue;
            SimDataLink lkdata = linkdata.get(link.id);

            double length_miles = haslgdata ? lkdata.link_length_miles : lkdata.cell_length();

            Set<SimDataLanegroup> lgDatas = new HashSet<>();

            if(globallgtype==null)
                lgDatas.addAll(lkdata.lgData.values());
            else {
                LaneGroupType lgtype = lkdata.lgtype2id.containsKey(globallgtype) ? globallgtype : LaneGroupType.gp;
                lgDatas.add(lkdata.lgData.get(lkdata.lgtype2id.get(lgtype)));
            }

            for (int k = 0; k < numtime() ; k++){
                for (SimDataLanegroup lgdata : lkdata.lgData.values()) {
                    vehs[k] += lgdata.get_sum_veh(null,k, haslgdata);
                    flw_length[k] += lgdata.get_sum_flw(null,k,haslgdata) * length_miles;
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
        return time.length;
    }

    public double get_dt_sec(){
        return time.length<2 ? Double.NaN : time[1]-time[0];
    }

    public TimeSeries get_vht_for_network(long commid){
        Set<Long> commids = new HashSet<>();
        commids.add(commid);
        return get_vht_for_network(commids);
    }

    public TimeSeries get_vht_for_network(Set<Long> commids){
        TimeSeries X = get_vehs_for_network(commids);
        X.mult(X.get_dt()/3600.0f);
        return X;
    }

    public TimeSeries get_vmt_for_network(long commid){
        Set<Long> commids = new HashSet<>();
        commids.add(commid);
        return get_vmt_for_network(commids);
    }

    /** get network vmt summed over commids
     * Pass commid=null to get sum over all commidities **/
    public TimeSeries get_vmt_for_network(Set<Long> commids){
        TimeSeries vmt = new TimeSeries(time);
        try {
            for(SimDataLink lkdata : linkdata.values())
                vmt.add(lkdata.get_vmt(null,commids));
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

    public TimeSeries get_delay_for_network_sources(float speed_threshold_mph){
        TimeSeries delay = new TimeSeries(time);
        try {
            for(SimDataLink lkdata : linkdata.values())
                if(lkdata.is_source)
                    delay.add(lkdata.get_delay(null, speed_threshold_mph));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delay;
    }

    public TimeSeries get_delay_for_network_nonsources(float speed_threshold_mph){
        TimeSeries delay = new TimeSeries(time);
        try {
            for(SimDataLink lkdata : linkdata.values())
                if(!lkdata.is_source)
                    delay.add(lkdata.get_delay(null, speed_threshold_mph));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delay;
    }

    public TimeSeries get_vehs_for_network(long commid){
        Set<Long> commids = new HashSet<>();
        commids.add(commid);
        return get_vehs_for_network(commids);
    }

    public TimeSeries get_vehs_for_network(Set<Long> commids){
        return new TimeSeries(time,get_vehs_for_network_array(commids));
    }

    public TimeSeries get_vehs_for_route(long routeid,LaneGroupType lgtype,long commid){
        Set<Long> commids = new HashSet<>();
        commids.add(commid);
        return get_vehs_for_route(routeid,lgtype,commids);
    }

    /** To get VHT just apply X.mult(X.get_dt()) to this **/
    public TimeSeries get_vehs_for_route(long routeid,LaneGroupType lgtype,Set<Long> commids){
        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        return new TimeSeries(time,get_vehs_for_route_array(routelinks,lgtype,commids));
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

    public TimeMatrix get_speed_contour_for_route(long routeid, LaneGroupType globallgtype) {
        assert(globallgtype!=null);

        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        TimeMatrix X = new TimeMatrix(time,routelinks,this,haslgdata,globallgtype);

        int i=0;
        while(i<X.space.size()){
            TimeMatrix.LinkLaneGroupCell e = X.space.get(i);
            SimDataLink lkdata = linkdata.get(e.linkid);
            LaneGroupType lgtype = e.lgtype;
            SimDataLanegroup lgdata = lkdata.lgData.get(lkdata.lgtype2id.get(lgtype));

            if(haslgdata)
                X.values[i++] = lkdata.get_spd_array(lgtype);

            else{
                double cell_length_miles = lkdata.link_length_miles/lgdata.celldata.size();
                for(double[] z : lgdata.get_cell_speeds(lkdata.ffspeed_mph,cell_length_miles))
                    X.values[i++] = z;
            }

        }

        return X;
    }

}

