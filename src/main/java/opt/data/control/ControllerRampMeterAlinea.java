package opt.data.control;

import error.OTMException;
import jaxb.Controller;
import opt.data.Scenario;

public class ControllerRampMeterAlinea extends AbstractController {

	public ControllerRampMeterAlinea(Controller j, Scenario scn) throws OTMException {
		super(j, scn);

		// CHECK
		if(actuators.values().stream().anyMatch(act -> !(act instanceof ActuatorRampMeter)))
			throw new OTMException("Found an Alinea controller on a non-ramp meter actuator");

	}

}
