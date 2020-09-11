package opt.data.control;

import jaxb.Parameter;
import opt.data.AbstractLink;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;
import utils.OTMUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ControlSchedule implements Comparable {

    protected long id;
    protected String name;
    protected FreewayScenario fwyscn;
    protected AbstractController.Type controlType;
    protected List<ScheduleEntry> entries;
    protected AbstractActuator actuator;

    ////////////////////////////////
    // construction
    ////////////////////////////////

    public ControlSchedule(long id,String name,Collection<AbstractLink> links, LaneGroupType lgtype, AbstractController.Type controlType,long act_id) throws Exception {
        this.id = id;
        this.name = name;
        this.controlType = controlType;
        this.entries = new ArrayList<>();

        // all links should belong to the same scenario
        assert(!links.isEmpty());
        assert(links.stream().map(x->x.get_segment().get_scenario()).distinct().count()==1);
        this.fwyscn = links.iterator().next().get_segment().get_scenario();

        switch(controlType){
            case RampMetering:
                assert(links.size()==1); // ramp metering controllers refer to a single onramp
                actuator = ControlFactory.create_actuator_ramp_meter(act_id,links.iterator().next(),lgtype);
                break;
            case HOVHOT:
                actuator = ControlFactory.create_actuator_hovhot_policy(act_id,links);
                break;
        }
    }

    public jaxb.Schd to_jaxb(){
        jaxb.Schd jsch = new jaxb.Schd();
        jsch.setId(id);
        jsch.setName(name);
        return jsch;
    }

    public jaxb.Controller to_jaxb_controller(){
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
            if(entry.cntrl.getDt()!=null && !entry.cntrl.getDt().isInfinite())
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

    public String get_name(){
        return name;
    }

    public void set_name(String name){
        this.name=name;
    }

    public LaneGroupType get_lgtype(){
        return actuator.lgtype;
    }

    public AbstractController.Type get_controlType(){
        return controlType;
    }

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

        // If there is no controller starting at midnight, then add default
        if(!entries.stream().anyMatch(e->e.start_time==0f)) {
            try {
                switch(controlType){
                    case RampMetering:
                        entries.add(new ScheduleEntry(0f, ControlFactory.create_controller_rmopen(fwyscn,null)));
                        break;
                    case HOVHOT:
                        entries.add(new ScheduleEntry(0f, ControlFactory.create_controller_hovhot(fwyscn,null,null,null,null,null,null,null,null)));
                        break;
                }
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

    public Set<AbstractLink> get_links(){
        return actuator.links;
    }

    public List<AbstractLink> get_ordered_links(){
        List<AbstractLink> X = new ArrayList<>();
        X.addAll(actuator.links);
        Collections.sort(X);
        return X;
    }

    // return true if successful, false otherwise
    public boolean add_link(AbstractLink link){

        // try to and schedule to link
        try {
            link.add_schedule(this);
        } catch (Exception e) {
            return false;
        }

        // if it works, add link to actuator
        actuator.links.add(link);
        return true;

    }


    // return true if successful, false otherwise
    public boolean add_links(Collection<AbstractLink> links){
        try {
            links.forEach(link->add_link(link));
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public void remove_link(AbstractLink link){
        this.actuator.links.remove(link);
        link.remove_schedule( get_lgtype(),get_controlType());
    }

    ////////////////////////////////
    // private
    ////////////////////////////////

    private void update_end_times(){
        for(int i=0;i<entries.size()-1;i++)
            entries.get(i).end_time = entries.get(i+1).start_time;
        entries.get(entries.size()-1).end_time = Float.POSITIVE_INFINITY;
    }

    @Override
    public int compareTo(Object o) {
        ControlSchedule that = (ControlSchedule) o;
        return this.name.compareTo(that.name);
    }
}
