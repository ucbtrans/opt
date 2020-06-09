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

    public void set_flws(long commid,List<Double> cummvalues,float dt_sec){
        List<Double> flw = new ArrayList<>();
        double alpha = 3600d/dt_sec;
        flw.add(0d);
        for(int i = 1; i < cummvalues.size(); ++i)
            flw.add((cummvalues.get(i)-cummvalues.get(i-1))*alpha);
        flws.put(commid,flw);
    }

    public void set_vehs(long commid,List<Double> values){
        vehs.put(commid,values);
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
}
