package opt.data;

import opt.data.control.AbstractController;
import utils.OTMUtils;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class Schedule {

	protected List<AbstractController> items = new ArrayList<>();

	protected void add_item(AbstractController newcontroller) throws Exception {

		Set<Long> actuator_ids = newcontroller.get_actuator_ids();

		// find controllers that address the same actuators as newcontroller
		Set<AbstractController> intersecting_controllers = new HashSet<>();
		for(AbstractController c : items){
			Set<Long> common_actuators = c.get_actuator_ids();
			common_actuators.retainAll(actuator_ids);
			if(!common_actuators.isEmpty())
				intersecting_controllers.add(c);
		}

		// of the intersecting controllers, find whether there is one whose time period intersects
		if( intersecting_controllers.stream()
				.anyMatch(i-> i.getEndTime() > newcontroller.getStartTime() && i.getStartTime() < newcontroller.getEndTime()) )
			throw new Exception("There is at least one controller whose time period intersects with this one.");

		// otherwise add the controller to the schedule
		items.add(newcontroller);

		// sort
		Collections.sort(items);
	}

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

	public boolean delete_controller(AbstractController c){
		return items.remove(c);
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
