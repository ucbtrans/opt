package opt.data.control;

import jaxb.Actuator;
import opt.data.AbstractLink;
import opt.data.LaneGroupType;

public class ActuatorRampMeter extends AbstractActuator {

	public ActuatorRampMeter(long id, AbstractLink link, LaneGroupType lgtype ) {
		super(id,link,lgtype);
	}

	@Override
	public Actuator to_jaxb() {
		jaxb.Actuator j =  super.to_jaxb();
		j.setType("meter");
		jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
		j.setActuatorTarget(jtgt);
		jtgt.setType("lanegroups");
		AbstractLink link = links.iterator().next();
		int []lanes = link.lgtype2lanes(lgtype);
		jtgt.setContent(String.format("%d(%d#%d)",link.id,lanes[0],lanes[1]));
		return j;
	}

}
