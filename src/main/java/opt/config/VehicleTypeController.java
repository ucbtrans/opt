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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.AppMainController;
import opt.data.Commodity;
import opt.data.FreewayScenario;
import opt.utils.ModifiedDoubleStringConverter;



/**
 * Controller for modal vehicle type editor.
 * @author Alex Kurzhanskiy
 */
public class VehicleTypeController {
    private AppMainController appMainController = null;
    private Commodity myCommodity = null;
    private FreewayScenario myScenario = null;
    
    
    @FXML // fx:id="topPane"
    private GridPane topPane; // Value injected by FXMLLoader

    @FXML // fx:id="labelTypeName"
    private Label labelTypeName; // Value injected by FXMLLoader

    @FXML // fx:id="vtName"
    private TextField vtName; // Value injected by FXMLLoader

    @FXML // fx:id="labelNumEquivCars"
    private Label labelNumEquivCars; // Value injected by FXMLLoader

    @FXML // fx:id="cbEClass"
    private ChoiceBox<Commodity.EmissionsClass> cbEClass; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader

    @FXML // fx:id="buttonOK"
    private Button buttonOK; // Value injected by FXMLLoader
    

    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the main app controller that is used to sync up
     *               all sub-windows.
     */
    public void setAppMainController(AppMainController ctrl) {
        appMainController = ctrl;
    }
    
    
    
    
    
    
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        cbEClass.getItems().clear();
        cbEClass.getItems().add(Commodity.EmissionsClass.Auto);
        cbEClass.getItems().add(Commodity.EmissionsClass.Truck);
        cbEClass.getItems().add(Commodity.EmissionsClass.Bus);
    }
    
    
    public void initWithCommodityAndScenario(Commodity comm, FreewayScenario scenario) {
        myCommodity = comm;
        myScenario = scenario;

        vtName.setText("New Vehicle Type");
        cbEClass.getSelectionModel().select(Commodity.EmissionsClass.Auto);
        if (myCommodity != null) {
            vtName.setText(myCommodity.get_name());
            cbEClass.getSelectionModel().select(comm.get_eclass());
        }
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

        Commodity.EmissionsClass eclass = cbEClass.getValue();
        String name = vtName.getText();
        
        if (myCommodity != null) {
            if (name.equals(""))
                name = myCommodity.get_name();
            name = opt.utils.Misc.validateAndCorrectVehicleTypeName(name, myScenario, myCommodity);
            myCommodity.set_name(name);
            myCommodity.set_class(eclass);
        } else {
            if (name.equals(""))
                name = "New Vehicle Type";
            name = opt.utils.Misc.validateAndCorrectVehicleTypeName(name, myScenario, myCommodity);
            myScenario.create_commodity(name, 1f, eclass);
        }
        
        appMainController.setProjectModified(true);
        
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }
    
}
