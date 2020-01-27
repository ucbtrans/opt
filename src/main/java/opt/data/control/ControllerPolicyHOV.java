package opt.data.control;

import opt.data.FreewayScenario;

public class ControllerPolicyHOV extends AbstractController {
	public ControllerPolicyHOV(FreewayScenario scn, float dt, float start_time, Float end_time) throws Exception {
		super(scn.new_controller_id(),dt,start_time,end_time,null);
	}
}
