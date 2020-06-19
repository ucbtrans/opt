package opt.data.control;

import opt.data.AbstractLink;
import opt.data.LaneGroupType;
import opt.data.control.AbstractActuator;
import opt.data.control.AbstractController;
import opt.data.control.ScheduleEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControlSchedule {

    protected AbstractLink link;
    protected LaneGroupType lgtype;
    protected List<ScheduleEntry> entries;
    protected AbstractActuator actuator;

    ////////////////////////////////
    // construction
    ////////////////////////////////

    public ControlSchedule(AbstractLink link, LaneGroupType lgtype, AbstractController.Type cntrl_type){
        this.link = link;
        this.lgtype = lgtype;
        this.entries = new ArrayList<>();

        // TODO CREATE THE ACTUATOR ACCORDIG TO  cntrl_type
    }

    ////////////////////////////////
    // API
    ////////////////////////////////

    public void add_entry(float start_time, AbstractController cntrl) throws Exception {
        // check the start time is not present
        if( entries.stream().anyMatch(e->e.start_time==start_time) )
            throw new Exception("Repeated start time");
        entries.add(new ScheduleEntry(start_time,cntrl));
        Collections.sort(entries);

        // set end time
        for(int i=0;i<entries.size()-1;i++)
            entries.get(i).end_time = entries.get(i+1).start_time;
        entries.get(entries.size()-1).end_time = Float.POSITIVE_INFINITY;
    }

    public int num_entries(){
        return entries.size();
    }

    public List<ScheduleEntry> get_entries(){
        return entries;
    }

    public void delete_entry(int index){
        entries.remove(index);
    }

    public AbstractActuator get_actuator(){
        return actuator;
    }

    public float get_largest_start_time(){
        return entries.get(entries.size()-1).start_time;
    }



}
