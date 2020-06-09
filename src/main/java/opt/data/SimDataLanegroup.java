package opt.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimDataLanegroup {

    protected List<SimCellData> celldata;

    public SimDataLanegroup(models.fluid.FluidLaneGroup lg, Set<Long> commids){
        celldata = new ArrayList<>();
        for(int i=0;i<lg.cells.size();i++)
            celldata.add(new SimCellData(commids));
    }

    protected double [] get_veh(Long commid,int numtime){
        double [] X = new double[numtime];
        for (SimCellData simcell : celldata) {
            if(commid==null){
                for(List<Double> list : simcell.vehs.values()){
                    for (int k = 0; k < list.size(); k++)
                        X[k] += list.get(k);
                }
            } else {
                if (simcell.vehs.containsKey(commid)) {
                    List<Double> list = simcell.vehs.get(commid);
                    for (int k = 0; k < list.size(); k++)
                        X[k] += list.get(k);
                }
            }
        }
        return X;
    }

    protected double [] get_flw_exiting(Long commid,int numtime){
        double [] X = new double[numtime];
        SimCellData lastcell = celldata.get(celldata.size()-1);
        if(commid==null){
            X = lastcell.get_total_flw();
        } else {
            if (lastcell.vehs.containsKey(commid)) {
                List<Double> list = lastcell.flws.get(commid);
                for (int k = 0; k < list.size(); k++)
                    X[k] += list.get(k);
            }
        }
        return X;
    }

    protected List<double[]> get_cell_speeds(float ffspeed_mph,double cell_length_miles){
        List<double[]> speeds = new ArrayList<>();
        for(SimCellData cd : celldata)
            speeds.add(cd.get_speed(ffspeed_mph,cell_length_miles));
        return speeds;
    }

}
