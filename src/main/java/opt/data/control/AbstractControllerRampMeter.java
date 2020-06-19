package opt.data.control;

public abstract class AbstractControllerRampMeter extends AbstractController {

    protected boolean has_queue_control;
    protected float min_rate_vph;
    protected float max_rate_vph;

    ////////////////////////////////
    // construction
    ////////////////////////////////

    public AbstractControllerRampMeter(long id, float dt, control.AbstractController.Algorithm  algorithm,boolean has_queue_control,float min_rate_vph,float max_rate_vph) throws Exception {
        super(id, Type.RampMetering, dt, algorithm);
        this.has_queue_control = has_queue_control;
        this.min_rate_vph = min_rate_vph;
        this.max_rate_vph = max_rate_vph;
    }

    ////////////////////////////////
    // to jaxb
    ////////////////////////////////

    @Override
    public jaxb.Controller to_jaxb(){
        jaxb.Controller j = super.to_jaxb();

        boolean write_quecontrol = has_queue_control;
        boolean write_min_rate = Float.isFinite(min_rate_vph);
        boolean write_max_rate = Float.isFinite(max_rate_vph);

        if(write_quecontrol || write_min_rate || write_max_rate)
            j.setParameters(new jaxb.Parameters());

        // write has_queue_control
        if(write_quecontrol){
            jaxb.Parameter p = new jaxb.Parameter();
            p.setName("queue_control");
            p.setValue(has_queue_control ? "true" : "false");
            j.getParameters().getParameter().add(p);
        }

        // write min rate
        if(write_min_rate){
            jaxb.Parameter p = new jaxb.Parameter();
            p.setName("min_rate_vphpl");
            p.setValue(String.format("%.0f",min_rate_vph));
            j.getParameters().getParameter().add(p);
        }

        // write max rate
        if(write_max_rate){
            jaxb.Parameter p = new jaxb.Parameter();
            p.setName("max_rate_vphpl");
            p.setValue(String.format("%.0f",max_rate_vph));
            j.getParameters().getParameter().add(p);
        }

        return j;

        // TODO THIS IS TEMPORARY FOR STORING THE ACTUATOR LANE GROUP AS A PARAMETER OF THE CONTROLLER
//		if(actuators!=null && actuators.size()==1){
//			AbstractActuator act = actuators.values().iterator().next();
//
//			jaxb.Parameter param = new jaxb.Parameter();
//			param.setName("lane_group");
//			param.setValue(act.lgtype.toString());
//			j.getParameters().getParameter().add(param);
//		}
//
//		if(actuators!=null && !actuators.isEmpty()){
//			jaxb.TargetActuators tgtacts = new jaxb.TargetActuators();
//			j.setTargetActuators(tgtacts);
//			tgtacts.setIds(OTMUtils.comma_format(actuators.values().stream().map(x->x.id).collect(toSet())));
//		}


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
