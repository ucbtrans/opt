package opt.data.control;

import error.OTMException;
import jaxb.Controller;
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

	public List<TODEntry> entries = new ArrayList<>();

	public ControllerRampMeterTOD(long id,float dt, float start_time, Float end_time, Collection<AbstractActuator> xactuators) throws OTMException {
		super(id,dt,start_time,end_time,"tod",xactuators);

		// CHECK
		if(actuators.values().stream().anyMatch(act -> !(act instanceof opt.data.control.ActuatorRampMeter)))
			throw new OTMException("Found an TOD controller on a non-ramp meter actuator");
	}

	public ControllerRampMeterTOD(Controller j, Scenario scn) throws OTMException {
		super(j, scn);

		// TODO READ ENTRIES

		// CHECK
		for(TODEntry entry : entries){
			if(entry.time<0)
				throw new OTMException("entry.time<0");
			if(entry.rate_vph<0)
				throw new OTMException("entry.rate_vph<0");
		}

		if(actuators.values().stream().anyMatch(act -> !(act instanceof opt.data.control.ActuatorRampMeter)))
			throw new OTMException("Found a TOD controller on a non-ramp meter actuator");

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
