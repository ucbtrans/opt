package opt.data;

import java.util.*;

import static java.util.stream.Collectors.toSet;

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
        return link_length_miles/lgData.values().iterator().next().celldata.size();
    }

    protected double[] get_veh_array(LaneGroupType lgtype,Long commid){
        assert(lgtype2id.containsKey(lgtype) || lgtype==null);
        LaneGroupsAndCommodities X = choose_lgs_and_comms(lgtype,commid);
        double [] Y = new double[scndata.numtime()];
        for(SimDataLanegroup lgData : X.lgDatas){
            for(int k=0;k<scndata.numtime();k++)
                Y[k] += lgData.get_sum_veh(X.commids,k, scndata.haslgdata);
        }
        return Y;
    }

    protected double [] get_exit_flw_array(LaneGroupType lgtype, Long commid) {
        assert(lgtype2id.containsKey(lgtype) || lgtype==null);
        if(lgtype==null){
            double [] X = new double[scndata.numtime()];
            for(SimDataLanegroup data : lgData.values()){
                double [] XX = data.get_flw_exiting_lg(commid,numtime(), scndata.haslgdata);
                for(int k=0;k<X.length;k++)
                    X[k] += XX[k];
            }
            return X;
        } else {
            return lgData.get(lgtype2id.get(lgtype)).get_flw_exiting_lg(commid,numtime(),scndata.haslgdata);
        }
    }

    protected double [] get_spd_array(LaneGroupType lgtype) {

        assert(lgtype2id.containsKey(lgtype) || lgtype==null);

        LaneGroupsAndCommodities X = choose_lgs_and_comms(lgtype,null);
        double length_miles = scndata.haslgdata ? link_length_miles : cell_length();
        double [] speeds = new double [scndata.numtime()];
        for(int k=0;k<scndata.numtime();k++) {
            double sumflw = 0d;
            double sumveh = 0d;
            for(SimDataLanegroup lg : X.lgDatas) {
                sumflw += lg.get_sum_flw(X.commids, k, scndata.haslgdata);
                sumveh += lg.get_sum_veh(X.commids, k, scndata.haslgdata);
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

    public TimeSeries get_vht(LaneGroupType lgtype,Long commid){
        double[] vehs = get_veh_array(lgtype,commid);
        double dt_hr = scndata.get_dt_sec() / 3600d;
        for(int k=0;k<vehs.length;k++)
            vehs[k] = vehs[k]*dt_hr;
        return new TimeSeries(scndata.time,vehs);
    }

    public TimeSeries get_vmt(LaneGroupType lgtype,Long commid){

        assert(lgtype2id.containsKey(lgtype) || lgtype==null);

        LaneGroupsAndCommodities X = choose_lgs_and_comms(lgtype,commid);

        // collect data
        double dt_hr = scndata.get_dt_sec() / 3600d;
        double lgth_times_dt = scndata.haslgdata ? link_length_miles*dt_hr : cell_length()*dt_hr;
        double [] vmt = new double [scndata.numtime()];
        for(int k=0;k<scndata.numtime();k++) {
            for (SimDataLanegroup lg : X.lgDatas)
                vmt[k] += lg.get_sum_flw(X.commids, k, scndata.haslgdata);
            vmt[k] *= lgth_times_dt;
        }
        return new TimeSeries(scndata.time,vmt);
    }

    public TimeSeries get_delay(LaneGroupType lgtype, float threshold_mph) {

        assert(lgtype2id.containsKey(lgtype) || lgtype==null);

        LaneGroupsAndCommodities X = choose_lgs_and_comms(lgtype,null);

        double dt_hr = scndata.get_dt_sec() / 3600d;
        
        double my_thres = threshold_mph;
        if (my_thres < 0)
            my_thres = ffspeed_mph;

        double length_over_threshold = scndata.haslgdata ? link_length_miles/my_thres : cell_length() / my_thres;
        double [] delays = new double [scndata.numtime()];
        double flw,veh;
        for(int k=0;k<scndata.numtime();k++) {
            for(SimDataLanegroup lg : X.lgDatas) {
                flw = lg.get_sum_flw(X.commids, k, scndata.haslgdata);
                veh = lg.get_sum_veh(X.commids, k, scndata.haslgdata);
                delays[k] += veh==0d? 0d : Math.max( 0d, (veh-flw*length_over_threshold)*dt_hr );
            }
        }

        return new TimeSeries(scndata.time,delays);
    }

    /** returns a TimeSeries of vehicle numbers for the given lane group type and commodity id
     * lgtype==null means all lanes in the link
     * comm==null means all commodities
     * NOTE: if the lane group does not exist, returns a list of ZEROS
     */
    public TimeSeries get_veh(LaneGroupType lgtype,Long commid){
        return new TimeSeries(scndata.time,lgtype2id.containsKey(lgtype)||lgtype==null ?
                get_veh_array(lgtype,commid) :
                new double[scndata.numtime()] );
    }

    /** returns a TimeSeries of vehicle numbers for the given lane group type and commodity id
     * lgtype==null means all lanes in the link
     * comm==null means all commodities
     * NOTE: if the lane group does not exist, returns a list of ZEROS
     */
    public TimeSeries get_flw_exiting(LaneGroupType lgtype, Long commid){
        // use cell data only if there is no lg data
        return new TimeSeries(scndata.time,lgtype2id.containsKey(lgtype)||lgtype==null ?
                get_exit_flw_array(lgtype,commid) :
                new double[scndata.numtime()] );
    }

    /** returns a TimeSeries of vehicle numbers for the given lane group type
     * lgtype==null means all lanes in the link
     * NOTE: if the lane group does not exist, returns NULL
     */
    public TimeSeries get_speed(LaneGroupType lgtype){
        return new TimeSeries(scndata.time,lgtype2id.containsKey(lgtype)||lgtype==null ? get_spd_array(lgtype) : scndata.nan());
    }

    public LaneGroupsAndCommodities choose_lgs_and_comms(LaneGroupType lgtype, Long commid){

        // choose lane groups
        Set<SimDataLanegroup> lgDatas = new HashSet<>();
        if(lgtype==null)
            lgDatas.addAll(lgData.values());
        else
            lgDatas.add(lgData.get(lgtype2id.get(lgtype)));

        // choose commodities
        Set<Long> commids = new HashSet<>();
        if(commid==null)
            commids.addAll(lgDatas.stream().flatMap(x->x.get_comm_ids().stream()).collect(toSet()));
        else
            commids.add(commid);

        return new LaneGroupsAndCommodities(lgDatas,commids);
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
