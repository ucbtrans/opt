package opt.simulation;

import common.Link;
import models.AbstractLaneGroup;
import models.fluid.Cell;
import opt.data.LaneGroupType;
import runner.Scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkData {
    public Map<Long, LinkD> linkData;
    public List<Float> time = new ArrayList<>();

    public NetworkData(Scenario scenario){
        linkData = new HashMap<>();
        for(Link link : scenario.network.links.values())
            linkData.put(link.getId(),new LinkD(link));
    }

    public void update(float t, Scenario scenario) {
        time.add(t);
        for(Link link : scenario.network.links.values()) {
            LinkD linkd = linkData.get(link.getId());
            for(AbstractLaneGroup alg : link.lanegroups_flwdn.values()) {
                models.fluid.LaneGroup lg = ( models.fluid.LaneGroup)alg;
                LanegroupD lgd = linkd.lanegroupData.get(lg.id);
                for(int i=0;i<lg.cells.size();i++) {
                    CellD cd = lgd.celldata.get(i);
                    Cell cell = lg.cells.get(i);
                    cd.veh.add(cell.get_vehicles());
//                    cd.flow_vph.add(cell.get_flow_vph());
                }
            }
        }
    }

    public LaneGroupData get_vehs_for_link(long link_id, LaneGroupType lgType){

        // TODO ONLY WORKS FOR GP
        assert(lgType==LaneGroupType.gp);

        if(!linkData.containsKey(link_id))
            return null;

        if(!linkData.get(link_id).lanegroupData.containsKey(lgType))
            return null;

        LanegroupD stored_data = linkData.get(link_id).lanegroupData.get(lgType);
        LaneGroupData return_data = new LaneGroupData(stored_data,time);
        for(int c=0;c<stored_data.celldata.size();c++){
            CellD cdata = stored_data.celldata.get(c);
            for(int k=0;k<time.size();k++)
                return_data.data[c][k] = cdata.veh.get(k);
        }
        return return_data;
    }


    ////////////////////////////////////////////////

    protected class LinkD {

        public double link_length_miles;
        public Map<LaneGroupType, LanegroupD> lanegroupData;

        public LinkD(common.Link link){
            link_length_miles = link.length / 1609.344;
            lanegroupData = new HashMap<>();

            // TODO: For now we are only dealing with single gp lane groups
            assert(link.lanegroups_flwdn.size()==1);

            for(AbstractLaneGroup lg : link.lanegroups_flwdn.values())
                lanegroupData.put(LaneGroupType.gp,new LanegroupD((models.fluid.LaneGroup)lg));
        }
    }

    protected class LanegroupD {
        public List<CellD> celldata;
        public LanegroupD(models.fluid.LaneGroup lg){
            celldata = new ArrayList<>();
            for(Cell cell : lg.cells)
                celldata.add(new CellD(cell));
        }
    }

    protected class CellD {
        public List<Double> flow_vph;
        public List<Double> veh;
        public CellD(Cell cell){
            flow_vph = new ArrayList<>();
            veh = new ArrayList<>();
        }
    }

}

