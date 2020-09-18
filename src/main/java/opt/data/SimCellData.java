package opt.data;

import opt.utils.Misc;

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

    protected void set(long commid,List<Double> flwdata,List<Double> vehdata,float sim_dt_sec,float out_dt_sec){
        int numtime = vehdata.size();
        double [] veh = new double[numtime];
        double [] flw = new double[numtime];
        double alpha = 3600d/out_dt_sec;
        double beta = sim_dt_sec/out_dt_sec;

        if(!vehdata.isEmpty()) {
            for(int k=0;k<numtime;k++){
                double this_flow = k==0 ? 0d : (flwdata.get(k) - flwdata.get(k-1)) * alpha;
                flw[k] = this_flow;
                veh[k] = beta * vehdata.get(k);
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
            speeds[k] = sumveh<1 ? ffspeed_mph : cell_length_miles*sumflw/sumveh;
            if(speeds[k]>ffspeed_mph)
                speeds[k] = ffspeed_mph;
        }
        return speeds;
    }

    protected double[] get_cell_flw(Set<Long> commids){
        int numtime = flws.values().iterator().next().length;
        double [] flw = new double[numtime];
        for(long commid :commids){
            double [] x = flws.get(commid);
            for(int k=0;k<numtime;k++)
                flw[k] += x[k];
        }
        return flw;
    }

    protected double[] get_cell_veh(Set<Long> commids){
        int numtime = vehs.values().iterator().next().length;
        double [] veh = new double[numtime];
        for(long commid :commids){
            double [] x = vehs.get(commid);
            for(int k=0;k<numtime;k++)
                veh[k] += x[k];
        }
        return veh;
    }

    protected double[] get_cell_dty(Set<Long> commids,double cell_length_miles){
        int numtime = vehs.values().iterator().next().length;
        double[] veh = get_cell_veh(commids);
        for(int k=0;k<numtime;k++)
            veh[k] /= cell_length_miles;
        return veh;
    }
}
