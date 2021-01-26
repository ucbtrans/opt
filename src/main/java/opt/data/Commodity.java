package opt.data;

public class Commodity {

    public enum EmissionsClass {car, truck, bus}

    protected final long id;
    protected String name;
    protected float pvequiv;    // size factor as compared to a passenger vehicle.
    protected EmissionsClass eclass;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Commodity(long id, String name, float pvequiv, EmissionsClass eclass) {
        this.id = id;
        this.name = name;
        this.pvequiv = pvequiv;
        this.eclass = eclass;
    }

    public Commodity clone(){
        return new Commodity(id,name, pvequiv, eclass);
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

    public EmissionsClass get_eclass(){
        return eclass;
    }

    public void set_class(EmissionsClass eclass){
        if(eclass==null)
            return;
        this.eclass = eclass;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        return String.format("id=%d, name=%s, weight=%s", id, name, pvequiv);
    }

}
