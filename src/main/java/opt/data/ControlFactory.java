package opt.data;

import error.OTMException;
import opt.data.control.*;

public class ControlFactory {

	public static ControllerRampMeterAlinea create_controller_alinea(FreewayScenario fwyscn, float dt, float start_time, Float end_time, boolean has_queue_control, float min_rate_vph, float max_rate_vph, long sensor_link_id, float sensor_offset, long ramp_link_id, LaneGroupType lgtype) throws Exception {
		parameters_check(dt,start_time,end_time);

		// if the ramp already has an actuator it is because it already has a controller. Throw an error
		AbstractLink ramp = fwyscn.get_scenario().get_link_with_id(ramp_link_id);
		if(ramp==null)
			throw new Exception("Bad link id.");

		if(ramp.actuator.get(lgtype)!=null)
			throw new Exception("The lane group is already controlled. Please remove the controller first.");

		ControllerRampMeterAlinea ctrl = new ControllerRampMeterAlinea(fwyscn,dt,start_time,end_time,has_queue_control,min_rate_vph,max_rate_vph,sensor_link_id,sensor_offset,ramp_link_id,lgtype);
		ramp.actuator.put(lgtype,ctrl.get_actuators().values().iterator().next());
		fwyscn.controller_schedule.add_item(ctrl);
		return ctrl;
	}

	public static ControllerRampMeterTOD create_controller_tod(FreewayScenario fwyscn, float dt, float start_time, Float end_time, long ramp_link_id, LaneGroupType lgtype) throws Exception {
		parameters_check(dt,start_time,end_time);

		// if the ramp already has an actuator it is because it already has a controller. Throw an error
		AbstractLink ramp = fwyscn.get_scenario().get_link_with_id(ramp_link_id);
		if(ramp==null)
			throw new Exception("Bad link id.");
		if(ramp.actuator.get(lgtype)!=null)
			throw new Exception("The ramp is already controlled. Please remove the controller first.");

		ControllerRampMeterTOD ctrl = new ControllerRampMeterTOD(fwyscn,dt,start_time,end_time,ramp_link_id,lgtype);
		ramp.actuator.put(lgtype,ctrl.get_actuators().values().iterator().next());
		fwyscn.controller_schedule.add_item(ctrl);
		return ctrl;
	}

	public static ControllerPolicyHOV create_controller_hov(FreewayScenario fwyscn, float dt, float start_time, Float end_time) throws Exception {
		parameters_check(dt,start_time,end_time);
		ControllerPolicyHOV ctrl = new ControllerPolicyHOV(fwyscn,dt,start_time,end_time);
		fwyscn.controller_schedule.add_item(ctrl);
		return ctrl;
	}

	public static ControllerPolicyHOT create_controller_hot(FreewayScenario fwyscn, float dt, float start_time, Float end_time) throws Exception {
		parameters_check(dt,start_time,end_time);
		ControllerPolicyHOT ctrl = new ControllerPolicyHOT(fwyscn,dt,start_time,end_time);
		fwyscn.controller_schedule.add_item(ctrl);
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

	private static void parameters_check(float dt,float start_time,Float end_time) throws OTMException {
		if(dt<=0f)
			throw new OTMException("dt<=0f");
		if(start_time<0)
			throw new OTMException("start_time<0");
		if(end_time!=null && end_time<=start_time)
			throw new OTMException("end_time!=null && end_time<=start_time");
	}

}
