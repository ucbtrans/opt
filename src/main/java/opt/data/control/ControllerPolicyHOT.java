package opt.data.control;

import jaxb.Parameter;
import opt.data.FreewayScenario;

import java.util.Collection;

public class ControllerPolicyHOT extends AbstractController {
	public ControllerPolicyHOT(FreewayScenario scn,Long id, float dt) throws Exception {
		super(id!=null ? id : scn.new_controller_id(), Type.HOTpolicy, dt, null);
	}

	@Override
	public Collection<Parameter> jaxb_parameters() {
		return null;
	}
}
