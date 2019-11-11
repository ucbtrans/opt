package opt.data;

import java.util.Set;

public abstract class AbstractActuator {
	public long id;
	public float min_value;
	public float max_value;

	public AbstractActuator(jaxb.Actuator j,Scenario scenario){
		this.id = j.getId();
		this.min_value = j.getMinValue();
		this.max_value = j.getMaxValue();
	}
}
