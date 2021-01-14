package opt.data.event;

import jaxb.Event;
import opt.data.control.ControlSchedule;
import utils.OTMUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class EventControlToggle extends AbstractEvent {

    protected List<ControlSchedule> controllers;
    public boolean ison;

    public EventControlToggle(long id, String type, float timestamp, String name, List<ControlSchedule> controllers, boolean ison) throws Exception {
        super(id, type, timestamp, name);
        this.controllers = controllers;
        this.ison = ison;

        // check no repeat controllers
        Set<ControlSchedule> X = new HashSet<>();
        X.addAll(controllers);
        if(X.size()!=controllers.size())
            throw new Exception("Controllers in EventControlToggle are not unique");
    }

    @Override
    public Event to_jaxb() {
        jaxb.Event jevent = super.to_jaxb();

        jaxb.EventTarget jtgt = new jaxb.EventTarget();
        jevent.setEventTarget(jtgt);
        jtgt.setIds(OTMUtils.comma_format(controllers.stream().map(x->x.getId()).collect(toList())));
        jtgt.setType("controllers");

        jaxb.Parameters pmtrs = new jaxb.Parameters();
        jevent.setParameters(pmtrs);
        jaxb.Parameter pmtr = new jaxb.Parameter();
        pmtrs.getParameter().add(pmtr);
        pmtr.setName("ison");
        pmtr.setValue(ison?"true":"false");
        return jevent;
    }

    public List<Long> get_controller_ids(){
        return controllers.stream().map(x->x.getId()).collect(toList());
    }

    /////////////////////
    // API
    /////////////////////

    public void add_controller(ControlSchedule x) {
        if(!controllers.contains(x))
            controllers.add(x);
    }

    public void remove_controller_by_index(int i){
        if(i>=0 && i<controllers.size())
            controllers.remove(i);
    }

    public void remove_controller(ControlSchedule x){
        controllers.remove(x);
    }

}
