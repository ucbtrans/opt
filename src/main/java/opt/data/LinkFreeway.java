package opt.data;

public class LinkFreeway extends AbstractLink {

    public LinkFreeway(jaxb.Link link, jaxb.Roadparam rp){
        super(link,rp);
    }

    public LinkFreeway(Long id, Long start_node_id, Long end_node_id, Integer full_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        super(id, start_node_id, end_node_id, full_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph,mysegment);
    }

}
