package opt.data;

import error.OTMException;
import opt.data.control.*;
import utils.OTMUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ControlFactory {

	public static Set<control.AbstractController.Algorithm> get_available_ramp_metering_algorithms(){
		Set<control.AbstractController.Algorithm> x = new HashSet<>();
		x.add(control.AbstractController.Algorithm.alinea);
		x.add(control.AbstractController.Algorithm.tod);
		return x;
	}

	public static ControllerRampMeterAlinea create_controller_alinea(FreewayScenario fwyscn, float dt, float start_time, Float end_time, boolean has_queue_control, float min_rate_vph, float max_rate_vph, long sensor_link_id, float sensor_offset, long ramp_link_id, LaneGroupType lgtype) throws Exception {
		parameters_check(dt,start_time,end_time);
		return new ControllerRampMeterAlinea(fwyscn,dt,start_time,end_time,has_queue_control,min_rate_vph,max_rate_vph,sensor_link_id,sensor_offset,ramp_link_id,lgtype);
	}

	public static ControllerRampMeterTOD create_controller_tod(FreewayScenario fwyscn, float dt, float start_time, Float end_time, long ramp_link_id, LaneGroupType lgtype) throws Exception {
		parameters_check(dt,start_time,end_time);
		return new ControllerRampMeterTOD(fwyscn,dt,start_time,end_time,ramp_link_id,lgtype);
	}

	public static ControllerPolicyHOV create_controller_hov(FreewayScenario fwyscn, float dt, float start_time, Float end_time) throws Exception {
		parameters_check(dt,start_time,end_time);
		ControllerPolicyHOV ctrl = new ControllerPolicyHOV(fwyscn,dt,start_time,end_time);
		return ctrl;
	}

	public static ControllerPolicyHOT create_controller_hot(FreewayScenario fwyscn, float dt, float start_time, Float end_time) throws Exception {
		parameters_check(dt,start_time,end_time);
		ControllerPolicyHOT ctrl = new ControllerPolicyHOT(fwyscn,dt,start_time,end_time);
		return ctrl;
	}

	public static Sensor create_sensor(FreewayScenario fwyscn, long link_id, float offset, AbstractController myController){
		return new Sensor(fwyscn.new_sensor_id(),link_id,offset,myController);
	}

	public static ActuatorRampMeter create_ramp_meter(FreewayScenario fwyscn,long link_id,LaneGroupType lgtype, AbstractController myController){
		return new ActuatorRampMeter(fwyscn.new_actuator_id(),link_id,lgtype,myController);
	}

	public static ActuatorPolicy create_policy_actuator(FreewayScenario fwyscn,long link_id,LaneGroupType lgtype, AbstractController myController){
		return new ActuatorPolicy(fwyscn.new_actuator_id(),link_id,lgtype,myController);
	}

	/////////////////////////
	// jaxb
	/////////////////////////

	public static ControllerRampMeterAlinea create_controller_alinea(FreewayScenario fwyscn, jaxb.Controller jcnt, Map<Long,jaxb.Actuator> actuator_pool, Map<Long,jaxb.Sensor> sensor_pool) throws Exception {

		// complex sensors : check that there are none
		if(jcnt.getFeedbackSensors()!=null)
			assert(jcnt.getFeedbackSensors().getFeedbackSensor().isEmpty());

		// complex actuators : check that there are none
		if(jcnt.getTargetActuators()!=null)
			assert(jcnt.getTargetActuators().getTargetActuator().isEmpty());

		boolean has_queue_control = false; // TODO FIX THIS
		long ramp_link_id = -1l;
		float min_rate_vph = -1f;
		float max_rate_vph = -1f;
		long sensor_link_id = -1l;
		float sensor_offset = 0f;
		LaneGroupType lgtype = LaneGroupType.gp; // TODO FIX THIS

		// read actuators
		if(jcnt.getTargetActuators()!=null){
			List<Long> act_ids = OTMUtils.csv2longlist(jcnt.getTargetActuators().getIds());
			assert(act_ids.size()==1);
			for(long act_id : act_ids) {
				jaxb.Actuator jact = actuator_pool.get(act_id);
				min_rate_vph = jact.getMinValue();
				max_rate_vph = jact.getMaxValue();
				ramp_link_id = jact.getActuatorTarget().getId();
			}
		}

		// read sensors
		if(jcnt.getFeedbackSensors()!=null){
			List<Long> sens_ids = OTMUtils.csv2longlist(jcnt.getFeedbackSensors().getIds());
			assert(sens_ids.size()==1);
			for(long sens_id :sens_ids) {
				jaxb.Sensor jsns = sensor_pool.get(sens_id);
				sensor_link_id = jsns.getLinkId();
				sensor_offset = jsns.getPosition();
			}
		}

		return create_controller_alinea(fwyscn,jcnt.getDt(),jcnt.getStartTime(),jcnt.getEndTime(),has_queue_control,min_rate_vph,max_rate_vph,sensor_link_id,sensor_offset,ramp_link_id,lgtype);
	}

	public static ControllerRampMeterTOD create_controller_tod(FreewayScenario fwyscn,jaxb.Controller jcnt, Map<Long,jaxb.Actuator> actuator_pool) throws Exception {

		long ramp_link_id = -1l;
		LaneGroupType lgtype = LaneGroupType.gp; // TODO FIX THIS

		// read actuators
		if(jcnt.getTargetActuators()!=null){
			List<Long> act_ids = OTMUtils.csv2longlist(jcnt.getTargetActuators().getIds());
			assert(act_ids.size()==1);
			for(long act_id : act_ids) {
				jaxb.Actuator jact = actuator_pool.get(act_id);
				ramp_link_id = jact.getActuatorTarget().getId();
			}
		}

		return create_controller_tod( fwyscn, jcnt.getDt(),jcnt.getStartTime(),jcnt.getEndTime(),ramp_link_id,lgtype);
	}

	public static ControllerPolicyHOV create_controller_hov(FreewayScenario fwyscn,jaxb.Controller jcnt) throws Exception {
		return create_controller_hov(fwyscn,jcnt.getDt(),jcnt.getStartTime(),jcnt.getEndTime());
	}

	public static ControllerPolicyHOT create_controller_hot(FreewayScenario fwyscn,jaxb.Controller jcnt) throws Exception {
		return create_controller_hot(fwyscn,jcnt.getDt(),jcnt.getStartTime(),jcnt.getEndTime());
	}

	/////////////////////////
	// private
	/////////////////////////

	private static void parameters_check(float dt,float start_time,Float end_time) throws OTMException {
		if(dt<=0f)
			throw new OTMException("dt<=0f");
		if(start_time<0)
			throw new OTMException("start_time<0");
		if(end_time!=null && end_time<=start_time)
			throw new OTMException("end_time!=null && end_time<=start_time");
	}

}
