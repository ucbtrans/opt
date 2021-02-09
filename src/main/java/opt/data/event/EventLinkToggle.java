package opt.data.event;

import jaxb.Event;
import opt.data.AbstractLink;
import utils.OTMUtils;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class EventLinkToggle extends AbstractEvent {

    public boolean isopen;

    public EventLinkToggle(long id, String type, float timestamp, String name, List<AbstractLink> links, boolean isopen) throws Exception {
        super(id, type, timestamp, name);
        this.isopen = isopen;
        if (!set_links(links))
            throw new Exception("Links in Event are not unique");
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

    /////////////////////
    // API
    /////////////////////

    public void add_link(AbstractLink x){
        if(!links.contains(x))
            links.add(x);
    }

}
