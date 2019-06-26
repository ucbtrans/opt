package opt.data;

public class jLink {
    public long id;
    public long start_node_id;
    public long end_node_id;
    public int full_lanes;
    public float length;

    public boolean is_source;
    public boolean is_mainline;
    public boolean is_ramp;
    public float capacity_vphpl;
    public float jam_density_vpkpl;
    public float ff_speed_kph;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public jLink(jaxb.Link link,jaxb.Roadparam rp){
        this.id = link.getId();
        this.start_node_id = link.getStartNodeId();
        this.end_node_id = link.getEndNodeId();
        this.full_lanes = link.getFullLanes();
        this.length = link.getLength();
        this.is_mainline = link.getRoadType().equals("mainline");
        this.is_ramp = link.getRoadType().equals("ramp");
        this.capacity_vphpl = rp.getCapacity();
        this.jam_density_vpkpl = rp.getJamDensity();
        this.ff_speed_kph = rp.getSpeed();
    }

    public jLink(long id,long start_node_id,long end_node_id,int full_lanes,float length,boolean is_mainline,
                 boolean is_ramp,boolean is_source,float capacity_vphpl,float jam_density_vpkpl,float ff_speed_kph) {

        this.id = id;
        this.start_node_id = start_node_id;
        this.end_node_id = end_node_id;
        this.full_lanes = full_lanes;
        this.length = length;

        this.is_mainline = is_mainline;
        this.is_ramp = is_ramp;
        this.is_source = is_source;
        this.capacity_vphpl = capacity_vphpl;
        this.jam_density_vpkpl = jam_density_vpkpl;
        this.ff_speed_kph = ff_speed_kph;

    }

    /////////////////////////////////////
    // getters
    /////////////////////////////////////

    public float get_max_vehicles(){
        return jam_density_vpkpl * full_lanes * length / 1000f;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        String str = String.format(
                "\tid\t%d\n" +
                        "\tstart_node_id\t%d\n" +
                        "\tend_node_id\t%d\n" +
                        "\tfull_lanes\t%d\n" +
                        "\tlength\t%f\n" +
                        "\tis_source\t%s\n" +
                        "\tis_mainline\t%s\n" +
                        "\tis_ramp\t%s\n" +
                        "\tcapacity_vphpl\t%f\n" +
                        "\tjam_density_vpkpl\t%f\n" +
                        "\tff_speed_kph\t%f",
                id,start_node_id,end_node_id,full_lanes,length,is_source, is_mainline,is_ramp,capacity_vphpl,jam_density_vpkpl,ff_speed_kph);
        return str;
    }
}
