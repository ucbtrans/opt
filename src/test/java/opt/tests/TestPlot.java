package opt.tests;

import opt.data.TimeSeries;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TestPlot {

    TimeSeries timeseries;

    @Before
    public void setup(){
        java.util.List<Float> time = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        float dt = 1f;
        int numvalues = 100;
        for(int k=0;k<numvalues;k++){
            float t = k*dt;
            time.add(t);
            values.add(Math.exp(-t/30)*Math.sin(t/5));
        }
        timeseries = new TimeSeries(time,values);
    }

    @Test
    public void test_print_png(){
        String filename = "temp/plotA.png";
        XYSeries series = timeseries.get_XYSeries("label 1");
        plot(series,"test","yaxis_label",filename);
    }

    ////////////////////////////
    // private
    ////////////////////////////

    protected static void plot(XYSeries series,String title,String yaxis_label,String filename) {
        XYSeriesCollection seriess = new XYSeriesCollection();
        seriess.addSeries(series);
        plot(seriess,title,yaxis_label,filename);
    }

    protected static void plot(XYSeriesCollection seriess,String title,String yaxis_label,String filename) {
        ChartPanel chartpanel = create_plot(title,seriess,yaxis_label);
        try {
            print_png(chartpanel, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static ChartPanel create_plot(String title,XYSeriesCollection seriess,String yaxis_label){

        JFreeChart chart = ChartFactory.createXYLineChart(title,
                "time", yaxis_label, seriess,
                PlotOrientation.VERTICAL,
                true,false,false);

        //customization
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);

        // line
        for(int i=0;i<seriess.getSeriesCount();i++)
            renderer.setSeriesStroke(i, new BasicStroke(2f));

        // markers
        long series_size = IntStream.range(0, seriess.getSeriesCount()).mapToLong(i->seriess.getItemCount(i)).max().getAsLong();
        if(series_size>30)
            for(int i=0;i<seriess.getSeriesCount();i++)
                renderer.setSeriesShapesVisible(i,false);
        else
            for(int i=0;i<seriess.getSeriesCount();i++)
                renderer.setSeriesShape(i, new Ellipse2D.Double(-3, -3, 6, 6));

        // remove outline
        plot.setOutlineVisible(false);

        // white background
        plot.setBackgroundPaint(Color.WHITE);

        // grid
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        // package
        ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setSize(new java.awt.Dimension( 560 , 367 ) );

        return chartPanel;
    }

    protected static void print_png(ChartPanel chartPanel, String filename) throws IOException {
        ChartUtilities.writeChartAsPNG(new FileOutputStream(filename),
                chartPanel.getChart(),
                chartPanel.getWidth(),
                chartPanel.getHeight());
    }

}
