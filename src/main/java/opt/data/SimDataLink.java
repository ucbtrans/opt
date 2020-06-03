package opt.data;

import java.util.*;

public class SimDataLink {

    public double link_length_miles;
    public Map<LaneGroupType, SimDataLanegroup> lgData;

    public SimDataLink(AbstractLink optlink, common.Link otmlink, Set<Long> commids){

        link_length_miles = otmlink.length / 1609.344;
        lgData = new HashMap<>();

        models.fluid.FluidLaneGroup lg;

        if(optlink.params.has_mng()){
            lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(0);
            lgData.put(LaneGroupType.mng,new SimDataLanegroup(lg,commids));
        }

        lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(optlink.get_mng_lanes()+1);
        lgData.put(LaneGroupType.gp,new SimDataLanegroup(lg,commids));

        if(optlink.params.has_aux()){
            lg = (models.fluid.FluidLaneGroup) otmlink.dnlane2lanegroup.get(
                    optlink.get_mng_lanes() + optlink.get_gp_lanes() + 1);
            lgData.put(LaneGroupType.aux,new SimDataLanegroup(lg,commids));
        }

    }

    /////////////////////////////////////////////////
    // API
    /////////////////////////////////////////////////

    /** NOT IMPLEMENTED **/
    public TimeSeries get_veh(LaneGroupType lgtype,Long commid){
        TimeSeries X = null;
        return X;
    }

    /** NOT IMPLEMENTED **/
    public TimeSeries get_flw(LaneGroupType lgtype,Long commid){
        TimeSeries X = null;
        return X;
    }

    /** NOT IMPLEMENTED **/
    public TimeSeries get_speed(LaneGroupType lgtype,Long commid){
        TimeSeries X = null;
        return X;
    }

    /** NOT IMPLEMENTED **/
    public Time2DSeries get_veh_cell(LaneGroupType lgtype,Long commid){
        Time2DSeries X = null;
        return X;
    }

    /** NOT IMPLEMENTED **/
    public Time2DSeries get_flw_cell(LaneGroupType lgtype,Long commid){
        Time2DSeries X = null;
        return X;
    }

    /** NOT IMPLEMENTED **/
    public Time2DSeries get_speed_cell(LaneGroupType lgtype,Long commid){
        Time2DSeries X = null;
        return X;
    }

}
