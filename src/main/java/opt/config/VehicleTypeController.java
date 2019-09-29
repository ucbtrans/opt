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
    private SpinnerValueFactory<Double> numCarsSpinnerValueFactory = null;
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

    @FXML // fx:id="spinnerNumCars"
    private Spinner<Double> spinnerNumCars; // Value injected by FXMLLoader

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
        numCarsSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 1);
        numCarsSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        spinnerNumCars.setValueFactory(numCarsSpinnerValueFactory);
        spinnerNumCars.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (Math.abs(oldValue-newValue) > 0.00001) {
                onNumCarsChange();
            }
        });
        spinnerNumCars.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double dflt = new Double(1);
            opt.utils.WidgetFunctionality.commitEditorText(spinnerNumCars, dflt);
        });
    }
    
    
     public void initWithCommodityAndScenario(Commodity comm, FreewayScenario scenario) {
         myCommodity = comm;
         myScenario = scenario;
         
         vtName.setText("New Vehicle Type");
         numCarsSpinnerValueFactory.setValue(new Double(1.0));
         if (myCommodity != null) {
             vtName.setText(myCommodity.get_name());
             numCarsSpinnerValueFactory.setValue(myCommodity.get_pvequiv());
         }
     }
    
    
    
    
    
    
    
    /***************************************************************************
     * CALLBACKS
     **************************************************************************/
    
    private void onNumCarsChange() {
        double num_cars = numCarsSpinnerValueFactory.getValue();
        if (num_cars < 0.1) {
            num_cars = 1.0;
            if (myCommodity != null)
                num_cars = myCommodity.get_pvequiv();
            numCarsSpinnerValueFactory.setValue(num_cars);
        }
        return;
    }
    
    
    @FXML
    void onCancel(ActionEvent event) {
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }
    

    @FXML
    void onOK(ActionEvent event) {
        String name = vtName.getText();
        double num_cars = numCarsSpinnerValueFactory.getValue();
        num_cars = Math.min(Math.max(0.1, num_cars), 10);
        
        if (myCommodity != null) {
            if (name.equals(""))
                name = myCommodity.get_name();
            name = opt.utils.Misc.validateAndCorrectVehicleTypeName(name, myScenario, myCommodity);
            myCommodity.set_name(name);
            myCommodity.set_pvequiv((float)num_cars);
        } else {
            if (name.equals(""))
                name = "New Vehicle Type";
            name = opt.utils.Misc.validateAndCorrectVehicleTypeName(name, myScenario, myCommodity);
            myScenario.create_commodity(name, (float)num_cars);
        }
        
        appMainController.setProjectModified(true);
        
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }
    
}
