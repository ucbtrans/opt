package opt.data.control;

import jaxb.Actuator;
import opt.data.Scenario;

public class ActuatorRampMeter extends AbstractActuator {

	public ActuatorRampMeter(Actuator j, Scenario scenario) {
		super(j, scenario);
	}

	@Override
	public Actuator to_jaxb() {
		jaxb.Actuator j =  super.to_jaxb();
		j.setType("ramp_meter");
		jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
		j.setActuatorTarget(jtgt);
		jtgt.setId(link.id);
		return j;
	}

}
