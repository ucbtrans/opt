package opt.data;

import org.jfree.data.xy.XYSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TimeSeries {
    private static boolean shift_values = true;
    protected float [] time;
    protected double [] values;

    public TimeSeries(float [] time){
        this.time = time;
        this.values = new double[time.length];
    }

    public TimeSeries(float [] time,double [] v){
        this.time = time;
        this.values = v;
    }

    public float get_dt(){
        return time[1]-time[0];
    }

    public double [] get_values(){
        double [] round_values = new double[values.length];
        if(shift_values && values.length>=2){
            for(int i=0;i<values.length-1;i++)
                round_values[i] = Math.floor(values[i+1]*10d)/10d;
            round_values[values.length-1] = round_values[values.length-2];
        }
        else{
            for(int i=0;i<values.length;i++)
                round_values[i] = Math.floor(values[i]*10d)/10d;
        }
        return round_values;
    }

    public int num_values(){
        return values.length;
    }

    public void add(TimeSeries ts) throws Exception {
        if (ts == null)
            return;
        
        if(this.time.length!=ts.values.length)
            throw new Exception("this.time.size()!=ts.values.size()");

        if(this.values.length==0) {
            this.values = ts.values;
            return;
        }

        for(int k=0;k<this.time.length;k++)
            values [k] = values[k] + ts.values[k];

    }

    public void mult(float alpha){
        for(int k=0;k<time.length;k++)
            this.values[k] = alpha*values[k];
    }

    public TimeSeries resample(float newdt){
        final double epsilon = 1e-3;
        final float curr_dt = get_dt();
        final int n = time.length;

        int newn = 1+(int)(n*this.get_dt()/newdt);

        float[] newtime = new float[newn];
        double[] newvalues = new double[newn];
        int k=0;
        for(float currtime=time[0];currtime<=time[time.length-1];currtime+=newdt){
            newtime[k] = currtime;

            float float_index = currtime/curr_dt;
            int index = Math.round(float_index);

            if(float_index>=n-1){
                newvalues[k] = values[n-1];
                k++;
                continue;
            }

            float lambda = float_index-index;
            if(Math.abs(lambda)<=epsilon)
                newvalues[k] = values[index];
            else {
                if(lambda>epsilon){
                    int ind0 = index;
                    int ind1 = ind0+1;
                    newvalues[k] = (1-lambda)*values[ind0] + lambda*values[ind1];
                } else {
                    int ind0 = index-1;
                    int ind1 = ind0+1;
                    lambda = -lambda;
                    newvalues[k] = lambda*values[ind0] + (1-lambda)*values[ind1];
                }
            }
            k++;
        }

        return new TimeSeries(newtime,newvalues);
    }

    public XYSeries get_XYSeries(String label){
        XYSeries series = new XYSeries(label);
        if(values==null)
            return series;
        double [] round_values = get_values();
        for(int k=0;k<time.length;k++)
            series.add(time[k],round_values[k]);
        return series;
    }

    @Override
    public String toString() {
        String str = "";
        for(int i=0;i<time.length;i++)
            str += String.format("%.1f\t%f\n",time[i],values[i]);
        return str;
    }
}
