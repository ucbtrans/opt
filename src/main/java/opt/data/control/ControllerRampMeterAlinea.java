package opt.data.control;

import opt.data.FreewayScenario;

public class ControllerRampMeterAlinea extends AbstractControllerRampMeter {

	////////////////////////////////
	// construction
	////////////////////////////////

	public ControllerRampMeterAlinea(FreewayScenario scn,float dt, boolean has_queue_control, float min_rate_vph, float max_rate_vph,Long sensor_id, long sensor_link_id, float sensor_offset) throws Exception {
		super(dt,control.AbstractController.Algorithm.rm_alinea,has_queue_control,min_rate_vph,max_rate_vph);

		// feedback sensor
		Sensor sns = ControlFactory.create_sensor(scn,sensor_id,sensor_link_id,sensor_offset,this);
		add_sensor(sns);
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
