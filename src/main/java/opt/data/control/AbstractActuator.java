package opt.data.control;

public abstract class AbstractActuator {

	public long id;
	public long link_id;
	public AbstractController myController;

	public AbstractActuator(long id,long link_id){
		this.id = id;
		this.link_id = link_id;
	}

	public AbstractActuator(jaxb.Actuator j){
		this.id = j.getId();
		this.link_id = j.getActuatorTarget().getId();
	}

	public jaxb.Actuator to_jaxb(){
		jaxb.Actuator j = new jaxb.Actuator();
		j.setId(id);
		jaxb.ActuatorTarget target = new jaxb.ActuatorTarget();
		target.setType("link");
		target.setId(link_id);
		j.setActuatorTarget(target);
		return j;
	}

}
