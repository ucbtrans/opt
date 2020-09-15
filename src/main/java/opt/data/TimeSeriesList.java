package opt.data;

import java.util.ArrayList;
import java.util.List;

public class TimeSeriesList {

    public float [] time;
    public List<LinkLaneGroupCell> space;
    public List<double[]> values;

    TimeSeriesList(float [] time){
        this.time = time;
        this.space = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public float get_dt(){
        return time[1]-time[0];
    }

    public void add_entry(AbstractLink link,LaneGroupType lgtype,int cellindex,double[] x){
        space.add(new LinkLaneGroupCell(link.id,lgtype,cellindex));
        values.add(x);
    }

//    public TimeSeriesList resample(float newdt){
//        final double epsilon = 1e-3;
//        final float curr_dt = get_dt();
//        final int n = time.size();
//        List<Float> newtime = new ArrayList<>();
//        List<double[]> newvalues = new ArrayList<>();
//
//        for(float currtime=time.get(0);currtime<=time.get(time.size()-1);currtime+=newdt){
//            newtime.add(currtime);
//
//            float float_index = currtime/curr_dt;
//            int index = Math.round(float_index);
//
//
//            if(float_index>=n-1){
//                newvalues.add(values.get(n-1));
//                continue;
//            }
//
//            float lambda = float_index-index;
//            if(Math.abs(lambda)<=epsilon)
//                newvalues.add(values.get(index));
//            else {
//                if(lambda>epsilon){
//                    int ind0 = index;
//                    int ind1 = ind0+1;
//                    newvalues.add( (1-lambda)*values.get(ind0) + lambda*values.get(ind1) );
//                } else {
//                    int ind0 = index-1;
//                    int ind1 = ind0+1;
//                    lambda = -lambda;
//                    newvalues.add( lambda*values.get(ind0) + (1-lambda)*values.get(ind1) );
//                }
//            }
//        }
//
//        return new TimeSeries(newtime,newvalues);
//    }

    public String print_time(){
        String str = "";
        for(float t : time)
            str += String.format("%.2f\n",t);
        return str;
    }

    public String print_space(){
        String str = "";
        for(LinkLaneGroupCell s : space)
            str += String.format("%d\t%s\t%d\n",s.link,s.lg,s.cell);
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
        final long link;
        final LaneGroupType lg;
        final int cell;
        public LinkLaneGroupCell(long link,LaneGroupType lg,int cell){
            this.link=link;
            this.lg = lg;
            this.cell = cell;
        }
    }

    @Override
    public String toString() {
        return print_values();
    }
}
