package opt.data.control;

import error.OTMException;
import jaxb.Controller;
import opt.data.Scenario;

import java.util.Collection;

public class ControllerRampMeterAlinea extends AbstractController {

	public ControllerRampMeterAlinea(long id,float dt, float start_time, Float end_time, Collection<AbstractActuator> xactuators) throws OTMException {
		super(id,dt,start_time,end_time,"alinea",xactuators);

		// CHECK
		if(actuators.values().stream().anyMatch(act -> !(act instanceof opt.data.control.ActuatorRampMeter)))
			throw new OTMException("Found an Alinea controller on a non-ramp meter actuator");
	}

	public ControllerRampMeterAlinea(Controller j, Scenario scn) throws OTMException {
		super(j, scn);

		// CHECK
		if(actuators.values().stream().anyMatch(act -> !(act instanceof opt.data.control.ActuatorRampMeter)))
			throw new OTMException("Found an Alinea controller on a non-ramp meter actuator");
	}

}
