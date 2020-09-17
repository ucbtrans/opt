package opt.tests;

import opt.data.TimeSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestTimeSeries extends AbstractTest {

    TimeSeries timeseries;

    @Before
    public void setup(){
        float dt = 1f;
        int numvalues = 100;
        float[] time = new float[numvalues];
        double[] values = new double[numvalues];
        for(int k=0;k<numvalues;k++){
            float t = k*dt;
            time[k] = t;
            values[k] = Math.exp(-t/30)*Math.sin(t/5);
        }
        timeseries = new TimeSeries(time,values);
    }

    @Test
    public void test_get_dt(){
        assertEquals(timeseries.get_dt(),1f,0.001f);
    }

    @Test
    public void test_mult(){
        double value = timeseries.values[10];
        timeseries.mult(2f);
        assertEquals(timeseries.values[10],value*2d,0.001);
    }

    @Test
    public void test_resample(){
        XYSeriesCollection seriess = new XYSeriesCollection();
        seriess.addSeries(timeseries.get_XYSeries("original"));
        seriess.addSeries(timeseries.resample(12f).get_XYSeries("resample 12"));
        seriess.addSeries(timeseries.resample(20f).get_XYSeries("resample 20"));
        TestPlot.plot(seriess,
                "resample test",
                "yaxis",
                "temp/plotB.png");

        TimeSeries t12 = timeseries.resample(12f);
        seriess = new XYSeriesCollection();
        seriess.addSeries(t12.get_XYSeries("original"));
        seriess.addSeries(t12.resample(7f).get_XYSeries("t7"));
        TestPlot.plot(seriess,
                "resample test",
                "yaxis",
                "temp/plotC.png");
    }

}
