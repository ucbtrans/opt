package opt.data.control;

import jaxb.Parameter;
import opt.UserSettings;
import opt.data.FreewayScenario;
import utils.OTMUtils;

import java.util.*;

public class ControllerPolicyHOVHOT extends AbstractController {


	public enum Permission { Free, Banned, Tolled }
	protected Permission permission;
	protected Map<Long,Permission> comm2permission;

	protected Double a0,a1,a2;
	protected int [][] vphpl_to_cents_table;
	// vphpl_to_cents_table[i][1] is the price for all flows
	// between vphpl_to_cents_table[i][0] and vplph_to_cents_table[i+1][0]
	// the price for flows above vphpl_to_cents_table[last][0] = vphpl_to_cents_table[last][1]
	// the price for flows below vphpl_to_cents_table[0][0] = 0

	protected Double qos_speed_threshold_kph;
	// It is the quality of service speed threshold: when speed in a managed lane falls below this threshold,
	// only free vehicle types are allowed in the managed lane, no tolled traffic. It is an equivalent to the infinite price.
	// For example, for HOT lanes in CA, this threshold is 45 mph. If the speed in an HOT lane drops below that, only HOVs are admitted.

	////////////////////////////////
	// construction
	////////////////////////////////

	public ControllerPolicyHOVHOT(FreewayScenario scn, Long id, Set<Long> disallowed_comms,Set<Long> free_comms,Float dt, Double a0,Double a1,Double a2,int [][] vphpl_to_cents_table, Double qos_speed_threshold_kph) {
		super(id!=null ? id : scn.new_controller_id(),Type.HOVHOT,dt, control.AbstractController.Algorithm.lg_restrict);

		this.comm2permission = new HashMap<>();

		// all are tolled by default
		for(Long cid : scn.get_commodities().keySet())
			comm2permission.put(cid,Permission.Tolled);
		for(Long cid : disallowed_comms)
			comm2permission.put(cid,Permission.Banned);
		for(Long cid : free_comms)
			comm2permission.put(cid,Permission.Free);

		refresh_type();

		this.a0 = a0==null ? UserSettings.defaultLaneChoice_A0 : a0;
		this.a1 = a1==null ? UserSettings.defaultLaneChoice_A1 : a1;
		this.a2 = a2==null ? UserSettings.defaultLaneChoice_A2 : a2;

		if(vphpl_to_cents_table==null){
			this.vphpl_to_cents_table = new int[1][2];
			this.vphpl_to_cents_table[0][0] = 0;
			this.vphpl_to_cents_table[0][1] = 0;
		} else
			this.vphpl_to_cents_table = vphpl_to_cents_table;
		this.qos_speed_threshold_kph = qos_speed_threshold_kph==null ? UserSettings.defaultQosSpeedThresholdKph : qos_speed_threshold_kph;
	}
        
	////////////////////////////////
	// Getters
	////////////////////////////////

	public Double get_a0() {
		return a0;
	}

	public Double get_a1() {
		return a1;
	}

	public Double get_a2() {
		return a2;
	}

	public int[][] get_vphpl_to_cents_table() {
		return vphpl_to_cents_table;
	}

	public Double get_qos_speed_threshold_kph() {
		return qos_speed_threshold_kph;
	}

	public Permission get_global_permission(){
		return permission;
	}

	public Permission get_comm_permission(Long commid){
		return comm2permission.get(commid);
	}


	////////////////////////////////
	// Setters
	////////////////////////////////

	public void set_a0(Double x) {
		a0 = x;
	}

	public void set_a1(Double x) {
		a1 = x;
	}

	public void set_a2(Double x) {
		a2 = x;
	}

	public void set_vphpl_to_cents_table(int[][] x) {
		vphpl_to_cents_table = x;
	}

	public void set_qos_speed_threshold_kph(Double x) {
		qos_speed_threshold_kph = x;
	}

	public void set_comm_permission(Long commid,Permission perm){
		comm2permission.put(commid,perm);
	}

	public void refresh_type(){
		Set<Permission> unique_types = new HashSet(comm2permission.values());
		permission = unique_types.size()>1 ? Permission.Tolled :  unique_types.iterator().next();
	}

	////////////////////////////////
	// AbstractController
	////////////////////////////////

	@Override
	public Collection<Parameter> jaxb_parameters() {

		Set<Parameter> params = new HashSet<>();

		Set<Long> banned_comms = new HashSet<>();
		Set<Long> free_comms = new HashSet<>();
		for(Map.Entry<Long,Permission> e : comm2permission.entrySet()){
			switch(e.getValue()){
				case Free:
					free_comms.add(e.getKey());
					break;
				case Banned:
					banned_comms.add(e.getKey());
					break;
				default:
					break;
			}
		}

		if(!banned_comms.isEmpty()){
			Parameter n = new Parameter();
			n.setName("disallowed_comms");
			n.setValue(OTMUtils.comma_format(banned_comms));
			params.add(n);
		}

		if(!free_comms.isEmpty()){
			Parameter n = new Parameter();
			n.setName("free_comms");
			n.setValue(OTMUtils.comma_format(free_comms));
			params.add(n);
		}

		// tolling parameters ............

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

		if(vphpl_to_cents_table!=null){
			Parameter n = new Parameter();
			n.setName("vplph_to_cents_table");
			n.setValue(OTMUtils.write_int_table(vphpl_to_cents_table));
			params.add(n);
		}
                
		if(qos_speed_threshold_kph!=null){
			Parameter n = new Parameter();
			n.setName("qos_speed_threshold_kph");
			n.setValue(qos_speed_threshold_kph.toString());
			params.add(n);
		}

		return params;
	}

}
