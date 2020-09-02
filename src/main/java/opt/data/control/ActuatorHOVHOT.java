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
        j.setType("lg_restrict");
        jaxb.ActuatorTarget jtgt = new jaxb.ActuatorTarget();
        j.setActuatorTarget(jtgt);
        jtgt.setType("lanegroups");

        String str = "";
        for(AbstractLink link : links){
            int []lanes = link.lgtype2lanes(lgtype);
            String str2 = String.format("%d(%d#%d)",link.id,lanes[0],lanes[1]);
            str += str2 + ",";
        }
        if(!str.isEmpty())
            str = str.substring(0, str.length() - 1);
        jtgt.setContent(str);


        return j;
    }

}
