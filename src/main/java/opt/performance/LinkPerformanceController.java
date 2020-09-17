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

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
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
import opt.data.LaneGroupType;
import opt.utils.Misc;
import opt.UserSettings;
import opt.data.Commodity;
import opt.utils.jfxutils.chart.JFXChartUtil;
import org.jfree.data.xy.XYDataItem;
import profiles.Profile1D;

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
    private float start = 0;
    
    private List<Commodity> listVT = null;
     
    private String timeLabel;
    private double timeDivider;
    
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
        start = myLink.get_segment().get_scenario().get_start_time();
        
        timeLabel = "Time (hours)";
        timeDivider = 3600.0;
        if (sdata.fwyscenario.get_sim_duration() <= 7200) {
            timeLabel = "Time (minutes)";
            timeDivider = 60.0;
        }
        
        fillTabTimeseries();
        fillTabAggregates();
        //fillTabEmissions();
    }
    
    
    
    public void fillTabTimeseries() {
//        String label_gp, label_mng, label_aux, label_units;
//        double cc;
//        XYChart.Series dataSeries_total;
//        vbTimeSeries.getChildren().clear();
//
//        label_gp = "GP Lanes";
//        label_mng = "Managed Lanes";
//        label_aux = "Aux Lanes";
//        label_units = UserSettings.unitsSpeed;
//        cc = UserSettings.speedConversionMap.get("mph"+label_units);
//
//        //CategoryAxis xAxis = new CategoryAxis();
//        NumberAxis xAxis = new NumberAxis();
//        xAxis.setLabel(timeLabel);
//        NumberAxis yAxis = new NumberAxis();
//        yAxis.setLabel("Speed (" + label_units + ")");
//
//        LineChart speedChart = new LineChart(xAxis, yAxis);
//        speedChart.setTitle("Speed");
//
//        XYChart.Series dataSeries_gp = new XYChart.Series();
//        dataSeries_gp.setName(label_gp);
//        List<XYDataItem> xydata_gp = mySimData.get_speed(LaneGroupType.gp).get_XYSeries(label_gp).getItems();
//
//        XYChart.Series dataSeries_mng = new XYChart.Series();
//        dataSeries_mng.setName(label_mng);
//        List<XYDataItem> xydata_mng = mySimData.get_speed(LaneGroupType.mng).get_XYSeries(label_mng).getItems();
//
//        XYChart.Series dataSeries_aux = new XYChart.Series();
//        dataSeries_aux.setName(label_aux);
//        List<XYDataItem> xydata_aux = mySimData.get_speed(LaneGroupType.gp).get_XYSeries(label_aux).getItems();
//
//        int sz_gp = xydata_gp.size();
//        int sz_mng = xydata_mng.size();
//        int sz_aux = xydata_aux.size();
//        int max_sz = Math.max(Math.max(sz_gp, sz_mng), sz_aux);
//
//        XYDataItem xy;
//        float dt = mySimData.get_speed(LaneGroupType.gp).get_dt();
//        for (int i = 0; i < max_sz; i++) {
//            if (i < sz_gp) {
//                xy = xydata_gp.get(i);
//                //dataSeries_gp.getData().add(new XYChart.Data(Misc.seconds2timestring(start+(float)xy.getXValue(), ":"), cc*xy.getYValue()));
//                dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, cc*xy.getYValue()));
//            } else {
//                //dataSeries_gp.getData().add(new XYChart.Data(Misc.seconds2timestring(start+i*dt, ":"), 0));
//                dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//            }
//            if (i < sz_mng) {
//                xy = xydata_mng.get(i);
//                dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, cc*xy.getYValue()));
//            } else {
//                dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//            }
//            if (i < sz_aux) {
//                xy = xydata_aux.get(i);
//                dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, cc*xy.getYValue()));
//            } else {
//                dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//            }
//        }
//
//        speedChart.getData().add(dataSeries_gp);
//        if (myLink.get_mng_lanes() > 0)
//            speedChart.getData().add(dataSeries_mng);
//        if (myLink.get_aux_lanes() > 0)
//            speedChart.getData().add(dataSeries_aux);
//        speedChart.setCreateSymbols(false);
//        speedChart.setLegendSide(Side.RIGHT);
//        speedChart.setMinHeight(200);
//
//        /*final double SCALE_DELTA = 1.1;
//        final StackPane zoomPane = new StackPane(speedChart);
//        zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
//            @Override public void handle(ScrollEvent event) {
//                event.consume();
//                if (event.getDeltaY() == 0) {
//                    return;
//                }
//
//                double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1/SCALE_DELTA;
//
//                speedChart.setScaleX(speedChart.getScaleX() * scaleFactor);
//                speedChart.setScaleY(speedChart.getScaleY() * scaleFactor);
//            }
//        });*/
//
//        if (myLink.get_up_link() != null) // source links have no speed display
//            vbTimeSeries.getChildren().add(speedChart);
//
//        //Zooming works only via primary mouse button without ctrl held down
//        JFXChartUtil.setupZooming(speedChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//        });
//        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(speedChart);
//
//
//        LineChart flowChart;
//
//        if (myLink.get_up_link() == null) { // source link
//            label_gp = "Demand";
//            label_units = UserSettings.unitsFlow;
//            cc = UserSettings.flowConversionMap.get("vph"+label_units);
//
//            xAxis = new NumberAxis();
//            xAxis.setLabel(timeLabel);
//            yAxis = new NumberAxis();
//            yAxis.setLabel("Demand (" + label_units + ")");
//
//            flowChart = new LineChart(xAxis, yAxis);
//            flowChart.setTitle(label_gp);
//
//            max_sz = 0;
//            for (Commodity c : listVT) {
//                max_sz = Math.max(max_sz, mySimData.get_flw(LaneGroupType.gp, c.getId()).values.size());
//            }
//            dt = mySimData.get_flw(LaneGroupType.gp, listVT.get(0).getId()).get_dt();
//            double[] total = new double[max_sz];
//            for (int i = 0; i < max_sz; i++)
//                total[i] = 0;
//            for (Commodity c : listVT) {
//                dataSeries_gp = new XYChart.Series();
//                dataSeries_gp.setName(c.get_name());
//                Profile1D demand = myLink.get_demand_vph(c.getId());
//
//                for (int i = 0; i < max_sz; i++) {
//                    float ts = start + i*dt;
//                    double val = 0;
//                    if (demand != null)
//                        val = demand.get_value_for_time(ts);
//                    if (Double.isNaN(val))
//                        val = 0;
//                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, cc*val));
//                    total[i] += cc*val;
//                }
//                flowChart.getData().add(dataSeries_gp);
//            }
//
//            dataSeries_total = new XYChart.Series();
//            dataSeries_total.setName("Total");
//            for (int i = 0; i < max_sz; i++)
//                dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//            if (listVT.size() > 1)
//                flowChart.getData().add(dataSeries_total);
//
//            flowChart.setCreateSymbols(false);
//            flowChart.setLegendSide(Side.RIGHT);
//            flowChart.setMinHeight(200);
//            vbTimeSeries.getChildren().add(flowChart);
//            JFXChartUtil.setupZooming(flowChart, (MouseEvent mouseEvent) -> {
//                if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                        mouseEvent.isShortcutDown() )
//                    mouseEvent.consume();
//            });
//            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(flowChart);
//
//        }
//
//
//        label_gp = "Flow in GP Lanes";
//        label_mng = "Flow in Managed Lanes";
//        label_aux = "Flow in Aux Lanes";
//        label_units = UserSettings.unitsFlow;
//        cc = UserSettings.flowConversionMap.get("vph"+label_units);
//
//        xAxis = new NumberAxis();
//        xAxis.setLabel(timeLabel);
//        yAxis = new NumberAxis();
//        yAxis.setLabel("Flow (" + label_units + ")");
//
//        flowChart = new LineChart(xAxis, yAxis);
//        flowChart.setTitle(label_gp);
//
//        max_sz = 0;
//        for (Commodity c : listVT) {
//            max_sz = Math.max(max_sz, mySimData.get_flw(LaneGroupType.gp, c.getId()).values.size());
//        }
//        double[] total = new double[max_sz];
//        for (int i = 0; i < max_sz; i++)
//            total[i] = 0;
//        for (Commodity c : listVT) {
//            dt = mySimData.get_flw(LaneGroupType.gp, c.getId()).get_dt();
//            dataSeries_gp = new XYChart.Series();
//            dataSeries_gp.setName(c.get_name());
//            xydata_gp = mySimData.get_flw(LaneGroupType.gp, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//            sz_gp = xydata_gp.size();
//
//            for (int i = 0; i < max_sz; i++) {
//                if (i < sz_gp) {
//                    xy = xydata_gp.get(i);
//                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, cc*xy.getYValue()));
//                    total[i] += cc*xy.getYValue();
//                } else {
//                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                }
//            }
//            flowChart.getData().add(dataSeries_gp);
//        }
//
//        dataSeries_total = new XYChart.Series();
//        dataSeries_total.setName("Total");
//        for (int i = 0; i < max_sz; i++)
//            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//        if (listVT.size() > 1)
//            flowChart.getData().add(dataSeries_total);
//
//        flowChart.setCreateSymbols(false);
//        flowChart.setLegendSide(Side.RIGHT);
//        flowChart.setMinHeight(200);
//        vbTimeSeries.getChildren().add(flowChart);
//        JFXChartUtil.setupZooming(flowChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//        });
//        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(flowChart);
//
//        if (myLink.get_mng_lanes() > 0) {
//            xAxis = new NumberAxis();
//            xAxis.setLabel(timeLabel);
//            yAxis = new NumberAxis();
//            yAxis.setLabel("Flow (" + label_units + ")");
//
//            flowChart = new LineChart(xAxis, yAxis);
//            flowChart.setTitle(label_mng);
//
//            max_sz = 0;
//            for (Commodity c : listVT) {
//                max_sz = Math.max(max_sz, mySimData.get_flw(LaneGroupType.mng, c.getId()).values.size());
//            }
//            for (int i = 0; i < max_sz; i++)
//                total[i] = 0;
//            for (Commodity c : listVT) {
//                dataSeries_mng = new XYChart.Series();
//                dataSeries_mng.setName(c.get_name());
//                xydata_mng = mySimData.get_flw(LaneGroupType.mng, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//                sz_mng = xydata_mng.size();
//
//                for (int i = 0; i < max_sz; i++) {
//                    if (i < sz_mng) {
//                        xy = xydata_mng.get(i);
//                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, cc*xy.getYValue()));
//                        total[i] += cc*xy.getYValue();
//                    } else {
//                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                    }
//                }
//                flowChart.getData().add(dataSeries_mng);
//            }
//
//            dataSeries_total = new XYChart.Series();
//            dataSeries_total.setName("Total");
//            for (int i = 0; i < max_sz; i++)
//                dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//            if (listVT.size() > 1)
//                flowChart.getData().add(dataSeries_total);
//
//            flowChart.setCreateSymbols(false);
//            flowChart.setLegendSide(Side.RIGHT);
//            flowChart.setMinHeight(200);
//            vbTimeSeries.getChildren().add(flowChart);
//            JFXChartUtil.setupZooming(flowChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//            });
//            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(flowChart);
//        }
//
//        if (myLink.get_aux_lanes() > 0) {
//            xAxis = new NumberAxis();
//            xAxis.setLabel(timeLabel);
//            yAxis = new NumberAxis();
//            yAxis.setLabel("Flow (" + label_units + ")");
//
//            flowChart = new LineChart(xAxis, yAxis);
//            flowChart.setTitle(label_aux);
//
//            max_sz = 0;
//            for (Commodity c : listVT) {
//                max_sz = Math.max(max_sz, mySimData.get_flw(LaneGroupType.aux, c.getId()).values.size());
//            }
//            for (int i = 0; i < max_sz; i++)
//                total[i] = 0;
//            for (Commodity c : listVT) {
//                dataSeries_aux = new XYChart.Series();
//                dataSeries_aux.setName(c.get_name());
//                xydata_aux = mySimData.get_flw(LaneGroupType.aux, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//                sz_aux = xydata_aux.size();
//
//                for (int i = 0; i < max_sz; i++) {
//                    if (i < sz_aux) {
//                        xy = xydata_aux.get(i);
//                        dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, cc*xy.getYValue()));
//                        total[i] += cc*xy.getYValue();
//                    } else {
//                        dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                    }
//                }
//                flowChart.getData().add(dataSeries_aux);
//            }
//
//            dataSeries_total = new XYChart.Series();
//            dataSeries_total.setName("Total");
//            for (int i = 0; i < max_sz; i++)
//                dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//            if (listVT.size() > 1)
//                flowChart.getData().add(dataSeries_total);
//
//            flowChart.setCreateSymbols(false);
//            flowChart.setLegendSide(Side.RIGHT);
//            flowChart.setMinHeight(200);
//            vbTimeSeries.getChildren().add(flowChart);
//            JFXChartUtil.setupZooming(flowChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//            });
//            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(flowChart);
//        }
//
//
//        label_gp = "Vehicles in GP Lanes";
//        label_mng = "Vehicles in Managed Lanes";
//        label_aux = "Vehicles in Aux Lanes";
//        if ((myLink.get_up_link() == null) && (myLink.get_type() == AbstractLink.Type.onramp)) { // source link
//            label_gp = "GP Lane Vehicle Queue";
//            label_mng = "Managed Lane Vehicle Queue";
//        }
//
//        xAxis = new NumberAxis();
//        xAxis.setLabel(timeLabel);
//        yAxis = new NumberAxis();
//        yAxis.setLabel("Number of Vehicles");
//
//        LineChart vehChart = new LineChart(xAxis, yAxis);
//        vehChart.setTitle(label_gp);
//
//        max_sz = 0;
//        for (Commodity c : listVT) {
//            max_sz = Math.max(max_sz, mySimData.get_veh(LaneGroupType.gp, c.getId()).values.size());
//        }
//        for (int i = 0; i < max_sz; i++)
//            total[i] = 0;
//        for (Commodity c : listVT) {
//            dataSeries_gp = new XYChart.Series();
//            dataSeries_gp.setName(c.get_name());
//            xydata_gp = mySimData.get_veh(LaneGroupType.gp, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//            sz_gp = xydata_gp.size();
//
//            for (int i = 0; i < max_sz; i++) {
//                if (i < sz_gp) {
//                    xy = xydata_gp.get(i);
//                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                    total[i] += xy.getYValue();
//                } else {
//                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                }
//            }
//            vehChart.getData().add(dataSeries_gp);
//        }
//
//        dataSeries_total = new XYChart.Series();
//        dataSeries_total.setName("Total");
//        for (int i = 0; i < max_sz; i++)
//            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//        if (listVT.size() > 1)
//            vehChart.getData().add(dataSeries_total);
//
//        vehChart.setCreateSymbols(false);
//        vehChart.setLegendSide(Side.RIGHT);
//        vehChart.setMinHeight(200);
//        vbTimeSeries.getChildren().add(vehChart);
//        JFXChartUtil.setupZooming(vehChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//        });
//        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vehChart);
//
//        if (myLink.get_mng_lanes() > 0) {
//            xAxis = new NumberAxis();
//            xAxis.setLabel(timeLabel);
//            yAxis = new NumberAxis();
//            yAxis.setLabel("Number of Vehicles");
//
//            vehChart = new LineChart(xAxis, yAxis);
//            vehChart.setTitle(label_mng);
//
//            max_sz = 0;
//            for (Commodity c : listVT) {
//                max_sz = Math.max(max_sz, mySimData.get_veh(LaneGroupType.mng, c.getId()).values.size());
//            }
//            for (int i = 0; i < max_sz; i++)
//                total[i] = 0;
//            for (Commodity c : listVT) {
//                dataSeries_mng = new XYChart.Series();
//                dataSeries_mng.setName(c.get_name());
//                xydata_mng = mySimData.get_veh(LaneGroupType.mng, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//                sz_mng = xydata_mng.size();
//
//                for (int i = 0; i < max_sz; i++) {
//                    if (i < sz_mng) {
//                        xy = xydata_mng.get(i);
//                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                        total[i] += xy.getYValue();
//                    } else {
//                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                    }
//                }
//                vehChart.getData().add(dataSeries_mng);
//            }
//
//            dataSeries_total = new XYChart.Series();
//            dataSeries_total.setName("Total");
//            for (int i = 0; i < max_sz; i++)
//                dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//            if (listVT.size() > 1)
//                vehChart.getData().add(dataSeries_total);
//
//            vehChart.setCreateSymbols(false);
//            vehChart.setLegendSide(Side.RIGHT);
//            vehChart.setMinHeight(200);
//            vbTimeSeries.getChildren().add(vehChart);
//            JFXChartUtil.setupZooming(vehChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//            });
//            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vehChart);
//        }
//
//        if (myLink.get_aux_lanes() > 0) {
//            xAxis = new NumberAxis();
//            xAxis.setLabel(timeLabel);
//            yAxis = new NumberAxis();
//            yAxis.setLabel("Number of Vehicles");
//
//            vehChart = new LineChart(xAxis, yAxis);
//            vehChart.setTitle(label_aux);
//
//            max_sz = 0;
//            for (Commodity c : listVT) {
//                max_sz = Math.max(max_sz, mySimData.get_veh(LaneGroupType.aux, c.getId()).values.size());
//            }
//            for (int i = 0; i < max_sz; i++)
//                total[i] = 0;
//            for (Commodity c : listVT) {
//                dataSeries_aux = new XYChart.Series();
//                dataSeries_aux.setName(c.get_name());
//                xydata_aux = mySimData.get_veh(LaneGroupType.aux, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//                sz_aux = xydata_aux.size();
//
//                for (int i = 0; i < max_sz; i++) {
//                    if (i < sz_aux) {
//                        xy = xydata_aux.get(i);
//                        dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                        total[i] += xy.getYValue();
//                    } else {
//                        dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                    }
//                }
//                vehChart.getData().add(dataSeries_aux);
//            }
//
//            dataSeries_total = new XYChart.Series();
//            dataSeries_total.setName("Total");
//            for (int i = 0; i < max_sz; i++)
//                dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//            if (listVT.size() > 1)
//                vehChart.getData().add(dataSeries_total);
//
//            vehChart.setCreateSymbols(false);
//            vehChart.setLegendSide(Side.RIGHT);
//            vehChart.setMinHeight(200);
//            vbTimeSeries.getChildren().add(vehChart);
//            JFXChartUtil.setupZooming(vehChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//            });
//            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vehChart);
//        }
    }
    
     
    
    
    public void fillTabAggregates() {
//        vbAggregates.getChildren().clear();
//        String label_gp, label_mng, label_aux;
//        XYChart.Series dataSeries_gp, dataSeries_mng, dataSeries_aux, dataSeries_total;
//        List<XYDataItem> xydata_gp, xydata_mng, xydata_aux;
//        int sz_gp, sz_mng, sz_aux;
//        XYDataItem xy;
//        float dt = mySimData.get_speed(LaneGroupType.gp).get_dt();
//
//        label_gp = "VMT in GP Lanes";
//        label_mng = "VMT in Managed Lanes";
//        label_aux = "VMT in Aux Lanes";
//
//        NumberAxis xAxis = new NumberAxis();
//        xAxis.setLabel(timeLabel);
//        NumberAxis yAxis = new NumberAxis();
//        yAxis.setLabel("VMT");
//
//        LineChart vmtChart = new LineChart(xAxis, yAxis);
//        vmtChart.setTitle(label_gp);
//
//        int max_sz = 0;
//        for (Commodity c : listVT) {
//            max_sz = Math.max(max_sz, mySimData.get_vmt(LaneGroupType.gp, c.getId()).values.size());
//        }
//        double[] total = new double[max_sz];
//        for (int i = 0; i < max_sz; i++)
//            total[i] = 0;
//        for (Commodity c : listVT) {
//            dt = mySimData.get_vmt(LaneGroupType.gp, c.getId()).get_dt();
//            dataSeries_gp = new XYChart.Series();
//            dataSeries_gp.setName(c.get_name());
//            xydata_gp = mySimData.get_vmt(LaneGroupType.gp, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//            sz_gp = xydata_gp.size();
//
//            for (int i = 0; i < max_sz; i++) {
//                if (i < sz_gp) {
//                    xy = xydata_gp.get(i);
//                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                    total[i] += xy.getYValue();
//                } else {
//                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                }
//            }
//            vmtChart.getData().add(dataSeries_gp);
//        }
//
//        dataSeries_total = new XYChart.Series();
//        dataSeries_total.setName("Total");
//        for (int i = 0; i < max_sz; i++)
//            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//        if (listVT.size() > 1)
//            vmtChart.getData().add(dataSeries_total);
//
//        vmtChart.setCreateSymbols(false);
//        vmtChart.setLegendSide(Side.RIGHT);
//        vmtChart.setMinHeight(200);
//        vbAggregates.getChildren().add(vmtChart);
//        JFXChartUtil.setupZooming(vmtChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//        });
//        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vmtChart);
//
//        if (myLink.get_mng_lanes() > 0) {
//            xAxis = new NumberAxis();
//            xAxis.setLabel(timeLabel);
//            yAxis = new NumberAxis();
//            yAxis.setLabel("VMT");
//
//            vmtChart = new LineChart(xAxis, yAxis);
//            vmtChart.setTitle(label_mng);
//
//            max_sz = 0;
//            for (Commodity c : listVT) {
//                max_sz = Math.max(max_sz, mySimData.get_vmt(LaneGroupType.mng, c.getId()).values.size());
//            }
//            for (int i = 0; i < max_sz; i++)
//                total[i] = 0;
//            for (Commodity c : listVT) {
//                dataSeries_mng = new XYChart.Series();
//                dataSeries_mng.setName(c.get_name());
//                xydata_mng = mySimData.get_vmt(LaneGroupType.mng, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//                sz_mng = xydata_mng.size();
//
//                for (int i = 0; i < max_sz; i++) {
//                    if (i < sz_mng) {
//                        xy = xydata_mng.get(i);
//                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                        total[i] += xy.getYValue();
//                    } else {
//                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                    }
//                }
//                vmtChart.getData().add(dataSeries_mng);
//            }
//
//            dataSeries_total = new XYChart.Series();
//            dataSeries_total.setName("Total");
//            for (int i = 0; i < max_sz; i++)
//                dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//            if (listVT.size() > 1)
//                vmtChart.getData().add(dataSeries_total);
//
//            vmtChart.setCreateSymbols(false);
//            vmtChart.setLegendSide(Side.RIGHT);
//            vmtChart.setMinHeight(200);
//            vbAggregates.getChildren().add(vmtChart);
//            JFXChartUtil.setupZooming(vmtChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//            });
//            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vmtChart);
//        }
//
//        if (myLink.get_aux_lanes() > 0) {
//            xAxis = new NumberAxis();
//            xAxis.setLabel(timeLabel);
//            yAxis = new NumberAxis();
//            yAxis.setLabel("VMT");
//
//            vmtChart = new LineChart(xAxis, yAxis);
//            vmtChart.setTitle(label_aux);
//
//            max_sz = 0;
//            for (Commodity c : listVT) {
//                max_sz = Math.max(max_sz, mySimData.get_vmt(LaneGroupType.aux, c.getId()).values.size());
//            }
//            for (int i = 0; i < max_sz; i++)
//                total[i] = 0;
//            for (Commodity c : listVT) {
//                dataSeries_aux = new XYChart.Series();
//                dataSeries_aux.setName(c.get_name());
//                xydata_aux = mySimData.get_vmt(LaneGroupType.aux, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//                sz_aux = xydata_aux.size();
//
//                for (int i = 0; i < max_sz; i++) {
//                    if (i < sz_aux) {
//                        xy = xydata_aux.get(i);
//                        dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                        total[i] += xy.getYValue();
//                    } else {
//                        dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                    }
//                }
//                vmtChart.getData().add(dataSeries_aux);
//            }
//
//            dataSeries_total = new XYChart.Series();
//            dataSeries_total.setName("Total");
//            for (int i = 0; i < max_sz; i++)
//                dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//            if (listVT.size() > 1)
//                vmtChart.getData().add(dataSeries_total);
//
//            vmtChart.setCreateSymbols(false);
//            vmtChart.setLegendSide(Side.RIGHT);
//            vmtChart.setMinHeight(200);
//            vbAggregates.getChildren().add(vmtChart);
//            JFXChartUtil.setupZooming(vmtChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//            });
//            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vmtChart);
//        }
//
//
//        label_gp = "VHT in GP Lanes";
//        label_mng = "VHT in Managed Lanes";
//        label_aux = "VHT in Aux Lanes";
//
//        xAxis = new NumberAxis();
//        xAxis.setLabel(timeLabel);
//        yAxis = new NumberAxis();
//        yAxis.setLabel("VHT");
//
//        LineChart vhtChart = new LineChart(xAxis, yAxis);
//        vhtChart.setTitle(label_gp);
//
//        max_sz = 0;
//        for (Commodity c : listVT) {
//            max_sz = Math.max(max_sz, mySimData.get_vht(LaneGroupType.gp, c.getId()).values.size());
//        }
//        total = new double[max_sz];
//        for (int i = 0; i < max_sz; i++)
//            total[i] = 0;
//        for (Commodity c : listVT) {
//            dt = mySimData.get_vht(LaneGroupType.gp, c.getId()).get_dt();
//            dataSeries_gp = new XYChart.Series();
//            dataSeries_gp.setName(c.get_name());
//            xydata_gp = mySimData.get_vht(LaneGroupType.gp, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//            sz_gp = xydata_gp.size();
//
//            for (int i = 0; i < max_sz; i++) {
//                if (i < sz_gp) {
//                    xy = xydata_gp.get(i);
//                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                    total[i] += xy.getYValue();
//                } else {
//                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                }
//            }
//            vhtChart.getData().add(dataSeries_gp);
//        }
//
//        dataSeries_total = new XYChart.Series();
//        dataSeries_total.setName("Total");
//        for (int i = 0; i < max_sz; i++)
//            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//        if (listVT.size() > 1)
//            vhtChart.getData().add(dataSeries_total);
//
//        vhtChart.setCreateSymbols(false);
//        vhtChart.setLegendSide(Side.RIGHT);
//        vhtChart.setMinHeight(200);
//        vbAggregates.getChildren().add(vhtChart);
//        JFXChartUtil.setupZooming(vhtChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//        });
//        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vhtChart);
//
//        if (myLink.get_mng_lanes() > 0) {
//            xAxis = new NumberAxis();
//            xAxis.setLabel(timeLabel);
//            yAxis = new NumberAxis();
//            yAxis.setLabel("VHT");
//
//            vhtChart = new LineChart(xAxis, yAxis);
//            vhtChart.setTitle(label_mng);
//
//            max_sz = 0;
//            for (Commodity c : listVT) {
//                max_sz = Math.max(max_sz, mySimData.get_vht(LaneGroupType.mng, c.getId()).values.size());
//            }
//            for (int i = 0; i < max_sz; i++)
//                total[i] = 0;
//            for (Commodity c : listVT) {
//                dataSeries_mng = new XYChart.Series();
//                dataSeries_mng.setName(c.get_name());
//                xydata_mng = mySimData.get_vht(LaneGroupType.mng, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//                sz_mng = xydata_mng.size();
//
//                for (int i = 0; i < max_sz; i++) {
//                    if (i < sz_mng) {
//                        xy = xydata_mng.get(i);
//                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                        total[i] += xy.getYValue();
//                    } else {
//                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                    }
//                }
//                vhtChart.getData().add(dataSeries_mng);
//            }
//
//            dataSeries_total = new XYChart.Series();
//            dataSeries_total.setName("Total");
//            for (int i = 0; i < max_sz; i++)
//                dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//            if (listVT.size() > 1)
//                vhtChart.getData().add(dataSeries_total);
//
//            vhtChart.setCreateSymbols(false);
//            vhtChart.setLegendSide(Side.RIGHT);
//            vhtChart.setMinHeight(200);
//            vbAggregates.getChildren().add(vhtChart);
//            JFXChartUtil.setupZooming(vhtChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//            });
//            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vhtChart);
//        }
//
//        if (myLink.get_aux_lanes() > 0) {
//            xAxis = new NumberAxis();
//            xAxis.setLabel(timeLabel);
//            yAxis = new NumberAxis();
//            yAxis.setLabel("VHT");
//
//            vhtChart = new LineChart(xAxis, yAxis);
//            vhtChart.setTitle(label_aux);
//
//            max_sz = 0;
//            for (Commodity c : listVT) {
//                max_sz = Math.max(max_sz, mySimData.get_vht(LaneGroupType.aux, c.getId()).values.size());
//            }
//            for (int i = 0; i < max_sz; i++)
//                total[i] = 0;
//            for (Commodity c : listVT) {
//                dataSeries_aux = new XYChart.Series();
//                dataSeries_aux.setName(c.get_name());
//                xydata_aux = mySimData.get_vht(LaneGroupType.aux, c.getId()).get_XYSeries(c.get_name()).getItems();
//
//                sz_aux = xydata_aux.size();
//
//                for (int i = 0; i < max_sz; i++) {
//                    if (i < sz_aux) {
//                        xy = xydata_aux.get(i);
//                        dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                        total[i] += xy.getYValue();
//                    } else {
//                        dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//                    }
//                }
//                vhtChart.getData().add(dataSeries_aux);
//            }
//
//            dataSeries_total = new XYChart.Series();
//            dataSeries_total.setName("Total");
//            for (int i = 0; i < max_sz; i++)
//                dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//            if (listVT.size() > 1)
//                vhtChart.getData().add(dataSeries_total);
//
//            vhtChart.setCreateSymbols(false);
//            vhtChart.setLegendSide(Side.RIGHT);
//            vhtChart.setMinHeight(200);
//            vbAggregates.getChildren().add(vhtChart);
//            JFXChartUtil.setupZooming(vhtChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//            });
//            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vhtChart);
//        }
//
//
//        label_gp = "GP Lanes";
//        label_mng = "Managed Lanes";
//        label_aux = "Aux Lanes";
//        String label_units = UserSettings.unitsSpeed;
//        double cc = UserSettings.speedConversionMap.get("mph"+label_units);
//        double v_thres = UserSettings.defaultFreeFlowSpeedThresholdForDelayMph;
//        if (v_thres < 0)
//            v_thres = UserSettings.speedConversionMap.get("kphmph") * myLink.get_gp_freespeed_kph();
//        String label_thres = String.format("(Speed Threshold: %.0f %s)", cc*v_thres, label_units);
//
//        xAxis = new NumberAxis();
//        xAxis.setLabel(timeLabel);
//        yAxis = new NumberAxis();
//        yAxis.setLabel("Delay (veh.-hr.)");
//
//        LineChart delayChart = new LineChart(xAxis, yAxis);
//        delayChart.setTitle("Delay " + label_thres);
//
//        dataSeries_gp = new XYChart.Series();
//        dataSeries_gp.setName(label_gp);
//        xydata_gp = mySimData.get_delay(LaneGroupType.gp, (float)v_thres).get_XYSeries(label_gp).getItems();
//
//        dataSeries_mng = new XYChart.Series();
//        dataSeries_mng.setName(label_mng);
//        xydata_mng = null;
//        sz_mng = 0;
//        if (myLink.get_mng_lanes() > 0) {
//            xydata_mng = mySimData.get_delay(LaneGroupType.mng, (float)v_thres).get_XYSeries(label_mng).getItems();
//            sz_mng = xydata_mng.size();
//        }
//
//        dataSeries_aux = new XYChart.Series();
//        dataSeries_aux.setName(label_aux);
//        xydata_aux = null;
//        sz_aux = 0;
//        if (myLink.get_aux_lanes() > 0) {
//            xydata_aux = mySimData.get_delay(LaneGroupType.aux, (float)v_thres).get_XYSeries(label_aux).getItems();
//            sz_aux = xydata_aux.size();
//        }
//
//        sz_gp = xydata_gp.size();
//        max_sz = Math.max(Math.max(sz_gp, sz_mng), sz_aux);
//
//        for (int i = 0; i < max_sz; i++)
//            total[i] = 0;
//
//        //dt = mySimData.get_speed(LaneGroupType.gp).get_dt();
//        for (int i = 0; i < max_sz; i++) {
//            if (i < sz_gp) {
//                xy = xydata_gp.get(i);
//                dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                total[i] += xy.getYValue();
//            } else {
//                dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//            }
//            if (i < sz_mng) {
//                xy = xydata_mng.get(i);
//                dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                total[i] += xy.getYValue();
//            } else {
//                dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//            }
//            if (i < sz_aux) {
//                xy = xydata_aux.get(i);
//                dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
//                total[i] += xy.getYValue();
//            } else {
//                dataSeries_aux.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
//            }
//        }
//
//        dataSeries_total = new XYChart.Series();
//        dataSeries_total.setName("Total");
//        for (int i = 0; i < max_sz; i++)
//            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//
//        delayChart.getData().add(dataSeries_gp);
//        if (myLink.get_mng_lanes() > 0)
//            delayChart.getData().add(dataSeries_mng);
//        if (myLink.get_aux_lanes() > 0)
//            delayChart.getData().add(dataSeries_aux);
//        if ((myLink.get_mng_lanes() > 0) || (myLink.get_aux_lanes() > 0))
//            delayChart.getData().add(dataSeries_total);
//
//        delayChart.setCreateSymbols(false);
//        delayChart.setLegendSide(Side.RIGHT);
//        delayChart.setMinHeight(200);
//
//        vbAggregates.getChildren().add(delayChart);
//
//        //Zooming works only via primary mouse button without ctrl held down
//        JFXChartUtil.setupZooming(delayChart, (MouseEvent mouseEvent) -> {
//            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
//                    mouseEvent.isShortcutDown() )
//                mouseEvent.consume();
//        });
//        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(delayChart);
//
//
    }
        
        










}




