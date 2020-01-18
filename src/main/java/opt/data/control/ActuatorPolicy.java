package opt.data.control;

import jaxb.Actuator;

public class ActuatorPolicy extends AbstractActuator {

	public ActuatorPolicy(long id, long link_id,AbstractController myController){
		super(id,link_id,myController);
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
		jtgt.setId(link_id);
		return j;
	}

}
