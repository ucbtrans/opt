package opt.data.control;

import error.OTMException;
import opt.data.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ControlFactory {

	public static ControllerRampMeterAlinea create_controller_alinea(float dt, float start_time, Float end_time, Collection<Long> link_ids, FreewayScenario fwyscn) throws OTMException {

		// CHECKS
		parameters_check(dt,start_time,end_time);

		// Create actuators
		Scenario scenario = fwyscn.get_scenario();
		Set<AbstractActuator> actuators = new HashSet<>();
		for(long link_id : link_ids){
			AbstractLink link = scenario.get_link_with_id(link_id);

			// link type
			if(!(link instanceof opt.data.LinkOnramp) && !(link instanceof opt.data.LinkConnector))
				throw new OTMException("!(link instanceof LinkOnramp) && !(link instanceof LinkConnector)");

			// link already controlled
			if(link.actuator!=null && link.actuator.myController!=null)
				throw new OTMException("link.actuator!=null && link.actuator.myController!=null");

			// create an actuator if there is none
			if(link.actuator==null){
				ActuatorRampMeter actrm = new ActuatorRampMeter(fwyscn.new_actuator_id(),link);
				link.actuator = actrm;
				scenario.add_actuator(actrm);
			}

			// otherwise replace the actuator if it is not a ramp meter
			else if(!(link.actuator instanceof opt.data.control.ActuatorRampMeter)){
				scenario.delete_actuator(link.actuator.id);
				ActuatorRampMeter actrm = new ActuatorRampMeter(fwyscn.new_actuator_id(),link);
				link.actuator = actrm;
				scenario.add_actuator(actrm);
			}

			// record the actuator
			actuators.add(link.actuator);
		}

		ControllerRampMeterAlinea ctrl = new ControllerRampMeterAlinea(fwyscn.new_controller_id(),dt,start_time,end_time,actuators);
		scenario.add_controller(ctrl);
		return ctrl;
	}

	public static ControllerRampMeterTOD create_controller_tod(float dt, float start_time, Float end_time, Collection<Long> link_ids, FreewayScenario fwyscn) throws OTMException {

		// CHECKS
		parameters_check(dt,start_time,end_time);

		// Create actuators
		Scenario scenario = fwyscn.get_scenario();
		Set<AbstractActuator> actuators = new HashSet<>();
		for(long link_id : link_ids){
			AbstractLink link = scenario.get_link_with_id(link_id);

			// link type
			if(!(link instanceof opt.data.LinkOnramp) && !(link instanceof opt.data.LinkConnector))
				throw new OTMException("!(link instanceof LinkOnramp) && !(link instanceof LinkConnector)");

			// link already controlled
			if(link.actuator!=null && link.actuator.myController!=null)
				throw new OTMException("link.actuator!=null && link.actuator.myController!=null");

			// create an actuator if there is none
			if(link.actuator==null){
				ActuatorRampMeter actrm = new ActuatorRampMeter(fwyscn.new_actuator_id(),link);
				link.actuator = actrm;
				scenario.add_actuator(actrm);
			}

			// otherwise replace the actuator if it is not a ramp meter
			else if(!(link.actuator instanceof opt.data.control.ActuatorRampMeter)){
				scenario.delete_actuator(link.actuator.id);
				ActuatorRampMeter actrm = new ActuatorRampMeter(fwyscn.new_actuator_id(),link);
				link.actuator = actrm;
				scenario.add_actuator(actrm);
			}

			// record the actuator
			actuators.add(link.actuator);
		}

		ControllerRampMeterTOD ctrl = new ControllerRampMeterTOD(fwyscn.new_controller_id(),dt,start_time,end_time,actuators);
		scenario.add_controller(ctrl);
		return ctrl;
	}

	public static ControllerPolicyHOV create_controller_hov(float dt, float start_time, Float end_time, Collection<Long> link_ids, FreewayScenario fwyscn) throws OTMException {

		// CHECKS
		parameters_check(dt,start_time,end_time);

		// Create actuators
		Scenario scenario = fwyscn.get_scenario();
		Set<AbstractActuator> actuators = new HashSet<>();
		for(long link_id : link_ids){
			AbstractLink link = scenario.get_link_with_id(link_id);

			// link type
			if(!(link instanceof opt.data.LinkFreewayOrConnector))
				throw new OTMException("!(link instanceof opt.data.LinkFreewayOrConnector)");

			// link already controlled
			if(link.actuator!=null && link.actuator.myController!=null)
				throw new OTMException("link.actuator!=null && link.actuator.myController!=null");

			// create an actuator if there is none
			if(link.actuator==null){
				ActuatorPolicy actrm = new ActuatorPolicy(fwyscn.new_actuator_id(),link);
				link.actuator = actrm;
				scenario.add_actuator(actrm);
			}

			// otherwise replace the actuator if it is not a ramp meter
			else if(!(link.actuator instanceof opt.data.control.ActuatorPolicy)){
				scenario.delete_actuator(link.actuator.id);
				ActuatorPolicy actrm = new ActuatorPolicy(fwyscn.new_actuator_id(),link);
				link.actuator = actrm;
				scenario.add_actuator(actrm);
			}

			// record the actuator
			actuators.add(link.actuator);
		}

		ControllerPolicyHOV ctrl = new ControllerPolicyHOV(fwyscn.new_controller_id(),dt,start_time,end_time,actuators);
		scenario.add_controller(ctrl);
		return ctrl;
	}

	public static ControllerPolicyHOT create_controller_hot(float dt, float start_time, Float end_time, Collection<Long> link_ids, FreewayScenario fwyscn) throws OTMException {

		// CHECKS
		parameters_check(dt,start_time,end_time);

		// Create actuators
		Scenario scenario = fwyscn.get_scenario();
		Set<AbstractActuator> actuators = new HashSet<>();
		for(long link_id : link_ids){
			AbstractLink link = scenario.get_link_with_id(link_id);

			// link type
			if(!(link instanceof opt.data.LinkFreewayOrConnector))
				throw new OTMException("!(link instanceof opt.data.LinkFreewayOrConnector)");

			// link already controlled
			if(link.actuator!=null && link.actuator.myController!=null)
				throw new OTMException("link.actuator!=null && link.actuator.myController!=null");

			// create an actuator if there is none
			if(link.actuator==null){
				ActuatorPolicy actrm = new ActuatorPolicy(fwyscn.new_actuator_id(),link);
				link.actuator = actrm;
				scenario.add_actuator(actrm);
			}

			// otherwise replace the actuator if it is not a ramp meter
			else if(!(link.actuator instanceof opt.data.control.ActuatorPolicy)){
				scenario.delete_actuator(link.actuator.id);
				ActuatorPolicy actrm = new ActuatorPolicy(fwyscn.new_actuator_id(),link);
				link.actuator = actrm;
				scenario.add_actuator(actrm);
			}

			// record the actuator
			actuators.add(link.actuator);
		}

		ControllerPolicyHOT ctrl = new ControllerPolicyHOT(fwyscn.new_controller_id(),dt,start_time,end_time,actuators);
		scenario.add_controller(ctrl);
		return ctrl;
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
