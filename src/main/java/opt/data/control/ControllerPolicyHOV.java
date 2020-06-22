package opt.data.control;

import jaxb.Parameter;
import opt.data.FreewayScenario;

import java.util.Collection;

public class ControllerPolicyHOV extends AbstractController {
	public ControllerPolicyHOV(FreewayScenario scn,Long id, float dt) throws Exception {
		super(id!=null ? id : scn.new_controller_id(),Type.HOVpolicy,dt,null);
	}

	@Override
	public Collection<Parameter> jaxb_parameters() {
		return null;
	}
}
