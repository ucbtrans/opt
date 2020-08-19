package opt.data;

import java.util.Objects;

public class Commodity {

    protected final long id;
    protected String name;
    protected float pvequiv;    // size factor as compared to a passenger vehicle.

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Commodity(long id, String name, float pvequiv) {
        this.id = id;
        this.name = name;
        this.pvequiv = pvequiv;
    }

    public Commodity clone(){
        return new Commodity(id,name, pvequiv);
    }

    /////////////////////////////////////
    // getters
    /////////////////////////////////////

    public long getId() {
        return id;
    }
    
    public String get_name(){
        return name;
    }

    public void set_name(String name){
        this.name = name;
    }

    public double get_pvequiv(){
        return pvequiv;
    }

    public void set_pvequiv(float x){
        if(x>0f)
            pvequiv = x;
    }
    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        return String.format("id=%d, name=%s, weight=%s", id, name, pvequiv);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Commodity commodity = (Commodity) o;
//        return id == commodity.id &&  name.equals(commodity.name) && pvequiv == commodity.pvequiv;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, name, pvequiv);
//    }

}
