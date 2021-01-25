package opt.data.event;

import jaxb.Event;
import opt.data.AbstractLink;
import opt.data.LaneGroupType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class AbstractEventLaneGroup extends AbstractEvent {

    protected List<AbstractLink> links;
    protected LaneGroupType lgtype;

    public AbstractEventLaneGroup(long id, String type, float timestamp, String name, List<AbstractLink> links, LaneGroupType lgtype) throws Exception {
        super(id, type, timestamp, name);
        this.lgtype = lgtype;
        if (!set_links(links))
            throw new Exception("Links in EventControlToggle are not unique");
    }

    @Override
    public Event to_jaxb() {
        jaxb.Event jevent = super.to_jaxb();

        jaxb.EventTarget jtgt = new jaxb.EventTarget();
        jevent.setEventTarget(jtgt);

        String str = "";
        for(AbstractLink link : links){
            int [] ln = link.lgtype2lanes(lgtype);
            str += String.format("%d(%d#%d),",link.get_id(),ln[0],ln[1]);
        }
        str = str.substring(0,str.length()-1);
        jtgt.setLanegroups(str);
        jtgt.setType("lanegroups");

        return jevent;
    }

    public List<Long> get_link_ids(){
        return links.stream().map(x->x.id).collect(toList());
    }
    

    /////////////////////
    // API
    /////////////////////
    
    public List<AbstractLink> get_links() {
        return links;
    }
    
    public boolean set_links(List<AbstractLink> links) {
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
    

    public void add_link(AbstractLink x) throws Exception {
        if(links.contains(x))
            return;
        if(!x.has_lgtype(lgtype))
            throw new Exception("!x.has_lgtype(lgtype)");
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

    public LaneGroupType get_lgtype(){
        return lgtype;
    }

    public void set_lgtype(LaneGroupType lgt) throws Exception {
        if(!links.stream().allMatch(l->l.has_lgtype(lgt)))
            throw new Exception("Not all links in event have lane group type "+lgt);
        this.lgtype = lgt;
    }

}
