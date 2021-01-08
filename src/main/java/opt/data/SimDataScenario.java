package opt.data;

import core.OTM;
import core.AbstractLaneGroup;
import models.fluid.FluidLaneGroup;
import opt.utils.Misc;
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

    public SimDataScenario(FreewayScenario fwyscenario, OTM otm, float outdt, boolean storecelldata, boolean storelgdata){
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
        Set<Long> commids = otm.scenario.commodities.keySet();
        this.linkdata = new HashMap<>();
        for(AbstractLink optlink : fwyscenario.get_links())
            linkdata.put(optlink.id, new SimDataLink(this,optlink, otm.scenario.network.links.get(optlink.id), commids,storecelldata,storelgdata,numtime));

        if(hascelldata)
            read_cell_data(otm,commids,sim_dt);

        if(haslgdata)
            read_lg_data(otm,commids,sim_dt);

        // link ghost sinks and sources
        for(LinkGhost glink : fwyscenario.ghost_pieces.links){
            assert(glink.up_link!=null ^ glink.dn_link!=null);
            if(glink.up_link!=null)
                linkdata.get(glink.up_link.id).my_ghost_sink = linkdata.get(glink.id);
            if(glink.dn_link!=null)
                linkdata.get(glink.dn_link.id).my_ghost_source = linkdata.get(glink.id);
            linkdata.remove(glink.id);
        }

    }

    private void read_lg_data(OTM otmdev,Set<Long> commids,float sim_dt){

        Set<OutputLaneGroupFlow> flws = otmdev.output.get_data().stream()
                .filter(s->s.type==AbstractOutput.Type.lanegroup_flw)
                .map(s->(OutputLaneGroupFlow)s)
                .collect(toSet());

        Set<OutputLaneGroupSumVehicles> vehs = otmdev.output.get_data().stream()
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

                if(!linkdata.containsKey(flg.get_link().getId()))
                    continue;

                SimDataLink lkdata = linkdata.get(flg.get_link().getId());
                LaneGroupType lgtype = lkdata.lgid2type.get(flg.getId());
                SimDataLanegroup lgdata = lkdata.lgData.get(lgtype);
                lgdata.set_lg_data(commid,
                        flw.lgprofiles.get(alg.getId()).profile.values,
                        veh.lgprofiles.get(alg.getId()).profile.values,
                        sim_dt,outdt);

            }
        }

    }

    private void read_cell_data(OTM otmdev,Set<Long> commids,float sim_dt){

        Set<OutputCellFlow> flws = otmdev.output.get_data().stream()
                .filter(s->s.type==AbstractOutput.Type.cell_flw)
                .map(s->(OutputCellFlow)s)
                .collect(toSet());

        Set<OutputCellSumVehicles> vehs = otmdev.output.get_data().stream()
                .filter(s->s.type==AbstractOutput.Type.cell_sumveh)
                .map(s->(OutputCellSumVehicles)s)
                .collect(toSet());

        Set<OutputCellSumVehiclesDwn> vehsdwn = otmdev.output.get_data().stream()
                .filter(s->s.type==AbstractOutput.Type.cell_sumvehdwn)
                .map(s->(OutputCellSumVehiclesDwn)s)
                .collect(toSet());

        for(Long commid : commids){
            Optional<OutputCellFlow> oflw = flws.stream().filter(s->s.get_commodity_id()==commid).findFirst();
            Optional<OutputCellSumVehicles> oveh = vehs.stream().filter(s->s.get_commodity_id()==commid).findFirst();
            Optional<OutputCellSumVehiclesDwn> ovehdwn = vehsdwn.stream().filter(s->s.get_commodity_id()==commid).findFirst();

            if(!oflw.isPresent() || !oveh.isPresent() || !ovehdwn.isPresent())
                continue;

            OutputCellFlow flw = oflw.get();
            OutputCellSumVehicles veh = oveh.get();
            OutputCellSumVehiclesDwn vehdwn = ovehdwn.get();

            for(FluidLaneGroup flg : flw.ordered_lgs) {

                if(!linkdata.containsKey(flg.get_link().getId()))
                    continue;

                SimDataLink lkdata = linkdata.get(flg.get_link().getId());
                LaneGroupType lgtype = lkdata.lgid2type.get(flg.getId());
                SimDataLanegroup lgdata = lkdata.lgData.get(lgtype);
                List<AbstractOutputTimedCell.CellProfile> flw_cellprofs = flw.lgprofiles.get(flg.getId());
                List<AbstractOutputTimedCell.CellProfile> veh_cellprofs = veh.lgprofiles.get(flg.getId());
                List<AbstractOutputTimedCell.CellProfile> vehdwn_cellprofs = vehdwn.lgprofiles.get(flg.getId());

                for(int i=0;i<flw_cellprofs.size();i++)
                    lgdata.celldata.get(i).set(commid,
                            flw_cellprofs.get(i).profile.values,
                            veh_cellprofs.get(i).profile.values,
                            vehdwn_cellprofs.get(i).profile.values,
                            sim_dt,outdt);
            }

        }
    }

    public Set<Long> all_comm_ids(){
        return fwyscenario.get_commodities().keySet();
    }

    protected double [] nan(){
        double [] X = new double[numtime()];
        for(int k=0;k<X.length;k++)
            X[k] = Double.NaN;
        return X;
    }

    // includes source and sink ghost link vehicles
    protected double[] get_vehs_for_network_array(Set<Long> commids){
        double[] X = new double[numtime()];
        for(SimDataLink lkdata : linkdata.values()) {
            for (SimDataLanegroup lgdata : lkdata.lgData.values())
                Misc.add_in_place(X, lgdata.get_sum_veh(commids, haslgdata));
            if(lkdata.my_ghost_source!=null)
                for (SimDataLanegroup lgdata : lkdata.my_ghost_source.lgData.values())
                    Misc.add_in_place(X, lgdata.get_sum_veh(commids, haslgdata));
            if(lkdata.my_ghost_sink!=null)
                for (SimDataLanegroup lgdata : lkdata.my_ghost_sink.lgData.values())
                    Misc.add_in_place(X, lgdata.get_sum_veh(commids, haslgdata));
        }
        return X;
    }

    // includes source and sink ghost link vehicles
    protected double[] get_vehs_for_route_array(List<AbstractLink> links,Set<LaneGroupType> lgtypes,Set<Long> commids, boolean include_ghost_source, boolean include_ghost_sink) {
        double[] X = new double[numtime()];
        for (AbstractLink link : links) {
            SimDataLink lkdata = linkdata.get(link.id);
            for (SimDataLanegroup lgdata : lkdata.get_lgdatas(lgtypes))
                Misc.add_in_place(X, lgdata.get_sum_veh(commids, haslgdata));

            if(include_ghost_source && lkdata.my_ghost_source!=null)
                for (SimDataLanegroup lgdata : lkdata.my_ghost_source.get_lgdatas(lgtypes))
                    Misc.add_in_place(X, lgdata.get_sum_veh(commids, haslgdata));

            if(include_ghost_sink && lkdata.my_ghost_sink!=null)
                for (SimDataLanegroup lgdata : lkdata.my_ghost_sink.get_lgdatas(lgtypes))
                    Misc.add_in_place(X, lgdata.get_sum_veh(commids, haslgdata));
        }
        return X;
    }

    // excludes source and sink ghost links
    protected double[] get_speed_for_network_array(){
        double[] dty = new double[numtime()];
        double[] flw = new double[numtime()];
        Set<Long> commids = fwyscenario.get_commodities().keySet();
        for(SimDataLink lkdata : linkdata.values()) {
            for (SimDataLanegroup lgdata : lkdata.lgData.values()) {
                Misc.add_in_place(dty,lgdata.get_sum_veh(commids,haslgdata));
                Misc.add_in_place(flw,lgdata.get_sum_flw(commids,haslgdata));
            }
            double length_miles = haslgdata ? lkdata.link_length_miles : lkdata.cell_length();
            Misc.mult_in_place(dty,1/length_miles);
        }
        double[] speed = new double[numtime()];
        for (int k = 0; k < numtime() ; k++)
            speed[k] = dty[k]<.1 ? Double.NaN : flw[k]/dty[k];
        return speed;
    }

    // excludes source and sink ghost links
    protected double[] get_speed_for_route_array(List<AbstractLink> links,Set<LaneGroupType> lgtypes){

        double[] vehs = new double[numtime()];
        double[] flw_length = new double[numtime()];
        Set<Long> commids = this.fwyscenario.get_commodities().keySet();

        for(AbstractLink link : links) {
            if (link.is_source())
                continue;
            SimDataLink lkdata = linkdata.get(link.id);
            for(SimDataLanegroup lgdata : lkdata.get_lgdatas(lgtypes)){
                Misc.add_in_place(vehs,lgdata.get_sum_veh(commids,haslgdata));
                Misc.add_in_place(flw_length,lgdata.get_sum_flw(commids,haslgdata));
            }
            double length_miles = haslgdata ? lkdata.link_length_miles : lkdata.cell_length();
            Misc.mult_in_place(flw_length,length_miles);
        }

        double[] speed = new double[numtime()];
        for (int k = 0; k < numtime(); k++)
            speed[k] = vehs[k]<1 || flw_length[k]<1 ? Double.NaN : flw_length[k]/vehs[k];

        return speed;
    }

    /////////////////////////////////////////////////
    // API
    /////////////////////////////////////////////////

    // time ................................................

    public int numtime(){
        return time.length;
    }

    public double get_dt_sec(){
        return time.length<2 ? Double.NaN : time[1]-time[0];
    }

    // VHT ................................................

    // DONE
    public TimeSeries get_vht_for_network(Set<Long> commids){
        TimeSeries X = get_vehs_for_network(commids==null?all_comm_ids():commids);
        X.mult(X.get_dt()/3600.0f);
        return X;
    }
    
    public TimeSeries get_vht_for_network_sources(Set<Long> commids){
        TimeSeries vht = new TimeSeries(time);
        try {
            for(SimDataLink lkdata : linkdata.values())
                if(lkdata.my_ghost_source!=null)
                    vht.add(lkdata.get_vht(null, commids,true,true));   //public
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vht;
    }

    public TimeSeries get_vht_for_network_nonsources(Set<Long> commids){
        TimeSeries vht = new TimeSeries(time);
        try {
            for(SimDataLink lkdata : linkdata.values())
                if(lkdata.my_ghost_source==null)
                    vht.add(lkdata.get_vht(null, commids, false, true));  // public
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vht;
    }

    // VMT ................................................

    // TODO TEMPORARY
    public TimeSeries get_vmt_for_network(Set<Long> commids){
        return  get_vmt_for_network(commids, true,true);
    }

    /** get network vmt summed over commids
     * Pass commid=null to get sum over all commidities **/
    public TimeSeries get_vmt_for_network(Set<Long> commids, boolean include_ghost_source, boolean include_ghost_sink){
        commids = commids==null?all_comm_ids():commids;
        TimeSeries vmt = new TimeSeries(time);
        try {
            for(SimDataLink lkdata : linkdata.values())
                vmt.add(lkdata.get_vmt(null,commids,include_ghost_source,include_ghost_sink));  // public
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vmt;
    }

    // Delay ................................................

    public TimeSeries get_delay_for_network(Set<Long> commids,float speed_threshold_mph){
        TimeSeries delay = new TimeSeries(time);
        try {
            for(SimDataLink lkdata : linkdata.values())
                delay.add(lkdata.get_delay(null,commids,speed_threshold_mph,true,true));   // public
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delay;
    }

    public TimeSeries get_delay_for_network_sources(Set<Long> commids,float speed_threshold_mph){
        TimeSeries delay = new TimeSeries(time);
        try {
            for(SimDataLink lkdata : linkdata.values())
                if(lkdata.my_ghost_source!=null)
                    delay.add(lkdata.get_delay(null, commids,speed_threshold_mph,true,true));   //public
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delay;
    }

    public TimeSeries get_delay_for_network_nonsources(Set<Long> commids,float speed_threshold_mph){
        TimeSeries delay = new TimeSeries(time);
        try {
            for(SimDataLink lkdata : linkdata.values())
                if(lkdata.my_ghost_source==null)
                    delay.add(lkdata.get_delay(null, commids,speed_threshold_mph,false,true));  // public
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delay;
    }

    // Vehicles in network ................................................

    public TimeSeries get_vehs_for_network(Set<Long> commids){
        return new TimeSeries(time,get_vehs_for_network_array(commids==null?all_comm_ids():commids));
    }

    // Vehicles on route ................................................

    // TODO TEMPORARY
    public TimeSeries get_vehs_for_route(long routeid,Set<LaneGroupType> lgtypes,Set<Long> commids ){
        return get_vehs_for_route( routeid, lgtypes, commids,true,true);
    }

    public TimeSeries get_vehs_for_route(long routeid,Set<LaneGroupType> lgtypes,Set<Long> commids,boolean include_ghost_source,boolean include_ghost_sink){
        if(lgtypes==null)
            lgtypes = new HashSet<>(Arrays.asList(LaneGroupType.values()));
        if(commids==null)
            commids = fwyscenario.get_commodities().keySet();

        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        return new TimeSeries(time,get_vehs_for_route_array(routelinks,lgtypes,commids,include_ghost_source,include_ghost_sink));
    }

    // Density ................................................

    // TODO TEMPORARY
    public TimeMatrix get_density_contour_for_route(long routeid, Set<LaneGroupType> lgtypes,Set<Long> commids){
        return get_density_contour_for_route( routeid, lgtypes,commids, true, true);
    }

    public TimeMatrix get_density_contour_for_route(long routeid, Set<LaneGroupType> lgtypes,Set<Long> commids,boolean include_ghost_source,boolean include_ghost_sink) {

        if(lgtypes==null)
            lgtypes = new HashSet<>(Arrays.asList(LaneGroupType.values()));
        if(commids==null)
            commids = fwyscenario.get_commodities().keySet();
        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        TimeMatrix X = new TimeMatrix(time,routelinks,this,haslgdata,lgtypes);

        int i=0;
        while(i<X.space.size()){
            TimeMatrix.LinkLaneGroupCell e = X.space.get(i);
            SimDataLink lkdata = linkdata.get(e.linkid);

            // special case I have no data for these lgtypes in this link
            if(lgtypes.stream().noneMatch(lgtype-> lkdata.lgData.containsKey(lgtype))){
                X.add_timeseries(i++,null);
                continue;
            }

            if(haslgdata)
                X.add_timeseries(i++,lkdata.get_dty_array(lgtypes,commids,include_ghost_source,include_ghost_sink));
            else {
                double cell_length_miles = lkdata.cell_length();
                for(LaneGroupType lgtype : lgtypes){
                    int c = i;
                    SimDataLanegroup lgdata = lkdata.lgData.get(lgtype);
                    if(lgdata==null)
                        continue;
                    for (SimCellData cd : lgdata.celldata)
                        X.add_timeseries(c++, cd.get_cell_dty(commids, cell_length_miles));
                }
                i += lkdata.numcells();
            }

        }

        return X;
    }

    // Flow [vph] ................................................

    public TimeMatrix get_flow_contour_for_route(long routeid, Set<LaneGroupType> lgtypes,Set<Long> commids) {
        if(lgtypes==null)
            lgtypes = new HashSet<>(Arrays.asList(LaneGroupType.values()));
        if(commids==null)
            commids = fwyscenario.get_commodities().keySet();
        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        TimeMatrix X = new TimeMatrix(time,routelinks,this,haslgdata,lgtypes);

        int i=0;
        while(i<X.space.size()){
            TimeMatrix.LinkLaneGroupCell e = X.space.get(i);
            SimDataLink lkdata = linkdata.get(e.linkid);

            // special case I have no data for these lgtypes in this link
            if(lgtypes.stream().noneMatch(lgtype-> lkdata.lgData.containsKey(lgtype))){
                X.add_timeseries(i++,null);
                continue;
            }

            if(haslgdata)
                X.add_timeseries(i++,lkdata.get_exit_flw_array(lgtypes,commids));
            else {
                for(LaneGroupType lgtype : lgtypes){
                    int c = i;
                    SimDataLanegroup lgdata = lkdata.lgData.get(lgtype);
                    if(lgdata==null)
                        continue;
                    for (SimCellData cd : lgdata.celldata)
                        X.add_timeseries(c++, cd.get_cell_flw(commids));
                }
                i += lkdata.numcells();
            }
        }
        return X;
    }

    // Speed [mph] ................................................

    public TimeSeries get_speed_for_network(){
        return new TimeSeries(time,get_speed_for_network_array());
    }

    public TimeSeries get_speed_for_route(long routeid,Set<LaneGroupType> lgtypes){
        if(lgtypes==null)
            lgtypes = new HashSet<>(Arrays.asList(LaneGroupType.values()));
        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        return new TimeSeries(time,get_speed_for_route_array(routelinks,lgtypes));
    }

    public TimeMatrix get_speed_contour_for_route(long routeid, Set<LaneGroupType> lgtypes) {
        if(lgtypes==null)
            lgtypes = new HashSet<>(Arrays.asList(LaneGroupType.values()));
        List<AbstractLink> routelinks = fwyscenario.routes.get(routeid).get_link_sequence();
        TimeMatrix X = new TimeMatrix(time,routelinks,this,haslgdata,lgtypes);
        int numtime = X.time.length;

        int i=0;
        while(i<X.space.size()){
            TimeMatrix.LinkLaneGroupCell e = X.space.get(i);
            SimDataLink lkdata = linkdata.get(e.linkid);

            // special case I have no data for these lgtypes in this link
            if(lgtypes.stream().noneMatch(lgtype-> lkdata.lgData.containsKey(lgtype))){
                X.add_timeseries(i++,null);
                continue;
            }

            if(haslgdata)
                X.add_timeseries(i++,lkdata.get_spd_array(lgtypes));
            else{
                double cell_length_miles = lkdata.cell_length();

                for(int cellindex=0;cellindex<lkdata.numcells();cellindex++){

                    double [] sumflw = new double[numtime];
                    double [] sumveh = new double[numtime];

                    double count = 0d;
                    double ffspeed_mph = 0d; // just take the average over lanegroups
                    for(LaneGroupType lgtype : lgtypes){

                        SimDataLanegroup lgdata = lkdata.lgData.get(lgtype);
                        if(lgdata==null)
                            continue;
                        ffspeed_mph += lgdata.ffspeed_mph;
                        count +=1d;
                        SimCellData cd = lgdata.celldata.get(cellindex);
                        for (long commid : cd.vehs.keySet()) {
                            Misc.add_in_place(sumflw,cd.flws.get(commid));
                            Misc.add_in_place(sumveh,cd.vehs.get(commid));
                        }
                    }

                    ffspeed_mph /= count;

                    double [] speeds = new double[numtime];
                    for(int k=0;k<numtime;k++) {
                        speeds[k] = sumveh[k]<1 ? ffspeed_mph : cell_length_miles*sumflw[k]/sumveh[k];
                        if(speeds[k]>ffspeed_mph)
                            speeds[k] = ffspeed_mph;
                    }

                    X.add_timeseries(i++,speeds);

                }

            }

        }

        return X;
    }

}

