package opt.data;

import error.OTMException;
import opt.data.control.*;
import opt.utils.BijectiveMap;
import utils.OTMUtils;

import java.util.*;

public class ControlFactory {

	public static BijectiveMap<control.AbstractController.Algorithm,String> cntrl_alg_name;

	static{
		cntrl_alg_name = new BijectiveMap<>();
		cntrl_alg_name.put(control.AbstractController.Algorithm.rm_alinea,"Alinea");
		cntrl_alg_name.put(control.AbstractController.Algorithm.rm_fixed_rate,"Fixed rate");
		cntrl_alg_name.put(control.AbstractController.Algorithm.rm_open,"Open");
		cntrl_alg_name.put(control.AbstractController.Algorithm.rm_closed,"Closed");
	}

	public static List<control.AbstractController.Algorithm> get_available_ramp_metering_algorithms(){
		List<control.AbstractController.Algorithm> x = new ArrayList<>();
		x.add(control.AbstractController.Algorithm.rm_alinea);
		x.add(control.AbstractController.Algorithm.rm_fixed_rate);
		x.add(control.AbstractController.Algorithm.rm_open);
		x.add(control.AbstractController.Algorithm.rm_closed);
		return x;
	}

	public static List<String> get_available_ramp_metering_names(){
		List<String> x = new ArrayList<>();
		x.add(cntrl_alg_name.AtoB(control.AbstractController.Algorithm.rm_alinea));
		x.add(cntrl_alg_name.AtoB(control.AbstractController.Algorithm.rm_fixed_rate));
		x.add(cntrl_alg_name.AtoB(control.AbstractController.Algorithm.rm_open));
		x.add(cntrl_alg_name.AtoB(control.AbstractController.Algorithm.rm_closed));
		return x;
	}

	/////////////////////
	// schedule
	/////////////////////

	public static ControlSchedule create_empty_controller_schedule(Long id,AbstractLink link, LaneGroupType lgtype, AbstractController.Type cntrl_type) {
		Set<AbstractLink> links = new HashSet<>();
		links.add(link);
		return create_empty_controller_schedule(id,links,lgtype,cntrl_type);
	}

	public static ControlSchedule create_empty_controller_schedule(Long id,Set<AbstractLink> links, LaneGroupType lgtype, AbstractController.Type cntrl_type) {

		// all links should be in the same scenario
		assert(links.stream().map(x->x.mysegment.my_fwy_scenario).distinct().count()==1);
		FreewayScenario fwyscn = links.iterator().next().mysegment.my_fwy_scenario;

		ControlSchedule schedule = new ControlSchedule(
				id==null ? fwyscn.new_controller_id() : id,
				links,
				lgtype,
				cntrl_type,
				fwyscn.new_actuator_id()
				);

		try {
			switch(cntrl_type){

				case RampMetering:
					schedule.update(0f,ControlFactory.create_controller_rmopen(fwyscn,null));
					break;

				case HOVHOT:
					schedule.update(0f,ControlFactory.create_controller_hovhot(fwyscn,null,null));
					break;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return schedule;
	}

	/////////////////////
	// controller
	/////////////////////

	public static ControllerRampMeterOpen create_controller_rmopen(FreewayScenario fwyscn, Long id) throws Exception {
		return new ControllerRampMeterOpen(fwyscn,id);
	}

	public static ControllerRampMeterClosed create_controller_rmclosed(FreewayScenario fwyscn, Long id) throws Exception {
		return new ControllerRampMeterClosed(fwyscn,id);
	}

	public static ControllerRampMeterFixedRate create_controller_fixed_rate(FreewayScenario fwyscn, Long id, float dt, boolean has_queue_control, float min_rate_vphpl, float max_rate_vphpl, float rate_vphpl) throws Exception {
		parameters_check(dt);
		return new ControllerRampMeterFixedRate(fwyscn,id,dt,has_queue_control,min_rate_vphpl,max_rate_vphpl,rate_vphpl);
	}

	public static ControllerRampMeterAlinea create_controller_alinea(FreewayScenario fwyscn,Long id, float dt, boolean has_queue_control, float min_rate_vphpl, float max_rate_vphpl,Long sensor_id, long sensor_link_id, float sensor_offset) throws Exception {
		parameters_check(dt);
		return new ControllerRampMeterAlinea(fwyscn,id,dt,has_queue_control,min_rate_vphpl,max_rate_vphpl,sensor_id,sensor_link_id,sensor_offset);
	}

	public static ControllerPolicyHOVHOT create_controller_hovhot(FreewayScenario fwyscn, Long id, Set<Long> disallowed_comms) throws Exception {
		return new ControllerPolicyHOVHOT(fwyscn,id,disallowed_comms);
	}

	/////////////////////
	// sensor
	/////////////////////

	public static Sensor create_sensor(FreewayScenario fwyscn,Long sensor_id, long link_id, float offset, AbstractController myController){
		return new Sensor(sensor_id!=null?sensor_id:fwyscn.new_sensor_id(),link_id,offset,myController);
	}

	/////////////////////
	// actuator
	/////////////////////

	public static ActuatorRampMeter create_actuator_ramp_meter(long id,AbstractLink link, LaneGroupType lgtype){
		return new ActuatorRampMeter(id, link, lgtype);
	}

	public static ActuatorHOVHOT create_actuator_hov_policy(long id, Collection<AbstractLink> links, LaneGroupType lgtype){
		return new ActuatorHOVHOT(id, links, lgtype);
	}

	public static ActuatorHOTPolicy create_actuator_hot_policy(long id,Set<AbstractLink> links, LaneGroupType lgtype){
		return new ActuatorHOTPolicy(id, links, lgtype);
	}

	/////////////////////////
	// jaxb
	/////////////////////////

	public static ControllerRampMeterAlinea create_controller_alinea(jaxb.Entry jentry,jaxb.Sensor jsn) throws Exception {

		// read parameters
		boolean has_queue_control = false;
		float min_rate_vphpl = Float.NaN;
		float max_rate_vphpl = Float.NaN;
		if(jentry.getParameters()!=null)
			for(jaxb.Parameter param : jentry.getParameters().getParameter()){
				switch(param.getName()){
					case "queue_control":
						has_queue_control = param.getValue().equals("true");
						break;
					case "min_rate_vphpl":
						min_rate_vphpl = Float.parseFloat(param.getValue());
						break;
					case "max_rate_vphpl":
						max_rate_vphpl = Float.parseFloat(param.getValue());
						break;
					default:
						throw new Exception("Unknown controller parameter");
				}
			}

		ControllerRampMeterAlinea cntrl = create_controller_alinea(null,0l,
				jentry.getDt(),
				has_queue_control,
				min_rate_vphpl,
				max_rate_vphpl,
				jsn.getId(),
				jsn.getLinkId(),
				jsn.getPosition());

		return cntrl;
	}

//		public static ControllerRampMeterAlinea create_controller_alinea(FreewayScenario fwyscn, jaxb.Controller jcnt, Map<Long,jaxb.Actuator> actuator_pool, Map<Long,jaxb.Sensor> sensor_pool) throws Exception {
//
//		// complex sensors : check that there are none
//		if(jcnt.getFeedbackSensors()!=null)
//			assert(jcnt.getFeedbackSensors().getFeedbackSensor().isEmpty());
//
//		// complex actuators : check that there are none
//		if(jcnt.getTargetActuators()!=null)
//			assert(jcnt.getTargetActuators().getTargetActuator().isEmpty());
//
//		// read parameters
//		boolean has_queue_control = false;
//		LaneGroupType lgtype = LaneGroupType.gp;
//		if(jcnt.getParameters()!=null)
//			for(jaxb.Parameter param : jcnt.getParameters().getParameter()){
//				switch(param.getName()){
//					case "queue_control":
//						has_queue_control = param.getValue().equals("true");
//						break;
//					case "lane_group":
//						lgtype = LaneGroupType.valueOf(param.getValue());
//						break;
//					default:
//						throw new Exception("Unknown controller parameter");
//				}
//			}
//
//		float min_rate_vph = -1f;
//		float max_rate_vph = -1f;
//		List<Long> act_ids = OTMUtils.csv2longlist(jcnt.getTargetActuators().getIds());
//		assert(act_ids.size()==1);
//		for(long a : act_ids) {
//			jaxb.Actuator jact = actuator_pool.get(a);
//			min_rate_vph = jact.getMinValue();
//			max_rate_vph = jact.getMaxValue();
//		}
//
//		// read sensors
//		long sensor_id = -1l;
//		long sensor_link_id = -1l;
//		float sensor_offset = -1f;
//		List<Long> sens_ids = OTMUtils.csv2longlist(jcnt.getFeedbackSensors().getIds());
//		assert(sens_ids.size()==1);
//		for(long sens_id :sens_ids) {
//			jaxb.Sensor jsns = sensor_pool.get(sens_id);
//			sensor_id = jsns.getId();
//			sensor_link_id = jsns.getLinkId();
//			sensor_offset = jsns.getPosition();
//		}
//
//		ControllerRampMeterAlinea cntrl = create_controller_alinea(fwyscn,jcnt.getId(),jcnt.getDt(),has_queue_control,min_rate_vph,max_rate_vph,sensor_id,sensor_link_id,sensor_offset);
//		cntrl.setId( jcnt.getId() );
//		return cntrl;
//	}

	public static ControllerRampMeterFixedRate create_controller_fixed_rate(jaxb.Entry jentry) throws Exception {

		// read parameters
		boolean has_queue_control = false;
		float min_rate_vphpl = Float.NaN;
		float max_rate_vphpl = Float.NaN;
		float rate_vphpl = Float.NaN;
		if(jentry.getParameters()!=null)
			for(jaxb.Parameter param : jentry.getParameters().getParameter()){
				switch(param.getName()){
					case "queue_control":
						has_queue_control = param.getValue().equals("true");
						break;
					case "min_rate_vphpl":
						min_rate_vphpl = Float.parseFloat(param.getValue());
						break;
					case "max_rate_vphpl":
						max_rate_vphpl = Float.parseFloat(param.getValue());
						break;
					case "rate_vphpl":
						rate_vphpl = Float.parseFloat(param.getValue());
						break;
					default:
						throw new Exception("Unknown controller parameter");
				}
			}

		ControllerRampMeterFixedRate cntrl = create_controller_fixed_rate(null,0l,
				jentry.getDt(),has_queue_control,min_rate_vphpl,max_rate_vphpl,rate_vphpl);

		return cntrl;

	}

//	public static ControllerRampMeterFixedRate create_controller_fixed_rate(FreewayScenario fwyscn, jaxb.Controller jcnt, Map<Long,jaxb.Actuator> actuator_pool) throws Exception {
//
//		// read parameters
//		float rate_vphpl = 0f;
//		boolean has_queue_control = false;
//		LaneGroupType lgtype = LaneGroupType.gp;
//		if(jcnt.getParameters()!=null)
//			for(jaxb.Parameter param : jcnt.getParameters().getParameter()){
//				switch(param.getName()){
//					case "queue_control":
//						has_queue_control = param.getValue().equals("true");
//						break;
//					case "lane_group":
//						lgtype = LaneGroupType.valueOf(param.getValue());
//						break;
//					case "rate_vphpl":
//						rate_vphpl = Float.parseFloat(param.getValue());
//						break;
//					default:
//						throw new Exception("Unknown controller parameter");
//				}
//			}
//
//		// read actuators
//		float min_rate_vph = -1f;
//		float max_rate_vph = -1f;
//		List<Long> act_ids = OTMUtils.csv2longlist(jcnt.getTargetActuators().getIds());
//		assert(act_ids.size()==1);
//		for(long a : act_ids) {
//			jaxb.Actuator jact = actuator_pool.get(a);
//			min_rate_vph = jact.getMinValue();
//			max_rate_vph = jact.getMaxValue();
//		}
//
//		ControllerRampMeterFixedRate cntrl = create_controller_fixed_rate(fwyscn,jcnt.getId(),jcnt.getDt(),has_queue_control,min_rate_vph,max_rate_vph,rate_vphpl);
//		cntrl.setId( jcnt.getId() );
//		return cntrl;
//
//	}

	public static ControllerPolicyHOVHOT create_controller_hovhot(jaxb.Entry jentry) throws Exception {

		// read parameters
		Set<Long> disallowed_comms = new HashSet<>();
		if(jentry.getParameters()!=null)
			for(jaxb.Parameter param : jentry.getParameters().getParameter()){
				switch(param.getName()){
					case "disallowed_comms":
						disallowed_comms.addAll(OTMUtils.csv2longlist(param.getValue()));
						break;
					default:
						throw new Exception("Unknown controller parameter");
				}
			}

		ControllerPolicyHOVHOT cntrl = create_controller_hovhot(null,0l,disallowed_comms);
		return cntrl;
	}

	/////////////////////////
	// private
	/////////////////////////

	private static void parameters_check(float dt) throws OTMException {
		if(dt<=0f)
			throw new OTMException("dt<=0f");
	}

}
