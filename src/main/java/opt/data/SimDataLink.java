package opt.data;

import java.util.*;

public class SimDataLink {

    private SimDataScenario scndata;
    public double link_length_miles;
    public Map<LaneGroupType,Long> lgtype2id;
    public Map<Long, SimDataLanegroup> lgData;

    public SimDataLink(SimDataScenario scndata,AbstractLink optlink, common.Link otmlink, Set<Long> commids){

        this.scndata = scndata;
        link_length_miles = otmlink.length / 1609.344;
        lgData = new HashMap<>();
        lgtype2id = new HashMap<>();

        models.fluid.FluidLaneGroup lg;

        if(optlink.params.has_mng()){
            lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(0);
            lgtype2id.put(LaneGroupType.mng,lg.id);
            lgData.put(lg.id,new SimDataLanegroup(lg,commids));
        }

        lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(optlink.get_mng_lanes()+1);
        lgtype2id.put(LaneGroupType.gp,lg.id);
        lgData.put(lg.id,new SimDataLanegroup(lg,commids));

        if(optlink.params.has_aux()){
            lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(
                    optlink.get_mng_lanes() + optlink.get_gp_lanes() + 1);
            lgtype2id.put(LaneGroupType.aux,lg.id);
            lgData.put(lg.id,new SimDataLanegroup(lg,commids));
        }

    }

    protected List<Double> get_veh_list(LaneGroupType lgtype,Long commid){
        assert(lgtype2id.containsKey(lgtype) || lgtype==null);
        if(lgtype==null){
            List<Double> X = null;
            for(SimDataLanegroup data : lgData.values()){
                List<Double> XX = data.get_veh(commid);
                if(X==null)
                    X = XX;
                else
                    for(int k=0;k<X.size();k++)
                        X.set(k,X.get(k)+XX.get(k));
            }
            return X;
        } else {
            return lgData.get(lgtype2id.get(lgtype)).get_veh(commid);
        }
    }

    protected List<Double> get_flw_list(LaneGroupType lgtype,Long commid) {
        assert(lgtype2id.containsKey(lgtype) || lgtype==null);
        if(lgtype==null){
            List<Double> X = null;
            for(SimDataLanegroup data : lgData.values()){
                List<Double> XX = data.get_flw(commid);
                if(X==null)
                    X = XX;
                else
                    for(int k=0;k<X.size();k++)
                        X.set(k,X.get(k)+XX.get(k));
            }
            return X;
        } else {
            return lgData.get(lgtype2id.get(lgtype)).get_flw(commid);
        }
    }

    protected List<Double> get_spd_list(LaneGroupType lgtype) {
        assert(lgtype2id.containsKey(lgtype) || lgtype==null);
        if(lgtype==null){
            List<Double> X = null;
            for(SimDataLanegroup data : lgData.values()){
                List<Double> XX = data.get_speed();
                if(X==null)
                    X = XX;
                else
                    for(int k=0;k<X.size();k++)
                        X.set(k,X.get(k)+XX.get(k));
            }
            return X;
        } else {
            return lgData.get(lgtype2id.get(lgtype)).get_speed();
        }
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
        return new TimeSeries(scndata.time,lgtype2id.containsKey(lgtype)||lgtype==null ? get_veh_list(lgtype,commid) : scndata.zeros());
    }

    /** returns a TimeSeries of vehicle numbers for the given lane group type and commodity id
     * lgtype==null means all lanes in the link
     * comm==null means all commodities
     * NOTE: if the lane group does not exist, returns a list of ZEROS
     */
    public TimeSeries get_flw(LaneGroupType lgtype,Long commid){
        return new TimeSeries(scndata.time,lgtype2id.containsKey(lgtype)||lgtype==null ? get_flw_list(lgtype,commid) : scndata.zeros());

    }

    /** returns a TimeSeries of vehicle numbers for the given lane group type
     * lgtype==null means all lanes in the link
     * NOTE: if the lane group does not exist, returns NULL
     */
    public TimeSeries get_speed(LaneGroupType lgtype){
        return new TimeSeries(scndata.time,lgtype2id.containsKey(lgtype)||lgtype==null ? get_spd_list(lgtype) : null);
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
