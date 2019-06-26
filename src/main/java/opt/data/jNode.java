package opt.data;

import java.util.HashSet;
import java.util.Set;

public class jNode {
    protected long id;
    protected Set<jLink> in_links = new HashSet<>();
    protected Set<jLink> out_links = new HashSet<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    protected jNode(jaxb.Node jnode){
        this.id = jnode.getId();
    }

    protected jNode(long id){
        this.id = id;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        return String.format("%d",id);
    }

}
