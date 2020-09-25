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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javafx.scene.control.Separator;
import opt.data.TimeMatrix;

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
    private double[][] flowDataGP = null;
    private double[][] flowDataManaged = null;
    private double[][] densityDataGP = null;
    private double[][] densityDataManaged = null;
    
    private double minCellLength = 0;
    private double maxCellLength = 0;
    private double minCellLengthM = 0;
    private double maxCellLengthM = 0;
    private double maxLinkLength = 0;
    private double routeLength = 0;
    
    private double minSpeed = Double.MAX_VALUE;
    private double maxSpeed = 0;
    private double minFlow = Double.MAX_VALUE;
    private double maxFlow = 0;
    private double minDensity = Double.MAX_VALUE;
    private double maxDensity = 0;
    
    private String timeLabel;
    private double timeDivider;
    private double myDt;
    
    private boolean hasManagedLanes = false;
    private boolean hasAuxLanes = false;
    
    private Set<LaneGroupType> lgset_gp = new HashSet<LaneGroupType>();
    private Set<LaneGroupType> lgset_mng = new HashSet<LaneGroupType>();
    
    private DefaultXYZDataset speedGPDS = null;
    private DefaultXYZDataset speedManagedDS = null;
    private DefaultXYZDataset flowGPDS = null;
    private DefaultXYZDataset flowManagedDS = null;
    private DefaultXYZDataset densityGPDS = null;
    private DefaultXYZDataset densityManagedDS = null;
    
    private JFreeChart speedGPChart;
    private JFreeChart speedManagedChart;
    private JFreeChart flowGPChart;
    private JFreeChart flowManagedChart;
    private JFreeChart densityGPChart;
    private JFreeChart densityManagedChart;
    
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
        lgset_gp.add(LaneGroupType.gp);
        lgset_gp.add(LaneGroupType.aux);
        lgset_mng.add(LaneGroupType.mng);
    }

    private Set<Long> cset(Commodity c) {
        Set<Long> s = new HashSet<Long>();
        s.add(c.getId());
        return s;
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
        processRouteData();
        long endTime = System.nanoTime();
        double dur = (endTime - startTime) / 1000000000f;
        System.err.println("processRouteData(): " + dur); //FIXME: remove
        
        startTime = System.nanoTime();
        fillTabContours();
        endTime = System.nanoTime();
        dur = (endTime - startTime) / 1000000000f;
        System.err.println("fillTabContours(): " + dur); //FIXME: remove
        
        startTime = System.nanoTime();
        fillTabAggregates();
        endTime = System.nanoTime();
        dur = (endTime - startTime) / 1000000000f;
        System.err.println("fillTabAggregates(): " + dur); //FIXME: remove

             
    }

    
    
    private void processRouteData() {
        double dt = mySimData.get_dt_sec();
        myDt = dt /(float)timeDivider;
        minCellLength = Double.MAX_VALUE;
        maxCellLength = 0;
        minCellLengthM = Double.MAX_VALUE;
        maxCellLengthM = 0;
        maxLinkLength = 0;
        routeLength = 0;
        minSpeed = 0;
        maxSpeed = 0;
        minFlow = 0;
        maxFlow = 0;
        minDensity = 0;
        maxDensity = 0;
        hasManagedLanes = false;
        hasAuxLanes = false;
        List<AbstractLink> links = myRoute.get_link_sequence();
        
        double lcc = UserSettings.lengthConversionMap.get("meters"+UserSettings.unitsLength);
        double lcc2 = UserSettings.lengthConversionMap.get("miles"+UserSettings.unitsLength);
        double scc = UserSettings.speedConversionMap.get("mph"+UserSettings.unitsSpeed);
        double fcc = UserSettings.flowConversionMap.get("vph"+UserSettings.unitsFlow);
        double dcc = UserSettings.densityConversionMap.get("vpm"+UserSettings.unitsDensity);
        
        TimeMatrix tm_speed_gp = mySimData.get_speed_contour_for_route(myRoute.getId(), lgset_gp);
        TimeMatrix tm_speed_mng = mySimData.get_speed_contour_for_route(myRoute.getId(), lgset_mng);
        TimeMatrix tm_flow_gp = mySimData.get_flow_contour_for_route(myRoute.getId(), lgset_gp, null);
        TimeMatrix tm_flow_mng = mySimData.get_flow_contour_for_route(myRoute.getId(), lgset_mng, null);
        TimeMatrix tm_density_gp = mySimData.get_density_contour_for_route(myRoute.getId(), lgset_gp, null);
        TimeMatrix tm_density_mng = mySimData.get_density_contour_for_route(myRoute.getId(), lgset_mng, null);
        
        double[][] vmtrx_gp = tm_speed_gp.values;
        double[][] vmtrx_mng = tm_speed_mng.values;
        double[][] fmtrx_gp = tm_flow_gp.values;
        double[][] fmtrx_mng = tm_flow_mng.values;
        double[][] dmtrx_gp = tm_density_gp.values;
        double[][] dmtrx_mng = tm_density_mng.values;
        int ySize = vmtrx_gp.length;
        int xSize = vmtrx_gp[0].length;
        int ySizeM = 0;
        if (vmtrx_mng != null)
            ySizeM = vmtrx_mng.length;
        
        for (AbstractLink l : links) {
            maxLinkLength = Math.max(maxLinkLength, lcc*l.get_length_meters());

            if (l.get_mng_lanes() > 0)
                hasManagedLanes = true;

            if (l.get_aux_lanes() > 0)
                hasAuxLanes = true;
        }

        speedDataGP = new double[3][xSize*ySize];
        speedDataManaged = new double[3][xSize*ySize];
        flowDataGP = new double[3][xSize*ySize];
        flowDataManaged = new double[3][xSize*ySize];
        densityDataGP = new double[3][xSize*ySize];
        densityDataManaged = new double[3][xSize*ySize];
        
        double[] dist = new double[ySize];
        double[] distM = new double[ySizeM];
        List<Double> cell_lengths = tm_speed_gp.cell_lengths_miles;
        List<Double> cell_lengthsM = tm_speed_mng.cell_lengths_miles;
        
        for (int i = 0; i < xSize; i++) {
            double hour = (start + i*dt) / timeDivider;
            double dd = 0.0;
            double ddM = 0.0;
            for (int j = 0; j < ySize; j++) {
                if (i == 0) {
                    if (j > 0)
                        dd += lcc2 * cell_lengths.get(j-1);
                    dist[j] = dd;
                    minCellLength = Math.min(minCellLength, cell_lengths.get(j));
                    maxCellLength = Math.max(maxCellLength, cell_lengths.get(j));
                    routeLength += lcc2 * cell_lengths.get(j);
                } else {
                    dd = dist[j];
                }
                
                int idx = i * ySize + j;
                speedDataGP[0][idx] = dd;
                speedDataGP[1][idx] = hour;
                speedDataGP[2][idx] = scc*vmtrx_gp[j][i];
                flowDataGP[0][idx] = dd;
                flowDataGP[1][idx] = hour;
                flowDataGP[2][idx] = fcc*fmtrx_gp[j][i];
                densityDataGP[0][idx] = dd;
                densityDataGP[1][idx] = hour;
                densityDataGP[2][idx] = dcc*dmtrx_gp[j][i];
                
                if (!Double.isNaN(speedDataGP[2][idx])) {
                    minSpeed = Math.min(minSpeed, speedDataGP[2][idx]);
                    maxSpeed = Math.max(maxSpeed, speedDataGP[2][idx]);
                }
                if (!Double.isNaN(flowDataGP[2][idx])) {
                    minFlow = Math.min(minFlow, flowDataGP[2][idx]);
                    maxFlow = Math.max(maxFlow, flowDataGP[2][idx]);
                }
                if (!Double.isNaN(densityDataGP[2][idx])) {
                    minDensity = Math.min(minDensity, densityDataGP[2][idx]);
                    maxDensity = Math.max(maxDensity, densityDataGP[2][idx]);
                }
            }
            
            if (hasManagedLanes) {
                for (int j = 0; j < ySizeM; j++) {
                    if (i == 0) {
                        if (j > 0)
                            ddM += lcc2 * cell_lengthsM.get(j-1);
                        distM[j] = ddM;
                        minCellLengthM = Math.min(minCellLengthM, cell_lengths.get(j));
                        maxCellLengthM = Math.max(maxCellLengthM, cell_lengthsM.get(j));
                    } else {
                        ddM = distM[j];
                    }

                    int idx = i * ySizeM + j;
                    speedDataManaged[0][idx] = ddM;
                    speedDataManaged[1][idx] = hour;
                    speedDataManaged[2][idx] = scc*vmtrx_mng[j][i];
                    flowDataManaged[0][idx] = ddM;
                    flowDataManaged[1][idx] = hour;
                    flowDataManaged[2][idx] = fcc*fmtrx_mng[j][i];
                    densityDataManaged[0][idx] = ddM;
                    densityDataManaged[1][idx] = hour;
                    densityDataManaged[2][idx] = dcc*dmtrx_mng[j][i];

                    if (!Double.isNaN(speedDataManaged[2][idx])) {
                        minSpeed = Math.min(minSpeed, speedDataManaged[2][idx]);
                        maxSpeed = Math.max(maxSpeed, speedDataManaged[2][idx]);
                    }
                    if (!Double.isNaN(flowDataManaged[2][idx])) {
                        minFlow = Math.min(minFlow, flowDataGP[2][idx]);
                        maxFlow = Math.max(maxFlow, flowDataManaged[2][idx]);
                    }
                    if (!Double.isNaN(densityDataManaged[2][idx])) {
                        minDensity = Math.min(minDensity, densityDataManaged[2][idx]);
                        maxDensity = Math.max(maxDensity, densityDataManaged[2][idx]);
                    }
                }
            }
        }
        
        minCellLength = lcc2 * minCellLength;
        maxCellLength = lcc2 * maxCellLength;
        minCellLengthM = lcc2 * minCellLengthM;
        maxCellLengthM = lcc2 * maxCellLengthM; 
    }
    
    
    private void fillTabContours() {
        int minWidthGP = 300;
        int minHeightGP = 200;
        int minWidthManaged = 300;
        int minHeightManaged = 260;
        
        String lanes_buf = "in GP Lanes";
        if (hasAuxLanes)
            lanes_buf = "in GP and Aux Lanes";
        
        vbContours.getChildren().clear();
        speedGPDS = new DefaultXYZDataset();
        speedGPDS.addSeries("Speed " + lanes_buf, speedDataGP);
        speedManagedDS = new DefaultXYZDataset();
        speedManagedDS.addSeries("Speed in Managed Lanes", speedDataManaged);
        flowGPDS = new DefaultXYZDataset();
        flowGPDS.addSeries("Flow " + lanes_buf, flowDataGP);
        flowManagedDS = new DefaultXYZDataset();
        flowManagedDS.addSeries("Flow in Managed Lanes", flowDataManaged);
        densityGPDS = new DefaultXYZDataset();
        densityGPDS.addSeries("Density " + lanes_buf, densityDataGP);
        densityManagedDS = new DefaultXYZDataset();
        densityManagedDS.addSeries("Density in Managed Lanes", flowDataManaged);
        
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
        speedGPChart = new JFreeChart("Speed " + lanes_buf, plot);
        speedGPChart.removeLegend();
        scaleAxis = new org.jfree.chart.axis.NumberAxis("Speed (" + UserSettings.unitsSpeed + ")");
        scaleAxis.setRange(minSpeed, maxSpeed);
        psl = new PaintScaleLegend(paintScale, scaleAxis);
        psl.setMargin(new RectangleInsets(3, 10, 3, 10));
        psl.setPosition(RectangleEdge.BOTTOM);
        psl.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        psl.setAxisOffset(5.0);
        psl.setPosition(RectangleEdge.BOTTOM);
        psl.setFrame(new BlockBorder(Color.GRAY));
        if (!hasManagedLanes)
            speedGPChart.addSubtitle(psl);
        viewer = new ChartViewer(speedGPChart);
        viewer.setMinWidth(minWidthGP);
        viewer.setMinHeight(minHeightGP);
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
            renderer.setBlockWidth(maxCellLengthM);
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
            psl.setPosition(RectangleEdge.BOTTOM);
            psl.setFrame(new BlockBorder(Color.GRAY));
            speedManagedChart.addSubtitle(psl);
            viewer = new ChartViewer(speedManagedChart);
            vbContours.getWidth();
            viewer.setMinWidth(minWidthManaged);
            viewer.setMinHeight(minHeightManaged);
            prefWidth = routePerformanceMainPane.getPrefWidth();
            prefHeight = routePerformanceMainPane.getPrefHeight()/3;
            viewer.setPrefSize(prefWidth, prefHeight);
            vbContours.getChildren().add(viewer);
        }
        vbContours.getChildren().add(new Separator());


        
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
        psl.setPosition(RectangleEdge.BOTTOM);
        psl.setFrame(new BlockBorder(Color.GRAY));
        if (!hasManagedLanes)
            flowGPChart.addSubtitle(psl);
        viewer = new ChartViewer(flowGPChart);
        viewer.setMinWidth(minWidthGP);
        viewer.setMinHeight(minHeightGP);
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
            renderer.setBlockWidth(maxCellLengthM);
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
            psl.setPosition(RectangleEdge.BOTTOM);
            psl.setFrame(new BlockBorder(Color.GRAY));
            flowManagedChart.addSubtitle(psl);
            viewer = new ChartViewer(flowManagedChart);
            vbContours.getWidth();
            viewer.setMinWidth(minWidthManaged);
            viewer.setMinHeight(minHeightManaged);
            prefWidth = routePerformanceMainPane.getPrefWidth();
            prefHeight = routePerformanceMainPane.getPrefHeight()/3;
            viewer.setPrefSize(prefWidth, prefHeight);
            vbContours.getChildren().add(viewer);
        }
        vbContours.getChildren().add(new Separator());
        
        
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
        paintScale = densityPaintScale();
        renderer.setPaintScale(paintScale);
        plot = new XYPlot(densityGPDS, distAxis, timeAxis, renderer);
        plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
        chartTitle = "Density in GP Lanes";
        if (hasAuxLanes)
            chartTitle = "Density in GP and Aux Lanes";
        densityGPChart = new JFreeChart(chartTitle, plot);
        densityGPChart.removeLegend();
        scaleAxis = new org.jfree.chart.axis.NumberAxis("Flow (" + UserSettings.unitsFlow + ")");
        scaleAxis.setRange(minDensity, maxDensity);
        psl = new PaintScaleLegend(paintScale, scaleAxis);
        psl.setMargin(new RectangleInsets(3, 10, 3, 10));
        psl.setPosition(RectangleEdge.BOTTOM);
        psl.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        psl.setAxisOffset(5.0);
        psl.setPosition(RectangleEdge.BOTTOM);
        psl.setFrame(new BlockBorder(Color.GRAY));
        if (!hasManagedLanes)
            densityGPChart.addSubtitle(psl);
        viewer = new ChartViewer(densityGPChart);
        viewer.setMinWidth(minWidthGP);
        viewer.setMinHeight(minHeightGP);
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
            renderer.setBlockWidth(maxCellLengthM);
            renderer.setBlockHeight(myDt);
            renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);
            paintScale = densityPaintScale();
            renderer.setPaintScale(paintScale);
            plot = new XYPlot(densityManagedDS, distAxis, timeAxis, renderer);
            plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
            densityManagedChart = new JFreeChart("Density in Managed Lanes", plot);
            densityManagedChart.removeLegend();
            scaleAxis = new org.jfree.chart.axis.NumberAxis("Density (" + UserSettings.unitsDensity + ")");
            scaleAxis.setRange(minDensity, maxDensity);
            psl = new PaintScaleLegend(paintScale, scaleAxis);
            psl.setMargin(new RectangleInsets(3, 10, 3, 10));
            psl.setPosition(RectangleEdge.BOTTOM);
            psl.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            psl.setAxisOffset(5.0);
            psl.setPosition(RectangleEdge.BOTTOM);
            psl.setFrame(new BlockBorder(Color.GRAY));
            densityManagedChart.addSubtitle(psl);
            viewer = new ChartViewer(densityManagedChart);
            vbContours.getWidth();
            viewer.setMinWidth(minWidthManaged);
            viewer.setMinHeight(minHeightManaged);
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
    
    
    /**
     * Generates paint scale for the density contour plot.
     */
    private LookupPaintScale densityPaintScale() {
        if (minDensity >= maxDensity)
            if (minDensity < 1.0)
                maxDensity = minDensity + 1.0;
            else
                minDensity = maxDensity - 1.0;
        LookupPaintScale pScale = new LookupPaintScale(minDensity, maxDensity, Color.white);
        Color[] clr = UtilGUI.gyrkColorScale();
        double delta = (maxDensity - minDensity)/(clr.length - 1);
        double value = minDensity;
        pScale.add(value, clr[0]);
        value += Double.MIN_VALUE;
        for (int i = 1; i < clr.length; i++) {
            pScale.add(value, clr[i]);
            value += delta;
        }
        return pScale;
    }
    
    
    
    
    
    
    
    public void fillTabAggregates() {
        vbAggregates.getChildren().clear();
        String label_gp, label_mng, label_aux;
        XYChart.Series dataSeries_gp, dataSeries_mng, dataSeries_total;
        List<XYDataItem> xydata_gp, xydata_mng, xydata_aux;
        int sz_gp, sz_mng, sz_aux;
        XYDataItem xy;
        double dt = mySimData.get_dt_sec();
        int disp_dt = (int)Math.round(dt / 60.0);
        String per_buf = " per " + disp_dt + " Minutes";
        if (disp_dt == 60)
            per_buf = " per Hour";
        else if (disp_dt > 60) 
            per_buf = " per " + (dt / 3600.0) + " Hours";
        int num_comms = listVT.size();
        TimeSeries[] vmt_series_gp = new TimeSeries[num_comms];
        TimeSeries[] vmt_series_mng = new TimeSeries[num_comms];
        TimeSeries[] vht_series_gp = new TimeSeries[num_comms];
        TimeSeries[] vht_series_mng = new TimeSeries[num_comms];
        TimeSeries[] delay_series_gp = new TimeSeries[num_comms];
        TimeSeries[] delay_series_mng = new TimeSeries[num_comms];

        for (int i = 0; i < num_comms; i++) {
            vmt_series_gp[i] = null;
            vmt_series_mng[i] = null;
            vht_series_gp[i] = null;
            vht_series_mng[i] = null;
        }
        
        String label_units = UserSettings.unitsSpeed;
        double cc = UserSettings.speedConversionMap.get("mph"+label_units);
        double v_thres = UserSettings.defaultFreeFlowSpeedThresholdForDelayMph;
        String label_thres = String.format("(Speed Threshold: %.0f %s)", cc*v_thres, label_units);

        List<AbstractLink> links = myRoute.get_link_sequence();

        for (AbstractLink l : links) {
            double v_thres0 = v_thres;
            if (v_thres < 0)
                v_thres0 = UserSettings.speedConversionMap.get("kphmph") * l.get_gp_freespeed_kph();
            SimDataLink sdl = mySimData.linkdata.get(l.id);
            if (sdl != null) {
                for (int i = 0; i < num_comms; i++) {
                    TimeSeries ts = sdl.get_vmt(lgset_gp, cset(listVT.get(i)));
                    if (ts != null) {
                        if (vmt_series_gp[i] == null)
                            vmt_series_gp[i] = ts;
                        else
                            try {
                                vmt_series_gp[i].add(ts);
                            } catch(Exception e) {
                                opt.utils.Dialogs.ExceptionDialog("Error adding GP Lane VMT timeseries!", e);
                            }
                    }

                    ts = sdl.get_vht(lgset_gp, cset(listVT.get(i)));
                    if (ts != null) {
                        if (vht_series_gp[i] == null)
                            vht_series_gp[i] = ts;
                        else
                            try {
                                vht_series_gp[i].add(ts);
                            } catch(Exception e) {
                                opt.utils.Dialogs.ExceptionDialog("Error adding GP Lane VHT timeseries!", e);
                            }
                    }
                    
                    ts = sdl.get_delay(lgset_gp, cset(listVT.get(i)), (float)v_thres0);
                    if (ts != null) {
                        if (delay_series_gp[i] == null)
                            delay_series_gp[i] = ts;
                        else
                            try {
                                delay_series_gp[i].add(ts);
                            } catch(Exception e) {
                                opt.utils.Dialogs.ExceptionDialog("Error adding GP Lane Delay timeseries!", e);
                            }
                    }

                    if (l.get_mng_lanes() > 0) {
                        if (v_thres < 0)
                            v_thres0 = UserSettings.speedConversionMap.get("kphmph") * l.get_mng_freespeed_kph();
                        ts = sdl.get_vmt(lgset_mng, cset(listVT.get(i)));
                        if (ts != null) {
                            if (vmt_series_mng[i] == null)
                                vmt_series_mng[i] = ts;
                            else
                                try {
                                    vmt_series_mng[i].add(ts);
                                } catch(Exception e) {
                                    opt.utils.Dialogs.ExceptionDialog("Error adding Managed Lane VMT timeseries!", e);
                                }
                        }

                        ts = sdl.get_vht(lgset_mng, cset(listVT.get(i)));
                        if (ts != null) {
                            if (vht_series_mng[i] == null)
                                vht_series_mng[i] = ts;
                            else
                                try {
                                    vht_series_mng[i].add(ts);
                                } catch(Exception e) {
                                    opt.utils.Dialogs.ExceptionDialog("Error adding Managed Lane VHT timeseries!", e);
                                }
                        }
                        
                        ts = sdl.get_delay(lgset_mng, cset(listVT.get(i)), (float)v_thres0);
                        if (ts != null) {
                            if (delay_series_mng[i] == null)
                                delay_series_mng[i] = ts;
                            else
                                try {
                                    delay_series_mng[i].add(ts);
                                } catch(Exception e) {
                                    opt.utils.Dialogs.ExceptionDialog("Error adding Managed Lane Delay timeseries!", e);
                                }
                        }
                    }
                }
            }
        }

        label_gp = "VMT in GP Lanes";
        if (hasAuxLanes)
            label_gp = "VMT in GP and Aux Lanes";
        label_mng = "VMT in Managed Lanes";
        label_gp += per_buf;
        label_mng += per_buf;

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(timeLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("VMT");

        LineChart vmtChart = new LineChart(xAxis, yAxis);
        vmtChart.setTitle(label_gp);

        int max_sz = 0;
        for (int k = 0; k < num_comms; k++) {
            max_sz = Math.max(max_sz, vmt_series_gp[k].values.length);
        }
        double[] total = new double[max_sz];
        for (int i = 0; i < max_sz; i++)
            total[i] = 0;
        for (int k = 0; k < num_comms; k++) {
            dataSeries_gp = new XYChart.Series();
            dataSeries_gp.setName(listVT.get(k).get_name());
            xydata_gp = vmt_series_gp[k].get_XYSeries(listVT.get(k).get_name()).getItems();

            sz_gp = xydata_gp.size();

            for (int i = 0; i < max_sz; i++) {
                if (i < sz_gp) {
                    xy = xydata_gp.get(i);
                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                    total[i] += xy.getYValue();
                } else {
                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
                }
            }
            vmtChart.getData().add(dataSeries_gp);
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
        JFXChartUtil.setupZooming(vmtChart, (MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown() )
                mouseEvent.consume();
        });
        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vmtChart);

        if (hasManagedLanes) {
            xAxis = new NumberAxis();
            xAxis.setLabel(timeLabel);
            yAxis = new NumberAxis();
            yAxis.setLabel("VMT");

            vmtChart = new LineChart(xAxis, yAxis);
            vmtChart.setTitle(label_mng);

            max_sz = 0;
            for (int k = 0; k < num_comms; k++) {
                if (vmt_series_gp[k] != null)
                    max_sz = Math.max(max_sz, vmt_series_gp[k].values.length);
            }
            for (int i = 0; i < max_sz; i++)
                total[i] = 0;
            for (int k = 0; k < num_comms; k++) {
                dataSeries_mng = new XYChart.Series();
                dataSeries_mng.setName(listVT.get(k).get_name());
                xydata_mng = vmt_series_mng[k].get_XYSeries(listVT.get(k).get_name()).getItems();

                sz_mng = xydata_mng.size();

                for (int i = 0; i < max_sz; i++) {
                    if (i < sz_mng) {
                        xy = xydata_mng.get(i);
                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                        total[i] += xy.getYValue();
                    } else {
                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
                    }
                }
                vmtChart.getData().add(dataSeries_mng);
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
            JFXChartUtil.setupZooming(vmtChart, (MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown() )
                mouseEvent.consume();
            });
            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vmtChart);
        }
        vbAggregates.getChildren().add(new Separator());


        label_gp = "VHT in GP Lanes";
        if (hasAuxLanes)
            label_gp = "VHT in GP and Aux Lanes";
        label_mng = "VHT in Managed Lanes";
        label_gp += per_buf;
        label_mng += per_buf;

        xAxis = new NumberAxis();
        xAxis.setLabel(timeLabel);
        yAxis = new NumberAxis();
        yAxis.setLabel("VHT");

        LineChart vhtChart = new LineChart(xAxis, yAxis);
        vhtChart.setTitle(label_gp);

        max_sz = 0;
        for (int k = 0; k < num_comms; k++) {
            max_sz = Math.max(max_sz, vht_series_gp[k].values.length);
        }
        total = new double[max_sz];
        for (int i = 0; i < max_sz; i++)
            total[i] = 0;
        for (int k = 0; k < num_comms; k++) {
            dataSeries_gp = new XYChart.Series();
            dataSeries_gp.setName(listVT.get(k).get_name());
            xydata_gp = vht_series_gp[k].get_XYSeries(listVT.get(k).get_name()).getItems();

            sz_gp = xydata_gp.size();

            for (int i = 0; i < max_sz; i++) {
                if (i < sz_gp) {
                    xy = xydata_gp.get(i);
                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                    total[i] += xy.getYValue();
                } else {
                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
                }
            }
            vhtChart.getData().add(dataSeries_gp);
        }

        dataSeries_total = new XYChart.Series();
        dataSeries_total.setName("Total");
        for (int i = 0; i < max_sz; i++)
            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
        if (listVT.size() > 1)
            vhtChart.getData().add(dataSeries_total);

        vhtChart.setCreateSymbols(false);
        vhtChart.setLegendSide(Side.BOTTOM);
        vhtChart.setMinHeight(300);
        vbAggregates.getChildren().add(vhtChart);
        JFXChartUtil.setupZooming(vhtChart, (MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown() )
                mouseEvent.consume();
        });
        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vhtChart);

        if (hasManagedLanes) {
            xAxis = new NumberAxis();
            xAxis.setLabel(timeLabel);
            yAxis = new NumberAxis();
            yAxis.setLabel("VHT");

            vhtChart = new LineChart(xAxis, yAxis);
            vhtChart.setTitle(label_mng);

            max_sz = 0;
            for (int k = 0; k < num_comms; k++) {
                if (vht_series_gp[k] != null)
                    max_sz = Math.max(max_sz, vht_series_gp[k].values.length);
            }
            for (int i = 0; i < max_sz; i++)
                total[i] = 0;
            for (int k = 0; k < num_comms; k++) {
                dataSeries_mng = new XYChart.Series();
                dataSeries_mng.setName(listVT.get(k).get_name());
                xydata_mng = vht_series_mng[k].get_XYSeries(listVT.get(k).get_name()).getItems();

                sz_mng = xydata_mng.size();

                for (int i = 0; i < max_sz; i++) {
                    if (i < sz_mng) {
                        xy = xydata_mng.get(i);
                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                        total[i] += xy.getYValue();
                    } else {
                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
                    }
                }
                vhtChart.getData().add(dataSeries_mng);
            }

            dataSeries_total = new XYChart.Series();
            dataSeries_total.setName("Total");
            for (int i = 0; i < max_sz; i++)
                dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
            if (listVT.size() > 1)
                vhtChart.getData().add(dataSeries_total);

            vhtChart.setCreateSymbols(false);
            vhtChart.setLegendSide(Side.BOTTOM);
            vhtChart.setMinHeight(300);
            vbAggregates.getChildren().add(vhtChart);
            JFXChartUtil.setupZooming(vhtChart, (MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown() )
                mouseEvent.consume();
            });
            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(vhtChart);
        }
        vbAggregates.getChildren().add(new Separator());

        
        label_gp = "Delay in GP Lanes";
        if (hasAuxLanes)
            label_gp = "Delay in GP and Aux Lanes";
        label_mng = "Delay in Managed Lanes";
        label_gp += per_buf + " ";
        label_mng += per_buf + " ";

        xAxis = new NumberAxis();
        xAxis.setLabel(timeLabel);
        yAxis = new NumberAxis();
        yAxis.setLabel("Delay (veh.-hr.)");

        LineChart delayChart = new LineChart(xAxis, yAxis);
        if (v_thres < 0)
            label_thres = "(Speed Threshold: Free Flow Speed)";
        delayChart.setTitle(label_gp + label_thres);

        max_sz = 0;
        for (int k = 0; k < num_comms; k++) {
            max_sz = Math.max(max_sz, delay_series_gp[k].values.length);
        }
        total = new double[max_sz];
        for (int i = 0; i < max_sz; i++)
            total[i] = 0;
        for (int k = 0; k < num_comms; k++) {
            dataSeries_gp = new XYChart.Series();
            dataSeries_gp.setName(listVT.get(k).get_name());
            xydata_gp = delay_series_gp[k].get_XYSeries(listVT.get(k).get_name()).getItems();

            sz_gp = xydata_gp.size();

            for (int i = 0; i < max_sz; i++) {
                if (i < sz_gp) {
                    xy = xydata_gp.get(i);
                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                    total[i] += xy.getYValue();
                } else {
                    dataSeries_gp.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
                }
            }
            delayChart.getData().add(dataSeries_gp);
        }

        dataSeries_total = new XYChart.Series();
        dataSeries_total.setName("Total");
        for (int i = 0; i < max_sz; i++)
            dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
        if (listVT.size() > 1)
            delayChart.getData().add(dataSeries_total);

        delayChart.setCreateSymbols(false);
        delayChart.setLegendSide(Side.BOTTOM);
        delayChart.setMinHeight(300);
        vbAggregates.getChildren().add(delayChart);
        JFXChartUtil.setupZooming(delayChart, (MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown() )
                mouseEvent.consume();
        });
        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(delayChart);

        if (hasManagedLanes) {
            xAxis = new NumberAxis();
            xAxis.setLabel(timeLabel);
            yAxis = new NumberAxis();
            yAxis.setLabel("Delay (veh.-hr.)");

            delayChart = new LineChart(xAxis, yAxis);
            delayChart.setTitle(label_mng + label_thres);

            max_sz = 0;
            for (int k = 0; k < num_comms; k++) {
                if (delay_series_gp[k] != null)
                    max_sz = Math.max(max_sz, delay_series_gp[k].values.length);
            }
            for (int i = 0; i < max_sz; i++)
                total[i] = 0;
            for (int k = 0; k < num_comms; k++) {
                dataSeries_mng = new XYChart.Series();
                dataSeries_mng.setName(listVT.get(k).get_name());
                xydata_mng = delay_series_mng[k].get_XYSeries(listVT.get(k).get_name()).getItems();

                sz_mng = xydata_mng.size();

                for (int i = 0; i < max_sz; i++) {
                    if (i < sz_mng) {
                        xy = xydata_mng.get(i);
                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, xy.getYValue()));
                        total[i] += xy.getYValue();
                    } else {
                        dataSeries_mng.getData().add(new XYChart.Data((start+i*dt)/timeDivider, 0));
                    }
                }
                delayChart.getData().add(dataSeries_mng);
            }

            dataSeries_total = new XYChart.Series();
            dataSeries_total.setName("Total");
            for (int i = 0; i < max_sz; i++)
                dataSeries_total.getData().add(new XYChart.Data((start+i*dt)/timeDivider, total[i]));
            if (listVT.size() > 1)
                delayChart.getData().add(dataSeries_total);

            delayChart.setCreateSymbols(false);
            delayChart.setLegendSide(Side.BOTTOM);
            delayChart.setMinHeight(300);
            vbAggregates.getChildren().add(delayChart);
            JFXChartUtil.setupZooming(delayChart, (MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown() )
                mouseEvent.consume();
            });
            JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(delayChart);
        }
        
    }
    
    
}
