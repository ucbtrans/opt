package opt.data;

import java.util.*;

public class SimCellData {

    public Map<Long, List<Double>> vehs;    // commid->vehs over time
    public Map<Long, List<Double>> flws;    // commid->flw over time

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
        for(int i = 1; i < cummvalues.size(); ++i)
            flw.add((cummvalues.get(i)-cummvalues.get(i-1))*alpha);
        flws.put(commid,flw);
    }

    public void set_vehs(long commid,List<Double> values){
        vehs.put(commid,values);
    }

}
