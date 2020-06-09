package opt.data;

import api.OTMdev;
import models.fluid.FluidLaneGroup;
import output.*;

import java.util.*;

public class SimDataScenario {

    public FreewayScenario fwyscenario;
    public List<Float> time;
    public Map<Long,SimDataLink> linkdata;

    public SimDataScenario(FreewayScenario fwyscenario, OTMdev otmdev){
        this.fwyscenario = fwyscenario;

        // initialize linkdata
        Set<Long> commids = otmdev.scenario.commodities.keySet();
        this.linkdata = new HashMap<>();
        for(AbstractLink optlink : fwyscenario.get_links())
            linkdata.put(optlink.id,new SimDataLink(this,optlink,otmdev.scenario.network.links.get(optlink.id),commids));

        // populate with data
        for(AbstractOutput aoutput :  otmdev.otm.output.get_data()){

            if(time==null)
                time = ((AbstractOutputTimedCell) aoutput).lgprofiles.entrySet().iterator().next()
                        .getValue()
                        .get(0)
                        .profile
                        .get_times();

            if (aoutput instanceof OutputCellFlow){
                OutputCellFlow output = (OutputCellFlow) aoutput;
                float dt_sec = output.get_outdt();
                long commid = output.get_commodity_id();
                for(FluidLaneGroup flg : output.ordered_lgs) {
                    SimDataLanegroup lgdata = linkdata.get(flg.link.getId()).lgData.get(flg.id);
                    List<AbstractOutputTimedCell.CellProfile> cellprofs = output.lgprofiles.get(flg.id);
                    for(int i=0;i<cellprofs.size();i++)
                        lgdata.celldata.get(i).set_flws(commid, cellprofs.get(i).profile.values,dt_sec);
                }
            }

            if (aoutput instanceof OutputCellVehicles) {
                OutputCellVehicles output = (OutputCellVehicles) aoutput;
                long commid = output.get_commodity_id();
                for(FluidLaneGroup flg : output.ordered_lgs) {
                    SimDataLanegroup lgdata = linkdata.get(flg.link.getId()).lgData.get(flg.id);
                    List<AbstractOutputTimedCell.CellProfile> cellprofs = output.lgprofiles.get(flg.id);
                    for(int i=0;i<cellprofs.size();i++)
                        lgdata.celldata.get(i).set_vehs(commid, cellprofs.get(i).profile.values);
                }
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

    /** To get VHT just apply X.mult(X.get_dt()) to this **/
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
        TimeSeriesList tsl = new TimeSeriesList(time);

        for(AbstractLink link : routelinks) {

            if(link.is_source()){
                tsl.add_entry(link,LaneGroupType.gp,0,nan());
                continue;
            }

            SimDataLink lkdata = linkdata.get(link.id);

            LaneGroupType lgtype = lkdata.lgtype2id.containsKey(globallgtype) ? globallgtype : LaneGroupType.gp;
            SimDataLanegroup lg = lkdata.lgData.get(lkdata.lgtype2id.get(lgtype));
            double cell_length_miles = lkdata.link_length_miles/lg.celldata.size();

            List<double []> zzz =  lg.get_cell_speeds(lkdata.ffspeed_mph,cell_length_miles);

            for(int i=0;i<zzz.size();i++)
                tsl.add_entry(link,lgtype,i,zzz.get(i));
        }


        return tsl;
    }

}

