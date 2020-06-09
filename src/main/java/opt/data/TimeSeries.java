package opt.data;

import org.jfree.data.xy.XYSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TimeSeries {
    public List<Float> time;
    public List<Double> values;

    public TimeSeries(List<Float> time,double [] v){
        this.time = time;
        this.values = Arrays.stream(v).boxed().collect(Collectors.toList());
    }


    public TimeSeries(List<Float> time,List<Double> v){
        this.time = time;
        this.values = v;
    }

    public float get_dt(){
        return time.get(1)-time.get(0);
    }

//    public void mult(float alpha){
//        for(int i=0;i<time.size();i++)
//            this.values.set(i,alpha*values.get(i));
//    }

    public TimeSeries resample(float newdt){
        final double epsilon = 1e-3;
        final float curr_dt = get_dt();
        final int n = time.size();
        List<Float> newtime = new ArrayList<>();
        List<Double> newvalues = new ArrayList<>();

        for(float currtime=time.get(0);currtime<=time.get(time.size()-1);currtime+=newdt){
            newtime.add(currtime);

            float float_index = currtime/curr_dt;
            int index = Math.round(float_index);

            if(float_index>=n-1){
                newvalues.add(values.get(n-1));
                continue;
            }

            float lambda = float_index-index;
            if(Math.abs(lambda)<=epsilon)
                newvalues.add(values.get(index));
            else {
                if(lambda>epsilon){
                    int ind0 = index;
                    int ind1 = ind0+1;
                    newvalues.add( (1-lambda)*values.get(ind0) + lambda*values.get(ind1) );
                } else {
                    int ind0 = index-1;
                    int ind1 = ind0+1;
                    lambda = -lambda;
                    newvalues.add( lambda*values.get(ind0) + (1-lambda)*values.get(ind1) );
                }
            }
        }

        return new TimeSeries(newtime,newvalues);
    }

    public XYSeries get_XYSeries(String label){
        XYSeries series = new XYSeries(label);
        if(values==null)
            return series;
        for(int k=0;k<time.size();k++)
            series.add(time.get(k),values.get(k));
        return series;
    }

    @Override
    public String toString() {
        String str = "";
        for(int i=0;i<time.size();i++)
            str += String.format("%.1f\t%f\n",time.get(i),values.get(i));
        return str;
    }
}
