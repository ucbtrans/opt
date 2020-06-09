package opt.data;

import java.util.ArrayList;
import java.util.List;

public class TimeSeriesList {

    public List<Float> time;
    public List<LinkLaneGroupCell> space;
    public List<double[]> values;

    public TimeSeriesList(List<Float> time){
        this.time = time;
        this.space = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public float get_dt(){
        return time.get(1)-time.get(0);
    }

    public void add_entry(AbstractLink link,LaneGroupType lgtype,int cellindex,double[] x){
        space.add(new LinkLaneGroupCell(link.id,lgtype,cellindex));
        values.add(x);
    }

    public TimeSeriesList resample(float newdt){
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
        return null;
    }



    public class LinkLaneGroupCell{
        long link;
        LaneGroupType lg;
        int cell;
        public LinkLaneGroupCell(long link,LaneGroupType lg,int cell){
            this.link=link;
            this.lg = lg;
            this.cell = cell;
        }
    }

}
