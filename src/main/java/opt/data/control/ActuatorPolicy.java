package opt.data.control;

import jaxb.Actuator;
import opt.data.AbstractLink;

public class ActuatorPolicy extends AbstractActuator {

	public ActuatorPolicy(long id, AbstractLink link){
		super(id,link);
	}

	public ActuatorPolicy(Actuator j) {
		super(j);
	}

	@Override
	public Actuator to_jaxb() {
		jaxb.Actuator j =  super.to_jaxb();
		j.setType("policy");
		jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
		j.setActuatorTarget(jtgt);
		jtgt.setType("link");
		jtgt.setId(link.id);
		return j;
	}
}
