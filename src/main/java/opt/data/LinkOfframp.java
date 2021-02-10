package opt.data;

import jaxb.Link;
import jaxb.Roadparam;
import profiles.Profile1D;

import java.util.*;

public class LinkOfframp extends LinkRamp {

    private Map<Long, Profile1D> splits = new HashMap<>();     // commodity -> Profile1D
    public Map<Long, Profile1D> frflows = new HashMap<>();     // commodity -> Profile1D
    public boolean usefrflows = false;
    public float ctrldt;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkOfframp(Link link, Roadparam rp,int mng_lanes,FDparams mng_fd,boolean mng_barrier,boolean mng_separated) {
        super(link,rp,mng_lanes,mng_fd,mng_barrier,mng_separated);
    }

    public LinkOfframp(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, ParametersRamp params) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);
    }

    // used by clone
    public LinkOfframp(long id, Long start_node_id, Long end_node_id, AbstractParameters params) throws Exception {
        super(id,start_node_id,end_node_id,params);
        this.set_is_inner(((ParametersRamp)params).is_inner);
    }

    /////////////////////////////////////
    // up and dn segment
    /////////////////////////////////////

    @Override
    public Segment get_up_segment(){
        return up_link.get_up_segment();
    }

    /////////////////////////////////////
    // splits and frflows
    /////////////////////////////////////

    public final boolean get_use_fr_flows() {
        return usefrflows;
    }

    public final void set_use_fr_flows(boolean x){
        this.usefrflows=x;
    }

    public final Profile1D get_splits(Long comm_id,double default_dt){
        if(splits.containsKey(comm_id))
            return splits.get(comm_id);
        else {
            List<Double> list = new ArrayList<>();
            list.add(0d);
            Profile1D prof = new Profile1D(0f,(float)default_dt,list);
            splits.put(comm_id,prof);
            return prof;
        }
    }

    public final Profile1D get_frflows(Long comm_id,double default_dt){
        if(frflows.containsKey(comm_id))
            return frflows.get(comm_id);
        else {
            List<Double> list = new ArrayList<>();
            list.add(0d);
            Profile1D prof = new Profile1D(0f,(float)default_dt,list);
            frflows.put(comm_id,prof);
            return prof;
        }
    }

    public final void remove_split_for_commodity(long comm_id){
        if(splits.containsKey(comm_id))
            splits.remove(comm_id);
    }

    public final void remove_frlow_for_commodity(long comm_id){
        if(frflows.containsKey(comm_id))
            frflows.remove(comm_id);
    }

    public final void set_split(Long comm_id,float dt, double[] values) throws Exception {
        Profile1D profile = new Profile1D(0f,dt);
        for(double v : values)
            profile.add_entry(v);
        this.splits.put(comm_id, profile);
    }

    public final void set_frflow(Long comm_id,float profdt, double[] values,boolean usefrflows) throws Exception {
        Profile1D profile = new Profile1D(0f,profdt);
        for(double v : values)
            profile.add_entry(v);
        this.usefrflows = usefrflows;
        this.frflows.put(comm_id, profile);

    }

    public final void set_split(Long comm_id,Profile1D profile) throws Exception {
        this.splits.put(comm_id,profile);
    }

    public final void set_frflow(long comm_id,Profile1D profile) throws Exception {
        this.frflows.put(comm_id,profile);
    }

    public final void delete_splits(){
        splits = null;
    }

    public final void delete_frflows(){
        frflows = null;
        usefrflows=false;
    }

    /////////////////////////////////////
    // insert
    /////////////////////////////////////

    @Override
    public Segment insert_dn_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {

        if(dn_link!=null)
            return null;
        assert(ramp_params==null);

        Segment dn_segment = get_dn_segment();

        // create new dnstream link
        LinkConnector new_link = (LinkConnector) create_dn_FwyOrConnLink(Type.connector,fwy_params);

        // wrap in a segment
        Segment new_segment = create_segment(new_link,seg_name);

        // connect dnstream segment to new node
        if(dn_segment!=null) {
            connect_segments_upstr_node_to(dn_segment, new_link.end_node_id);
            new_link.dn_link = dn_segment.fwy;
            dn_segment.fwy.up_link = new_link;
        }

        // add to routes
        mysegment.my_fwy_scenario.add_segment_to_routes(new_segment);

        return new_segment;
    }

    @Override
    public Segment insert_up_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {
        return null;
    }

    @Override
    protected boolean is_permitted_uplink(AbstractLink link) {
        return link instanceof LinkFreeway;
    }

    @Override
    protected boolean is_permitted_dnlink(AbstractLink link) {
        return link instanceof LinkConnector;
    }

    /////////////////////////////////////
    // get set
    /////////////////////////////////////

    @Override
    public Type get_type() {
        return Type.offramp;
    }

    /////////////////////////////////////
    // misc
    /////////////////////////////////////

    @Override
    public AbstractLink clone() {
        LinkOfframp clink = (LinkOfframp) super.clone();
        for(Map.Entry<Long, Profile1D> e : splits.entrySet())
            clink.splits.put(e.getKey(),e.getValue().clone());
        for(Map.Entry<Long, Profile1D> e : frflows.entrySet())
            clink.frflows.put(e.getKey(),e.getValue().clone());
//        for(Map.Entry<Long, Float> e : frctrldt.entrySet())
//            clink.frctrldt.put(e.getKey(),e.getValue());
        return clink;
    }

}
