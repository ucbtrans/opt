package opt.data.control;

import jaxb.Parameter;
import opt.data.FreewayScenario;
import utils.OTMUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ControllerPolicyHOVHOT extends AbstractController {

	Set<Long> disallowed_comms;

	////////////////////////////////
	// construction
	////////////////////////////////

	public ControllerPolicyHOVHOT(FreewayScenario scn, Long id, Set<Long> disallowed_comms) {
		super(id!=null ? id : scn.new_controller_id(),Type.HOVHOT,null, control.AbstractController.Algorithm.lg_restrict);
		this.disallowed_comms = disallowed_comms==null ? new HashSet<>() : disallowed_comms;
	}

	////////////////////////////////
	// AbstractController
	////////////////////////////////

	@Override
	public Collection<Parameter> jaxb_parameters() {
		if(disallowed_comms.isEmpty())
			return new HashSet<>();
		Set<Parameter> params = new HashSet<>();
		Parameter n = new Parameter();
		n.setName("disallowed_comms");
		n.setValue(OTMUtils.comma_format(disallowed_comms));
		params.add(n);
		return params;
	}

}
