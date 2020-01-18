package opt.data.control;

import jaxb.Actuator;

public class ActuatorRampMeter extends AbstractActuator {


	public ActuatorRampMeter(long id, long link_id,AbstractController myController ){
		super(id,link_id,myController);
	}

	public ActuatorRampMeter(Actuator j) {
		super(j);
	}

	@Override
	public Actuator to_jaxb() {
		jaxb.Actuator j =  super.to_jaxb();
		j.setType("capacity");
		jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
		j.setActuatorTarget(jtgt);
		jtgt.setType("link");
		jtgt.setId(link_id);
		return j;
	}

}
