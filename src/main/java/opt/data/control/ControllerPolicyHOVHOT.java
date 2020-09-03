package opt.data.control;

import jaxb.Parameter;
import opt.data.FreewayScenario;
import utils.OTMUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ControllerPolicyHOVHOT extends AbstractController {

	Set<Long> disallowed_comms;

	// TODO add these
	Set<Long> free_comms;
	int [][] vplph_to_cents_table;
	// vplph_to_cents_table[i][1] is the price for all flows
	// between vplph_to_cents_table[i][0] and vplph_to_cents_table[i+1][0]
	// the price for flows above vplph_to_cents_table[last][0] = vplph_to_cents_table[last][1]
	// the price for flows below vplph_to_cents_table[0][0] = 0

	Double a0,a1,a2;

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
