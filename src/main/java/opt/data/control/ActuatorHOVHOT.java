package opt.data.control;

import jaxb.Actuator;
import opt.data.AbstractLink;
import opt.data.LaneGroupType;

import java.util.Collection;
import java.util.Set;

public class ActuatorHOVHOT extends AbstractActuator  {

    public ActuatorHOVHOT(long id, Collection<AbstractLink> links, LaneGroupType lgtype){
        super(id,links,lgtype);
    }

    @Override
    public Actuator to_jaxb() {
        jaxb.Actuator j =  super.to_jaxb();
        j.setType("hovpolicy");
//        jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
//        j.setActuatorTarget(jtgt);
//        jtgt.setType("link");
//        jtgt.setId(link.id);
        return j;
    }

}
