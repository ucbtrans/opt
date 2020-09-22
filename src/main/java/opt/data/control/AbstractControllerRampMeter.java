package opt.data.control;

import jaxb.Parameter;

import java.util.Collection;
import java.util.HashSet;

public abstract class AbstractControllerRampMeter extends AbstractController {

    protected boolean has_queue_control;
    protected float min_rate_vph;
    protected float max_rate_vph;

    ////////////////////////////////
    // construction
    ////////////////////////////////

    public AbstractControllerRampMeter(Float dt, control.AbstractController.Algorithm  algorithm,boolean has_queue_control,float min_rate_vph,float max_rate_vph) throws Exception {
        super(Type.RampMetering, dt, algorithm);
        this.has_queue_control = has_queue_control;
        this.min_rate_vph = min_rate_vph;
        this.max_rate_vph = max_rate_vph;
    }

    ////////////////////////////////
    // to jaxb
    ////////////////////////////////

    @Override
    public Collection<Parameter> jaxb_parameters() {
        Collection<jaxb.Parameter> pset = new HashSet<>();

        boolean write_quecontrol = has_queue_control;
        boolean write_min_rate = Float.isFinite(min_rate_vph);
        boolean write_max_rate = Float.isFinite(max_rate_vph);

        // write has_queue_control
        if(write_quecontrol){
            jaxb.Parameter p = new jaxb.Parameter();
            p.setName("queue_control");
            p.setValue(has_queue_control ? "true" : "false");
            pset.add(p);
        }

        // write min rate
        if(write_min_rate){
            jaxb.Parameter p = new jaxb.Parameter();
            p.setName("min_rate_vphpl");
            p.setValue(String.format("%.0f",min_rate_vph));
            pset.add(p);
        }

        // write max rate
        if(write_max_rate){
            jaxb.Parameter p = new jaxb.Parameter();
            p.setName("max_rate_vphpl");
            p.setValue(String.format("%.0f",max_rate_vph));
            pset.add(p);
        }

        return pset;
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
