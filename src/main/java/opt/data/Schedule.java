package opt.data;

import opt.data.control.AbstractController;
import utils.OTMUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Schedule {

	public List<AbstractController> items = new ArrayList<>();

	public int get_num_items(){
		return items.size();
	}

	public Schedule get_schedule_for_link(long link_id){
		Schedule sched = new Schedule();
		for(AbstractController ctrl : items)
			if(ctrl.actuators.values().stream().anyMatch(act->act.link.id==link_id))
				sched.items.add(ctrl);
		Collections.sort(sched.items);
		return sched;
	}

	public Schedule get_schedule_for_time_range(float start_time,float end_time){
		Schedule sched = new Schedule();
		for(AbstractController ctrl : items)
			if(ctrl.end_time>=start_time || ctrl.start_time<=end_time)
				sched.items.add(ctrl);
		Collections.sort(sched.items);
		return sched;
	}

	@Override
	public String toString() {
		String str = "start\tend\ttype\tlink(s)";
		for(AbstractController cntrl : this.items){
			String link_ids = OTMUtils.comma_format(cntrl.actuators.values().stream().map(act->act.link.id).collect(toList()));
			str = String.format("%s\n%.1f\t%.1f\t%s\t%s",str,cntrl.start_time,cntrl.end_time,cntrl.getAlgorithm(),link_ids);
		}
		return str;
	}
}
