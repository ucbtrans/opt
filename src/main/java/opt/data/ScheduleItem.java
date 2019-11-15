package opt.data;

import opt.data.control.AbstractController;

public class ScheduleItem implements Comparable {

	protected float start_time;
	protected float end_time;
	protected AbstractController controller;

	public ScheduleItem(float start_time,float end_time,AbstractController controller) {
		this.start_time = start_time;
	}

	@Override
	public int compareTo(Object o) {
		ScheduleItem that = (ScheduleItem) o;
		if(this.start_time<that.start_time)
			return -1;
		else if(this.start_time>that.start_time)
			return 1;
		else return 0;
	}
}
