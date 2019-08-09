package opt.data;

import jaxb.Roadparam;
import profiles.Profile1D;

public class LinkFreeway extends LinkFreewayOrConnector {

    public LinkFreeway(jaxb.Link link, Roadparam rp) {
        super(link, Type.freeway, rp);
    }

    public LinkFreeway(Long id, Long start_node_id, Long end_node_id, Integer full_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        super(id, Type.freeway, start_node_id, end_node_id, full_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph, mysegment);
    }

    @Override
    public void set_split(Long comm_id, Profile1D profile) throws Exception {
        this.splits.put(comm_id,profile);
    }

//    public Segment insert_upstrm_mainline_segment(){
//
//        // create new upstream node
//        Node existing_node = fwy_scenario.scenario.nodes.get(fwy().start_node_id);
//        Node new_node = new Node(fwy_scenario.new_node_id());
//
//        // connect upstream links to new node
//        connect_segment_to_downstream_node(get_upstrm_fwy_segment(),new_node);
//
//        // create new freeway link
//        AbstractLink new_link = new AbstractLink(
//                fwy_scenario.new_link_id(),
//                AbstractLink.Type.freeway,
//                new_node.id,
//                existing_node.id,
//                get_mixed_lanes(),
//                get_length_meters(),
//                get_capacity_vphpl(),
//                get_jam_density_vpkpl(),
//                get_freespeed_kph(),null);
//
//        // connect new link to start and end nodes
//        existing_node.in_links.add(new_link.id);
//        new_node.out_links.add(new_link.id);
//
//        // add link to scenario
//        fwy_scenario.scenario.links.put(new_link.id,new_link);
//
//        // create new segment
//        Segment newseg = create_new_segment(new_link);
//        newseg.segment_fwy_dn_id = this.id;
//        newseg.segment_fwy_up_id = this.segment_fwy_up_id;
//        this.segment_fwy_up_id = newseg.id;
//
//        // add to fwy scenario
//        fwy_scenario.segments.put(newseg.id,newseg);
//
//        return newseg;
//    }



//    public Segment insert_dnstrm_mainline_segment(){
//
//        // existing node and new node
//        Node existing_node = fwy_scenario.scenario.nodes.get(fwy().end_node_id);
//        Node new_node = new Node(fwy_scenario.new_node_id());
//
//        // connect downstream links to new node
//        connect_segment_to_upstream_node(get_dnstrm_fwy_segment(),new_node);
//
//        // create new freeway link
//        AbstractLink new_link = new AbstractLink(
//                fwy_scenario.new_link_id(),
//                AbstractLink.Type.freeway,
//                existing_node.id,
//                new_node.id,
//                get_mixed_lanes(),
//                get_length_meters(),
//                get_capacity_vphpl(),
//                get_jam_density_vpkpl(),
//                get_freespeed_kph(),null);
//
//        // connect new link to start and end nodes
//        existing_node.out_links.add(new_link.id);
//        new_node.in_links.add(new_link.id);
//
//        // add link to scenario
//        fwy_scenario.scenario.links.put(new_link.id,new_link);
//
//        // create new segment
//        Segment newseg = create_new_segment(new_link);
//        newseg.segment_fwy_dn_id = this.segment_fwy_dn_id;
//        this.segment_fwy_dn_id = newseg.id;
//
//        // add to fwy scenario
//        fwy_scenario.segments.put(newseg.id,newseg);
//
//        return newseg;
//    }
//

}
