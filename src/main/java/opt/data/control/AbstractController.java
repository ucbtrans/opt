package opt.data.control;

import error.OTMException;
import opt.data.LaneGroupType;
import opt.data.Scenario;
import utils.OTMUtils;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public abstract class AbstractController implements Comparable {

	protected long id;
	protected float dt;
	protected float start_time;
	protected float end_time;
	protected control.AbstractController.Algorithm algorithm;
	protected Map<Long,AbstractActuator> actuators;
	protected Map<Long,Sensor> sensors;

	// jaxb
	public AbstractController(jaxb.Controller jcntrl,Map<Long,jaxb.Actuator> jactuators, Map<Long,jaxb.Sensor> jsensors,  Scenario scn) throws Exception {

		this.id = jcntrl.getId();
		this.dt = jcntrl.getDt();
		this.start_time = jcntrl.getStartTime();
		this.end_time = jcntrl.getEndTime();
		this.algorithm = control.AbstractController.Algorithm.valueOf(jcntrl.getType());
		this.actuators = new HashMap<>();
		this.sensors = new HashMap<>();

		// read actuators
		if(jcntrl.getTargetActuators()!=null){

			// plain actuators
			for(long act_id : OTMUtils.csv2longlist(jcntrl.getTargetActuators().getIds())) {
				jaxb.Actuator jact = jactuators.get(act_id);
				AbstractActuator act = null;
				switch(jact.getType()){
					case "capacity":
						act = new opt.data.control.ActuatorRampMeter(jact);
						break;
					case "policy":
						act = new opt.data.control.ActuatorPolicy(jact);
						break;
					default:
						throw new Exception("Wrong actuator type, actuator id=" + jact.getId());
				}
				actuators.put(act_id, act);
				act.myController = this;
			}

			// complex actuators : check that there are none
			assert(!jcntrl.getTargetActuators().getTargetActuator().isEmpty());
		}

		// read sensors
		if(jcntrl.getFeedbackSensors()!=null){

			// plain sensors
			for(long sens_id : OTMUtils.csv2longlist(jcntrl.getFeedbackSensors().getIds())) {
				jaxb.Sensor jsns = jsensors.get(sens_id);
				Sensor sensor = new Sensor(jsns);
				sensors.put(sens_id, sensor);
				sensor.myController = this;
			}

			// complex sensors : check that there are none
			assert(!jcntrl.getFeedbackSensors().getFeedbackSensor().isEmpty());
		}
	}

	// factory
	public AbstractController(long id, float dt, float start_time, Float end_time, control.AbstractController.Algorithm algorithm) throws Exception {

		// CHECKS
		if(start_time<0)
			throw new OTMException("start_time<0");

		if(end_time!=null && end_time<=start_time)
			throw new OTMException("end_time<=start_time");

		this.id = id;
		this.dt = dt;
		this.start_time = start_time;
		this.end_time = end_time==null ? Float.POSITIVE_INFINITY : end_time;
		this.algorithm = algorithm;

	}

	protected void add_sensor(Sensor sensor){
		if(sensors==null)
			sensors = new HashMap<>();
		sensors.put(sensor.id,sensor);
	}

	protected void add_actuator(AbstractActuator actuator){
		if(actuators==null)
			actuators = new HashMap<>();
		actuators.put(actuator.id,actuator);
	}

	public jaxb.Controller to_jaxb(){
		jaxb.Controller j = new jaxb.Controller();
		j.setId(id);
		j.setDt(dt);
		j.setStartTime(start_time);
		j.setEndTime(end_time);
		j.setType(algorithm.toString());

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

	@Override
	public int compareTo(Object o) {
		AbstractController that = (AbstractController) o;
		if(this.start_time<that.start_time)
			return -1;
		else if(this.start_time>that.start_time)
			return 1;
		else return 0;
	}

	////////////////////////////////
	// API
	////////////////////////////////

	public Set<LaneGroupType> get_lanegroup_types(){
		return actuators.values().stream().map(a->a.lgtype).collect(toSet());
	}

	public Set<Long> get_link_ids(){
		return actuators.values().stream().map(a->a.link_id).collect(toSet());
	}

	public Set<Long>get_actuator_ids(){
		return actuators.keySet();
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

	public float getStartTime() {
		return start_time;
	}

	public void setStartTime(float start_time) {
		this.start_time = start_time;
	}

	public float getEndTime() {
		return end_time;
	}

	public void setEndTime(float end_time) {
		this.end_time = end_time;
	}

	public String getAlgorithm() {
		return algorithm.toString();
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = control.AbstractController.Algorithm.valueOf(algorithm);
	}

	public Map<Long,AbstractActuator> get_actuators(){
		return actuators;
	}

	public Map<Long,Sensor> get_sensors(){
		return sensors;
	}

}
