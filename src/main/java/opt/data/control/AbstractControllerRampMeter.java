package opt.data.control;

import jaxb.Controller;

public abstract class AbstractControllerRampMeter extends AbstractController {

    protected boolean has_queue_control;
    protected float min_rate_vph;
    protected float max_rate_vph;

    // factory
    public AbstractControllerRampMeter(long id, float dt, float start_time, Float end_time, control.AbstractController.Algorithm  algorithm,boolean has_queue_control,float min_rate_vph,float max_rate_vph) throws Exception {
        super(id, dt, start_time, end_time, algorithm);
        this.has_queue_control = has_queue_control;
        this.min_rate_vph = min_rate_vph;
        this.max_rate_vph = max_rate_vph;
    }

    @Override
    public Controller to_jaxb() {
        Controller cntrl = super.to_jaxb();

        jaxb.Parameters params = new jaxb.Parameters();
        cntrl.setParameters(params);

        // write has_queue_control
        jaxb.Parameter param = new jaxb.Parameter();
        param.setName("queue_control");
        param.setValue(has_queue_control ? "true" : "false");
        params.getParameter().add(param);

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
