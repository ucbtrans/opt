package opt.data.control;

import opt.data.FreewayScenario;

public class ControllerRampMeterOpen extends AbstractControllerRampMeter {

    public ControllerRampMeterOpen(FreewayScenario scn) throws Exception {
        super(Float.POSITIVE_INFINITY,
                control.AbstractController.Algorithm.rm_open,
                false,
                Float.POSITIVE_INFINITY,
                Float.POSITIVE_INFINITY);
    }

}
