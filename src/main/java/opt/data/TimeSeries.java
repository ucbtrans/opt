package opt.data;

import java.util.List;

public class TimeSeries {
    List<Float> time;
    List<Double> values;

    public TimeSeries(List<Float> time,List<Double>  values){
        this.time = time;
        this.values = values;
    }

    @Override
    public String toString() {
        String str = "";
        for(int i=0;i<time.size();i++)
            str += String.format("%.1f\t%f\n",time.get(i),values.get(i));
        return str;
    }
}
