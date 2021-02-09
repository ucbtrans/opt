package opt.data.event;

import opt.data.AbstractLink;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public abstract class AbstractEvent implements Comparable<AbstractEvent> {

    public final long id;
    public final String type;
    public float timestamp;
    public String name;
    protected List<AbstractLink> links;

    public AbstractEvent(long id, String type, float timestamp, String name) {
        this.id = id;
        this.type = type;
        this.timestamp = timestamp;
        this.name = name;
    }

    public final List<Long> get_link_ids(){
        return links.stream().map(x->x.id).collect(toList());
    }

    public final List<AbstractLink> get_links() {
        return links;
    }

    public final boolean set_links(List<AbstractLink> links) {
        if (links == null)
            return false;

        this.links = links;

        // check no repeat links
        Set<AbstractLink> X = new HashSet<>();
        X.addAll(links);
        if(X.size()!=links.size())
            return false;

        return true;
    }

    public final void remove_link_by_index(int i){
        if(i>=0 && i<links.size())
            links.remove(i);
    }

    public final void remove_link(AbstractLink x){
        links.remove(x);
    }

    public jaxb.Event to_jaxb(){
        jaxb.Event jevent = new jaxb.Event();
        jevent.setId(id);
        jevent.setType(type);
        jevent.setTimestamp(timestamp);
        jevent.setName(name);
        return jevent;
    }

    @Override
    public int compareTo(AbstractEvent that) {
        return Float.compare(this.timestamp,that.timestamp);
    }
}
