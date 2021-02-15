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
package opt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Callback;
import opt.config.*;
import opt.data.*;
import opt.data.control.AbstractController;
import opt.data.control.ControlSchedule;
import opt.data.event.AbstractEvent;
import opt.data.event.AbstractEventLaneGroup;
import opt.data.event.EventLinkToggle;
import opt.performance.LinkPerformanceController;
import opt.performance.RoutePerformanceController;
import opt.performance.ScenarioPerformanceController;
import opt.utils.Misc;
import opt.utils.UtilGUI;
import opt.utils.Version;
import org.apache.poi.ss.usermodel.Workbook;


/**
 * This class contains callback functions for the main application window controls.
 * 
 * @author Alex Kurzhanskiy
 */
public class AppMainController {

    private Stage primaryStage = null;
    private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
    private boolean projectModified = false;
    private String projectFilePath = null;
    private String optProjectFileDir_String = "optProjectFileDir_String";
    private File projectFileDir = null;
    private String roadLinksTreeItem = "Road Sections";
    private String routesTreeItem = "Routes";
    private String controllersTreeItem = "Controllers";
    private String eventsTreeItem = "Events";
    private Project project = null;
    
    private Map<TreeItem, Object> tree2object = new HashMap<TreeItem, Object>();
    private Map<Object, TreeItem> object2tree = new HashMap<Object, TreeItem>();
    
    private GridPane preferencesPane = null;
    private PreferencesController preferencesController = null;
    private Scene preferencesScene = null;
    
    private GridPane helpPane = null;
    private HelpController helpController = null;
    private Scene helpScene = null;
    
    private GridPane simProgressPane = null;
    private SimulationController simulationController = null;
    private Scene simProgressScene = null;
    
    private SplitPane scenarioEditorPane = null;
    private ScenarioEditorController scenarioEditorController = null;
    private TabPane scenarioPerformancePane = null;
    private ScenarioPerformanceController scenarioPerformanceController = null;
    private GridPane vehicleTypePane = null;
    private VehicleTypeController vehicleTypeController = null;
    private GridPane lanePolicyControlPane = null;
    private LaneControlEditorController laneControlEditorController = null;
    
    private SplitPane linkEditorPane = null;
    private LinkEditorController linkEditorController = null;
    private TabPane linkPerformancePane = null;
    private LinkPerformanceController linkPerformanceController = null;
    private ScrollPane linkInfoPane = null;
    private LinkInfoController linkInfoController = null;
    private ScrollPane routeInfoPane = null;
    private RouteInfoController routeInfoController = null;
    private ScrollPane scenarioInfoPane = null;
    private ScenarioInfoController scenarioInfoController = null;
    private GridPane newLinkPane = null;
    private NewLinkController newLinkController = null;
    private GridPane newRampPane = null;
    private NewRampController newRampController = null;
    private GridPane connectPane = null;
    private ConnectController connectController = null;
    private GridPane newRampMeterPane = null;
    private NewRampMeterController newRampMeterController = null;

    private GridPane rampMeterClosedPane = null;
    private RampMeterClosed rampMeterClosed = null;

    private GridPane rampMeterOpenPane = null;
    private RampMeterOpen rampMeterOpen = null;

    private GridPane rampMeterAlineaPane = null;
    private RampMeterAlinea rampMeterAlinea = null;

    private GridPane rampMeterFixedRatePane = null;
    private RampMeterFixed rampMeterFixed = null;
    
    private GridPane newEventPane = null;
    private NewEventController newEventController = null;
    
    private GridPane eventFDPane = null;
    private EventFD eventFD = null;
    
    private GridPane eventLanesPane = null;
    private EventLanes eventLanes = null;
    
    private GridPane eventLinkOCPane = null;
    private EventLinkOC eventLinkOC = null;
    
    
    private SplitPane routeEditorPane = null;
    private RouteController routeController = null;
    private TabPane routePerformancePane = null;
    private RoutePerformanceController routePerformanceController = null;
    private GridPane routeChoicePane = null;
    private RouteChoiceController routeChoiceController = null;
    
    private Image imageLinkFreeway = new Image(getClass().getResourceAsStream("/LinkFreeway.gif"));
    private Image imageLinkOR = new Image(getClass().getResourceAsStream("/LinkOR.gif"));
    private Image imageLinkFR = new Image(getClass().getResourceAsStream("/LinkFR.gif"));
    private Image imageLinkConnector = new Image(getClass().getResourceAsStream("/LinkConnector.gif"));
    private Image imageRoute = new Image(getClass().getResourceAsStream("/Route.gif"));
    private Image imageScenario = new Image(getClass().getResourceAsStream("/Scenario.gif"));
    private Image imageFolder = new Image(getClass().getResourceAsStream("/imageFolder.png"));
 
    private TreeItem<String> selectedTreeItem = null;
    private FreewayScenario selectedScenario = null;
    private FreewayScenario mySimScenario = null;
    protected SimDataScenario simdata = null;
    private Map<Object, Object> scenario2simData = new HashMap<Object, Object>();
    
    private ContextMenu routesCM = new ContextMenu(); 
    private MenuItem cmNewRoute = new MenuItem("New Route");
    
    private boolean linksOpen = false;
    private boolean routesOpen = false;
    
    @FXML // fx:id="topPane"
    private VBox topPane; // Value injected by FXMLLoader

    @FXML // fx:id="menuFileNewProject"
    private MenuItem menuFileNewProject; // Value injected by FXMLLoader

    @FXML // fx:id="menuFileOpenProject"
    private MenuItem menuFileOpenProject; // Value injected by FXMLLoader

    @FXML // fx:id="menuFileSave"
    private MenuItem menuFileSave; // Value injected by FXMLLoader

    @FXML // fx:id="menuFileSaveAs"
    private MenuItem menuFileSaveAs; // Value injected by FXMLLoader

    @FXML // fx:id="menuFilePreferences"
    private MenuItem menuFilePreferences; // Value injected by FXMLLoader

    @FXML // fx:id="menuFileExit"
    private MenuItem menuFileExit; // Value injected by FXMLLoader
    
    @FXML // fx:id="menuSimulationRun"
    private MenuItem menuSimulationRun; // Value injected by FXMLLoader

    @FXML // fx:id="menuSimulationExportResultsToExcel"
    private MenuItem menuSimulationExportResultsToExcel; // Value injected by FXMLLoader

    @FXML // fx:id="menuSimulationFullExcelReport"
    private MenuItem menuSimulationFullExcelReport; // Value injected by FXMLLoader

    @FXML // fx:id="menuHelpAbout"
    private MenuItem menuHelpAbout; // Value injected by FXMLLoader
    
    @FXML // fx:id="menuHelpOnline"
    private MenuItem menuHelpOnline; // Value injected by FXMLLoader

    @FXML // fx:id="projectTree"
    private TreeView<String> projectTree; // Value injected by FXMLLoader

    @FXML // fx:id="actionPane"
    private TabPane actionPane; // Value injected by FXMLLoader

    @FXML // fx:id="configTabPane"
    private Tab configTabPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="configAnchorPane"
    private AnchorPane configAnchorPane; // Value injected by FXMLLoader

    @FXML // fx:id="reportTabPane"
    private Tab reportTabPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="reportAnchorPane"
    private AnchorPane reportAnchorPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="infoAnchorPane"
    private AnchorPane infoAnchorPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="leftStatus"
    private Label leftStatus; // Value injected by FXMLLoader

    @FXML // fx:id="simProgressBar"
    private ProgressBar simProgressBar; // Value injected by FXMLLoader

    @FXML // fx:id="x3"
    private Font x3; // Value injected by FXMLLoader

    @FXML // fx:id="x4"
    private Color x4; // Value injected by FXMLLoader
    


    
    /***************************************************************************
     *  GETTERS
     ***************************************************************************/
    
    public GridPane getNewLinkPane() {
        return newLinkPane;
    }
    
    public NewLinkController getNewLinkController() {
        return newLinkController;
    }
    
    public Label getLeftStatus() {
        return leftStatus;
    }
    
    public Tab getReportTabPane() {
        return reportTabPane;
    }

    
    
    
    
    /***************************************************************************
     * OPERATIONAL
     ***************************************************************************/
    
    public void setPrimaryStage(Stage s) {
        primaryStage = s;
    }
    
    
    public void toSaveProjectOrNot() {
        if (projectModified) {
            if (opt.utils.Dialogs.ConfirmationYesNoDialog("Your project was modified...", "Do you want to save it?")) {
                if (projectFilePath == null) {
                    onClickMenuFileSaveAs(null);
                } else {
                    onClickMenuFileSave(null);
                }
            }
        }
    }
    
    public void setProjectModified(boolean val) {
        projectModified = val;
        
        String title = "OPT: ";
        if (projectFilePath == null) {
            title += "New Project";
        } else {
            title += projectFilePath;
            if (projectModified)
                title += "*";
        }
        
        primaryStage.setTitle(title);
    }
    
    
    private void setSelectedScenario(TreeItem<String> treeItem) {
        selectedScenario = null;
        menuSimulationRun.setDisable(true);
        menuSimulationExportResultsToExcel.setDisable(true);
        menuSimulationFullExcelReport.setDisable(true);
        TreeItem<String> p = treeItem;
        while (!p.equals(projectTree.getRoot())) {
            Object obj = tree2object.get(p);
            if (obj instanceof FreewayScenario) {
                selectedScenario = (FreewayScenario)obj;
                menuSimulationRun.setDisable(false);
                if (Misc.myMapGet(scenario2simData, selectedScenario) != null) {
                    reportTabPane.setDisable(false);
                    menuSimulationFullExcelReport.setDisable(false);
                }
                return;
            }
            p = p.getParent();
        }
        actionPane.getSelectionModel().selectFirst();
        reportTabPane.setDisable(true);
    }
    
    
    public void setLeftStatus(String status) {
        leftStatus.setText(status);   
    }
    
    public void bindProgressBar(ReadOnlyDoubleProperty prop) {
        //simProgressBar.progressProperty().bind(prop);
        //simProgressBar.setVisible(true);
        simulationController.bindProgressBar(prop);
    }

    public void unbindProgressBar() {
        //simProgressBar.progressProperty().unbind();
        //simProgressBar.setVisible(false);
        ;//simulationController.unbindProgressBar();
    }
    
    
    public void clearSimData() {
        if (selectedScenario == null)
            return;

        if (Misc.myMapGet(scenario2simData, selectedScenario) != null) {
            scenario2simData = Misc.myMapRemove(scenario2simData, selectedScenario);
            actionPane.getSelectionModel().selectFirst();
            reportTabPane.setDisable(true);
        }
    }
    
    
    /**
     * Start simulation of the selected scenario.
     */
    @FXML
    public void runSimulation(ActionEvent event) {
        if (selectedScenario == null)
            return;

        clearSimData();
        
        leftStatus.setText("Simulating scenario \"" + selectedScenario.name + "\"...");
        menuFileNewProject.setDisable(true);
        menuFileOpenProject.setDisable(true);
        menuSimulationRun.setDisable(true);
        menuSimulationExportResultsToExcel.setDisable(true);
        menuSimulationFullExcelReport.setDisable(true);
        
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(simProgressScene);
        simulationController.initWithScenarioData(this, selectedScenario);
        String title = "Simulation is Running...";
        inputStage.setTitle(title);
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
        
        
        
    }
    
    
    public void attachSimDataToScenario(SimDataScenario d, Exception e) {
        if (e != null) {
            opt.utils.Dialogs.ExceptionDialog("Error in simulation", e);
            return;
        }
        
        if (d == null)
            return;
        
        scenario2simData.put(d.fwyscenario, d);
        mySimScenario = d.fwyscenario;
        simdata = d;
    }
    
    
    
    public void completeSimulation() {
        simulationController.unbindProgressBar();
        menuSimulationRun.setDisable(false);
        menuFileNewProject.setDisable(false);
        menuFileOpenProject.setDisable(false);
        reportTabPane.setDisable(false);
        leftStatus.setText("Simulation completed!");
        if ((selectedScenario != null) &&
            (scenario2simData.containsKey(selectedScenario)) &&
            (scenario2simData.get(selectedScenario) != null)) {
            actionPane.getSelectionModel().selectLast();
            processTreeSelection(selectedTreeItem);
        } else {
            reportTabPane.setDisable(true);
        }
    }

    
    
    
    /***************************************************************************
     *  INITIALIZATION
     ***************************************************************************/
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        
        // Tree action listener
        projectTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           processTreeSelection((TreeItem<String>)newValue); 
        });
        
        actionPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) -> {
           onSelectTabInActionPane();
        });
        
        routesCM.getItems().add(cmNewRoute);
        cmNewRoute.setOnAction(new EventHandler() {
            public void handle(Event t) {
                createNewRoute(t);
            }
        });
        
        projectTree.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> arg0) {
                // custom tree cell that defines a context menu for the root tree item
                return new MyTreeCell();
            }
        });
        
        simProgressBar.setVisible(false);
        
        // Initialize link editor
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenario_editor.fxml"));
            scenarioEditorPane = loader.load();
            scenarioEditorController = loader.getController();
            scenarioEditorController.setPrimaryStage(primaryStage);
            scenarioEditorController.setAppMainController(this);
            
            loader = new FXMLLoader(getClass().getResource("/scenario_performance.fxml"));
            scenarioPerformancePane = loader.load();
            scenarioPerformanceController = loader.getController();
            scenarioPerformanceController.setPrimaryStage(primaryStage);
            scenarioPerformanceController.setAppMainController(this);
            
            
            loader = new FXMLLoader(getClass().getResource("/vehicle_type.fxml"));
            vehicleTypePane = loader.load();
            vehicleTypeController = loader.getController();
            vehicleTypeController.setAppMainController(this);
            scenarioEditorController.setVehicleTypeControllerAndScene(vehicleTypeController, new Scene(vehicleTypePane));
            
            loader = new FXMLLoader(getClass().getResource("/lane_policy_editor.fxml"));
            lanePolicyControlPane = loader.load();
            laneControlEditorController = loader.getController();
            laneControlEditorController.setScenarioEditorController(scenarioEditorController);
            scenarioEditorController.setLaneControlEditorControllerAndScene(laneControlEditorController, new Scene(lanePolicyControlPane));
            
            
            loader = new FXMLLoader(getClass().getResource("/link_editor.fxml"));
            linkEditorPane = loader.load();
            linkEditorController = loader.getController();
            linkEditorController.setPrimaryStage(primaryStage);
            linkEditorController.setAppMainController(this);
            
            loader = new FXMLLoader(getClass().getResource("/link_performance.fxml"));
            linkPerformancePane = loader.load();
            linkPerformanceController = loader.getController();
            linkPerformanceController.setPrimaryStage(primaryStage);
            linkPerformanceController.setAppMainController(this);
            
            loader = new FXMLLoader(getClass().getResource("/link_info.fxml"));
            linkInfoPane = loader.load();
            linkInfoController = loader.getController();
            linkInfoController.setAppMainController(this);
            
            loader = new FXMLLoader(getClass().getResource("/route_info.fxml"));
            routeInfoPane = loader.load();
            routeInfoController = loader.getController();
            routeInfoController.setAppMainController(this);
            
            loader = new FXMLLoader(getClass().getResource("/scenario_info.fxml"));
            scenarioInfoPane = loader.load();
            scenarioInfoController = loader.getController();
            scenarioInfoController.setAppMainController(this);
            
            loader = new FXMLLoader(getClass().getResource("/new_link.fxml"));
            newLinkPane = loader.load();
            newLinkController = loader.getController();
            newLinkController.setAppMainController(this);
            linkEditorController.setNewLinkControllerAndScene(newLinkController, new Scene(newLinkPane));
            
            loader = new FXMLLoader(getClass().getResource("/new_ramp.fxml"));
            newRampPane = loader.load();
            newRampController = loader.getController();
            newRampController.setAppMainController(this);
            linkEditorController.setNewRampControllerAndScene(newRampController, new Scene(newRampPane));
            
            loader = new FXMLLoader(getClass().getResource("/connect_link.fxml"));
            connectPane = loader.load();
            connectController = loader.getController();
            connectController.setAppMainController(this);
            linkEditorController.setConnectControllerAndScene(connectController, new Scene(connectPane));
            
            loader = new FXMLLoader(getClass().getResource("/new_ramp_meter.fxml"));
            newRampMeterPane = loader.load();
            newRampMeterController = loader.getController();
            newRampMeterController.setLinkEditorController(linkEditorController);
            linkEditorController.setNewRampMeterControllerAndScene(newRampMeterController, new Scene(newRampMeterPane));

            loader = new FXMLLoader(getClass().getResource("/rm_open_editor.fxml"));
            rampMeterOpenPane = loader.load();
            rampMeterOpen = loader.getController();
            rampMeterOpen.setLinkEditorController(linkEditorController);
            linkEditorController.setRampMeterOpenControllerAndScene(rampMeterOpen, new Scene(rampMeterOpenPane));


            loader = new FXMLLoader(getClass().getResource("/rm_closed_editor.fxml"));
            rampMeterClosedPane = loader.load();
            rampMeterClosed = loader.getController();
            rampMeterClosed.setLinkEditorController(linkEditorController);
            linkEditorController.setRampMeterClosedControllerAndScene(rampMeterClosed, new Scene(rampMeterClosedPane));

            loader = new FXMLLoader(getClass().getResource("/rm_alinea_editor.fxml"));
            rampMeterAlineaPane = loader.load();
            rampMeterAlinea = loader.getController();
            rampMeterAlinea.setLinkEditorController(linkEditorController);
            linkEditorController.setRampMeterAlineaControllerAndScene(rampMeterAlinea, new Scene(rampMeterAlineaPane));
            
            loader = new FXMLLoader(getClass().getResource("/rm_fixedrate_editor.fxml"));
            rampMeterFixedRatePane = loader.load();
            rampMeterFixed = loader.getController();
            rampMeterFixed.setLinkEditorController(linkEditorController);
            linkEditorController.setRampMeterFixedRateControllerAndScene(rampMeterFixed, new Scene(rampMeterFixedRatePane));
            
            // Event controllers
            loader = new FXMLLoader(getClass().getResource("/new_event.fxml"));
            newEventPane = loader.load();
            newEventController = loader.getController();
            newEventController.setScenarioEditorController(scenarioEditorController);
            scenarioEditorController.setNewEventControllerAndScene(newEventController, new Scene(newEventPane));
            
            loader = new FXMLLoader(getClass().getResource("/event_fd.fxml"));
            eventFDPane = loader.load();
            eventFD = loader.getController();
            eventFD.setScenarioEditorController(scenarioEditorController);
            scenarioEditorController.setEventFDControllerAndScene(eventFD, new Scene(eventFDPane));
            
            loader = new FXMLLoader(getClass().getResource("/event_lanes.fxml"));
            eventLanesPane = loader.load();
            eventLanes = loader.getController();
            eventLanes.setScenarioEditorController(scenarioEditorController);
            scenarioEditorController.setEventLanesControllerAndScene(eventLanes, new Scene(eventLanesPane));
            
            loader = new FXMLLoader(getClass().getResource("/event_links_toggle.fxml"));
            eventLinkOCPane = loader.load();
            eventLinkOC = loader.getController();
            eventLinkOC.setScenarioEditorController(scenarioEditorController);
            scenarioEditorController.setEventLinkOCControllerAndScene(eventLinkOC, new Scene(eventLinkOCPane));
            
            
            // Route controllers
            loader = new FXMLLoader(getClass().getResource("/route_editor.fxml"));
            routeEditorPane = loader.load();
            routeController = loader.getController();
            routeController.setPrimaryStage(primaryStage);
            routeController.setAppMainController(this);
            
            loader = new FXMLLoader(getClass().getResource("/route_performance.fxml"));
            routePerformancePane = loader.load();
            routePerformanceController = loader.getController();
            routePerformanceController.setPrimaryStage(primaryStage);
            routePerformanceController.setAppMainController(this);
            
            loader = new FXMLLoader(getClass().getResource("/route_choice.fxml"));
            routeChoicePane = loader.load();
            routeChoiceController = loader.getController();
            routeController.setRouteChoiceControllerAndScene(routeChoiceController, new Scene(routeChoicePane));
            
            
            loader = new FXMLLoader(getClass().getResource("/preferences_editor.fxml"));
            preferencesPane = loader.load();
            preferencesController = loader.getController();
            int[] dims = UtilGUI.getWindowDims(700, 700);
            preferencesScene = new Scene(preferencesPane, dims[0], dims[1]);
            
            
            loader = new FXMLLoader(getClass().getResource("/help.fxml"));
            helpPane = loader.load();
            helpController = loader.getController();
            dims = UtilGUI.getWindowDims(700, 850);
            helpScene = new Scene(helpPane, dims[0], dims[1]);
            
            
            loader = new FXMLLoader(getClass().getResource("/simulation_run.fxml"));
            simProgressPane = loader.load();
            simulationController = loader.getController();
            simProgressScene = new Scene(simProgressPane);
            
        } catch (IOException e) {
            opt.utils.Dialogs.ExceptionDialog("Cannot initialize UI modules...", e);
        }
        
        
    }
    
    
    private void populateProjectTree() {
        leftStatus.setText("");
        reportTabPane.setDisable(true);
        actionPane.getSelectionModel().selectFirst();
        tree2object = new HashMap<TreeItem, Object>();
        object2tree = new HashMap<Object, TreeItem>();
        
        if (project == null)
            return;
        
        TreeItem<String> root = new TreeItem<String>("Project");
        Collection<FreewayScenario> scenarios = project.get_scenarios();
        for (FreewayScenario scenario : scenarios) {
            String s_nm = "Scenario: " + scenario.name;
            TreeItem<String> scenario_node = new TreeItem<String>(s_nm, new ImageView(imageScenario));
            tree2object.put(scenario_node, scenario);
            object2tree.put(scenario, scenario_node);
            
            TreeItem<String> links_node = new TreeItem<String>(roadLinksTreeItem, new ImageView(imageFolder));
            scenario_node.getChildren().add(links_node);
            for (List<Segment> seg_list : scenario.get_linear_freeway_segments()) {
                for (Segment segment : seg_list) {
                    if (segment == null)
                        continue;
                    
                    AbstractLink link = segment.fwy();
                    if (object2tree.containsKey(link))
                        continue;
                    
                    TreeItem<String> seg_node;
                    if (link.get_type() == AbstractLink.Type.connector) {
                        seg_node = new TreeItem<String>(link.get_name(), new ImageView(imageLinkConnector));
                        tree2object.put(seg_node, link);
                        object2tree.put(link, seg_node);
                        links_node.getChildren().add(seg_node);
                    } else {
                        seg_node = new TreeItem<String>(link.get_name(), new ImageView(imageLinkFreeway));
                        tree2object.put(seg_node, link);
                        object2tree.put(link, seg_node);
                        links_node.getChildren().add(seg_node);
                    }

                    for (int i = 0; i < segment.num_out_ors(); i++) {
                        link = segment.out_ors(i);
                        TreeItem<String> link_node = new TreeItem<String>(link.get_name(), new ImageView(imageLinkOR));
                        tree2object.put(link_node, link);
                        object2tree.put(link, link_node);
                        seg_node.getChildren().add(link_node);
                    }
                    for (int i = 0; i < segment.num_in_ors(); i++) {
                        link = segment.in_ors(i);
                        TreeItem<String> link_node = new TreeItem<String>(link.get_name(), new ImageView(imageLinkOR));
                        tree2object.put(link_node, link);
                        object2tree.put(link, link_node);
                        seg_node.getChildren().add(link_node);
                    }
                    for (int i = 0; i < segment.num_out_frs(); i++) {
                        link = segment.out_frs(i);
                        TreeItem<String> link_node = new TreeItem<String>(link.get_name(), new ImageView(imageLinkFR));
                        tree2object.put(link_node, link);
                        object2tree.put(link, link_node);
                        seg_node.getChildren().add(link_node);
                    }
                    for (int i = 0; i < segment.num_in_frs(); i++) {
                        link = segment.in_frs(i);
                        TreeItem<String> link_node = new TreeItem<String>(link.get_name(), new ImageView(imageLinkFR));
                        tree2object.put(link_node, link);
                        object2tree.put(link, link_node);
                        seg_node.getChildren().add(link_node);
                    }
                    
                    /*List<AbstractLink> link_list = scenario.get_links();
                    for (AbstractLink lnk : link_list) {
                        if ((link == null) || (object2tree.containsKey(link))) {
                            continue;
                        }
                        
                        ImageView imageView = new ImageView(imageLinkFreeway);
                        if (link.get_type() == AbstractLink.Type.connector) {
                            imageView = new ImageView(imageLinkConnector);
                        } else if (link.get_type() == AbstractLink.Type.onramp) {
                            imageView = new ImageView(imageLinkOR);
                        } else if (link.get_type() == AbstractLink.Type.offramp) {
                            imageView = new ImageView(imageLinkFR);
                        }
                        TreeItem<String> link_node = new TreeItem<String>(link.get_name(), imageView);
                        tree2object.put(link_node, link);
                        object2tree.put(link, link_node);
                        links_node.getChildren().add(link_node);
                    }*/
                }
            }
            
            for (AbstractLink link : scenario.get_connectors()) {
                if ((link == null) || (object2tree.containsKey(link))) {
                    continue;
                }
                TreeItem<String> link_node = new TreeItem<String>(link.get_name(), new ImageView(imageLinkConnector));
                tree2object.put(link_node, link);
                object2tree.put(link, link_node);
                links_node.getChildren().add(link_node);
            }
            
            links_node.setExpanded(linksOpen);
            
            TreeItem<String> routes_node = new TreeItem<String>(routesTreeItem, new ImageView(imageFolder));    
            scenario_node.getChildren().add(routes_node);
            for (Route route : scenario.get_routes()) {
                if ((route == null) || (object2tree.containsKey(route))) {
                    continue;
                }
                TreeItem<String> route_node = new TreeItem<String>(route.getName(), new ImageView(imageRoute));
                tree2object.put(route_node, route);
                object2tree.put(route, route_node);
                routes_node.getChildren().add(route_node); 
            }
            
            routes_node.setExpanded(routesOpen);
            
            //TreeItem<String> events_node = new TreeItem<String>(eventsTreeItem, new ImageView(imageFolder));    
            //scenario_node.getChildren().add(events_node);
            
            root.getChildren().add(scenario_node);
        }
        
        root.setExpanded(true);
        if (projectTree.getRoot() != null)
            projectTree.getRoot().getChildren().clear();
        projectTree.setRoot(root);
        projectTree.setShowRoot(true);
        projectTree.refresh();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    /***************************************************************************
     *  CALLBACKS
     ***************************************************************************/
    
    @FXML
    private void onClickMenuFileNew(ActionEvent event) {
        if (projectModified) {
            toSaveProjectOrNot();
        }
        reset();
        
        ParametersFreeway params = UserSettings.getDefaultFreewayParams("A -> B",null);
        project = new Project("A","description A", "A -> B",params);
        menuFileSave.setDisable(false);
        menuFileSaveAs.setDisable(false);
        Collection<FreewayScenario> scenarios = project.get_scenarios();
        for (FreewayScenario scenario : scenarios) {
             for (AbstractLink link : scenario.get_links()) {
                if (link == null)
                    continue;
                try {
                    link.set_gp_lanes(UserSettings.defaultFreewayGPLanes);
                } catch (Exception e) {
                    ;
                }
             }
        }
        populateProjectTree();
        
        setProjectModified(true);
    }
    
    
    @FXML
    private void onClickMenuFileOpen(ActionEvent event) {
        if (projectModified) {
            toSaveProjectOrNot();
        }
        reset();
        
        boolean validate = true; 
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open the OPT Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OPT", "*.opt"));
        String optProjectFileDir_String = prefs.get(this.optProjectFileDir_String, null);
        if (optProjectFileDir_String != null) {
            projectFileDir = new File(optProjectFileDir_String);
            if ((projectFileDir != null) && (projectFileDir.isDirectory()))
                fileChooser.setInitialDirectory(projectFileDir);
        }
        File file = fileChooser.showOpenDialog(null);
        if (file==null)
            return;
        try {
            projectFilePath = file.getAbsolutePath();
            projectFileDir = file.getParentFile();
            prefs.put(this.optProjectFileDir_String, projectFileDir.getAbsolutePath());
            project = ProjectFactory.load_project(projectFilePath, validate);
            menuFileSave.setDisable(false);
            menuFileSaveAs.setDisable(false);
            populateProjectTree();
            setProjectModified(false);
        } catch (Exception ex) {
            opt.utils.Dialogs.ExceptionDialog("Error loading OPT project", ex);
        }
    }
   
    
   
    @FXML
    private void onClickMenuFileSave(ActionEvent event) {
        if (projectFilePath == null)
            onClickMenuFileSaveAs(event);
        try {
            ProjectFactory.save_project(project, projectFilePath);
            setProjectModified(false);
        } catch (Exception ex) {
            opt.utils.Dialogs.ExceptionDialog("Error saving OPT project", ex);
        }
    }
    
    
    
    @FXML
    private void onClickMenuFileSaveAs(ActionEvent event) {
        boolean validate = true; 
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save the OPT Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OPT", "*.opt"));

        // TODO THE FOLLOWING LINE COULD BE COMMENTED OUT FOR RELEASE VERSIONS. IN GENERAL IT WOULD BE NICE TO HAVE A DEBUG FLAG THAT CAN BE TURNED OFF.
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OTM", "*.xml"));

        String optProjectFileDir_String = prefs.get(this.optProjectFileDir_String, null);
        if (optProjectFileDir_String != null) {
            projectFileDir = new File(optProjectFileDir_String);
            if ((projectFileDir != null) && (projectFileDir.isDirectory()))
                fileChooser.setInitialDirectory(projectFileDir);
        }
        File file = fileChooser.showSaveDialog(null);
        if (file == null)
            return;
        try {

            String filetype = fileChooser.getSelectedExtensionFilter().getDescription();

            projectFilePath = file.getAbsolutePath();
            projectFileDir = file.getParentFile();
            prefs.put(this.optProjectFileDir_String, projectFileDir.getAbsolutePath());

            // save OPT file
            if( filetype.equalsIgnoreCase("OPT") ) {
                ProjectFactory.save_project(project, projectFilePath);
                setProjectModified(false);
            }

            // save OTM file
            if( filetype.equalsIgnoreCase("OTM") ) {
                int i = 0;
                for(FreewayScenario scn : project.get_scenarios())
                    ProjectFactory.save_scenario(scn, projectFilePath+ "_" + i++,true);
            }

        } catch (Exception ex) {
            opt.utils.Dialogs.ExceptionDialog("Error saving OPT project", ex);
        }
    }
    
    
    
    @FXML
    private void onClickMenuFilePreferences(ActionEvent event) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(preferencesScene);
        preferencesController.initWithLatestPreferences();
        String title = "User Preferences";
        inputStage.setTitle(title);
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
        processTreeSelection(selectedTreeItem);
    }
    
    
    
    @FXML
    private void onClickMenuFileExit(ActionEvent event) {
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }
    
    
    @FXML
    private void onExportResultsToExcel(ActionEvent event) {
        Object o = tree2object.get(selectedTreeItem);
        if (o == null)
            return;
        
        Workbook workbook = null;
        String wbFileName = "";
        
        if (o instanceof AbstractLink) {
            workbook = linkPerformanceController.getWorkbook();
            AbstractLink l = linkPerformanceController.getLink();
            if (l != null)
                wbFileName = l.get_name().replaceAll("->", "-to-");
        } else if (o instanceof Route) {
            workbook = routePerformanceController.getWorkbook();
            Route r = routePerformanceController.getRoute();
            if (r != null)
                wbFileName = r.getName();
        } else if (o instanceof FreewayScenario) {
            workbook = scenarioPerformanceController.getWorkbook();
            FreewayScenario s = scenarioPerformanceController.getScenario();
            if (s != null)
                wbFileName = s.name;
        }
        
        if (workbook == null)
            return;
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Simulation Results");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));

        String optProjectFileDir_String = prefs.get(this.optProjectFileDir_String, null);
        if (optProjectFileDir_String != null) {
            projectFileDir = new File(optProjectFileDir_String);
            if ((projectFileDir != null) && (projectFileDir.isDirectory())) {
                fileChooser.setInitialDirectory(projectFileDir);
                fileChooser.setInitialFileName(wbFileName);
            }
        }
        File file = fileChooser.showSaveDialog(null);
        if (file == null)
            return;
        try {
            String filetype = fileChooser.getSelectedExtensionFilter().getDescription();
            projectFileDir = file.getParentFile();
            prefs.put(this.optProjectFileDir_String, projectFileDir.getAbsolutePath());

            // save Excel file
            if( filetype.equalsIgnoreCase("Excel") ) {
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
            }
        } catch (Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Cannot export simulation results", e);
        }
        
        try {
            ;//workbook.close();
        } catch (Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Cannot close Workbook...", e);
        }
        
    }
    
    
    @FXML
    private void onFullExcelReport(ActionEvent event) {
        
    }
    
    
    
    @FXML
    void onClickMenuHelpAbout(ActionEvent event) {
        String versionName = "OPT Alpha release";
        String optgit = Version.getOPTGitHash();
        String otmgit = Version.getOTMSimGitHash();
        String date = "2021/01/31";
        String content = String.format("%s\nDate: %s\nOPT git hash: %s...\nOTM git hash: %s...",versionName,date,
                optgit.substring(0,10),otmgit.substring(0,10));
        opt.utils.Dialogs.InformationDialog(null, content);
    }
    
    
    @FXML
    void onMenuHelpOnline(ActionEvent event) {
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(helpScene);
        helpController.initHelpBrowser();
        String title = "OPT Help";
        inputStage.setTitle(title);
        inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setResizable(false);
        inputStage.showAndWait();
    }
    
    
    void onSelectTabInActionPane() {
        if (actionPane.getSelectionModel().getSelectedItem().equals(configTabPane))
            leftStatus.setText("");
        else
            processTreeSelection(selectedTreeItem);
    }
    
    
    
    void createNewRoute(Event event) {
        if (selectedScenario == null)
            return;
        
        clearSimData();
        
        Route route = selectedScenario.create_route("New Route");
        populateProjectTree();
        TreeItem item = object2tree.get(route);
        if (item == null) { 
            return;
        }
        
        projectTree.getSelectionModel().select(item);  
    }

    
    
    private void processTreeSelection(TreeItem<String> treeItem) {
        leftStatus.setText("");
        selectedScenario = null;
        if (treeItem == null)
            return;
        selectedTreeItem = treeItem;
        if (treeItem.equals(projectTree.getRoot()))
            return;
        
        if (treeItem.getValue().equals(roadLinksTreeItem))
            linksOpen = !treeItem.isExpanded();
        
        if (treeItem.getValue().equals(routesTreeItem))
            routesOpen = !treeItem.isExpanded();
        
        setSelectedScenario(treeItem);
        
        if ((selectedScenario != null) &&
            (Misc.myMapGet(scenario2simData, selectedScenario) != null)) {
            reportTabPane.setDisable(false);
        } else {
            actionPane.getSelectionModel().selectFirst();
            reportTabPane.setDisable(true);
        }
        
        Object obj = tree2object.get(treeItem);
        if (obj == null) {
            configAnchorPane.getChildren().clear();
            reportAnchorPane.getChildren().clear();
            infoAnchorPane.getChildren().clear();
            return;
        }
        
        configAnchorPane.getChildren().clear();
        reportAnchorPane.getChildren().clear();
        infoAnchorPane.getChildren().clear();
        
        if (obj instanceof AbstractLink) {
            configAnchorPane.getChildren().setAll(linkEditorPane);
            configAnchorPane.setTopAnchor(linkEditorPane, 0.0);
            configAnchorPane.setBottomAnchor(linkEditorPane, 0.0);
            configAnchorPane.setLeftAnchor(linkEditorPane, 0.0);
            configAnchorPane.setRightAnchor(linkEditorPane, 0.0);
            
            reportAnchorPane.getChildren().setAll(linkPerformancePane);
            reportAnchorPane.setTopAnchor(linkPerformancePane, 0.0);
            reportAnchorPane.setBottomAnchor(linkPerformancePane, 0.0);
            reportAnchorPane.setLeftAnchor(linkPerformancePane, 0.0);
            reportAnchorPane.setRightAnchor(linkPerformancePane, 0.0);
                
            infoAnchorPane.getChildren().setAll(linkInfoPane);
            infoAnchorPane.setTopAnchor(linkInfoPane, 0.0);
            infoAnchorPane.setBottomAnchor(linkInfoPane, 0.0);
            infoAnchorPane.setLeftAnchor(linkInfoPane, 0.0);
            infoAnchorPane.setRightAnchor(linkInfoPane, 0.0);

            AbstractLink lnk = (AbstractLink)obj;
            linkEditorController.initWithLinkData(lnk);
            linkInfoController.initWithScenarioAndLinkData(lnk);
            if (Misc.myMapGet(scenario2simData, selectedScenario) != null) { 
                menuSimulationExportResultsToExcel.setDisable(false);
                linkPerformanceController.initWithLinkData(lnk, (SimDataScenario)Misc.myMapGet(scenario2simData, selectedScenario));
                if (actionPane.getSelectionModel().getSelectedItem().equals(reportTabPane)) {
                    String ln = "(" + lnk.get_gp_lanes() + " GP";
                    if (lnk.get_mng_lanes() > 0)
                        ln += ", " + lnk.get_mng_lanes() + " managed";
                    if (lnk.get_aux_lanes() > 0)
                        ln += ", " + lnk.get_aux_lanes() + " aux";
                    ln += " lanes)";
                    leftStatus.setText("Report for section\"" + lnk.get_name() + "\" " + ln);
                }
            } else {
                menuSimulationExportResultsToExcel.setDisable(true);
            }
        }

        if (obj instanceof FreewayScenario) {
            configAnchorPane.getChildren().setAll(scenarioEditorPane);
            configAnchorPane.setTopAnchor(scenarioEditorPane, 0.0);
            configAnchorPane.setBottomAnchor(scenarioEditorPane, 0.0);
            configAnchorPane.setLeftAnchor(scenarioEditorPane, 0.0);
            configAnchorPane.setRightAnchor(scenarioEditorPane, 0.0);
            
            reportAnchorPane.getChildren().setAll(scenarioPerformancePane);
            reportAnchorPane.setTopAnchor(scenarioPerformancePane, 0.0);
            reportAnchorPane.setBottomAnchor(scenarioPerformancePane, 0.0);
            reportAnchorPane.setLeftAnchor(scenarioPerformancePane, 0.0);
            reportAnchorPane.setRightAnchor(scenarioPerformancePane, 0.0);
            
            FreewayScenario fws = (FreewayScenario)obj;
            launchScenarioInfoPanel(fws);
            if (fws != null)
                scenarioEditorController.initWithScenarioData(fws);
            if (Misc.myMapGet(scenario2simData, selectedScenario) != null) {
                menuSimulationExportResultsToExcel.setDisable(false);
                scenarioPerformanceController.initWithScenarioData((SimDataScenario)Misc.myMapGet(scenario2simData, selectedScenario));
                if (actionPane.getSelectionModel().getSelectedItem().equals(reportTabPane)) {
                    leftStatus.setText("Report for scenario \"" + selectedScenario.name + "\"");
                }
            } else {
                menuSimulationExportResultsToExcel.setDisable(true);
            }
        }
        
        if (obj instanceof Route) {
            configAnchorPane.getChildren().setAll(routeEditorPane);
            configAnchorPane.setTopAnchor(routeEditorPane, 0.0);
            configAnchorPane.setBottomAnchor(routeEditorPane, 0.0);
            configAnchorPane.setLeftAnchor(routeEditorPane, 0.0);
            configAnchorPane.setRightAnchor(routeEditorPane, 0.0);
            
            reportAnchorPane.getChildren().setAll(routePerformancePane);
            reportAnchorPane.setTopAnchor(routePerformancePane, 0.0);
            reportAnchorPane.setBottomAnchor(routePerformancePane, 0.0);
            reportAnchorPane.setLeftAnchor(routePerformancePane, 0.0);
            reportAnchorPane.setRightAnchor(routePerformancePane, 0.0);
            
            Route route = (Route)obj;
            launchRouteInfoPanel(route);
            if (route != null)
                routeController.initWithRouteData(route);
            if (Misc.myMapGet(scenario2simData, selectedScenario) != null) {
                menuSimulationExportResultsToExcel.setDisable(false);
                routePerformanceController.initWithRouteData(route, (SimDataScenario)Misc.myMapGet(scenario2simData, selectedScenario));
                if (actionPane.getSelectionModel().getSelectedItem().equals(reportTabPane)) {
                    leftStatus.setText("Report for route \"" + route.getName() + "\"");
                }
            } else {
                menuSimulationExportResultsToExcel.setDisable(true);
            }
        }
        
    }
    
    
    public void launchRouteInfoPanel(Route r) {
        if (r == null)
            return;
        
        infoAnchorPane.getChildren().clear();
        infoAnchorPane.getChildren().setAll(routeInfoPane);
        infoAnchorPane.setTopAnchor(routeInfoPane, 0.0);
        infoAnchorPane.setBottomAnchor(routeInfoPane, 0.0);
        infoAnchorPane.setLeftAnchor(routeInfoPane, 0.0);
        infoAnchorPane.setRightAnchor(routeInfoPane, 0.0);
        routeInfoController.initWithScenarioAndRouteData(r);
    }
    
    
    private void launchScenarioInfoPanel(FreewayScenario s) {
        if (s == null)
            return;
        
        infoAnchorPane.getChildren().clear();
        infoAnchorPane.getChildren().setAll(scenarioInfoPane);
        infoAnchorPane.setTopAnchor(scenarioInfoPane, 0.0);
        infoAnchorPane.setBottomAnchor(scenarioInfoPane, 0.0);
        infoAnchorPane.setLeftAnchor(scenarioInfoPane, 0.0);
        infoAnchorPane.setRightAnchor(scenarioInfoPane, 0.0);
        scenarioInfoController.initWithScenarioData(s);
    }
    

    /**
     * This function is called when the user navigates to the given link not
     * from projectTree, but using other pointers. Now we need to change the
     * selected tree item appropriately and open the corresponding action
     * and info panels.
     * 
     * @param lnk - road link.
     */
    public void selectLink(AbstractLink lnk) {
        if (lnk == null) 
            return;
        
        if (projectTree.getRoot() != null)
            projectTree.getRoot().getChildren().clear();

        populateProjectTree();
        
        TreeItem item = object2tree.get(lnk);
        if (item == null) { 
            return;
        }
        
        projectTree.getSelectionModel().select(item);  
    }
    
    
    /**
     * This function is called when the user navigates to the given route not
     * from projectTree, but using other pointers. Now we need to change the
     * selected tree item appropriately and open the corresponding action
     * and info panels.
     * 
     * @param r - route.
     */
    public void selectRoute(Route r) {
        if (r == null) 
            return;
        
        if (projectTree.getRoot() == null)
            populateProjectTree();
        
        TreeItem item = object2tree.get(r);
        if (item == null) { 
            return;
        }
        
        projectTree.getSelectionModel().select(item);  
    }
    
    
    public void openLanePolicies(ControlSchedule cs) {
        if (cs == null) 
            return;
        
        if (projectTree.getRoot() == null)
            populateProjectTree();
        
        TreeItem item = object2tree.get(selectedScenario);
        if (item == null) { 
            return;
        }
        
        actionPane.getSelectionModel().select(configTabPane);
        projectTree.getSelectionModel().select(item);
        scenarioEditorController.openLanePolicies(cs);
    }
    
    
    public void openEvents(AbstractEvent e) {
        if (e == null) 
            return;
        
        if (projectTree.getRoot() == null)
            populateProjectTree();
        
        TreeItem item = object2tree.get(selectedScenario);
        if (item == null) { 
            return;
        }
        
        actionPane.getSelectionModel().select(configTabPane);
        projectTree.getSelectionModel().select(item);
        scenarioEditorController.openEvents(e);
    }
    
    
    
    
    
    public void objectNameUpdate(Object obj) {
        if (obj == null) 
            return;
        
        if (projectTree.getRoot() != null)
            projectTree.getRoot().getChildren().clear();
        
        tree2object = new HashMap<TreeItem, Object>();
        object2tree = new HashMap<Object, TreeItem>();
        
        setProjectModified(true);
        populateProjectTree();
        
        TreeItem item = object2tree.get(obj);
        if (item == null) 
            return;
        
        projectTree.getSelectionModel().select(item);  
    }
    
    
    public void changeScenarioName(FreewayScenario s, String new_name) {
        if (s == null)
            return;
        
        try {
            project.set_scenario_name(s.name, new_name);
        } catch (Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Cannot change scenario name...", e);
        }
        
        objectNameUpdate(s);
    }
    
    
    public void removeFromPolicies(List<AbstractLink> links) {
        for (ControlSchedule cs : selectedScenario.get_schedules_for_controltype(AbstractController.Type.LgRestrict)) {
            Set<AbstractLink> ll = cs.get_links();
            for (AbstractLink l : links) {
                if (ll.contains(l)) {
                    String header = String.format("Policy \"%s\" is modified", cs.get_name());
                        String content = String.format("Section \"%s\" is removed", l.get_name());
                    if (ll.size() == 1) {
                        header = String.format("Policy targets no sections");
                        content = String.format("Policy \"%s\" is removed", cs.get_name());
                        //opt.utils.Dialogs.WarningDialog(header, content);
                        //selectedScenario.delete_schedule(cs);
                        cs.remove_link(l);
                    } else {
                        //opt.utils.Dialogs.WarningDialog(header, content);
                        cs.remove_link(l);
                    }
                }
            }
        }
        
    }
    
    
    public void removeFromEvents(List<AbstractLink> links, boolean lgOnlyCheck) {
        for (AbstractEvent e : selectedScenario.get_events()) {
            List<AbstractLink> ll;
            boolean lge = false;
            if (e instanceof EventLinkToggle)
                ll = ((EventLinkToggle)e).get_links();
            else {
                ll = ((AbstractEventLaneGroup)e).get_links();
                lge = true;
            }
            for (AbstractLink l : links) {
                if (lgOnlyCheck && lge)
                    if (((((AbstractEventLaneGroup)e).get_lgtype() == LaneGroupType.mng) && l.has_mng()) ||
                        ((((AbstractEventLaneGroup)e).get_lgtype() == LaneGroupType.aux) && l.has_aux()))
                        continue;
                int idx = ll.indexOf(l);
                if (idx >= 0) {
                    String header = String.format("Event \"%s\" is modified", e.name);
                    String content = String.format("Section \"%s\" is removed", l.get_name());
                    //opt.utils.Dialogs.WarningDialog(header, content);
                    e.remove_link(ll.get(idx));
                }
            }
            if ((e.get_links() == null) || (e.get_links().size() == 0)) {
                String header = String.format("Event targets no sections");
                String content = String.format("Event \"%s\" is removed", e.name);
                //opt.utils.Dialogs.WarningDialog(header, content);
                selectedScenario.delete_event_by_id(e.id);
            }
        }
        clearSimData();
    }
    
    
    public void removeFromPoliciesAndEvents(List<AbstractLink> links, boolean lgOnlyCheck) {
        removeFromPolicies(links);
        removeFromEvents(links, lgOnlyCheck);
    }
    
    
    public void deleteLink(AbstractLink lnk, boolean reconnect) {
        if (lnk == null) 
            return;

        FreewayScenario scenario = lnk.get_segment().get_scenario();
        try {
            removeFromPoliciesAndEvents(lnk.get_segment().get_links(), false);
            scenario.delete_segment(lnk.get_segment(), reconnect, true);
        } catch (Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Could not delete road section...", e);
            return;
        }
        
        setProjectModified(true);
        populateProjectTree();
        
        TreeItem<String> stn = object2tree.get(scenario);
        TreeItem<String> links_node = stn.getChildren().stream()
                .filter(ch -> ch.getValue().matches(roadLinksTreeItem))
                .collect(Collectors.toList()).get(0);
        projectTree.getSelectionModel().select(links_node);
        links_node.setExpanded(true);
    }
    
    
    
    public void deleteRoute(Route rt) {
        if (rt == null) 
            return;
        
        try {
            selectedScenario.delete_route(rt.getId());
        } catch (Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Could not delete route '" + rt.getName() + "'...", e);
            return;
        }
        
        setProjectModified(true);
        populateProjectTree();
        
        TreeItem<String> stn = object2tree.get(rt.get_scenario());
        TreeItem<String> routes_node = stn.getChildren().stream()
                .filter(ch -> ch.getValue().matches(routesTreeItem))
                .collect(Collectors.toList()).get(0);
        projectTree.getSelectionModel().select(routes_node);
        routes_node.setExpanded(true);
    }





    /***************************************************************************
     * RESET
     ***************************************************************************/
    
    private void reset() {
        selectedScenario = null;
        setProjectModified(false);
        projectFilePath = null;
        
        scenario2simData.clear();
        
        menuSimulationRun.setDisable(true);
        menuSimulationExportResultsToExcel.setDisable(true);
        menuSimulationFullExcelReport.setDisable(true);
        
        if (projectTree.getRoot() != null)
            projectTree.getRoot().getChildren().clear();
        
        leftStatus.setText("");
        actionPane.getSelectionModel().select(configTabPane);
        reportTabPane.setDisable(true);
        configAnchorPane.getChildren().clear();
        infoAnchorPane.getChildren().clear();

        linksOpen = false;
        routesOpen = false;
    }
    
    





    class MyTreeCell extends TextFieldTreeCell<String> {
        public MyTreeCell() {
            super();
        }
        
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            // if the item is not empty and is a Routes folder
            if (!empty && getTreeItem().getValue().equals(routesTreeItem)) {
                setContextMenu(routesCM);
            }
            else {
                setContextMenu(null);
            }
        }

    }


    
}
