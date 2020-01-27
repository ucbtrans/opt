package opt.data.control;

import opt.data.FreewayScenario;

public class ControllerPolicyHOT extends AbstractController {
	public ControllerPolicyHOT(FreewayScenario scn, float dt, float start_time, Float end_time) throws Exception {
		super(scn.new_controller_id(), dt, start_time, end_time, null);
	}
}
