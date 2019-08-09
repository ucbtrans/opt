package opt.data;

import jaxb.Roadparam;
import profiles.Profile1D;

import java.util.HashMap;
import java.util.Map;

public class LinkConnector extends LinkFreewayOrConnector {

    protected Map<Long, Profile1D> fwy_demands = new HashMap<>();     // commodity -> Profile1D

    public LinkConnector(jaxb.Link link, Roadparam rp) {
        super(link, AbstractLink.Type.freeway, rp);
    }

    public LinkConnector(Long id, Long start_node_id, Long end_node_id, Integer full_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        super(id, AbstractLink.Type.freeway, start_node_id, end_node_id, full_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph, mysegment);
    }

//    public boolean connect_dnstrm_onramp(AbstractLink onramp) throws Exception {
//
//        // checks
//        if(!fwy().type.equals(AbstractLink.Type.connector))
//            throw new Exception("This method can only be called on connector segments");
//        if(segment_fwy_dn_id!=null)
//            throw new Exception("This segment is already connected downstream");
//
//
//        Segment dnstrm_segment = onramp.mysegment;
//        if(dnstrm_segment.fwy().type.equals(AbstractLink.Type.connector))
//            throw new Exception("The downstream segment cannot be a connector");
//        if(!onramp.type.equals(AbstractLink.Type.onramp))
//            throw new Exception("The provided link is not an onramp");
//        if(!dnstrm_segment.or_demands.isEmpty())
//            throw new Exception("Please delete onramp demands before calling this method");
//        if(dnstrm_segment.segment_or_up_id!=null)
//            throw new Exception("The onramp segment is already connected upstream");
//
//
//
//        Node node = fwy_scenario.scenario.nodes.get(onramp.start_node_id);
//
//        if(!node.in_links.isEmpty())
//            throw new Exception("The onramp already has an upstream connection");
//
//
//
//        return false;
//    }
//
//    public boolean connect_upstream_offramp(AbstractLink offramp){
//
//    }


}
