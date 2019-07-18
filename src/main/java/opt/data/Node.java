package opt.data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Node {
    protected long id;

    protected Set<AbstractLink> in_links = new HashSet<>();
    protected Set<AbstractLink> out_links = new HashSet<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    /////////////////////////////////////
    // private
    /////////////////////////////////////

    public Set<Long> get_in_link_ids(){
        return in_links.stream().map(link->link.id).collect(Collectors.toSet());
    }

    public Set<Long> get_out_link_ids(){
        return out_links.stream().map(link->link.id).collect(Collectors.toSet());
    }

}
