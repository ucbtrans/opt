package opt.data;

import utils.OTMUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractController {
	public long id;
	public float dt;
	public Map<Long,AbstractActuator> actuators;

	public AbstractController(jaxb.Controller j,Scenario scn){
		this.id = j.getId();
		this.dt = j.getDt();
		this.actuators = new HashMap<>();
		List<Long> ids = OTMUtils.csv2longlist(j.getTargetActuators().getIds());
		for(Long id :ids)
			this.actuators.put(id,scn.actuators.get(id));
	}
}
