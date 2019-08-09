package opt.data;

import java.util.Objects;

public class Commodity {

    protected final long id;
    protected String name;
    protected double weight;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Commodity(long id, String name) {
        this.id = id;
        this.name = name;
        this.weight = 1.0;
    }
    
    public Commodity(long id, String name, double weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    public Commodity clone(){
        return new Commodity(id,name,weight);
    }

    /////////////////////////////////////
    // getters
    /////////////////////////////////////

    public String get_name(){
        return name;
    }
    
    public double get_weight(){
        return weight;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        return String.format("id=%d, name=%s, weight=%s", id, name, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commodity commodity = (Commodity) o;
        return id == commodity.id &&  name.equals(commodity.name) && weight == commodity.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, weight);
    }

}
