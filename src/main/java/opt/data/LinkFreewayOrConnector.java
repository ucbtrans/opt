package opt.data;

import jaxb.Link;
import jaxb.Roadparam;
import profiles.Profile1D;

public abstract class LinkFreewayOrConnector extends AbstractLink {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkFreewayOrConnector(Link link, Type type, Roadparam rp) {
        super(link, type, rp);
    }

    public LinkFreewayOrConnector(Long id, Type type, Long start_node_id, Long end_node_id, Integer full_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        super(id, type, start_node_id, end_node_id, full_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph, mysegment);
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public void set_demand_vph(Long comm_id, Profile1D profile) throws Exception {
        this.demands.put(comm_id,profile);
    }

    @Override
    public int get_managed_lanes(){
        System.out.println("NOT IMPLEMENTED!");
        return 0;
    }

    @Override
    public int get_aux_lanes(){
        System.out.println("NOT IMPLEMENTED!");
        return 0;
    }

    /////////////////////////////////////
    // insert
    /////////////////////////////////////

//    /**
//     * Get the freeway demand for this segment, for a particular commodity.
//     * @param comm_id ID for the commodity
//     * @return Profile1D object if demand is defined for this commodity. null otherwise.
//     */
//    public Profile1D get_fwy_demand_vph(long comm_id){
//        return fwy_demands.containsKey(comm_id) ? fwy_demands.get(comm_id) : null;
//    }
//
//    /**
//     * Set the freeway demand in vehicles per hour.
//     * @param comm_id ID for the commodity
//     * @param demand_vph Demand in veh/hr as a Profile1D object
//     */
//    public void set_fwy_demand_vph(long comm_id, Profile1D demand_vph)throws Exception {
//        OTMErrorLog errorLog = new OTMErrorLog();
//        demand_vph.validate(errorLog);
//        if (errorLog.haserror())
//            throw new Exception(errorLog.format_errors());
//        this.fwy_demands.put(comm_id,demand_vph);
//    }
}
