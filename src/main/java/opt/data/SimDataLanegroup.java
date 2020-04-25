package opt.data;

import models.fluid.AbstractCell;

import java.util.ArrayList;
import java.util.List;

public class SimDataLanegroup {

    private models.fluid.FluidLaneGroup lg;
    protected List<CellData> celldata;
    protected int num_cells;

    public SimDataLanegroup(models.fluid.FluidLaneGroup lg){
        this.lg = lg;
        celldata = new ArrayList<>();
        for(int i=0;i<num_cells;i++)
            celldata.add(new CellData());
    }

    protected void update(){
        for(int i=0;i<num_cells;i++) {
            CellData cd = celldata.get(i);
            AbstractCell cell = lg.cells.get(i);
            cd.vehs.add(cell.get_vehicles());
        }
    }

    protected class CellData {
        public List<Double> vehs = new ArrayList<>();
    }

}
