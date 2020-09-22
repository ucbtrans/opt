/**
 * Copyright (c) 2020, Regents of the University of California
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 **/

package opt.performance;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import opt.AppMainController;
import opt.data.AbstractLink;
import opt.data.SimDataLink;
import opt.data.SimDataScenario;
import opt.data.TimeSeries;
import opt.data.LaneGroupType;
import opt.utils.Misc;
import opt.UserSettings;
import opt.data.Commodity;
import opt.data.FreewayScenario;
import opt.data.Route;
import opt.utils.jfxutils.chart.JFXChartUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class serves to display plots of simulation data for a given scenario network.
 * 
 * @author Alex Kurzhanskiy
 */
public class ScenarioPerformanceController {
    private Stage primaryStage = null;
    private AppMainController appMainController = null;
    
    private FreewayScenario myScenario = null;
    private SimDataScenario mySimData = null;
    
    private List<Commodity> listVT = null;
    
    private float start = 0;
    private String timeLabel;
    private double timeDivider;
            
    
    @FXML // fx:id="scenarioPerformanceMainPane"
    private TabPane scenarioPerformanceMainPane; // Value injected by FXMLLoader

    @FXML // fx:id="tabSummary"
    private Tab tabSummary; // Value injected by FXMLLoader

    @FXML // fx:id="spSummary"
    private ScrollPane spSummary; // Value injected by FXMLLoader
    
    @FXML // fx:id="vbSummary"
    private VBox vbSummary; // Value injected by FXMLLoader

    @FXML // fx:id="tabAggregates"
    private Tab tabAggregates; // Value injected by FXMLLoader

    @FXML // fx:id="spAggregates"
    private ScrollPane spAggregates; // Value injected by FXMLLoader
    
    @FXML // fx:id="vbAggregates"
    private VBox vbAggregates; // Value injected by FXMLLoader

    @FXML // fx:id="tabEmissions"
    private Tab tabEmissions; // Value injected by FXMLLoader

    @FXML // fx:id="spEmissions"
    private ScrollPane spEmissions; // Value injected by FXMLLoader
    


    /***************************************************************************
     * Setup and initialization
     ***************************************************************************/

    public void setPrimaryStage(Stage s) {
        primaryStage = s;
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the main app controller that is used to sync up
     *               all sub-windows.
     */
    public void setAppMainController(AppMainController ctrl) {
        appMainController = ctrl;
    }
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        
    }



    /**
     * This function is called every time one opens a scenario in the
     * report module.
     * @param sdata - simulation data for the whole scenario.
     */
    public void initWithScenarioData(SimDataScenario sdata) {   
        if (sdata == null)
            return;
        
        mySimData = sdata;
        myScenario = mySimData.fwyscenario;
        
        start = myScenario.get_start_time();
        
        timeLabel = "Time (hours)";
        timeDivider = 3600.0;
        if (sdata.fwyscenario.get_sim_duration() <= 7200) {
            timeLabel = "Time (minutes)";
            timeDivider = 60.0;
        }
        
        listVT = Misc.makeListVT(myScenario.get_commodities());
        fillTabSummary();
        fillTabAggregates();
             
    }
    
    
    private Set<Long> cset(Commodity c) {
        Set<Long> s = new HashSet<Long>();
        s.add(c.getId());
        return s;
    }
    
    private Set<Long> all_comms() {
        Set<Long> s = new HashSet<Long>();
        listVT.forEach((c) -> { s.add(c.getId()); });
        return s;
    }

    
    
    private void fillTabSummary() {
        vbSummary.getChildren().clear();

        PieChart chart;
        ObservableList<PieChart.Data> vmtPieData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> vhtPieData1 = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> vhtPieData2 = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> delayPieData1 = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> delayPieData2 = FXCollections.observableArrayList();
        
        double prefWidth = 0.75*scenarioPerformanceMainPane.getPrefWidth();
        double prefHeight = scenarioPerformanceMainPane.getPrefHeight()/3;

        int sz = listVT.size();
        double v_thres = UserSettings.defaultFreeFlowSpeedThresholdForDelayMph;
        String[] labels = new String[sz];
        double[] c_vmt = new double[sz];
        double[] c_vht = new double[sz];
        double[] c_delay = new double[sz];
        int[] prcts = new int[sz];
        double total_vmt = 0;
        double total_vht = 0;
        double total_delay = 0;

        for (int i = 0; i < sz; i++) {
            double t_vmt = 0;
            double t_vht = 0;
            double t_delay = 0;
            double[] vmt_s = mySimData.get_vmt_for_network(cset(listVT.get(i))).values;
            double[] vht_s = mySimData.get_vht_for_network(cset(listVT.get(i))).values;
            double[] delay_s = mySimData.get_delay_for_network(cset(listVT.get(i)), (float)v_thres).values;
            for (int j = 0; j < vmt_s.length; j++) {
                if (!Double.isNaN(vmt_s[j]))
                    t_vmt += vmt_s[j];
                if (!Double.isNaN(vht_s[j]))
                    t_vht += vht_s[j];
                if (!Double.isNaN(delay_s[j]))
                    t_delay += delay_s[j];
            }
            total_vmt += t_vmt;
            total_vht += t_vht;
            total_delay += t_delay;
            c_vmt[i] = t_vmt;
            c_vht[i] = t_vht;
            c_delay[i] = t_delay;
        }

        int vmt_prct = 0;
        int vht_prct = 0;
        int delay_prct = 0;
        for (int i = 0; i < sz; i++) {
            int p_vmt = (int) Math.round(100 * c_vmt[i] / total_vmt);
            int p_vht = (int) Math.round(100 * c_vht[i] / total_vht);
            int p_delay = (int) Math.round(100 * c_delay[i] / total_delay);
            if (i == sz - 1) {
                p_vmt = Math.max(0, 100 - vmt_prct);
                p_vht = Math.max(0, 100 - vht_prct);
                p_delay = Math.max(0, 100 - delay_prct);
            }
            else {
                vmt_prct += p_vmt;
                vht_prct += p_vht;
                delay_prct += p_delay;
            }
            String l = listVT.get(i).get_name() + " = " + (int)Math.round(c_vmt[i]) + " (" + p_vmt + "%)";
            vmtPieData.add(new PieChart.Data(l, c_vmt[i]));
            l = listVT.get(i).get_name() + " = " + (int)Math.round(c_vht[i]) + " (" + p_vht + "%)";
            vhtPieData1.add(new PieChart.Data(l, c_vht[i]));
            l = listVT.get(i).get_name() + " = " + (int)Math.round(c_vht[i]) + " (" + p_delay + "%)";
            delayPieData1.add(new PieChart.Data(l, c_delay[i]));
        }

        chart = new PieChart(vmtPieData);
        chart.setTitle("Total VMT (" + (int)Math.round(total_vmt) + ")");
        chart.setLegendVisible(false);
        chart.setPrefSize(prefWidth, prefHeight);
        chart.setMinWidth(prefWidth);
        //chart.setMaxWidth(prefWidth);
        chart.setMinHeight(prefHeight);
        //chart.setMaxHeight(prefHeight);
        vbSummary.getChildren().add(chart);
        vbSummary.getChildren().add(new Separator());

        chart = new PieChart(vhtPieData1);
        chart.setTitle("Total VHT (" + (int)Math.round(total_vht) + ")");
        chart.setLegendVisible(false);
        chart.setPrefSize(prefWidth, prefHeight);
        chart.setMinWidth(prefWidth);
        //chart.setMaxWidth(prefWidth);
        chart.setMinHeight(prefHeight);
        //chart.setMaxHeight(prefHeight);
        vbSummary.getChildren().add(chart);
        
        
        TimeSeries vht_no = mySimData.get_vht_for_network_nonsources(null);
        TimeSeries vht_o = mySimData.get_vht_for_network_sources(null);

        double d_n = 0.0;
        if (vht_no != null) {
            double[] vals = vht_no.values;
            for (double v : vals)
                d_n += v;
        }

        double d_o = 0.0;
        if (vht_o != null) {
            double[] vals = vht_o.values;
            for (double v : vals)
                d_o += v;
        }

        int p_no = (int) Math.round(100 * d_n / (d_n + d_o));
        String l = String.format("Non-Origins = %.1f (%d%%)", d_n, p_no);
        vhtPieData2.add(new PieChart.Data(l, d_n));
        l = String.format("Origins = %.1f (%d%%)", d_o, 100-p_no);
        vhtPieData2.add(new PieChart.Data(l, d_o));
        
        chart = new PieChart(vhtPieData2);
        //chart.setTitle("Total VHT (" + (int)Math.round(total_vht) + ")");
        chart.setLegendVisible(false);      
        chart.setPrefSize(prefWidth, prefHeight);
        chart.setMinWidth(prefWidth);
        //chart.setMaxWidth(prefWidth);
        chart.setMinHeight(prefHeight);
        //chart.setMaxHeight(prefHeight);
        vbSummary.getChildren().add(chart);
        vbSummary.getChildren().add(new Separator());
        
        
        String label_units = UserSettings.unitsSpeed;
        double cc = UserSettings.speedConversionMap.get("mph"+label_units);
        String label_thres = String.format("(%.1f veh.-hr., Speed Threshold: %.0f %s)", total_delay, cc*v_thres, label_units);
        if (v_thres < 0)
            label_thres = String.format("(%.1f veh.-hr., Speed Threshold: Free Flow Speed)", total_delay);
        
        chart = new PieChart(delayPieData1);
        chart.setTitle("Total Delay " + label_thres);
        chart.setLegendVisible(false);
        chart.setPrefSize(prefWidth, prefHeight);
        chart.setMinWidth(prefWidth);
        //chart.setMaxWidth(prefWidth);
        chart.setMinHeight(prefHeight);
        //chart.setMaxHeight(prefHeight);
        vbSummary.getChildren().add(chart);
        
        
        TimeSeries delay_no = mySimData.get_delay_for_network_nonsources(null, (float)v_thres);
        TimeSeries delay_o = mySimData.get_delay_for_network_sources(null, (float)v_thres);

        d_n = 0.0;
        if (delay_no != null) {
            double[] vals = delay_no.values;
            for (double v : vals)
                d_n += v;
        }

        d_o = 0.0;
        if (delay_o != null) {
            double[] vals = delay_o.values;
            for (double v : vals)
                d_o += v;
        }

        p_no = (int) Math.round(100 * d_n / (d_n + d_o));
        l = String.format("Non-Origins = %.1f (%d%%)", d_n, p_no);
        delayPieData2.add(new PieChart.Data(l, d_n));
        l = String.format("Origins = %.1f (%d%%)", d_o, 100-p_no);
        delayPieData2.add(new PieChart.Data(l, d_o));
        
        chart = new PieChart(delayPieData2);
        //chart.setTitle("Total Delay " + label_thres);
        chart.setLegendVisible(false);
        chart.setPrefSize(prefWidth, prefHeight);
        chart.setMinWidth(prefWidth);
        //chart.setMaxWidth(prefWidth);
        chart.setMinHeight(prefHeight);
        //chart.setMaxHeight(prefHeight);
        vbSummary.getChildren().add(chart);
    }
    
    
    
    public void fillTabAggregates() {
        vbAggregates.getChildren().clear();
        String label;
        XYChart.Series dataSeries, dataSeries_mng, dataSeries_aux, dataSeries_total;
        List<XYDataItem> xydata, xydata_src;
        int sz, sz_src;
        XYDataItem xy;
        double dt = mySimData.get_dt_sec();

        label = "Network VMT";
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(timeLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("VMT");

        LineChart vmtChart = new LineChart(xAxis, yAxis);
        vmtChart.setTitle(label);

        int max_sz = 0;
        for (Commodity c : listVT) {
            TimeSeries ts = mySimData.get_vmt_for_network(cset(c));
            if (ts != null)
                max_sz = Math.max(max_sz, ts.values.length);
        }
        double[] total = new double[max_sz];
        for (int i = 0; i < max_sz; i++)
            total[i] = 0;
        for (Commodity c : listVT) {
            dataSeries = new XYChart.Series();
            dataSeries.setName(c.get_name());
            xydata = mySimData.get_vmt_for_network(cset(c)).get_XYSeries(c.get_name()).getItems();

            sz = xydata.size();

            for (int i = 0; i < max_sz; i++) {
                if (i < sz) {
                    xy = xydata.get(i);
                    dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                    total[i] += xy.getYValue();
                } else {
                    dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
                }
            }
            vmtChart.getData().add(dataSeries);
        }

        dataSeries_total = new XYChart.Series();
        dataSeries_total.setName("Total");
        for (int i = 0; i < max_sz; i++)
            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
        if (listVT.size() > 1)
            vmtChart.getData().add(dataSeries_total);

        vmtChart.setCreateSymbols(false);
        vmtChart.setLegendSide(Side.BOTTOM);
        vmtChart.setMinHeight(300);
        vbAggregates.getChildren().add(vmtChart);
        vbAggregates.getChildren().add(new Separator());
        JFXChartUtil.setupZooming(vmtChart, (MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown() )
                mouseEvent.consume();
        });
        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vmtChart);



        label = "Network VHT";
        xAxis = new NumberAxis();
        xAxis.setLabel(timeLabel);
        yAxis = new NumberAxis();
        yAxis.setLabel("VHT");

        LineChart vhtChart1 = new LineChart(xAxis, yAxis);
        vhtChart1.setTitle(label);

        max_sz = 0;
        for (Commodity c : listVT) {
            TimeSeries ts = mySimData.get_vht_for_network(cset(c));
            if (ts != null)
                max_sz = Math.max(max_sz, ts.values.length);
        }
        total = new double[max_sz];
        for (int i = 0; i < max_sz; i++)
            total[i] = 0;
        for (Commodity c : listVT) {
            dataSeries = new XYChart.Series();
            dataSeries.setName(c.get_name());
            xydata = mySimData.get_vht_for_network(cset(c)).get_XYSeries(c.get_name()).getItems();

            sz = xydata.size();

            for (int i = 0; i < max_sz; i++) {
                if (i < sz) {
                    xy = xydata.get(i);
                    dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                    total[i] += xy.getYValue();
                } else {
                    dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
                }
            }
            vhtChart1.getData().add(dataSeries);
        }

        dataSeries_total = new XYChart.Series();
        dataSeries_total.setName("Total");
        for (int i = 0; i < max_sz; i++)
            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
        if (listVT.size() > 1)
            vhtChart1.getData().add(dataSeries_total);

        vhtChart1.setCreateSymbols(false);
        vhtChart1.setLegendSide(Side.BOTTOM);
        vhtChart1.setMinHeight(300);
        vbAggregates.getChildren().add(vhtChart1);
        JFXChartUtil.setupZooming(vhtChart1, (MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown() )
                mouseEvent.consume();
        });
        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vhtChart1);
        
        
        xAxis = new NumberAxis();
        xAxis.setLabel(timeLabel);
        yAxis = new NumberAxis();
        yAxis.setLabel("VHT");

        LineChart vhtChart2 = new LineChart(xAxis, yAxis);
        //vhtChart2.setTitle(label + label_thres);

        max_sz = 0;
        TimeSeries ts = mySimData.get_vht_for_network(null);
        if (ts != null)
            max_sz = Math.max(max_sz, ts.values.length);

        total = new double[max_sz];
        for (int i = 0; i < max_sz; i++)
            total[i] = 0;

        dataSeries = new XYChart.Series();
        dataSeries.setName("Non-Origin Sections");
        xydata = mySimData.get_vht_for_network_nonsources(null).get_XYSeries("Non-Origin Sections").getItems();
        sz = xydata.size();
        for (int i = 0; i < max_sz; i++) {
            if (i < sz) {
                xy = xydata.get(i);
                dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                total[i] += xy.getYValue();
            } else {
                dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
            }
        }
        vhtChart2.getData().add(dataSeries);

        dataSeries = new XYChart.Series();
        dataSeries.setName("Origin Sections");
        ts = mySimData.get_vht_for_network_sources(null);
        xydata = ts.get_XYSeries("Origin Sections").getItems();
        sz = xydata.size();
        for (int i = 0; i < max_sz; i++) {
            if (i < sz) {
                xy = xydata.get(i);
                dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                total[i] += xy.getYValue();
            } else {
                dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
            }
        }
        vhtChart2.getData().add(dataSeries);

        dataSeries_total = new XYChart.Series();
        dataSeries_total.setName("Total");
        for (int i = 0; i < max_sz; i++)
            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
        if (listVT.size() > 1)
            vhtChart2.getData().add(dataSeries_total);

        vhtChart2.setCreateSymbols(false);
        vhtChart2.setLegendSide(Side.BOTTOM);
        vhtChart2.setMinHeight(300);
        vbAggregates.getChildren().add(vhtChart2);
        vbAggregates.getChildren().add(new Separator());
        JFXChartUtil.setupZooming(vhtChart2, (MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown() )
                mouseEvent.consume();
        });
        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vhtChart2);
        
        
        
        
        
        
        
        label = "Network Delay ";
        String label_units = UserSettings.unitsSpeed;
        double cc = UserSettings.speedConversionMap.get("mph"+label_units);
        double v_thres = UserSettings.defaultFreeFlowSpeedThresholdForDelayMph;
        String label_thres = String.format("(Speed Threshold: %.0f %s)", cc*v_thres, label_units);
        if (v_thres < 0)
            label_thres = "(Speed Threshold: Free Flow Speed)";
        xAxis = new NumberAxis();
        xAxis.setLabel(timeLabel);
        yAxis = new NumberAxis();
        yAxis.setLabel("Delay (veh.-hr.)");

        LineChart delayChart1 = new LineChart(xAxis, yAxis);
        delayChart1.setTitle(label + label_thres);

        max_sz = 0;
        for (Commodity c : listVT) {
            ts = mySimData.get_delay_for_network(cset(c), (float)v_thres);
            if (ts != null)
                max_sz = Math.max(max_sz, ts.values.length);
        }
        total = new double[max_sz];
        for (int i = 0; i < max_sz; i++)
            total[i] = 0;
        for (Commodity c : listVT) {
            dataSeries = new XYChart.Series();
            dataSeries.setName(c.get_name());
            xydata = mySimData.get_delay_for_network(cset(c), (float)v_thres).get_XYSeries(c.get_name()).getItems();

            sz = xydata.size();

            for (int i = 0; i < max_sz; i++) {
                if (i < sz) {
                    xy = xydata.get(i);
                    dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                    total[i] += xy.getYValue();
                } else {
                    dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
                }
            }
            delayChart1.getData().add(dataSeries);
        }

        dataSeries_total = new XYChart.Series();
        dataSeries_total.setName("Total");
        for (int i = 0; i < max_sz; i++)
            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
        if (listVT.size() > 1)
            delayChart1.getData().add(dataSeries_total);

        delayChart1.setCreateSymbols(false);
        delayChart1.setLegendSide(Side.BOTTOM);
        delayChart1.setMinHeight(300);
        vbAggregates.getChildren().add(delayChart1);
        JFXChartUtil.setupZooming(delayChart1, (MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown() )
                mouseEvent.consume();
        });
        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(delayChart1);

        
        xAxis = new NumberAxis();
        xAxis.setLabel(timeLabel);
        yAxis = new NumberAxis();
        yAxis.setLabel("Delay (veh.-hr.)");

        LineChart delayChart2 = new LineChart(xAxis, yAxis);
        //delayChart2.setTitle(label + label_thres);

        max_sz = 0;
        ts = mySimData.get_delay_for_network(null, (float)v_thres);
        if (ts != null)
            max_sz = Math.max(max_sz, ts.values.length);

        total = new double[max_sz];
        for (int i = 0; i < max_sz; i++)
            total[i] = 0;

        dataSeries = new XYChart.Series();
        dataSeries.setName("Non-Origin Sections");
        xydata = mySimData.get_delay_for_network_nonsources(null, (float)v_thres).get_XYSeries("Non-Origin Sections").getItems();
        sz = xydata.size();
        for (int i = 0; i < max_sz; i++) {
            if (i < sz) {
                xy = xydata.get(i);
                dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                total[i] += xy.getYValue();
            } else {
                dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
            }
        }
        delayChart2.getData().add(dataSeries);

        dataSeries = new XYChart.Series();
        dataSeries.setName("Origin Sections");
        ts = mySimData.get_delay_for_network_sources(null, (float)v_thres);
        xydata = ts.get_XYSeries("Origin Sections").getItems();
        sz = xydata.size();
        for (int i = 0; i < max_sz; i++) {
            if (i < sz) {
                xy = xydata.get(i);
                dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                total[i] += xy.getYValue();
            } else {
                dataSeries.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
            }
        }
        delayChart2.getData().add(dataSeries);

        dataSeries_total = new XYChart.Series();
        dataSeries_total.setName("Total");
        for (int i = 0; i < max_sz; i++)
            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
        if (listVT.size() > 1)
            delayChart2.getData().add(dataSeries_total);

        delayChart2.setCreateSymbols(false);
        delayChart2.setLegendSide(Side.BOTTOM);
        delayChart2.setMinHeight(300);
        vbAggregates.getChildren().add(delayChart2);
        JFXChartUtil.setupZooming(delayChart2, (MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown() )
                mouseEvent.consume();
        });
        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(delayChart2);
    }
    
    
}
