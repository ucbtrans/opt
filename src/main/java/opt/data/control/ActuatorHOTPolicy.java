package opt.data.control;

import jaxb.Actuator;
import opt.data.AbstractLink;
import opt.data.LaneGroupType;

public class ActuatorHOTPolicy extends AbstractActuator {

	public ActuatorHOTPolicy(long id, AbstractLink link, LaneGroupType lgtype){
		super(id,link,lgtype);
	}

	@Override
	public Actuator to_jaxb() {
		jaxb.Actuator j =  super.to_jaxb();
		j.setType("hotpolicy");
		jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
		j.setActuatorTarget(jtgt);
		jtgt.setType("link");
		jtgt.setId(link.id);
		return j;
	}

}
