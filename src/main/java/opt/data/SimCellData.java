package opt.data;

import java.util.*;

public class SimCellData {

    public Map<Long, double []> vehs;    // commid->vehs over time [veh]
    public Map<Long, double []> flws;    // commid->flw over time [vph]

    public SimCellData(Set<Long> commids,int numtime){
        vehs = new HashMap<>();
        flws = new HashMap<>();
        for(Long commid : commids) {
            vehs.put(commid, new double[numtime]);
            flws.put(commid, new double[numtime]);
        }
    }

    protected void set(long commid,int [] time_indices,List<Double> flwdata,List<Double> vehdata,float sim_dt_sec,float out_dt_sec){
        int numtime = time_indices.length;
        double [] veh = new double[numtime];
        double [] flw = new double[numtime];
        double alpha = 3600d/sim_dt_sec;
        double beta = sim_dt_sec/out_dt_sec;

        if(!vehdata.isEmpty()) {
            for(int k=0;k<numtime;k++){
                int time_index = time_indices[k];
                double this_flow = time_index == 0 ? 0d : (flwdata.get(time_index) - flwdata.get(time_index - 1)) * alpha;
                flw[k] = this_flow;
                veh[k] = beta * vehdata.get(time_index);
            }
        }

        vehs.put(commid,veh);
        flws.put(commid,flw);
    }

    protected double[] get_speed(float ffspeed_mph,double cell_length_miles){
        int numtime = vehs.values().iterator().next().length;
        double [] speeds = new double[numtime];
        for(int k=0;k<numtime;k++) {
            double sumflw = 0d;
            double sumveh = 0d;
            for (long commid : vehs.keySet()) {
                sumflw += flws.get(commid)[k];
                sumveh += vehs.get(commid)[k];
            }
            speeds[k] = sumveh<1 || sumflw<1 ? ffspeed_mph : cell_length_miles*sumflw/sumveh;
            if(speeds[k]>ffspeed_mph)
                speeds[k] = ffspeed_mph;
        }
        return speeds;
    }

}
