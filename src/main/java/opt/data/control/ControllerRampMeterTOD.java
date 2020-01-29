package opt.data.control;

import opt.data.ControlFactory;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;

import java.util.*;

public class ControllerRampMeterTOD extends AbstractControllerRampMeter {

	class TODEntry implements Comparable<TODEntry>{
		public float time;
		public float rate_vph;
		public TODEntry(float time,float rate_vph){
			this.time = time;
			this.rate_vph = rate_vph;
		}

		@Override
		public int compareTo(TODEntry that) {
			return Float.compare(this.time,that.time);
		}
	}

	protected List<TODEntry> entries = new ArrayList<>();

	public ControllerRampMeterTOD(FreewayScenario scn, float dt, float start_time, Float end_time, boolean has_queue_control, float min_rate_vph, float max_rate_vph,Long act_id, long ramp_link_id, LaneGroupType lgtype) throws Exception {
		super(scn.new_controller_id(),dt,start_time,end_time,control.AbstractController.Algorithm.tod,has_queue_control,min_rate_vph,max_rate_vph);

		// ramp meter actuator
		add_actuator(ControlFactory.create_ramp_meter(scn,act_id,ramp_link_id,lgtype,this));
	}

	/////////////////////////
	// API
	/////////////////////////

	public void clear_schedule(){
		entries.clear();
	}

	public void add_entry(float time,float rate_vph){
		entries.add(new TODEntry(time,rate_vph));
	}

	public void remove_entry(float time,float rate_vph) {
		Iterator<TODEntry> it = entries.iterator();
		while(it.hasNext()){
			TODEntry e = it.next();
			if(e.time==time && e.rate_vph==rate_vph)
				entries.remove(e);
		}
	}

	public List<TODEntry> get_entries(){
		Collections.sort(entries);
		return entries;
	}

}
