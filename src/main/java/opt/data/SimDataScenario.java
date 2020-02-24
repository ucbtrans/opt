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

    }

    public TimeSeries get_vmt_for_network(){

    }

    public TimeSeries get_delay_for_network(){

    }

}

