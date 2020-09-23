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
package opt.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import opt.AppMainController;
import opt.UserSettings;
import opt.data.*;
import opt.data.control.*;
import opt.utils.EditCell;
import opt.utils.Misc;
import opt.utils.ModifiedDoubleStringConverter;
import opt.utils.ModifiedIntegerStringConverter;
import opt.utils.ModifiedNumberStringConverter;
import opt.utils.TSTableHandler;
import profiles.Profile1D;


/**
 * Link Editor UI control.
 * Link Editor is located in Configuration tab of the Action Pane when a link
 * is selected in the navigation tree.
 * 
 * @author Alex Kurzhanskiy
 */
public class LinkEditorController {
    private Stage primaryStage = null;
    private AppMainController appMainController = null;
    private NewLinkController newLinkController = null;
    private Scene newLinkScene = null;
    private NewRampController newRampController = null;
    private Scene newRampScene = null;
    private ConnectController connectController = null;
    private Scene connectScene = null;
    private NewRampMeterController newRampMeterController = null;
    private Scene newRampMeterScene = null;

    private RampMeterOpen rampMeterOpen = null;
    private Scene rampMeterOpenScene = null;

    private RampMeterClosed rampMeterClosed = null;
    private Scene rampMeterClosedScene = null;

    private RampMeterAlinea rampMeterAlinea = null;
    private Scene rampMeterAlineaScene = null;

    private RampMeterFixed rampMeterFixed = null;
    private Scene rampMeterFixedScene = null;

    private AbstractLink myLink = null;
    private boolean ignoreChange = true;
    
    private List<AbstractLink> onramps = new ArrayList<AbstractLink>();
    private List<AbstractLink> offramps = new ArrayList<AbstractLink>();
    
    private List<AbstractLink> upConnectCandidates = new ArrayList<AbstractLink>();
    private List<AbstractLink> dnConnectCandidates = new ArrayList<AbstractLink>();
    
    private List<Commodity> listVT = new ArrayList<Commodity>();

    // control -- ramp metering and policies
    private ControlSchedule controlScheduleGP = null;
    private ControlSchedule controlScheduleManaged = null;
    private ScheduleEntry controlEntry = null;

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
    
    private SpinnerValueFactory<Integer> dtDemandSpinnerValueFactory = null;
    private SpinnerValueFactory<Integer> dtSRSpinnerValueFactory = null;
    
    
    private TSTableHandler demandTableHandler = new TSTableHandler();
    private TSTableHandler srTableHandler = new TSTableHandler();
    
    private ObservableList<Object> defaultDemandRow = null;
    private ObservableList<Object> defaultSRRow = null;
    
    
    
    
    @FXML // fx:id="linkEditorMainPane"
    private SplitPane linkEditorMainPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="canvasParent"
    private AnchorPane canvasParent; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkEditorCanvas"
    private Canvas linkEditorCanvas; // Value injected by FXMLLoader
    
    @FXML // fx:id="addSectionUpstream"
    private Button addSectionUpstream; // Value injected by FXMLLoader

    @FXML // fx:id="connectSectionUpstream"
    private Button connectSectionUpstream; // Value injected by FXMLLoader

    @FXML // fx:id="connectSectionDownstream"
    private Button connectSectionDownstream; // Value injected by FXMLLoader

    @FXML // fx:id="addSectionDownstream"
    private Button addSectionDownstream; // Value injected by FXMLLoader

    @FXML // fx:id="deleteSection"
    private Button deleteSection; // Value injected by FXMLLoader

    @FXML // fx:id="labelFromName"
    private Label labelFromName; // Value injected by FXMLLoader

    @FXML // fx:id="labelToName"
    private Label labelToName; // Value injected by FXMLLoader
    
    @FXML // fx:id="labelLength"
    private Label labelLength; // Value injected by FXMLLoader

    @FXML // fx:id="linkFromName"
    private TextField linkFromName; // Value injected by FXMLLoader

    @FXML // fx:id="linkToName"
    private TextField linkToName; // Value injected by FXMLLoader
    
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
    
    @FXML // fx:id="listOnramps"
    private ListView<String> listOnramps; // Value injected by FXMLLoader

    @FXML // fx:id="listOfframps"
    private ListView<String> listOfframps; // Value injected by FXMLLoader

    @FXML // fx:id="addOnRamp"
    private Button addOnRamp; // Value injected by FXMLLoader

    @FXML // fx:id="addOffRamp"
    private Button addOffRamp; // Value injected by FXMLLoader

    @FXML // fx:id="deleteOnRamps"
    private Button deleteOnRamps; // Value injected by FXMLLoader

    @FXML // fx:id="deleteOffRamps"
    private Button deleteOffRamps; // Value injected by FXMLLoader

    @FXML // fx:id="trafficDemand"
    private TitledPane trafficDemand; // Value injected by FXMLLoader
    
    @FXML // fx:id="labelDemandUpdatePeriod"
    private Label labelDemandUpdatePeriod; // Value injected by FXMLLoader

    @FXML // fx:id="dtDemand"
    private Spinner<Integer> dtDemand; // Value injected by FXMLLoader

    @FXML // fx:id="tableDemand"
    private TableView<ObservableList<Object>> tableDemand; // Value injected by FXMLLoader

    @FXML // fx:id="trafficSplitDownstream"
    private TitledPane trafficSplitDownstream; // Value injected by FXMLLoader
    
    @FXML // fx:id="labelSRUpdatePeriod"
    private Label labelSRUpdatePeriod; // Value injected by FXMLLoader

    @FXML // fx:id="dtSR"
    private Spinner<Integer> dtSR; // Value injected by FXMLLoader

    @FXML // fx:id="tableSR"
    private TableView<ObservableList<Object>> tableSR; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkControllerPane"
    private TitledPane linkControllerPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="controlTabPane"
    private TabPane controlTabPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="tabGPLanesControl"
    private Tab tabGPLanesControl; // Value injected by FXMLLoader
    
    @FXML // fx:id="listControllersGP"
    private ListView<String> listControllersGP; // Value injected by FXMLLoader

    @FXML // fx:id="deleteControllerGP"
    private Button deleteControllerGP; // Value injected by FXMLLoader

    @FXML // fx:id="addControllerGP"
    private Button addControllerGP; // Value injected by FXMLLoader
    
    @FXML // fx:id="tabManagedLanesControl"
    private Tab tabManagedLanesControl; // Value injected by FXMLLoader
    
    @FXML // fx:id="listControllersManaged"
    private ListView<String> listControllersManaged; // Value injected by FXMLLoader

    @FXML // fx:id="deleteControllerManaged"
    private Button deleteControllerManaged; // Value injected by FXMLLoader

    @FXML // fx:id="addControllerManaged"
    private Button addControllerManaged; // Value injected by FXMLLoader
    
    @FXML // fx:id="linkEventPane"
    private TitledPane linkEventPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="ttAddSectionUpstream"
    private Tooltip ttAddSectionUpstream; // Value injected by FXMLLoader
    
    @FXML // fx:id="ttAddSectionDownstream"
    private Tooltip ttAddSectionDownstream; // Value injected by FXMLLoader
    
    @FXML // fx:id="ttConnectUpstream"
    private Tooltip ttConnectUpstream; // Value injected by FXMLLoader
    
    @FXML // fx:id="ttConnectDownstream"
    private Tooltip ttConnectDownstream; // Value injected by FXMLLoader
    
    @FXML // fx:id="ttDeleteSection"
    private Tooltip ttDeleteSection; // Value injected by FXMLLoader




    
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
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the new link controller that is used to set up
     *               new road sections.
     */
    public void setNewLinkControllerAndScene(NewLinkController ctrl, Scene scn) {
        newLinkController = ctrl;
        newLinkScene = scn;
        newLinkScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the new ramp controller that is used to set up
     *               new on- and off-ramps.
     */
    public void setNewRampControllerAndScene(NewRampController ctrl, Scene scn) {
        newRampController = ctrl;
        newRampScene = scn;
        newRampScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the connect controller that is used to connect
     *               two open-ended links.
     */
    public void setConnectControllerAndScene(ConnectController ctrl, Scene scn) {
        connectController = ctrl;
        connectScene = scn;
        connectScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the new ramp meter controller that is used to
     *               create a new ramp meter.
     */
    public void setNewRampMeterControllerAndScene(NewRampMeterController ctrl, Scene scn) {
        newRampMeterController = ctrl;
        newRampMeterScene = scn;
        newRampMeterScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }


    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the Open ramp meter controller that is used to
     *               edit Open ramp meter.
     */
    public void setRampMeterOpenControllerAndScene(RampMeterOpen ctrl, Scene scn) {
        rampMeterOpen = ctrl;
        rampMeterOpenScene = scn;
        rampMeterOpenScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }

    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the Closed ramp meter controller that is used to
     *               edit Closed ramp meter.
     */
    public void setRampMeterClosedControllerAndScene(RampMeterClosed ctrl, Scene scn) {
        rampMeterClosed = ctrl;
        rampMeterClosedScene = scn;
        rampMeterClosedScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }

    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the ALINEA ramp meter controller that is used to
     *               edit ALINEA ramp meter.
     */
    public void setRampMeterAlineaControllerAndScene(RampMeterAlinea ctrl, Scene scn) {
        rampMeterAlinea = ctrl;
        rampMeterAlineaScene = scn;
        rampMeterAlineaScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the Fixed rate ramp meter controller that is used to
     *               edit Fixed rate ramp meter.
     */
    public void setRampMeterFixedRateControllerAndScene(RampMeterFixed ctrl, Scene scn) {
        rampMeterFixed = ctrl;
        rampMeterFixedScene = scn;
        rampMeterFixedScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    
    public void setProjectModified(boolean val) {
        appMainController.setProjectModified(val);
    }
    
    public void updateTablePositions(KeyCode kc) {
        if (myLink.get_up_link() == null)
            demandTableHandler.onKeyPressed2(kc);
        if (myLink.get_type() == AbstractLink.Type.offramp)
            srTableHandler.onKeyPressed2(kc);
    }
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {        
        linkFromName.textProperty().addListener((observable, oldValue, newValue) -> {
            onLinkNameChanged(null);
        });
        
        linkToName.textProperty().addListener((observable, oldValue, newValue) -> {
            onLinkNameChanged(null);
        });
        
        
        
        
        double length_step = 1;
        if (UserSettings.unitsLength.equals("kilometers") || UserSettings.unitsLength.equals("miles"))
            length_step = 0.1;
        lengthSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, length_step);
        lengthSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        linkLength.setValueFactory(lengthSpinnerValueFactory);
        linkLength.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                /*double length = (double) myLink.get_length_meters();
                length = UserSettings.convertLength(length, "meters", UserSettings.unitsLength);
                linkLength.getValueFactory().setValue(length);*/
                return;
            }
            if (!ignoreChange && ((oldValue == null) || (Math.abs(oldValue-newValue) > 0.00001))) {
                onLinkLengthChange();
            }
        });
        linkLength.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double length = (double) -1;
            if (myLink != null) {
                length = (double) myLink.get_length_meters();
                length = UserSettings.convertLength(length, "meters", UserSettings.unitsLength);
            }
            opt.utils.WidgetFunctionality.commitEditorText(linkLength, length);
        });
        linkLength.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            opt.utils.WidgetFunctionality.commitEditorText(linkLength);
        });
        
        
        numLanesGPSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1, 1);
        numLanesGPSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter());
        numGPLanes.setValueFactory(numLanesGPSpinnerValueFactory);
        numGPLanes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                numGPLanes.getValueFactory().setValue(myLink.get_gp_lanes());
                return;
            }
            if (!ignoreChange && (!Objects.equals(oldValue, newValue)))
                onNumLanesChange();
        });
        numGPLanes.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue)
                return;
            int num_lanes = 1;
            if (myLink != null) {
                num_lanes = myLink.get_gp_lanes();
            }
            opt.utils.WidgetFunctionality.commitEditorText(numGPLanes, num_lanes);
        });
        numGPLanes.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            opt.utils.WidgetFunctionality.commitEditorText(numGPLanes);
        });
        
        
        numLanesAuxSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0, 1);
        numLanesAuxSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter());
        numAuxLanes.setValueFactory(numLanesAuxSpinnerValueFactory);
        numAuxLanes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                numAuxLanes.getValueFactory().setValue(myLink.get_aux_lanes());
                return;
            }
            if (!ignoreChange && (!Objects.equals(oldValue, newValue)))
                onNumLanesChange();
        });
        numAuxLanes.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Integer num_lanes = 0;
            if (myLink != null) {
                num_lanes = myLink.get_aux_lanes();
            }
            opt.utils.WidgetFunctionality.commitEditorText(numAuxLanes, num_lanes);
        });
        numAuxLanes.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            opt.utils.WidgetFunctionality.commitEditorText(numAuxLanes);
        });
        
        
        numLanesManagedSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0, 1);
        numLanesManagedSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter());
        numManagedLanes.setValueFactory(numLanesManagedSpinnerValueFactory);
        numManagedLanes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                numManagedLanes.getValueFactory().setValue(myLink.get_mng_lanes());
                return;
            }
            if (!ignoreChange && (!Objects.equals(oldValue, newValue)))
                onNumLanesChange();
        });
        numManagedLanes.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Integer num_lanes = 0;
            if (myLink != null) {
                num_lanes = myLink.get_mng_lanes();
            }
            opt.utils.WidgetFunctionality.commitEditorText(numManagedLanes, num_lanes);
        });
        numManagedLanes.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            opt.utils.WidgetFunctionality.commitEditorText(numManagedLanes);
        });
        
        
        double cap_step = 1;
        if (UserSettings.unitsFlow.equals("vps"))
            cap_step = 0.01;
        capacityGPSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, cap_step);
        capacityGPSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        capacityGPLane.setValueFactory(capacityGPSpinnerValueFactory);
        capacityGPLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        capacityGPLane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double cap = -1.0;
            if (myLink != null) {
                cap = (double)myLink.get_gp_capacity_vphpl();
                cap = UserSettings.convertFlow(cap, "vph", UserSettings.unitsFlow);
            }
            opt.utils.WidgetFunctionality.commitEditorText(capacityGPLane, cap);
        });
        
        capacityAuxSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, cap_step);
        capacityAuxSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        capacityAuxLane.setValueFactory(capacityAuxSpinnerValueFactory);
        capacityAuxLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        capacityAuxLane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double cap = -1.0;
            if (myLink != null) {
                cap = (double)(myLink.get_type() == AbstractLink.Type.freeway ? myLink.get_aux_capacity_vphpl() : 0.0);
                cap = UserSettings.convertFlow(cap, "vph", UserSettings.unitsFlow);
            }
            opt.utils.WidgetFunctionality.commitEditorText(capacityAuxLane, cap);
        });
        
        capacityManagedSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, cap_step);
        capacityManagedSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        capacityManagedLane.setValueFactory(capacityManagedSpinnerValueFactory);
        capacityManagedLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        capacityManagedLane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double cap = new Double(-1);
            if (myLink != null) {
                cap = new Double(myLink.get_mng_capacity_vphpl());
                cap = UserSettings.convertFlow(cap, "vph", UserSettings.unitsFlow);
            }
            opt.utils.WidgetFunctionality.commitEditorText(capacityManagedLane, cap);
        });
        
        ffSpeedGPSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        ffSpeedGPSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        ffSpeedGP.setValueFactory(ffSpeedGPSpinnerValueFactory);
        ffSpeedGP.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        ffSpeedGP.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double ffspeed = new Double(-1);
            if (myLink != null) {
                ffspeed = new Double(myLink.get_gp_freespeed_kph());
                ffspeed = UserSettings.convertFlow(ffspeed, "kph", UserSettings.unitsSpeed);
            }
            opt.utils.WidgetFunctionality.commitEditorText(ffSpeedGP, ffspeed);
        });
        
        ffSpeedAuxSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        ffSpeedAuxSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        ffSpeedAux.setValueFactory(ffSpeedAuxSpinnerValueFactory);
        ffSpeedAux.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        ffSpeedAux.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double ffspeed = new Double(-1);
            if (myLink != null) {
                ffspeed = new Double(myLink.get_type() == AbstractLink.Type.freeway ? myLink.get_aux_ff_speed_kph() : 0.0);
                ffspeed = UserSettings.convertFlow(ffspeed, "kph", UserSettings.unitsSpeed);
            }
            opt.utils.WidgetFunctionality.commitEditorText(ffSpeedAux, ffspeed);
        });
        
        ffSpeedManagedSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        ffSpeedManagedSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        ffSpeedManaged.setValueFactory(ffSpeedManagedSpinnerValueFactory);
        ffSpeedManaged.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        ffSpeedManaged.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double ffspeed = -1.0;
            if (myLink != null) {
                ffspeed = (double)myLink.get_mng_freespeed_kph();
                ffspeed = UserSettings.convertFlow(ffspeed, "kph", UserSettings.unitsSpeed);
            }
            opt.utils.WidgetFunctionality.commitEditorText(ffSpeedManaged, ffspeed);
        });
        
        double density_step = 1;
        String num_format = "#.##";
        if (UserSettings.unitsDensity.equals("vpmtr") || UserSettings.unitsDensity.equals("vpf")) {
            density_step = 0.001;
            num_format = "#.###";
        }
        jamDensityGPSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, density_step);
        jamDensityGPSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter(num_format));
        jamDensityGPLane.setValueFactory(jamDensityGPSpinnerValueFactory);
        jamDensityGPLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        jamDensityGPLane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double jamDensity = new Double(-1);
            if (myLink != null) {
                jamDensity = new Double(myLink.get_gp_jam_density_vpkpl());
                jamDensity = UserSettings.convertDensity(jamDensity, "vpkm", UserSettings.unitsDensity);
            }
            opt.utils.WidgetFunctionality.commitEditorText(jamDensityGPLane, jamDensity);
        });
        
        jamDensityAuxSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, density_step);
        jamDensityAuxSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter(num_format));
        jamDensityAuxLane.setValueFactory(jamDensityAuxSpinnerValueFactory);
        jamDensityAuxLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        jamDensityAuxLane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double jamDensity = new Double(-1);
            if (myLink != null) {
                jamDensity = new Double(myLink.get_type() == AbstractLink.Type.freeway ? myLink.get_aux_jam_density_vpkpl() : 0.0);
                jamDensity = UserSettings.convertDensity(jamDensity, "vpkm", UserSettings.unitsDensity);
            }
            opt.utils.WidgetFunctionality.commitEditorText(jamDensityAuxLane, jamDensity);
        });
        
        jamDensityManagedSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, density_step);
        jamDensityManagedSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter(num_format));
        jamDensityManagedLane.setValueFactory(jamDensityManagedSpinnerValueFactory);
        jamDensityManagedLane.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onParamChange();
        });
        jamDensityManagedLane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double jamDensity = new Double(-1);
            if (myLink != null) {
                jamDensity = new Double(myLink.get_mng_jam_density_vpkpl());
                jamDensity = UserSettings.convertDensity(jamDensity, "vpkm", UserSettings.unitsDensity);
            }
            opt.utils.WidgetFunctionality.commitEditorText(jamDensityManagedLane, jamDensity);
        });
        
        
        
        /**
         * Demand pane
         **/
        dtDemandSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1440, UserSettings.defaultDemandDtMinutes, 1);
        dtDemandSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter());
        dtDemand.setValueFactory(dtDemandSpinnerValueFactory);
        dtDemand.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onDtDemandChange();
        });
        dtDemand.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Integer dt = new Integer(UserSettings.defaultDemandDtMinutes);
            // TODO: obtain demand dt
            opt.utils.WidgetFunctionality.commitEditorText(dtDemand, dt);
        });
        
        demandTableHandler.setTable(tableDemand);
        tableDemand.setOnKeyPressed(event -> {
            if (ignoreChange)
                return;
            demandTableHandler.setDt(dtDemandSpinnerValueFactory.getValue());
            if (demandTableHandler.onKeyPressed(event))
                setDemand();
            
            TablePosition<ObservableList<Object>, ?> focusedCell = tableDemand.focusModelProperty().get().focusedCellProperty().get();
            if ((event.getCode() == KeyCode.C) && event.isControlDown()) {
                appMainController.setLeftStatus("Copied demand data from '" + myLink.get_name() + "' to clipboard.");
            }
            
            if ((event.getCode() == KeyCode.DELETE) || (event.getCode() == KeyCode.BACK_SPACE)) {
                int del_num = demandTableHandler.deleteRows();
                setDemand();
                appMainController.setLeftStatus("Deleted " + del_num + " demand entries from '" + myLink.get_name() + "'.");
            }

            if (event.getCode().isDigitKey()) {              
                tableDemand.edit(focusedCell.getRow(), focusedCell.getTableColumn());
            } 
        });
        
        tableDemand.setOnMouseClicked(event -> {
            if (ignoreChange)
                return;
            demandTableHandler.onMouseClicked(event);
        });
        
        tableDemand.setRowFactory(tv -> {
            TableRow<ObservableList<Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if ((event.getClickCount() == 2) && (row.isEmpty())) {
                    demandTableHandler.addRow();
                    setDemand();
                }
            });
            return row ;
        });
        
        tableDemand.getSelectionModel().setCellSelectionEnabled(true);
        tableDemand.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        /*tableDemand.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                demandTableHandler.onSelection(newSelection);
            }
        });*/

       
        
        /**
         * Split Ratio pane
         **/
        dtSRSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1440, UserSettings.defaultSRDtMinutes, 1);
        dtSRSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter());
        dtSR.setValueFactory(dtSRSpinnerValueFactory);
        dtSR.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onDtSRChange();
        });
        dtSR.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Integer dt = UserSettings.defaultSRDtMinutes;
            // TODO: obtain SR dt
            opt.utils.WidgetFunctionality.commitEditorText(dtSR, dt);
        });
        
        srTableHandler.setTable(tableSR);
        tableSR.setOnKeyPressed(event -> {
            if (ignoreChange)
                return;
            srTableHandler.setDt(dtSRSpinnerValueFactory.getValue());
            if (srTableHandler.onKeyPressed(event))
                setSR();
            
            TablePosition<ObservableList<Object>, ?> focusedCell = tableSR.focusModelProperty().get().focusedCellProperty().get();
            if ((event.getCode() == KeyCode.C) && event.isControlDown()) {
                appMainController.setLeftStatus("Copied split ratio data from '" + myLink.get_name() + "' to clipboard.");
            }
            
            if ((event.getCode() == KeyCode.DELETE) || (event.getCode() == KeyCode.BACK_SPACE)) {
                int del_num = srTableHandler.deleteRows();
                setSR();
                appMainController.setLeftStatus("Deleted " + del_num + " split ratio entries from '" + myLink.get_name() + "'.");
            }

            
            if (event.getCode().isDigitKey()) {              
                tableSR.edit(focusedCell.getRow(), focusedCell.getTableColumn());
            } 
        });
        
        tableSR.setOnMouseClicked(event -> {
            if (ignoreChange)
                return;
            srTableHandler.onMouseClicked(event);
        });
        
        tableSR.setRowFactory(tv -> {
            TableRow<ObservableList<Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if ((event.getClickCount() == 2) && (row.isEmpty())) {
                    srTableHandler.addRow();
                    setSR();
                }
            });
            return row ;
        });
        
        tableSR.getSelectionModel().setCellSelectionEnabled(true);
        tableSR.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
       
        
        linkEditorCanvas.widthProperty().bind(canvasParent.widthProperty());
        linkEditorCanvas.heightProperty().bind(canvasParent.heightProperty());

        
        
        linkEditorCanvas.widthProperty().addListener((observable, oldValue, newValue) -> {
           ignoreChange = true;
           drawRoadSection(); 
           ignoreChange = false;
        });
        
        linkEditorCanvas.heightProperty().addListener((observable, oldValue, newValue) -> {
           ignoreChange = true;
           drawRoadSection();
           ignoreChange = false;
        });
        
        
    }
    
    
    
    /**
     * This function is called every time one opens a link in the
     * configuration module.
     * @param lnk 
     */
    public void initWithLinkData(AbstractLink lnk) {        
        if (lnk == null)
            return;
        
        ignoreChange = true;        
        myLink = lnk;
        
        UserSettings.linkEditorController = this;
        appMainController.setLeftStatus("");
    
        initHeader();
        initLaneProperties();
        initOnOffRamps();
        initDemand();
        initSR();
        initControllers();
            
        drawRoadSection();
        
        ignoreChange = false;
    }
    
    
    
    
    
    /***************************************************************************
     * Header initialization and callbacks
     ***************************************************************************/
    
    /**
     * Initialize the header part of the Link Editor.
     */
    private void initHeader() {
        String link_name = myLink.get_name();
        String[] name_subs = link_name.split(" -> ");
        int sz = name_subs.length;
        String from_name = name_subs[0];
        String to_name = "";
        for (int i = 1; i < sz; i++) {
            to_name += name_subs[i];
            if (i < sz - 1)
                to_name += " -> ";
        }
        if (myLink.get_type() == AbstractLink.Type.onramp) {
            if (from_name.equals(""))
                linkFromName.setText(to_name);
            else
                linkFromName.setText(from_name);
            linkToName.setText("");
            linkFromName.setVisible(true);
            linkToName.setVisible(false);
            labelFromName.setVisible(true);
            labelToName.setVisible(false);
            deleteSection.setVisible(false);
            addSectionDownstream.setVisible(false);
            connectSectionDownstream.setVisible(false);
            if (myLink.get_up_link() == null) {
                addSectionUpstream.setVisible(true);
            }
            else {
                addSectionUpstream.setVisible(false);
            }
        } else if (myLink.get_type() == AbstractLink.Type.offramp) {
            if (to_name.equals(""))
                linkToName.setText(from_name);
            else
                linkToName.setText(to_name);
            linkFromName.setText("");
            linkFromName.setVisible(false);
            linkToName.setVisible(true);
            labelFromName.setVisible(false);
            labelToName.setVisible(true);
            labelToName.setText("TO Name:");
            deleteSection.setVisible(false);
            addSectionUpstream.setVisible(false);
            connectSectionUpstream.setVisible(false);
            if (myLink.get_dn_link() == null) {
                addSectionDownstream.setVisible(true);
            }
            else {
                addSectionDownstream.setVisible(false);
            }
        } else {
            linkFromName.setText(from_name);
            linkToName.setText(to_name);
            linkFromName.setVisible(true);
            linkToName.setVisible(true);
            labelFromName.setVisible(true);
            labelToName.setVisible(true);
            labelToName.setText("TO Name:");
            deleteSection.setVisible(true);
            addSectionUpstream.setVisible(true);
            addSectionDownstream.setVisible(true);
            
            if (myLink.get_type() == AbstractLink.Type.connector) {
                if (myLink.get_up_link() != null)
                    addSectionUpstream.setVisible(false);
                if (myLink.get_dn_link() != null)
                    addSectionDownstream.setVisible(false);   
            }
        }
        
        String unitsLength = UserSettings.unitsLength;
        double length = myLink.get_length_meters();
        length = UserSettings.convertLength(length, "meters", unitsLength);
        labelLength.setText("Length (" + unitsLength + "):");
        lengthSpinnerValueFactory.setValue(length);
        
        
        upConnectCandidates.clear();
        dnConnectCandidates.clear();
        FreewayScenario scenario = myLink.get_segment().get_scenario();
        List<AbstractLink> allLinks = scenario.get_links();
        for (AbstractLink l : allLinks) {
            if (myLink.get_type() == AbstractLink.Type.freeway) {
                if (l.get_type() == AbstractLink.Type.freeway) {
                    if ((myLink.get_up_link() == null) && 
                        (l.get_dn_link() == null) &&
                        (!myLink.equals(l)))
                        upConnectCandidates.add(l);
                    if ((myLink.get_dn_link() == null) && 
                        (l.get_up_link() == null) &&
                        (!myLink.equals(l)))
                        dnConnectCandidates.add(l);
                }
            } else if (myLink.get_type() == AbstractLink.Type.connector) {
                if (l.get_type() == AbstractLink.Type.onramp) {
                    if ((myLink.get_dn_link() == null) && 
                        (l.get_up_link() == null))
                        dnConnectCandidates.add(l);
                }
                if (l.get_type() == AbstractLink.Type.offramp) {
                    if ((myLink.get_up_link() == null) && 
                        (l.get_dn_link() == null))
                        upConnectCandidates.add(l);
                }
            } else if (myLink.get_type() == AbstractLink.Type.onramp) {
                if (myLink.get_up_link() != null)
                    break;
                if ((l.get_type() == AbstractLink.Type.connector) && (l.get_dn_link() == null))
                    upConnectCandidates.add(l);
            } else { // offramp
                if (myLink.get_dn_link() != null)
                    break;
                if ((l.get_type() == AbstractLink.Type.connector) && (l.get_up_link() == null))
                    dnConnectCandidates.add(l);
            }
        }
        
        if (upConnectCandidates.isEmpty())
            connectSectionUpstream.setVisible(false);
        else
            connectSectionUpstream.setVisible(true);
            
        if (dnConnectCandidates.isEmpty())
            connectSectionDownstream.setVisible(false);
        else
            connectSectionDownstream.setVisible(true);
        
        cbBarrier.setSelected(myLink.get_mng_barrier());
        cbSeparated.setSelected(myLink.get_mng_separated());
        
        if (myLink.get_type() == AbstractLink.Type.freeway) {
             ttDeleteSection.setText("Delete the entire freeway section with all its ramps (if it has any)");
             ttAddSectionDownstream.setText("Create and attach a new freeway section downstream");
             if (myLink.get_dn_link() != null) 
                 ttAddSectionDownstream.setText("Create and insert a new freeway section downstream");
             ttAddSectionUpstream.setText("Create and attach a new freeway section upstream");
             if (myLink.get_up_link() != null) 
                 ttAddSectionDownstream.setText("Create and insert a new freeway section upstream");
             ttConnectDownstream.setText("Connect to beginning of downstream freeway segment");
             ttConnectUpstream.setText("Connect to end of upstream freeway segment");
        } else if (myLink.get_type() == AbstractLink.Type.connector) {
             ttDeleteSection.setText("Delete the connector");
             ttAddSectionDownstream.setText("Create and attach a new freeway section with an on-ramp downstream");
             ttAddSectionUpstream.setText("Create and attach a new freeway with an off-ramp section upstream");
             ttConnectDownstream.setText("Connect to beginning of downstream freeway segment");
             ttConnectUpstream.setText("Connect to end of upstream freeway segment");
        } else if (myLink.get_type() == AbstractLink.Type.onramp) {
             ttAddSectionUpstream.setText("Create and attach a new connector upstream");
             ttConnectUpstream.setText("Connect to end of upstream freeway segment");
        } else { // off-ramp
             ttAddSectionDownstream.setText("Create and attach a new connector downstream");
             ttConnectDownstream.setText("Connect to beginning of downstream freeway segment");
        }
    }
    
    
    
    /*
     * Add, Connect Delete section button callbacks
     */
    
    @FXML
    void onAddSectionDownstreamAction(ActionEvent event) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newLinkScene);
        newLinkController.initWithTwoLinks(myLink, null);
        inputStage.setTitle("New Freeway Section");
        if ((myLink.get_type() == AbstractLink.Type.onramp) || (myLink.get_type() == AbstractLink.Type.offramp))
            inputStage.setTitle("New Connector");
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }

    @FXML
    void onAddSectionUpstreamAction(ActionEvent event) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newLinkScene);
        newLinkController.initWithTwoLinks(null, myLink);
        inputStage.setTitle("New Freeway Section");
        if ((myLink.get_type() == AbstractLink.Type.onramp) || (myLink.get_type() == AbstractLink.Type.offramp))
            inputStage.setTitle("New Connector");
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }
    
    
    @FXML
    void onConnectSectionDownstreamAction(ActionEvent event) {
        if (dnConnectCandidates.isEmpty()) {
            opt.utils.Dialogs.ErrorDialog("There are no sections to connect to...", "No compatibe origin sections are found!");
            return;
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(connectScene);
        String title = "Connect to a Freeway Section Downstream";
        String label = "Choose an Origin Freeway Section:";
        if (myLink.get_type() == AbstractLink.Type.offramp) {
            title = "Connect to a Connector Downstream";
            label = "Choose a Connector:";
        } else if (myLink.get_type() == AbstractLink.Type.connector) {
            title = "Connect to an On-Ramp Downstream";
            label = "Choose an On-Ramp:";
        } else if (myLink.get_type() == AbstractLink.Type.onramp) {
            opt.utils.Dialogs.ErrorDialog("On-ramps cannot connect to downstream sections...", "Please, report this problem!");
            return;
        }
        connectController.initWithLinkAndCandidates(myLink, dnConnectCandidates, label, true);
        inputStage.setTitle(title);
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }

    @FXML
    void onConnectSectionUpstreamAction(ActionEvent event) {
        if (upConnectCandidates.isEmpty()) {
            opt.utils.Dialogs.ErrorDialog("There are no sections to connect to...", "No compatibe destination sections are found!");
            return;
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(connectScene);
        String title = "Connect to a Freeway Section Upstream";
        String label = "Choose a Destination Freeway Section:";
        if (myLink.get_type() == AbstractLink.Type.onramp) {
            title = "Connect to a Connector Upstream";
            label = "Choose a Connector:";
        } else if (myLink.get_type() == AbstractLink.Type.connector) {
            title = "Connect to an Off-Ramp Upstream";
            label = "Choose an Off-Ramp:";
        } else if (myLink.get_type() == AbstractLink.Type.offramp) {
            opt.utils.Dialogs.ErrorDialog("Off-ramps cannot connect to upstream sections...", "Please, report this problem!");
            return;
        }
        connectController.initWithLinkAndCandidates(myLink, upConnectCandidates, label, false);
        inputStage.setTitle(title);
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }
    

    @FXML
    void onDeleteSection(ActionEvent event) {
        if (ignoreChange)
            return;
        
        String header = "You are deleting " +
                opt.utils.Misc.linkType2String(myLink.get_type()).toLowerCase() +
                " section '" + myLink.get_name() + "'...";
        String content = "Are you sure?";
        String bt1 = "Yes, Disconnect Freeway";
        String bt2 = "Yes, Reconnect Freeway";
        String bt3 = "No";
        
        if ((myLink.get_type() == AbstractLink.Type.connector) ||
            (myLink.get_up_link() == null) || (myLink.get_dn_link() == null)) {
            if (!opt.utils.Dialogs.ConfirmationYesNoDialog(header, content))
                return;
           
            appMainController.deleteLink(myLink, false);
            return;
        }
        
        int res = opt.utils.Dialogs.Confirmation3ButtonDialog(header, content, bt1, bt2, bt3);
        if (res < 0)
            return;
        
        appMainController.deleteLink(myLink, (res > 0));
    }
    
    
    /*
     * Link name and length callbacks
     */

    @FXML
    void onLinkNameChanged(ActionEvent event) {
        if (ignoreChange)
            return;
        
        String from = linkFromName.getText();
        String to = linkToName.getText();
        
        if (from.equals("") && (myLink.get_type() != AbstractLink.Type.offramp))
            return;
        
        if (to.equals("") && (myLink.get_type() != AbstractLink.Type.onramp))
            return;
        
        String link_name = linkFromName.getText() + " -> " + linkToName.getText();
        link_name = opt.utils.Misc.validateAndCorrectLinkName(link_name, myLink.get_segment().get_scenario());
        
        myLink.set_name(link_name);
        if ((myLink.get_type() == AbstractLink.Type.freeway) || (myLink.get_type() == AbstractLink.Type.connector))
            myLink.get_segment().name = link_name;
        
        appMainController.objectNameUpdate(myLink);
    }
    
    
    
    @FXML
    private void onLinkLengthChange() {
        if (ignoreChange)
            return;
        String unitsLength = UserSettings.unitsLength;
        double length = lengthSpinnerValueFactory.getValue();
        /*if (length < 0.001) {
            length = myLink.get_length_meters();
            length = UserSettings.convertLength(length, "meters", unitsLength);
            lengthSpinnerValueFactory.setValue(length);
            return;
        }*/
        
        length = UserSettings.convertLength(length, unitsLength, "meters");
        if (length < 0.1)
            return;
        
        try {
            myLink.set_length_meters((float)length);
            appMainController.setProjectModified(true);
        } catch(Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Could not change section length...", e);
        }

    }
    
    
    
    
    
    /***************************************************************************
     * Lane properties pane: initialization and callbacks
     ***************************************************************************/
    
    private void initLaneProperties() {
        int managed_lanes = myLink.get_mng_lanes();
        int gp_lanes = myLink.get_gp_lanes();
        int aux_lanes = myLink.get_aux_lanes();
        
        numLanesManagedSpinnerValueFactory.setValue(managed_lanes);
        numLanesGPSpinnerValueFactory.setValue(gp_lanes);
        numLanesAuxSpinnerValueFactory.setValue(aux_lanes);
        
        if (myLink.get_type() != AbstractLink.Type.freeway) {
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
        
        if (new Double(myLink.get_mng_capacity_vphpl()).isNaN()) {
            try {
                myLink.set_mng_capacity_vphpl((float)UserSettings.defaultManagedLaneCapacityVph);
            } catch (Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set capacity for managed lane...", e);
            }
        } 
        
        if (new Double(myLink.get_mng_freespeed_kph()).isNaN()) {
            try {
                myLink.set_mng_freespeed_kph((float)UserSettings.defaultManagedLaneFreeFlowSpeedKph);
            } catch (Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set free flow speed for managed lane...", e);
            }
        }
        
        if (new Double(myLink.get_mng_jam_density_vpkpl()).isNaN()) {
            try {
                myLink.set_mng_jam_density_vpkpl((float)UserSettings.defaultManagedLaneJamDensityVpk);
            } catch (Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set jam density for managed lane...", e);
            }
        }
        
        if (new Double(myLink.get_aux_capacity_vphpl()).isNaN()) {
            try {
                myLink.set_aux_capacity_vphpl((float)UserSettings.defaultAuxLaneCapacityVph);
            } catch (Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set capacity for auxiliary lane...", e);
            }
        } 
        
        if (new Double(myLink.get_aux_ff_speed_kph()).isNaN()) {
            try {
                myLink.set_aux_ff_speed_kph((float)UserSettings.defaultAuxLaneFreeFlowSpeedKph);
            } catch (Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set free flow speed for auxiliary lane...", e);
            }
        }
        
        if (new Double(myLink.get_aux_jam_density_vpkpl()).isNaN()) {
            try {
                myLink.set_aux_jam_density_vpkpl((float)UserSettings.defaultAuxLaneJamDensityVpk);
            } catch (Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set jam density for auxiliary lane...", e);
            }
        } 
        
        
        // Capacity per lane
        String unitsFlow = UserSettings.unitsFlow;
        labelGPLaneCapacity.setText("GP Lane Capacity (" + unitsFlow + "):");
        double cap = myLink.get_gp_capacity_vphpl();
        cap = UserSettings.convertFlow(cap, "vph", unitsFlow);
        capacityGPSpinnerValueFactory.setValue(cap);
        labelManagedLaneCapacity.setText("M Lane Capacity (" + unitsFlow + "):");
        cap = myLink.get_mng_capacity_vphpl(); 
        cap = UserSettings.convertFlow(cap, "vph", unitsFlow);
        capacityManagedSpinnerValueFactory.setValue(cap);
        labelAuxLaneCapacity.setText("Aux Lane Capacity (" + unitsFlow + "):");
        cap = myLink.get_type() == AbstractLink.Type.freeway ? myLink.get_aux_capacity_vphpl() : 0.0;
        cap = UserSettings.convertFlow(cap, "vph", unitsFlow);
        capacityAuxSpinnerValueFactory.setValue(cap);

        // Free flow speed
        String unitsSpeed = UserSettings.unitsSpeed;
        labelFreeFlowSpeedGP.setText("GP Lane Free Flow Speed (" + unitsSpeed + "):");
        double ffspeed = myLink.get_gp_freespeed_kph();
        ffspeed = UserSettings.convertSpeed(ffspeed, "kph", unitsSpeed);
        ffSpeedGPSpinnerValueFactory.setValue(ffspeed);
        labelFreeFlowSpeedManaged.setText("M Lane Free Flow Speed (" + unitsSpeed + "):");
        ffspeed = myLink.get_mng_freespeed_kph(); // FIXME
        ffspeed = UserSettings.convertSpeed(ffspeed, "kph", unitsSpeed);
        ffSpeedManagedSpinnerValueFactory.setValue(ffspeed);
        labelFreeFlowSpeedAux.setText("Aux Lane Free Flow Speed (" + unitsSpeed + "):");
        ffspeed = myLink.get_type() == AbstractLink.Type.freeway ? myLink.get_aux_ff_speed_kph() : 0.0;
        ffspeed = UserSettings.convertSpeed(ffspeed, "kph", unitsSpeed);
        ffSpeedAuxSpinnerValueFactory.setValue(ffspeed);
        
        // Jam density per lane
        String unitsDensity = UserSettings.unitsDensity;
        labelJamDensityGP.setText("GP Lane Jam Density (" + unitsDensity + "):");
        double jamDensity = myLink.get_mng_jam_density_vpkpl();
        jamDensity = UserSettings.convertDensity(jamDensity, "vpkm", unitsDensity);
        jamDensityManagedSpinnerValueFactory.setValue(jamDensity);
        labelJamDensityManaged.setText("M Lane Jam Density (" + unitsDensity + "):");
        jamDensity = myLink.get_gp_jam_density_vpkpl();
        jamDensity = UserSettings.convertDensity(jamDensity, "vpkm", unitsDensity);
        jamDensityGPSpinnerValueFactory.setValue(jamDensity);
        labelJamDensityAux.setText("Aux Lane Jam Density (" + unitsDensity + "):");
        jamDensity = myLink.get_type() == AbstractLink.Type.freeway ? myLink.get_aux_jam_density_vpkpl() : 0.0;
        jamDensity = UserSettings.convertDensity(jamDensity, "vpkm", unitsDensity);
        jamDensityAuxSpinnerValueFactory.setValue(jamDensity);
        
    }
    
    
    
    /*
     * Lane number and road parameters callbacks
     */
    
    private void onNumLanesChange() { 
        if (ignoreChange)
            return;
        
        int num_lanes = numLanesGPSpinnerValueFactory.getValue();
        if (num_lanes < 1) {
            num_lanes = myLink.get_gp_lanes();
            numLanesGPSpinnerValueFactory.setValue(new Integer(num_lanes));
            return;
        }
        
        num_lanes = numLanesManagedSpinnerValueFactory.getValue();
        if (num_lanes < 0) {
            num_lanes = myLink.get_mng_lanes();
            numLanesManagedSpinnerValueFactory.setValue(new Integer(num_lanes));
            return;
        }
        
        num_lanes = numLanesAuxSpinnerValueFactory.getValue();
        if (num_lanes < 0) {
            num_lanes = myLink.get_aux_lanes();
            numLanesAuxSpinnerValueFactory.setValue(num_lanes);
            return;
        }
        
        drawRoadSection();
    }
    
    
    private void onParamChange() {  
        if (ignoreChange)
            return;
        
        String unitsFlow = UserSettings.unitsFlow;
        String unitsSpeed = UserSettings.unitsSpeed;
        String unitsDensity = UserSettings.unitsDensity;
        
        double cap = capacityGPSpinnerValueFactory.getValue();
        if (cap <= 0) {
            cap = myLink.get_gp_capacity_vphpl();
            cap = UserSettings.convertFlow(cap, "vph", unitsFlow);
            capacityGPSpinnerValueFactory.setValue(cap);
            return;
        }
        double ffspeed = ffSpeedGPSpinnerValueFactory.getValue();
        if (ffspeed <= 0) {
            ffspeed = myLink.get_gp_freespeed_kph();
            ffspeed = UserSettings.convertSpeed(ffspeed, "kph", unitsSpeed);
            ffSpeedGPSpinnerValueFactory.setValue(ffspeed);
            return;
        }
        double jamDensity = jamDensityGPSpinnerValueFactory.getValue();
        if (jamDensity <= 0) {
            jamDensity = myLink.get_gp_jam_density_vpkpl();
            jamDensity = UserSettings.convertDensity(jamDensity, "vpkm", unitsDensity);
            jamDensityGPSpinnerValueFactory.setValue(jamDensity);
            return;
        }
        
        cap = capacityGPSpinnerValueFactory.getValue();
        cap = UserSettings.convertFlow(cap, unitsFlow, "vph");
        try {
            myLink.set_gp_capacity_vphpl((float)cap);
        } catch (Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Cannot set capacity for general purpose lane...", e);
        }
        ffspeed = ffSpeedGPSpinnerValueFactory.getValue();
        ffspeed = UserSettings.convertSpeed(ffspeed, unitsSpeed, "kph");
        try {
            myLink.set_gp_freespeed_kph((float)ffspeed);
        } catch (Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Cannot set free flow speed for general purpose lane...", e);
        }
        jamDensity = jamDensityGPSpinnerValueFactory.getValue();
        jamDensity = UserSettings.convertDensity(jamDensity, unitsDensity, "vpkm");
        try {
            myLink.set_gp_jam_density_vpkpl((float)jamDensity);
        } catch(Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Cannot set jam density for general purpose lane...", e);
        }
        
        
        if (myLink.get_mng_lanes() > 0) {
            cap = capacityManagedSpinnerValueFactory.getValue();
            if (cap < 0) {
                cap = myLink.get_mng_capacity_vphpl();
                cap = UserSettings.convertFlow(cap, "vph", unitsFlow);
                capacityManagedSpinnerValueFactory.setValue(cap);
                return;
            }
            ffspeed = ffSpeedManagedSpinnerValueFactory.getValue();
            if (ffspeed <= 0) {
                ffspeed = myLink.get_mng_freespeed_kph();
                ffspeed = UserSettings.convertSpeed(ffspeed, "kph", unitsSpeed);
                ffSpeedManagedSpinnerValueFactory.setValue(ffspeed);
                return;
            }
            jamDensity = jamDensityManagedSpinnerValueFactory.getValue();
            if (jamDensity <= 0) {
                jamDensity = myLink.get_mng_jam_density_vpkpl();
                jamDensity = UserSettings.convertDensity(jamDensity, "vpkm", unitsDensity);
                jamDensityManagedSpinnerValueFactory.setValue(jamDensity);
                return;
            }
            
            cap = capacityManagedSpinnerValueFactory.getValue();
            cap = UserSettings.convertFlow(cap, unitsFlow, "vph");
            try {
                myLink.set_mng_capacity_vphpl((float)cap);
            } catch (Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set capacity for managed lane...", e);
            }
            ffspeed = ffSpeedManagedSpinnerValueFactory.getValue();
            ffspeed = UserSettings.convertSpeed(ffspeed, unitsSpeed, "kph");
            try {
                myLink.set_mng_freespeed_kph((float)ffspeed);
            } catch (Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set free flow speed for managed lane...", e);
            }
            jamDensity = jamDensityManagedSpinnerValueFactory.getValue();
            jamDensity = UserSettings.convertDensity(jamDensity, unitsDensity, "vpkm");
            try {
                myLink.set_mng_jam_density_vpkpl((float)jamDensity);
            } catch(Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set jam density for managed lane...", e);
            }
        }
    
        if (myLink.get_aux_lanes() > 0) {
            cap = capacityAuxSpinnerValueFactory.getValue();
            if (cap < 0) {
                cap =  myLink.get_type() == AbstractLink.Type.freeway ? myLink.get_gp_capacity_vphpl() : 0.0;
                cap = UserSettings.convertFlow(cap, "vph", unitsFlow);
                capacityAuxSpinnerValueFactory.setValue(cap);
                return;
            }
            ffspeed = ffSpeedAuxSpinnerValueFactory.getValue();
            if (ffspeed <= 0) {
                ffspeed = myLink.get_type() == AbstractLink.Type.freeway ? myLink.get_aux_ff_speed_kph() : 0.0;
                ffspeed = UserSettings.convertSpeed(ffspeed, "kph", unitsSpeed);
                ffSpeedAuxSpinnerValueFactory.setValue(ffspeed);
                return;
            }
            jamDensity = jamDensityAuxSpinnerValueFactory.getValue();
            if (jamDensity <= 0) {
                jamDensity = myLink.get_type() == AbstractLink.Type.freeway ? myLink.get_aux_jam_density_vpkpl() : 0.0;
                jamDensity = UserSettings.convertDensity(jamDensity, "vpkm", unitsDensity);
                jamDensityAuxSpinnerValueFactory.setValue(jamDensity);
                return;
            }
            
            cap = myLink.get_type() == AbstractLink.Type.freeway ? capacityAuxSpinnerValueFactory.getValue() : 0.0;
            cap = UserSettings.convertFlow(cap, unitsFlow, "vph");
            try {
                myLink.set_aux_capacity_vphpl((float)cap);
            } catch (Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set capacity for auxiliary lane...", e);
            }
            ffspeed = myLink.get_type() == AbstractLink.Type.freeway ? ffSpeedAuxSpinnerValueFactory.getValue() : 0.0;
            ffspeed = UserSettings.convertSpeed(ffspeed, unitsSpeed, "kph");
            try {
                myLink.set_aux_ff_speed_kph((float)ffspeed);
            } catch (Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set free flow speed for auxiliary lane...", e);
            }
            jamDensity = myLink.get_type() == AbstractLink.Type.freeway ? jamDensityAuxSpinnerValueFactory.getValue() : 0.0;
            jamDensity = UserSettings.convertDensity(jamDensity, unitsDensity, "vpkm");
            try {
                myLink.set_aux_jam_density_vpkpl((float)jamDensity);
            } catch(Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set jam density for auxiliary lane...", e);
            }
        }
         
        appMainController.setProjectModified(true);
    }
    
    
    
    
    
    
    
    
    /***************************************************************************
     * On-ramps and off-ramps pane: initialization and callbacks
     ***************************************************************************/
    
    private void initOnOffRamps() {
        if (myLink.get_type() == AbstractLink.Type.freeway) {
            rampsPane.setDisable(false);
        } else {
            if (rampsPane.isExpanded())
                laneProperties.setExpanded(true);
            rampsPane.setDisable(true);
        }
        
        listOnramps.getItems().clear();
        onramps.clear();
        listOfframps.getItems().clear();
        offramps.clear();
        
        if (myLink.get_type() == AbstractLink.Type.freeway) {
            // Populate onramp list
            int num_ramps = myLink.get_segment().num_out_ors();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink or = myLink.get_segment().out_ors(i);
                listOnramps.getItems().add(or.get_name() + " (outer)");
                onramps.add(or);
            }
            num_ramps = myLink.get_segment().num_in_ors();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink or = myLink.get_segment().in_ors(i);
                listOnramps.getItems().add(or.get_name() + " (inner)");
                onramps.add(or);
            }
        
            // Populate offramp list
            num_ramps = myLink.get_segment().num_out_frs();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink fr = myLink.get_segment().out_frs(i);
                listOfframps.getItems().add(fr.get_name() + " (outer)");
                offramps.add(fr);
            }
            num_ramps = myLink.get_segment().num_in_frs();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink fr = myLink.get_segment().in_frs(i);
                listOfframps.getItems().add(fr.get_name() + " (inner)");
                offramps.add(fr);
            }
        }
    }
    
    
    
    /*
     * Add/delete on/off-ramp and jump-to-ramp callbacks
     */

    @FXML
    void onAddOnRamp(ActionEvent event) {
        int num_in_ors = myLink.get_segment().num_in_ors();
        int num_out_ors = myLink.get_segment().num_out_ors();
        if ((num_in_ors >= 3) && (num_out_ors >= 3)) {
            opt.utils.Dialogs.ErrorDialog("Cannot add an on-ramp to this freeway section...",
                                          "The on-ramp limit is reached!");
            return;
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newRampScene);
        newRampController.initWithLinkAndType(myLink, AbstractLink.Type.onramp);
        inputStage.setTitle("New On-Ramp");
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }
    
    @FXML
    void onAddOffRamp(ActionEvent event) {
        int num_in_frs = myLink.get_segment().num_in_frs();
        int num_out_frs = myLink.get_segment().num_out_frs();
        if ((num_in_frs >= 3) && (num_out_frs >= 3)) {
            opt.utils.Dialogs.ErrorDialog("Cannot add an off-ramp to this freeway section...",
                                          "The off-ramp limit is reached!");
            return;
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newRampScene);
        newRampController.initWithLinkAndType(myLink, AbstractLink.Type.offramp);
        inputStage.setTitle("New Off-Ramp");
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }

        
    @FXML
    void onDeleteOnRamp(ActionEvent event) {
        if (ignoreChange)
            return;
        
        int idx = listOnramps.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= onramps.size()))
            return;
        
        String header = "You are deleting on-ramp '" + onramps.get(idx).get_name() + "'...";
                
        if (!opt.utils.Dialogs.ConfirmationYesNoDialog(header, "Are you sure?")) 
            return;
     
        if (idx < myLink.get_segment().num_out_ors())
            myLink.get_segment().delete_out_or((LinkOnramp)onramps.get(idx));
        else
            myLink.get_segment().delete_in_or((LinkOnramp)onramps.get(idx));
        
        appMainController.objectNameUpdate(myLink);
    }
    
    @FXML
    void onDeleteOffRamp(ActionEvent event) {
        if (ignoreChange)
            return;
        
        int idx = listOfframps.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= offramps.size()))
            return;
            
        String header = "You are deleting off-ramp '" + offramps.get(idx).get_name() + "'...";
                
        if (!opt.utils.Dialogs.ConfirmationYesNoDialog(header, "Are you sure?")) 
            return;
        
        if (idx < myLink.get_segment().num_out_frs())
            myLink.get_segment().delete_out_fr((LinkOfframp)offramps.get(idx));
        else
            myLink.get_segment().delete_in_fr((LinkOfframp)offramps.get(idx));
        
        appMainController.objectNameUpdate(myLink);
    }

    
    @FXML
    void onrampsOnClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            int idx = listOnramps.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= onramps.size()))
                return;
            appMainController.selectLink(onramps.get(idx));
        }
    }
    
    @FXML
    void onrampsKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            int idx = listOnramps.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= onramps.size()))
                return;
            appMainController.selectLink(onramps.get(idx));
        }
        if ((event.getCode() == KeyCode.DELETE) || (event.getCode() == KeyCode.BACK_SPACE)) {
            onDeleteOnRamp(null);
        }
    }
    
    
    @FXML
    void offrampsOnClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            int idx = listOfframps.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= offramps.size()))
                return;
            appMainController.selectLink(offramps.get(idx));
        }
    }

    @FXML
    void offrampsKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            int idx = listOfframps.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= offramps.size()))
                return;
            appMainController.selectLink(offramps.get(idx));
        }
        if ((event.getCode() == KeyCode.DELETE) || (event.getCode() == KeyCode.BACK_SPACE)) {
            onDeleteOffRamp(null);
        }
    }

    
    
    
    
    /***************************************************************************
     * Demand pane: initialization and callbacks
     ***************************************************************************/
    
    private void initDemand() {
        if (myLink.get_up_link() == null) {
            trafficDemand.setDisable(false);
        } else {
             if (trafficDemand.isExpanded())
                laneProperties.setExpanded(true);
            trafficDemand.setDisable(true);
            return;
        }
        
        tableDemand.getItems().clear();
        tableDemand.getColumns().clear();
        listVT.clear();
        
        TableColumn<ObservableList<Object>, String> colTime = new TableColumn("Time");
        colTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0).toString()));
        colTime.setStyle( "-fx-alignment: CENTER;");
        colTime.setEditable(false);
        colTime.setSortable(false);
        tableDemand.getColumns().add(colTime);
        colTime.prefWidthProperty().bind(tableDemand.widthProperty().multiply(0.09));
        colTime.setReorderable(false);
        
        Map<Long, Commodity> mapVT = myLink.get_segment().get_scenario().get_commodities();
        mapVT.forEach((k, v) -> {listVT.add(v);});
        int num_vt = listVT.size();
        
        TableColumn<ObservableList<Object>, Number> colDemand = new TableColumn("Demand (" + UserSettings.unitsFlow + ")");
        colDemand.setCellFactory(EditCell.<ObservableList<Object>, Number>forTableColumn(new ModifiedNumberStringConverter(), true));
        colDemand.setCellValueFactory(data -> new SimpleDoubleProperty((Double)data.getValue().get(1)));
        colDemand.setStyle( "-fx-alignment: CENTER-RIGHT;");
        colDemand.setEditable(true);
        colDemand.setSortable(false);
        tableDemand.getColumns().add(colDemand);
        colDemand.prefWidthProperty().bind(tableDemand.widthProperty().multiply(0.89/(num_vt+1)));
        colDemand.setOnEditCommit(event -> {
            if (ignoreChange)
                return;
            TablePosition<ObservableList<Object>, ?> focusedCell = event.getTablePosition();
            if ((event.getNewValue() != null) && (!event.getNewValue().equals("")))
                tableDemand.getItems().get(focusedCell.getRow()).set(focusedCell.getColumn(), event.getNewValue().doubleValue());
            tableDemand.refresh();
            setDemand();
        });
        colDemand.setReorderable(false);
        
        List<List<Double>> profiles = new ArrayList<>();
        double pdt = Integer.MAX_VALUE;
        int numSteps = 0;
        for (int i = 0; i < num_vt; i++) {
            final int idx = i;
            TableColumn<ObservableList<Object>, Number> col = new TableColumn(listVT.get(i).get_name() + " (%)");
            col.setCellFactory(EditCell.<ObservableList<Object>, Number>forTableColumn(new ModifiedNumberStringConverter(), true));
            col.setCellValueFactory(data -> new SimpleDoubleProperty((Double)data.getValue().get(idx+2)));
            col.setStyle( "-fx-alignment: CENTER-RIGHT;");
            col.setEditable(true);
            col.setSortable(false);
            tableDemand.getColumns().add(col);
            col.prefWidthProperty().bind(tableDemand.widthProperty().multiply(0.89/(num_vt+1)));
            col.setOnEditCommit(event -> {
                if (ignoreChange)
                    return;
                TablePosition<ObservableList<Object>, ?> focusedCell = event.getTablePosition();
                if ((event.getNewValue() != null) && (!event.getNewValue().equals(""))) {
                    double val = event.getNewValue().doubleValue();
                    val = Math.min(Math.max(val, 0), 100);
                    tableDemand.getItems().get(focusedCell.getRow()).set(focusedCell.getColumn(), val);
                }
                tableDemand.refresh();
                setDemand();
            });
            col.setReorderable(false);
            Profile1D cdp = myLink.get_demand_vph(listVT.get(i).getId());
            if (cdp != null) {
                pdt = Math.min(pdt, cdp.get_dt());
                List<Double> lst = cdp.get_values();
                if (lst == null) {
                    lst = new ArrayList<>();
                }
                if (lst.size() < 1) {
                    lst.add(0.0);
                }
                numSteps = Math.max(numSteps, lst.size());
                profiles.add(lst);
            } else {
                pdt = Math.min(pdt, UserSettings.defaultDemandDtMinutes * 60);
                List<Double> lst = new ArrayList<>();
                lst.add(0.0);
                profiles.add(lst);
                numSteps = Math.max(numSteps, lst.size());
            }
        }
        
        int dtD = (int)Math.round(pdt / 60);
        
        double fcc = UserSettings.flowConversionMap.get("vph" + UserSettings.unitsFlow);
        
        ObservableList<Object> row;
        for (int i = 0; i < numSteps; i++) {
            row = FXCollections.observableArrayList();
            row.add(opt.utils.Misc.minutes2timeString(i*dtD));
            
            row.add(0.0);
            
            double total = 0;
            for (int j = 0; j < num_vt; j++) {
                double val = profiles.get(j).get(profiles.get(j).size()-1);
                if (i < profiles.get(j).size()) {
                    val = profiles.get(j).get(i);
                }
                total += val;
                row.add(val);
            }
            
            for (int j = 0; j < num_vt; j++) {
                double val = (Double)row.get(j+2);
                val = Math.round(100 * val / total);
                row.set(j+2, val);
                if ((total < 0.0000000001) && (j == 0))
                    row.set(j+2, 100.0);
            }
            
            row.set(1, fcc*total);
            tableDemand.getItems().add(row);
        }
        
        row = FXCollections.observableArrayList();
        row.add("");
        row.add(0.0);
        row.add(100.0);
        for (int i = 1; i < num_vt + 1; i++)
            row.add(0.0);
        demandTableHandler.setDefaultRow(row);
        demandTableHandler.setDt(dtD);
        
        if (num_vt == 1) {
            demandTableHandler.setColumnValue(2, 100.0);
        }
        
        tableDemand.refresh();

        dtDemandSpinnerValueFactory.setValue(dtD);
    }
    
    
    
    /*
     * Demand dt and matrix callbacks 
     */
    
    private void onDtDemandChange() { 
        if (ignoreChange)
            return;
        
        int dt = dtDemandSpinnerValueFactory.getValue();
        demandTableHandler.setDt(dt);
        demandTableHandler.timeColumnUpdate();
        setDemand();
    }
    
    
    private void setDemand() {
        if (ignoreChange)
            return;
        
        float dt = 60 * dtDemandSpinnerValueFactory.getValue();
        ObservableList<ObservableList<Object>> myItems = tableDemand.getItems();
        int numSteps = myItems.size();
        int num_vt = listVT.size();
        
        if (num_vt == 1) {
            demandTableHandler.setColumnValue(2, 100.0);
        }
        
        double fcc = UserSettings.flowConversionMap.get(UserSettings.unitsFlow + "vph");
        
        for (int j = 0; j < num_vt; j++) {
            double[] values = new double[numSteps];
            
            for (int i = 0; i < numSteps; i++) {
                double total_prct = 0;
                for (int jj = 0; jj < num_vt; jj++) {
                    total_prct += (Double)myItems.get(i).get(jj+2);
                }
                if (total_prct < 0.0000000001) {
                    myItems.get(i).set(2, 100.0);
                    total_prct = 100.0;
                }
                values[i] = fcc * (Double)myItems.get(i).get(1) * (Double)myItems.get(i).get(j+2) / total_prct;
            }
            
            try {
                myLink.set_demand_vph(listVT.get(j).getId(), dt, values);
            } catch(Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set demand for vehicle type '" + listVT.get(j).get_name() + "'...", e);
            }
        }
        
        appMainController.setProjectModified(true);
    }
    
    
    
    
    
    /***************************************************************************
     * Split Ratio pane: initialization and callbacks
     ***************************************************************************/
    
    private void initSR() {
        if (myLink.get_type() == AbstractLink.Type.offramp) {
            trafficSplitDownstream.setDisable(false);
        } else {
             if (trafficSplitDownstream.isExpanded())
                laneProperties.setExpanded(true);
            trafficSplitDownstream.setDisable(true);
            return;
        }
        
        tableSR.getItems().clear();
        tableSR.getColumns().clear();
        listVT.clear();
        
        TableColumn<ObservableList<Object>, String> colTime = new TableColumn("Time");
        colTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0).toString()));
        colTime.setStyle( "-fx-alignment: CENTER;");
        colTime.setEditable(false);
        colTime.setSortable(false);
        tableSR.getColumns().add(colTime);
        colTime.prefWidthProperty().bind(tableSR.widthProperty().multiply(0.09));
        colTime.setReorderable(false);
        
        Map<Long, Commodity> mapVT = myLink.get_segment().get_scenario().get_commodities();
        mapVT.forEach((k, v) -> {listVT.add(v);});
        int num_vt = listVT.size();
        
        List<List<Double>> profiles = new ArrayList<>();
        double pdt = Integer.MAX_VALUE;
        int numSteps = 0;
        for (int i = 0; i < num_vt; i++) {
            final int idx = i;
            TableColumn<ObservableList<Object>, Number> col = new TableColumn(listVT.get(i).get_name() + " (%)");
            col.setCellFactory(EditCell.<ObservableList<Object>, Number>forTableColumn(new ModifiedNumberStringConverter(), true));
            col.setCellValueFactory(data -> new SimpleDoubleProperty((Double)data.getValue().get(idx+1)));
            col.setStyle( "-fx-alignment: CENTER-RIGHT;");
            col.setEditable(true);
            col.setSortable(false);
            tableSR.getColumns().add(col);
            col.prefWidthProperty().bind(tableSR.widthProperty().multiply(0.89/(num_vt)));
            col.setOnEditCommit(event -> {
                if (ignoreChange)
                    return;
                TablePosition<ObservableList<Object>, ?> focusedCell = event.getTablePosition();
                if ((event.getNewValue() != null) && (!event.getNewValue().equals(""))) {
                    double val = event.getNewValue().doubleValue();
                    val = Math.min(Math.max(val, 0), 100);
                    tableSR.getItems().get(focusedCell.getRow()).set(focusedCell.getColumn(), val);
                }
                tableSR.refresh();
                setSR();
            });
            col.setReorderable(false);
            Profile1D cdp = ((LinkOfframp)myLink).get_splits(listVT.get(i).getId(),
                    Math.min(pdt, UserSettings.defaultSRDtMinutes * 60));
            if (cdp != null) {
                pdt = Math.min(pdt, cdp.get_dt());
                List<Double> lst = cdp.get_values();
                if (lst == null) {
                    lst = new ArrayList<>();
                }
                if (lst.size() < 1) {
                    lst.add(0.0);
                }
                numSteps = Math.max(numSteps, lst.size());
                profiles.add(lst);
            } else {
                assert(false); //GG this should never happen
                pdt = Math.min(pdt, UserSettings.defaultSRDtMinutes * 60);
                List<Double> lst = new ArrayList<>();
                lst.add(0.0);
                profiles.add(lst);
                numSteps = Math.max(numSteps, lst.size());
            }
        }
        
        int dtSR = (int)Math.round(pdt / 60);
        
        ObservableList<Object> row;
        for (int i = 0; i < numSteps; i++) {
            row = FXCollections.observableArrayList();
            row.add(opt.utils.Misc.minutes2timeString(i*dtSR));
            
            double total = 0;
            for (int j = 0; j < num_vt; j++) {
                double val = profiles.get(j).get(profiles.get(j).size()-1);
                if (i < profiles.get(j).size()) {
                    val = profiles.get(j).get(i);
                }
                row.add(100*val);
            }
            
            tableSR.getItems().add(row);
        }
        
        row = FXCollections.observableArrayList();
        row.add("");
        for (int i = 1; i < num_vt + 1; i++)
            row.add(0.0);
        srTableHandler.setDefaultRow(row);
        srTableHandler.setDt(dtSR);
        
        tableSR.refresh();

        dtSRSpinnerValueFactory.setValue(dtSR);
    }
    
    
    
    /*
     * Split Ratio dt and matrix callbacks 
     */
    
    private void onDtSRChange() { 
        if (ignoreChange)
            return;
        
        int dt = dtSRSpinnerValueFactory.getValue();
        srTableHandler.setDt(dt);
        srTableHandler.timeColumnUpdate();
        setSR();
    }
    
    
    private void setSR() {
        if (ignoreChange)
            return;
        
        float dt = 60 * dtSRSpinnerValueFactory.getValue();
        ObservableList<ObservableList<Object>> myItems = tableSR.getItems();
        int numSteps = myItems.size();
        int num_vt = listVT.size();
        
        for (int j = 0; j < num_vt; j++) {
            double[] values = new double[numSteps];
            
            for (int i = 0; i < numSteps; i++) {
                double total_prct = 100;
                values[i] = (Double)myItems.get(i).get(j+1) / total_prct;
            }
            
            try {
                ((LinkOfframp)myLink).set_split(listVT.get(j).getId(), dt, values);
            } catch(Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Cannot set split ratios for vehicle type '" + listVT.get(j).get_name() + "'...", e);
            }
        }
        
        appMainController.setProjectModified(true);
    }
    
    
    

    
    
    
    
    
    
    /***************************************************************************
     * Controller pane: initialization and callbacks
     ***************************************************************************/
    
    private void refreshControllerList(boolean isml) {

        read_controlType_and_lanegroup();

        ControlSchedule controlSchedule = controlScheduleGP;
        ListView<String> listControllers = listControllersGP;
        if (isml) {
            controlSchedule = controlScheduleManaged;
            listControllers = listControllersManaged;
        }
        
        listControllers.getItems().clear();
        for (ScheduleEntry entry : controlSchedule.get_entries()) {
            float start_time = entry.get_start_time();
            float end_time = entry.get_end_time();
            AbstractController cntrl = entry.get_cntrl();
            String ct = "";
            String rm_qc = "";
            if (cntrl.getType() == AbstractController.Type.RampMetering) {
                if (cntrl.getAlgorithm() == control.AbstractController.Algorithm.rm_open) {
                    ct = "Ramp open";
                    rm_qc = "";
                } else if (cntrl.getAlgorithm() == control.AbstractController.Algorithm.rm_open) {
                    ct = "Ramp closed";
                    rm_qc = "";
                } else {
                    ct = "Ramp meter - " + cntrl.getName();
                    if (((AbstractControllerRampMeter)cntrl).isHas_queue_control())
                        rm_qc = " with queue control";
                }
            }

            String buf = Misc.seconds2timestring(start_time, ":");
            if(Float.isFinite(end_time))
                buf += " - " + Misc.seconds2timestring(end_time, ":");
            else
                buf += " - End";
            buf += ": " + ct + rm_qc;

            listControllers.getItems().add(buf);
        }

    }

    /** called iff a link is selected in the tree **/
    private void initControllers() {
        if (myLink.get_type() == AbstractLink.Type.onramp) {
            linkControllerPane.setText("Ramp Metering");
            linkControllerPane.setDisable(false);
        } else {
            linkControllerPane.setText("Control");
            linkControllerPane.setDisable(true);
            if (linkControllerPane.isExpanded())
                laneProperties.setExpanded(true);
            return;
        }
        
        if (myLink.get_mng_lanes() > 0)
            tabManagedLanesControl.setDisable(false);
        else {
            tabManagedLanesControl.setDisable(true);
            controlTabPane.getSelectionModel().selectFirst();
        }
        
        refreshControllerList(false);
        refreshControllerList(true);
    }

    private void read_controlType_and_lanegroup(){
        controlScheduleGP = myLink.get_controller_schedule(LaneGroupType.gp, AbstractController.Type.RampMetering);
        if (controlScheduleGP == null) {
            try {
                controlScheduleGP = ControlFactory.create_empty_controller_schedule(null, "GP Lane Ramp Metering Scheme", myLink, LaneGroupType.gp, AbstractController.Type.RampMetering);
                if (!controlScheduleGP.add_link(myLink))
                    throw new Exception("Error adding on-ramp to GP lane ramp metering scheme.");
            } catch(Exception ex) {
                opt.utils.Dialogs.ExceptionDialog("Error adding ramp metering scheme for GP lanes", ex);
            }
        }
        
        controlScheduleManaged = myLink.get_controller_schedule(LaneGroupType.mng, AbstractController.Type.RampMetering);
        if (controlScheduleManaged == null) {
            try {
                controlScheduleManaged = ControlFactory.create_empty_controller_schedule(null, "Managed Lane Ramp Metering Scheme", myLink, LaneGroupType.mng, AbstractController.Type.RampMetering);
                if (!controlScheduleManaged.add_link(myLink))
                    throw new Exception("Error adding on-ramp to Managed lane ramp metering scheme.");
            } catch(Exception ex) {
                opt.utils.Dialogs.ExceptionDialog("Error adding ramp metering scheme for Managed lanes", ex);
            }
        }
    }

    /** called onOk and onCancel in NewRampMeterController **/
    public void prepareNewController(control.AbstractController.Algorithm ca, boolean isml) {
        controlEntry = null;
        if (ca == null)
            return;

        read_controlType_and_lanegroup();

        ControlSchedule controlSchedule = controlScheduleGP;
        if (isml)
            controlSchedule = controlScheduleManaged;
        
        float start_time = controlSchedule.num_entries()==0 ? 0 :
                Math.round(controlSchedule.get_largest_start_time()) + 3600;

        float min_rate_vphpl, max_rate_vphpl, dt;
        dt = (float)opt.UserSettings.defaultControlDtSeconds;
        
        min_rate_vphpl = (float)opt.UserSettings.minGPRampMeteringRatePerLaneVph;
        max_rate_vphpl = (float)opt.UserSettings.maxGPRampMeteringRatePerLaneVph;
        if (isml) {
            min_rate_vphpl = (float)opt.UserSettings.minManagedRampMeteringRatePerLaneVph;
            max_rate_vphpl = (float)opt.UserSettings.maxManagedRampMeteringRatePerLaneVph;
        }
        
        try {
            AbstractControllerRampMeter newController;
            switch(ca) {
                case rm_open:
                    newController = ControlFactory.create_controller_rmopen(myLink.get_segment().get_scenario());
                    break;

                case rm_closed:
                    newController = ControlFactory.create_controller_rmclosed(myLink.get_segment().get_scenario());
                    break;

                case rm_alinea:
                    AbstractLink sensor_link = myLink.get_dn_link();
                    newController = ControlFactory.create_controller_alinea(myLink.get_segment().get_scenario(),
                        dt,
                        false,
                        min_rate_vphpl,
                        max_rate_vphpl,
                        null,
                        sensor_link.get_id(),
                        sensor_link.get_length_meters() / 2f);
                    break;

                case rm_fixed_rate:
                    newController = ControlFactory.create_controller_fixed_rate(myLink.get_segment().get_scenario(),
                        dt,
                        false,
                        min_rate_vphpl,
                        max_rate_vphpl,
                        max_rate_vphpl);

                    break;

                default:
                    throw new Exception("Bad controller type");

            }

            controlEntry = new ScheduleEntry(start_time, newController);

        } catch (Exception e) {
            controlEntry = null;
            opt.utils.Dialogs.ExceptionDialog("Could not create new ramp meter...", e);
        }

    }

    
    void launchRampMeterEditor(ScheduleEntry entry, boolean isml, boolean isnew) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        ControlSchedule controlSchedule = controlScheduleGP;
        if (isml)
            controlSchedule = controlScheduleManaged;

        switch(entry.get_cntrl().getAlgorithm()){
            case rm_open:
                inputStage.setScene(rampMeterOpenScene);
                rampMeterOpen.initWithLinkAndController(myLink, controlSchedule, entry, isnew);
                inputStage.setTitle("Open On-Ramp");
                break;

            case rm_closed:
                inputStage.setScene(rampMeterClosedScene);
                rampMeterClosed.initWithLinkAndController(myLink, controlSchedule, entry, isnew);
                inputStage.setTitle("Close On-Ramp");
                break;

            case rm_alinea:
                inputStage.setScene(rampMeterAlineaScene);
                rampMeterAlinea.initWithLinkAndController(myLink, controlSchedule, entry, isml, isnew);
                inputStage.setTitle("Ramp Meter ALINEA");
                break;

            case rm_fixed_rate:
                inputStage.setScene(rampMeterFixedScene);
                rampMeterFixed.initWithLinkAndController(myLink, controlSchedule, entry, isml, isnew);
                inputStage.setTitle("Ramp Meter Fixed Rate");
                break;
                
            default:
                // should not end up here...
                opt.utils.Dialogs.ErrorDialog("Ramp meter '" + entry.get_cntrl().getAlgorithm() + "' is not supported...", "Please, report this problem!");
                return;
        }

        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
        refreshControllerList(isml);
    }
    
    
    @FXML
    void onAddControllerGP(ActionEvent event) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newRampMeterScene);
        newRampMeterController.initWithLink(myLink, false);
        inputStage.setTitle("New Ramp Meter for GP Lanes");
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
        if (controlEntry != null)
            launchRampMeterEditor(controlEntry, false, true);
    }
    
    @FXML
    void onAddControllerManaged(ActionEvent event) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newRampMeterScene);
        newRampMeterController.initWithLink(myLink, true);
        inputStage.setTitle("New Ramp Meter for Managed Lanes");
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
        if (controlEntry != null)
            launchRampMeterEditor(controlEntry, true, true);
    }
    
    
    @FXML
    void onDeleteControllerGP(ActionEvent event) {
        int idx = listControllersGP.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= controlScheduleGP.num_entries()))
            return;

        String header = "You are deleting controller '" + listControllersGP.getItems().get(idx) + "'...";
        if (!opt.utils.Dialogs.ConfirmationYesNoDialog(header, "Are you sure?"))
            return;

        controlScheduleGP.delete_entry(idx);
        refreshControllerList(false);
        appMainController.setProjectModified(true);
    }
    
    
    @FXML
    void onDeleteControllerManaged(ActionEvent event) {
        int idx = listControllersManaged.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= controlScheduleManaged.num_entries()))
            return;

        String header = "You are deleting controller '" + listControllersManaged.getItems().get(idx) + "'...";
        if (!opt.utils.Dialogs.ConfirmationYesNoDialog(header, "Are you sure?"))
            return;

        controlScheduleManaged.delete_entry(idx);
        refreshControllerList(true);
        appMainController.setProjectModified(true);
    }
    
    
    @FXML
    void controllersOnKeyPressedGP(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            int idx = listControllersGP.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= controlScheduleGP.num_entries()))
                return;
            launchRampMeterEditor(controlScheduleGP.get_entries().get(idx), false, false);
        }
        if ((event.getCode() == KeyCode.DELETE) || (event.getCode() == KeyCode.BACK_SPACE)) {
            onDeleteControllerGP(null);
        }
    }
    
    
    @FXML
    void controllersOnKeyPressedManaged(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            int idx = listControllersManaged.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= controlScheduleManaged.num_entries()))
                return;
            launchRampMeterEditor(controlScheduleManaged.get_entries().get(idx), true, false);
        }
        if ((event.getCode() == KeyCode.DELETE) || (event.getCode() == KeyCode.BACK_SPACE)) {
            onDeleteControllerManaged(null);
        }
    }
    
    
    @FXML
    void controllersOnClickGP(MouseEvent event) {
        if (event.getClickCount() == 2) {
            int idx = listControllersGP.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= controlScheduleGP.num_entries()))
                return;
            launchRampMeterEditor(controlScheduleGP.get_entries().get(idx), false, false);
        }
    }
    
    
    @FXML
    void controllersOnClickManaged(MouseEvent event) {
        if (event.getClickCount() == 2) {
            int idx = listControllersManaged.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= controlScheduleManaged.num_entries()))
                return;
            launchRampMeterEditor(controlScheduleManaged.get_entries().get(idx), true, false);
        }
    }
    
    
    
    
    
    
    
    /***************************************************************************
     * Generic callbacks
     ***************************************************************************/
    
    /**
     * Draw the road section according to its type, lane configuration and ramps.
     */
    @FXML
    private void drawRoadSection() {
        int ramp_angle = 45;
        double width = linkEditorCanvas.getWidth();
        double height = linkEditorCanvas.getHeight();
        boolean rightSideRoads = UserSettings.rightSideRoads;
        
        int managed_lanes = numLanesManagedSpinnerValueFactory.getValue();
        int gp_lanes = numLanesGPSpinnerValueFactory.getValue();
        int aux_lanes = numLanesAuxSpinnerValueFactory.getValue();
        int total_lanes = gp_lanes + managed_lanes + aux_lanes;
        

        if (managed_lanes > 0) {
            cbBarrier.setDisable(false);
            capacityManagedLane.setDisable(false);
            ffSpeedManaged.setDisable(false);
            jamDensityManagedLane.setDisable(false);
            if (managed_lanes > 1) {
                cbSeparated.setDisable(false);
            } else {
                cbSeparated.setDisable(true);
            }
            tabManagedLanesControl.setDisable(false);
        } else {
            cbBarrier.setDisable(true);
            cbSeparated.setDisable(true);
            capacityManagedLane.setDisable(true);
            ffSpeedManaged.setDisable(true);
            jamDensityManagedLane.setDisable(true);
            tabManagedLanesControl.setDisable(true);
            controlTabPane.getSelectionModel().selectFirst();
        }
        
        if (aux_lanes > 0) {
            capacityAuxLane.setDisable(false);
            ffSpeedAux.setDisable(false);
            jamDensityAuxLane.setDisable(false);
        } else {
            capacityAuxLane.setDisable(true);
            ffSpeedAux.setDisable(true);
            jamDensityAuxLane.setDisable(true);
        }
        
        boolean barrier = cbBarrier.isSelected();
        boolean separated = cbSeparated.isSelected();
        
        
        // Set lane properties to link
        if (!ignoreChange) {
            try {
                myLink.set_mng_lanes(managed_lanes);
                myLink.set_gp_lanes(gp_lanes);
                if (myLink.get_type() == AbstractLink.Type.freeway)
                    myLink.set_aux_lanes(aux_lanes);
                myLink.set_mng_barrier(barrier);
                myLink.set_mng_separated(separated);
                appMainController.setProjectModified(true);
            } catch(Exception e) {
                opt.utils.Dialogs.ExceptionDialog("Could not change lane configuration...", e);
            }
        }
        
        
        GraphicsContext g = linkEditorCanvas.getGraphicsContext2D();
        //g.setFill(Color.WHITE);
        g.clearRect(0, 0, width, height);

        double lane_length = 2*width/3;
        double lane_width = height/16;
        if (total_lanes > 8) { // we want the road segment to take about half of the canvas 
            lane_width = Math.sqrt(2)*(height/(2 * total_lanes));
        }
        
        double x0 = width/6;
        double x1 = x0 + lane_length;
        
        int total_ramp_lanes = 0;
        int num_ramps = myLink.get_segment().num_out_ors();
        for (int i = 0; i < num_ramps; i++) {
            int l_count = myLink.get_segment().out_ors(i).get_gp_lanes() +
                          myLink.get_segment().out_ors(i).get_mng_lanes();
            if (l_count > total_ramp_lanes)
                total_ramp_lanes = l_count;
        }
        num_ramps = myLink.get_segment().num_in_ors();
        for (int i = 0; i < num_ramps; i++) {
            int l_count = myLink.get_segment().in_ors(i).get_gp_lanes() +
                          myLink.get_segment().in_ors(i).get_mng_lanes();
            if (l_count > total_ramp_lanes)
                total_ramp_lanes = l_count;
        }
        num_ramps = myLink.get_segment().num_out_frs();
        for (int i = 0; i < num_ramps; i++) {
            int l_count = myLink.get_segment().out_frs(i).get_gp_lanes() +
                          myLink.get_segment().out_frs(i).get_mng_lanes();
            if (l_count > total_ramp_lanes)
                total_ramp_lanes = l_count;
        }
        num_ramps = myLink.get_segment().num_in_frs();
        for (int i = 0; i < num_ramps; i++) {
            int l_count = myLink.get_segment().in_frs(i).get_gp_lanes() +
                          myLink.get_segment().in_frs(i).get_mng_lanes();
            if (l_count > total_ramp_lanes)
                total_ramp_lanes = l_count;
        }
        double coeff = Math.min(1.0, (double)total_lanes/(double)total_ramp_lanes);
        
        
        if (rightSideRoads) { // right-side driving road
            double base_y = 0.75*height;
            double base_y_1 = base_y - total_lanes*lane_width;
            double ramp_length = height - base_y;
            double r_lane_width = coeff*lane_width;
            double y1 = base_y;
            double y0 = y1;
            
            if (myLink.get_type() == AbstractLink.Type.freeway) {
                // Draw outer on-ramps
                num_ramps = myLink.get_segment().num_out_ors();
                double delta = 0.0;
                for (int i = 0; i < num_ramps; i++) {
                    int or_gp = myLink.get_segment().out_ors(i).get_gp_lanes();
                    int or_managed = myLink.get_segment().out_ors(i).get_mng_lanes();
                    boolean or_barrier = myLink.get_segment().out_ors(i).get_mng_barrier();
                    boolean or_separated = myLink.get_segment().out_ors(i).get_mng_separated();
                    double or_lanes = or_gp + or_managed;
                    double or_width = or_lanes * r_lane_width;
                    double or_gp_width = or_gp * r_lane_width;
                    double or_managed_width = or_managed * r_lane_width;
                    if (i > 0)
                        delta += 0.5*or_width;
                    double rotationCenterX = x0 + delta;
                    y0 = base_y + 0.5*or_width - 0.5*or_gp_width;
                    double rotationCenterY = base_y;
                    g.setFill(Color.DARKGREY);
                    g.save();
                    g.translate(rotationCenterX, rotationCenterY);
                    g.rotate(-ramp_angle);
                    g.translate(-rotationCenterX, -rotationCenterY);
                    g.fillRect(x0+delta-ramp_length/2, y0-or_gp_width/2, ramp_length, or_gp_width);

                    y0 -= 0.5*or_width;
                    g.setFill(Color.BLACK);
                    g.fillRect(x0+delta-ramp_length/2, y0-or_managed_width/2, ramp_length, or_managed_width);
                    
                    g.setStroke(Color.WHITE);
                    for (int j = 1; j < or_lanes; j++) {
                        y0 = base_y+or_width/2 - j*r_lane_width;
                        if (or_barrier && (j == or_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(2);
                        } else if (or_separated && (j > or_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(1);
                        } else {
                            g.setLineDashes(r_lane_width/3, r_lane_width/2);
                            g.setLineWidth(1);
                        }
                        g.strokeLine(x0+delta-ramp_length/2, y0, x0+delta+ramp_length/2, y0);
                    }
                    g.restore();
                    delta += 0.5*ramp_length + 0.5*or_width;
                }
                
                // Draw inner on-ramps
                num_ramps = myLink.get_segment().num_in_ors();
                delta = 0.0;
                for (int i = 0; i < num_ramps; i++) {
                    int or_gp = myLink.get_segment().in_ors(i).get_gp_lanes();
                    int or_managed = myLink.get_segment().in_ors(i).get_mng_lanes();
                    boolean or_barrier = myLink.get_segment().in_ors(i).get_mng_barrier();
                    boolean or_separated = myLink.get_segment().in_ors(i).get_mng_separated();
                    double or_lanes = or_gp + or_managed;
                    double or_width = or_lanes * r_lane_width;
                    double or_gp_width = or_gp * r_lane_width;
                    double or_managed_width = or_managed * r_lane_width;
                    if (i > 0)
                        delta += 0.5*or_width;
                    
                    double rotationCenterX = x0 + delta;
                    y0 = base_y_1 + 0.5*or_width - 0.5*or_gp_width;
                    double rotationCenterY = base_y_1;
                    g.setFill(Color.DARKGREY);
                    g.save();
                    g.translate(rotationCenterX, rotationCenterY);
                    g.rotate(ramp_angle);
                    g.translate(-rotationCenterX, -rotationCenterY);
                    g.fillRect(x0+delta-ramp_length/2, y0-or_gp_width/2, ramp_length, or_gp_width);

                    y0 -= 0.5*or_width;
                    g.setFill(Color.BLACK);
                    g.fillRect(x0+delta-ramp_length/2, y0-or_managed_width/2, ramp_length, or_managed_width);
                    
                    g.setStroke(Color.WHITE);
                    for (int j = 1; j < or_lanes; j++) {
                        y0 = base_y_1 + or_width/2 - j*r_lane_width;
                        if (or_barrier && (j == or_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(2);
                        } else if (or_separated && (j > or_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(1);
                        } else {
                            g.setLineDashes(r_lane_width/3, r_lane_width/2);
                            g.setLineWidth(1);
                        }
                        g.strokeLine(x0+delta-ramp_length/2, y0, x0+delta+ramp_length/2, y0);
                    }
                    g.restore();
                    delta += 0.5*ramp_length + 0.5*or_width;
                }



                // Draw outer off-ramps
                num_ramps = myLink.get_segment().num_out_frs();
                delta = 0.0;
                for (int i = num_ramps-1; i >= 0; i--) {
                    int fr_gp = myLink.get_segment().out_frs(i).get_gp_lanes();
                    int fr_managed = myLink.get_segment().out_frs(i).get_mng_lanes();
                    boolean fr_barrier = myLink.get_segment().out_frs(i).get_mng_barrier();
                    boolean fr_separated = myLink.get_segment().out_frs(i).get_mng_separated();
                    double fr_lanes = fr_gp + fr_managed;
                    double fr_width = fr_lanes * r_lane_width;
                    double fr_gp_width = fr_gp * r_lane_width;
                    double fr_managed_width = fr_managed * r_lane_width;
                    if (i < num_ramps-1)
                        delta += 0.5*fr_width;
                    double rotationCenterX = x1 - delta;
                    y0 = base_y + 0.5*fr_width - 0.5*fr_gp_width;
                    double rotationCenterY = base_y;
                    g.setFill(Color.DARKGREY);
                    g.save();
                    g.translate(rotationCenterX, rotationCenterY);
                    g.rotate(ramp_angle);
                    g.translate(-rotationCenterX, -rotationCenterY);
                    g.fillRect(x1-delta-ramp_length/2, y0-fr_gp_width/2, ramp_length, fr_gp_width);
                    
                    y0 -= 0.5*fr_width;
                    g.setFill(Color.BLACK);
                    g.fillRect(x1-delta-ramp_length/2, y0-fr_managed_width/2, ramp_length, fr_managed_width);
                    
                    g.setStroke(Color.WHITE);
                    for (int j = 1; j < fr_lanes; j++) {
                        y0 = base_y + fr_width/2 - j*r_lane_width;
                        if (fr_barrier && (j == fr_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(2);
                        } else if (fr_separated && (j > fr_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(1);
                        } else {
                            g.setLineDashes(r_lane_width/3, r_lane_width/2);
                            g.setLineWidth(1);
                        }
                        g.strokeLine(x1-delta-ramp_length/2, y0, x1-delta+ramp_length/2, y0);
                    }
                    g.restore();
                    delta += 0.5*ramp_length + 0.5*fr_width;
                }
                
                // Draw inner off-ramps
                num_ramps = myLink.get_segment().num_in_frs();
                delta = 0.0;
                for (int i = num_ramps-1; i >= 0; i--) {
                    int fr_gp = myLink.get_segment().in_frs(i).get_gp_lanes();
                    int fr_managed = myLink.get_segment().in_frs(i).get_mng_lanes();
                    boolean fr_barrier = myLink.get_segment().in_frs(i).get_mng_barrier();
                    boolean fr_separated = myLink.get_segment().in_frs(i).get_mng_separated();
                    double fr_lanes = fr_gp + fr_managed;
                    double fr_width = fr_lanes * r_lane_width;
                    double fr_gp_width = fr_gp * r_lane_width;
                    double fr_managed_width = fr_managed * r_lane_width;
                    if (i < num_ramps-1)
                        delta += 0.5*fr_width;
                    double rotationCenterX = x1 - delta;
                    y0 = base_y_1 + 0.5*fr_width - 0.5*fr_gp_width;
                    double rotationCenterY = base_y_1;
                    g.setFill(Color.DARKGREY);
                    g.save();
                    g.translate(rotationCenterX, rotationCenterY);
                    g.rotate(-ramp_angle);
                    g.translate(-rotationCenterX, -rotationCenterY);
                    g.fillRect(x1-delta-ramp_length/2, y0-fr_gp_width/2, ramp_length, fr_gp_width);
                    
                    y0 -= 0.5*fr_width;
                    g.setFill(Color.BLACK);
                    g.fillRect(x1-delta-ramp_length/2, y0-fr_managed_width/2, ramp_length, fr_managed_width);
                    
                    g.setStroke(Color.WHITE);
                    for (int j = 1; j < fr_lanes; j++) {
                        y0 = base_y_1 + fr_width/2 - j*r_lane_width;
                        if (fr_barrier && (j == fr_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(2);
                        } else if (fr_separated && (j > fr_gp)) {
                            g.setLineDashes();
                            g.setLineWidth(1);
                        } else {
                            g.setLineDashes(r_lane_width/3, r_lane_width/2);
                            g.setLineWidth(1);
                        }
                        g.strokeLine(x1-delta-ramp_length/2, y0, x1-delta+ramp_length/2, y0);
                    }
                    g.restore();
                    delta += 0.5*ramp_length + 0.5*fr_width;
                }

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
            
            String label = Misc.linkType2String(myLink.get_type());
            g.setStroke(Color.BLACK);
            g.setFill(Color.BLACK);
            g.setLineDashes();
            g.setFont(new Font("Verdana", 18));
            //g.strokeText(label, 0.46*width, 0.2*height);
            g.fillText(label, 0.46*width, 0.2*height);
            
        } else {
            
            
            
            ;//TODO AK left-side driving road
            
            
            
        }
    }

    
    
}
