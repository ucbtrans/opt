package opt.data.event;

import jaxb.Event;
import opt.data.AbstractLink;
import opt.data.LaneGroupType;

import java.util.List;

public class EventLanegroupLanes extends AbstractEventLaneGroup  {

    public Integer delta_lanes;

    public EventLanegroupLanes(long id, String type, float timestamp, String name, List<AbstractLink> links, LaneGroupType lgtype, Integer delta_lanes) throws Exception {
        super(id, type, timestamp, name, links,lgtype);
        this.delta_lanes = delta_lanes;
    }

    @Override
    public Event to_jaxb() {
        jaxb.Event jevent = super.to_jaxb();

        if(delta_lanes!=null){
            jaxb.Parameters pmtrs = new jaxb.Parameters();
            jevent.setParameters(pmtrs);
            jaxb.Parameter pmtr = new jaxb.Parameter();
            pmtrs.getParameter().add(pmtr);
            pmtr.setName("dlanes");
            pmtr.setValue(String.format("%d",delta_lanes));
        }

        return jevent;
    }


}
