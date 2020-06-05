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

    protected List<Double> zeros(){
        List<Double> list = new ArrayList<>();
        time.forEach(t->list.add(0d));
        return list;
    }

    protected List<Double> get_vehs_for_network_list(Long commid){

        List<Double> vehs = new ArrayList<>(time.size()); // list over time
        time.forEach(t->vehs.add(0d));

        for(SimDataLink simlink : linkdata.values())
            for (SimDataLanegroup simlg : simlink.lgData.values())
                for (SimCellData simcell : simlg.celldata) {
                    if(commid==null){
                        for(List<Double> list : simcell.vehs.values()){
                            for (int k = 0; k < list.size(); k++)
                                vehs.set(k, vehs.get(k) + list.get(k));
                        }
                    } else {
                        if (simcell.vehs.containsKey(commid)) {
                            List<Double> list = simcell.vehs.get(commid);
                            for (int k = 0; k < list.size(); k++)
                                vehs.set(k, vehs.get(k) + list.get(k));
                        }
                    }
                }

        return vehs;
    }

    protected List<Double> get_flws_for_network_list(Long commid){

        List<Double> flws = new ArrayList<>(time.size()); // list over time
        time.forEach(t->flws.add(0d));

        for(SimDataLink simlink : linkdata.values())
            for (SimDataLanegroup simlg : simlink.lgData.values())
                for (SimCellData simcell : simlg.celldata) {
                    if(commid==null){
                        for(List<Double> list : simcell.flws.values()){
                            for (int k = 0; k < list.size(); k++)
                                flws.set(k, flws.get(k) + list.get(k));
                        }
                    } else {
                        if (simcell.vehs.containsKey(commid)) {
                            List<Double> list = simcell.flws.get(commid);
                            for (int k = 0; k < list.size(); k++)
                                flws.set(k, flws.get(k) + list.get(k));
                        }
                    }
                }

        return flws;
    }

    protected List<Double> get_vehs_for_route_list(List<AbstractLink> links,LaneGroupType globallgtype,Long commid){
        List<Double> route_vehs = null;
        for(AbstractLink link : links){
            SimDataLink lkdata = linkdata.get(link.id);
            LaneGroupType lgtype = lkdata.lgtype2id.containsKey(globallgtype) ? globallgtype : LaneGroupType.gp;
            List<Double> link_veh = lkdata.get_veh_list(lgtype,commid);
            if(route_vehs==null)
                route_vehs = link_veh;
            else
                for(int i=0;i<route_vehs.size();i++)
                    route_vehs.set(i,route_vehs.get(i)+link_veh.get(i));
        }
        return route_vehs;
    }

    protected List<Double> get_flws_for_route_list(List<AbstractLink> links,LaneGroupType globallgtype,Long commid){
        List<Double> route_flws = null;
        for(AbstractLink link : links){
            SimDataLink lkdata = linkdata.get(link.id);
            LaneGroupType lgtype = lkdata.lgtype2id.containsKey(globallgtype) ? globallgtype : LaneGroupType.gp;
            List<Double> links_flws = linkdata.get(link.id).get_flw_list(lgtype,commid);
            if(route_flws==null)
                route_flws = links_flws;
            else
                for(int i=0;i<route_flws.size();i++)
                    route_flws.set(i,route_flws.get(i)+links_flws.get(i));
        }
        return route_flws;
    }

    /////////////////////////////////////////////////
    // API
    /////////////////////////////////////////////////

    /** To get VHT just apply X.mult(X.get_dt()) to this **/
    public TimeSeries get_vehs_for_network(Long commid){
        return new TimeSeries(time,get_vehs_for_network_list(commid));
    }

    /** NOT TESTED **/
    public TimeSeries get_speed_for_network(){
        List<Double> vehs = get_vehs_for_network_list(null);
        List<Double> flws = get_flws_for_network_list(null);
        List<Double> spds = new ArrayList<>();
        for(int i=0;i<time.size();i++){
            double speed = vehs.get(i) > 0 ? flws.get(i) / vehs.get(i) : 0d;    // TODO CHANGE DEFAULT SPEED
            spds.add(speed);
        }
        return new TimeSeries(time,spds);
    }

    /** NOT TESTED **/
    /** To get VHT just apply X.mult(X.get_dt()) to this **/
    public TimeSeries get_vehs_for_route(long routeid,LaneGroupType lgtype,Long commid){
        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        return new TimeSeries(time,get_vehs_for_route_list(routelinks,lgtype,commid));
    }

    /** NOT TESTED **/
    public TimeSeries get_speed_for_route(long routeid,LaneGroupType globallgtype){
        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        List<Double> vehs = get_vehs_for_route_list(routelinks,globallgtype,null);
        List<Double> flws = get_flws_for_route_list(routelinks,globallgtype,null);
        List<Double> spds = new ArrayList<>();
        for(int i=0;i<time.size();i++){
            double speed = vehs.get(i) > 0 ? flws.get(i) / vehs.get(i) : 0d;    // TODO CHANGE DEFAULT SPEED
            spds.add(speed);
        }
        return new TimeSeries(time,spds);
    }


}

