package opt.data.control;

import control.AbstractController;
import opt.data.FreewayScenario;

public class ControllerRampMeterClosed extends AbstractControllerRampMeter {

    public ControllerRampMeterClosed(FreewayScenario scn) throws Exception {
        super(Float.POSITIVE_INFINITY,
                AbstractController.Algorithm.rm_closed,
                false,
                Float.POSITIVE_INFINITY,
                Float.POSITIVE_INFINITY);
    }

}
