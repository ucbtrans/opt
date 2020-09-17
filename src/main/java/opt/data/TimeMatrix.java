package opt.data;

import java.util.ArrayList;
import java.util.List;

public class TimeMatrix {

    public float [] time;
    public List<LinkLaneGroupCell> space;
    public double[][] values;   // space x time

    TimeMatrix(float [] time, List<AbstractLink> routelinks, SimDataScenario data, boolean haslgdata, LaneGroupType globallgtype){
        this.time = time;

        // determine spatial resolution
        space = new ArrayList<>();
        for(AbstractLink link : routelinks){
            SimDataLink lkdata = data.linkdata.get(link.id);
            LaneGroupType lgtype = lkdata.lgtype2id.containsKey(globallgtype) ? globallgtype : LaneGroupType.gp;
            if(haslgdata)
                space.add(new LinkLaneGroupCell(link.id,lgtype,-1));
            else{
                SimDataLanegroup lgdata = lkdata.lgData.get(lkdata.lgtype2id.get(lgtype));
                for(int i=0;i<lgdata.celldata.size();i++)
                    space.add(new LinkLaneGroupCell(link.id,lgtype,i));
            }
        }

        values = new double[space.size()][time.length];
    }

    public float get_dt(){
        return time[1]-time[0];
    }

    public String print_time(){
        String str = "";
        for(float t : time)
            str += String.format("%.2f\n",t);
        return str;
    }

    public String print_space(){
        String str = "";
        for(LinkLaneGroupCell s : space)
            str += String.format("%d\t%s\t%d\n",s.linkid,s.lgtype,s.cell);
        return str;
    }

    public String print_values(){
        String str = "";
        for(double[] v : values){
            for(int i = 0; i < v.length - 1; ++i)
                str += String.format("%.2f,",v[i]);
            str += String.format("%.2f;\n",v[v.length-1]);
        }
        return str;
    }

    public class LinkLaneGroupCell{
        final long linkid;
        final LaneGroupType lgtype;
        final int cell;
        public LinkLaneGroupCell(long link,LaneGroupType lg,int cell){
            this.linkid =link;
            this.lgtype = lg;
            this.cell = cell;
        }
    }

    @Override
    public String toString() {
        return print_values();
    }
}
