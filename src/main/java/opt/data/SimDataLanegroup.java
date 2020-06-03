package opt.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimDataLanegroup {

    protected models.fluid.FluidLaneGroup lg;
    protected List<SimCellData> celldata;
    protected int num_cells;

    public SimDataLanegroup(models.fluid.FluidLaneGroup lg, Set<Long> commids){
        this.lg = lg;
        this.num_cells = lg.cells.size();
        celldata = new ArrayList<>();
        for(int i=0;i<num_cells;i++)
            celldata.add(new SimCellData(commids));
    }
}
