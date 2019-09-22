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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.AppMainController;
import opt.UserSettings;
import opt.data.*;


/**
 *
 * @author Alex Kurzhanskiy
 */
public class NewLinkController {
    private AppMainController appMainController = null;
    private AbstractLink upstreamLink = null;
    private AbstractLink downstreamLink = null;
    private AbstractLink myLink = null;
    private String from_name;
    private String to_name;
    
    private SpinnerValueFactory<Double> lengthSpinnerValueFactory = null;
    
    
    @FXML
    private GridPane topPane;
     
    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader

    @FXML // fx:id="buttonOK"
    private Button buttonOK; // Value injected by FXMLLoader

    @FXML // fx:id="createOption"
    private ChoiceBox<String> createOption; // Value injected by FXMLLoader
    
    @FXML // fx:id="cbInner"
    private CheckBox cbInner; // Value injected by FXMLLoader

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
    
    @FXML // fx:id="labelCreateOption"
    private Label labelCreateOption; // Value injected by FXMLLoader
    
    
    
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
        linkLength.setValueFactory(lengthSpinnerValueFactory);
        
        linkLength.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                return;
            Double length = new Double(1);
            if (myLink != null) {
                length = new Double(myLink.get_length_meters());
                length = UserSettings.convertLength(length, "meters", UserSettings.unitsLength);
            }
            opt.utils.WidgetFunctionality.commitEditorText(linkLength, length);
        });
        
        createOption.getItems().clear();
        createOption.getItems().add("Copy from Current Section");
        createOption.getItems().add("Use Default Settings");
    }
    
    
    public void initWithTwoLinks(AbstractLink upLink, AbstractLink downLink) {
        upstreamLink = upLink;
        downstreamLink = downLink;
        
        if (upstreamLink != null)
            myLink = upstreamLink;
        else
            myLink = downstreamLink;
        
        if (myLink == null)
            return;
        
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
        if (myLink.get_type() == AbstractLink.Type.onramp) {
            if (from_name.equals(""))
                linkFromName.setText(to_name);
            else
                linkFromName.setText(from_name);
            linkToName.setText("");
        } else if (myLink.get_type() == AbstractLink.Type.offramp) {
            if (to_name.equals(""))
                linkToName.setText(from_name);
            else
                linkToName.setText(to_name);
            linkFromName.setText("");
        } else if (myLink.get_type() == AbstractLink.Type.connector) {
            if (upstreamLink != null) {
                linkFromName.setText(from_name);
                linkToName.setText("");
            } else {
                linkToName.setText(to_name);
                linkFromName.setText("");
            }     
        } else {
            if (upstreamLink != null) {
                linkFromName.setText(to_name);
                linkToName.setText("");
            } else {
                linkToName.setText(from_name);
                linkFromName.setText("");
            }     
        }
        from_name = linkFromName.getText();
        to_name = linkToName.getText();
        
        cbInner.setSelected(false);
        if (myLink.get_type() == AbstractLink.Type.connector)
            cbInner.setVisible(true);
        else
            cbInner.setVisible(false);
        
        String unitsLength = UserSettings.unitsLength;
        double length = myLink.get_length_meters();
        length = UserSettings.convertLength(length, "meters", unitsLength);
        labelLength.setText("Length (" + unitsLength + "):");
        lengthSpinnerValueFactory.setValue(length);
        
        if (myLink.get_type() == AbstractLink.Type.freeway) {
            createOption.setVisible(true);
            createOption.getSelectionModel().select(0);
            labelCreateOption.setVisible(true);
        } else {
            createOption.setVisible(false);
            labelCreateOption.setVisible(false);
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
        // Link name
        String link_name = linkFromName.getText() + linkToName.getText();
        if (link_name.equals("")) {
            if (from_name.equals(""))
                from_name = "A";
            if (to_name.equals(""))
                to_name = "B";
            link_name = from_name + " -> " + to_name;
        } else {
            link_name = linkFromName.getText() + " -> " + linkToName.getText();
        }
        link_name = opt.utils.Misc.validateAndCorrectLinkName(link_name, myLink.get_segment().get_scenario());
        
        // Link length
        String unitsLength = UserSettings.unitsLength;
        double length = lengthSpinnerValueFactory.getValue();
        length = UserSettings.convertLength(length, unitsLength, "meters");
        length = Math.max(length, 0.001);
        
        boolean is_inner = cbInner.isSelected();
        
        Segment new_segment;
        String segment_name = link_name;
        
        ParametersFreeway fwyParams = null;
        ParametersRamp rmpParams = null;
        
        if ((myLink.get_type() == AbstractLink.Type.connector) ||
            (myLink.get_type() == AbstractLink.Type.freeway)) {
            // We're creating a freeway section
            fwyParams = opt.UserSettings.getDefaultFreewayParams(link_name, (float)length);
        } else {
            // We're creating a connector section
            fwyParams = opt.UserSettings.getDefaultConnectorParams(link_name, (float)length);
        }

        if (downstreamLink != null) {
            if (downstreamLink.get_type() == AbstractLink.Type.connector) {
                String fr_name = to_name;
                if (fr_name.equals(""))
                    fr_name = "B";
                fr_name = " -> " + fr_name;
                fr_name = opt.utils.Misc.validateAndCorrectLinkName(fr_name, myLink.get_segment().get_scenario());
                rmpParams = opt.UserSettings.getDefaultOfframpParams(fr_name, (float)opt.UserSettings.defaultRampLengthMeters);
                rmpParams.set_is_inner(is_inner);
            }
            new_segment = downstreamLink.insert_up_segment(segment_name, fwyParams, rmpParams);
            if (downstreamLink.get_type() == AbstractLink.Type.connector) {
                AbstractLink fr;
                if (is_inner)
                    fr = new_segment.in_frs(0);
                else
                    fr = new_segment.out_frs(0);
                fr.set_gp_lanes(myLink.get_gp_lanes());
                fr.set_managed_lanes(myLink.get_managed_lanes());
                fr.set_barrier(myLink.get_barrier());
                fr.set_separated(myLink.get_separated());
            }
        } else {
            if (upstreamLink.get_type() == AbstractLink.Type.connector) {
                String or_name = to_name;
                if (or_name.equals(""))
                    or_name = "A";
                or_name += " -> ";
                or_name = opt.utils.Misc.validateAndCorrectLinkName(or_name, myLink.get_segment().get_scenario());
                rmpParams = opt.UserSettings.getDefaultOnrampParams(or_name, (float)opt.UserSettings.defaultRampLengthMeters);
                rmpParams.set_is_inner(is_inner);
            }
            new_segment = upstreamLink.insert_dn_segment(segment_name, fwyParams, rmpParams);
            if (upstreamLink.get_type() == AbstractLink.Type.connector) {
                AbstractLink or = null;
                if (is_inner)
                    or = new_segment.in_ors(0);
                else
                    or = new_segment.out_ors(0);
                or.set_gp_lanes(myLink.get_gp_lanes());
                or.set_managed_lanes(myLink.get_managed_lanes());
                or.set_barrier(myLink.get_barrier());
                or.set_separated(myLink.get_separated());
            }
        }
        LinkFreewayOrConnector new_link = new_segment.fwy();
        if (myLink.get_type() == AbstractLink.Type.freeway) {
            int crOpt = createOption.getSelectionModel().getSelectedIndex();
            if (crOpt == 0) {
                new_link.set_gp_lanes(myLink.get_gp_lanes());
                new_link.set_managed_lanes(myLink.get_managed_lanes());
                new_link.set_barrier(myLink.get_barrier());
                new_link.set_separated(myLink.get_separated());
            }
        } else if ((myLink.get_type() == AbstractLink.Type.onramp) || (myLink.get_type() == AbstractLink.Type.offramp)) {
            new_link.set_gp_lanes(myLink.get_gp_lanes());
            new_link.set_managed_lanes(myLink.get_managed_lanes());
            new_link.set_barrier(myLink.get_barrier());
            new_link.set_separated(myLink.get_separated());
        }
        appMainController.objectNameUpdate(new_link);

        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    } 

}
