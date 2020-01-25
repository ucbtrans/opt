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
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.data.AbstractLink;
import opt.data.ControlFactory;


/**
 * Controller for the pop-up dialog that creates a new ramp meter.
 * 
 * @author Alex Kurzhanskiy
 */
public class NewRampMeterController {
    private LinkEditorController linkEditorController = null;
    private AbstractLink myLink = null;
    

    @FXML // fx:id="topPane"
    private GridPane topPane; // Value injected by FXMLLoader

    @FXML // fx:id="listRM"
    private ChoiceBox<control.AbstractController.Algorithm> listRM; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader

    @FXML // fx:id="buttonOK"
    private Button buttonOK; // Value injected by FXMLLoader

    @FXML // fx:id="cbManagedLanes"
    private CheckBox cbManagedLanes; // Value injected by FXMLLoader

    
    
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the link editor controller from where this
     *               sub-window is launched.
     */
    public void setLinkEditorController(LinkEditorController ctrl) {
        linkEditorController = ctrl;
    }
    
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        listRM.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (listRM.getSelectionModel().getSelectedIndex() < 0)
                buttonOK.setDisable(true);
            else
                buttonOK.setDisable(false);
        });
    }
    
    
    
    public void initWithLink(AbstractLink lnk) {
        myLink = lnk;
        cbManagedLanes.setSelected(false);
        if (myLink.get_mng_lanes() > 0) {
            cbManagedLanes.setDisable(false);
        } else {
            cbManagedLanes.setDisable(true);
        }
        
        buttonOK.setDisable(true);
        listRM.getItems().clear();
        
        Set<control.AbstractController.Algorithm> ctrl_set = ControlFactory.get_available_ramp_metering_algorithms();
        for (control.AbstractController.Algorithm ctrl : ctrl_set) {
            listRM.getItems().add(ctrl);
        }
    }
    
    
   
    
    /***************************************************************************
     * CALLBACKS
     **************************************************************************/

    @FXML
    void onCancel(ActionEvent event) {
        linkEditorController.prepareNewRampMeter(null, cbManagedLanes.isSelected());
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onOK(ActionEvent event) {
        int idx = listRM.getSelectionModel().getSelectedIndex();
        
        if (idx < 0) {
            opt.utils.Dialogs.WarningDialog("No ramp metering algorithm selected!", "Please, choose a ramp meter from the list...");
            return;
        }
        
        linkEditorController.prepareNewRampMeter(listRM.getItems().get(idx), cbManagedLanes.isSelected());
        
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

}
