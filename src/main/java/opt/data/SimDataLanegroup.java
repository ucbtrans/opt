package opt.data;

import java.util.*;

public class SimDataLanegroup {

    public Map<Long, double []> vehs;    // commid->vehs over time [veh]
    public Map<Long, double []> flws;    // commid->flw over time [vph]

    public List<SimCellData> celldata;

    public SimDataLanegroup(models.fluid.FluidLaneGroup lg, Set<Long> commids,boolean storecelldata,boolean storelgdata,int numtime){

        if(storelgdata){
            vehs = new HashMap<>();
            flws = new HashMap<>();
            for(Long commid : commids) {
                vehs.put(commid, new double[numtime]);
                flws.put(commid, new double[numtime]);
            }
        }

        if(storecelldata){
            celldata = new ArrayList<>();
            for(int i=0;i<lg.cells.size();i++)
                celldata.add(new SimCellData(commids,numtime));
        }

    }

    protected void set_lg_data(long commid,int [] time_indices,List<Double> flwdata,List<Double> vehdata,float dt_sec){
        int numtime = time_indices.length;
        double [] veh = new double[numtime];
        double [] flw = new double[numtime];
        double alpha = 3600d/dt_sec;

        if(!vehdata.isEmpty()) {
            for(int k=0;k<numtime;k++){
                int time_index = time_indices[k];
                double this_flow = time_index == 0 ? 0d : (flwdata.get(time_index) - flwdata.get(time_index - 1)) * alpha;
                flw[k] = this_flow;
                veh[k] = vehdata.get(time_index);
            }
        }

        vehs.put(commid,veh);
        flws.put(commid,flw);
    }

    protected double [] get_veh(Long commid,int numtime, boolean usecells){
        double [] X = new double[numtime];

        if(usecells){
            for (SimCellData simcell : celldata) {
                if(commid==null){
                    for(double[] list : simcell.vehs.values()){
                        for (int k = 0; k < list.length; k++)
                            X[k] += list[k];
                    }
                } else {
                    if (simcell.vehs.containsKey(commid)) {
                        double[] list = simcell.vehs.get(commid);
                        for (int k = 0; k < list.length; k++)
                            X[k] += list[k];
                    }
                }
            }
        }
        else{
            System.out.println("USE LANEGROUP DATA");
        }

        return X;
    }

    protected double [] get_flw_exiting(Long commid,int numtime, boolean usecells){
        double [] X = new double[numtime];

        if(usecells){
            SimCellData lastcell = celldata.get(celldata.size()-1);
            if(commid==null){
                X = lastcell.get_total_flw();
            } else {
                if (lastcell.vehs.containsKey(commid)) {
                    double[] list = lastcell.flws.get(commid);
                    for (int k = 0; k < list.length; k++)
                        X[k] += list[k];
                }
            }
        }
        else{
            System.out.println("USE LANEGROUP DATA");
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
