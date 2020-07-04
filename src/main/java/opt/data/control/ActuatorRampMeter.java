package opt.data.control;

import jaxb.Actuator;
import opt.data.AbstractLink;
import opt.data.LaneGroupType;

public class ActuatorRampMeter extends AbstractActuator {

	public ActuatorRampMeter(long id, AbstractLink link, LaneGroupType lgtype ){
		super(id,link,lgtype);
	}

	@Override
	public Actuator to_jaxb() {
		jaxb.Actuator j =  super.to_jaxb();
		j.setType("meter");
		jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
		j.setActuatorTarget(jtgt);
		jtgt.setType("lanegroup");
		jtgt.setId(link.id);
		int []lanes = link.lgtype2lanes(lgtype);
		jtgt.setLanes(String.format("%d#%d",lanes[0],lanes[1]));

//			AbstractControllerRampMeter c = (AbstractControllerRampMeter) myController;
//			j.setMinValue(c.min_rate_vph);
//			j.setMaxValue(c.max_rate_vph);

		return j;
	}

}
