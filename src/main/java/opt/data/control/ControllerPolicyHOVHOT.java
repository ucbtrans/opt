package opt.data.control;

import jaxb.Parameter;
import opt.data.FreewayScenario;
import utils.OTMUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ControllerPolicyHOVHOT extends AbstractController {

	protected Set<Long> disallowed_comms;
	protected Set<Long> free_comms;
	protected Double a0,a1,a2;
	protected int [][] vplph_to_cents_table;
	// vplph_to_cents_table[i][1] is the price for all flows
	// between vplph_to_cents_table[i][0] and vplph_to_cents_table[i+1][0]
	// the price for flows above vplph_to_cents_table[last][0] = vplph_to_cents_table[last][1]
	// the price for flows below vplph_to_cents_table[0][0] = 0


	////////////////////////////////
	// construction
	////////////////////////////////

	public ControllerPolicyHOVHOT(FreewayScenario scn, Long id, Set<Long> disallowed_comms,Set<Long> free_comms,Double a0,Double a1,Double a2,int [][] vplph_to_cents_table) {
		super(id!=null ? id : scn.new_controller_id(),Type.HOVHOT,null, control.AbstractController.Algorithm.lg_restrict);
		this.disallowed_comms = disallowed_comms==null ? new HashSet<>() : disallowed_comms;
		this.free_comms =  free_comms==null ? new HashSet<>() : free_comms;
		this.a0 = a0;
		this.a1 = a1;
		this.a2 = a2;
		this.vplph_to_cents_table = vplph_to_cents_table;
	}

	////////////////////////////////
	// AbstractController
	////////////////////////////////

	@Override
	public Collection<Parameter> jaxb_parameters() {

		Set<Parameter> params = new HashSet<>();

		if(!disallowed_comms.isEmpty()){
			Parameter n = new Parameter();
			n.setName("disallowed_comms");
			n.setValue(OTMUtils.comma_format(disallowed_comms));
			params.add(n);
		}

		if(!free_comms.isEmpty()){
			Parameter n = new Parameter();
			n.setName("free_comms");
			n.setValue(OTMUtils.comma_format(free_comms));
			params.add(n);
		}

		if(a0!=null){
			Parameter n = new Parameter();
			n.setName("a0");
			n.setValue(a0.toString());
			params.add(n);
		}

		if(a1!=null){
			Parameter n = new Parameter();
			n.setName("a1");
			n.setValue(a1.toString());
			params.add(n);
		}

		if(a2!=null){
			Parameter n = new Parameter();
			n.setName("a2");
			n.setValue(a2.toString());
			params.add(n);
		}

		if(vplph_to_cents_table!=null){
			Parameter n = new Parameter();
			n.setName("vplph_to_cents_table");
			n.setValue(OTMUtils.write_int_table(vplph_to_cents_table));
			params.add(n);
		}

		return params;
	}

}
