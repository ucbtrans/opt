/**
 * Copyright (c) 2021, Regents of the University of California
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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;
import opt.data.control.ControlFactory;
import opt.data.event.AbstractEvent;

/**
 * UI to choose the type of the new event.
 * 
 * @author Alex Kurzhanskiy
 */
public class NewEventController {
    private ScenarioEditorController scenarioEditorController = null;
    private FreewayScenario myScenario = null;
    
    @FXML // fx:id="topPane"
    private GridPane topPane; // Value injected by FXMLLoader

    @FXML // fx:id="listEventTypes"
    private ChoiceBox<String> listEventTypes; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader

    @FXML // fx:id="buttonOK"
    private Button buttonOK; // Value injected by FXMLLoader

    
    
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the scenario editor controller from where this
     *               sub-window is launched.
     */
    public void setScenarioEditorController(ScenarioEditorController ctrl) {
        scenarioEditorController = ctrl;
    }
    
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        listEventTypes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (listEventTypes.getSelectionModel().getSelectedIndex() < 0)
                buttonOK.setDisable(true);
            else
                buttonOK.setDisable(false);
        });
        
    }
    
    
    
    public void initWithScenario(FreewayScenario s) {
        myScenario = s;
        buttonOK.setDisable(true);
        
        listEventTypes.getItems().clear();
        listEventTypes.getItems().add("Change Number of Lanes");
        listEventTypes.getItems().add("Change Traffic Dynamics");
        listEventTypes.getItems().add("Open / Close Off-Ramps");
    }
    
    
   
    
    /***************************************************************************
     * CALLBACKS
     **************************************************************************/


    @FXML
    void onCancel(ActionEvent event) {
       // scenarioEditorController.prepareNewEvent(null);
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onOK(ActionEvent event) {
        int idx = listEventTypes.getSelectionModel().getSelectedIndex();
        
        if (idx < 0) {
            opt.utils.Dialogs.WarningDialog("No event type selected!", "Please, choose an event type from the list...");
            return;
        }

        AbstractEvent evt;
        try {
            switch (idx) {
                case 0:
                    evt = myScenario.add_event_lglanes(0, "Add/Remove lane(s)", new ArrayList<>(), LaneGroupType.gp, 0);
                    break;
                case 1:
                    evt = myScenario.add_event_lgfd(0, "Change traffic dynamics", new ArrayList<>(), LaneGroupType.gp, null);
                    break;
                case 2:
                    evt = myScenario.add_event_linktoggle(0, "Open/Close off-ramp(s)", new ArrayList<>(), true);
                    break;
                default:
                    evt = null;
                    break;
            }
        } catch (Exception ex) {
            opt.utils.Dialogs.ExceptionDialog("Cannot create event!", ex);
            return;
        }
        
        scenarioEditorController.prepareEvent(evt);
        
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

}
