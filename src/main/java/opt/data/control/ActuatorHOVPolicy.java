package opt.data.control;

import jaxb.Actuator;
import opt.data.LaneGroupType;

public class ActuatorHOVPolicy extends AbstractActuator  {

    public ActuatorHOVPolicy(long id, long link_id,int [] lanes, LaneGroupType lgtype){
        super(id,link_id,lanes,lgtype);
    }

    public ActuatorHOVPolicy(Actuator j) {
        super(j);
    }

    @Override
    public Actuator to_jaxb() {
        jaxb.Actuator j =  super.to_jaxb();
        j.setType("hovpolicy");
        jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
        j.setActuatorTarget(jtgt);
        jtgt.setType("link");
        jtgt.setId(link_id);
        return j;
    }

}
