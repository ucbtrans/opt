package opt.data;

public class Commodity {

    protected final long id;
    protected String name;

    public Commodity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("id=%d, name=%s",id,name);
    }
}
