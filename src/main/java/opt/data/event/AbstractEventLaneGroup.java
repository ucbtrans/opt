package opt.data.event;

import jaxb.Event;
import opt.data.AbstractLink;
import opt.data.LaneGroupType;

import java.util.List;

public abstract class AbstractEventLaneGroup extends AbstractEvent {

    protected LaneGroupType lgtype;

    public AbstractEventLaneGroup(long id, String type, float timestamp, String name, List<AbstractLink> links, LaneGroupType lgtype) throws Exception {
        super(id, type, timestamp, name);
        this.lgtype = lgtype;
        if (!set_links(links))
            throw new Exception("Links in Event are not unique");
    }

    @Override
    public Event to_jaxb() {
        jaxb.Event jevent = super.to_jaxb();

        if(!links.isEmpty()){

            jaxb.EventTarget jtgt = new jaxb.EventTarget();
            jevent.setEventTarget(jtgt);
            jtgt.setType("lanegroups");

            String str = "";
            for(AbstractLink link : links){
                int [] ln = link.lgtype2lanes(lgtype);
                str += String.format("%d(%d#%d),",link.get_id(),ln[0],ln[1]);
            }
            str = str.substring(0,str.length()-1);
            jtgt.setLanegroups(str);
        }

        return jevent;
    }

    /////////////////////
    // API
    /////////////////////

    public void add_link(AbstractLink x) throws Exception {
        if(links.contains(x))
            return;
        if(!x.has_lgtype(lgtype))
            throw new Exception("!x.has_lgtype(lgtype)");
        if(!links.contains(x))
            links.add(x);
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
