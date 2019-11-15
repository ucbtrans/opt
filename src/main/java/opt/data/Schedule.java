package opt.data;

import opt.data.control.AbstractController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Schedule {

	protected List<ScheduleItem> items = new ArrayList<>();

	public void add_item(float start_time, float end_time, AbstractController controller){
		items.add(new ScheduleItem(start_time,end_time,controller));
		Collections.sort(items);
	}

	public void clear(){
		items.clear();
	}

	public Schedule get_schedule_for_link(long link_id){
		Schedule sched = new Schedule();
		for(ScheduleItem item : items)
			if(item.controller.actuators.values().stream().anyMatch(act->act.link.id==link_id))
				sched.items.add(item);
		Collections.sort(sched.items);
		return sched;
	}

	public Schedule get_schedule_for_time_range(float start_time,float end_time){
		Schedule sched = new Schedule();
		for(ScheduleItem item : items)
			if(item.end_time>=start_time || item.start_time<=end_time)
				sched.items.add(item);
		Collections.sort(sched.items);
		return sched;
	}

}
