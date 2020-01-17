package opt.data.control;

import error.OTMException;
import jaxb.Controller;
import opt.data.Scenario;

import java.util.Collection;

public class ControllerRampMeterAlinea extends AbstractControllerRampMeter {

	protected long sensor_link_id;
	protected float sensor_offset_m;

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

	public long getSensor_link_id() {
		return sensor_link_id;
	}

	public void setSensor_link_id(long sensor_link_id) {
		this.sensor_link_id = sensor_link_id;
	}

	public float getSensor_offset_m() {
		return sensor_offset_m;
	}

	public void setSensor_offset_m(float sensor_offset_m) {
		this.sensor_offset_m = sensor_offset_m;
	}


}
