package opt.data;

import jaxb.Actuator;

public class ActuatorRampMeter extends AbstractActuator {

	AbstractLink link;

	public ActuatorRampMeter(Actuator j,Scenario scenario) {
		super(j,scenario);
		if(j.getActuatorTarget().getType().equals("link"))
			link = scenario.links.get(j.getActuatorTarget().getId());
	}
}
