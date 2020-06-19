package opt.data.control;

import opt.data.FreewayScenario;

public class ControllerRampMeterClosed extends AbstractControllerRampMeter {

    public ControllerRampMeterClosed(FreewayScenario scn, Long id, Long act_id, long ramp_link_id) throws Exception {
        super(id!=null ? id : scn.new_controller_id(),
                Float.POSITIVE_INFINITY,
                control.AbstractController.Algorithm.closed,
                false,
                Float.POSITIVE_INFINITY,
                Float.POSITIVE_INFINITY);

        // ramp meter actuator
//        ActuatorRampMeter rm = ControlFactory.create_ramp_meter(scn,act_id,ramp_link_id,lgtype,this);
//        add_actuator(rm);
    }

}
