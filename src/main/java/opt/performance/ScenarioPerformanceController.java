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
        
        listVT = Misc.makeListVT(myScenario.get_commodities());
        fillTabSummary();

             
    }

    
    
    private void fillTabSummary() {
        vbSummary.getChildren().clear();
        
        PieChart chart;
        ObservableList<PieChart.Data> vmtPieData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> vhtPieData = FXCollections.observableArrayList();
        
        int sz = listVT.size();
        String[] labels = new String[sz];
        double[] c_vmt = new double[sz];
        double[] c_vht = new double[sz];
        int[] prcts = new int[sz];
        double total_vmt = 0;
        double total_vht = 0;
        
        for (int i = 0; i < sz; i++) {
            double t_vmt = 0;
            double t_vht = 0;
            List<Double> vmt_s = mySimData.get_vmt_for_network(listVT.get(i).getId()).values;
            List<Double> vht_s = mySimData.get_vht_for_network(listVT.get(i).getId()).values;
            t_vmt = vmt_s.stream().map((v) -> v).reduce(t_vmt, (accumulator, _item) -> accumulator + _item);
            t_vht = vht_s.stream().map((v) -> v).reduce(t_vht, (accumulator, _item) -> accumulator + _item);
            total_vmt += t_vmt;
            total_vht += t_vht;
            c_vmt[i] = t_vmt;
            c_vht[i] = t_vht;
        }
        
        int vmt_prct = 0;
        int vht_prct = 0;
        for (int i = 0; i < sz; i++) {
            int p_vmt = (int) Math.round(100 * c_vmt[i] / total_vmt);
            int p_vht = (int) Math.round(100 * c_vht[i] / total_vht);
            if (i == sz - 1) {
                p_vmt = Math.max(0, 100 - vmt_prct);
                p_vht = Math.max(0, 100 - vht_prct);
            }
            else {
                vmt_prct += p_vmt;
                vht_prct += p_vht;
            }
            String l = listVT.get(i).get_name() + " = " + (int)Math.round(c_vmt[i]) + " (" + p_vmt + "%)";
            vmtPieData.add(new PieChart.Data(l, c_vmt[i]));
            l = listVT.get(i).get_name() + " = " + (int)Math.round(c_vht[i]) + " (" + p_vht + "%)";
            vhtPieData.add(new PieChart.Data(l, c_vht[i]));
        }
        
        chart = new PieChart(vmtPieData);
        chart.setTitle("Total VMT (" + (int)Math.round(total_vmt) + ")");
        chart.setLegendSide(Side.RIGHT);
        chart.setMinWidth(300);
        chart.setMinHeight(200);
        double prefWidth = scenarioPerformanceMainPane.getPrefWidth();
        double prefHeight = scenarioPerformanceMainPane.getPrefHeight()/3;
        chart.setPrefSize(prefWidth, prefHeight);
        vbSummary.getChildren().add(chart);
        
        chart = new PieChart(vhtPieData);
        chart.setTitle("Total VHT (" + (int)Math.round(total_vht) + ")");
        chart.setLegendSide(Side.RIGHT);
        chart.setMinWidth(300);
        chart.setMinHeight(200);
        prefWidth = scenarioPerformanceMainPane.getPrefWidth();
        prefHeight = scenarioPerformanceMainPane.getPrefHeight()/3;
        chart.setPrefSize(prefWidth, prefHeight);
        vbSummary.getChildren().add(chart);
        
    }
    
    
    
}
