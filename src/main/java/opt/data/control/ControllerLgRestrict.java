package opt.data.control;

import jaxb.Parameter;
import opt.UserSettings;
import opt.data.FreewayScenario;
import utils.OTMUtils;

import java.util.*;

public class ControllerLgRestrict extends AbstractController {

	public enum Permission { Free, Banned, Tolled }
	protected Permission permission;
	protected Map<Long,Permission> comm2permission;

	protected Double toll_coef;
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

	public ControllerLgRestrict(FreewayScenario scn, Set<Long> tolled_comms, Set<Long> disallowed_comms, Set<Long> free_comms, Float dt, Double toll_coef, int [][] vphpl_to_cents_table, Double qos_speed_threshold_kph) {
		super(Type.LgRestrict,dt, control.AbstractController.Algorithm.lg_restrict);

		this.comm2permission = new HashMap<>();

		if(tolled_comms==null)
			tolled_comms = new HashSet<>();
		if(disallowed_comms==null)
			disallowed_comms = new HashSet<>();
		if(free_comms==null)
			free_comms = new HashSet<>();

		// check that together they are a partition of the commodities
		assert(Collections.disjoint(tolled_comms,disallowed_comms));
		assert(Collections.disjoint(tolled_comms,free_comms));
		assert(Collections.disjoint(disallowed_comms,free_comms));
		Set<Long> all_comms = new HashSet<>();
		all_comms.addAll(tolled_comms);
		all_comms.addAll(disallowed_comms);
		all_comms.addAll(free_comms);
		assert(all_comms.containsAll(scn.get_commodities().keySet()));

		for(Long cid : tolled_comms)
			comm2permission.put(cid,Permission.Tolled);
		for(Long cid : disallowed_comms)
			comm2permission.put(cid,Permission.Banned);
		for(Long cid : free_comms)
			comm2permission.put(cid,Permission.Free);

		refresh_type();

//		this.a0 = a0==null ? UserSettings.defaultLaneChoice_keep : a0;
//		this.a1 = a1==null ? UserSettings.defaultLaneChoice_rhovpmplane : a1;
		this.toll_coef = toll_coef ==null ? UserSettings.defaultLaneChoice_tollcents : toll_coef;

		if(vphpl_to_cents_table==null){
			this.vphpl_to_cents_table = new int[1][2];
			this.vphpl_to_cents_table[0][0] = 0;
			this.vphpl_to_cents_table[0][1] = 0;
		} else
			this.vphpl_to_cents_table = vphpl_to_cents_table;
		this.qos_speed_threshold_kph = qos_speed_threshold_kph==null ? UserSettings.defaultQosSpeedThresholdKph : qos_speed_threshold_kph;
	}

	@Override
	public Set<Sensor> get_sensors() {
		return new HashSet<>();
	}

	public void remove_commodity(long comm_id){
		if(comm2permission.containsKey(comm_id))
			comm2permission.remove(comm_id);
		refresh_type();
	}

	public void add_commodity(long comm_id){
		if(!comm2permission.containsKey(comm_id))
			comm2permission.put(comm_id,Permission.Free);
		refresh_type();
	}

	public Double get_toll_coef() {
		return toll_coef;
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

	public void set_toll_coef(Double x) {
		toll_coef = x;
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

	@Override
	public control.AbstractController.Algorithm getAlgorithm() {
		if(comm2permission.values().stream().anyMatch(x->x==Permission.Tolled))
			return control.AbstractController.Algorithm.lg_toll;
		else
			return control.AbstractController.Algorithm.lg_restrict;
	}

	////////////////////////////////
	// AbstractController
	////////////////////////////////

	@Override
	public Collection<Parameter> jaxb_parameters() {

		Set<Parameter> params = new HashSet<>();
		Set<Long> banned_comms = new HashSet<>();
		Set<Long> tolled_comms = new HashSet<>();
		Set<Long> free_comms = new HashSet<>();
		for(Map.Entry<Long,Permission> e : comm2permission.entrySet()){
			switch(e.getValue()){
				case Tolled:
					tolled_comms.add(e.getKey());
					break;
				case Banned:
					banned_comms.add(e.getKey());
					break;
				case Free:
					free_comms.add(e.getKey());
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
		if(!tolled_comms.isEmpty()) {

			Parameter n0 = new Parameter();
			n0.setName("tolled_comms");
			n0.setValue(OTMUtils.comma_format(tolled_comms));
			params.add(n0);

			if (toll_coef != null) {
				Parameter n = new Parameter();
				n.setName("toll_coef");
				n.setValue(toll_coef.toString());
				params.add(n);
			}

			if (vphpl_to_cents_table != null) {
				Parameter n = new Parameter();
				n.setName("vplph_to_cents_table");
				n.setValue(OTMUtils.write_int_table(vphpl_to_cents_table));
				params.add(n);
			}

			if (qos_speed_threshold_kph != null) {
				Parameter n = new Parameter();
				n.setName("qos_speed_threshold_kph");
				n.setValue(qos_speed_threshold_kph.toString());
				n.setValue(qos_speed_threshold_kph.toString());
				params.add(n);
			}

		}

		return params;
	}

}
