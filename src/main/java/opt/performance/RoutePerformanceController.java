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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import opt.AppMainController;
import opt.data.AbstractLink;
import opt.data.SimDataLink;
import opt.data.SimDataScenario;
import opt.data.TimeSeries;
import opt.data.LaneGroupType;
import opt.utils.Misc;
import opt.UserSettings;
import opt.data.Commodity;
import opt.data.Route;
import opt.data.SimCellData;
import opt.data.SimDataLanegroup;
import opt.utils.UtilGUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.block.BlockBorder;
//import org.jfree.chart.fx.ChartViewer;
import opt.utils.jfreechart.ChartViewer;
import opt.utils.jfxutils.chart.JFXChartUtil;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import java.util.concurrent.TimeUnit;

/**
 * This class serves to display plots of simulation data for a given route.
 * 
 * @author Alex Kurzhanskiy
 */
public class RoutePerformanceController {
    private Stage primaryStage = null;
    private AppMainController appMainController = null;
    
    //private AbstractLink myLink = null;
    private Route myRoute = null;
    private SimDataScenario mySimData = null;
    private float start = 0;
    
    private double[][] speedDataGP = null;
    private double[][] speedDataManaged = null;
    private double[][] speedDataAux = null;
    private double[][] flowDataGP = null;
    private double[][] flowDataManaged = null;
    private double[][] flowDataAux = null;
    private double[][] vehDataGP = null;
    private double[][] vehDataManaged = null;
    private double[][] vehDataAux = null;
    
    private double maxCellLength = 0;
    private double maxLinkLength = 0;
    private double routeLength = 0;
    
    private double minSpeed = Double.MAX_VALUE;
    private double maxSpeed = 0;
    private double minFlow = Double.MAX_VALUE;
    private double maxFlow = 0;
    private double minVeh = Double.MAX_VALUE;
    private double maxVeh = 0;
    
    private String timeLabel;
    private double timeDivider;
    private double myDt;
    
    private boolean hasManagedLanes = false;
    private boolean hasAuxLanes = false;
    
    private DefaultXYZDataset speedGPDS = null;
    private DefaultXYZDataset speedManagedDS = null;
    private DefaultXYZDataset speedAuxDS = null;
    private DefaultXYZDataset flowGPDS = null;
    private DefaultXYZDataset flowManagedDS = null;
    private DefaultXYZDataset flowAuxDS = null;
    private DefaultXYZDataset vehGPDS = null;
    private DefaultXYZDataset vehManagedDS = null;
    private DefaultXYZDataset vehAuxDS = null;
    
    private JFreeChart speedGPChart;
    private JFreeChart speedManagedChart;
    private JFreeChart speedAuxChart;
    private JFreeChart flowGPChart;
    private JFreeChart flowManagedChart;
    private JFreeChart flowAuxChart;
    private JFreeChart vehGPChart;
    private JFreeChart vehManagedChart;
    private JFreeChart vehAuxChart;
    
    private List<Commodity> listVT = null;
            
    
    @FXML // fx:id="routePerformanceMainPane"
    private TabPane routePerformanceMainPane; // Value injected by FXMLLoader

    @FXML // fx:id="tabContous"
    private Tab tabContous; // Value injected by FXMLLoader

    @FXML // fx:id="spContous"
    private ScrollPane spContous; // Value injected by FXMLLoader
    
    @FXML // fx:id="vbContours"
    private VBox vbContours; // Value injected by FXMLLoader

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
     * This function is called every time one opens a route in the
     * report module.
     * @param rt 
     * @param sdata - simulation data for the whole scenario.
     */
    public void initWithRouteData(Route rt, SimDataScenario sdata) {   
        if ((rt == null) || (sdata == null))
            return;
        
        myRoute = rt;
        mySimData = sdata;
        
        listVT = Misc.makeListVT(mySimData.fwyscenario.get_commodities());
        start = mySimData.fwyscenario.get_start_time();
        
        timeLabel = "Time (hours)";
        timeDivider = 3600.0;
        if (mySimData.fwyscenario.get_sim_duration() <= 7200) {
            timeLabel = "Time (minutes)";
            timeDivider = 60.0;
        }
        
        long startTime = System.nanoTime();
        processLinkSequence();
        long endTime = System.nanoTime();
        double dur = (endTime - startTime) / 1000000000f;
        System.err.println("processLinkSequence(): " + dur);
        
        startTime = System.nanoTime();
        fillTabContours();
        endTime = System.nanoTime();
        dur = (endTime - startTime) / 1000000000f;
        System.err.println("fillTabContours(): " + dur);
        
        startTime = System.nanoTime();
        fillTabAggregates();
        endTime = System.nanoTime();
        dur = (endTime - startTime) / 1000000000f;
        System.err.println("fillTabAggregates(): " + dur);

             
    }

    
    private void processLinkSequence() {
//
//        maxCellLength = 0;
//        maxLinkLength = 0;
//        routeLength = 0;
//        minSpeed = Double.MAX_VALUE;
//        maxSpeed = 0;
//        minFlow = Double.MAX_VALUE;
//        maxFlow = 0;
//        minVeh = Double.MAX_VALUE;
//        maxVeh = 0;
//        hasManagedLanes = false;
//        hasAuxLanes = false;
//        List<AbstractLink> links = myRoute.get_link_sequence();
//        int lSize = links.size();
//        int xSize = 0;
//        int ySize = 0;
//        double lcc = UserSettings.lengthConversionMap.get("meters"+UserSettings.unitsLength);
//        double scc = UserSettings.speedConversionMap.get("mph"+UserSettings.unitsSpeed);
//        double fcc = UserSettings.flowConversionMap.get("vph"+UserSettings.unitsFlow);
//        SimDataLink[] sdl = new SimDataLink[lSize];
//        double[] cellLengths = new double[lSize];
//        double[] ffspeed_gp_mph = new double[lSize];
//        double[] ffspeed_mng_mph = new double[lSize];
//        int[] begCellIdx = new int[lSize];
//
//        int prev_num_cells = 0;
//        for (int i = 0; i < lSize; i++) {
//            AbstractLink l = links.get(i);
//            maxLinkLength = Math.max(maxLinkLength, lcc*l.get_length_meters());
//            routeLength += lcc*l.get_length_meters();
//            sdl[i] = mySimData.linkdata.get(l.id);
//            List<SimCellData> scd = sdl[i].lgData.get(sdl[i].lgtype2id.get(LaneGroupType.gp)).celldata;
//            int num_cells = scd.size();
//            double cell_length = lcc * l.get_length_meters() / (double)num_cells;
//            maxCellLength = Math.max(maxCellLength, cell_length);
//            cellLengths[i] = cell_length;
//            xSize += num_cells;
//            if (i == 0)
//                begCellIdx[i] = 0;
//            else
//                begCellIdx[i] = begCellIdx[i-1] + prev_num_cells;
//            prev_num_cells = num_cells;
//            ffspeed_gp_mph[i] = UserSettings.speedConversionMap.get("kphmph")*l.get_gp_freespeed_kph();
//            ffspeed_mng_mph[i] = Double.NaN;
//            if (l.get_mng_lanes() > 0)
//                ffspeed_mng_mph[i] = UserSettings.speedConversionMap.get("kphmph")*l.get_mng_freespeed_kph();
//            ySize = Math.max(ySize, sdl[i].get_speed(LaneGroupType.gp).time.length);
//
//            if (l.get_mng_lanes() > 0) {
//                hasManagedLanes = true;
//                ySize = Math.max(ySize, sdl[i].get_speed(LaneGroupType.mng).time.length);
//            }
//            if (l.get_aux_lanes() > 0) {
//                hasAuxLanes = true;
//                ySize = Math.max(ySize, sdl[i].get_speed(LaneGroupType.aux).time.length);
//            }
//        }
//
//        float dt = sdl[0].get_speed(LaneGroupType.gp).get_dt();
//        myDt = dt / (float)timeDivider;
//        speedDataGP = new double[3][xSize*ySize];
//        speedDataManaged = new double[3][xSize*ySize];
//        speedDataAux = new double[3][xSize*ySize];
//        flowDataGP = new double[3][xSize*ySize];
//        flowDataManaged = new double[3][xSize*ySize];
//        flowDataAux = new double[3][xSize*ySize];
//        vehDataGP = new double[3][xSize*ySize];
//        vehDataManaged = new double[3][xSize*ySize];
//        vehDataAux = new double[3][xSize*ySize];
//
//        for (int j = 0; j < ySize; j++) {
//            double dd = 0.0;
//            float hour = (start+j*dt) / (float)timeDivider;
//            for (int i = 0; i < lSize; i++) {
//                List<SimCellData> scd_gp = sdl[i].lgData.get(sdl[i].lgtype2id.get(LaneGroupType.gp)).celldata;
//                List<SimCellData> scd_mng = null;
//                List<SimCellData> scd_aux = null;
//                if (links.get(i).get_mng_lanes() > 0)
//                    scd_mng = sdl[i].lgData.get(sdl[i].lgtype2id.get(LaneGroupType.mng)).celldata;
//                if (links.get(i).get_aux_lanes() > 0)
//                    scd_aux = sdl[i].lgData.get(sdl[i].lgtype2id.get(LaneGroupType.aux)).celldata;
//                int num_cells = scd_gp.size();
//                for (int k = 0; k < num_cells; k++) {
//                    double[] vv = scd_gp.get(k).get_speed((float)ffspeed_gp_mph[i], cellLengths[i]);
//                    double v = Double.NaN;
//                    if ((vv != null) && (j < vv.length))
//                        v = scc*vv[j];
//                    if (!Double.isNaN(v)) {
//                        minSpeed = Math.min(minSpeed, v);
//                        maxSpeed = Math.max(maxSpeed, v);
//                    }
//
//                    double[] ff = scd_gp.get(k).get_total_flw();
//                    double f = Double.NaN;
//                    if ((ff != null) && (j < ff.length))
//                        f = fcc*ff[j];
//                    if ((scd_aux != null) && (k < scd_aux.size())) {
//                        ff = scd_aux.get(k).get_total_flw();
//                        if ((ff != null) && (j < ff.length))
//                            if (!Double.isNaN(f)) {
//                                if (!Double.isNaN(ff[j]))
//                                    f += fcc*ff[j];
//                            } else
//                                f = fcc*ff[j];
//                    }
//                    if (!Double.isNaN(f)) {
//                        minFlow = Math.min(minFlow, f);
//                        maxFlow = Math.max(maxFlow, f);
//                    }
//
//                    speedDataGP[0][j * xSize + begCellIdx[i] + k] = dd;
//                    speedDataGP[1][j * xSize + begCellIdx[i] + k] = hour;
//                    speedDataGP[2][j * xSize + begCellIdx[i] + k] = v;
//                    flowDataGP[0][j * xSize + begCellIdx[i] + k] = dd;
//                    flowDataGP[1][j * xSize + begCellIdx[i] + k] = hour;
//                    flowDataGP[2][j * xSize + begCellIdx[i] + k] = f;
//                    vehDataGP[0][j * xSize + begCellIdx[i] + k] = dd;
//                    vehDataGP[1][j * xSize + begCellIdx[i] + k] = hour;
//
//                    if (hasManagedLanes) {
//                        v = Double.NaN;
//                        if (links.get(i).get_mng_lanes() > 0) {
//                            vv = null;
//                            if (scd_mng != null)
//                                vv = scd_mng.get(k).get_speed((float)ffspeed_mng_mph[i], cellLengths[i]);
//                            if ((vv != null) && (j < vv.length))
//                                v = scc*vv[j];
//                            if (!Double.isNaN(v)) {
//                                minSpeed = Math.min(minSpeed, v);
//                                maxSpeed = Math.max(maxSpeed, v);
//                            }
//                        }
//
//                        f = Double.NaN;
//                        if (links.get(i).get_mng_lanes() > 0) {
//                            ff = null;
//                            if (scd_mng != null)
//                                ff = scd_mng.get(k).get_total_flw();
//                            if ((ff != null) && (j < ff.length))
//                                f = fcc*ff[j];
//                            if (!Double.isNaN(f)) {
//                                minFlow = Math.min(minFlow, f);
//                                maxFlow = Math.max(maxFlow, f);
//                            }
//                        }
//
//                        speedDataManaged[0][j * xSize + begCellIdx[i] + k] = dd;
//                        speedDataManaged[1][j * xSize + begCellIdx[i] + k] = hour;
//                        speedDataManaged[2][j * xSize + begCellIdx[i] + k] = v;
//                        flowDataManaged[0][j * xSize + begCellIdx[i] + k] = dd;
//                        flowDataManaged[1][j * xSize + begCellIdx[i] + k] = hour;
//                        flowDataManaged[2][j * xSize + begCellIdx[i] + k] = f;
//                        vehDataManaged[0][j * xSize + begCellIdx[i] + k] = dd;
//                        vehDataManaged[1][j * xSize + begCellIdx[i] + k] = hour;
//                    }
//
//                    if (hasAuxLanes) {
//                        speedDataAux[0][j * xSize + i] = dd;
//                        speedDataAux[1][j * xSize + i] = hour;
//                        flowDataAux[0][j * xSize + i] = dd;
//                        flowDataAux[1][j * xSize + i] = hour;
//                        vehDataAux[0][j * xSize + i] = dd;
//                        vehDataAux[1][j * xSize + i] = hour;
//                    }
//
//                    dd += cellLengths[i];
//                }
//            }
//	    }
//
//        // HACK
//        minSpeed = 0d;
        
    }
    
    
    private void fillTabContours() {
        vbContours.getChildren().clear();
        speedGPDS = new DefaultXYZDataset();
        speedGPDS.addSeries("Speed in GP Lanes", speedDataGP);
        speedManagedDS = new DefaultXYZDataset();
        speedManagedDS.addSeries("Speed in Managed Lanes", speedDataManaged);
        speedAuxDS = new DefaultXYZDataset();
        flowGPDS = new DefaultXYZDataset();
        flowGPDS.addSeries("Flow in GP Lanes", flowDataGP);
        flowManagedDS = new DefaultXYZDataset();
        flowManagedDS.addSeries("Flow in Managed Lanes", flowDataManaged);
        flowAuxDS = new DefaultXYZDataset();
        vehGPDS = new DefaultXYZDataset();
        vehManagedDS = new DefaultXYZDataset();
        vehAuxDS = new DefaultXYZDataset();
        
        XYPlot plot;
        XYBlockRenderer renderer;
        LookupPaintScale paintScale;
        PaintScaleLegend psl;
        org.jfree.chart.axis.NumberAxis distAxis;
        org.jfree.chart.axis.NumberAxis timeAxis;
        org.jfree.chart.axis.NumberAxis scaleAxis;
        ChartViewer viewer;
        
        distAxis = new org.jfree.chart.axis.NumberAxis("Distance (" + UserSettings.unitsLength + ")");
	distAxis.setRange(0.0, routeLength);
        distAxis.setLowerMargin(0.0);
        distAxis.setUpperMargin(0.0);
        timeAxis = new org.jfree.chart.axis.NumberAxis(timeLabel);
        timeAxis.setUpperMargin(0.0);
        timeAxis.setRange(start/timeDivider, (start + mySimData.fwyscenario.get_sim_duration())/timeDivider);
        renderer = new XYBlockRenderer();
        renderer.setBlockWidth(maxCellLength);
        renderer.setBlockHeight(myDt);
        renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);
        paintScale = speedPaintScale();
        renderer.setPaintScale(paintScale);
        plot = new XYPlot(speedGPDS, distAxis, timeAxis, renderer);
        plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
        speedGPChart = new JFreeChart("Speed in GP Lanes", plot);
        speedGPChart.removeLegend();
        scaleAxis = new org.jfree.chart.axis.NumberAxis("Speed (" + UserSettings.unitsSpeed + ")");
        scaleAxis.setRange(minSpeed, maxSpeed);
        psl = new PaintScaleLegend(paintScale, scaleAxis);
        psl.setMargin(new RectangleInsets(3, 10, 3, 10));
        psl.setPosition(RectangleEdge.BOTTOM);
        psl.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        psl.setAxisOffset(5.0);
        psl.setPosition(RectangleEdge.RIGHT);
        psl.setFrame(new BlockBorder(Color.GRAY));
        speedGPChart.addSubtitle(psl);
        viewer = new ChartViewer(speedGPChart);
        //viewer.setEventDispatcher(null);
        viewer.setMinWidth(300);
        viewer.setMinHeight(200);
        double prefWidth = routePerformanceMainPane.getPrefWidth();
        double prefHeight = routePerformanceMainPane.getPrefHeight()/3;
        viewer.setPrefSize(prefWidth, prefHeight);
        vbContours.getChildren().add(viewer);
        
        if (hasManagedLanes) {
            distAxis = new org.jfree.chart.axis.NumberAxis("Distance (" + UserSettings.unitsLength + ")");
            distAxis.setRange(0.0, routeLength);
            distAxis.setLowerMargin(0.0);
            distAxis.setUpperMargin(0.0);
            timeAxis = new org.jfree.chart.axis.NumberAxis(timeLabel);
            timeAxis.setUpperMargin(0.0);
            timeAxis.setRange(start/timeDivider, (start + mySimData.fwyscenario.get_sim_duration())/timeDivider);
            renderer = new XYBlockRenderer();
            renderer.setBlockWidth(maxCellLength);
            renderer.setBlockHeight(myDt);
            renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);
            paintScale = speedPaintScale();
            renderer.setPaintScale(paintScale);
            plot = new XYPlot(speedManagedDS, distAxis, timeAxis, renderer);
            plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
            speedManagedChart = new JFreeChart("Speed in Managed Lanes", plot);
            speedManagedChart.removeLegend();
            scaleAxis = new org.jfree.chart.axis.NumberAxis("Speed (" + UserSettings.unitsSpeed + ")");
            scaleAxis.setRange(minSpeed, maxSpeed);
            psl = new PaintScaleLegend(paintScale, scaleAxis);
            psl.setMargin(new RectangleInsets(3, 10, 3, 10));
            psl.setPosition(RectangleEdge.BOTTOM);
            psl.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            psl.setAxisOffset(5.0);
            psl.setPosition(RectangleEdge.RIGHT);
            psl.setFrame(new BlockBorder(Color.GRAY));
            speedManagedChart.addSubtitle(psl);
            viewer = new ChartViewer(speedManagedChart);
            //viewer.setEventDispatcher(null);
            vbContours.getWidth();
            viewer.setMinWidth(300);
            viewer.setMinHeight(200);
            prefWidth = routePerformanceMainPane.getPrefWidth();
            prefHeight = routePerformanceMainPane.getPrefHeight()/3;
            viewer.setPrefSize(prefWidth, prefHeight);
            vbContours.getChildren().add(viewer);
        }
        
        distAxis = new org.jfree.chart.axis.NumberAxis("Distance (" + UserSettings.unitsLength + ")");
	distAxis.setRange(0.0, routeLength);
        distAxis.setLowerMargin(0.0);
        distAxis.setUpperMargin(0.0);
        timeAxis = new org.jfree.chart.axis.NumberAxis(timeLabel);
        timeAxis.setUpperMargin(0.0);
        timeAxis.setRange(start/timeDivider, (start + mySimData.fwyscenario.get_sim_duration())/timeDivider);
        renderer = new XYBlockRenderer();
        renderer.setBlockWidth(maxCellLength);
        renderer.setBlockHeight(myDt);
        renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);
        paintScale = flowPaintScale();
        renderer.setPaintScale(paintScale);
        plot = new XYPlot(flowGPDS, distAxis, timeAxis, renderer);
        plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
        String chartTitle = "Flow in GP Lanes";
        if (hasAuxLanes)
            chartTitle = "Flow in GP and Aux Lanes";
        flowGPChart = new JFreeChart(chartTitle, plot);
        flowGPChart.removeLegend();
        scaleAxis = new org.jfree.chart.axis.NumberAxis("Flow (" + UserSettings.unitsFlow + ")");
        scaleAxis.setRange(minFlow, maxFlow);
        psl = new PaintScaleLegend(paintScale, scaleAxis);
        psl.setMargin(new RectangleInsets(3, 10, 3, 10));
        psl.setPosition(RectangleEdge.BOTTOM);
        psl.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        psl.setAxisOffset(5.0);
        psl.setPosition(RectangleEdge.RIGHT);
        psl.setFrame(new BlockBorder(Color.GRAY));
        flowGPChart.addSubtitle(psl);
        viewer = new ChartViewer(flowGPChart);
        //viewer.setEventDispatcher(null);
        viewer.setMinWidth(300);
        viewer.setMinHeight(200);
        prefWidth = routePerformanceMainPane.getPrefWidth();
        prefHeight = routePerformanceMainPane.getPrefHeight()/3;
        viewer.setPrefSize(prefWidth, prefHeight);
        vbContours.getChildren().add(viewer);
        
        if (hasManagedLanes) {
            distAxis = new org.jfree.chart.axis.NumberAxis("Distance (" + UserSettings.unitsLength + ")");
            distAxis.setRange(0.0, routeLength);
            distAxis.setLowerMargin(0.0);
            distAxis.setUpperMargin(0.0);
            timeAxis = new org.jfree.chart.axis.NumberAxis(timeLabel);
            timeAxis.setUpperMargin(0.0);
            timeAxis.setRange(start/timeDivider, (start + mySimData.fwyscenario.get_sim_duration())/timeDivider);
            renderer = new XYBlockRenderer();
            renderer.setBlockWidth(maxCellLength);
            renderer.setBlockHeight(myDt);
            renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);
            paintScale = flowPaintScale();
            renderer.setPaintScale(paintScale);
            plot = new XYPlot(flowManagedDS, distAxis, timeAxis, renderer);
            plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
            flowManagedChart = new JFreeChart("Flow in Managed Lanes", plot);
            flowManagedChart.removeLegend();
            scaleAxis = new org.jfree.chart.axis.NumberAxis("Flow (" + UserSettings.unitsFlow + ")");
            scaleAxis.setRange(minFlow, maxFlow);
            psl = new PaintScaleLegend(paintScale, scaleAxis);
            psl.setMargin(new RectangleInsets(3, 10, 3, 10));
            psl.setPosition(RectangleEdge.BOTTOM);
            psl.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            psl.setAxisOffset(5.0);
            psl.setPosition(RectangleEdge.RIGHT);
            psl.setFrame(new BlockBorder(Color.GRAY));
            flowManagedChart.addSubtitle(psl);
            viewer = new ChartViewer(flowManagedChart);
            //viewer.setEventDispatcher(null);
            vbContours.getWidth();
            viewer.setMinWidth(300);
            viewer.setMinHeight(200);
            prefWidth = routePerformanceMainPane.getPrefWidth();
            prefHeight = routePerformanceMainPane.getPrefHeight()/3;
            viewer.setPrefSize(prefWidth, prefHeight);
            vbContours.getChildren().add(viewer);
        }
        
    }
    
    
    
    
    /**
     * Generates paint scale for the speed contour plot.
     */
    private LookupPaintScale speedPaintScale() {
        if (minSpeed >= maxSpeed)
            if (minSpeed < 1.0)
                maxSpeed = minSpeed + 1.0;
            else
                minSpeed = maxSpeed - 1.0;
            LookupPaintScale pScale = new LookupPaintScale(minSpeed, maxSpeed, Color.white);
        Color[] clr = UtilGUI.krygColorScale();

        double delta = (maxSpeed - minSpeed)/(clr.length - 1);
        double value = minSpeed;
        pScale.add(value, clr[0]);
        value += Double.MIN_VALUE;
        for (int i = 1; i < clr.length; i++) {
            pScale.add(value, clr[i]);
            value += delta;
        }
        return pScale;
    }
    
    
    
    /**
     * Generates paint scale for the flow contour plot.
     */
    private LookupPaintScale flowPaintScale() {
        if (minFlow >= maxFlow)
            if (minFlow < 1.0)
                maxFlow = minFlow + 1.0;
            else
                minFlow = maxFlow - 1.0;
        LookupPaintScale pScale = new LookupPaintScale(minFlow, maxFlow, Color.white);
        Color[] clr = UtilGUI.byrColorScale();
        double delta = (maxFlow - minFlow)/(clr.length - 1);
        double value = minFlow;
        pScale.add(value, clr[0]);
        value += Double.MIN_VALUE;
        for (int i = 1; i < clr.length; i++) {
            pScale.add(value, clr[i]);
            value += delta;
        }
        return pScale;
    }
    
    
    
    
    
    
    
    public void fillTabAggregates() {
//        vbAggregates.getChildren().clear();
//        String label_gp, label_mng, label_aux;
//        XYChart.Series dataSeries_gp, dataSeries_mng, dataSeries_total;
//        List<XYDataItem> xydata_gp, xydata_mng, xydata_aux;
//        int sz_gp, sz_mng, sz_aux;
//        XYDataItem xy;
//        double dt = mySimData.get_dt_sec();
//        int num_comms = listVT.size();
//        TimeSeries[] vmt_series_gp = new TimeSeries[num_comms];
//        TimeSeries[] vmt_series_mng = new TimeSeries[num_comms];
//        TimeSeries[] vht_series_gp = new TimeSeries[num_comms];
//        TimeSeries[] vht_series_mng = new TimeSeries[num_comms];
//        TimeSeries delay_gp = null;
//        TimeSeries delay_mng = null;
//
//        for (int i = 0; i < num_comms; i++) {
//            vmt_series_gp[i] = null;
//            vmt_series_mng[i] = null;
//            vht_series_gp[i] = null;
//            vht_series_mng[i] = null;
//        }
//
//        List<AbstractLink> links = myRoute.get_link_sequence();
//
//        for (AbstractLink l : links) {
//            double v_thres = UserSettings.defaultFreeFlowSpeedThresholdForDelayMph;
//            if (v_thres < 0)
//                v_thres = UserSettings.speedConversionMap.get("kphmph") * l.get_gp_freespeed_kph();
//            SimDataLink sdl = mySimData.linkdata.get(l.id);
//            if (sdl != null) {
//                for (int i = 0; i < num_comms; i++) {
//                    TimeSeries ts = sdl.get_vmt(LaneGroupType.gp, listVT.get(i).getId());
//                    if (ts != null) {
//                        if (vmt_series_gp[i] == null)
//                            vmt_series_gp[i] = ts;
//                        else
//                            try {
//                                vmt_series_gp[i].add(ts);
//                            } catch(Exception e) {
//                                System.err.println("Error adding GP VMT timeseries: " + e.getMessage());
//                            }
//                    }
//
//                    ts = sdl.get_vht(LaneGroupType.gp, listVT.get(i).getId());
//                    if (ts != null) {
//                        if (vht_series_gp[i] == null)
//                            vht_series_gp[i] = ts;
//                        else
//                            try {
//                                vht_series_gp[i].add(ts);
//                            } catch(Exception e) {
//                                System.err.println("Error adding GP VHT timeseries: " + e.getMessage());
//                            }
//                    }
//
//                    if (l.get_mng_lanes() > 0) {
//                        ts = sdl.get_vmt(LaneGroupType.mng, listVT.get(i).getId());
//                        if (ts != null) {
//                            if (vmt_series_mng[i] == null)
//                                vmt_series_mng[i] = ts;
//                            else
//                                try {
//                                    vmt_series_mng[i].add(ts);
//                                } catch(Exception e) {
//                                    System.err.println("Error adding Managed Lane VMT timeseries: " + e.getMessage());
//                                }
//                        }
//
//                        ts = sdl.get_vht(LaneGroupType.mng, listVT.get(i).getId());
//                        if (ts != null) {
//                            if (vht_series_mng[i] == null)
//                                vht_series_mng[i] = ts;
//                            else
//                                try {
//                                    vht_series_mng[i].add(ts);
//                                } catch(Exception e) {
//                                    System.err.println("Error adding Managed Lane VHT timeseries: " + e.getMessage());
//                                }
//                        }
//                    }
//
//                    if (l.get_aux_lanes() > 0) {
//                        ts = sdl.get_vmt(LaneGroupType.aux, listVT.get(i).getId());
//                        if (ts != null) {
//                            if (vmt_series_gp[i] == null)
//                                vmt_series_gp[i] = ts;
//                            else
//                                try {
//                                    vmt_series_gp[i].add(ts);
//                                } catch(Exception e) {
//                                    System.err.println("Error adding Aux Lane VMT timeseries: " + e.getMessage());
//                                }
//                        }
//
//                        ts = sdl.get_vht(LaneGroupType.aux, listVT.get(i).getId());
//                        if (ts != null) {
//                            if (vht_series_gp[i] == null)
//                                vht_series_gp[i] = ts;
//                            else
//                                try {
//                                    vht_series_gp[i].add(ts);
//                                } catch(Exception e) {
//                                    System.err.println("Error adding Aux Lane VHT timeseries: " + e.getMessage());
//                                }
//                        }
//                    }
//                }
//
//                TimeSeries ts = sdl.get_delay(LaneGroupType.gp, (float)v_thres);
//                if (ts != null) {
//                    if (delay_gp == null)
//                        delay_gp = ts;
//                    else
//                        try {
//                            delay_gp.add(ts);
//                        } catch(Exception e) {
//                            System.err.println("Error adding GP delay timeseries: " + e.getMessage());
//                        }
//                }
//
//                if (l.get_mng_lanes() > 0) {
//                    ts = sdl.get_delay(LaneGroupType.mng, (float)v_thres);
//                    if (ts != null) {
//                        if (delay_mng == null)
//                            delay_mng = ts;
//                        else
//                            try {
//                                delay_mng.add(ts);
//                            } catch(Exception e) {
//                                System.err.println("Error adding Managed Lane delay timeseries: " + e.getMessage());
//                            }
//                    }
//                }
//
//                if (l.get_aux_lanes() > 0) {
//                    ts = sdl.get_delay(LaneGroupType.aux, (float)v_thres);
//                    if (ts != null) {
//                        if (delay_gp == null)
//                            delay_gp = ts;
//                        else
//                            try {
//                                delay_gp.add(ts);
//                            } catch(Exception e) {
//                                System.err.println("Error adding Aux Lane delay timeseries: " + e.getMessage());
//                            }
//                    }
//                }
//            }
//        }
//
//        label_gp = "VMT in GP Lanes";
//        if (hasAuxLanes)
//            label_gp = "VMT in GP and Aux Lanes";
//        label_mng = "VMT in Managed Lanes";
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
//        for (int k = 0; k < num_comms; k++) {
//            max_sz = Math.max(max_sz, vmt_series_gp[k].values.size());
//        }
//        double[] total = new double[max_sz];
//        for (int i = 0; i < max_sz; i++)
//            total[i] = 0;
//        for (int k = 0; k < num_comms; k++) {
//            dataSeries_gp = new XYChart.Series();
//            dataSeries_gp.setName(listVT.get(k).get_name());
//            xydata_gp = vmt_series_gp[k].get_XYSeries(listVT.get(k).get_name()).getItems();
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
//        if (hasManagedLanes) {
//            xAxis = new NumberAxis();
//            xAxis.setLabel(timeLabel);
//            yAxis = new NumberAxis();
//            yAxis.setLabel("VMT");
//
//            vmtChart = new LineChart(xAxis, yAxis);
//            vmtChart.setTitle(label_mng);
//
//            max_sz = 0;
//            for (int k = 0; k < num_comms; k++) {
//                if (vmt_series_gp[k] != null)
//                    max_sz = Math.max(max_sz, vmt_series_gp[k].values.size());
//            }
//            for (int i = 0; i < max_sz; i++)
//                total[i] = 0;
//            for (int k = 0; k < num_comms; k++) {
//                dataSeries_mng = new XYChart.Series();
//                dataSeries_mng.setName(listVT.get(k).get_name());
//                xydata_mng = vmt_series_mng[k].get_XYSeries(listVT.get(k).get_name()).getItems();
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
//
//        label_gp = "VHT in GP Lanes";
//        if (hasAuxLanes)
//            label_gp = "VHT in GP and Aux Lanes";
//        label_mng = "VHT in Managed Lanes";
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
//        for (int k = 0; k < num_comms; k++) {
//            max_sz = Math.max(max_sz, vht_series_gp[k].values.size());
//        }
//        total = new double[max_sz];
//        for (int i = 0; i < max_sz; i++)
//            total[i] = 0;
//        for (int k = 0; k < num_comms; k++) {
//            dataSeries_gp = new XYChart.Series();
//            dataSeries_gp.setName(listVT.get(k).get_name());
//            xydata_gp = vht_series_gp[k].get_XYSeries(listVT.get(k).get_name()).getItems();
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
//        if (hasManagedLanes) {
//            xAxis = new NumberAxis();
//            xAxis.setLabel(timeLabel);
//            yAxis = new NumberAxis();
//            yAxis.setLabel("VHT");
//
//            vhtChart = new LineChart(xAxis, yAxis);
//            vhtChart.setTitle(label_mng);
//
//            max_sz = 0;
//            for (int k = 0; k < num_comms; k++) {
//                if (vht_series_gp[k] != null)
//                    max_sz = Math.max(max_sz, vht_series_gp[k].values.size());
//            }
//            for (int i = 0; i < max_sz; i++)
//                total[i] = 0;
//            for (int k = 0; k < num_comms; k++) {
//                dataSeries_mng = new XYChart.Series();
//                dataSeries_mng.setName(listVT.get(k).get_name());
//                xydata_mng = vht_series_mng[k].get_XYSeries(listVT.get(k).get_name()).getItems();
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
//
//        label_gp = "GP Lanes";
//        if (hasAuxLanes)
//            label_gp = "GP and Aux Lanes";
//        label_mng = "Managed Lanes";
//
//        xAxis = new NumberAxis();
//        xAxis.setLabel(timeLabel);
//        yAxis = new NumberAxis();
//        yAxis.setLabel("Delay (veh.-hr.)");
//
//        LineChart delayChart = new LineChart(xAxis, yAxis);
//        String label_units = UserSettings.unitsSpeed;
//        double cc = UserSettings.speedConversionMap.get("mph"+label_units);
//        double v_thres = UserSettings.defaultFreeFlowSpeedThresholdForDelayMph;
//        String label_thres = String.format("(Speed Threshold: %.0f %s)", cc*v_thres, label_units);
//        if (v_thres < 0)
//            label_thres = "(Speed Threshold: Free Flow Speed)";
//        delayChart.setTitle("Delay " + label_thres);
//
//        dataSeries_gp = new XYChart.Series();
//        dataSeries_gp.setName(label_gp);
//        xydata_gp = delay_gp.get_XYSeries(label_gp).getItems();
//
//        dataSeries_mng = new XYChart.Series();
//        dataSeries_mng.setName(label_mng);
//        xydata_mng = null;
//        sz_mng = 0;
//        if (hasManagedLanes) {
//            xydata_mng = delay_mng.get_XYSeries(label_mng).getItems();
//            sz_mng = xydata_mng.size();
//        }
//
//        sz_gp = xydata_gp.size();
//        max_sz = Math.max(sz_gp, sz_mng);
//
//        for (int i = 0; i < max_sz; i++)
//            total[i] = 0;
//
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
//        }
//
//        dataSeries_total = new XYChart.Series();
//        dataSeries_total.setName("Total");
//        for (int i = 0; i < max_sz; i++)
//            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
//
//        delayChart.getData().add(dataSeries_gp);
//        if (hasManagedLanes) {
//            delayChart.getData().add(dataSeries_mng);
//            delayChart.getData().add(dataSeries_total);
//        }
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
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
