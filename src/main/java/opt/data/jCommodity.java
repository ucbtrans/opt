package opt.data;

public class jCommodity {

    protected final long id;
    protected String name;

    public jCommodity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("id=%d, name=%s",id,name);
    }
}
