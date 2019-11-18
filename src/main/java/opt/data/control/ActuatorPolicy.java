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

}
