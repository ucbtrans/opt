package opt.data;

import java.util.ArrayList;
import java.util.List;

public class SimDataScenario {

    public FreewayScenario fwyscenario;
    public List<Float> time = new ArrayList<>();

    public SimDataScenario(FreewayScenario fwyscenario, runner.Scenario scenario){
        this.fwyscenario = fwyscenario;
        for(Segment segment : fwyscenario.segments.values())
            for(AbstractLink optlink : segment.get_links())
                optlink.simdata = new SimDataLink(optlink,scenario.network.links.get(optlink.id));
    }

    public void update(float t) {
        time.add(t);
        for(Segment segment : fwyscenario.segments.values())
            for(AbstractLink optlink : segment.get_links())
                optlink.simdata.update();
    }

    public TimeSeries get_vht_for_network(){

        // need at least 2 data points
        if(time.size()<2)
            return null;

        double dt = time.get(1) - time.get(0);
        List<Double> totalvehs = new ArrayList<>();
        for(int i=0;i<time.size();i++)
            totalvehs.add(0d);
        for(AbstractLink link : fwyscenario.get_links()) {
            List<Double> linkvehs = link.simdata.get_vehs(null).aggregate_cells();
            for(int i=0;i<linkvehs.size();i++)
                totalvehs.set(i, totalvehs.get(i) + linkvehs.get(i)*dt );
        }

        TimeSeries vht = new TimeSeries();
        vht.time = time;
        vht.values  = totalvehs;

        return vht;
    }

//    public TimeSeries get_vmt_for_network(){
//        XXX
//    }
//
//    public TimeSeries get_delay_for_network(){
//        XXX
//    }

}

