package opt.data;

import java.util.HashSet;
import java.util.Set;

public class Node {
    protected long id;
    protected Set<Link> in_links = new HashSet<>();
    protected Set<Link> out_links = new HashSet<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    protected Node(jaxb.Node jnode){
        this.id = jnode.getId();
    }

    protected Node(long id){
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
