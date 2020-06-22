package opt.data.control;

public class ScheduleEntry  implements Comparable<ScheduleEntry>{
    protected float start_time;
    protected float end_time;
    protected AbstractController cntrl;
    public ScheduleEntry( float start_time, AbstractController cntrl){
        this.start_time = start_time;
        this.cntrl = cntrl;
    }
    public float get_start_time(){
        return start_time;
    }
    public float get_end_time(){
        return end_time;
    }
    public AbstractController get_cntrl(){
        return cntrl;
    }
    @Override
    public int compareTo(ScheduleEntry that) {
        return Float.compare(this.start_time,that.start_time);
    }
}