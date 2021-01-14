package opt.data;

public class LaneGroup {
    long linkid;
    int [] lanes;

    public LaneGroup(long linkid, int[] lanes) {
        this.linkid = linkid;
        this.lanes = lanes;
    }
}
