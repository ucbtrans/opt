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
import java.util.List;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import opt.AppMainController;
import opt.data.Commodity;
import opt.data.FreewayScenario;
import opt.utils.Misc;


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
    private List<Commodity> listVT = new ArrayList<Commodity>();
    
    
    @FXML // fx:id="scenarioEditorMainPane"
    private SplitPane scenarioEditorMainPane; // Value injected by FXMLLoader

    @FXML // fx:id="canvasParent"
    private AnchorPane canvasParent; // Value injected by FXMLLoader

    @FXML // fx:id="scenarioEditorCanvas"
    private Canvas scenarioEditorCanvas; // Value injected by FXMLLoader

    @FXML // fx:id="scenarioName"
    private TextField scenarioName; // Value injected by FXMLLoader
    
    @FXML // fx:id="runSimulationButton"
    private Button runSimulationButton; // Value injected by FXMLLoader

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
    
    @FXML // fx:id="controllerPane"
    private TitledPane controllerPane; // Value injected by FXMLLoader

    @FXML // fx:id="eventPane"
    private TitledPane eventPane; // Value injected by FXMLLoader

   
    
    public Button getRunSimulationButton() {
        return runSimulationButton;
    }


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
     * @param ctrl - pointer to the new ramp controller that is used to set up
     *               new on- and off-ramps.
     */
    public void setVehicleTypeControllerAndScene(VehicleTypeController ctrl, Scene scn) {
        vehicleTypeController = ctrl;
        vehicleTypeScene = scn;
        vehicleTypeScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
    }
    
    
    
    void launchVehicleTypeWindow(Commodity comm) {
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
    
    
    
    /**
     * Start simulation of the current scenario.
     * @param event 
     */
    @FXML
    void runSimulation(ActionEvent event) {
        appMainController.runSimulation();
    }

    
    
    
    
    private void makeListVT(Map<Long, Commodity> mapVT) {
        listVT.clear();
        listVehicleTypes.getItems().clear();

        mapVT.forEach((k, v) -> {
            DecimalFormat df = new DecimalFormat("#.#");
            double nc = v.get_pvequiv();
            String s = v.get_name() + ": " + df.format(nc) + " car" + ((nc == 1) ? "" : "s");
            listVT.add(v);
            listVehicleTypes.getItems().add(s);
        });
    }
    
    
    
    

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        scenarioName.textProperty().addListener((observable, oldValue, newValue) -> {
            onScenarioNameChange(null);
        });
        
        startTime.setTextFormatter(opt.utils.TextFormatting.createTimeTextFormatter(Misc.seconds2timestring((float)opt.UserSettings.defaultStartTime, "")));
        startTime.textProperty().addListener((observable, oldValue, newValue) -> {
            onStartTimeChange(null);
        });
        
        sDuration.setTextFormatter(opt.utils.TextFormatting.createTimeTextFormatter(Misc.seconds2timestring((float)opt.UserSettings.defaultSimulationDuration, "")));
        sDuration.textProperty().addListener((observable, oldValue, newValue) -> {
            onDurationChange(null);
        });
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
        
        ignoreChange = false;
    }
    
    
    public void initScenarioTiming() {
        float st = myScenario.get_start_time();
        if (st == Float.NaN)
            st = (float)opt.UserSettings.defaultStartTime;
        
        float duration = myScenario.get_sim_duration();
        if (duration == Float.NaN)
            duration = (float)opt.UserSettings.defaultSimulationDuration;
        
        startTime.setText(Misc.seconds2timestring(st, ""));
        sDuration.setText(Misc.seconds2timestring(duration, ""));
        
    }
    


    
    
    
    
    
    
    /************************************************************
     * CALLBACKS
     ************************************************************/
    
    @FXML
    private void onScenarioNameChange(ActionEvent event) {
        if (ignoreChange)
            return;
        
        String nm = scenarioName.getText();
        if (nm.equals(""))
            nm = origScenarioName;
        
        appMainController.changeScenarioName(myScenario, nm);
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
    
}
