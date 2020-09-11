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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.UserSettings;
import opt.data.Commodity;
import opt.data.FreewayScenario;
import opt.data.control.ControlSchedule;
import opt.data.control.ControllerPolicyHOVHOT;
import opt.data.control.ScheduleEntry;
import opt.utils.Misc;
import opt.utils.ModifiedDoubleStringConverter;
import opt.utils.ModifiedIntegerStringConverter;
import opt.utils.RadioButtonCell;

public class LaneControlEditorController {
    
    private ScenarioEditorController scenarioEditorController = null;
    private List<Commodity> listVT = new ArrayList<Commodity>();
    private List<Permission> listPermsVT = new ArrayList<Permission>();
    
    
    private FreewayScenario myScenario = null;
    private boolean ignoreChange = true;
    private ControlSchedule mySchedule;
    private ControllerPolicyHOVHOT myController = null;
    private float origStartTime;
    private boolean isnew;


    @FXML // fx:id="topPane"
    private GridPane topPane; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader

    @FXML // fx:id="buttonOK"
    private Button buttonOK; // Value injected by FXMLLoader

    @FXML // fx:id="textStartTime"
    private TextField textStartTime; // Value injected by FXMLLoader

    @FXML // fx:id="cbControlType"
    private ComboBox<String> cbControlType; // Value injected by FXMLLoader

    @FXML // fx:id="restrictedPane"
    private TabPane restrictedPane; // Value injected by FXMLLoader

    @FXML // fx:id="tabVT"
    private Tab tabVT; // Value injected by FXMLLoader

    @FXML // fx:id="tablePermsVT"
    private TableView<PermsVT> tablePermsVT; // Value injected by FXMLLoader

    @FXML // fx:id="colVT"
    private TableColumn<PermsVT, String> colVT; // Value injected by FXMLLoader
    
    @FXML // fx:id="colPermission"
    private TableColumn<PermsVT, Permission> colPermission; // Value injected by FXMLLoader
    
    @FXML // fx:id="controlDt"
    private Spinner<Integer> controlDt; // Value injected by FXMLLoader

    @FXML // fx:id="tabTolling"
    private Tab tabTolling; // Value injected by FXMLLoader

    @FXML // fx:id="tableFlowPrice"
    private TableView<?> tableFlowPrice; // Value injected by FXMLLoader

    @FXML // fx:id="colFlow"
    private TableColumn<?, ?> colFlow; // Value injected by FXMLLoader

    @FXML // fx:id="coplPrice"
    private TableColumn<?, ?> coplPrice; // Value injected by FXMLLoader

    @FXML // fx:id="spA0"
    private Spinner<Double> spA0; // Value injected by FXMLLoader

    @FXML // fx:id="spA1"
    private Spinner<Double> spA1; // Value injected by FXMLLoader

    @FXML // fx:id="spA2"
    private Spinner<Double> spA2; // Value injected by FXMLLoader

    @FXML // fx:id="labelSpeedThreshold"
    private Label labelSpeedThreshold; // Value injected by FXMLLoader

    @FXML // fx:id="spSpeedThreshold"
    private Spinner<Double> spSpeedThreshold; // Value injected by FXMLLoader

   
    
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the scenario editor controller from where this
     *               sub-window is launched.
     */
    public void setScenarioEditorController(ScenarioEditorController ctrl) {
        scenarioEditorController = ctrl;
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        textStartTime.setTextFormatter(opt.utils.TextFormatting.createTimeTextFormatter(Misc.seconds2timestring((float)opt.UserSettings.defaultStartTime, "")));
        
        cbControlType.getItems().clear();
        cbControlType.getItems().add("Open for All");
        cbControlType.getItems().add("Closed for All");
        cbControlType.getItems().add("Restricted");
        

        colVT.setReorderable(false);
        colVT.setSortable(false);
        colVT.setEditable(false);
        colVT.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().vt_name.getValue()));
        
        colPermission.setReorderable(false);
        colPermission.setSortable(false);
        colPermission.setEditable(true);
        colPermission.setCellFactory((TableColumn<PermsVT, Permission> param) -> {
            return new RadioButtonCell<>(EnumSet.allOf(Permission.class));
        });
        colPermission.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().permission.getValue()));
        colPermission.setOnEditCommit(new EventHandler<CellEditEvent<PermsVT, Permission>>() {
            @Override
            public void handle(CellEditEvent<PermsVT, Permission> t) {
                int row = t.getTablePosition().getRow();
                Permission p = t.getNewValue();
                ((PermsVT) t.getTableView().getItems().get(row)).setPermission(p);
                if ((row >= 0) && (row < listPermsVT.size()))
                    listPermsVT.set(row, p);
                if (countTolled() > 0)
                    tabTolling.setDisable(false);
                else
                    tabTolling.setDisable(true);
            }
        });
        
        
        SpinnerValueFactory<Integer> controlDtSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3600, 1, 1);
        controlDtSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter());
        controlDt.setValueFactory(controlDtSpinnerValueFactory);
        
        SpinnerValueFactory<Double> a0 = new SpinnerValueFactory.DoubleSpinnerValueFactory(-1000.0, 1000.0, 1.0, 1);
        a0.setConverter(new ModifiedDoubleStringConverter("#.####", 1.0));
        spA0.setValueFactory(a0);
        
        SpinnerValueFactory<Double> a1 = new SpinnerValueFactory.DoubleSpinnerValueFactory(-1000.0, 1000.0, 1.0, 1);
        a1.setConverter(new ModifiedDoubleStringConverter("#.####", 1.0));
        spA1.setValueFactory(a1);
        
        SpinnerValueFactory<Double> a2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(-1000.0, 1000.0, 1.0, 1);
        a2.setConverter(new ModifiedDoubleStringConverter("#.####", 1.0));
        spA2.setValueFactory(a2);
        
        SpinnerValueFactory<Double> v_thresh = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 120, 0.0, 1);
        v_thresh.setConverter(new ModifiedDoubleStringConverter("#.##", 0.0));
        spSpeedThreshold.setValueFactory(v_thresh);
    }
    
    
    
    /**
     * This function is called every time one opens a lane control editor in the
     * configuration module.
     * 
     * @param scenario
     * @param schedule
     * @param entry
     * @param isnew 
     */
    public void initWithScenarioAndLanePolicyData(FreewayScenario scenario, ControlSchedule schedule, ScheduleEntry entry, boolean isnew) {
        ignoreChange = true;
        
        myScenario = (FreewayScenario)scenario;
        
        Map<Long, Commodity> mapVT = myScenario.get_commodities();
        listVT.clear();
        mapVT.forEach((k, v) -> {listVT.add(v);});
        mySchedule = schedule;
        myController = (ControllerPolicyHOVHOT) entry.get_cntrl();
        this.isnew = isnew;
        origStartTime = entry.get_start_time();
        textStartTime.setText(Misc.seconds2timestring(origStartTime, ""));
        
        fillListPermsVT();
        if (countFree() == listVT.size()) {
            cbControlType.getSelectionModel().select(0);
            restrictedPane.setVisible(false);
        } else if (countBanned() == listVT.size()) {
            cbControlType.getSelectionModel().select(1);
            restrictedPane.setVisible(false);
        } else {
            cbControlType.getSelectionModel().select(2);
            restrictedPane.setVisible(true);
        }
        initPermsVT();
        initTolling();
        
        ignoreChange = false;
    }
    
    private void fillListPermsVT() {
        listPermsVT.clear();
        listVT.forEach((c) -> {
            if (myController.get_free_comms().contains(c.getId())) {
                listPermsVT.add(Permission.Free);
            } else if (myController.get_disallowed_comms().contains(c.getId())) {
                listPermsVT.add(Permission.Banned);
                
            } else {
                listPermsVT.add(Permission.Tolled);
            }
        });
    }
    
    private void initPermsVT() {
        tablePermsVT.getItems().clear();
        int sz = listVT.size();
        ObservableList<PermsVT> vt_entries = FXCollections.observableArrayList();
        for (int i = 0; i < sz; i++)
            vt_entries.add(new PermsVT(listVT.get(i).get_name(), listPermsVT.get(i)));
        tablePermsVT.getItems().addAll(vt_entries);
        tablePermsVT.refresh();
        
        if (countTolled() > 0)
            tabTolling.setDisable(false);
        else
            tabTolling.setDisable(true);
    }
    
    private void initTolling() {
        String unitsFlow = UserSettings.unitsFlow;
        double cc = UserSettings.flowConversionMap.get("vph" + unitsFlow);
        colFlow.setText("Flow per Lane (" + unitsFlow + ")");
        
        
        
        Float dt = myController.getDt();
        if (dt == null)
            dt = (float)UserSettings.defaultControlDtSeconds;
        controlDt.getValueFactory().setValue(Math.round(dt));
        
        Double a0 = myController.get_a0();
        if (a0 == null)
            a0 = UserSettings.defaultLaneChoice_A0;
        spA0.getValueFactory().setValue(a0);
        
        Double a1 = myController.get_a1();
        if (a1 == null)
            a1 = UserSettings.defaultLaneChoice_A1;
        spA1.getValueFactory().setValue(a1);
        
        Double a2 = myController.get_a2();
        if (a2 == null)
            a2 = UserSettings.defaultLaneChoice_A2;
        spA2.getValueFactory().setValue(a2);
        
        String unitsSpeed = UserSettings.unitsSpeed;
        labelSpeedThreshold.setText("QOS Speed Threshold (" + unitsSpeed + "):");
        cc = UserSettings.speedConversionMap.get("kph" + unitsSpeed);
        Double speed_threshold = myController.get_qos_speed_threshold_kph();
        if (speed_threshold == null)
            speed_threshold = UserSettings.defaultQosSpeedThresholdKph;
        spSpeedThreshold.getValueFactory().setValue(cc*speed_threshold);
    }
    
    
    
    private int countFree() {
        int count = 0;
        return listPermsVT.stream().filter((p) -> (p == Permission.Free)).map((_item) -> 1).reduce(count, Integer::sum);
    }
    
    private int countBanned() {
        int count = 0;
        return listPermsVT.stream().filter((p) -> (p == Permission.Banned)).map((_item) -> 1).reduce(count, Integer::sum);
    }
    
    private int countTolled() {
        int count = 0;
        return listPermsVT.stream().filter((p) -> (p == Permission.Tolled)).map((_item) -> 1).reduce(count, Integer::sum);
    }
    
    private void setAllPersToFree() {
        int sz = listVT.size();
        for (int i = 0; i < sz; i++)
            listPermsVT.set(i, Permission.Free);
        restrictedPane.setVisible(false);
    }
    
    private void setAllPersToBanned() {
        int sz = listVT.size();
        for (int i = 0; i < sz; i++)
            listPermsVT.set(i, Permission.Banned);
        restrictedPane.setVisible(false);
    }
    
    
    
    
   

    /***************************************************************************
     * CALLBACKS
     ***************************************************************************/
    
    @FXML
    void onControlTypeChange(ActionEvent event) {
        if (ignoreChange)
            return;
        
        int idx = cbControlType.getSelectionModel().getSelectedIndex();
        
        switch (idx) {
            case 0:
                // open for all
                setAllPersToFree();
                break;
            case 1:
                // closed for all
                setAllPersToBanned();
                break;
            default:
                // restricted
                restrictedPane.setVisible(true);
                initPermsVT();
                break;
        }

    }
    
    
    
    @FXML
    void onCancel(ActionEvent event) {
        if (ignoreChange)
            return;
        
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onOK(ActionEvent event) {
        if (ignoreChange)
            return;

        int startSeconds = Misc.timeString2Seconds(textStartTime.getText());
        
        myController.get_free_comms().clear();
        myController.get_disallowed_comms().clear();
        int sz = listVT.size();
        for (int i = 0; i < sz; i++) {
            if (listPermsVT.get(i) == Permission.Free)
                myController.get_free_comms().add(listVT.get(i).getId());
            if (listPermsVT.get(i) == Permission.Banned)
                myController.get_disallowed_comms().add(listVT.get(i).getId());
        }

        myController.setDt((float)controlDt.getValue());
        myController.set_a0(spA0.getValue());
        myController.set_a1(spA1.getValue());
        myController.set_a2(spA2.getValue());
        
        double cc = UserSettings.speedConversionMap.get(UserSettings.unitsSpeed + "kph");
        myController.set_qos_speed_threshold_kph(cc*spSpeedThreshold.getValue());
        
        mySchedule.update(startSeconds, myController);
        scenarioEditorController.setProjectModified(true);
        
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }
    
    
    
    
    
    
    
    
    
    
    public static enum Permission {
        Free,
        Banned,
        Tolled;

        public String toString() { return super.toString(); };
    }
    
    
    public static class PermsVT {
        private final SimpleStringProperty vt_name = new SimpleStringProperty();
        private final SimpleObjectProperty<LaneControlEditorController.Permission> permission = new SimpleObjectProperty<LaneControlEditorController.Permission>();

        PermsVT(String nm, LaneControlEditorController.Permission p) {
            vt_name.set(nm);
            permission.set(p);
        }

        public void setPermission(LaneControlEditorController.Permission p) {
            permission.set(p);
        }
    }

}
