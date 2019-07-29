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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.InputMethodEvent;



/**
 *
 * @author Alex Kurzhanskiy
 */
public class LinkEditorController {
    
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="addSectionUpstream"
    private Button addSectionUpstream; // Value injected by FXMLLoader

    @FXML // fx:id="duplicateSectionUpstream"
    private Button duplicateSectionUpstream; // Value injected by FXMLLoader

    @FXML // fx:id="duplicateSectionDownstream"
    private Button duplicateSectionDownstream; // Value injected by FXMLLoader

    @FXML // fx:id="addSectionDownstream"
    private Button addSectionDownstream; // Value injected by FXMLLoader

    @FXML // fx:id="linkName"
    private TextField linkName; // Value injected by FXMLLoader

    @FXML // fx:id="sectionProperties"
    private TitledPane sectionProperties; // Value injected by FXMLLoader

    @FXML // fx:id="trafficFlowParameters"
    private TitledPane trafficFlowParameters; // Value injected by FXMLLoader

    @FXML // fx:id="trafficDemand"
    private TitledPane trafficDemand; // Value injected by FXMLLoader

    @FXML // fx:id="trafficSplitDownstream"
    private TitledPane trafficSplitDownstream; // Value injected by FXMLLoader

    @FXML
    void onAddSectionDownstreamAction(ActionEvent event) {

    }

    @FXML
    void onAddSectionUpstreamAction(ActionEvent event) {

    }

    @FXML
    void onDuplicateSectionDownstreamAction(ActionEvent event) {

    }

    @FXML
    void onDuplicateSectionUpstreamAction(ActionEvent event) {

    }

    @FXML
    void onLinkNameChanged(InputMethodEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        
    }
    
}
