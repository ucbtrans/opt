package opt.data.control;

import error.OTMException;
import jaxb.Controller;
import opt.data.Scenario;
import opt.data.control.AbstractController;

import java.util.Collection;

public class ControllerPolicyHOT extends AbstractController {

	public ControllerPolicyHOT(long id,float dt, float start_time, Float end_time, Collection<AbstractActuator> xactuators) throws OTMException {
		super(id,dt,start_time,end_time,"hot",xactuators);

		// CHECK
		if(actuators.values().stream().anyMatch(act -> !(act instanceof opt.data.control.ActuatorPolicy)))
			throw new OTMException("Found an HOT controller on a non-policy actuator");

	}

	public ControllerPolicyHOT(Controller j, Scenario scn) throws OTMException {
		super(j, scn);

		// CHECK
		if(actuators.values().stream().anyMatch(act -> !(act instanceof opt.data.control.ActuatorPolicy)))
			throw new OTMException("Found an HOT controller on a non-policy actuator");
	}
}
