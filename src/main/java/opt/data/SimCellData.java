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

    protected void set(long commid,int [] time_indices,List<Double> flwdata,List<Double> vehdata,float dt_sec){
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

    public double [] get_total_flw(){
        int numtime = flws.values().iterator().next().length;
        double [] X = new double[numtime];
        for( double [] c : flws.values())
            for(int k=0;k<numtime;k++)
                X[k] += c[k];
        return X;
    }

    public double [] get_total_veh(){
        int numtime = vehs.values().iterator().next().length;
        double [] X = new double[numtime];
        for( double [] c : vehs.values())
            for(int k=0;k<numtime;k++)
                X[k] += c[k];
        return X;
    }

    public double[] get_speed(float ffspeed_mph,double cell_length_miles){
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

    /** returns flow*(TT-TTt), in unit [veh]. Should be multiplied by dt for delay units [veh.hr]
     * http://pems.dot.ca.gov/?dnode=Help&content=help_calc#perf **/
    public double[] get_delay_vehhr(float ffspeed_mph,double cell_length_miles,float threshold_mph,float dt_sec){
        double dt_hr = dt_sec/3600d;
        double cell_length_over_threshold = cell_length_miles / threshold_mph;
        int numtime = vehs.values().iterator().next().length;
        double [] delays = new double[numtime];
        for(int k=0;k<numtime;k++) {
            double flw = 0d;
            double veh = 0d;
            for (long commid : vehs.keySet()) {
                flw += flws.get(commid)[k];
                veh += vehs.get(commid)[k];
            }
            delays[k] =  veh==0d? 0d : Math.max(0d,(veh-flw*cell_length_over_threshold)*dt_hr);
        }
        return delays;
    }

}
