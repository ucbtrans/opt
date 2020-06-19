package opt.data.control;

import jaxb.Controller;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;

public class ControllerRampMeterFixedRate extends AbstractControllerRampMeter {

    protected float rate_vphpl;

    ////////////////////////////////
    // construction
    ////////////////////////////////

    public ControllerRampMeterFixedRate(FreewayScenario scn, Long id, float dt, boolean has_queue_control, float min_rate_vphpl, float max_rate_vphpl,float rate_vphpl, Long act_id, long ramp_link_id, LaneGroupType lgtype) throws Exception {
        super(id!=null ? id : scn.new_controller_id(),
                dt,control.AbstractController.Algorithm.fixed_rate,has_queue_control,min_rate_vphpl,max_rate_vphpl);

        this.rate_vphpl = rate_vphpl;

        // ramp meter actuator
//        ActuatorRampMeter rm = ControlFactory.create_ramp_meter(scn,act_id,ramp_link_id,lgtype,this);
//        add_actuator(rm);

    }

    public void set_rate_vph(float new_rate){
        this.rate_vphpl = new_rate;
    }

    @Override
    public Controller to_jaxb() {
        jaxb.Controller j = super.to_jaxb();

        // write rate
        jaxb.Parameter p = new jaxb.Parameter();
        p.setName("rate_vphpl");
        p.setValue(String.format("%.0f",rate_vphpl));
        j.getParameters().getParameter().add(p);

        return j;
    }
}
