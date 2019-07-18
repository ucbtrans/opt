package opt.data;

import java.util.Objects;

public class Commodity {

    protected final long id;
    protected String name;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Commodity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Commodity deep_copy(){
        return new Commodity(this.id,this.name);
    }

    /////////////////////////////////////
    // getters
    /////////////////////////////////////

    public String get_name(){
        return name;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        return String.format("id=%d, name=%s",id,name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commodity commodity = (Commodity) o;
        return id == commodity.id &&
                name.equals(commodity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
