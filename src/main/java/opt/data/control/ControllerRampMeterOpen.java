package opt.data.control;

import opt.data.FreewayScenario;

public class ControllerRampMeterOpen extends AbstractControllerRampMeter {

    public ControllerRampMeterOpen(FreewayScenario scn, Long id) throws Exception {
        super(id!=null ? id : scn.new_controller_id(),
                Float.POSITIVE_INFINITY,
                control.AbstractController.Algorithm.open,
                false,
                Float.POSITIVE_INFINITY,
                Float.POSITIVE_INFINITY);
    }

}
