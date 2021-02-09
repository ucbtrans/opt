package opt.data;

import jaxb.Link;
import jaxb.Roadparam;

import java.util.HashMap;

public class LinkFreeway extends LinkFreewayOrConnector {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkFreeway(Link link, Roadparam rp,int mng_lanes,FDparams mng_fd,boolean mng_barrier,boolean mng_separated,int aux_lanes,FDparams aux_fd) {
        super(link, rp,mng_lanes, mng_fd, mng_barrier, mng_separated, aux_lanes, aux_fd);
    }

    public LinkFreeway(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, ParametersFreeway params) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);
    }

    /////////////////////////////////////
    // insert and connect
    /////////////////////////////////////

    @Override
    public Segment insert_up_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {

        assert(ramp_params==null);

        Segment up_segment = get_up_segment();

        // create new upstream link
        LinkFreeway fwy_link = (LinkFreeway) create_up_FwyOrConnLink(Type.freeway,fwy_params);

        // wrap in a segment
        Segment new_segment = create_segment(fwy_link,seg_name);

        // delete existing demands
        this.demands = new HashMap<>();

        // connect upstream segment to new node
        if(up_segment!=null) {
            connect_segments_dwnstr_node_to(up_segment, fwy_link.start_node_id);
            fwy_link.up_link = up_segment.fwy;
            up_segment.fwy.dn_link = fwy_link;
        }

        return new_segment;
    }

    @Override
    public Segment insert_dn_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {

        assert(ramp_params==null);

        Segment dn_segment = get_dn_segment();

        // create new dnstream link
        LinkFreeway fwy_link = (LinkFreeway) create_dn_FwyOrConnLink(Type.freeway,fwy_params);

        // wrap in a segment
        Segment new_segment = create_segment(fwy_link,seg_name);

        // connect dnstream segment to new node
        if(dn_segment!=null) {
            connect_segments_upstr_node_to(dn_segment, fwy_link.end_node_id);
            fwy_link.dn_link = dn_segment.fwy;
            dn_segment.fwy.up_link = fwy_link;
        }

        return new_segment;
    }

    @Override
    public boolean connect_to_upstream(AbstractLink conn_up_link) {
        boolean success =  super.connect_to_upstream(conn_up_link);

        if(!success)
            return false;

        // move my onramps to new startnode
        for(LinkOnramp or : mysegment.get_ors())
            or.end_node_id = conn_up_link.end_node_id;

        // TODO: GG THINKS THIS SHOULD NO LONGER BE NECESSARY
//        // deal with splits to this link
//        this.splits = new HashMap<>();
//
//        Segment up_segment = conn_up_link.mysegment;
//
//        List<LinkOfframp> frs = up_segment.get_frs();
//        Set<Long> comm_ids = frs.stream()
//                .flatMap(link->link.splits.keySet().stream())
//                .collect(toSet());
//
//        for(Long comm_id : comm_ids){
//
//            Set<Profile1D> profiles = frs.stream()
//                    .filter(link->link.splits.containsKey(comm_id))
//                    .map(link->link.splits.get(comm_id))
//                    .collect(toSet());
//
//            if(profiles.isEmpty())
//                continue;
//
//            // check they all have the same dt
//            Set<Float> dts = profiles.stream().map(p->p.dt).collect(toSet());
//            Set<Integer> ns = profiles.stream().map(p->p.get_length()).collect(toSet());
//            assert(dts.size()==1);
//            assert(ns.size()==1);
//
//            float dt = dts.iterator().next();
//            int n = ns.iterator().next();
//
//            Profile1D fwy_split = new Profile1D(0f,dt);
//            for(int i=0;i<n;i++) {
//                int finalI = i;
//                fwy_split.add(1f-profiles.stream().mapToDouble(p->p.get_ith_value(finalI)).sum());
//            }
//
//            assert( fwy_split.values.stream().anyMatch(x->x>=0) );
//
//            this.splits.put(comm_id,fwy_split);
//        }

        return true;
    }

    @Override
    protected boolean is_permitted_uplink(AbstractLink link) {
        return link instanceof LinkFreeway;
    }

    @Override
    protected boolean is_permitted_dnlink(AbstractLink link) {
        return link instanceof LinkFreeway;
    }

    /////////////////////////////////////
    // get set
    /////////////////////////////////////

    @Override
    public Type get_type() {
        return Type.freeway;
    }

}
