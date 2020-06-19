package opt.data.control;

import opt.data.ControlFactory;
import opt.data.LaneGroupType;
import utils.OTMUtils;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public abstract class AbstractController {

	public enum Type {RampMetering,HOVpolicy,HOTpolicy}

	protected long id;
	protected Type type;
	protected float dt;
	protected control.AbstractController.Algorithm algorithm;
	protected Map<Long,AbstractActuator> actuators;
	protected Map<Long,Sensor> sensors;

	////////////////////////////////
	// construction
	////////////////////////////////

	public AbstractController(long id, Type type, float dt, control.AbstractController.Algorithm algorithm) throws Exception {
		this.id = id;
		this.type = type;
		this.dt = dt;
		this.algorithm = algorithm;
		this.actuators = new HashMap<>();
		this.sensors = new HashMap<>();
	}

	protected void add_sensor(Sensor sensor){
		sensors.put(sensor.id,sensor);
	}

	protected void add_actuator(AbstractActuator actuator){
		actuators.put(actuator.id,actuator);
	}

	////////////////////////////////
	// to jaxb
	////////////////////////////////

	public jaxb.Controller to_jaxb(){
		jaxb.Controller j = new jaxb.Controller();
		j.setId(id);
		j.setDt(dt);
//		j.setStartTime(start_time);
//		j.setEndTime(end_time);
		j.setType(algorithm.toString());
		j.setParameters(new jaxb.Parameters());

		// TODO THIS IS TEMPORARY FOR STORING THE ACTUATOR LANE GROUP AS A PARAMETER OF THE CONTROLLER
		if(actuators!=null && actuators.size()==1){
			AbstractActuator act = actuators.values().iterator().next();

			jaxb.Parameter param = new jaxb.Parameter();
			param.setName("lane_group");
			param.setValue(act.lgtype.toString());
			j.getParameters().getParameter().add(param);
		}

		if(actuators!=null && !actuators.isEmpty()){
			jaxb.TargetActuators tgtacts = new jaxb.TargetActuators();
			j.setTargetActuators(tgtacts);
			tgtacts.setIds(OTMUtils.comma_format(actuators.values().stream().map(x->x.id).collect(toSet())));
		}

		if(sensors!=null && !sensors.isEmpty()){
			jaxb.FeedbackSensors fbsensors = new jaxb.FeedbackSensors();
			j.setFeedbackSensors(fbsensors);
			fbsensors.setIds(OTMUtils.comma_format(sensors.values().stream().map(x->x.id).collect(toSet())));
		}

		return j;
	}

	////////////////////////////////
	// public final
	////////////////////////////////

	public final String getName(){
		return ControlFactory.cntrl_alg_name.AtoB(algorithm);
	}

	public final control.AbstractController.Algorithm getAlgorithm(){
		return algorithm;
	}

	////////////////////////////////
	// Comparable
	////////////////////////////////

//	@Override
//	public int compareTo(Object o) {
//		AbstractController that = (AbstractController) o;
//		if(this.start_time<that.start_time)
//			return -1;
//		else if(this.start_time>that.start_time)
//			return 1;
//		else return 0;
//	}

	////////////////////////////////
	// API
	////////////////////////////////

	public Set<LaneGroupType> get_lanegroup_types(){
		return new HashSet<>(actuators.values().stream().map(a->a.lgtype).collect(toSet()));
	}

	public Set<Long> get_link_ids(){
		return new HashSet<>(actuators.values().stream().map(a->a.link_id).collect(toSet()));
	}

	public Set<Long>get_actuator_ids(){
		return new HashSet<>(actuators.keySet());
	}

	public Set<Long>get_sensor_ids(){
		return sensors.keySet();
	}

	public long getId() {
		return id;
	}

	public float getDt() {
		return dt;
	}

	public void setDt(float dt) {
		this.dt = dt;
	}

	public Map<Long,AbstractActuator> get_actuators(){
		return actuators;
	}

	public Map<Long,Sensor> get_sensors(){
		return sensors;
	}

	public void setId(long id){
		this.id = id;
	}

}
