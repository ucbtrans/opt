package opt.data.control;

import opt.data.FreewayScenario;

import java.util.HashSet;
import java.util.Set;

public class ControllerRampMeterAlinea extends AbstractControllerRampMeter {

	protected long sensor_id;
	protected long sensor_link_id;
	protected float sensor_offset;

	////////////////////////////////
	// construction
	////////////////////////////////

	public ControllerRampMeterAlinea(FreewayScenario scn,float dt, boolean has_queue_control,float override_threshold, float min_rate_vph, float max_rate_vph,Long sensor_id, long sensor_link_id, float sensor_offset) throws Exception {
		super(dt,control.AbstractController.Algorithm.rm_alinea,has_queue_control,override_threshold,min_rate_vph,max_rate_vph);

		// feedback sensor
		this.sensor_id = sensor_id;
		this.sensor_link_id = sensor_link_id;
		this.sensor_offset = sensor_offset;
//		Sensor sns = ControlFactory.create_sensor(scn,sensor_id,sensor_link_id,sensor_offset,this);
//		add_sensor(sns);
	}

	////////////////////////////////
	// API
	////////////////////////////////

	public long getSensor_link_id() {
		return sensor_link_id;
	}

	public void setSensor_link_id(long sensor_link_id) {
		this.sensor_link_id = sensor_link_id;
	}

	public float getSensor_offset_m() {
		return sensor_offset;
	}

	public void setSensor_offset_m(float sensor_offset_m) {
		this.sensor_offset = sensor_offset_m;
	}

	@Override
	public Set<Sensor> get_sensors() {
		Set<Sensor> X = new HashSet<>();
		X.add(new Sensor(sensor_link_id,sensor_offset));
		return X;
	}
}
