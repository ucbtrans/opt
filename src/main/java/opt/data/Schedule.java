package opt.data;

import opt.data.control.AbstractActuator;
import opt.data.control.AbstractController;
import utils.OTMUtils;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class Schedule {

	public final FreewayScenario my_fwy_scenario;
	public List<AbstractController> items;

	public Schedule(FreewayScenario my_fwy_scenario) {
		this.my_fwy_scenario = my_fwy_scenario;
		this.items = new ArrayList<>();
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

	///////////////////
	// API
	///////////////////

	public int get_num_items(){
		return items.size();
	}

	public Schedule get_schedule_for_link(long link_id){
		Schedule sched = new Schedule(my_fwy_scenario);
		for(AbstractController ctrl : items)
			if(ctrl.get_actuators().values().stream().anyMatch(act->act.link_id==link_id))
				sched.items.add(ctrl);
		Collections.sort(sched.items);
		return sched;
	}

	public Schedule get_schedule_for_time_range(float start_time,float end_time){
		Schedule sched = new Schedule(my_fwy_scenario);
		for(AbstractController ctrl : items)
			if(ctrl.getEndTime()>=start_time || ctrl.getStartTime()<=end_time)
				sched.items.add(ctrl);
		Collections.sort(sched.items);
		return sched;
	}

	public boolean delete_controller(AbstractController c){

		if(!items.contains(c))
			return false;

//		// unlink actuators from links
//		for(AbstractActuator act : c.get_actuators().values()) {
//			AbstractLink link = my_fwy_scenario.scenario.links.get(act.link_id);
//			link.actuator.remove(act.lgtype);
//		}

		return items.remove(c);
	}

	public void add_item(AbstractController newcontroller) throws Exception {

		// find controllers that simultaneously address the same actuators as new controller
		for (AbstractController c : items)
			if (interval_overlaps_with(newcontroller,c))
				for (AbstractActuator act : c.get_actuators().values())
					for (AbstractActuator new_act : newcontroller.get_actuators().values())
						if (same_lanegroup_as(new_act,act))
							throw new Exception(String.format("The new controller has an actuator on link %d, lane group %s, where another controller is already present.",act.link_id,act.lgtype));

		// add the controller to the schedule
		items.add(newcontroller);

		// sort
		Collections.sort(items);
	}

	public void clear(){
		items.clear();
	}

	private static boolean interval_overlaps_with(AbstractController a, AbstractController b){
		return b.getStartTime()<a.getEndTime() && a.getStartTime()<b.getEndTime();
	}

	private static boolean same_lanegroup_as(AbstractActuator a,AbstractActuator b){
		return a.lgtype==b.lgtype && a.link_id==b.link_id;
	}
}
