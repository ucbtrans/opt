package opt.data;

/** This class is only used to save road connections for use in writing controllers **/
public class RoadConnection {
    public long id;
    public long inlink;
    public LaneGroupType inlgtype;
    public long outlink;
    public LaneGroupType outlgtype;

    public RoadConnection(long id, long inlink, LaneGroupType inlgtype, long outlink, LaneGroupType outlgtype) {
        this.id = id;
        this.inlink = inlink;
        this.inlgtype = inlgtype;
        this.outlink = outlink;
        this.outlgtype = outlgtype;
    }
}
