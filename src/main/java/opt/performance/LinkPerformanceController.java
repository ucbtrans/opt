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
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
 * This class serves to display plots of simulation data for a given link.
 * 
 * @author Alex Kurzhanskiy
 */
public class LinkPerformanceController {
    private Stage primaryStage = null;
    private AppMainController appMainController = null;
    
    private AbstractLink myLink = null;
    private SimDataLink mySimData = null;
    
    private List<Commodity> listVT = null;
            
    
    @FXML // fx:id="linkPerformanceMainPane"
    private TabPane linkPerformanceMainPane; // Value injected by FXMLLoader

    @FXML // fx:id="tabTimeSeries"
    private Tab tabTimeSeries; // Value injected by FXMLLoader

    @FXML // fx:id="spTimeSeries"
    private ScrollPane spTimeSeries; // Value injected by FXMLLoader
    
    @FXML // fx:id="vbTimeSeries"
    private VBox vbTimeSeries; // Value injected by FXMLLoader

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
     * This function is called every time one opens a link in the
     * report module.
     * @param lnk 
     * @param sdata - simulation data for the whole scenario.
     */
    public void initWithLinkData(AbstractLink lnk, SimDataScenario sdata) {   
        if ((lnk == null) || (sdata == null))
            return;
        
        myLink = lnk;
        mySimData = sdata.linkdata.get(myLink.id);
        
        listVT = Misc.makeListVT(myLink.get_segment().get_scenario().get_commodities());
        fillTabTimeseries();
        fillTabAggregates();
        //fillTabEmissions();
    }
    
    
    
    public void fillTabTimeseries() {
        String label_gp, label_mng, label_aux, label_units;
        double cc;
        vbTimeSeries.getChildren().clear();
        
        float start = myLink.get_segment().get_scenario().get_start_time();
        
        label_gp = "GP Lanes";
        label_mng = "Managed Lanes";
        label_aux = "Aux Lanes";
        label_units = UserSettings.unitsSpeed;
        cc = UserSettings.speedConversionMap.get("mph"+label_units);
        
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Speed (" + label_units + ")");

        LineChart speedChart = new LineChart(xAxis, yAxis);
        speedChart.setTitle("Speed");

        XYChart.Series dataSeries_gp = new XYChart.Series();
        dataSeries_gp.setName(label_gp);
        List<XYDataItem> xydata_gp = mySimData.get_speed(LaneGroupType.gp).get_XYSeries(label_gp).getItems();
        
        XYChart.Series dataSeries_mng = new XYChart.Series();
        dataSeries_mng.setName(label_mng);
        List<XYDataItem> xydata_mng = mySimData.get_speed(LaneGroupType.mng).get_XYSeries(label_mng).getItems();
        
        XYChart.Series dataSeries_aux = new XYChart.Series();
        dataSeries_aux.setName(label_aux);
        List<XYDataItem> xydata_aux = mySimData.get_speed(LaneGroupType.gp).get_XYSeries(label_aux).getItems();
        
        int sz_gp = xydata_gp.size();
        int sz_mng = xydata_mng.size();
        int sz_aux = xydata_aux.size();
        int max_sz = Math.max(Math.max(sz_gp, sz_mng), sz_aux);
        
        XYDataItem xy;
        for (int i = 0; i < max_sz; i++) {
            if (i < sz_gp) {
                xy = xydata_gp.get(i);
                dataSeries_gp.getData().add(new XYChart.Data(Misc.seconds2timestring(start+(float)xy.getXValue(), ":"), cc*xy.getYValue()));
            } else {
                float dt = mySimData.get_speed(LaneGroupType.gp).get_dt();
                dataSeries_gp.getData().add(new XYChart.Data(Misc.seconds2timestring(start+i*dt, ":"), 0));
            }
            if (i < sz_mng) {
                xy = xydata_mng.get(i);
                dataSeries_mng.getData().add(new XYChart.Data(Misc.seconds2timestring(start+(float)xy.getXValue(), ":"), cc*xy.getYValue()));
            } else {
                float dt = mySimData.get_speed(LaneGroupType.mng).get_dt();
                dataSeries_mng.getData().add(new XYChart.Data(Misc.seconds2timestring(start+i*dt, ":"), 0));
            }
            if (i < sz_aux) {
                xy = xydata_aux.get(i);
                dataSeries_aux.getData().add(new XYChart.Data(Misc.seconds2timestring(start+(float)xy.getXValue(), ":"), cc*xy.getYValue()));
            } else {
                float dt = mySimData.get_speed(LaneGroupType.aux).get_dt();
                dataSeries_aux.getData().add(new XYChart.Data(Misc.seconds2timestring(start+i*dt, ":"), 0));
            }
        }

        speedChart.getData().add(dataSeries_gp);
        if (myLink.get_mng_lanes() > 0)
            speedChart.getData().add(dataSeries_mng);
        if (myLink.get_aux_lanes() > 0)
            speedChart.getData().add(dataSeries_aux);
        speedChart.setCreateSymbols(false);
        speedChart.setLegendSide(Side.RIGHT);
        speedChart.setMinHeight(200);
        vbTimeSeries.getChildren().add(speedChart);
        
        
        label_gp = "Flow in GP Lanes";
        label_mng = "Flow in Managed Lanes";
        label_aux = "Flow in Aux Lanes";
        label_units = UserSettings.unitsFlow;
        cc = UserSettings.flowConversionMap.get("vph"+label_units);
        
        xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        yAxis = new NumberAxis();
        yAxis.setLabel("Flow (" + label_units + ")");

        LineChart flowChart = new LineChart(xAxis, yAxis);
        flowChart.setTitle(label_gp);
        
        max_sz = 0;
        for (Commodity c : listVT) {
            max_sz = Math.max(max_sz, mySimData.get_flw_exiting(LaneGroupType.gp, c.getId()).values.size());
        }
        for (Commodity c : listVT) {
            dataSeries_gp = new XYChart.Series();
            dataSeries_gp.setName(c.get_name());
            xydata_gp = mySimData.get_flw_exiting(LaneGroupType.gp, c.getId()).get_XYSeries(c.get_name()).getItems();
            
            sz_gp = xydata_gp.size();
            
            for (int i = 0; i < max_sz; i++) {
                if (i < sz_gp) {
                    xy = xydata_gp.get(i);
                    dataSeries_gp.getData().add(new XYChart.Data(Misc.seconds2timestring(start+(float)xy.getXValue(), ":"), cc*xy.getYValue()));
                } else {
                    float dt = mySimData.get_flw_exiting(LaneGroupType.gp, c.getId()).get_dt();
                    dataSeries_gp.getData().add(new XYChart.Data(Misc.seconds2timestring(start+i*dt, ":"), 0));
                }
            }
            flowChart.getData().add(dataSeries_gp);
        }
        flowChart.setCreateSymbols(false);
        flowChart.setLegendSide(Side.RIGHT);
        flowChart.setMinHeight(200);
        vbTimeSeries.getChildren().add(flowChart);
        
        if (myLink.get_mng_lanes() > 0) {
            xAxis = new CategoryAxis();
            xAxis.setLabel("Time");
            yAxis = new NumberAxis();
            yAxis.setLabel("Flow (" + label_units + ")");

            flowChart = new LineChart(xAxis, yAxis);
            flowChart.setTitle(label_mng);

            max_sz = 0;
            for (Commodity c : listVT) {
                max_sz = Math.max(max_sz, mySimData.get_flw_exiting(LaneGroupType.mng, c.getId()).values.size());
            }
            for (Commodity c : listVT) {
                dataSeries_mng = new XYChart.Series();
                dataSeries_mng.setName(c.get_name());
                xydata_mng = mySimData.get_flw_exiting(LaneGroupType.mng, c.getId()).get_XYSeries(c.get_name()).getItems();

                sz_mng = xydata_mng.size();

                for (int i = 0; i < max_sz; i++) {
                    if (i < sz_mng) {
                        xy = xydata_mng.get(i);
                        dataSeries_mng.getData().add(new XYChart.Data(Misc.seconds2timestring(start+(float)xy.getXValue(), ":"), cc*xy.getYValue()));
                    } else {
                        float dt = mySimData.get_flw_exiting(LaneGroupType.mng, c.getId()).get_dt();
                        dataSeries_mng.getData().add(new XYChart.Data(Misc.seconds2timestring(start+i*dt, ":"), 0));
                    }
                }
                flowChart.getData().add(dataSeries_mng);
            }
            flowChart.setCreateSymbols(false);
            flowChart.setLegendSide(Side.RIGHT);
            flowChart.setMinHeight(200);
            vbTimeSeries.getChildren().add(flowChart);
        }
        
        if (myLink.get_aux_lanes() > 0) {
            xAxis = new CategoryAxis();
            xAxis.setLabel("Time");
            yAxis = new NumberAxis();
            yAxis.setLabel("Flow (" + label_units + ")");

            flowChart = new LineChart(xAxis, yAxis);
            flowChart.setTitle(label_aux);

            max_sz = 0;
            for (Commodity c : listVT) {
                max_sz = Math.max(max_sz, mySimData.get_flw_exiting(LaneGroupType.aux, c.getId()).values.size());
            }
            for (Commodity c : listVT) {
                dataSeries_aux = new XYChart.Series();
                dataSeries_aux.setName(c.get_name());
                xydata_aux = mySimData.get_flw_exiting(LaneGroupType.aux, c.getId()).get_XYSeries(c.get_name()).getItems();

                sz_aux = xydata_aux.size();

                for (int i = 0; i < max_sz; i++) {
                    if (i < sz_aux) {
                        xy = xydata_aux.get(i);
                        dataSeries_aux.getData().add(new XYChart.Data(Misc.seconds2timestring(start+(float)xy.getXValue(), ":"), cc*xy.getYValue()));
                    } else {
                        float dt = mySimData.get_flw_exiting(LaneGroupType.aux, c.getId()).get_dt();
                        dataSeries_aux.getData().add(new XYChart.Data(Misc.seconds2timestring(start+i*dt, ":"), 0));
                    }
                }
                flowChart.getData().add(dataSeries_aux);
            }
            flowChart.setCreateSymbols(false);
            flowChart.setLegendSide(Side.RIGHT);
            flowChart.setMinHeight(200);
            vbTimeSeries.getChildren().add(flowChart);
        }

        
        label_gp = "Vehicles in GP Lanes";
        label_mng = "Vehicles in Managed Lanes";
        label_aux = "Vehicles in Aux Lanes";
        
        xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        yAxis = new NumberAxis();
        yAxis.setLabel("Number of Vehicles");

        LineChart vehChart = new LineChart(xAxis, yAxis);
        vehChart.setTitle(label_gp);
        
        max_sz = 0;
        for (Commodity c : listVT) {
            max_sz = Math.max(max_sz, mySimData.get_veh(LaneGroupType.gp, c.getId()).values.size());
        }
        for (Commodity c : listVT) {
            dataSeries_gp = new XYChart.Series();
            dataSeries_gp.setName(c.get_name());
            xydata_gp = mySimData.get_veh(LaneGroupType.gp, c.getId()).get_XYSeries(c.get_name()).getItems();
            
            sz_gp = xydata_gp.size();
            
            for (int i = 0; i < max_sz; i++) {
                if (i < sz_gp) {
                    xy = xydata_gp.get(i);
                    dataSeries_gp.getData().add(new XYChart.Data(Misc.seconds2timestring(start+(float)xy.getXValue(), ":"), cc*xy.getYValue()));
                } else {
                    float dt = mySimData.get_veh(LaneGroupType.gp, c.getId()).get_dt();
                    dataSeries_gp.getData().add(new XYChart.Data(Misc.seconds2timestring(start+i*dt, ":"), 0));
                }
            }
            vehChart.getData().add(dataSeries_gp);
        }
        vehChart.setCreateSymbols(false);
        vehChart.setLegendSide(Side.RIGHT);
        vehChart.setMinHeight(200);
        vbTimeSeries.getChildren().add(vehChart);
        
        if (myLink.get_mng_lanes() > 0) {
            xAxis = new CategoryAxis();
            xAxis.setLabel("Time");
            yAxis = new NumberAxis();
            yAxis.setLabel("Number of Vehicles");

            vehChart = new LineChart(xAxis, yAxis);
            vehChart.setTitle(label_mng);

            max_sz = 0;
            for (Commodity c : listVT) {
                max_sz = Math.max(max_sz, mySimData.get_veh(LaneGroupType.mng, c.getId()).values.size());
            }
            for (Commodity c : listVT) {
                dataSeries_mng = new XYChart.Series();
                dataSeries_mng.setName(c.get_name());
                xydata_mng = mySimData.get_veh(LaneGroupType.mng, c.getId()).get_XYSeries(c.get_name()).getItems();

                sz_mng = xydata_mng.size();

                for (int i = 0; i < max_sz; i++) {
                    if (i < sz_mng) {
                        xy = xydata_mng.get(i);
                        dataSeries_mng.getData().add(new XYChart.Data(Misc.seconds2timestring(start+(float)xy.getXValue(), ":"), cc*xy.getYValue()));
                    } else {
                        float dt = mySimData.get_veh(LaneGroupType.mng, c.getId()).get_dt();
                        dataSeries_mng.getData().add(new XYChart.Data(Misc.seconds2timestring(start+i*dt, ":"), 0));
                    }
                }
                vehChart.getData().add(dataSeries_mng);
            }
            vehChart.setCreateSymbols(false);
            vehChart.setLegendSide(Side.RIGHT);
            vehChart.setMinHeight(200);
            vbTimeSeries.getChildren().add(vehChart);
        }

        if (myLink.get_aux_lanes() > 0) {
            xAxis = new CategoryAxis();
            xAxis.setLabel("Time");
            yAxis = new NumberAxis();
            yAxis.setLabel("Number of Vehicles");

            vehChart = new LineChart(xAxis, yAxis);
            vehChart.setTitle(label_aux);

            max_sz = 0;
            for (Commodity c : listVT) {
                max_sz = Math.max(max_sz, mySimData.get_veh(LaneGroupType.aux, c.getId()).values.size());
            }
            for (Commodity c : listVT) {
                dataSeries_aux = new XYChart.Series();
                dataSeries_aux.setName(c.get_name());
                xydata_aux = mySimData.get_veh(LaneGroupType.aux, c.getId()).get_XYSeries(c.get_name()).getItems();

                sz_aux = xydata_aux.size();

                for (int i = 0; i < max_sz; i++) {
                    if (i < sz_aux) {
                        xy = xydata_aux.get(i);
                        dataSeries_aux.getData().add(new XYChart.Data(Misc.seconds2timestring(start+(float)xy.getXValue(), ":"), cc*xy.getYValue()));
                    } else {
                        float dt = mySimData.get_veh(LaneGroupType.aux, c.getId()).get_dt();
                        dataSeries_aux.getData().add(new XYChart.Data(Misc.seconds2timestring(start+i*dt, ":"), 0));
                    }
                }
                vehChart.getData().add(dataSeries_aux);
            }
            vehChart.setCreateSymbols(false);
            vehChart.setLegendSide(Side.RIGHT);
            vehChart.setMinHeight(200);
            vbTimeSeries.getChildren().add(vehChart);
        }
    }
     
    
    
    public void fillTabAggregates() {
        String label_gp, label_mng, label_aux;
        vbAggregates.getChildren().clear();
        
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Speed");

        LineChart speedChart = new LineChart(xAxis, yAxis);
        
        label_gp = "Speed in GP Lanes";
        label_mng = "Speed in Managed Lanes";
        label_aux = "Speed in Aux Lanes";

        XYChart.Series dataSeries_gp = new XYChart.Series();
        dataSeries_gp.setName(label_gp);
        List<XYDataItem> xydata_gp = mySimData.get_speed(LaneGroupType.gp).get_XYSeries(label_gp).getItems();
        
        XYChart.Series dataSeries_mng = new XYChart.Series();
        dataSeries_mng.setName(label_mng);
        List<XYDataItem> xydata_mng = mySimData.get_speed(LaneGroupType.mng).get_XYSeries(label_mng).getItems();
        
        XYChart.Series dataSeries_aux = new XYChart.Series();
        dataSeries_aux.setName(label_aux);
        List<XYDataItem> xydata_aux = mySimData.get_speed(LaneGroupType.gp).get_XYSeries(label_aux).getItems();
        
        int sz_gp = xydata_gp.size();
        int sz_mng = xydata_gp.size();
        int sz_aux = xydata_gp.size();
        
        for (int i = 0; i < sz_gp; i++) {
            XYDataItem xy = xydata_gp.get(i);
            dataSeries_gp.getData().add(new XYChart.Data(xy.getX(), xy.getY()));
            if (i < sz_mng) {
                xy = xydata_mng.get(i);
                dataSeries_mng.getData().add(new XYChart.Data(xy.getX(), xy.getYValue()*0.5));
            }
            if (i < sz_aux) {
                xy = xydata_aux.get(i);
                dataSeries_aux.getData().add(new XYChart.Data(xy.getX(), xy.getY()));
            }
        }

        speedChart.getData().add(dataSeries_gp);
        if (myLink.get_mng_lanes() > 0)
            speedChart.getData().add(dataSeries_mng);
        if (myLink.get_aux_lanes() > 0)
            speedChart.getData().add(dataSeries_aux);
        speedChart.setCreateSymbols(false);
        speedChart.setLegendSide(Side.RIGHT);

        vbAggregates.getChildren().add(speedChart);

        
    }
        
        










}




