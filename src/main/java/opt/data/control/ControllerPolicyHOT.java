package opt.data.control;

import opt.data.FreewayScenario;

public class ControllerPolicyHOT extends AbstractController {
	public ControllerPolicyHOT(FreewayScenario scn,Long id, float dt, float start_time, Float end_time) throws Exception {
		super(id!=null ? id : scn.new_controller_id(), dt, start_time, end_time, null);
	}

	@Override
	public String getAlgorithm() {
		return "HOT";
	}
}
