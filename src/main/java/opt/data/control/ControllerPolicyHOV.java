package opt.data.control;

import jaxb.Parameter;
import opt.data.FreewayScenario;
import utils.OTMUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ControllerPolicyHOV extends AbstractController {

	Set<Long> dissallowed_comms;

	////////////////////////////////
	// construction
	////////////////////////////////

	public ControllerPolicyHOV(FreewayScenario scn, Long id, float dt, Set<Long> dissallowed_comms) throws Exception {
		super(id!=null ? id : scn.new_controller_id(),Type.HOVpolicy,dt,null);
		this.dissallowed_comms = dissallowed_comms;
	}

	////////////////////////////////
	// AbstractController
	////////////////////////////////

	@Override
	public Collection<Parameter> jaxb_parameters() {
		Set<Parameter> params = new HashSet<>();
		Parameter n = new Parameter();
		n.setName("dissallowed_comms");
		n.setValue(OTMUtils.comma_format(dissallowed_comms));
		params.add(n);
		return params;
	}

}
