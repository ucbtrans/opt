package opt.data.control;

import error.OTMException;
import opt.UserSettings;
import opt.data.AbstractLink;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;
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

	public static ControlSchedule create_empty_controller_schedule(Long id, String name, AbstractLink link, LaneGroupType lgtype, AbstractController.Type cntrl_type) throws Exception {
		Set<AbstractLink> links = new HashSet<>();
		links.add(link);
		return create_empty_controller_schedule(id,name,links,lgtype,cntrl_type);
	}

	public static ControlSchedule create_empty_controller_schedule(Long id,String name,Set<AbstractLink> links, LaneGroupType lgtype, AbstractController.Type cntrl_type) throws Exception {

		// check that
		// all links should be in the same scenario
		assert(links.stream().map(x->x.get_segment().get_scenario()).distinct().count()==1);
		FreewayScenario fwyscn = links.iterator().next().get_segment().get_scenario();

		ControlSchedule schedule = new ControlSchedule(
				id==null ? fwyscn.new_controller_id() : id,
				name,
				links,
				lgtype,
				cntrl_type );

		try {
			switch(cntrl_type){

				case RampMetering:
					schedule.update(0f,ControlFactory.create_controller_rmopen(fwyscn,null));
					break;

				case HOVHOT:
					schedule.update(0f,ControlFactory.create_controller_hovhot(fwyscn,null,null,null,null,null,null,null,null,null));
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

	public static ControllerPolicyHOVHOT create_controller_hovhot(FreewayScenario fwyscn, Long id, Set<Long> disallowed_comms,Set<Long> free_comms,Float dt, Double a0,Double a1,Double a2,int [][] vplph_to_cents_table, Double qos_speed_threshold_kph) throws Exception {
		return new ControllerPolicyHOVHOT(fwyscn,id,disallowed_comms,free_comms,dt,a0,a1,a2,vplph_to_cents_table,qos_speed_threshold_kph);
	}

	/////////////////////
	// sensor
	/////////////////////

	protected static Sensor create_sensor(FreewayScenario fwyscn,Long sensor_id, long link_id, float offset, AbstractController myController){
		return new Sensor(sensor_id!=null?sensor_id:fwyscn.new_sensor_id(),link_id,offset,myController);
	}

	/////////////////////////
	// jaxb
	/////////////////////////

	public static ControllerRampMeterAlinea create_controller_alinea(jaxb.Entry jentry,jaxb.Sensor jsn,Long id) throws Exception {

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

		ControllerRampMeterAlinea cntrl = create_controller_alinea(null,id,
				jentry.getDt(),
				has_queue_control,
				min_rate_vphpl,
				max_rate_vphpl,
				jsn.getId(),
				jsn.getLinkId(),
				jsn.getPosition());

		return cntrl;
	}

	public static ControllerRampMeterFixedRate create_controller_fixed_rate(jaxb.Entry jentry,Long id) throws Exception {

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

		ControllerRampMeterFixedRate cntrl = create_controller_fixed_rate(null,id,
				jentry.getDt(),has_queue_control,min_rate_vphpl,max_rate_vphpl,rate_vphpl);

		return cntrl;

	}

	public static ControllerPolicyHOVHOT create_controller_hovhot(FreewayScenario fwyscn,jaxb.Entry jentry,Long id) throws Exception {

		// read parameters
		Set<Long> disallowed_comms = new HashSet<>();
		Set<Long> free_comms = new HashSet<>();
		Double a0 = null;
		Double a1 = null;
		Double a2 = null;
		int [][] vplph_to_cents_table = null;
                Double qos_speed_threshold_kph = null;
		if(jentry.getParameters()!=null) {
			for (jaxb.Parameter param : jentry.getParameters().getParameter()) {
				switch (param.getName()) {
					case "disallowed_comms":
						disallowed_comms.addAll(OTMUtils.csv2longlist(param.getValue()));
						break;
					case "free_comms":
						free_comms.addAll(OTMUtils.csv2longlist(param.getValue()));
						break;
					case "a0":
						a0 = Double.parseDouble(param.getValue());
						break;
					case "a1":
						a1 = Double.parseDouble(param.getValue());
						break;
					case "a2":
						a2 = Double.parseDouble(param.getValue());
						break;
					case "vplph_to_cents_table":
						vplph_to_cents_table = OTMUtils.read_int_table(param.getValue());
						break;
					case "qos_speed_threshold_kph":
						qos_speed_threshold_kph = Double.parseDouble(param.getValue());
						break;
					default:
						throw new Exception("Unknown controller parameter");
				}
			}
		}

		Float dt = jentry.getDt()==null ? (float) UserSettings.defaultControlDtSeconds : jentry.getDt();

		ControllerPolicyHOVHOT cntrl = create_controller_hovhot(fwyscn,id,disallowed_comms,free_comms,dt,a0,a1,a2,vplph_to_cents_table,qos_speed_threshold_kph);
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
