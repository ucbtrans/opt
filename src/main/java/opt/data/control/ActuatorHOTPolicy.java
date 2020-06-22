package opt.data.control;

import jaxb.Actuator;
import opt.data.LaneGroupType;

public class ActuatorHOTPolicy extends AbstractActuator {

	public ActuatorHOTPolicy(long id, long link_id,int [] lanes, LaneGroupType lgtype){
		super(id,link_id,lanes,lgtype);
	}

	public ActuatorHOTPolicy(Actuator j) {
		super(j);
	}

	@Override
	public Actuator to_jaxb() {
		jaxb.Actuator j =  super.to_jaxb();
		j.setType("hotpolicy");
		jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
		j.setActuatorTarget(jtgt);
		jtgt.setType("link");
		jtgt.setId(link_id);
		return j;
	}

}
