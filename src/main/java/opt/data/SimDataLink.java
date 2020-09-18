package opt.data;

import opt.utils.Misc;

import java.util.*;
import java.util.stream.Collectors;

public class SimDataLink {

    private SimDataScenario scndata;
    public long id;

    public Map<LaneGroupType,Long> lgtype2id;
    public Map<Long, SimDataLanegroup> lgData;

    public double link_length_miles;
    public boolean is_source;
    protected float ffspeed_mph;

    public SimDataLink(SimDataScenario scndata,AbstractLink optlink, common.Link otmlink, Set<Long> commids, boolean is_source,boolean storecelldata,boolean storelgdata,int numtime){

        this.id = otmlink.getId();
        this.scndata = scndata;
        this.is_source = is_source;
        this.link_length_miles = otmlink.length / 1609.344;

        lgData = new HashMap<>();
        lgtype2id = new HashMap<>();

        models.fluid.FluidLaneGroup lg;

        if(optlink.has_mng()){
            lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(1);
            lgtype2id.put(LaneGroupType.mng,lg.id);
            lgData.put(lg.id,new SimDataLanegroup(lg,commids,storecelldata,storelgdata,numtime));
        }

        lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(optlink.get_mng_lanes()+1);
        int numcells = lg.cells.size();
        lgtype2id.put(LaneGroupType.gp,lg.id);
        lgData.put(lg.id,new SimDataLanegroup(lg,commids,storecelldata,storelgdata,numtime));

        float simdt_hr = scndata.fwyscenario.get_sim_dt_sec() / 3600f;
        ffspeed_mph = (float) (lg.ffspeed_cell_per_dt * link_length_miles/numcells/simdt_hr);

        if(optlink.has_aux()){
            lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(
                    optlink.get_mng_lanes() + optlink.get_gp_lanes() + 1);
            lgtype2id.put(LaneGroupType.aux,lg.id);
            lgData.put(lg.id,new SimDataLanegroup(lg,commids,storecelldata,storelgdata,numtime));
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

    protected double[] get_veh_array(Set<LaneGroupType> lgtypes,Set<Long> commids){
        double [] X = new double[scndata.numtime()];
        for(LaneGroupType lgtype : lgtypes)
            Misc.add_in_place(X,lgData.get(lgtype2id.get(lgtype)).get_sum_veh(commids,scndata.haslgdata));
        return X;
    }

    protected double[] get_dty_array(Set<LaneGroupType> lgtypes,Set<Long> commids){
        double [] X = get_veh_array(lgtypes,commids);
        Misc.mult_in_place(X, 1/link_length_miles);
        return X;
    }

    protected double [] get_exit_flw_array(Set<LaneGroupType> lgtypes,Set<Long> commids) {
        double [] X = new double[scndata.numtime()];
        for(LaneGroupType lgtype : lgtypes)
            Misc.add_in_place(X,lgData.get(lgtype2id.get(lgtype)).get_flw_exiting_lg(commids,numtime(), scndata.haslgdata));
        return X;
    }

    protected double [] get_spd_array(Set<LaneGroupType> lgtypes) {

        LaneGroupsAndCommodities X =  extract_lgdatas_and_comms(lgtypes, null);

        double length_miles = scndata.haslgdata ? link_length_miles : cell_length();
        double [] speeds = new double [numtime()];
        for(int k=0;k<numtime();k++) {
            double sumflw = 0d;
            double sumveh = 0d;
            for(SimDataLanegroup lg : X.lgDatas) {
                sumflw += lg.get_sum_flw_for_time(X.commids, k, scndata.haslgdata);
                sumveh += lg.get_sum_veh_for_time(X.commids, k, scndata.haslgdata);
            }
            speeds[k] = sumveh<1 ? ffspeed_mph : length_miles*sumflw/sumveh;
            if(speeds[k]>ffspeed_mph)
                speeds[k] = ffspeed_mph;
        }

        return speeds;
    }

    /////////////////////////////////////////////////
    // API
    /////////////////////////////////////////////////

    // VHT ................................................
    public TimeSeries get_vht(Set<LaneGroupType> lgtypes,Set<Long> commids){

        // get lgtypes and comms
        if(commids==null)
            commids = scndata.fwyscenario.get_commodities().keySet();
        lgtypes = extract_lgtypes(lgtypes);

        // degenerate case
        if(lgtypes.isEmpty() || commids.isEmpty())
            return new TimeSeries(scndata.time,scndata.nan());

        double[] X = get_veh_array(lgtypes,commids);
        Misc.mult_in_place(X,scndata.get_dt_sec() / 3600d);
        return new TimeSeries(scndata.time,X);
    }

    // VMT .................................................
    public TimeSeries get_vmt(Set<LaneGroupType> lgtypes,Set<Long> commids){

        // get lgdata and comms
        LaneGroupsAndCommodities X = extract_lgdatas_and_comms(lgtypes,commids);

        // degenerate case
        if(X.lgDatas.isEmpty() || X.commids.isEmpty())
            return new TimeSeries(scndata.time,scndata.nan());

        double [] vmt = new double [scndata.numtime()];
        for (SimDataLanegroup lg : X.lgDatas)
            Misc.add_in_place(vmt,lg.get_sum_flw(X.commids, scndata.haslgdata));

        double dt_hr = scndata.get_dt_sec() / 3600d;
        double lgth_times_dt = scndata.haslgdata ? link_length_miles*dt_hr : cell_length()*dt_hr;
        Misc.mult_in_place(vmt,lgth_times_dt);
        return new TimeSeries(scndata.time,vmt);
    }

    // Delay ..............................................
    public TimeSeries get_delay(Set<LaneGroupType> lgtypes, Set<Long> commids, float threshold_mph) {

        // get lgdata and comms
        LaneGroupsAndCommodities X = extract_lgdatas_and_comms(lgtypes,commids);

        // degenerate case
        if(X.lgDatas.isEmpty() || X.commids.isEmpty())
            return new TimeSeries(scndata.time,scndata.nan());

        double dt_hr = scndata.get_dt_sec() / 3600d;
        
        double my_thres = threshold_mph;
        if (my_thres < 0)
            my_thres = ffspeed_mph;

        double length_over_threshold = scndata.haslgdata ? link_length_miles/my_thres : cell_length() / my_thres;
        double [] delays = new double [scndata.numtime()];
        double flw,veh;
        for(int k=0;k<scndata.numtime();k++) {
            for(SimDataLanegroup lg : X.lgDatas) {
                flw = lg.get_sum_flw_for_time(X.commids, k, scndata.haslgdata);
                veh = lg.get_sum_veh_for_time(X.commids, k, scndata.haslgdata);
                delays[k] += veh==0d? 0d : Math.max( 0d, (veh-flw*length_over_threshold)*dt_hr );
            }
        }

        return new TimeSeries(scndata.time,delays);
    }

    // # vehicles ...........................................
    public TimeSeries get_veh(Set<LaneGroupType> lgtypes,Set<Long> commids){

        // get lgtypes and comms
        if(commids==null)
            commids = scndata.fwyscenario.get_commodities().keySet();
        lgtypes = extract_lgtypes(lgtypes);

        // degenerate case
        if(lgtypes.isEmpty() || commids.isEmpty())
            return new TimeSeries(scndata.time,scndata.nan());

        return new TimeSeries(scndata.time,get_veh_array(lgtypes,commids));
    }

    // Flow [vph] Speed [mph] ..............................
    public TimeSeries get_flw(Set<LaneGroupType> lgtypes, Set<Long> commids){

        // get lgtypes and comms
        if(commids==null)
            commids = scndata.fwyscenario.get_commodities().keySet();
        lgtypes = extract_lgtypes(lgtypes);

        // degenerate case
        if(lgtypes.isEmpty() || commids.isEmpty())
            return new TimeSeries(scndata.time,scndata.nan());

        return new TimeSeries(scndata.time,get_exit_flw_array(lgtypes,commids));
    }

    // Speed [mph] ..........................................
    public TimeSeries get_speed(Set<LaneGroupType> lgtypes){

        lgtypes = extract_lgtypes(lgtypes);

        // degenerate case
        if(lgtypes.isEmpty() )
            return new TimeSeries(scndata.time,scndata.nan());

        return new TimeSeries(scndata.time,get_spd_array(lgtypes));
    }









    public Set<SimDataLanegroup> get_lgdatas(Set<LaneGroupType> lgtypes){
        return extract_lgtypes(lgtypes).stream().map(x->  lgData.get(lgtype2id.get(x))).collect(Collectors.toSet());
    }

    public Set<LaneGroupType> extract_lgtypes(Set<LaneGroupType> lgtypes){
        Set<LaneGroupType> X = new HashSet<>();
        if(lgtypes==null) {
            X.addAll(lgtype2id.keySet());
            return X;
        } else {
            X.addAll(lgtypes);
            X.retainAll(lgtype2id.keySet());
            return X;
        }
    }

    private LaneGroupsAndCommodities extract_lgdatas_and_comms(Set<LaneGroupType> lgtypes,Set<Long> commids){
        lgtypes = extract_lgtypes(lgtypes);
        Set<SimDataLanegroup> lgDatas = lgtypes.stream()
                .map(x->lgData.get(lgtype2id.get(x)))
                .collect(Collectors.toSet());
        return new LaneGroupsAndCommodities(lgDatas,commids==null? scndata.fwyscenario.get_commodities().keySet() : commids);
    }

    public class LaneGroupsAndCommodities {
        Set<SimDataLanegroup> lgDatas;
        Set<Long> commids;
        public LaneGroupsAndCommodities(Set<SimDataLanegroup> lgDatas,Set<Long> commids){
            this.commids = commids;
            this.lgDatas = lgDatas;
        }
    }

}
