package opt.data.control;

import control.AbstractController;
import opt.data.FreewayScenario;

import java.util.HashSet;
import java.util.Set;

public class ControllerRampMeterClosed extends AbstractControllerRampMeter {

    public ControllerRampMeterClosed(FreewayScenario scn) throws Exception {
        super(Float.POSITIVE_INFINITY,
                AbstractController.Algorithm.rm_closed,
                false,
                Float.NaN,
                Float.POSITIVE_INFINITY,
                Float.POSITIVE_INFINITY);
    }

    @Override
    public Set<Sensor> get_sensors() {
        return new HashSet<>();
    }
}
