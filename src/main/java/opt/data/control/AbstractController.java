package opt.data.control;

import error.OTMException;
import opt.data.Scenario;
import utils.OTMUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

public abstract class AbstractController {

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

	public AbstractController(jaxb.Controller j, Scenario scn) throws OTMException {
		this.id = j.getId();
		this.dt = j.getDt();
		this.start_time = j.getStartTime();
		this.end_time = j.getEndTime()==null ? Float.POSITIVE_INFINITY : j.getEndTime();
		this.algorithm = Algorithm.valueOf(j.getType());
		this.actuators = new HashMap<>();
		List<Long> ids = OTMUtils.csv2longlist(j.getTargetActuators().getIds());
		for(Long id :ids)
			this.actuators.put(id,scn.get_actuator_with_id(id));

		// CHECKS
		if(start_time<0)
			throw new OTMException("start_time<0");

		if(end_time<=start_time)
			throw new OTMException("end_time<=start_time");
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

}
