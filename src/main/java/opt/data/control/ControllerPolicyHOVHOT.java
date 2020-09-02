package opt.data.control;

import jaxb.Parameter;
import opt.data.FreewayScenario;
import utils.OTMUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ControllerPolicyHOVHOT extends AbstractController {

	Set<Long> dissallowed_comms;

	////////////////////////////////
	// construction
	////////////////////////////////

	public ControllerPolicyHOVHOT(FreewayScenario scn, Long id, Set<Long> dissallowed_comms) {
		super(id!=null ? id : scn.new_controller_id(),Type.HOVHOT,null, control.AbstractController.Algorithm.lg_restrict);
		this.dissallowed_comms = dissallowed_comms==null ? new HashSet<>() : dissallowed_comms;
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
