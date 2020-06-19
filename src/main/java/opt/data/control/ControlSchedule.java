package opt.data.control;

import opt.data.AbstractLink;
import opt.data.ControlFactory;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ControlSchedule {

    protected AbstractLink link;
    protected AbstractController.Type controlType;
    protected List<ScheduleEntry> entries;
    protected AbstractActuator actuator;

    ////////////////////////////////
    // construction
    ////////////////////////////////

    public ControlSchedule(AbstractLink link, LaneGroupType lgtype, AbstractController.Type controlType){
        this.link = link;
        this.controlType = controlType;
        this.entries = new ArrayList<>();

        FreewayScenario fwyscn = link.get_segment().get_scenario();
        switch(controlType){
            case RampMetering:
                actuator = ControlFactory.create_actuator_ramp_meter(fwyscn,null,link.get_id(),lgtype);
                break;
            case HOTpolicy:
                actuator = ControlFactory.create_actuator_hot_policy(fwyscn,null,link.get_id(),lgtype);
                break;
            case HOVpolicy:
                actuator = ControlFactory.create_actuator_hov_policy(fwyscn,null,link.get_id(),lgtype);
                break;
        }
    }

    ////////////////////////////////
    // API
    ////////////////////////////////

    public void update(float start_time, AbstractController cntrl) {
        boolean have_controller = entries.stream().anyMatch(e->e.get_cntrl()==cntrl);

        if(!have_controller){
            // remove entries with coinciding start time
            entries = entries.stream().filter(e->e.start_time!=start_time).collect(Collectors.toList());
            entries.add(new ScheduleEntry(start_time,cntrl));
        } else {
            ScheduleEntry entry = entries.stream().filter(e->e.get_cntrl()==cntrl).findFirst().get();
            entry.start_time = start_time;
        }

        // If there is no controller starting at midnight, then add Open
        if(!entries.stream().anyMatch(e->e.start_time==0f)) {
            try {
                entries.add(new ScheduleEntry(0f,
                        ControlFactory.create_controller_open(link.get_segment().get_scenario(), null, null, link.get_id())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        Collections.sort(entries);
        update_end_times();
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

    ////////////////////////////////
    // private
    ////////////////////////////////

    private void update_end_times(){
        for(int i=0;i<entries.size()-1;i++)
            entries.get(i).end_time = entries.get(i+1).start_time;
        entries.get(entries.size()-1).end_time = Float.POSITIVE_INFINITY;
    }

}
