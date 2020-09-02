package opt.data.control;

import jaxb.Parameter;
import opt.data.AbstractLink;
import opt.data.ControlFactory;
import opt.data.LaneGroupType;
import utils.OTMUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ControlSchedule {

    protected long id;
    protected AbstractLink link;
    protected AbstractController.Type controlType;
    protected List<ScheduleEntry> entries;
    protected AbstractActuator actuator;

    ////////////////////////////////
    // construction
    ////////////////////////////////

    public ControlSchedule(long id,AbstractLink link, LaneGroupType lgtype, AbstractController.Type controlType,long act_id){
        this.id = id;
        this.link = link;
        this.controlType = controlType;
        this.entries = new ArrayList<>();

        switch(controlType){
            case RampMetering:
                actuator = ControlFactory.create_actuator_ramp_meter(act_id,link,lgtype);
                break;
            case HOTpolicy:
                actuator = ControlFactory.create_actuator_hot_policy(act_id,link,lgtype);
                break;
            case HOVpolicy:
                actuator = ControlFactory.create_actuator_hov_policy(act_id,link,lgtype);
                break;
        }
    }

    public jaxb.Controller to_jaxb(){
        jaxb.Controller jcntrl = new jaxb.Controller();

        if(entries.isEmpty())
            return jcntrl;

        // controller attributes
        jcntrl.setType("schedule");
        jcntrl.setId(id);

        // target actuator
        jaxb.TargetActuators tacts = new jaxb.TargetActuators();
        tacts.setIds(String.format("%d",actuator.id));
        jcntrl.setTargetActuators(tacts);

        // schedule
        jaxb.Schedule jsch = new jaxb.Schedule();
        jcntrl.setSchedule(jsch);
        for(ScheduleEntry entry : entries){
            float start_time = entry.get_start_time();
            float end_time = entry.get_end_time();

            jaxb.Entry jentry = new jaxb.Entry();
            jsch.getEntry().add(jentry);

            // dt
            if(!entry.cntrl.getDt().isInfinite())
                jentry.setDt(entry.cntrl.getDt());

            // start time and end time
            jentry.setStartTime(start_time);
            if(Float.isFinite(end_time))
                jentry.setEndTime(end_time);

            // controller
            AbstractController cntrl = entry.get_cntrl();
            jentry.setType(cntrl.algorithm.toString());

            // sensors
            if(!cntrl.get_sensors().isEmpty()){
                jaxb.FeedbackSensors jsns = new jaxb.FeedbackSensors();
                jentry.setFeedbackSensors(jsns);
                jsns.setIds(OTMUtils.comma_format(cntrl.get_sensors().keySet()));
            }

            // controller parameters
            Collection<Parameter> jparamslist = cntrl.jaxb_parameters();
            if(!jparamslist.isEmpty()){
                jaxb.Parameters jparams = new jaxb.Parameters();
                jentry.setParameters(jparams);
                jparams.getParameter().addAll(jparamslist);
            }

        }

        return jcntrl;
    }

    ////////////////////////////////
    // API
    ////////////////////////////////

    public void update(float start_time, AbstractController cntrl) {

        if(cntrl!=null){
            boolean have_controller = entries.stream().anyMatch(e->e.get_cntrl()==cntrl);

            if(!have_controller){
                // remove entries with coinciding start time
                entries = entries.stream().filter(e->e.start_time!=start_time).collect(Collectors.toList());
                entries.add(new ScheduleEntry(start_time,cntrl));
            } else {
                ScheduleEntry entry = entries.stream().filter(e->e.get_cntrl()==cntrl).findFirst().get();
                entry.start_time = start_time;
            }
        }

        // If there is no controller starting at midnight, then add Open
        if(!entries.stream().anyMatch(e->e.start_time==0f)) {
            try {
                entries.add(new ScheduleEntry(0f,
                        ControlFactory.create_controller_rmopen(link.get_segment().get_scenario(),null)));
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

        update(0f,null);
    }

    public AbstractActuator get_actuator(){
        return actuator;
    }

    public float get_largest_start_time(){
        return entries.get(entries.size()-1).start_time;
    }

    public Set<Sensor> get_sensors(){
        return entries.stream().flatMap(e->e.get_cntrl().get_sensors().values().stream()).collect(Collectors.toSet());
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
