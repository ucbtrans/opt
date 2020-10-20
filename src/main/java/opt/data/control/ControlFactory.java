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
				id==null ? fwyscn.new_schedule_id() : id,
				name,
				links,
				lgtype,
				cntrl_type );

		try {
			switch(cntrl_type){

				case RampMetering:
					schedule.update(0f,ControlFactory.create_controller_rmopen(fwyscn));
					break;

				case LgRestrict:
					schedule.update(0f,ControlFactory.create_controller_hovhot(fwyscn,fwyscn.get_commodities().keySet(),null,null,null,null,null,null));
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

	public static ControllerRampMeterOpen create_controller_rmopen(FreewayScenario fwyscn) throws Exception {
		return new ControllerRampMeterOpen(fwyscn);
	}

	public static ControllerRampMeterClosed create_controller_rmclosed(FreewayScenario fwyscn) throws Exception {
		return new ControllerRampMeterClosed(fwyscn);
	}

	public static ControllerRampMeterFixedRate create_controller_fixed_rate(FreewayScenario fwyscn, float dt, boolean has_queue_control,float override_threshold, float min_rate_vphpl, float max_rate_vphpl, float rate_vphpl) throws Exception {
		parameters_check(dt);
		return new ControllerRampMeterFixedRate(fwyscn,dt,has_queue_control,override_threshold,min_rate_vphpl,max_rate_vphpl,rate_vphpl);
	}

	public static ControllerRampMeterAlinea create_controller_alinea(FreewayScenario fwyscn, float dt, boolean has_queue_control,float override_threshold, float min_rate_vphpl, float max_rate_vphpl, long sensor_link_id, float sensor_offset) throws Exception {
		parameters_check(dt);
		return new ControllerRampMeterAlinea(fwyscn,dt,has_queue_control,override_threshold,min_rate_vphpl,max_rate_vphpl,sensor_link_id,sensor_offset);
	}

	public static ControllerLgRestrict create_controller_hovhot(FreewayScenario fwyscn, Set<Long> tolled_comms, Set<Long> disallowed_comms, Set<Long> free_comms, Float dt, Double toll_coef, int [][] vplph_to_cents_table, Double qos_speed_threshold_kph) throws Exception {
		return new ControllerLgRestrict(fwyscn,tolled_comms,disallowed_comms,free_comms,dt,toll_coef,vplph_to_cents_table,qos_speed_threshold_kph);
	}

	/////////////////////////
	// jaxb
	/////////////////////////

	public static ControllerRampMeterAlinea create_controller_alinea(FreewayScenario fwyscn,jaxb.Entry jentry,jaxb.Sensor jsn) throws Exception {

		// read parameters
		boolean has_queue_control = false;
		float override_threshold = Float.NaN;
		float min_rate_vphpl = Float.NaN;
		float max_rate_vphpl = Float.NaN;
		if(jentry.getParameters()!=null)
			for(jaxb.Parameter param : jentry.getParameters().getParameter()){
				switch(param.getName()){
					case "queue_control":
						has_queue_control = param.getValue().equals("true");
						break;
					case "override_threshold":
						override_threshold = Float.parseFloat(param.getValue());
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

		ControllerRampMeterAlinea cntrl = create_controller_alinea(fwyscn,
				jentry.getDt(),
				has_queue_control,
				override_threshold,
				min_rate_vphpl,
				max_rate_vphpl,
				jsn.getLinkId(),
				jsn.getPosition());

		return cntrl;
	}

	public static ControllerRampMeterFixedRate create_controller_fixed_rate(FreewayScenario fwyscn,jaxb.Entry jentry) throws Exception {

		// read parameters
		boolean has_queue_control = false;
		float override_threshold = Float.NaN;
		float min_rate_vphpl = Float.NaN;
		float max_rate_vphpl = Float.NaN;
		float rate_vphpl = Float.NaN;
		if(jentry.getParameters()!=null)
			for(jaxb.Parameter param : jentry.getParameters().getParameter()){
				switch(param.getName()){
					case "queue_control":
						has_queue_control = param.getValue().equals("true");
						break;
					case "override_threshold":
						override_threshold = Float.parseFloat(param.getValue());
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

		ControllerRampMeterFixedRate cntrl = create_controller_fixed_rate(fwyscn,
				jentry.getDt(),has_queue_control,override_threshold,min_rate_vphpl,max_rate_vphpl,rate_vphpl);

		return cntrl;

	}

	public static ControllerLgRestrict create_controller_hovhot(FreewayScenario fwyscn, jaxb.Entry jentry) throws Exception {

		// read parameters
		Set<Long> tolled_comms = new HashSet<>();
		Set<Long> disallowed_comms = new HashSet<>();
		Set<Long> free_comms = new HashSet<>();
		Double toll_coef = null;
		int [][] vplph_to_cents_table = null;
                Double qos_speed_threshold_kph = null;
		if(jentry.getParameters()!=null) {
			for (jaxb.Parameter param : jentry.getParameters().getParameter()) {
				switch (param.getName()) {
					case "tolled_comms":
						tolled_comms.addAll(OTMUtils.csv2longlist(param.getValue()));
						break;
					case "disallowed_comms":
						disallowed_comms.addAll(OTMUtils.csv2longlist(param.getValue()));
						break;
					case "free_comms":
						free_comms.addAll(OTMUtils.csv2longlist(param.getValue()));
						break;
					case "a2":
					case "toll_coef":
						toll_coef = Double.parseDouble(param.getValue());
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

		ControllerLgRestrict cntrl = create_controller_hovhot(fwyscn,tolled_comms,disallowed_comms,free_comms,dt,toll_coef,vplph_to_cents_table,qos_speed_threshold_kph);
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
