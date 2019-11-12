package opt.data;

import utils.OTMUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public abstract class AbstractController {

	// TODO Use the one in otm-sim instead
	public enum Algorithm {
		fuzzylogic,
		alinea
	}

	public long id;
	public float dt;
	public Algorithm algorithm;
	public Map<Long,AbstractActuator> actuators;

	public AbstractController(jaxb.Controller j,Scenario scn){
		this.id = j.getId();
		this.dt = j.getDt();
		this.algorithm = Algorithm.valueOf(j.getType());
		this.actuators = new HashMap<>();
		List<Long> ids = OTMUtils.csv2longlist(j.getTargetActuators().getIds());
		for(Long id :ids)
			this.actuators.put(id,scn.actuators.get(id));
	}

	public jaxb.Controller to_jaxb(){
		jaxb.Controller j = new jaxb.Controller();
		j.setId(id);
		j.setDt(dt);
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

	public String getAlgorithm() {
		return algorithm.toString();
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = Algorithm.valueOf(algorithm);
	}

}
