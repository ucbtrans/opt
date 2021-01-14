package opt.data.event;

public abstract class AbstractEvent {

    public final long id;
    public final String type;
    public float timestamp;
    public String name;

    public AbstractEvent(long id, String type, float timestamp, String name) {
        this.id = id;
        this.type = type;
        this.timestamp = timestamp;
        this.name = name;
    }

    public jaxb.Event to_jaxb(){
        jaxb.Event jevent = new jaxb.Event();
        jevent.setId(id);
        jevent.setType(type);
        jevent.setTimestamp(timestamp);
        jevent.setName(name);
        return jevent;
    }

}
