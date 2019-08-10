/**
 * Copyright (c) 2019, Regents of the University of California
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
package opt.config;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import opt.AppMainController;
import opt.data.Link;



/**
 *
 * @author Alex Kurzhanskiy
 */
public class LinkEditorController {
    private AppMainController appMainController = null;
    private Link myLink = null;
    private boolean ignoreChange  = true;
    
    private SpinnerValueFactory<Double> lengthSpinnerValueFactory = null;
    
    private SpinnerValueFactory<Integer> numLanesGPSpinnerValueFactory = null;
    private SpinnerValueFactory<Integer> numLanesAuxSpinnerValueFactory = null;
    private SpinnerValueFactory<Integer> numLanesManagedSpinnerValueFactory = null;
    
    private SpinnerValueFactory<Double> capacityGPSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> capacityAuxSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> capacityManagedSpinnerValueFactory = null;
    
    private SpinnerValueFactory<Double> ffSpeedGPSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> ffSpeedAuxSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> ffSpeedManagedSpinnerValueFactory = null;
    
    private SpinnerValueFactory<Double> jamDensityGPSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> jamDensityAuxSpinnerValueFactory = null;
    private SpinnerValueFactory<Double> jamDensityManagedSpinnerValueFactory = null;
    
    
    
    
    
    @FXML // fx:id="linkEditorMainPane"
    private SplitPane linkEditorMainPane; // Value injected by FXMLLoader
    
     @FXML // fx:id="canvasParent"
    private AnchorPane canvasParent; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkEditorCanvas"
    private Canvas linkEditorCanvas; // Value injected by FXMLLoader
    
    @FXML // fx:id="addSectionUpstream"
    private Button addSectionUpstream; // Value injected by FXMLLoader

    @FXML // fx:id="duplicateSectionUpstream"
    private Button duplicateSectionUpstream; // Value injected by FXMLLoader

    @FXML // fx:id="duplicateSectionDownstream"
    private Button duplicateSectionDownstream; // Value injected by FXMLLoader

    @FXML // fx:id="addSectionDownstream"
    private Button addSectionDownstream; // Value injected by FXMLLoader

    @FXML // fx:id="deleteSection"
    private Button deleteSection; // Value injected by FXMLLoader

    @FXML // fx:id="labelLength"
    private Label labelLength; // Value injected by FXMLLoader

    @FXML // fx:id="linkName"
    private TextField linkName; // Value injected by FXMLLoader

    @FXML // fx:id="linkType"
    private ChoiceBox<opt.data.Link.Type> linkType; // Value injected by FXMLLoader

    @FXML // fx:id="linkLength"
    private Spinner<Double> linkLength; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkEditorAccordionParent"
    private AnchorPane linkEditorAccordionParent; // Value injected by FXMLLoader

    @FXML // fx:id="linkEditorAccordion"
    private Accordion linkEditorAccordion; // Value injected by FXMLLoader

    @FXML // fx:id="laneProperties"
    private TitledPane laneProperties; // Value injected by FXMLLoader

    @FXML // fx:id="numGPLanes"
    private Spinner<Integer> numGPLanes; // Value injected by FXMLLoader

    @FXML // fx:id="numAuxLanes"
    private Spinner<Integer> numAuxLanes; // Value injected by FXMLLoader

    @FXML // fx:id="numManagedLanes"
    private Spinner<Integer> numManagedLanes; // Value injected by FXMLLoader

    @FXML // fx:id="capacityGPLane"
    private Spinner<Double> capacityGPLane; // Value injected by FXMLLoader

    @FXML // fx:id="capacityAuxLane"
    private Spinner<Double> capacityAuxLane; // Value injected by FXMLLoader

    @FXML // fx:id="capacityManagedLane"
    private Spinner<Double> capacityManagedLane; // Value injected by FXMLLoader

    @FXML // fx:id="ffSpeedGP"
    private Spinner<Double> ffSpeedGP; // Value injected by FXMLLoader

    @FXML // fx:id="ffSpeedAux"
    private Spinner<Double> ffSpeedAux; // Value injected by FXMLLoader

    @FXML // fx:id="ffSpeedManaged"
    private Spinner<Double> ffSpeedManaged; // Value injected by FXMLLoader

    @FXML // fx:id="jamDensityGPLane"
    private Spinner<Double> jamDensityGPLane; // Value injected by FXMLLoader

    @FXML // fx:id="jamDensityAuxLane"
    private Spinner<Double> jamDensityAuxLane; // Value injected by FXMLLoader

    @FXML // fx:id="jamDensityManagedLane"
    private Spinner<Double> jamDensityManagedLane; // Value injected by FXMLLoader
    
    @FXML // fx:id="cbBarrier"
    private CheckBox cbBarrier; // Value injected by FXMLLoader

    @FXML // fx:id="cbSeparated"
    private CheckBox cbSeparated; // Value injected by FXMLLoader
    
     @FXML // fx:id="labelManagedLaneCapacity"
    private Label labelManagedLaneCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="labelGPLaneCapacity"
    private Label labelGPLaneCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="labelAuxLaneCapacity"
    private Label labelAuxLaneCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="labelFreeFlowSpeedManaged"
    private Label labelFreeFlowSpeedManaged; // Value injected by FXMLLoader

    @FXML // fx:id="labelFreeFlowSpeedGP"
    private Label labelFreeFlowSpeedGP; // Value injected by FXMLLoader

    @FXML // fx:id="labelFreeFlowSpeedAux"
    private Label labelFreeFlowSpeedAux; // Value injected by FXMLLoader

    @FXML // fx:id="labelJamDensityManaged"
    private Label labelJamDensityManaged; // Value injected by FXMLLoader

    @FXML // fx:id="labelJamDensityGP"
    private Label labelJamDensityGP; // Value injected by FXMLLoader

    @FXML // fx:id="labelJamDensityAux"
    private Label labelJamDensityAux; // Value injected by FXMLLoader

    @FXML // fx:id="rampsPane"
    private TitledPane rampsPane; // Value injected by FXMLLoader

    @FXML // fx:id="addOnRamp"
    private Button addOnRamp; // Value injected by FXMLLoader

    @FXML // fx:id="addOffRamp"
    private Button addOffRamp; // Value injected by FXMLLoader

    @FXML // fx:id="deleteOnRamps"
    private Button deleteOnRamps; // Value injected by FXMLLoader

    @FXML // fx:id="deleteOffRamps"
    private Button deleteOffRamps; // Value injected by FXMLLoader

    @FXML // fx:id="trafficDemand1"
    private TitledPane trafficDemand1; // Value injected by FXMLLoader

    @FXML // fx:id="trafficSplitDownstream"
    private TitledPane trafficSplitDownstream; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkControllerPane"
    private TitledPane linkControllerPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkEventPane"
    private TitledPane linkEventPane; // Value injected by FXMLLoader



    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the main app controller that is used to sync up
     *               all sub-windows.
     */
    public void setAppMainController(AppMainController ctrl) {
        appMainController = ctrl;
    }
    
    

    @FXML
    void onAddOffRamp(ActionEvent event) {

    }

    @FXML
    void onAddOnRamp(ActionEvent event) {

    }

    @FXML
    void onAddSectionDownstreamAction(ActionEvent event) {

    }

    @FXML
    void onAddSectionUpstreamAction(ActionEvent event) {

    }

    @FXML
    void onDeleteOffRamp(ActionEvent event) {

    }

    @FXML
    void onDeleteOnRamp(ActionEvent event) {

    }

    @FXML
    void onDeleteSection(ActionEvent event) {

    }

    @FXML
    void onLinkNameChanged(ActionEvent event) {
        if (ignoreChange)
            return;
        
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        // Initialize new link window
        
        linkType.setItems(FXCollections.observableArrayList(opt.data.Link.Type.values()));
        linkType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onLinkTypeChange();
        });
        
        lengthSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        linkLength.setValueFactory(lengthSpinnerValueFactory);
        linkLength.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onLinkLengthChange();
        });
        
        numLanesGPSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1, 1);
        numGPLanes.setValueFactory(numLanesGPSpinnerValueFactory);
        numGPLanes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onNumLanesChange();
        });
        
        numLanesAuxSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0, 1);
        numAuxLanes.setValueFactory(numLanesAuxSpinnerValueFactory);
        numAuxLanes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onNumLanesChange();
        });
        
        numLanesManagedSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0, 1);
        numManagedLanes.setValueFactory(numLanesManagedSpinnerValueFactory);
        numManagedLanes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onNumLanesChange();
        });
        
        capacityGPSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        capacityGPLane.setValueFactory(capacityGPSpinnerValueFactory);
        capacityGPLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onCapacityChange();
        });
        
        capacityAuxSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        capacityAuxLane.setValueFactory(capacityAuxSpinnerValueFactory);
        capacityAuxLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onCapacityChange();
        });
        
        capacityManagedSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        capacityManagedLane.setValueFactory(capacityManagedSpinnerValueFactory);
        capacityManagedLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onCapacityChange();
        });
        
        ffSpeedGPSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        ffSpeedGP.setValueFactory(ffSpeedGPSpinnerValueFactory);
        ffSpeedGP.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onFreeFlowSpeedChange();
        });
        
        ffSpeedAuxSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        ffSpeedAux.setValueFactory(ffSpeedAuxSpinnerValueFactory);
        ffSpeedAux.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onFreeFlowSpeedChange();
        });
        
        ffSpeedManagedSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        ffSpeedManaged.setValueFactory(ffSpeedManagedSpinnerValueFactory);
        ffSpeedManaged.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onFreeFlowSpeedChange();
        });
        
        jamDensityGPSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        jamDensityGPLane.setValueFactory(jamDensityGPSpinnerValueFactory);
        jamDensityGPLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onJamDensityChange();
        });
        
        jamDensityAuxSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        jamDensityAuxLane.setValueFactory(jamDensityAuxSpinnerValueFactory);
        jamDensityAuxLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onJamDensityChange();
        });
        
        jamDensityManagedSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        jamDensityManagedLane.setValueFactory(jamDensityManagedSpinnerValueFactory);
        jamDensityManagedLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onJamDensityChange();
        });
        

        
        
        linkEditorCanvas.widthProperty().bind(canvasParent.widthProperty());
        linkEditorCanvas.heightProperty().bind(canvasParent.heightProperty());

        
        
        linkEditorCanvas.widthProperty().addListener((observable, oldValue, newValue) -> {
           drawRoadSection(); 
        });
        
        linkEditorCanvas.heightProperty().addListener((observable, oldValue, newValue) -> {
           drawRoadSection(); 
        });
        
        
        cbBarrier.setTooltip(new Tooltip("Sets barrier between managed and GP lanes"));
        
    }
    
    
    
    
    
    
    /**
     * This function is called every time one opens a link in the
     * configuration module.
     * @param lnk 
     */
    public void initWithLinkData(Link lnk) {
        laneProperties.setExpanded(false);
        
        if (lnk == null)
            return;
        
        ignoreChange = true;
                
        myLink = lnk;
        linkName.setText(myLink.get_name());
        laneProperties.setExpanded(true);
        
        opt.data.Link.Type lnkType = myLink.get_type();
        linkType.setItems(FXCollections.observableArrayList(lnkType)); // to enable choice, remove this line
        linkType.setValue(lnkType);;
        
        String unitsLength = appMainController.getUserSettings().getUnitsLength();
        double length = myLink.get_segment().get_length_meters();
        length = appMainController.getUserSettings().convertFlow(length, "meters", unitsLength);
        labelLength.setText("Length (" + unitsLength + "):");
        //if ((unitsLength.equals("miles")) || (unitsLength.equals("kilometers")))
        lengthSpinnerValueFactory.setValue(length);
        
        if (lnkType == opt.data.Link.Type.freeway) {
            numLanesGPSpinnerValueFactory.setValue(myLink.get_segment().get_mixed_lanes());
            rampsPane.setVisible(true);
        } else if (lnkType == opt.data.Link.Type.onramp) {
            numLanesGPSpinnerValueFactory.setValue(myLink.get_segment().get_or_lanes());
            rampsPane.setVisible(false);
        } else if (lnkType == opt.data.Link.Type.offramp) {
            numLanesGPSpinnerValueFactory.setValue(myLink.get_segment().get_fr_lanes());
            rampsPane.setVisible(false);
        } else if (lnkType == opt.data.Link.Type.connector) {
            numLanesGPSpinnerValueFactory.setValue(myLink.get_segment().get_mixed_lanes());
            rampsPane.setVisible(false);
        }
        
        int managed_lanes = myLink.get_segment().get_managed_lanes();
        int gp_lanes = myLink.get_segment().get_mixed_lanes();
        int aux_lanes = 0;
        if (myLink.get_type() == Link.Type.onramp)
            gp_lanes = myLink.get_segment().get_or_lanes();
        if (myLink.get_type() == Link.Type.offramp)
            gp_lanes = myLink.get_segment().get_fr_lanes();
        
        numLanesManagedSpinnerValueFactory.setValue(managed_lanes);
        numLanesGPSpinnerValueFactory.setValue(gp_lanes);
        numLanesAuxSpinnerValueFactory.setValue(aux_lanes);
        
        
        if (myLink.get_type() != Link.Type.freeway) {
            boolean flag = true;
            numAuxLanes.setDisable(flag);
            capacityAuxLane.setDisable(flag);
            ffSpeedAux.setDisable(flag);
            jamDensityAuxLane.setDisable(flag);
        } else {
            boolean flag = false;
            numAuxLanes.setDisable(flag);
            capacityAuxLane.setDisable(flag);
            ffSpeedAux.setDisable(flag);
            jamDensityAuxLane.setDisable(flag);
        }
        
        
        
        
        
        drawRoadSection();
        
        
        ignoreChange = false;
    }
    
    



    
    
    /**
     * Draw the road section according to its type, lane configuration and ramps.
     */
    @FXML
    private void drawRoadSection() {
        int ramp_angle = 30;
        double width = linkEditorCanvas.getWidth();
        double height = linkEditorCanvas.getHeight();
        boolean rightSideRoads = appMainController.getUserSettings().rightSideDrivingRoads();
        
        int managed_lanes = numLanesManagedSpinnerValueFactory.getValue();
        int gp_lanes = numLanesGPSpinnerValueFactory.getValue();
        int aux_lanes = numLanesAuxSpinnerValueFactory.getValue();
        int total_lanes = gp_lanes + managed_lanes + aux_lanes;
        
        if (managed_lanes > 0) {
            cbBarrier.setDisable(false);
            if (managed_lanes > 1) {
                cbSeparated.setDisable(false);
            } else {
                cbSeparated.setDisable(true);
            }
        } else {
            cbBarrier.setDisable(true);
        }
        
        boolean barrier = cbBarrier.isSelected();
        boolean separated = cbSeparated.isSelected();
        
        
        // TODO: set lane properties to link
        try {
            if ((myLink.get_type() == Link.Type.freeway) || (myLink.get_type() == Link.Type.connector)) {
                myLink.get_segment().set_mixed_lanes(gp_lanes);
            }
        
            if (myLink.get_type() == Link.Type.onramp) {
                myLink.get_segment().set_or_lanes(gp_lanes);
            }
        
            if (myLink.get_type() == Link.Type.offramp) {
                myLink.get_segment().set_fr_lanes(gp_lanes);
            }
        } catch(Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Could not change number of lanes...", e);
        }
        
        
        GraphicsContext g = linkEditorCanvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.clearRect(0, 0, width, height);

        double lane_length = 2*width/3;
        double lane_width = height/16;
        if (total_lanes > 8) { // we want the road segment to take about half of the canvas 
            lane_width = height/(2 * total_lanes);
        }
        
        double x0 = width/6;
        double x1 = x0 + lane_length;
        
        
        if (rightSideRoads) { // right-side driving road
            double base_y = 0.75*height;
            double ramp_length = height - base_y;
            double y1 = base_y;
            double y0 = y1;
            
            // Draw outer on-ramps
            if ((myLink.get_type() == Link.Type.freeway) && 
                myLink.get_segment().has_onramp()) {
                g.setFill(Color.DARKGREY);
                if (aux_lanes > 0)
                    g.setFill(Color.LIGHTGREY);
                double or_lanes = myLink.get_segment().get_or_lanes();
                double or_width = or_lanes * lane_width;
                double rotationCenterX = x0;
                double rotationCenterY = base_y;
                g.save();
                g.translate(rotationCenterX, rotationCenterY);
                g.rotate(-ramp_angle);
                g.translate(-rotationCenterX, -rotationCenterY);
                g.fillRect(x0-ramp_length/2, base_y-or_width/2, ramp_length, or_width);
                g.setStroke(Color.WHITE);
                for (int i = 1; i < or_lanes; i++) {
                    y0 = base_y+or_width/2 - i*lane_width;
                    g.setLineDashes(lane_width/3, lane_width/2);
                    g.setLineWidth(1);
                    g.strokeLine(x0-ramp_length/2, y0, x0+ramp_length/2, y0);
                }
                g.restore();
            }
            
            // Draw outer off-ramps
            if ((myLink.get_type() == Link.Type.freeway) && 
                myLink.get_segment().has_offramp()) {
                g.setFill(Color.DARKGREY);
                if (aux_lanes > 0)
                    g.setFill(Color.LIGHTGREY);
                double fr_lanes = myLink.get_segment().get_fr_lanes();
                double fr_width = fr_lanes * lane_width;
                double rotationCenterX = x1;
                double rotationCenterY = base_y;
                g.save();
                g.translate(rotationCenterX, rotationCenterY);
                g.rotate(ramp_angle);
                g.translate(-rotationCenterX, -rotationCenterY);
                g.fillRect(x1-ramp_length/2, base_y-fr_width/2, ramp_length, fr_width);
                g.setStroke(Color.WHITE);
                for (int i = 1; i < fr_lanes; i++) {
                    y0 = base_y+fr_width/2 - i*lane_width;
                    g.setLineDashes(lane_width/3, lane_width/2);
                    g.setLineWidth(1);
                    g.strokeLine(x1-ramp_length/2, y0, x1+ramp_length/2, y0);
                }
                g.restore();
            }
            
            
            
            
            
            
            
            
            y0 = y1;
            if (aux_lanes > 0) {
                g.setFill(Color.LIGHTGREY);
                y0 = y1 - aux_lanes*lane_width;
                g.fillRect(x0, y0, lane_length, aux_lanes*lane_width);
                y1 = y0;
            }
            if (gp_lanes > 0) {
                g.setFill(Color.DARKGREY);
                y0 = y1 - gp_lanes*lane_width;
                g.fillRect(x0, y0, lane_length, gp_lanes*lane_width);
                y1 = y0;
            }
            if (managed_lanes > 0) {
                g.setFill(Color.BLACK);
                y0 = y1 - managed_lanes*lane_width;
                g.fillRect(x0, y0, lane_length, managed_lanes*lane_width);
            }
            
            g.setStroke(Color.WHITE);
            g.setLineWidth(1);
            //g.setLineDashes(lane_width/3, lane_width/3);
            for (int i = 1; i < total_lanes; i++) {
                if (i == aux_lanes) {
                    g.setLineDashes(lane_width/4, lane_width/3);
                    g.setLineWidth(1);
                } else if (barrier && (i == aux_lanes + gp_lanes)) {
                    g.setLineDashes();
                    g.setLineWidth(2);
                } else if (separated && (i > aux_lanes + gp_lanes)) {
                    g.setLineDashes();
                    g.setLineWidth(1);
                } else {
                    g.setLineDashes(lane_width/3, lane_width/2);
                    g.setLineWidth(1);
                }
                    
                y0 = base_y - lane_width*i;
                g.strokeLine(x0, y0, x1, y0);
                
            }
            
            
            
            
            
            
        
        } else {
            ;//TODO left-side driving road
        }
        
        
        
    }





    /************************************************************
     * CALLBACKS
     ************************************************************/
    
    
    

    private void onLinkTypeChange() {    
        int linkTypeSelectedIndex = linkType.getSelectionModel().getSelectedIndex();
        opt.data.Link.Type lnkType = opt.data.Link.Type.values()[linkTypeSelectedIndex];
        //myLink.get_segment().set_type(lnkType);
        
        System.err.println("Type: " + lnkType + " (" + linkTypeSelectedIndex + ")");
    }
    
    
    @FXML
    private void onLinkLengthChange() {
        String unitsLength = appMainController.getUserSettings().getUnitsLength();
        double length = lengthSpinnerValueFactory.getValue();
        length = appMainController.getUserSettings().convertFlow(length, unitsLength, "meters");
        length = Math.max(length, 0.001);
        try {
            if ((myLink.get_type() == Link.Type.freeway) || (myLink.get_type() == Link.Type.connector)) {
                myLink.get_segment().set_length_meters((float)length);
            }
            if (myLink.get_type() == Link.Type.onramp) {
                myLink.get_segment().set_or_length_meters((float)length);
            }
            if (myLink.get_type() == Link.Type.offramp) {
                myLink.get_segment().set_fr_length_meters((float)length);
            }
        } catch(Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Could not change section length...", e);
        }
        
        System.err.println("Length: " + length);
    }
    
    
    
    private void onNumLanesChange() {    
        
    
        
        
        System.err.println("Lane number change!");
        drawRoadSection();
    }
    
    
    
    private void onCapacityChange() {    
        
        
        System.err.println("Capacity change!");
    }
    
    
    
    private void onFreeFlowSpeedChange() {    
        
        
        System.err.println("Free flow change!");
    }
    
    
    private void onJamDensityChange() {    
        
        
        System.err.println("Jam Density change!");
    }
    
    

    
    
    
}
