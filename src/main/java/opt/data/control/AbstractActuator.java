package opt.data.control;

import opt.data.AbstractLink;

public abstract class AbstractActuator {

	public long id;
	public AbstractLink link;
	public AbstractController myController;

	public AbstractActuator(long id,AbstractLink link){
		this.id = id;
		this.link = link;
	}

	public AbstractActuator(jaxb.Actuator j){
		this.id = j.getId();
	}

	public jaxb.Actuator to_jaxb(){
		jaxb.Actuator j = new jaxb.Actuator();
		j.setId(id);
		return j;
	}

}
