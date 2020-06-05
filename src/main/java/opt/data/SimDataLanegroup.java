package opt.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimDataLanegroup {

    protected List<SimCellData> celldata;

    public SimDataLanegroup(models.fluid.FluidLaneGroup lg, Set<Long> commids){
        celldata = new ArrayList<>();
        for(int i=0;i<lg.cells.size();i++)
            celldata.add(new SimCellData(commids));
    }

    protected int get_num_time(){
        return celldata.get(0).vehs.values().iterator().next().size();
    }

    protected List<Double> get_veh(Long commid){
        double [] X = new double[get_num_time()];
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
        return Arrays.stream(X).boxed().collect(Collectors.toList());
    }

    protected List<Double> get_flw(Long commid){
        double [] X = new double[get_num_time()];
        SimCellData lastcell = celldata.get(celldata.size()-1);
        if(commid==null){
            for(List<Double> list : lastcell.flws.values()){
                for (int k = 0; k < list.size(); k++)
                    X[k] += list.get(k);
            }
        } else {
            if (lastcell.vehs.containsKey(commid)) {
                List<Double> list = lastcell.flws.get(commid);
                for (int k = 0; k < list.size(); k++)
                    X[k] += list.get(k);
            }
        }
        return Arrays.stream(X).boxed().collect(Collectors.toList());
    }

    protected List<Double> get_speed(){
        List<Double> flws = get_flw(null);
        List<Double> vehs = get_veh(null);
        List<Double> speeds = new ArrayList<>();
        for(int i=0;i<flws.size();i++){
            double speed = flws.get(i)/vehs.get(i);
            speeds.add(speed);
        }
        return speeds;
    }
}
