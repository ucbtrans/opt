package opt.data.control;

import opt.data.LaneGroupType;

public abstract class AbstractActuator {

	protected long id;
	protected long link_id;
	protected LaneGroupType lgtype;

	public AbstractActuator(long id,long link_id,LaneGroupType lgtype){
		this.id = id;
		this.lgtype = lgtype;
		this.link_id = link_id;
	}

	// TODO READ AND WRITE LANE GROUP TYPE TO XML

	public AbstractActuator(jaxb.Actuator j){
		this.id = j.getId();
		this.link_id = j.getActuatorTarget().getId();
	}

	public jaxb.Actuator to_jaxb(){
		jaxb.Actuator j = new jaxb.Actuator();
		j.setId(id);
		jaxb.ActuatorTarget target = new jaxb.ActuatorTarget();
		target.setType("link");
		target.setId(link_id);
		j.setActuatorTarget(target);

		return j;
	}

	public long getId(){
		return id;
	}
}
