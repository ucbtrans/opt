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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.data.AbstractLink;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;
import opt.data.LinkConnector;
import opt.data.LinkOfframp;
import opt.data.Segment;
import opt.data.event.EventLinkToggle;
import opt.utils.Misc;
import opt.utils.ModifiedIntegerStringConverter;



/**
 * Editor for events of type Off-Ramp Open/Close.
 * 
 * @author Alex Kurzhanskiy
 */
public class EventLinkOC {
    private String originalName;
    private double timeSeconds = 0.0;
    
    private EventLinkToggle myEvent = null;
    private FreewayScenario myScenario = null;
    
    private Set<AbstractLink> linksUnderEvent = new HashSet<AbstractLink>();
    private List<AbstractLink> linksOrderedUnderEvent = new ArrayList<AbstractLink>();
    private List<AbstractLink> linksFreeForEvent = new ArrayList<AbstractLink>();
    
    private ScenarioEditorController scenarioEditorController = null;

    @FXML // fx:id="topPane"
    private GridPane topPane; // Value injected by FXMLLoader

    @FXML // fx:id="txtName"
    private TextField txtName; // Value injected by FXMLLoader

    @FXML // fx:id="txtTime"
    private TextField txtTime; // Value injected by FXMLLoader

    @FXML // fx:id="cbOpenClose"
    private ComboBox<String> cbOpenClose; // Value injected by FXMLLoader

    @FXML // fx:id="lvAllLinks"
    private ListView<String> lvAllLinks; // Value injected by FXMLLoader

    @FXML // fx:id="lvEventLinks"
    private ListView<String> lvEventLinks; // Value injected by FXMLLoader

    @FXML // fx:id="addToEvent"
    private Button addToEvent; // Value injected by FXMLLoader

    @FXML // fx:id="rmoveFromEvent"
    private Button rmoveFromEvent; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader

    @FXML // fx:id="buttonOK"
    private Button buttonOK; // Value injected by FXMLLoader

    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        txtTime.setTextFormatter(opt.utils.TextFormatting.createTimeTextFormatter(Misc.seconds2timestring((float)opt.UserSettings.defaultStartTime, "")));
        txtTime.textProperty().addListener((observable, oldValue, newValue) -> {
            onActivationTimeChange();
        });
        
        cbOpenClose.getItems().clear();
        cbOpenClose.getItems().add("Open");
        cbOpenClose.getItems().add("Close");
        
        lvAllLinks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvEventLinks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the scenario editor controller from where this
     *               sub-window is launched.
     */
    public void setScenarioEditorController(ScenarioEditorController ctrl) {
        scenarioEditorController = ctrl;
    }
    
    
    
    private void initEventLinks(boolean blank) {
        lvEventLinks.getItems().clear();
        linksUnderEvent.clear();
        
        int cnt = 0;
        
        if (blank) {
            linksOrderedUnderEvent.clear();
            List<AbstractLink> links = myEvent.get_links();
            if (links != null) {
                for (AbstractLink l : links)
                    if (l instanceof LinkOfframp)
                        linksOrderedUnderEvent.add(l);
                    else
                        cnt++;
                linksUnderEvent.addAll(linksOrderedUnderEvent);
            }
        } else {
            for (AbstractLink l : linksOrderedUnderEvent)
                if (!(l instanceof LinkOfframp)) {
                    linksOrderedUnderEvent.remove(l);
                    cnt++;
                }
            linksUnderEvent.addAll(linksOrderedUnderEvent);
        }
        
        for (AbstractLink l : linksOrderedUnderEvent)
            lvEventLinks.getItems().add(l.get_name());
        
        if (cnt > 0) {
            String lt = "managed";
            String sfx = "s";
            if (cnt == 1)
                sfx = "";
            
            String header = "Removed " + cnt + " off-ramp" + sfx + " from Event";
            String content = "These sections are not in network.";
            opt.utils.Dialogs.InformationDialog(header, content);
        }
    }
    
    private void initAllLinks() {
        linksFreeForEvent.clear();
        lvAllLinks.getItems().clear();
        
        List<List<Segment>> seg_list = myScenario.get_linear_freeway_segments();
        for (List<Segment> segments : seg_list)
            for(Segment segment : segments)
                for (AbstractLink l : segment.get_links())
                    if (!linksUnderEvent.contains(l))
                        if (l instanceof LinkOfframp)
                            linksFreeForEvent.add(l);
        
        List<LinkConnector> connectors = myScenario.get_connectors();
        for (AbstractLink l : connectors)
            if (!linksUnderEvent.contains(l))
                if (l instanceof LinkOfframp)
                    linksFreeForEvent.add(l);
        
        for (AbstractLink l : linksFreeForEvent)
            lvAllLinks.getItems().add(l.get_name());
    }
    
    
       
    public void initWithScenarioAndEvent(FreewayScenario s, EventLinkToggle e) {
        myScenario = s;
        myEvent = e;
        
        originalName = e.name;
        txtName.setText(originalName);
        timeSeconds = e.timestamp;
        txtTime.setText(Misc.seconds2timestring((float)timeSeconds, ""));
        
        int idx = 0;
        if (!e.isopen)
            idx = 1;
        cbOpenClose.getSelectionModel().select(idx);
        
        initEventLinks(true);
        initAllLinks();
    }
    
       
    
    
    
    /***************************************************************************
     * 
     * CALLBACKS
     *  
     ***************************************************************************/
    
    private void onActivationTimeChange() {
        String buf = txtTime.getText();
        if (buf.indexOf(':') == -1)
            return;

        timeSeconds = Misc.timeString2Seconds(buf);
    }
    
    
    @FXML
    void onLaneGroupChange(ActionEvent event) {
        initEventLinks(false);
        initAllLinks();
    }
    
    
    @FXML
    void eventLinksOnKeyPressed(KeyEvent event) {
        int idx = lvEventLinks.getSelectionModel().getSelectedIndex();
        if ((event.getCode() == KeyCode.LEFT) ||
            (event.getCode() == KeyCode.UP) ||
            (event.getCode() == KeyCode.RIGHT) ||
            (event.getCode() == KeyCode.DOWN)) {
            if (event.getCode() == KeyCode.LEFT)
                idx--;
            if (event.getCode() == KeyCode.RIGHT)
                idx++;
        }
        if ((idx < 0) || (idx >= linksOrderedUnderEvent.size()))
            return;

        scenarioEditorController.selectLink(linksOrderedUnderEvent.get(idx));
    }

    @FXML
    void eventLinksOnMouseClicked(MouseEvent event) {
        int idx = lvEventLinks.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= linksOrderedUnderEvent.size()))
            return;

        scenarioEditorController.selectLink(linksOrderedUnderEvent.get(idx));
    }

    @FXML
    void freeLinksOnKeyPressed(KeyEvent event) {
        int idx = lvAllLinks.getSelectionModel().getSelectedIndex();
        if ((event.getCode() == KeyCode.LEFT) ||
            (event.getCode() == KeyCode.UP) ||
            (event.getCode() == KeyCode.RIGHT) ||
            (event.getCode() == KeyCode.DOWN)) {
            if (event.getCode() == KeyCode.LEFT)
                idx--;
            if (event.getCode() == KeyCode.RIGHT)
                idx++;
        }   
        if ((idx < 0) || (idx >= linksFreeForEvent.size()))
            return;
        
        scenarioEditorController.selectLink(linksFreeForEvent.get(idx));
    }

    @FXML
    void freeLinksOnMouseClicked(MouseEvent event) {
        int idx = lvAllLinks.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= linksFreeForEvent.size()))
            return;
        
        scenarioEditorController.selectLink(linksFreeForEvent.get(idx));
    }

    @FXML
    void onAddToEvent(ActionEvent event) {
        List<Integer> selected = lvAllLinks.getSelectionModel().getSelectedIndices();
        if ((selected == null) || (selected.size() < 1))
            return;
        
        for (int i : selected) {
            if ((i < 0) || (i >= linksFreeForEvent.size()))
                continue;
            linksOrderedUnderEvent.add(linksFreeForEvent.get(i));
        }
        
        initEventLinks(false);
        initAllLinks();
    }
    
    
    @FXML
    void onRmoveFromEvent(ActionEvent event) {
        List<Integer> selected = lvEventLinks.getSelectionModel().getSelectedIndices();
        if ((selected == null) || (selected.size() < 1))
            return;
        
        for (int i : selected) {
            if ((i < 0) || (i >= linksOrderedUnderEvent.size()))
                continue;
            linksUnderEvent.remove(linksOrderedUnderEvent.get(i));
        }
        
        for (AbstractLink l : linksOrderedUnderEvent)
            if (!linksUnderEvent.contains(l))
                linksOrderedUnderEvent.remove(l);

        initEventLinks(false);
        initAllLinks();
    }
    
    
    
    

    @FXML
    void onCancel(ActionEvent event) {
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

    
    @FXML
    void onOK(ActionEvent event) {
        if ((linksOrderedUnderEvent == null) || (linksOrderedUnderEvent.isEmpty())) {
            String header = "No road sections are assigned to event";
            String content = "Without road sections assigned, event won't be saved.";
            opt.utils.Dialogs.WarningDialog(header, content);
        }
        
        String name = txtName.getText();
        if ((name.isEmpty()) || (name.isBlank()))
            name = originalName;
        myEvent.name = name;
        
        myEvent.timestamp = (float)timeSeconds;
        
        scenarioEditorController.setProjectModified(true);
        
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }


}