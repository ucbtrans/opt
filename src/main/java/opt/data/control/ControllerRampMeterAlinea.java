package opt.data.control;

import opt.data.ControlFactory;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;

public class ControllerRampMeterAlinea extends AbstractControllerRampMeter {

	////////////////////////////////
	// construction
	////////////////////////////////

	public ControllerRampMeterAlinea(FreewayScenario scn,Long id, float dt, boolean has_queue_control, float min_rate_vph, float max_rate_vph,Long sensor_id, long sensor_link_id, float sensor_offset,Long act_id, long ramp_link_id, LaneGroupType lgtype) throws Exception {
		super(id!=null ? id : scn.new_controller_id(),
				dt,control.AbstractController.Algorithm.alinea,has_queue_control,min_rate_vph,max_rate_vph);

		// feedback sensor
		Sensor sns = ControlFactory.create_sensor(scn,sensor_id,sensor_link_id,sensor_offset,this);
		add_sensor(sns);

		// ramp meter actuator
		ActuatorRampMeter rm = ControlFactory.create_ramp_meter(scn,act_id,ramp_link_id,lgtype,this);
		add_actuator(rm);
	}

	////////////////////////////////
	// API
	////////////////////////////////

	public long getSensor_link_id() {
		Sensor sensor = sensors.values().iterator().next();
		return sensor.link_id;
	}

	public void setSensor_link_id(long sensor_link_id) {
		Sensor sensor = sensors.values().iterator().next();
		sensor.link_id = sensor_link_id;
	}

	public float getSensor_offset_m() {
		Sensor sensor = sensors.values().iterator().next();
		return sensor.offset;
	}

	public void setSensor_offset_m(float sensor_offset_m) {
		Sensor sensor = sensors.values().iterator().next();
		sensor.offset = sensor_offset_m;
	}

}
