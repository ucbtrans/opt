package opt.data.control;

import opt.data.FreewayScenario;

public class ControllerPolicyHOT extends AbstractController {
	public ControllerPolicyHOT(FreewayScenario scn,Long id, float dt) throws Exception {
		super(id!=null ? id : scn.new_controller_id(), Type.HOTpolicy, dt, null);
	}
}
