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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.AppMainController;
import opt.UserSettings;
import opt.data.AbstractLink;
import opt.data.LinkFreewayOrConnector;
import opt.data.Segment;



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
        lengthSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1);
        linkLength.setValueFactory(lengthSpinnerValueFactory);
        
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
        
        String unitsLength = appMainController.getUserSettings().unitsLength;
        double length = myLink.get_length_meters();
        length = appMainController.getUserSettings().convertLength(length, "meters", unitsLength);
        labelLength.setText("Length (" + unitsLength + "):");
        lengthSpinnerValueFactory.setValue(length);
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
        String unitsLength = appMainController.getUserSettings().unitsLength;
        double length = lengthSpinnerValueFactory.getValue();
        length = appMainController.getUserSettings().convertLength(length, unitsLength, "meters");
        length = Math.max(length, 0.001);
        
        Segment new_segment;
        String segment_name = link_name;
        UserSettings user_settings = appMainController.getUserSettings();

        // TODO AK
        // insert_XXX_segment takes parameters for a freeway link and a potential
        // ramp link. A ramp link is only created if the calling link is a connector.
        // Otherwise you can pass ramp_params=null.

        // To obtain default parameters, use e.g.
        // user_settings.getDefaultFreewayParams(fwy_name,fwy_length)

        if (downstreamLink != null) {
//            new_segment = downstreamLink.insert_up_segment(segment_name,link_name);
            new_segment = downstreamLink.insert_up_segment(segment_name,
                    XXX,
                    XXX);
        } else {
//            new_segment = upstreamLink.insert_dn_segment(segment_name,link_name);
            new_segment = upstreamLink.insert_dn_segment(segment_name,
                    XXX,
                    XXX);
        }
        LinkFreewayOrConnector new_link = new_segment.fwy();

        // TODO AK : THIS IS NO LONGER NECESSARY
        try {
            new_link.set_length_meters((float)length);
        } catch(Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Could not set new section length...", e);
        }



        appMainController.linkNameUpdate(new_link);

        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

}
