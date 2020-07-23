package opt.data;

import jaxb.Gates;

import java.util.Objects;

/** wrapper for jaxb.AddLanes with equals/hashCode implementation **/
public class AddLanes {
    protected boolean isopen;
    protected String side;
    protected String pos;
    protected Float length;
    protected Integer lanes;
    protected Gates gates;

    public AddLanes(boolean isopen,String side,String pos,Float length,Integer lanes,Gates gates){
        this.isopen = isopen;
        this.side = side;
        this.pos = pos;
        this.length = length;
        this.lanes = lanes;
        this.gates = gates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddLanes addLanes = (AddLanes) o;
        return isopen == addLanes.isopen &&
                side.equals(addLanes.side) &&
                Objects.equals(pos, addLanes.pos) &&
                Objects.equals(length, addLanes.length) &&
                lanes.equals(addLanes.lanes) &&
                Objects.equals(gates, addLanes.gates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isopen, side, pos, length, lanes, gates);
    }

//    public Gates getGates() {
//        return jaddlanes.getGates();
//    }
//
//    public void setGates(Gates value) {
//        jaddlanes.setGates(value);
//    }
//
//    public boolean isIsopen() {
//        return jaddlanes.isIsopen();
//    }
//
//    public void setIsopen(Boolean value) {
//        jaddlanes.setIsopen(value);
//    }
//
//    public String getSide() {
//        return jaddlanes.getSide();
//    }
//
//    public void setSide(String value) {
//        jaddlanes.setSide(value);
//    }
//
//    public String getPos() {
//        return jaddlanes.getPos();
//    }
//
//    public void setPos(String value) {
//        jaddlanes.setPos(value);
//    }
//
//    public Float getLength() {
//        return jaddlanes.getLength();
//    }
//
//    public void setLength(Float value) {
//        jaddlanes.setLength(value);
//    }
//
//    public Long getRoadparam() {
//        return jaddlanes.getRoadparam();
//    }
//
//    public void setRoadparam(Long value) {
//        jaddlanes.setRoadparam(value);
//    }
//
//    public int getLanes() {
//        return jaddlanes.getLanes();
//    }
//
//    public void setLanes(Integer value) {
//        jaddlanes.setLanes(value);
//    }

//    @Override
//    public boolean equals(Object o) {
//        if(o==null)
//            return false;
//        AddLanes that = (AddLanes) o;
//        boolean isequal = this.isIsopen()==that.isIsopen() &&
//                Objects.equals(this.getSide(),that.getSide()) &&
//                Objects.equals(this.getPos(),that.getPos()) &&
//                Objects.equals(this.getLength(),that.getLength()) &&
//                this.getLanes()==that.getLanes() &&
//                gatesEqual(this.getGates(),that.getGates()) ;
//        return isequal;
//    }
//
//    private static boolean gatesEqual(Gates ga,Gates gb){
//        if(ga==null && gb==null)
//            return true;
//        if(ga==null ^ gb==null)
//            return false;
//        if(ga.getGate().size()!=gb.getGate().size())
//            return false;
//        boolean isequal = true;
//        for(int i=0;i<ga.getGate().size();i++){
//            isequal &= ga.getGate().get(i).getStartPos()==gb.getGate().get(i).getStartPos() &&
//                    ga.getGate().get(i).getEndPos()==gb.getGate().get(i).getEndPos();
//        }
//        return isequal;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(isIsopen(),getSide(),getPos(),getLength(),getLanes(),getGates());
//    }

}
