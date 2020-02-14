package opt.simulation;

import common.Link;
import models.AbstractLaneGroup;
import models.fluid.Cell;
import runner.Scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkData {
    public Map<Long,LinkData> linkData = new HashMap<>();
    public List<Float> time = new ArrayList<>();

    public void update(float t, Scenario scenario) {
        time.add(t);
        for(Link link : scenario.network.links.values()) {
            LinkData linkd = linkData.get(link.getId());
            for(AbstractLaneGroup alg : link.lanegroups_flwdn.values()) {
                models.fluid.LaneGroup lg = ( models.fluid.LaneGroup)alg;
                LaneGroupData lgd = linkd.lanegroupData.get(lg.id);
                for(int i=0;i<lg.cells.size();i++) {
                    CellData cd = lgd.celldata.get(i);
                    Cell cell = lg.cells.get(i);
                    cd.veh.add(cell.get_vehicles());
                }
            }
        }
    }

}
