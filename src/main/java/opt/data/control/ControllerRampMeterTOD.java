package opt.data.control;

import actuator.ActuatorCapacity;
import error.OTMException;
import jaxb.Controller;
import opt.data.Scenario;

import java.util.List;

public class ControllerRampMeterTOD extends AbstractController {

	public List<TODEntry> entries;

	public ControllerRampMeterTOD(Controller j, Scenario scn) throws OTMException {
		super(j, scn);

		// TODO READ ENTRIES

		// CHECK
		for(TODEntry entry : entries){
			if(entry.time<0)
				throw new OTMException("entry.time<0");
			if(entry.rate_vph<0)
				throw new OTMException("entry.rate_vph<0");
		}

		if(actuators.values().stream().anyMatch(act -> !(act instanceof ActuatorRampMeter)))
			throw new OTMException("Found a TOD controller on a non-ramp meter actuator");

	}

	class TODEntry {
		public float time;
		public float rate_vph;
	}

}
