package opt.data;

import models.fluid.AbstractCell;

import java.util.*;
import java.util.stream.Collectors;

public class SimDataScenario {

    public FreewayScenario fwyscenario;
    public List<Float> time = new ArrayList<>();
    public Map<Long,SimDataLink> linkdata;

    public SimDataScenario(FreewayScenario fwyscenario, common.Scenario scenario){
        this.fwyscenario = fwyscenario;
        this.linkdata = new HashMap<>();

        Set<Long> commids = scenario.commodities.keySet();
        for(Segment segment : fwyscenario.segments.values())
            for(AbstractLink optlink : segment.get_links())
                linkdata.put(optlink.id,new SimDataLink(optlink,scenario.network.links.get(optlink.id),commids));
    }

    public void update(float t) {
        time.add(t);
        Set<Long> commids = fwyscenario.scenario.commodities.values().stream()
                .map(c->c.getId())
                .collect(Collectors.toSet());
        for(SimDataLink linkd : linkdata.values()){
            for(SimDataLanegroup lg : linkd.lgData.values()){
                for(int i=0;i<lg.num_cells;i++) {
                    SimCellData cd = lg.celldata.get(i);
                    AbstractCell cell = lg.lg.cells.get(i);
                    for(Long commid: commids)
                        cd.vehs.get(commid).add(cell.get_veh_for_commodity(commid));
                }
            }
        }

    }

    /////////////////////////////////////////////////
    // API
    /////////////////////////////////////////////////

    public TimeSeries get_vht_for_network(Long commid){

        // need at least 2 data points
        if(time.size()<2)
            return null;

        double dt = (time.get(1) - time.get(0))/3600d;
        List<Double> totalvehs = new ArrayList<>(); // list over time

        for(int k=0;k<time.size();k++){
            final int finalk = k;
            double value = 0d;

            for(SimDataLink simlink : linkdata.values())
                for (SimDataLanegroup simlg : simlink.lgData.values())
                    for (SimCellData simcell : simlg.celldata)
                        value += simcell.vehs.values().stream().mapToDouble(z -> z.get(finalk)).sum();

            totalvehs.add(value*dt);
        }
        return new TimeSeries(time,totalvehs);
    }

    /** NOT IMPLEMENTED **/
    public TimeSeries get_vmt_for_network(Long commid){

        // need at least 2 data points
        if(time.size()<2)
            return null;

        double dt = (time.get(1) - time.get(0))/3600d;
        List<Double> totalvehs = new ArrayList<>(); // list over time

//        for(int k=0;k<time.size();k++){
//            final int finalk = k;
//            double value = 0d;
//
//            for(SimDataLink simlink : linkdata.values())
//                for (SimDataLanegroup simlg : simlink.lgData.values())
//                    for (SimCellData simcell : simlg.celldata)
//                        value += simcell.vehs.values().stream().mapToDouble(z -> z.get(finalk)).sum();
//
//            totalvehs.add(value*dt);
//        }
        return new TimeSeries(time,totalvehs);
    }
    /** NOT IMPLEMENTED **/
    public TimeSeries get_speed_for_network(Long commid){

        // need at least 2 data points
        if(time.size()<2)
            return null;

        double dt = (time.get(1) - time.get(0))/3600d;
        List<Double> totalvehs = new ArrayList<>(); // list over time

//        for(int k=0;k<time.size();k++){
//            final int finalk = k;
//            double value = 0d;
//
//            for(SimDataLink simlink : linkdata.values())
//                for (SimDataLanegroup simlg : simlink.lgData.values())
//                    for (SimCellData simcell : simlg.celldata)
//                        value += simcell.vehs.values().stream().mapToDouble(z -> z.get(finalk)).sum();
//
//            totalvehs.add(value*dt);
//        }
        return new TimeSeries(time,totalvehs);
    }

    /** NOT IMPLEMENTED **/
    public TimeSeries get_vht_for_route(long routeid,Long commid){
        TimeSeries X = null;
        return X;
    }

    /** NOT IMPLEMENTED **/
    public TimeSeries get_vmt_for_route(long routeid,Long commid){
        TimeSeries X = null;
        return X;
    }

    /** NOT IMPLEMENTED **/
    public TimeSeries get_speed_for_route(long routeid,Long commid){
        TimeSeries X = null;
        return X;
    }
}

