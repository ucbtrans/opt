package opt.data.control;

import opt.data.FreewayScenario;

import java.util.HashSet;
import java.util.Set;

public class ControllerRampMeterOpen extends AbstractControllerRampMeter {

    public ControllerRampMeterOpen(FreewayScenario scn) throws Exception {
        super(Float.POSITIVE_INFINITY,
                control.AbstractController.Algorithm.rm_open,
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
