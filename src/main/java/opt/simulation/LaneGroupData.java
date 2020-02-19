package opt.simulation;

import java.util.List;

public class LaneGroupData {

    public int num_cells;
    double [] time;
    double [][] data;

    public LaneGroupData(NetworkData.LanegroupD lgdata, List<Float> t){
        this.num_cells = lgdata.celldata.size();
        this.data = new double[num_cells][t.size()];
        this.time = new double[t.size()];
        for(int k=0;k<t.size();k++)
            time[k] = t.get(k);
    }
}
