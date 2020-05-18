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

import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


/**
 * Controller for the pop-up dialog that chooses a route from a list of possible routes.
 * 
 * @author Alex Kurzhanskiy
 */
public class RouteChoiceController {
    private RouteController routeController = null;
    private List<String> routeChoices = null;

    @FXML // fx:id="topPane"
    private GridPane topPane; // Value injected by FXMLLoader

    @FXML // fx:id="linkList"
    private ChoiceBox<String> listRoutes; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader

    @FXML // fx:id="buttonOK"
    private Button buttonOK; // Value injected by FXMLLoader


    
    
    
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the route controller that is used to sync up
     *               all sub-windows.
     */
    public void setRouteController(RouteController ctrl) {
        routeController = ctrl;
    }
    
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        listRoutes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (listRoutes.getSelectionModel().getSelectedIndex() < 0)
                buttonOK.setDisable(true);
            else
                buttonOK.setDisable(false);
        });
    }
    
    
    
    public void initWithRouteChoices(List<String> choices) {
        listRoutes.getItems().clear();
        buttonOK.setDisable(true);
        listRoutes.getItems().addAll(choices);
    }
    
    
    
    
    
    
    
    
    
    /***************************************************************************
     * CALLBACKS
     **************************************************************************/

    @FXML
    void onCancel(ActionEvent event) {
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onOK(ActionEvent event) {
        int idx = listRoutes.getSelectionModel().getSelectedIndex();
        
        if (idx < 0) {
            opt.utils.Dialogs.WarningDialog("No route selected!", "Please, choose a route from the list...");
            return;
        }
        
        routeController.setSelectedRouteIndex(idx);
        
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

}
