package opt.data.control;

import opt.data.AbstractLink;
import opt.data.Scenario;

public abstract class AbstractActuator {
	public long id;
	public AbstractController myController;
	public AbstractLink link;

	public AbstractActuator(jaxb.Actuator j, Scenario scenario){
		this.id = j.getId();
	}

	public jaxb.Actuator to_jaxb(){
		jaxb.Actuator j = new jaxb.Actuator();
		j.setId(id);
		return j;
	}

}
