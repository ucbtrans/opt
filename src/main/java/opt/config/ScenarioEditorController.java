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

import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import opt.AppMainController;
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
    private AppMainController appMainController = null;
    private FreewayScenario myScenario = null;
    private boolean ignoreChange = true;
    
    private String origScenarioName = null;
    
    
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
    private ListView<?> listVehicleTypes; // Value injected by FXMLLoader

    @FXML // fx:id="deleteVehicleType"
    private Button deleteVehicleType; // Value injected by FXMLLoader

    @FXML // fx:id="newVehicleType"
    private Button newVehicleType; // Value injected by FXMLLoader

    @FXML // fx:id="controllerPane"
    private TitledPane controllerPane; // Value injected by FXMLLoader

    @FXML // fx:id="eventPane"
    private TitledPane eventPane; // Value injected by FXMLLoader

    

    
    


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
    
    
    
    
    @FXML
    void onScenarioNameChange(ActionEvent event) {
        if (ignoreChange)
            return;
        
        String nm = scenarioName.getText();
        if (nm.equals(""))
            nm = origScenarioName;
        appMainController.changeScenarioName(myScenario, nm);
    }
    
    
    
    @FXML
    void vehicleTypesKeyPressed(KeyEvent event) {

    }
    
    
     @FXML
    void onNewVehicleType(ActionEvent event) {

    }

    @FXML
    void onDeleteVehicleType(ActionEvent event) {

    }
    

    
    
    

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        scenarioName.textProperty().addListener((observable, oldValue, newValue) -> {
            onScenarioNameChange(null);
        });
        
        
    }
    
    
    
    
    
    
    /**
     * This function is called every time one opens a scenario in the
     * configuration module.
     * @param lnk 
     */
    public void initWithScenarioData(FreewayScenario s) {
        ignoreChange = true;
        
        scenarioName.setText(s.name);
        origScenarioName = s.name;
        myScenario = (FreewayScenario)s;
        
        ignoreChange = false;
    }
    
    




    
    
}
