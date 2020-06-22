package opt.data.control;

import opt.data.FreewayScenario;

public class ControllerRampMeterClosed extends AbstractControllerRampMeter {

    public ControllerRampMeterClosed(FreewayScenario scn, Long id) throws Exception {
        super(id!=null ? id : scn.new_controller_id(),
                Float.POSITIVE_INFINITY,
                control.AbstractController.Algorithm.closed,
                false,
                Float.POSITIVE_INFINITY,
                Float.POSITIVE_INFINITY);
    }

}
