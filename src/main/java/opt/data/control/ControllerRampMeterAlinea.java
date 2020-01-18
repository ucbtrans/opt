package opt.data.control;

import opt.data.Scenario;

import java.util.Map;

public class ControllerRampMeterAlinea extends AbstractControllerRampMeter {

	protected Sensor sensor;

	// from jaxb
	public ControllerRampMeterAlinea(jaxb.Controller j, Map<Long,jaxb.Actuator> a, Map<Long,jaxb.Sensor> s, Scenario scn) throws Exception {
		super(j,a,s,scn);

//		// CHECK
//		if(actuators.values().stream().anyMatch(act -> !(act instanceof opt.data.control.ActuatorRampMeter)))
//			throw new OTMException("Found an Alinea controller on a non-ramp meter actuator");
	}

	// from factory
	public ControllerRampMeterAlinea(long id,float dt, float start_time, Float end_time) throws Exception {
		super(id,dt,start_time,end_time,"alinea");

		// CHECK
//		if(actuators.values().stream().anyMatch(act -> !(act instanceof opt.data.control.ActuatorRampMeter)))
//			throw new OTMException("Found an Alinea controller on a non-ramp meter actuator");
	}

	////////////////////////////////
	// API
	////////////////////////////////

	public long getSensor_link_id() {
		return sensor.link_id;
	}

	public void setSensor_link_id(long sensor_link_id) {
		this.sensor.link_id = sensor_link_id;
	}

	public float getSensor_offset_m() {
		return this.sensor.offset;
	}

	public void setSensor_offset_m(float sensor_offset_m) {
		this.sensor.offset = sensor_offset_m;
	}

}
