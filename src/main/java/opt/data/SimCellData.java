package opt.data;

import java.util.*;

public class SimCellData {

    // for each commodity id, a list over time
    public Map<Long, List<Double>> vehs;

    public SimCellData(Set<Long> commids){
        vehs = new HashMap<>();
        for(Long commid : commids)
            vehs.put(commid,new ArrayList<>());
    }

    public List<Double> get_vehs_for_commid(Long commid) {
        if (vehs.isEmpty())
            return null;
        if (vehs.values().iterator().next().isEmpty())
            return null;
        if (commid == null) {
            List<Double> X = new ArrayList<>();
            int numtime = vehs.values().iterator().next().size();
            for (int k = 0; k < numtime; k++) {
                int finalK = k;
                X.add(vehs.values().stream().mapToDouble(v -> v.get(finalK)).sum());
            }
            return X;
        } else if (vehs.containsKey(commid)) {
            return vehs.get(commid);
        } else {
            return null;
        }
    }

}
