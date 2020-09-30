package opt.data.control;

public class Sensor {

    public long id;
    public long link_id;
    public float offset;

    public Sensor(long link_id, float offset) {
        this.link_id = link_id;
        this.offset = offset;
    }

    public jaxb.Sensor to_jaxb(){
        jaxb.Sensor j = new jaxb.Sensor();
        j.setId(id);
        j.setPosition(offset);
        j.setLinkId(link_id);
        j.setType("fixed");
        return j;
    }
}
