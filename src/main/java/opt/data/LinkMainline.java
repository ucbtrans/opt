package opt.data;

public class LinkMainline extends AbstractLink {


    public LinkMainline(jaxb.Link link, jaxb.Roadparam rp){
        super(link,rp);
    }

    public LinkMainline(Long id, Long start_node_id, Long end_node_id, Integer full_lanes, Float length, Boolean is_source, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        super(id, start_node_id, end_node_id, full_lanes, length, is_source, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
    }

}
