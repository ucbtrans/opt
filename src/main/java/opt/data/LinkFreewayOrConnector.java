package opt.data;

import jaxb.Link;
import jaxb.Roadparam;
import profiles.Profile1D;

public abstract class LinkFreewayOrConnector extends AbstractLink {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkFreewayOrConnector(Link link, Roadparam rp) {
        super(link, rp);

        this.params = new ParametersFreeway(
                "",
                link.getFullLanes(),
                0,
                0,
                link.getLength(),
                Float.NaN,
                Float.NaN,
                Float.NaN );
    }

    public LinkFreewayOrConnector(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, AbstractParameters params) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);
    }

    public LinkFreewayOrConnector(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, String name, Boolean is_inner, Integer gp_lanes, Integer managed_lanes, Integer aux_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, name, is_inner, gp_lanes, managed_lanes, aux_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph);
        this.params = new ParametersFreeway(name, gp_lanes, managed_lanes, aux_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph );
    }

    // used by clone
    public LinkFreewayOrConnector(long id, Long start_node_id, Long end_node_id, AbstractParameters params){
        super(id,start_node_id,end_node_id,params);
    }

    /////////////////////////////////////
    // demands and splits
    /////////////////////////////////////

    @Override
    public void set_demand_vph(Long comm_id, Profile1D profile) throws Exception {
        this.demands.put(comm_id,profile);
    }

}
