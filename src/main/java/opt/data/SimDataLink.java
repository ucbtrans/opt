package opt.data;

import models.fluid.AbstractFluidModel;

import java.util.*;

public class SimDataLink {

    private SimDataScenario scndata;
    public long id;
    public double link_length_miles;
    public boolean is_source;
    protected float ffspeed_mph;
    public Map<LaneGroupType,Long> lgtype2id;
    public Map<Long, SimDataLanegroup> lgData;

    public SimDataLink(SimDataScenario scndata,AbstractLink optlink, common.Link otmlink, Set<Long> commids){

        this.id = otmlink.getId();
        this.scndata = scndata;
        this.is_source = otmlink.is_source;
        this.link_length_miles = otmlink.length / 1609.344;

        float dt_hr = ((AbstractFluidModel)otmlink.model).dt_sec / 3600f;

        lgData = new HashMap<>();
        lgtype2id = new HashMap<>();

        models.fluid.FluidLaneGroup lg;

        if(optlink.params.has_mng()){
            lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(0);
            lgtype2id.put(LaneGroupType.mng,lg.id);
            lgData.put(lg.id,new SimDataLanegroup(lg,commids));
        }

        lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(optlink.get_mng_lanes()+1);
        int numcells = lg.cells.size();
        lgtype2id.put(LaneGroupType.gp,lg.id);
        lgData.put(lg.id,new SimDataLanegroup(lg,commids));
        ffspeed_mph = (float) (lg.ffspeed_cell_per_dt * link_length_miles/numcells / dt_hr);

        if(optlink.params.has_aux()){
            lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(
                    optlink.get_mng_lanes() + optlink.get_gp_lanes() + 1);
            lgtype2id.put(LaneGroupType.aux,lg.id);
            lgData.put(lg.id,new SimDataLanegroup(lg,commids));
        }

    }

    protected double get_length_lg_miles(){
        return link_length_miles*lgData.size();
    }

    protected double cell_length(){
        return link_length_miles/lgData.values().iterator().next().celldata.size();
    }

    protected double[] get_veh_array(LaneGroupType lgtype,Long commid){
        assert(lgtype2id.containsKey(lgtype) || lgtype==null);
        if(lgtype==null){
            double [] X = new double[scndata.numtime()];
            for(SimDataLanegroup data : lgData.values()){
                double [] XX = data.get_veh(commid);
                for(int k=0;k<X.length;k++)
                    X[k] += XX[k];
            }
            return X;
        } else {
            return lgData.get(lgtype2id.get(lgtype)).get_veh(commid);
        }
    }

    protected double [] get_flw_array(LaneGroupType lgtype,Long commid) {
        assert(lgtype2id.containsKey(lgtype) || lgtype==null);
        if(lgtype==null){
            double [] X = new double[scndata.numtime()];
            for(SimDataLanegroup data : lgData.values()){
                double [] XX = data.get_flw_exiting(commid);
                for(int k=0;k<X.length;k++)
                    X[k] += XX[k];
            }
            return X;
        } else {
            return lgData.get(lgtype2id.get(lgtype)).get_flw_exiting(commid);
        }
    }

    protected double [] get_spd_array(LaneGroupType lgtype) {
        assert(lgtype2id.containsKey(lgtype) || lgtype==null);
        if(is_source)
            return scndata.nan();

        double cell_length_miles = cell_length();

        double [] speeds = new double [scndata.numtime()];

        if(lgtype==null){
            for(int k=0;k<scndata.numtime();k++) {
                double sumflw = 0d;
                double sumveh = 0d;
                for(SimDataLanegroup lg : lgData.values()) {

                    for (SimCellData cd : lg.celldata) {
                        for (long commid : cd.vehs.keySet()) {
                            sumflw += cd.flws.get(commid).get(k);
                            sumveh += cd.vehs.get(commid).get(k);
                        }
                    }
                }
                speeds[k] = sumveh<1 || sumflw<1 ? ffspeed_mph : cell_length_miles*sumflw/sumveh;
            }
        } else {

            SimDataLanegroup lgdata = lgData.get(lgtype2id.get(lgtype));

            for(int k=0;k<scndata.numtime();k++) {
                double sumflw = 0d;
                double sumveh = 0d;
                for (SimCellData cd : lgdata.celldata) {
                    for(long commid : cd.vehs.keySet()){
                        sumflw += cd.flws.get(commid).get(k);
                        sumveh += cd.vehs.get(commid).get(k);
                    }
                }
                speeds[k] = sumveh<1 || sumflw<1 ? ffspeed_mph : cell_length_miles*sumflw/sumveh;
            }

        }

        return speeds;

    }

    /////////////////////////////////////////////////
    // API
    /////////////////////////////////////////////////

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
        return new TimeSeries(scndata.time,lgtype2id.containsKey(lgtype)||lgtype==null ?
                get_flw_array(lgtype,commid) :
                new double[scndata.numtime()] );
    }

    /** returns a TimeSeries of vehicle numbers for the given lane group type
     * lgtype==null means all lanes in the link
     * NOTE: if the lane group does not exist, returns NULL
     */
    public TimeSeries get_speed(LaneGroupType lgtype){
        return new TimeSeries(scndata.time,lgtype2id.containsKey(lgtype)||lgtype==null ? get_spd_array(lgtype) : scndata.nan());
    }

//    /** NOT IMPLEMENTED **/
//    public Time2DSeries get_veh_cell(LaneGroupType lgtype,Long commid){
//        Time2DSeries X = null;
//        return X;
//    }
//
//    /** NOT IMPLEMENTED **/
//    public Time2DSeries get_flw_cell(LaneGroupType lgtype,Long commid){
//        Time2DSeries X = null;
//        return X;
//    }
//
//    /** NOT IMPLEMENTED **/
//    public Time2DSeries get_speed_cell(LaneGroupType lgtype,Long commid){
//        Time2DSeries X = null;
//        return X;
//    }

}
