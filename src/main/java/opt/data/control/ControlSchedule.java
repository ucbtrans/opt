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

    public Set<AbstractLink> links;
    protected LaneGroupType lgtype;

    ////////////////////////////////
    // construction
    ////////////////////////////////

    public ControlSchedule(long id,String name,Collection<AbstractLink> links, LaneGroupType lgtype, AbstractController.Type controlType) throws Exception {
        this.id = id;
        this.name = name;
        this.controlType = controlType;
        this.entries = new ArrayList<>();
        this.links = new HashSet<>(links);
        this.lgtype = lgtype;

        // controlType=HOVHOT -> lgtype=managed
        assert( controlType!=AbstractController.Type.LgRestrict || lgtype==LaneGroupType.mng );

        // all links should belong to the same scenario
        assert(!links.isEmpty());
        assert(links.stream().map(x->x.get_segment().get_scenario()).distinct().count()==1);
        this.fwyscn = links.iterator().next().get_segment().get_scenario();
    }

    public jaxb.Schd to_jaxb(){
        jaxb.Schd jsch = new jaxb.Schd();
        jsch.setId(id);
        jsch.setName(name);
        return jsch;
    }

    public Set<AbstractLink> links_to_write(){

        Set<AbstractLink> X = new HashSet<>();
        switch(controlType){
            case RampMetering:
                X.addAll(links);
                break;

            case LgRestrict:
                X = links.stream()
                        .filter(lk->lk.get_mng_lanes()>0)
                        .collect(Collectors.toSet());
                break;
        }
        return X;
    }

    public boolean ignore(Set<AbstractLink> links_to_write){

        if(links_to_write.isEmpty())
            return true;

        if(entries.isEmpty())
            return true;

        for(AbstractLink link : links_to_write)
            if(link.lgtype2lanes(lgtype)==null)
                return true;

        return false;
    }

    public jaxb.Actuator get_jaxb_actuator(Set<AbstractLink>  links_to_write){

        jaxb.Actuator jact = new jaxb.Actuator();
        long actuator_id = this.id;
        jact.setId(actuator_id);

        // set type, target
        jaxb.ActuatorTarget jtgt;
        switch(controlType){

            case RampMetering:
                jact.setType("lg_capacity");
                jtgt = new jaxb.ActuatorTarget();
                jact.setActuatorTarget(jtgt);
                jtgt.setType("lanegroups");
                AbstractLink link = links_to_write.iterator().next();
                int [] lanes = link.lgtype2lanes(lgtype);
                jtgt.setLanegroups(String.format("%d(%d#%d)",link.id,lanes[0],lanes[1]));
                break;

            case LgRestrict:
                jact.setType("lg_allowcomm");
                jtgt = new jaxb.ActuatorTarget();
                jact.setActuatorTarget(jtgt);
                jtgt.setType("lanegroups");

                String str = "";
                for(AbstractLink lk : links_to_write){
                    int [] lns = lk.lgtype2lanes(lgtype);
                    String str2 = String.format("%d(%d#%d)",lk.id,lns[0],lns[1]);
                    str += str2 + ",";
                }
                if(!str.isEmpty())
                    str = str.substring(0, str.length() - 1);
                jtgt.setLanegroups(str);

                // commodities to control
                jtgt.setCommids(OTMUtils.comma_format(fwyscn.get_commodities().keySet()));
                break;
        }

        return jact;
    }

    public jaxb.Controller to_jaxb_controller(long actuator_id){
        jaxb.Controller jcntrl = new jaxb.Controller();

        if(entries.isEmpty())
            return jcntrl;

        // controller attributes
        jcntrl.setType("schedule");
        jcntrl.setId(id);

        // target actuator
        jaxb.TargetActuators tacts = new jaxb.TargetActuators();
        tacts.setIds(String.format("%d",actuator_id));
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
            jentry.setType(cntrl.getAlgorithm().toString());

            // sensors
//            if(cntrl.get_num_sensors()>0){
//                jaxb.FeedbackSensors jsns = new jaxb.FeedbackSensors();
//                jentry.setFeedbackSensors(jsns);
//                jsns.setIds(OTMUtils.comma_format(cntrl.get_sensor_ids()));
//            }

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

    public long getId(){
        return id;
    }

    public String get_name(){
        return name;
    }

    public void set_name(String name){
        this.name=name;
    }

    public LaneGroupType get_lgtype(){
        return lgtype;
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
                        entries.add(new ScheduleEntry(0f, ControlFactory.create_controller_rmopen(fwyscn)));
                        break;
                    case LgRestrict:
                        Set<Long> all_comms = this.fwyscn.get_commodities().keySet();
                        entries.add(new ScheduleEntry(0f, ControlFactory.create_controller_hovhot(fwyscn,null,null,all_comms,null,null,null,null)));
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

    public float get_largest_start_time(){
        return entries.get(entries.size()-1).start_time;
    }

    public Set<AbstractLink> get_links(){
        return links;
    }

    public List<AbstractLink> get_ordered_links(){
        List<AbstractLink> X = new ArrayList<>();
        X.addAll(links);
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
        links.add(link);
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
        links.remove(link);
        link.remove_schedule( get_lgtype(),get_controlType());
    }

    public void remove_commodity(long comm_id){
        for(ScheduleEntry entry : entries)
            if(entry.cntrl instanceof ControllerLgRestrict)
                ((ControllerLgRestrict)entry.cntrl).remove_commodity(comm_id);
    }

    public void add_commodity(long comm_id){
        for(ScheduleEntry entry : entries)
            if(entry.cntrl instanceof ControllerLgRestrict)
                ((ControllerLgRestrict)entry.cntrl).add_commodity(comm_id);
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
