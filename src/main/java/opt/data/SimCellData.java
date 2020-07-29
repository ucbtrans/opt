package opt.data;

import java.util.*;

public class SimCellData {

    public Map<Long, List<Double>> vehs;    // commid->vehs over time [veh]
    public Map<Long, List<Double>> flws;    // commid->flw over time [vph]

    public SimCellData(Set<Long> commids){
        vehs = new HashMap<>();
        flws = new HashMap<>();
        for(Long commid : commids) {
            vehs.put(commid, new ArrayList<>());
            flws.put(commid, new ArrayList<>());
        }
    }

    protected void set(long commid,List<Integer> time_indices,List<Double> flwdata,List<Double> vehdata,float dt_sec){
        List<Double> veh = new ArrayList<>();
        List<Double> flw = new ArrayList<>();
        double alpha = 3600d/dt_sec;

        for(int time_index : time_indices){
            double this_flow = time_index==0 ? 0d : (flwdata.get(time_index)-flwdata.get(time_index-1))*alpha;
            flw.add(this_flow);
            veh.add(vehdata.get(time_index));
        }
        vehs.put(commid,veh);
        flws.put(commid,flw);
    }

    public double [] get_total_flw(){
        int numtime = flws.values().iterator().next().size();
        double [] X = new double[numtime];
        for( List<Double> c : flws.values())
            for(int k=0;k<numtime;k++)
                X[k] += c.get(k);
        return X;
    }

    public double [] get_total_veh(){
        int numtime = vehs.values().iterator().next().size();
        double [] X = new double[numtime];
        for( List<Double> c : vehs.values())
            for(int k=0;k<numtime;k++)
                X[k] += c.get(k);
        return X;
    }

    public double[] get_speed(float ffspeed_mph,double cell_length_miles){
        int numtime = vehs.values().iterator().next().size();
        double [] speeds = new double[numtime];
        for(int k=0;k<numtime;k++) {
            double sumflw = 0d;
            double sumveh = 0d;
            for (long commid : vehs.keySet()) {
                sumflw += flws.get(commid).get(k);
                sumveh += vehs.get(commid).get(k);
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
        int numtime = vehs.values().iterator().next().size();
        double [] delays = new double[numtime];
        for(int k=0;k<numtime;k++) {
            double flw = 0d;
            double veh = 0d;
            for (long commid : vehs.keySet()) {
                flw += flws.get(commid).get(k);
                veh += vehs.get(commid).get(k);
            }
            delays[k] =  veh==0d? 0d : Math.max(0d,(veh-flw*cell_length_over_threshold)*dt_hr);
        }
        return delays;
    }

}
