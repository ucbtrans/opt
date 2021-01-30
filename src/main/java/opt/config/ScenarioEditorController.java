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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import opt.AppMainController;
import opt.UserSettings;
import opt.data.AbstractLink;
import opt.data.Commodity;
import opt.data.control.ControlFactory;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;
import opt.data.LinkConnector;
import opt.data.Segment;
import opt.data.control.AbstractController;
import opt.data.control.ControlSchedule;
import opt.data.control.ControllerLgRestrict;
import opt.data.control.ScheduleEntry;
import opt.data.event.AbstractEvent;
import opt.data.event.AbstractEventLaneGroup;
import opt.data.event.EventLanegroupLanes;
import opt.data.event.EventLanegroupFD;
import opt.data.event.EventLinkToggle;
import opt.utils.Misc;
import opt.utils.ModifiedDoubleStringConverter;


/**
 * Scenario Editor UI control.
 * Scenario Editor is located in Configuration tab of the Action Pane when a scenario
 * is selected in the navigation tree.
 * 
 * @author Alex Kurzhanskiy
 */
public class ScenarioEditorController {
    private Stage primaryStage = null;
    private VehicleTypeController vehicleTypeController = null;
    private Scene vehicleTypeScene = null;
    
    private AppMainController appMainController = null;
    private FreewayScenario myScenario = null;
    private boolean ignoreChange = true;
    
    private String origScenarioName = null;
    private String origScenarioDescription = null;
    private List<Commodity> listVT = new ArrayList<Commodity>();
    
    private List<ControlSchedule> listLanePolicies = null;
    private ControlSchedule selectedPolicy = null;
    private int selectedPolicyIndex = -1;
    private Set<AbstractLink> linksUnderPolicy = new HashSet<AbstractLink>();
    private List<AbstractLink> linksFreeForPolicy = new ArrayList<AbstractLink>();
    
    private List<ScheduleEntry> policyEntries = null;
    
    private AbstractEvent selectedEvent = null;
    
    private LaneControlEditorController laneControlEditorController = null;
    private Scene laneControlEditorScene = null;
    
    private NewEventController newEventController = null;
    private Scene newEventScene = null;
    
    private EventFD eventFD = null;
    private Scene eventFDScene = null;
    
    private EventLanes eventLanes = null;
    private Scene eventLanesScene = null;
    
    private EventLinkOC eventLinkOC = null;
    private Scene eventLinkOCScene = null;
    
    private SpinnerValueFactory<Double> a0SpinnerValueFactory = null;
    private SpinnerValueFactory<Double> a1SpinnerValueFactory = null;
    
    private NetworkDisplay networkDisplay = null;
    
    private Tooltip ttCanvas = new Tooltip();
    
    
    @FXML // fx:id="scenarioEditorMainPane"
    private SplitPane scenarioEditorMainPane; // Value injected by FXMLLoader

    @FXML // fx:id="canvasParent"
    private AnchorPane canvasParent; // Value injected by FXMLLoader

    @FXML // fx:id="scenarioEditorCanvas"
    private Canvas scenarioEditorCanvas; // Value injected by FXMLLoader

    @FXML // fx:id="scenarioName"
    private TextField scenarioName; // Value injected by FXMLLoader

    @FXML // fx:id="scenarioEditorAccordionParent"
    private AnchorPane scenarioEditorAccordionParent; // Value injected by FXMLLoader

    @FXML // fx:id="scenarioEditorAccordion"
    private Accordion scenarioEditorAccordion; // Value injected by FXMLLoader

    @FXML // fx:id="vehicleTypesPane"
    private TitledPane vehicleTypesPane; // Value injected by FXMLLoader

    @FXML // fx:id="listVehicleTypes"
    private ListView<String> listVehicleTypes; // Value injected by FXMLLoader

    @FXML // fx:id="deleteVehicleType"
    private Button deleteVehicleType; // Value injected by FXMLLoader

    @FXML // fx:id="newVehicleType"
    private Button newVehicleType; // Value injected by FXMLLoader

    @FXML // fx:id="timePane"
    private TitledPane timePane; // Value injected by FXMLLoader
    
    @FXML // fx:id="startTime"
    private TextField startTime; // Value injected by FXMLLoader

    @FXML // fx:id="sDuration"
    private TextField sDuration; // Value injected by FXMLLoader
    
    @FXML // fx:id="sDescription"
    private TextArea sDescription; // Value injected by FXMLLoader
    
    @FXML // fx:id="spA0"
    private Spinner<Double> spA0; // Value injected by FXMLLoader

    @FXML // fx:id="labelA1"
    private Label labelA1; // Value injected by FXMLLoader
    
    @FXML // fx:id="spA1"
    private Spinner<Double> spA1; // Value injected by FXMLLoader
    
    @FXML // fx:id="controllerPane"
    private TitledPane controllerPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="cbPolicies"
    private ComboBox<String> cbPolicies; // Value injected by FXMLLoader

    @FXML // fx:id="addPolicy"
    private Button addPolicy; // Value injected by FXMLLoader

    @FXML // fx:id="deletePolicy"
    private Button deletePolicy; // Value injected by FXMLLoader

    @FXML // fx:id="policyPane"
    private TabPane policyPane; // Value injected by FXMLLoader

    @FXML // fx:id="tabSchedule"
    private AnchorPane tabSchedule; // Value injected by FXMLLoader

    @FXML // fx:id="listControllers"
    private ListView<String> listControllers; // Value injected by FXMLLoader

    @FXML // fx:id="deleteController"
    private Button deleteController; // Value injected by FXMLLoader

    @FXML // fx:id="addController"
    private Button addController; // Value injected by FXMLLoader

    @FXML // fx:id="tabLinks"
    private Tab tabLinks; // Value injected by FXMLLoader

    @FXML // fx:id="freeLinks"
    private ListView<String> freeLinks; // Value injected by FXMLLoader

    @FXML // fx:id="addToPolicy"
    private Button addToPolicy; // Value injected by FXMLLoader

    @FXML // fx:id="policyLinks"
    private ListView<String> policyLinks; // Value injected by FXMLLoader

    @FXML // fx:id="removeFromPolicy"
    private Button removeFromPolicy; // Value injected by FXMLLoader

    @FXML // fx:id="eventPane"
    private TitledPane eventPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="listEvents"
    private ListView<String> listEvents; // Value injected by FXMLLoader

    @FXML // fx:id="onDeleteEvent"
    private Button onDeleteEvent; // Value injected by FXMLLoader

    @FXML // fx:id="onAddEvent"
    private Button onAddEvent; // Value injected by FXMLLoader

   
    
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
    
    public void setProjectModified(boolean val) {
        appMainController.setProjectModified(val);
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the new ramp controller that is used to set up
     *               new on- and off-ramps.
     */
    public void setVehicleTypeControllerAndScene(VehicleTypeController ctrl, Scene scn) {
        vehicleTypeController = ctrl;
        vehicleTypeScene = scn;
        vehicleTypeScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the lane policy editor controller that is used to
     *               edit lane control.
     */
    public void setLaneControlEditorControllerAndScene(LaneControlEditorController ctrl, Scene scn) {
        laneControlEditorController = ctrl;
        laneControlEditorScene = scn;
        laneControlEditorScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the new event controller that is used to
     *               create a event.
     */
    public void setNewEventControllerAndScene(NewEventController ctrl, Scene scn) {
        newEventController = ctrl;
        newEventScene = scn;
        newEventScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the EventFD that is used to
     *               edit a fundamental diagram event.
     */
    public void setEventFDControllerAndScene(EventFD ctrl, Scene scn) {
        eventFD = ctrl;
        eventFDScene = scn;
        //eventFDScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the EventLanes that is used to
     *               edit a lane number change event.
     */
    public void setEventLanesControllerAndScene(EventLanes ctrl, Scene scn) {
        eventLanes = ctrl;
        eventLanesScene = scn;
        //eventLanesScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the EventLinkOC that is used to
     *               edit a off-ramp open/close event.
     */
    public void setEventLinkOCControllerAndScene(EventLinkOC ctrl, Scene scn) {
        eventLinkOC = ctrl;
        eventLinkOCScene = scn;
        //eventLinkToggleScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    
    
    private void launchVehicleTypeWindow(Commodity comm) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(vehicleTypeScene);
        vehicleTypeController.initWithCommodityAndScenario(comm, myScenario);
        String title = "New Vehicle Type";
        if (comm != null)
            title = "Vehicle Type Editor";
        inputStage.setTitle(title);
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
        
        ignoreChange = true;
        makeListVT(myScenario.get_commodities());
        ignoreChange = false;
    }
    
    
    private void launchLaneControlEditor(ScheduleEntry entry, boolean isnew) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(laneControlEditorScene);
        laneControlEditorController.initWithScenarioAndLanePolicyData(myScenario, selectedPolicy, entry, isnew);
        String title = "New Lane Control Entry";
        if (!isnew)
            title = "Lane Control Editor";
        inputStage.setTitle(title);
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
        fillLanePolicySchedule();
    }
    
    
    private void launchEventEditor(AbstractEvent event) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        String title = "Event: Change Number of Lanes";
        
        if (event instanceof EventLanegroupFD) {
            title = "Event: Change Traffic Dynamics";
            inputStage.setScene(eventFDScene);
            eventFD.initWithScenarioAndEvent(myScenario, (EventLanegroupFD)event);
        } else if (event instanceof EventLanegroupLanes) {
            inputStage.setScene(eventLanesScene);
            eventLanes.initWithScenarioAndEvent(myScenario, (EventLanegroupLanes)event);
        } else if (event instanceof EventLinkToggle) {
            title = "Event: Open/Close Off-Ramps";
            inputStage.setScene(eventLinkOCScene);
            eventLinkOC.initWithScenarioAndEvent(myScenario, (EventLinkToggle)event);
        } else {
            opt.utils.Dialogs.ErrorDialog("Unknown Event Type!", "Not implemented");
            return;
        }
        
        inputStage.setTitle(title);
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
        
        populateEventList();
    }
    
    
    
    
    private void makeListVT(Map<Long, Commodity> mapVT) {
        listVT.clear();
        listVehicleTypes.getItems().clear();

        mapVT.forEach((k, v) -> {
            DecimalFormat df = new DecimalFormat("#.#");
            Commodity.EmissionsClass ec = v.get_eclass();
            String s = v.get_name() + "   (Cal-BC Emissions Class: " + ec + ")";
            listVT.add(v);
            listVehicleTypes.getItems().add(s);
        });
    }
    
    
    
    

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        scenarioName.textProperty().addListener((observable, oldValue, newValue) -> {
            onScenarioNameChange(null);
        });
        
        sDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            onScenarioDescriptionChange(null);
        });
        
        startTime.setTextFormatter(opt.utils.TextFormatting.createTimeTextFormatter(Misc.seconds2timestring((float)opt.UserSettings.defaultStartTime, "")));
        startTime.textProperty().addListener((observable, oldValue, newValue) -> {
            onStartTimeChange(null);
        });
        
        sDuration.setTextFormatter(opt.utils.TextFormatting.createTimeTextFormatter(Misc.seconds2timestring((float)opt.UserSettings.defaultSimulationDuration, "")));
        sDuration.textProperty().addListener((observable, oldValue, newValue) -> {
            onDurationChange(null);
        });
        
        a0SpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE, Double.MAX_VALUE, 1.0, 1);
        a0SpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter("#.####", 1.0));
        spA0.setValueFactory(a0SpinnerValueFactory);
        spA0.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onLCParamChange();
        });
        spA0.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double a0 = myScenario.get_lc_keep();
            if (a0 == null)
                a0 = UserSettings.defaultLaneChoice_keep;
            opt.utils.WidgetFunctionality.commitEditorText(spA0, a0);
        });
        
        a1SpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE, Double.MAX_VALUE, 1.0, 1);
        a1SpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter("#.####", 1.0));
        spA1.setValueFactory(a1SpinnerValueFactory);
        spA1.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChange && (oldValue != newValue))
                onLCParamChange();
        });
        spA1.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            double cc = 1.0 / UserSettings.densityConversionMap.get("vpm"+UserSettings.unitsDensity);
            Double a1 = myScenario.get_lc_density_vpmilepl();
            if (a1 == null)
                a1 = UserSettings.defaultLaneChoice_rhovpmplane;
            opt.utils.WidgetFunctionality.commitEditorText(spA1, cc * a1);
        });
        
        cbPolicies.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            onPolicyNameChange(oldValue, newValue);
        });
        
        freeLinks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        policyLinks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        scenarioEditorCanvas.widthProperty().bind(canvasParent.widthProperty());
        scenarioEditorCanvas.heightProperty().bind(canvasParent.heightProperty());

        scenarioEditorCanvas.widthProperty().addListener((observable, oldValue, newValue) -> {
           if ((ignoreChange) || (networkDisplay == null))
               return;
           networkDisplay.execute();
        });
        
        scenarioEditorCanvas.heightProperty().addListener((observable, oldValue, newValue) -> {
           if ((ignoreChange) || (networkDisplay == null))
               return;
           networkDisplay.execute();
        });

        Tooltip.install(scenarioEditorCanvas, ttCanvas);
    }
    
     
    /**
     * This function is called every time one opens a scenario in the
     * configuration module.
     * @param s - Simulation scenario. 
     */
    public void initWithScenarioData(FreewayScenario s) {
        ignoreChange = true;
        
        scenarioName.setText(s.name);
        origScenarioName = s.name;
        myScenario = (FreewayScenario)s;
        
        makeListVT(myScenario.get_commodities());
        initScenarioTiming();
        initLanePolicies();
        
        populateEventList();
        
        networkDisplay = new NetworkDisplay(scenarioEditorCanvas, ttCanvas, myScenario);
        networkDisplay.execute();
        
        ignoreChange = false;
    }
    
    
    private void initScenarioTiming() {
        float st = myScenario.get_start_time();
        if (st == Float.NaN)
            st = (float)opt.UserSettings.defaultStartTime;
        
        float duration = myScenario.get_sim_duration();
        if (duration == Float.NaN)
            duration = (float)opt.UserSettings.defaultSimulationDuration;
        
        startTime.setText(Misc.seconds2timestring(st, ""));
        sDuration.setText(Misc.seconds2timestring(duration, ""));
        
        origScenarioDescription = myScenario.description;
        sDescription.setText(myScenario.description);
        
        Double a0 = myScenario.get_lc_keep();
        if (a0 == null)
            a0 = UserSettings.defaultLaneChoice_keep;
        spA0.getValueFactory().setValue(a0);
        
        String units = UserSettings.unitsDensity;
        labelA1.setText("Lane Choice Model Traffic Density Influencer: (1/" + units + "):");
        
        double cc = 1.0 / UserSettings.densityConversionMap.get("vpm"+UserSettings.unitsDensity);
        Double a1 = myScenario.get_lc_density_vpmilepl();
        if (a1 == null)
            a1 = UserSettings.defaultLaneChoice_rhovpmplane;
        spA1.getValueFactory().setValue(cc * a1);
    }
    
    private void initLanePolicies() {
        policyPane.setVisible(false);
        deletePolicy.setDisable(true);
        
        populateUnderPolicyLinkList();
        populatePolicyList();
        populateFreeForPolicyLinkList();
        
        if (selectedPolicyIndex < 0) {
            selectedPolicy = null;
            cbPolicies.getSelectionModel().clearSelection();
            return;
        }
        
        fillLanePolicyTabs();
                
        //cbPolicies.getSelectionModel().select(selectedPolicyIndex);
        policyPane.setVisible(true);
        deletePolicy.setDisable(false);
    }
    
    private void populatePolicyList() {
        cbPolicies.getItems().clear();
        
        int sz = listLanePolicies.size();
        selectedPolicyIndex = -1;
        for (int i = 0; i < sz; i++) {
            cbPolicies.getItems().add(listLanePolicies.get(i).get_name());
            if (listLanePolicies.get(i).equals(selectedPolicy)) {
                selectedPolicyIndex = i;
            }
        }
        
        if ((listLanePolicies.size() > 0) && (selectedPolicyIndex < 0))
            selectedPolicyIndex = 0;
        
        if ((selectedPolicyIndex >= 0) && (selectedPolicyIndex < listLanePolicies.size())) {
            selectedPolicy = listLanePolicies.get(selectedPolicyIndex);
            cbPolicies.getSelectionModel().select(selectedPolicyIndex);
        }
    }
    
    private void populateUnderPolicyLinkList() {
        linksUnderPolicy.clear();
        listLanePolicies = myScenario.get_schedules_for_controltype(AbstractController.Type.LgRestrict);
        
        for (ControlSchedule p : listLanePolicies)
            linksUnderPolicy.addAll(p.get_links());
    }
    
    private void populateFreeForPolicyLinkList() {
        linksFreeForPolicy.clear();
        
        List<List<Segment>> seg_list = myScenario.get_linear_freeway_segments();
        for (List<Segment> segments : seg_list)
            for(Segment segment : segments)
                for (AbstractLink link : segment.get_links())
                    if ((link.get_mng_lanes() > 0) && (!linksUnderPolicy.contains(link)))
                        linksFreeForPolicy.add(link);
        
        List<LinkConnector> connectors = myScenario.get_connectors();
        for (AbstractLink link : connectors)
            if ((link.get_mng_lanes() > 0) && (!linksUnderPolicy.contains(link)))
                linksFreeForPolicy.add(link);
    }
    
    
    private String generateRestrictedEntryDesc(ControllerLgRestrict ctrl) {
        String buf = "";
        List<Commodity> t_free = new ArrayList<Commodity>();
        List<Commodity> t_banned = new ArrayList<Commodity>();
        List<Commodity> t_tolled = new ArrayList<Commodity>();

        for (Commodity c : listVT) {
            switch(ctrl.get_comm_permission(c.getId())){
                case Free:
                    t_free.add(c);
                    break;
                case Banned:
                    t_banned.add(c);
                    break;
                case Tolled:
                    t_tolled.add(c);
                    break;
                default:
                    t_free.add(c);
                    break;
            }
        }
        
        int sz = t_free.size();
        if (sz > 0) {
            buf += "Free: ";
            for (int i = 0; i < sz; i++) {
                if (i > 0)
                    buf += ", ";
                buf += t_free.get(i).get_name();
            }
        }
        
        sz = t_banned.size();
        if (sz > 0) {
            if (t_free.size() > 0)
                buf += "; ";
            buf += "Banned: ";
            for (int i = 0; i < sz; i++) {
                if (i > 0)
                    buf += ", ";
                buf += t_banned.get(i).get_name();
            }
        }
        
        sz = t_tolled.size();
        if (sz > 0) {
            if ((t_free.size() > 0) || (t_banned.size() > 0))
                buf += "; ";
            buf += "Tolled: ";
            for (int i = 0; i < sz; i++) {
                if (i > 0)
                    buf += ", ";
                buf += t_tolled.get(i).get_name();
            }
        }
        
        return buf;
    }
    
    private void fillLanePolicySchedule() {
        listControllers.getItems().clear();
        policyEntries = selectedPolicy.get_entries();
        
        for (ScheduleEntry se : policyEntries) {
            float start = se.get_start_time();
            float end = se.get_end_time();
            ControllerLgRestrict ctrl = (ControllerLgRestrict)se.get_cntrl();
            if (ctrl == null)
                continue;
            
            String entry = Misc.seconds2timestring(start, ":") + " - ";
            if (Float.isFinite(end))
                entry += Misc.seconds2timestring(end, ":");
            else
                entry += "End";
            entry += ": ";

            switch(ctrl.get_global_permission()){
                case Free:
                    entry += "Open for all";
                    break;
                case Banned:
                    entry += "Closed for all";
                    break;
                case Tolled:
                    entry += "Restricted - " + generateRestrictedEntryDesc(ctrl);
                    break;
            }

            listControllers.getItems().add(entry);
        }
        
        
    }
    
    private void fillLanePolicyLinks() {
        freeLinks.getItems().clear();
        policyLinks.getItems().clear();
        
        for (AbstractLink link : linksFreeForPolicy)
            freeLinks.getItems().add(link.get_name());
        
        addToPolicy.setDisable(false);
        if (linksFreeForPolicy.size() < 1)
            addToPolicy.setDisable(true);
        
        List<AbstractLink> pll = selectedPolicy.get_ordered_links();
        for (AbstractLink link : pll)
            policyLinks.getItems().add(link.get_name());
        
        removeFromPolicy.setDisable(false);
        if (pll.size() < 2)
            removeFromPolicy.setDisable(true);
    }

    
    private void fillLanePolicyTabs() {
        if (selectedPolicy == null)
            return;
        
        fillLanePolicySchedule();
        fillLanePolicyLinks();
    }
    
    
    
    private void populateEventList() {
        listEvents.getItems().clear();
        
        for (AbstractEvent ev : myScenario.get_events()) {
            String entry = Misc.seconds2timestring(ev.timestamp, ":") + ": " + ev.name + " (";
            
            if (ev instanceof EventLanegroupFD) {
                entry += "Change Traffic Dynamics";
            }
            else if (ev instanceof EventLanegroupLanes)
                entry += "Change Number of Lanes";
            else if (ev instanceof EventLinkToggle)
                entry += "Open / Close Off-Ramps";
            else
                continue;
            
            String lg = "";
            if ((ev instanceof EventLanegroupFD) || (ev instanceof EventLanegroupLanes)) {
                LaneGroupType lgt = ((AbstractEventLaneGroup)ev).get_lgtype();
                lg = " - GP Lanes";
                if (lgt == LaneGroupType.mng)
                    lg = " - Managed Lanes";
                else if (lgt == LaneGroupType.aux)
                    lg = " - Auxiliary lanes";
            }
            entry += lg + ")";
            
            listEvents.getItems().add(entry);
        }
        
    }
    
    
    
    
    
    /************************************************************
     * CALLBACKS
     ************************************************************/
    
    @FXML
    private void onScenarioNameChange(ActionEvent event) {
        if (ignoreChange)
            return;
        
        String nm = scenarioName.getText();
        if (!nm.equals(""))
            appMainController.changeScenarioName(myScenario, nm);
    }
    
    
    
    
    private void onScenarioDescriptionChange(ActionEvent event) {
        if (ignoreChange)
            return;
        
        if (!myScenario.description.equals(sDescription.getText())) {
            myScenario.description = sDescription.getText();
            appMainController.setProjectModified(true);
        }
    }
    
    
    
    
    @FXML
    void vehicleTypesOnClick(MouseEvent event) {
        if (ignoreChange)
            return;
        
        if (event.getClickCount() == 2) {
            int idx = listVehicleTypes.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= listVT.size()))
                return;
           launchVehicleTypeWindow(listVT.get(idx));
        }
    }
    
    @FXML
    private void vehicleTypesKeyPressed(KeyEvent event) {
        if (ignoreChange)
            return;
        
        if (event.getCode() == KeyCode.ENTER) {
            int idx = listVehicleTypes.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= listVT.size()))
                return;
            launchVehicleTypeWindow(listVT.get(idx));
        }
        if ((event.getCode() == KeyCode.DELETE) || (event.getCode() == KeyCode.BACK_SPACE)) {
            onDeleteVehicleType(null);
        }
    }
    
    
     @FXML
    private void onNewVehicleType(ActionEvent event) {
        if (ignoreChange)
            return;
        
        launchVehicleTypeWindow(null);
        
        initLanePolicies();
    }

    @FXML
    private void onDeleteVehicleType(ActionEvent event) {
        if (ignoreChange)
            return;
        
        int idx = listVehicleTypes.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= listVT.size()))
            return;
        
        if (listVT.size() < 2) {
            String header = "Cannot delete vehicle type '" + listVT.get(idx).get_name() + "'";
            String content = "At least one vehicle type must be present!";
            opt.utils.Dialogs.ErrorDialog(header, content);
            return;
        }
        
        String header = "You are deleting vehicle type '" + listVT.get(idx).get_name() + "'...";       
        if (!opt.utils.Dialogs.ConfirmationYesNoDialog(header, "Are you sure?")) 
            return;
        
        myScenario.delete_commodity_with_name(listVT.get(idx).get_name());
        makeListVT(myScenario.get_commodities());
        
        initLanePolicies();
    }
    
    
    
    
    
    @FXML
    private void onStartTimeChange(ActionEvent event) {
        if (ignoreChange)
            return;
    
        String buf = startTime.getText();
        if (buf.indexOf(':') == -1)
            return;

        int seconds = Misc.timeString2Seconds(buf);
        myScenario.set_start_time(seconds);
        appMainController.setProjectModified(true);
    }
    
    
    @FXML
    private void onDurationChange(ActionEvent event) {
        if (ignoreChange)
            return;
    
        String buf = sDuration.getText();
        if (buf.indexOf(':') == -1)
            return;

        int seconds = Misc.timeString2Seconds(buf);
        myScenario.set_sim_duration(seconds);
        appMainController.setProjectModified(true);
    }
    
    
    private void onLCParamChange() {  
        if (ignoreChange)
            return;
        
        double cc = 1.0 / UserSettings.densityConversionMap.get(UserSettings.unitsDensity+"vpm");
        
        double a0 = a0SpinnerValueFactory.getValue();
        double a1 = cc * a1SpinnerValueFactory.getValue();
        
        myScenario.set_lc_keep(a0);
        myScenario.set_lc_density_vpmilepl(a1);
         
        appMainController.setProjectModified(true);
    }
    

    @FXML
    void onPolicySelection(ActionEvent event) {
        selectedPolicyIndex = cbPolicies.getSelectionModel().getSelectedIndex();
        //if (listLanePolicies.size() > 0)
          //  selectedPolicyIndex = Math.max(0, selectedPolicyIndex);
        if (ignoreChange || (selectedPolicyIndex < 0) || (selectedPolicyIndex >= listLanePolicies.size())) {
            policyPane.setVisible(false);
            deletePolicy.setDisable(true);
            return;
        }
        
        selectedPolicy = listLanePolicies.get(selectedPolicyIndex);
        fillLanePolicyTabs();
        policyPane.setVisible(true);
        deletePolicy.setDisable(false);
    }
    
    private void onPolicyNameChange(String old_nm, String nm) {
        if (ignoreChange)
            return;
        
        if (selectedPolicyIndex != cbPolicies.getSelectionModel().getSelectedIndex()) {
            selectedPolicyIndex = cbPolicies.getSelectionModel().getSelectedIndex();
            return;
        }
        
        if (selectedPolicy == null)
            return;
        
        if ((selectedPolicyIndex < 0) || (selectedPolicyIndex >= listLanePolicies.size())) {
            selectedPolicyIndex = cbPolicies.getSelectionModel().getSelectedIndex();
            if (listLanePolicies.size() == 1) {
                selectedPolicyIndex = 0;
                selectedPolicy = listLanePolicies.get(selectedPolicyIndex);
            } else
                return;
        }
        
        if ((!nm.equals("")) && (!nm.equals(selectedPolicy.get_name()))) {
            selectedPolicy.set_name(nm);
            cbPolicies.getItems().set(selectedPolicyIndex, nm);
            appMainController.setProjectModified(true);
        }
    }
    

    @FXML
    void onAddPolicy(ActionEvent event) {
        if (ignoreChange)
            return;
        
        if (linksFreeForPolicy.size() < 1) {
            opt.utils.Dialogs.ErrorDialog("Cannot add new lane policy...",
                                          "No road sections with managed lanes are available!");
            return;
        }
        
        int cnt = listLanePolicies.size() + 1;
        String name = "Lane Policy " + cnt;
        boolean exists = true;
        while (exists) {
            exists = false;
            for (ControlSchedule cs : listLanePolicies)
                if (name.equals(cs.get_name())) {
                    cnt++;
                    name = "Lane Policy " + cnt;
                    exists = true;
                    break;
                }
        }
        
        Set<AbstractLink> links = new HashSet<AbstractLink>();
        links.addAll(linksFreeForPolicy);
        try {
            selectedPolicy = ControlFactory.create_empty_controller_schedule(null, name, links, LaneGroupType.mng, AbstractController.Type.LgRestrict);

            // add all links
            if(!selectedPolicy.add_links(links))
                throw new Exception("Error adding links.");

        } catch(Exception ex) {
            opt.utils.Dialogs.ExceptionDialog("Error adding new lane policy", ex);
        }

        initLanePolicies();
        appMainController.setProjectModified(true);
    }
    
    @FXML
    void onDeletePolicy(ActionEvent event) {
        if (ignoreChange || (selectedPolicy == null) || (selectedPolicyIndex < 0))
            return;
        
        String header = "You are deleting lane policy '" + selectedPolicy.get_name() + "'...";
        if (opt.utils.Dialogs.ConfirmationYesNoDialog(header, "Are you sure?")) {
            myScenario.delete_schedule(selectedPolicy);
            selectedPolicy = null;
            initLanePolicies();
            appMainController.setProjectModified(true);
        }
    }

    @FXML
    void onAddToPolicy(ActionEvent event) {
        if (ignoreChange || (selectedPolicy == null))
            return;
        
        List<Integer> selected = freeLinks.getSelectionModel().getSelectedIndices();
        if ((selected == null) || (selected.size() < 1))
            return;
        
        for (int i : selected) {
            if ((i < 0) || (i >= linksFreeForPolicy.size()))
                continue;
            selectedPolicy.add_link(linksFreeForPolicy.get(i));
            linksUnderPolicy.add(linksFreeForPolicy.get(i));
        }
        
        populateFreeForPolicyLinkList();
        fillLanePolicyLinks();
        appMainController.setProjectModified(true);
    }
    
    @FXML
    void onRemoveFromPolicy(ActionEvent event) {
        if (ignoreChange || (selectedPolicy == null))
            return;
        
        List<Integer> selected = policyLinks.getSelectionModel().getSelectedIndices();
        if ((selected == null) || (selected.size() < 1))
            return;
        
        if (selected.size() == policyLinks.getItems().size()) {
            policyLinks.getSelectionModel().clearSelection(0);
            opt.utils.Dialogs.ErrorDialog("Cannot remove all road sections from policy...",
                                          "At least one road section must remain!");
            return;
        }
        
        List<AbstractLink> pll = selectedPolicy.get_ordered_links();
        for (int i : selected) {
            if ((i < 0) || (i >= pll.size()))
                continue;
            selectedPolicy.remove_link(pll.get(i));
            linksUnderPolicy.remove(pll.get(i));
        }
        
        populateFreeForPolicyLinkList();
        fillLanePolicyLinks();
        appMainController.setProjectModified(true);
    }

    @FXML
    void onAddController(ActionEvent event) {
        float start_time = selectedPolicy.num_entries() == 0 ? 0 :
                Math.round(selectedPolicy.get_largest_start_time()) + 3600;
        
//        double a0 = UserSettings.defaultLaneChoice_keep;      // removed by GG
//        double a1 = UserSettings.defaultLaneChoice_rhovpmplane;      // removed by GG
        double a2 = UserSettings.defaultLaneChoice_tollcents;
        float dt = (float) UserSettings.defaultControlDtSeconds;
        double v_thres = UserSettings.defaultQosSpeedThresholdKph;
        Set<Long> free_comms = new HashSet<Long>();
        listVT.forEach((c) -> { free_comms.add(c.getId()); });
        int[][] vphpl_to_cents_table = new int[1][2];
        vphpl_to_cents_table[0][0] = 0;
        vphpl_to_cents_table[0][1] = 0;
        try {
            ControllerLgRestrict ctrl = ControlFactory.create_controller_hovhot(myScenario, null,null, free_comms, dt, a2, vphpl_to_cents_table, v_thres);
            ScheduleEntry entry = new ScheduleEntry(start_time, ctrl);
            launchLaneControlEditor(entry, true);
        } catch(Exception ex) {
            opt.utils.Dialogs.ExceptionDialog("Error adding new lane controller", ex);
        }
    }
    
    @FXML
    void onDeleteController(ActionEvent event) {
        if ((ignoreChange) || (selectedPolicy == null))
            return;
        
        int se_idx = listControllers.getSelectionModel().getSelectedIndex();
        if ((se_idx < 0) || (se_idx >= selectedPolicy.num_entries()))
            return;
        
        String header = "You are deleting entry '" + listControllers.getItems().get(se_idx) + "'...";
        if (opt.utils.Dialogs.ConfirmationYesNoDialog(header, "Are you sure?")) {
            selectedPolicy.delete_entry(se_idx);
            this.fillLanePolicySchedule();
            appMainController.setProjectModified(true);
        }
    }
    
    @FXML
    void controllersOnClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            int idx = listControllers.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= selectedPolicy.num_entries()))
                return;
            launchLaneControlEditor(selectedPolicy.get_entries().get(idx), false);
        }
    }

    
    @FXML
    void controllersOnKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            int idx = listControllers.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= selectedPolicy.num_entries()))
                return;
            launchLaneControlEditor(selectedPolicy.get_entries().get(idx), false);
        }
        if ((event.getCode() == KeyCode.DELETE) || (event.getCode() == KeyCode.BACK_SPACE)) {
            onDeleteController(null);
        }
    }
    
    
    @FXML
    void canvasOnMouseMoved(MouseEvent event) {
        if (ignoreChange)
            return;
        
        networkDisplay.canvasOnMouseMoved(event);
    }
    
    @FXML
    void canvasOnMouseClicked(MouseEvent event) {
        if (ignoreChange)
            return;
        
        Segment s = networkDisplay.canvasOnMouseClicked(event);
        if (s == null)
            return;
        
        appMainController.selectLink(s.fwy());
    }
    
    
    @FXML
    void canvasOnKeyPressed(KeyEvent event) {
        if (ignoreChange)
            return;
        
        Segment s = networkDisplay.canvasOnKeyPressed(event);
        scenarioEditorCanvas.requestFocus();
        
        if (s == null)
            return;
        
        appMainController.selectLink(s.fwy());
    }
    
    
    
    @FXML
    void freeLinksOnMouseClicked(MouseEvent event) {
        if (ignoreChange)
            return;
        
        int idx = freeLinks.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= linksFreeForPolicy.size()))
            return;
        
        AbstractLink lnk = linksFreeForPolicy.get(idx);
        
        networkDisplay.findLink(lnk);
        freeLinks.requestFocus();
        
        if (event.getClickCount() == 2) {
            appMainController.selectLink(lnk);
        }
    }
    
    @FXML
    void freeLinksOnKeyPressed(KeyEvent event) {
        if (ignoreChange)
            return;
        
        event.consume();
        
        int idx = freeLinks.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= linksFreeForPolicy.size()))
            return;
            
        if (event.getCode() == KeyCode.ENTER) {
            appMainController.selectLink(linksFreeForPolicy.get(idx));
            return;
        }
        
        if ((event.getCode() == KeyCode.LEFT) ||
            (event.getCode() == KeyCode.UP) ||
            (event.getCode() == KeyCode.RIGHT) ||
            (event.getCode() == KeyCode.DOWN)) {
            if (event.getCode() == KeyCode.LEFT)
                idx--;
            if (event.getCode() == KeyCode.RIGHT)
                idx++;
            
            if ((idx < 0) || (idx >= linksFreeForPolicy.size()))
                freeLinks.getSelectionModel().clearSelection();
            else {
                freeLinks.getSelectionModel().select(idx);
                networkDisplay.findLink(linksFreeForPolicy.get(idx));
            }
            freeLinks.requestFocus();
        }
    }
    
    
    @FXML
    void policyLinksOnMouseClicked(MouseEvent event) {
        if (ignoreChange)
            return;
        
        List<AbstractLink> pll = selectedPolicy.get_ordered_links();
        
        int idx = policyLinks.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= pll.size()))
            return;
        
        AbstractLink lnk = pll.get(idx);
        
        networkDisplay.findLink(lnk);
        policyLinks.requestFocus();
        
        if (event.getClickCount() == 2) {
            appMainController.selectLink(lnk);
        }
    }
    
    @FXML
    void policyLinksOnKeyPressed(KeyEvent event) {
        if (ignoreChange)
            return;
        
        event.consume();
        
        List<AbstractLink> pll = selectedPolicy.get_ordered_links();
        
        int idx = policyLinks.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= pll.size()))
            return;
            
        if (event.getCode() == KeyCode.ENTER) {
            appMainController.selectLink(pll.get(idx));
            return;
        }
        
        if ((event.getCode() == KeyCode.LEFT) ||
            (event.getCode() == KeyCode.UP) ||
            (event.getCode() == KeyCode.RIGHT) ||
            (event.getCode() == KeyCode.DOWN)) {
            if (event.getCode() == KeyCode.LEFT)
                idx--;
            if (event.getCode() == KeyCode.RIGHT)
                idx++;
            
            if ((idx < 0) || (idx >= pll.size()))
                policyLinks.getSelectionModel().clearSelection();
            else {
                policyLinks.getSelectionModel().select(idx);
                networkDisplay.findLink(pll.get(idx));
            }
            policyLinks.requestFocus();
        }
    }
    
    
    
    
    @FXML
    void onAddEvent(ActionEvent event) {
        if (ignoreChange)
            return;
        
        selectedEvent = null;
        
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newEventScene);
        newEventController.initWithScenario(myScenario);
        inputStage.setTitle("New Event");
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
        
        if (selectedEvent != null)
            launchEventEditor(selectedEvent);
    }
    
    
    
    
    public void selectLink(AbstractLink lnk) {
        networkDisplay.findLink(lnk);
    }
    
    
    
    public void prepareEvent(AbstractEvent evt) {
        selectedEvent = evt;
    }
    
    
    @FXML
    void onDeleteEvent(ActionEvent event) {
        if (ignoreChange)
            return;
        
        List<AbstractEvent> events = myScenario.get_events();
        int idx = listEvents.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= events.size()))
            return;
        
        String header = "You are deleting event '" + events.get(idx).name + "'...";
        if (!opt.utils.Dialogs.ConfirmationYesNoDialog(header, "Are you sure?"))
            return;
        
        myScenario.delete_event_by_id(events.get(idx).id);
        populateEventList();
        setProjectModified(true);
    }
    
    
    @FXML
    void listEventsOnMouseClick(MouseEvent event) {
        if (ignoreChange)
            return;
        
        if (event.getClickCount() == 2) {
            List<AbstractEvent> events = myScenario.get_events();
            int idx = listEvents.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= events.size()))
                return;
            launchEventEditor(events.get(idx));
        }
    }
    
    
    @FXML
    void listEventsOnKeyPressed(KeyEvent event) {
        if (ignoreChange)
            return;
        
        List<AbstractEvent> events = myScenario.get_events();
        
        if (event.getCode() == KeyCode.ENTER) {
            int idx = listEvents.getSelectionModel().getSelectedIndex();
            if ((idx < 0) || (idx >= events.size()))
                return;
            launchEventEditor(events.get(idx));
        }
        if ((event.getCode() == KeyCode.DELETE) || (event.getCode() == KeyCode.BACK_SPACE)) {
            onDeleteEvent(null);
        }
    }

    
}
