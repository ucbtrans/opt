package opt.data;

import opt.utils.Misc;

import java.util.*;
import java.util.stream.Collectors;

public class SimDataLink {

    private SimDataScenario scndata;
    public long id;
    public AbstractLink link;

    public Map<Long,LaneGroupType> lgid2type;
    public Map<LaneGroupType, SimDataLanegroup> lgData;

    public SimDataLink my_ghost_source; // link data for an upstream ghost link
    public SimDataLink my_ghost_sink; // link data for an downstream ghost link

    public double link_length_miles;

    public SimDataLink(SimDataScenario scndata, AbstractLink optlink, core.Link otmlink, Set<Long> commids, boolean storecelldata, boolean storelgdata, int numtime){

        this.id = otmlink.getId();
        this.scndata = scndata;
        this.link = optlink;
        this.link_length_miles = otmlink.get_full_length() / 1609.344;

        lgData = new HashMap<>();
        lgid2type = new HashMap<>();

        models.fluid.FluidLaneGroup lg;
        float simdt_hr = scndata.fwyscenario.get_sim_dt_sec() / 3600f;

        if(optlink.has_mng()){
            lg = (models.fluid.FluidLaneGroup) otmlink.get_lanegroup_for_dn_lane(1);
            lgid2type.put(lg.getId(),LaneGroupType.mng);
            lgData.put(LaneGroupType.mng,new SimDataLanegroup(lg,commids,storecelldata,storelgdata,numtime,simdt_hr));
        }

        lg = (models.fluid.FluidLaneGroup) otmlink.get_lanegroup_for_dn_lane(optlink.get_mng_lanes()+1);
        lgid2type.put(lg.getId(),LaneGroupType.gp);
        lgData.put(LaneGroupType.gp,new SimDataLanegroup(lg,commids,storecelldata,storelgdata,numtime,simdt_hr));

        if(optlink.has_aux()){
            lg = (models.fluid.FluidLaneGroup) otmlink.get_lanegroup_for_dn_lane(
                    optlink.get_mng_lanes() + optlink.get_gp_lanes() + 1);
            lgid2type.put(lg.getId(),LaneGroupType.aux);
            lgData.put(LaneGroupType.aux,new SimDataLanegroup(lg,commids,storecelldata,storelgdata,numtime,simdt_hr));
        }

    }

    private int numtime(){
        return scndata.time.length;
    }

    protected double get_length_lg_miles(){
        return link_length_miles*lgData.size();
    }

    protected double cell_length(){
        return link_length_miles/numcells();
    }

    protected int numcells(){
        return lgData.values().iterator().next().celldata.size();
    }

    protected double[] get_veh_array(Set<LaneGroupType> lgtypes,Set<Long> commids, boolean include_ghost_source, boolean include_ghost_sink){
        double [] X = new double[scndata.numtime()];
        for(LaneGroupType lgtype : lgtypes) {
            if(lgData.containsKey(lgtype))
                Misc.add_in_place(X, lgData.get(lgtype).get_sum_veh(commids, scndata.haslgdata));

            if(include_ghost_source && my_ghost_source!=null)
                if(my_ghost_source.lgData.containsKey(lgtype))
                    Misc.add_in_place(X, my_ghost_source.lgData.get(lgtype).get_sum_veh(commids, scndata.haslgdata));

            if(include_ghost_sink && my_ghost_sink!=null)
                if(my_ghost_sink.lgData.containsKey(lgtype))
                    Misc.add_in_place(X, my_ghost_sink.lgData.get(lgtype).get_sum_veh(commids, scndata.haslgdata));

        }

        return X;
    }

    protected double[] get_dty_array(Set<LaneGroupType> lgtypes,Set<Long> commids,boolean include_ghost_source,boolean include_ghost_sink){
        double [] X = get_veh_array(lgtypes,commids,include_ghost_source,include_ghost_sink);

        double linklength = link_length_miles;
        if(include_ghost_source && my_ghost_source!=null)
            linklength += my_ghost_source.link_length_miles;
        if(include_ghost_sink && my_ghost_sink!=null)
            linklength += my_ghost_sink.link_length_miles;

        Misc.mult_in_place(X, 1d/linklength);
        return X;
    }

    protected double [] get_exit_flw_array(Set<LaneGroupType> lgtypes,Set<Long> commids) {
        double [] X = new double[scndata.numtime()];
        for(LaneGroupType lgtype : lgtypes) {
            if(!lgData.containsKey(lgtype))
                continue;
            Misc.add_in_place(X, lgData.get(lgtype).get_flw_exiting_lg(commids, numtime(), scndata.haslgdata));
        }
        return X;
    }

    protected double [] get_avg_flw_array(Set<LaneGroupType> lgtypes,Set<Long> commids) {
        double [] X = new double[scndata.numtime()];
        for(LaneGroupType lgtype : lgtypes) {
            if(!lgData.containsKey(lgtype))
                continue;
            Misc.add_in_place(X, lgData.get(lgtype).get_flw_avg_lg(commids, numtime(), scndata.haslgdata));
        }
        return X;
    }

    protected double [] get_spd_array(Set<LaneGroupType> lgtypes) {

        LaneGroupsAndCommodities X =  extract_lgdatas_and_comms(lgtypes, null);

        double length_miles = scndata.haslgdata ? link_length_miles : cell_length();
        double [] speeds = new double [numtime()];
        double ffspeed_mph = X.lgDatas.iterator().next().ffspeed_mph;
        for(int k=0;k<numtime();k++) {
            double sumflw = 0d;
            double sumveh = 0d;

            for(SimDataLanegroup lg : X.lgDatas) {
                sumflw += lg.get_sum_flw_for_time(X.commids, k, scndata.haslgdata);
                sumveh += lg.get_sum_vehdwn_for_time(X.commids, k, scndata.haslgdata);
            }
            speeds[k] = sumveh<1 || sumflw<1  ? ffspeed_mph : length_miles*sumflw/sumveh;

            if(speeds[k]>ffspeed_mph)
                speeds[k] = ffspeed_mph;
        }

        return speeds;
    }

    /////////////////////////////////////////////////
    // API
    /////////////////////////////////////////////////

    // VHT ................................................

    // TODO TEMPORARY
    public TimeSeries get_vht(Set<LaneGroupType> lgtypes,Set<Long> commids){
        return get_vht(lgtypes,commids,true,true);
    }

    public TimeSeries get_vht(Set<LaneGroupType> lgtypes,Set<Long> commids, boolean include_ghost_source, boolean include_ghost_sink){

        // get lgtypes and comms
        if(commids==null)
            commids = scndata.fwyscenario.get_commodities().keySet();
        lgtypes = extract_lgtypes(lgtypes);

        // degenerate case
        if(lgtypes.isEmpty() || commids.isEmpty())
            return new TimeSeries(scndata.time,scndata.nan());

        double[] X = get_veh_array(lgtypes,commids,include_ghost_source,include_ghost_sink);
        Misc.mult_in_place(X,scndata.get_dt_sec() / 3600d);
        return new TimeSeries(scndata.time,X);
    }

    // VMT .................................................

    // TODO TEMPORARY
    public TimeSeries get_vmt(Set<LaneGroupType> lgtypes,Set<Long> commids){
        return get_vmt(lgtypes,commids,true,true);
    }

    public TimeSeries get_vmt(Set<LaneGroupType> xlgtypes,Set<Long> xcommids, boolean include_ghost_source, boolean include_ghost_sink){

        // get lgdata and comms
        LaneGroupsAndCommodities X = extract_lgdatas_and_comms(xlgtypes,xcommids);

        // degenerate case
        if(X.lgDatas.isEmpty() || X.commids.isEmpty())
            return new TimeSeries(scndata.time,scndata.nan());

        double dt_hr = scndata.get_dt_sec() / 3600d;
        double [] vmt = new double [scndata.numtime()];

        for (SimDataLanegroup lg : X.lgDatas)
            Misc.add_in_place(vmt,lg.get_sum_flw(X.commids, scndata.haslgdata));
        double lgth_times_dt = scndata.haslgdata ? link_length_miles*dt_hr : cell_length()*dt_hr;
        Misc.mult_in_place(vmt,lgth_times_dt);

        if(include_ghost_source && my_ghost_source!=null){
            double [] sourcevmt = new double [scndata.numtime()];
            for (LaneGroupType lgtype :  X.lgtypes) {
                if(my_ghost_source.lgData.containsKey(lgtype))
                    Misc.add_in_place(sourcevmt, my_ghost_source.lgData.get(lgtype).get_sum_flw(X.commids, scndata.haslgdata));
            }
            Misc.mult_in_place(sourcevmt,scndata.haslgdata ? my_ghost_source.link_length_miles*dt_hr : my_ghost_source.cell_length()*dt_hr);
            Misc.add_in_place(vmt,sourcevmt);
        }

        if(include_ghost_sink && my_ghost_sink!=null){
            double [] sinkvmt = new double [scndata.numtime()];
            for (LaneGroupType lgtype :  X.lgtypes) {
                if(my_ghost_sink.lgData.containsKey(lgtype))
                    Misc.add_in_place(sinkvmt, my_ghost_sink.lgData.get(lgtype).get_sum_flw(X.commids, scndata.haslgdata));
            }
            Misc.mult_in_place(sinkvmt,scndata.haslgdata ? my_ghost_sink.link_length_miles*dt_hr : my_ghost_sink.cell_length()*dt_hr);
            Misc.add_in_place(vmt,sinkvmt);
        }

        return new TimeSeries(scndata.time,vmt);
    }

    // Delay ..............................................

    // TODO TEMPORARY

    public TimeSeries get_delay(Set<LaneGroupType> lgtypes, Set<Long> commids, float threshold_mph){
        return get_delay(lgtypes,commids,threshold_mph,true,true);
    }

    public TimeSeries get_delay(Set<LaneGroupType> lgtypes, Set<Long> commids, float threshold_mph, boolean include_ghost_source, boolean include_ghost_sink) {

        // get lgdata and comms
        LaneGroupsAndCommodities X = extract_lgdatas_and_comms(lgtypes,commids);

        // degenerate case
        if(X.lgDatas.isEmpty() || X.commids.isEmpty())
            return new TimeSeries(scndata.time,scndata.nan());

        double dt_hr = scndata.get_dt_sec() / 3600d;
        
        double my_thres = threshold_mph;
        if (my_thres < 0)
            my_thres = X.lgDatas.iterator().next().ffspeed_mph;

        double length_over_threshold = scndata.haslgdata ? link_length_miles/my_thres : cell_length() / my_thres;
        double [] delays = new double [scndata.numtime()];
        double flw,veh;

        for(int k=0;k<scndata.numtime();k++) {

            for(SimDataLanegroup lg : X.lgDatas) {
                veh = lg.get_sum_vehdwn_for_time(X.commids, k, scndata.haslgdata);
                if(veh>0d){
                    flw = lg.get_sum_flw_for_time(X.commids, k, scndata.haslgdata);
                    double val = veh-flw*length_over_threshold;
                    delays[k] += Math.max( 0d, val*dt_hr );
                }
            }

            if(include_ghost_source && my_ghost_source!=null){
                double source_length_over_threshold = scndata.haslgdata ?
                        my_ghost_source.link_length_miles/my_thres :
                        my_ghost_source.cell_length() / my_thres;
                for(LaneGroupType lgtype : X.lgtypes) {
                    if(my_ghost_source.lgData.containsKey(lgtype)){
                        SimDataLanegroup lg = my_ghost_source.lgData.get(lgtype);
                        veh = lg.get_sum_vehdwn_for_time(X.commids, k, scndata.haslgdata);
                        if(veh>0d) {
                            flw = lg.get_sum_flw_for_time(X.commids, k, scndata.haslgdata);
                            delays[k] += veh == 0d ? 0d : Math.max(0d, (veh - flw * source_length_over_threshold) * dt_hr);
                        }
                    }
                }
            }

            if(include_ghost_sink && my_ghost_sink!=null){
                double sink_length_over_threshold = scndata.haslgdata ?
                        my_ghost_sink.link_length_miles/my_thres :
                        my_ghost_sink.cell_length() / my_thres;
                for(LaneGroupType lgtype : X.lgtypes) {
                    if(my_ghost_sink.lgData.containsKey(lgtype)){
                        SimDataLanegroup lg = my_ghost_sink.lgData.get(lgtype);
                        veh = lg.get_sum_veh_for_time(X.commids, k, scndata.haslgdata);
                        if(veh>0d) {
                            flw = lg.get_sum_flw_for_time(X.commids, k, scndata.haslgdata);
                            delays[k] += veh == 0d ? 0d : Math.max(0d, (veh - flw * sink_length_over_threshold) * dt_hr);
                        }
                    }
                }
            }

        }

        return new TimeSeries(scndata.time,delays);
    }

    // # vehicles ...........................................

    // TODO TEMPORARY
    public TimeSeries get_veh(Set<LaneGroupType> lgtypes,Set<Long> commids){
        return get_veh(lgtypes,commids,true,true);
    }

    public TimeSeries get_veh(Set<LaneGroupType> lgtypes,Set<Long> commids, boolean include_ghost_source, boolean include_ghost_sink){

        // get lgtypes and comms
        if(commids==null)
            commids = scndata.fwyscenario.get_commodities().keySet();
        lgtypes = extract_lgtypes(lgtypes);

        // degenerate case
        if(lgtypes.isEmpty() || commids.isEmpty())
            return new TimeSeries(scndata.time,scndata.nan());

        return new TimeSeries(scndata.time,get_veh_array(lgtypes,commids,include_ghost_source,include_ghost_sink));
    }

    // Flow [vph] Speed [mph] ..............................
    public TimeSeries get_flw_exiting(Set<LaneGroupType> lgtypes, Set<Long> commids){

        // get lgtypes and comms
        if(commids==null)
            commids = scndata.fwyscenario.get_commodities().keySet();
        lgtypes = extract_lgtypes(lgtypes);

        // degenerate case
        if(lgtypes.isEmpty() || commids.isEmpty())
            return new TimeSeries(scndata.time,scndata.nan());

        return new TimeSeries(scndata.time,get_exit_flw_array(lgtypes,commids));
    }

    public TimeSeries get_flw_space_average(Set<LaneGroupType> lgtypes, Set<Long> commids){

        // get lgtypes and comms
        if(commids==null)
            commids = scndata.fwyscenario.get_commodities().keySet();
        lgtypes = extract_lgtypes(lgtypes);

        // degenerate case
        if(lgtypes.isEmpty() || commids.isEmpty())
            return new TimeSeries(scndata.time,scndata.nan());

        return new TimeSeries(scndata.time,get_avg_flw_array(lgtypes,commids));
    }

    // Speed [mph] ..........................................
    public TimeSeries get_speed(Set<LaneGroupType> lgtypes){

        lgtypes = extract_lgtypes(lgtypes);

        // degenerate case
        if(lgtypes.isEmpty() )
            return new TimeSeries(scndata.time,scndata.nan());

        return new TimeSeries(scndata.time,get_spd_array(lgtypes));
    }

    protected Set<SimDataLanegroup> get_lgdatas(Set<LaneGroupType> lgtypes){
        return extract_lgtypes(lgtypes).stream().map(x->  lgData.get(x)).collect(Collectors.toSet());
    }


    //////////////////////////
    // PROTECTED/PRIVATE
    //////////////////////////

    protected Set<LaneGroupType> extract_lgtypes(Set<LaneGroupType> lgtypes){
        Set<LaneGroupType> X = new HashSet<>();
        if(lgtypes==null) {
            X.addAll(lgData.keySet());
            return X;
        } else {
            X.addAll(lgtypes);
            X.retainAll(lgData.keySet());
            return X;
        }
    }

    private LaneGroupsAndCommodities extract_lgdatas_and_comms(Set<LaneGroupType> lgtypes,Set<Long> commids){
        lgtypes = extract_lgtypes(lgtypes);
        Set<SimDataLanegroup> lgDatas = lgtypes.stream()
                .map(x->lgData.get(x))
                .collect(Collectors.toSet());
        return new LaneGroupsAndCommodities(lgtypes,lgDatas,commids==null? scndata.fwyscenario.get_commodities().keySet() : commids);
    }

    public class LaneGroupsAndCommodities {
        Set<LaneGroupType> lgtypes;
        Set<SimDataLanegroup> lgDatas;
        Set<Long> commids;
        public LaneGroupsAndCommodities(Set<LaneGroupType> lgtypes,Set<SimDataLanegroup> lgDatas,Set<Long> commids){
            this.lgtypes = lgtypes;
            this.commids = commids;
            this.lgDatas = lgDatas;
        }
    }

}
