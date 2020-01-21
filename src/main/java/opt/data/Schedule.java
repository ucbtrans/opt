package opt.data;

import opt.data.control.AbstractController;
import utils.OTMUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Schedule {

	public List<AbstractController> items = new ArrayList<>();

	///////////////////
	// API
	///////////////////

	public int get_num_items(){
		return items.size();
	}

	public Schedule get_schedule_for_link(long link_id){
		Schedule sched = new Schedule();
		for(AbstractController ctrl : items)
			if(ctrl.get_actuators().values().stream().anyMatch(act->act.link_id==link_id))
				sched.items.add(ctrl);
		Collections.sort(sched.items);
		return sched;
	}

	public Schedule get_schedule_for_time_range(float start_time,float end_time){
		Schedule sched = new Schedule();
		for(AbstractController ctrl : items)
			if(ctrl.getEndTime()>=start_time || ctrl.getStartTime()<=end_time)
				sched.items.add(ctrl);
		Collections.sort(sched.items);
		return sched;
	}

	@Override
	public String toString() {
		String str = "start\tend\ttype\tlink(s)";
		for(AbstractController cntrl : this.items){
			String link_ids = OTMUtils.comma_format(cntrl.get_actuators().values().stream().map(act->act.link_id).collect(toList()));
			str = String.format("%s\n%.1f\t%.1f\t%s\t%s",str,cntrl.getStartTime(),cntrl.getEndTime(),cntrl.getAlgorithm(),link_ids);
		}
		return str;
	}
}
