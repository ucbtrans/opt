package opt.data.control;

import opt.data.ControlFactory;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;

public class ControllerRampMeterOpen extends AbstractControllerRampMeter {

    public ControllerRampMeterOpen(FreewayScenario scn, Long id, Long act_id, long ramp_link_id, LaneGroupType lgtype) throws Exception {
        super(id!=null ? id : scn.new_controller_id(),
                Float.POSITIVE_INFINITY,
                control.AbstractController.Algorithm.open,
                false,
                0f,
                Float.POSITIVE_INFINITY);

        // ramp meter actuator
//        ActuatorRampMeter rm = ControlFactory.create_ramp_meter(scn,act_id,ramp_link_id,lgtype,this);
//        add_actuator(rm);
    }

}
