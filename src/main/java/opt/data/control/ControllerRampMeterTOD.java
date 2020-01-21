package opt.data.control;

import jaxb.Actuator;
import jaxb.Controller;
import jaxb.Sensor;
import opt.data.ControlFactory;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;
import opt.data.Scenario;

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

	public ControllerRampMeterTOD(Controller j, Map<Long, Actuator> a, Map<Long, Sensor> s, Scenario scn) throws Exception {
		super(j, a, s, scn);
	}

	// factory
	public ControllerRampMeterTOD(FreewayScenario scn, float dt, float start_time, Float end_time, long ramp_link_id, LaneGroupType lgtype) throws Exception {
		super(scn.new_controller_id(),dt,start_time,end_time,"tod",false,Float.NaN,Float.NaN);

		// ramp meter actuator
		add_actuator(ControlFactory.create_ramp_meter(scn,ramp_link_id,lgtype,this));
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
