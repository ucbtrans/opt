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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.data.AbstractLink;
import opt.data.FDparams;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;
import opt.data.LinkConnector;
import opt.data.Segment;
import opt.data.event.EventLanegroupFD;
import opt.utils.Misc;
import opt.utils.ModifiedDoubleStringConverter;



/**
 * Editor for events of type Fundamental Diagram.
 * 
 * @author Alex Kurzhanskiy
 */
public class EventFD {
    private String originalName;
    private double timeSeconds = 0.0;
    
    private EventLanegroupFD myEvent = null;
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

    @FXML // fx:id="cbLaneGroup"
    private ComboBox<String> cbLaneGroup; // Value injected by FXMLLoader

    @FXML // fx:id="cbReset"
    private CheckBox cbReset; // Value injected by FXMLLoader

    @FXML // fx:id="lblV"
    private Label lblV; // Value injected by FXMLLoader

    @FXML // fx:id="spV"
    private Spinner<Double> spV; // Value injected by FXMLLoader

    @FXML // fx:id="lblF"
    private Label lblF; // Value injected by FXMLLoader

    @FXML // fx:id="spF"
    private Spinner<Double> spF; // Value injected by FXMLLoader

    @FXML // fx:id="lblJD"
    private Label lblJD; // Value injected by FXMLLoader

    @FXML // fx:id="spJD"
    private Spinner<Double> spJD; // Value injected by FXMLLoader

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
        
        cbLaneGroup.getItems().clear();
        cbLaneGroup.getItems().add("GP Lane");
        cbLaneGroup.getItems().add("Managed Lane");
        cbLaneGroup.getItems().add("Auxiliary Lane");

        SpinnerValueFactory svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1.5, 1, 0.1);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spV.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1.5, 1, 0.1);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spF.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1.5, 1, 0.1);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spJD.setValueFactory(svf);
        
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
        
        int lg = Math.max(0, cbLaneGroup.getSelectionModel().getSelectedIndex());
        int cnt = 0;
        
        if (blank) {
            linksOrderedUnderEvent.clear();
            List<AbstractLink> links = myEvent.get_links();
            if (links != null) {
                for (AbstractLink l : links)
                    if ((lg == 0) || ((lg == 1) && (l.has_mng())) || ((lg == 2) && (l.has_aux())))
                        linksOrderedUnderEvent.add(l);
                    else
                        cnt++;
                linksUnderEvent.addAll(linksOrderedUnderEvent);
            }
        } else {
            for (AbstractLink l : linksOrderedUnderEvent)
                if (!((lg == 0) || ((lg == 1) && (l.has_mng())) || ((lg == 2) && (l.has_aux())))) {
                    linksOrderedUnderEvent.remove(l);
                    cnt++;
                }
            linksUnderEvent.addAll(linksOrderedUnderEvent);
        }
        
        for (AbstractLink l : linksOrderedUnderEvent)
            lvEventLinks.getItems().add(l.get_name());
        
        if (cnt > 0) {
            String lt = "managed";
            if (lg == 2)
                lt = "auxiliary";
            String sfx = "s";
            String prfx = "They have";
            if (cnt == 1) {
                sfx = "";
                prfx = "It has";
            }
            String header = "Removed " + cnt + " section" + sfx + " from Event";
            String content = prfx + " no " + lt + " lanes.";
            opt.utils.Dialogs.InformationDialog(header, content);
        }
    }
    
    private void initAllLinks() {
        linksFreeForEvent.clear();
        lvAllLinks.getItems().clear();
        
        int lg = Math.max(0, cbLaneGroup.getSelectionModel().getSelectedIndex());
        
        List<List<Segment>> seg_list = myScenario.get_linear_freeway_segments();
        for (List<Segment> segments : seg_list)
            for(Segment segment : segments)
                for (AbstractLink l : segment.get_links())
                    if (!linksUnderEvent.contains(l))
                        if ((lg == 0) || ((lg == 1) && (l.has_mng())) || ((lg == 2) && (l.has_aux())))
                            linksFreeForEvent.add(l);
        
        List<LinkConnector> connectors = myScenario.get_connectors();
        for (AbstractLink l : connectors)
            if (!linksUnderEvent.contains(l))
                if ((lg == 0) || ((lg == 1) && (l.has_mng())) || ((lg == 2) && (l.has_aux())))
                    linksFreeForEvent.add(l);
        
        for (AbstractLink l : linksFreeForEvent)
            lvAllLinks.getItems().add(l.get_name());
    }
    
    
       
    public void initWithScenarioAndEvent(FreewayScenario s, EventLanegroupFD e) {
        myScenario = s;
        myEvent = e;
        
        originalName = e.name;
        txtName.setText(originalName);
        timeSeconds = e.timestamp;
        txtTime.setText(Misc.seconds2timestring((float)timeSeconds, ""));
        
        LaneGroupType lgt = e.get_lgtype();
        int idx = 0;
        if (lgt == LaneGroupType.mng)
            idx = 1;
        else if (lgt == LaneGroupType.aux)
            idx = 2;
        
        cbLaneGroup.getSelectionModel().select(idx);
        
        FDparams fdMult = e.get_fd_mult();
        cbReset.setSelected(fdMult == null);
        onResetToggle(null);
        
        if (fdMult == null) {
            spV.getValueFactory().setValue(1d);
            spF.getValueFactory().setValue(1d);
            spJD.getValueFactory().setValue(1d);
        } else {
            spV.getValueFactory().setValue((double)fdMult.ff_speed_kph);
            spF.getValueFactory().setValue((double)fdMult.capacity_vphpl);
            spJD.getValueFactory().setValue((double)fdMult.jam_density_vpkpl);
        }
        
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
    void onResetToggle(ActionEvent event) {
        boolean v = false;
        if (!cbReset.isSelected())
            v = true;

        lblV.setVisible(v);
        lblF.setVisible(v);
        lblJD.setVisible(v);
        spV.setVisible(v);
        spF.setVisible(v);
        spJD.setVisible(v);
    }
    
    @FXML
    void onLaneGroupChange(ActionEvent event) {
        initEventLinks(false);
        initAllLinks();
    }
    
    
    @FXML
    void eventLinksOnKeyPressed(KeyEvent event) {

    }

    @FXML
    void eventLinksOnMouseClicked(MouseEvent event) {

    }

    @FXML
    void freeLinksOnKeyPressed(KeyEvent event) {

    }

    @FXML
    void freeLinksOnMouseClicked(MouseEvent event) {

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
        
        myEvent.set_links(new ArrayList<>());
        LaneGroupType[] lgt = {LaneGroupType.gp, LaneGroupType.mng, LaneGroupType.aux};
        int idx = Math.max(0, cbLaneGroup.getSelectionModel().getSelectedIndex());
        try {
            myEvent.set_lgtype(lgt[idx]);
        } catch(Exception ex) {
            opt.utils.Dialogs.ExceptionDialog("Error modifying event", ex);
        }
        
        if (cbReset.isSelected()) {
            myEvent.set_fdmult_to_null();
        } else {
            double v = Math.max(0, Math.min(1.5, spV.getValue()));
            myEvent.set_ffspeed_mult((float)v);
            v = Math.max(0, Math.min(1.5, spF.getValue()));
            myEvent.set_capacity_mult((float)v);
            v = Math.max(0, Math.min(1.5, spJD.getValue()));
            myEvent.set_jamdensity_mult((float)v);
        }
        
        myEvent.set_links(new ArrayList<>(linksOrderedUnderEvent));
        
        scenarioEditorController.setProjectModified(true);
        
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }


}