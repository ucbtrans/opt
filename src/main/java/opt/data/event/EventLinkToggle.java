package opt.data.event;

import jaxb.Event;
import opt.data.AbstractLink;
import opt.data.control.ControlSchedule;
import utils.OTMUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class EventLinkToggle extends AbstractEvent {

    protected List<AbstractLink> links;
    public boolean isopen;

    public EventLinkToggle(long id, String type, float timestamp, String name, List<AbstractLink> links, boolean isopen) throws Exception {
        super(id, type, timestamp, name);
        this.isopen = isopen;
        this.links = links;

        // check no repeat links
        Set<AbstractLink> X = new HashSet<>();
        X.addAll(links);
        if(X.size()!=links.size())
            throw new Exception("Links in EventControlToggle are not unique");
    }

    @Override
    public Event to_jaxb() {
        jaxb.Event jevent = super.to_jaxb();

        jaxb.EventTarget jtgt = new jaxb.EventTarget();
        jevent.setEventTarget(jtgt);
        jtgt.setIds(OTMUtils.comma_format(links.stream().map(x->x.get_id()).collect(toList())));
        jtgt.setType("links");

        jaxb.Parameters pmtrs = new jaxb.Parameters();
        jevent.setParameters(pmtrs);
        jaxb.Parameter pmtr = new jaxb.Parameter();
        pmtrs.getParameter().add(pmtr);
        pmtr.setName("isopen");
        pmtr.setValue(isopen?"true":"false");

        return jevent;
    }

    public List<Long> get_link_ids(){
        return links.stream().map(x->x.id).collect(toList());
    }

    /////////////////////
    // API
    /////////////////////

    public void add_link(AbstractLink x){
        if(!links.contains(x))
            links.add(x);
    }

    public void remove_link_by_index(int i){
        if(i>=0 && i<links.size())
            links.remove(i);
    }

    public void remove_link(AbstractLink x){
        links.remove(x);
    }
}
