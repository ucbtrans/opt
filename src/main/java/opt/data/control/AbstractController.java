package opt.data.control;

import error.OTMException;
import opt.data.FreewayScenario;
import opt.data.Scenario;
import utils.OTMUtils;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public abstract class AbstractController implements Comparable {

	// TODO Use the one in otm-sim instead
	public enum Algorithm {
		tod,
		alinea,
		hov,
		hot
	}

	public long id;
	public float dt;
	public float start_time;
	public float end_time;
	public Algorithm algorithm;
	public Map<Long,AbstractActuator> actuators;

	public AbstractController(long id, float dt, float start_time, Float end_time, String algorithm, Collection<AbstractActuator> actuators) throws OTMException {

		// CHECKS
		if(start_time<0)
			throw new OTMException("start_time<0");

		if(end_time!=null && end_time<=start_time)
			throw new OTMException("end_time<=start_time");

		if(actuators.stream().anyMatch(x->x==null))
			throw new OTMException("Bad id in actuator list for controller id="+id);

		this.id = id;
		this.dt = dt;
		this.start_time = start_time;
		this.end_time = end_time==null ? Float.POSITIVE_INFINITY : end_time;
		try {
			this.algorithm = Algorithm.valueOf(algorithm);
		} catch(IllegalArgumentException e){
			throw new OTMException(e.getMessage());
		}
		this.actuators = new HashMap<>();
		for(AbstractActuator act :actuators)
			this.actuators.put(act.id,act);

	}

	public AbstractController(jaxb.Controller j, Scenario scn) throws OTMException {
		this(j.getId(),j.getDt(),j.getStartTime(),j.getEndTime(),j.getType(),
				OTMUtils.csv2longlist(j.getTargetActuators().getIds()).stream().map(act->scn.get_actuator_with_id(act)).collect(toSet()));
	}

	public jaxb.Controller to_jaxb(){
		jaxb.Controller j = new jaxb.Controller();
		j.setId(id);
		j.setDt(dt);
		j.setStartTime(start_time);
		j.setEndTime(end_time);
		j.setType(algorithm.toString());

		jaxb.TargetActuators tgtacts = new jaxb.TargetActuators();
		j.setTargetActuators(tgtacts);
		tgtacts.setIds(OTMUtils.comma_format(actuators.values().stream().map(x->x.id).collect(toSet())));

//		j.setFeedbackSensors();
		return j;
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
		this.algorithm = Algorithm.valueOf(algorithm);
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

}
