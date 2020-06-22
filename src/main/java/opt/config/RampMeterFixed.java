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
import opt.UserSettings;
import opt.data.AbstractLink;
import opt.data.control.*;
import opt.utils.Misc;
import opt.utils.ModifiedDoubleStringConverter;
import opt.utils.ModifiedIntegerStringConverter;


/**
 * Controller for the pop-up dialog that edits FIXED RATE ramp meter.
 * 
 * @author Alex Kurzhanskiy
 */
public class RampMeterFixed {
    private LinkEditorController linkEditorController = null;
    private AbstractLink myLink = null;
    private ControlSchedule mySchedule;
    private ControllerRampMeterFixedRate myController = null;
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

    @FXML // fx:id="labelRecRate"
    private Label labelRecRate; // Value injected by FXMLLoader

    @FXML // fx:id="labelMaxRate"
    private Label labelMaxRate; // Value injected by FXMLLoader

    @FXML // fx:id="spinnerRecRate"
    private Spinner<Double> spinnerRecRate; // Value injected by FXMLLoader

    @FXML // fx:id="spinnerMaxRate"
    private Spinner<Double> spinnerMaxRate; // Value injected by FXMLLoader

    @FXML // fx:id="controlDt"
    private Spinner<Integer> controlDt; // Value injected by FXMLLoader

    @FXML // fx:id="cbQueueControl"
    private CheckBox cbQueueControl; // Value injected by FXMLLoader

    
    
    
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
        textStartTime.setTextFormatter(opt.utils.TextFormatting.createTimeTextFormatter(Misc.seconds2timestring((float)opt.UserSettings.defaultStartTime, "")));

        double cap_step = 1;
        if (UserSettings.unitsFlow.equals("vps"))
            cap_step = 0.01;
        
        SpinnerValueFactory<Double> rateSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, cap_step);
        rateSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        spinnerRecRate.setValueFactory(rateSpinnerValueFactory);
        
        rateSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(10.0, Double.MAX_VALUE, 10.0, cap_step);
        rateSpinnerValueFactory.setConverter(new ModifiedDoubleStringConverter());
        spinnerMaxRate.setValueFactory(rateSpinnerValueFactory);
        
        SpinnerValueFactory<Integer> controlDtSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3600, 1, 1);
        controlDtSpinnerValueFactory.setConverter(new ModifiedIntegerStringConverter());
        controlDt.setValueFactory(controlDtSpinnerValueFactory);
    }
    
    
    
    public void initWithLinkAndController(AbstractLink lnk, ControlSchedule schedule, ScheduleEntry entry,boolean isnew) {
        myLink = lnk;
        mySchedule = schedule;
        myController = (ControllerRampMeterFixedRate) entry.get_cntrl();
        this.isnew = isnew;

        origStartTime = entry.get_start_time();
        textStartTime.setText(Misc.seconds2timestring(origStartTime, ""));

        double rate_vphpl = myController.get_rate_vphpl();
        double max_rate = myController.getMax_rate_vph();
        
        String unitsFlow = UserSettings.unitsFlow;
        labelRecRate.setText("Metering Rate per Lane (" + unitsFlow + "):");
        labelMaxRate.setText("Queue Override Rate per Lane (" + unitsFlow + "):");
//        min_rate = UserSettings.convertFlow(min_rate, "vph", unitsFlow);
        max_rate = UserSettings.convertFlow(max_rate, "vph", unitsFlow);
        spinnerRecRate.getValueFactory().setValue(rate_vphpl);
        spinnerMaxRate.getValueFactory().setValue(max_rate);

        controlDt.getValueFactory().setValue(Math.round(myController.getDt()));
        cbQueueControl.setSelected(myController.isHas_queue_control());
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
        int startSeconds = Misc.timeString2Seconds(textStartTime.getText());
        
        double rate_vphpl = spinnerRecRate.getValue();
        double max_rate = spinnerMaxRate.getValue();
        if (rate_vphpl > max_rate) {
            opt.utils.Dialogs.ErrorDialog("Recommended rate cannot exceed maximum rate...", "Please, correct the metering rates.");
            return;
        }

        rate_vphpl = UserSettings.convertFlow(rate_vphpl, UserSettings.unitsFlow, "vph");
//        myController.setMin_rate_vph((float)(0.5*rec_rate));
        myController.set_rate_vphpl((float)rate_vphpl);
        max_rate = UserSettings.convertFlow(max_rate, UserSettings.unitsFlow, "vph");
        myController.setMax_rate_vph((float)max_rate);
        myController.setHas_queue_control(cbQueueControl.isSelected());

        mySchedule.update(startSeconds,myController);

        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }

}

