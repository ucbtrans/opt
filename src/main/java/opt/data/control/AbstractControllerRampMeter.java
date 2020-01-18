package opt.data.control;

import jaxb.Actuator;
import jaxb.Controller;
import opt.data.Scenario;

import java.util.Map;

public abstract class AbstractControllerRampMeter extends AbstractController {

    protected boolean has_queue_control;
    protected float min_rate_vph;
    protected float max_rate_vph;

    public AbstractControllerRampMeter(long id, float dt, float start_time, Float end_time, String algorithm) throws Exception {
        super(id, dt, start_time, end_time, algorithm);
    }

    public AbstractControllerRampMeter(Controller j, Map<Long, Actuator> a, Map<Long,jaxb.Sensor> s, Scenario scn) throws Exception {
        super(j,a,s,scn);
    }

    ////////////////////////////////
    // API
    ////////////////////////////////

    public boolean isHas_queue_control() {
        return has_queue_control;
    }

    public void setHas_queue_control(boolean has_queue_control) {
        this.has_queue_control = has_queue_control;
    }

    public float getMin_rate_vph() {
        return min_rate_vph;
    }

    public void setMin_rate_vph(float min_rate_vph) {
        this.min_rate_vph = min_rate_vph;
    }

    public float getMax_rate_vph() {
        return max_rate_vph;
    }

    public void setMax_rate_vph(float max_rate_vph) {
        this.max_rate_vph = max_rate_vph;
    }
}
