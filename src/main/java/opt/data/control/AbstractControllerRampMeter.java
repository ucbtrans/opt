package opt.data.control;

import jaxb.Actuator;
import jaxb.Controller;
import opt.data.Scenario;

import java.util.Map;

public abstract class AbstractControllerRampMeter extends AbstractController {

    protected boolean has_queue_control;
    protected float min_rate_vph;
    protected float max_rate_vph;

    // jaxb
    public AbstractControllerRampMeter(Controller j, Map<Long, Actuator> a, Map<Long,jaxb.Sensor> s, Scenario scn) throws Exception {
        super(j,a,s,scn);

        // get my ramp meter id
        if(j.getTargetActuators()!=null){
            long act_id = j.getTargetActuators().getTargetActuator().iterator().next().getId();
            Actuator actuator = a.get(act_id);
            this.min_rate_vph = actuator.getMinValue();
            this.max_rate_vph = actuator.getMaxValue();
        }

        // TODO READ has_queue_control from JAXB

    }

    // factory
    public AbstractControllerRampMeter(long id, float dt, float start_time, Float end_time, String algorithm,boolean has_queue_control,float min_rate_vph,float max_rate_vph) throws Exception {
        super(id, dt, start_time, end_time, algorithm);
        this.has_queue_control = has_queue_control;
        this.min_rate_vph = min_rate_vph;
        this.max_rate_vph = max_rate_vph;
    }

    @Override
    public Controller to_jaxb() {

        // TODO WRITE has_queue_control

        Controller cntrl = super.to_jaxb();
        return cntrl;
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
