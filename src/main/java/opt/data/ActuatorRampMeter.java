package opt.data;

import jaxb.Actuator;

public class ActuatorRampMeter extends AbstractActuator {

	public float min_value;
	public float max_value;

	public ActuatorRampMeter(Actuator j,Scenario scenario) {
		super(j,scenario);
		this.min_value = j.getMinValue();
		this.max_value = j.getMaxValue();
	}

	@Override
	public Actuator to_jaxb() {
		jaxb.Actuator j = super.to_jaxb();
		j.setMaxValue(max_value);
		j.setMinValue(min_value);
		return j;
	}
}
