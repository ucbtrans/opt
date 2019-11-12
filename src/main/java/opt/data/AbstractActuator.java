package opt.data;

public abstract class AbstractActuator {
	public long id;
	public AbstractController myController;
	public AbstractLink link;

	public AbstractActuator(jaxb.Actuator j,Scenario scenario){
		this.id = j.getId();
	}

	public jaxb.Actuator to_jaxb(){
		jaxb.Actuator j = new jaxb.Actuator();
		j.setId(id);
		j.setDt(myController.dt);
		j.setType("ramp_meter");
		jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
		j.setActuatorTarget(jtgt);
		jtgt.setType("link");
		jtgt.setId(link.id);
		return j;
	}

}
