package opt.data.control;

import jaxb.Actuator;
import opt.data.LaneGroupType;

public class ActuatorRampMeter extends AbstractActuator {

	public ActuatorRampMeter(long id, long link_id, LaneGroupType lgtype, AbstractController myController ){
		super(id,link_id,lgtype,myController);
	}

	public ActuatorRampMeter(Actuator j) {
		super(j);
	}

	@Override
	public Actuator to_jaxb() {
		jaxb.Actuator j =  super.to_jaxb();
		j.setType("meter");
		jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
		j.setActuatorTarget(jtgt);
		jtgt.setType("link");
		jtgt.setId(link_id);

		if(myController instanceof AbstractControllerRampMeter){
			AbstractControllerRampMeter c = (AbstractControllerRampMeter) myController;
			j.setMinValue(c.min_rate_vph);
			j.setMaxValue(c.max_rate_vph);
		}

		return j;
	}

}
