package opt.data;

import models.fluid.Cell;

import java.util.ArrayList;
import java.util.List;

public class SimDataLanegroup {

    private models.fluid.LaneGroup lg;
    List<CellData> celldata;
    public int num_cells;

    public SimDataLanegroup(models.fluid.LaneGroup lg){
        this.lg = lg;
        celldata = new ArrayList<>();
        for(int i=0;i<num_cells;i++)
            celldata.add(new CellData());
    }

    protected void update(){
        for(int i=0;i<num_cells;i++) {
            CellData cd = celldata.get(i);
            Cell cell = lg.cells.get(i);
            cd.vehs.add(cell.get_vehicles());
        }
    }

    protected class CellData {
        public List<Double> vehs = new ArrayList<>();
    }

}
