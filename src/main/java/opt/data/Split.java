package opt.data;

import java.util.Objects;

public class Split {

    protected long commodity_id;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Split(long comm_id){
        this.commodity_id = comm_id;
    }

    public Split deep_copy(){
        return new Split(commodity_id);
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Split split = (Split) o;
        return commodity_id == split.commodity_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commodity_id);
    }
}
