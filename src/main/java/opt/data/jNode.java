package opt.data;

import java.util.HashSet;
import java.util.Set;

public class jNode {
    public long id;
    public Set<jLink> in_links = new HashSet<>();
    public Set<jLink> out_links = new HashSet<>();
    public jNode(jaxb.Node jnode){
        this.id = jnode.getId();
    }

    public jNode(long id){
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%d",id);
    }

}
