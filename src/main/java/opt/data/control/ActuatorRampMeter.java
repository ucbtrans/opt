package opt.data.control;

import jaxb.Actuator;
import opt.data.AbstractLink;

public class ActuatorRampMeter extends AbstractActuator {

	public ActuatorRampMeter(long id, AbstractLink link){
		super(id,link);
	}

	public ActuatorRampMeter(Actuator j) {
		super(j);
	}

	@Override
	public Actuator to_jaxb() {
		jaxb.Actuator j =  super.to_jaxb();
		j.setType("ramp_meter");
		jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
		j.setActuatorTarget(jtgt);
		jtgt.setId(link.id);
		return j;
	}

}
