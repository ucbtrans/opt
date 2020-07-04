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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.AppMainController;
import opt.UserSettings;
import opt.data.AbstractLink;
import opt.data.ParametersRamp;
import opt.utils.ModifiedDoubleStringConverter;
import opt.utils.ModifiedIntegerStringConverter;


/**
 *
 * @author Alex Kurzhanskiy
 */
public class NewRampController {
    private AppMainController appMainController = null;
    private AbstractLink myLink = null;
    boolean is_onramp;
    private String from_name;
    private String to_name;
    
    private SpinnerValueFactory<Double> lengthSpinnerValueFactory = null;
    private SpinnerValueFactory<Integer> numLanesGPSpinnerValueFactory = null;
    private SpinnerValueFactory<Integer> numLanesManagedSpinnerValueFactory = null;
    
    
    @FXML
    private GridPane topPane;
     
    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader

    @FXML // fx:id="buttonOK"
    private Button buttonOK; // Value injected by FXMLLoader

    @FXML // fx:id="linkLength"
    private Spinner<Double> linkLength; // Value injected by FXMLLoader

    @FXML // fx:id="linkFromName"
    private TextField linkFromName; // Value injected by FXMLLoader

    @FXML // fx:id="linkToName"
    private TextField linkToName; // Value injected by FXMLLoader

    @FXML // fx:id="labelFromName"
    private Label labelFromName; // Value injected by FXMLLoader

    @FXML // fx:id="labelToName"
    private Label labelToName; // Value injected by FXMLLoader

    @FXML // fx:id="labelLength"
    private Label labelLength; // Value injected by FXMLLoader
    
    @FXML // fx:id="cbInnerRamp"
    private CheckBox cbInnerRamp; // Value injected by FXMLLoader
    
    @FXML // fx:id="numManagedLanes"
    private Spinner<Integer> numManagedLanes; // Value injected by FXMLLoader

    @FXML // fx:id="numGPLanes"
    private Spinner<Integer> numGPLanes; // Value injected by FXMLLoader
    
    
    
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
        double length_step = 1;
        if (UserSettings.unitsLength.equals("kilometers") || UserSettings.unitsLength.equals("miles"))
            length_step = 0.1;
        lengthSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, length_step);
        lengthSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        linkLength.setValueFactory(lengthSpinnerValueFactory);
        linkLength.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (Math.abs(oldValue-newValue) > 0.00001) {
                onLinkLengthChange();
            }
        });
        linkLength.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double length = UserSettings.convertLength(UserSettings.defaultRampLengthMeters, "meters", UserSettings.unitsLength);
            opt.utils.WidgetFunctionality.commitEditorText(linkLength, length);
        });
     
        numLanesGPSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1, 1);
        numLanesGPSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter());
        numGPLanes.setValueFactory(numLanesGPSpinnerValueFactory);
        numGPLanes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue)
                onNumLanesChange();
        });
        numGPLanes.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Integer num_lanes = new Integer(UserSettings.defaultOnrampGPLanes);
            if (!is_onramp)
                num_lanes = UserSettings.defaultOfframpGPLanes;
            opt.utils.WidgetFunctionality.commitEditorText(numGPLanes, num_lanes);
        });
        
        numLanesManagedSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0, 1);
        numLanesManagedSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter());
        numManagedLanes.setValueFactory(numLanesManagedSpinnerValueFactory);
        numManagedLanes.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue)
                onNumLanesChange();
        });
        numManagedLanes.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Integer num_lanes = new Integer(UserSettings.defaultOnrampManagedLanes);
            if (!is_onramp)
                num_lanes = UserSettings.defaultOfframpManagedLanes;
            opt.utils.WidgetFunctionality.commitEditorText(numManagedLanes, num_lanes);
        }); 
    }
    
    
    
    public void initWithLinkAndType(AbstractLink lnk, AbstractLink.Type rampType) {
        int maxRamps = 3;
        myLink = lnk;
        
        if (myLink == null)
            return;
        
        if (rampType == AbstractLink.Type.onramp)
            is_onramp = true;
        else
            is_onramp = false;
                    
        
        String link_name = myLink.get_name();
        String[] name_subs = link_name.split(" -> ");
        int sz = name_subs.length;
        from_name = name_subs[0];
        to_name = "";
        for (int i = 1; i < sz; i++) {
            to_name += name_subs[i];
            if (i < sz - 1)
                to_name += " -> ";
        }
        
        cbInnerRamp.setSelected(false);
        
        if (is_onramp) {
            if (from_name.equals(""))
                linkFromName.setText(to_name);
            else
                linkFromName.setText(from_name);
            labelFromName.setVisible(true);
            labelToName.setVisible(false);
            linkFromName.setVisible(true);
            linkToName.setVisible(false);
            linkToName.setText("");
            int in_ors = myLink.get_segment().num_in_ors();
            int out_ors = myLink.get_segment().num_out_ors();
            if ((in_ors >= maxRamps) || (out_ors >= maxRamps)) {
                if (out_ors >= maxRamps)
                    cbInnerRamp.setSelected(true);
                cbInnerRamp.setDisable(true);
            }   
            to_name = "";
            numLanesManagedSpinnerValueFactory.setValue(UserSettings.defaultOnrampManagedLanes);
            numLanesManagedSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter(UserSettings.defaultOnrampManagedLanes));
            numLanesGPSpinnerValueFactory.setValue(UserSettings.defaultOnrampGPLanes);
            numLanesGPSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter(UserSettings.defaultOnrampGPLanes));
        } else {
            if (to_name.equals(""))
                linkToName.setText(from_name);
            else
                linkToName.setText(to_name);
            labelFromName.setVisible(false);
            labelToName.setVisible(true);
            linkFromName.setVisible(false);
            linkToName.setVisible(true);
            linkFromName.setText("");
            int in_frs = myLink.get_segment().num_in_frs();
            int out_frs = myLink.get_segment().num_out_frs();
            if ((in_frs >= maxRamps) || (out_frs >= maxRamps)) {
                if (out_frs >= maxRamps)
                    cbInnerRamp.setSelected(true);
                cbInnerRamp.setDisable(true);
            }
            from_name = "";
            numLanesManagedSpinnerValueFactory.setValue(UserSettings.defaultOfframpManagedLanes);
            numLanesManagedSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter(UserSettings.defaultOfframpManagedLanes));
            numLanesGPSpinnerValueFactory.setValue(UserSettings.defaultOfframpGPLanes);
            numLanesGPSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter(UserSettings.defaultOfframpGPLanes));
        } 
        from_name = linkFromName.getText();
        to_name = linkToName.getText();
        
        String unitsLength = UserSettings.unitsLength;
        double length = UserSettings.defaultRampLengthMeters;
        length = UserSettings.convertLength(length, "meters", unitsLength);
        labelLength.setText("Length (" + unitsLength + "):");
        lengthSpinnerValueFactory.setValue(length);
        
    }
    
    
    
    
    
    
    
    
    
    /***************************************************************************
     * CALLBACKS
     **************************************************************************/

    private void onLinkLengthChange() {
        String unitsLength = UserSettings.unitsLength;
        double length = lengthSpinnerValueFactory.getValue();
        if (length < 0.001) {
            length = myLink.get_length_meters();
            length = UserSettings.convertLength(length, "meters", unitsLength);
            lengthSpinnerValueFactory.setValue(length);
        }
        return;
    }
    
    
    private void onNumLanesChange() { 
        int num_lanes = numLanesGPSpinnerValueFactory.getValue();
        if (num_lanes < 1) {
            num_lanes = UserSettings.defaultOnrampGPLanes;
            if (!is_onramp)
                num_lanes = UserSettings.defaultOfframpGPLanes;
            numLanesGPSpinnerValueFactory.setValue(new Integer(num_lanes));
            return;
        }
        
        num_lanes = numLanesManagedSpinnerValueFactory.getValue();
        if (num_lanes < 0) {
            num_lanes = UserSettings.defaultOnrampManagedLanes;
            if (!is_onramp)
                num_lanes = UserSettings.defaultOfframpManagedLanes;
            numLanesManagedSpinnerValueFactory.setValue(new Integer(num_lanes));
            return;
        }
    }
    
    
    @FXML
    void onCancel(ActionEvent event) {
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onOK(ActionEvent event) {
        // Ramp name
        String ramp_name = linkFromName.getText() + linkToName.getText();
        if (ramp_name.equals("")) {
            if (is_onramp)
                from_name = "On-Ramp A";
            else
                to_name = "Off-Ramp B";
            ramp_name = from_name + " -> " + to_name;
        } else {
            ramp_name = linkFromName.getText() + " -> " + linkToName.getText();
        }
        ramp_name = opt.utils.Misc.validateAndCorrectLinkName(ramp_name, myLink.get_segment().get_scenario());
        
        // Link length
        String unitsLength = UserSettings.unitsLength;
        double length = lengthSpinnerValueFactory.getValue();
        length = UserSettings.convertLength(length, unitsLength, "meters");
        length = Math.max(length, 0.001);

        ParametersRamp params = UserSettings.getDefaultOnrampParams(ramp_name,(float)length);
        params.mng_lanes = numLanesManagedSpinnerValueFactory.getValue();
        params.gp_lanes = numLanesGPSpinnerValueFactory.getValue();
        params.is_inner = cbInnerRamp.isSelected();

        if (is_onramp)
            myLink.get_segment().add_or(params);
        else
            myLink.get_segment().add_fr(params);

        appMainController.clearSimData();
        appMainController.objectNameUpdate(myLink);

        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

}
