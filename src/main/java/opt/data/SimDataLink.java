package opt.data;

import java.util.HashMap;
import java.util.Map;

public class SimDataLink {

    public AbstractLink optlink;
    public double link_length_miles;
    public Map<LaneGroupType, SimDataLanegroup> lgData;

    public SimDataLink(AbstractLink optlink, common.Link otmlink){
//        this.otmlink = otmlink;
        this.optlink = optlink;
        link_length_miles = otmlink.length / 1609.344;
        lgData = new HashMap<>();

        models.fluid.LaneGroup lg;

        if(optlink.params.has_mng()){
            lg = (models.fluid.LaneGroup) otmlink.dnlane2lanegroup.get(0);
            lgData.put(LaneGroupType.mng,new SimDataLanegroup(lg));
        }

        lg = (models.fluid.LaneGroup) otmlink.dnlane2lanegroup.get(optlink.get_mng_lanes());
        lgData.put(LaneGroupType.gp,new SimDataLanegroup(lg));

        if(optlink.params.has_aux()){
            lg = (models.fluid.LaneGroup) otmlink.dnlane2lanegroup.get(
                    optlink.get_mng_lanes() + optlink.get_gp_lanes());
            lgData.put(LaneGroupType.aux,new SimDataLanegroup(lg));
        }

    }

    public void update(){
        lgData.values().forEach(x->x.update());
    }

    public Time2DSeries get_vehs(LaneGroupType lgtype){

        boolean all_lgs = lgtype==null;

        // bad lg type
        if(!all_lgs && !lgData.containsKey(lgtype))
            return null;

        Time2DSeries X = new Time2DSeries();
        X.time = optlink.mysegment.my_fwy_scenario.simData.time;

        if(all_lgs){
            // TODO G IMPLEMENT THIS 
//            for(LaneGroupType lgt : lgData.keySet())
//                for(SimDataLanegroup.CellData celldata : lgData.get(lgt).celldata)
//                    X.values.add(celldata.vehs);
        } else {
            for(SimDataLanegroup.CellData celldata : lgData.get(lgtype).celldata)
                X.values.add(celldata.vehs);
        }

        return X;
    }

}
