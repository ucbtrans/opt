package opt.data.control;

import opt.data.FreewayScenario;

public class ControllerPolicyHOV extends AbstractController {
	public ControllerPolicyHOV(FreewayScenario scn,Long id, float dt) throws Exception {
		super(id!=null ? id : scn.new_controller_id(),Type.HOVpolicy,dt,null);
	}
}
