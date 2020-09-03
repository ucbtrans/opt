package opt.data.control;

import opt.data.AbstractLink;
import opt.data.LaneGroupType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractActuator {

	protected long id;
	protected Set<AbstractLink> links;
	protected LaneGroupType lgtype;

	public AbstractActuator(long id,AbstractLink link,LaneGroupType lgtype){
		this.id = id;
		this.lgtype = lgtype;
		this.links = new HashSet<>();
		links.add(link);
	}

	public AbstractActuator(long id, Collection<AbstractLink> links, LaneGroupType lgtype){
		this.id = id;
		this.lgtype = lgtype;
		this.links = new HashSet<>(links);
	}

	public jaxb.Actuator to_jaxb(){
		jaxb.Actuator j = new jaxb.Actuator();
		j.setId(id);
		return j;
	}

	public long getId(){
		return id;
	}

}
