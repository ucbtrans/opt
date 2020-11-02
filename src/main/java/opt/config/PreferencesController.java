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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.UserSettings;
import static opt.UserSettings.reportingPeriodSeconds;
import opt.utils.Misc;
import opt.utils.ModifiedDoubleStringConverter;
import opt.utils.ModifiedIntegerStringConverter;


/**
 * Routines for setting user preferences.
 * 
 * @author akurzhan@berkeley.edu
 */
public class PreferencesController {
    private double startTimeSeconds = 0.0;
    private double simulationDurationSeconds = 28800.0;
    

    @FXML // fx:id="topPane"
    private GridPane topPane; // Value injected by FXMLLoader

    @FXML // fx:id="labelMaxCellLength"
    private Label labelMaxCellLength; // Value injected by FXMLLoader

    @FXML // fx:id="startTime"
    private TextField startTime; // Value injected by FXMLLoader

    @FXML // fx:id="simulationDuration"
    private TextField simulationDuration; // Value injected by FXMLLoader

    @FXML // fx:id="spDemandDt"
    private Spinner<Integer> spDemandDt; // Value injected by FXMLLoader

    @FXML // fx:id="spSRDt"
    private Spinner<Integer> spSRDt; // Value injected by FXMLLoader

    @FXML // fx:id="spFRFDt"
    private Spinner<Integer> spFRFDt; // Value injected by FXMLLoader

    @FXML // fx:id="spOutDt"
    private Spinner<Integer> spOutDt; // Value injected by FXMLLoader

    @FXML // fx:id="spMaxCellLength"
    private Spinner<Double> spMaxCellLength; // Value injected by FXMLLoader

    @FXML // fx:id="cbContourSpatial"
    private ComboBox<String> cbContourSpatial; // Value injected by FXMLLoader

    @FXML // fx:id="labelGPCapacity"
    private Label labelGPCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="labelManagedCapacity"
    private Label labelManagedCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="labelAuxCapacity"
    private Label labelAuxCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="labelGPFFSpeed"
    private Label labelGPFFSpeed; // Value injected by FXMLLoader

    @FXML // fx:id="labelManagedFFSpeed"
    private Label labelManagedFFSpeed; // Value injected by FXMLLoader

    @FXML // fx:id="labelAuxFFSpeed"
    private Label labelAuxFFSpeed; // Value injected by FXMLLoader

    @FXML // fx:id="labelGPJamDensity"
    private Label labelGPJamDensity; // Value injected by FXMLLoader

    @FXML // fx:id="labelManagedJamDensity"
    private Label labelManagedJamDensity; // Value injected by FXMLLoader

    @FXML // fx:id="labelAuxJamDensity"
    private Label labelAuxJamDensity; // Value injected by FXMLLoader

    @FXML // fx:id="labelSpeedThresholdDelay"
    private Label labelSpeedThresholdDelay; // Value injected by FXMLLoader

    @FXML // fx:id="spGPCapacity"
    private Spinner<Double> spGPCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="spManagedCapacity"
    private Spinner<Double> spManagedCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="spAuxCapacity"
    private Spinner<Double> spAuxCapacity; // Value injected by FXMLLoader

    @FXML // fx:id="spGPFFSpeed"
    private Spinner<Double> spGPFFSpeed; // Value injected by FXMLLoader

    @FXML // fx:id="spManagedFFSpeed"
    private Spinner<Double> spManagedFFSpeed; // Value injected by FXMLLoader

    @FXML // fx:id="spAuxFFSpeed"
    private Spinner<Double> spAuxFFSpeed; // Value injected by FXMLLoader

    @FXML // fx:id="spGPJamDensity"
    private Spinner<Double> spGPJamDensity; // Value injected by FXMLLoader

    @FXML // fx:id="spManagedJamDensity"
    private Spinner<Double> spManagedJamDensity; // Value injected by FXMLLoader

    @FXML // fx:id="spAuxJamDensity"
    private Spinner<Double> spAuxJamDensity; // Value injected by FXMLLoader

    @FXML // fx:id="spSpeedThresholdDelay"
    private Spinner<Double> spSpeedThresholdDelay; // Value injected by FXMLLoader

    @FXML // fx:id="labelA1"
    private Label labelA1; // Value injected by FXMLLoader

    @FXML // fx:id="labelA2"
    private Label labelA2; // Value injected by FXMLLoader

    @FXML // fx:id="labelQosSpeed"
    private Label labelQosSpeed; // Value injected by FXMLLoader

    @FXML // fx:id="spA0"
    private Spinner<Double> spA0; // Value injected by FXMLLoader

    @FXML // fx:id="spA1"
    private Spinner<Double> spA1; // Value injected by FXMLLoader

    @FXML // fx:id="spA2"
    private Spinner<Double> spA2; // Value injected by FXMLLoader

    @FXML // fx:id="spQosSpeed"
    private Spinner<Double> spQosSpeed; // Value injected by FXMLLoader

    @FXML // fx:id="labelMinGPRate"
    private Label labelMinGPRate; // Value injected by FXMLLoader

    @FXML // fx:id="labelMaxGPRate"
    private Label labelMaxGPRate; // Value injected by FXMLLoader

    @FXML // fx:id="labelMinManagedRate"
    private Label labelMinManagedRate; // Value injected by FXMLLoader

    @FXML // fx:id="labelMaxManagedRate"
    private Label labelMaxManagedRate; // Value injected by FXMLLoader

    @FXML // fx:id="spControlDt"
    private Spinner<Integer> spControlDt; // Value injected by FXMLLoader

    @FXML // fx:id="spMinRateGP"
    private Spinner<Double> spMinRateGP; // Value injected by FXMLLoader

    @FXML // fx:id="spMaxRateGP"
    private Spinner<Double> spMaxRateGP; // Value injected by FXMLLoader

    @FXML // fx:id="spMinRateManaged"
    private Spinner<Double> spMinRateManaged; // Value injected by FXMLLoader

    @FXML // fx:id="spMaxRateManaged"
    private Spinner<Double> spMaxRateManaged; // Value injected by FXMLLoader

    @FXML // fx:id="spQOverrideOffset"
    private Spinner<Double> spQOverrideOffset; // Value injected by FXMLLoader

    @FXML // fx:id="cbUnitsLength"
    private ComboBox<String> cbUnitsLength; // Value injected by FXMLLoader

    @FXML // fx:id="cbUnitsSpeed"
    private ComboBox<String> cbUnitsSpeed; // Value injected by FXMLLoader

    @FXML // fx:id="cbUnitsFlow"
    private ComboBox<String> cbUnitsFlow; // Value injected by FXMLLoader

    @FXML // fx:id="cbUnitsDensity"
    private ComboBox<String> cbUnitsDensity; // Value injected by FXMLLoader

    @FXML // fx:id="buttonReset"
    private Button buttonReset; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader

    @FXML // fx:id="buttonOK"
    private Button buttonOK; // Value injected by FXMLLoader

    
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        /// Scenario Parameters ////////////////////////////////////////////////
        
        startTime.setTextFormatter(opt.utils.TextFormatting.createTimeTextFormatter(Misc.seconds2timestring((float)opt.UserSettings.defaultStartTime, "")));
        startTime.textProperty().addListener((observable, oldValue, newValue) -> {
            onStartTimeChange();
        });
        
        simulationDuration.setTextFormatter(opt.utils.TextFormatting.createTimeTextFormatter(Misc.seconds2timestring((float)opt.UserSettings.defaultSimulationDuration, "")));
        simulationDuration.textProperty().addListener((observable, oldValue, newValue) -> {
            onDurationChange();
        });
        
        SpinnerValueFactory svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1440, UserSettings.defaultDemandDtMinutes, 1);
        svf.setConverter(new ModifiedIntegerStringConverter());
        spDemandDt.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1440, UserSettings.defaultSRDtMinutes, 1);
        svf.setConverter(new ModifiedIntegerStringConverter());
        spSRDt.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1440, UserSettings.defaultFRFlowDtMinutes, 1);
        svf.setConverter(new ModifiedIntegerStringConverter());
        spFRFDt.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1440, UserSettings.defaultFRFlowDtMinutes, 1);
        svf.setConverter(new ModifiedIntegerStringConverter());
        spOutDt.setValueFactory(svf);
        
        String units = UserSettings.unitsLength;
        double cc = UserSettings.lengthConversionMap.get("meters" + units);
        double minVal = cc * 100.0;
        double step = 10.0;
        if (minVal < 10) {
            step = 1;
            if (minVal < 1)
                step = 0.1;
        }
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(minVal, Double.MAX_VALUE, cc * UserSettings.defaultMaxCellLength, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spMaxCellLength.setValueFactory(svf);
        
        cbContourSpatial.getItems().add("Cell level (more granular)");
        cbContourSpatial.getItems().add("Section level (less granular)");
        
        
        /// Traffic Flow Parameters ////////////////////////////////////////////
        
        units = UserSettings.unitsFlow;
        cc = UserSettings.flowConversionMap.get("vph" + units);
        step = 10.0;
        if (units.equals("vpm"))
            step = 1;
        else if (units.equals("vps"))
            step = 0.01;
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.defaultGPLaneCapacityVph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spGPCapacity.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.defaultManagedLaneCapacityVph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spManagedCapacity.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.defaultAuxLaneCapacityVph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spAuxCapacity.setValueFactory(svf);
        
        units = UserSettings.unitsSpeed;
        step = 1.0;
        cc = UserSettings.speedConversionMap.get("kph" + units);
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.defaultGPLaneFreeFlowSpeedKph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spGPFFSpeed.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.defaultManagedLaneFreeFlowSpeedKph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spManagedFFSpeed.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.defaultAuxLaneFreeFlowSpeedKph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spAuxFFSpeed.setValueFactory(svf);
        
        cc = UserSettings.speedConversionMap.get("mph" + units);
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.defaultFreeFlowSpeedThresholdForDelayMph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spSpeedThresholdDelay.setValueFactory(svf);
        
        units = UserSettings.unitsDensity;
        cc = UserSettings.densityConversionMap.get("vpkm" + units);
        step = 1.0;
        if (units.equals("vpmtr") || units.equals("vpf"))
            step = 0.01;
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.defaultGPLaneJamDensityVpk, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spGPJamDensity.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.defaultManagedLaneJamDensityVpk, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spManagedJamDensity.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.defaultAuxLaneJamDensityVpk, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spAuxJamDensity.setValueFactory(svf);
        
        
        /// Lane Choice ////////////////////////////////////////////////////////
        
        step = 0.1;
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE, Double.MAX_VALUE, UserSettings.defaultLaneChoice_keep, step);
        svf.setConverter(new ModifiedDoubleStringConverter("#.####", 1));
        spA0.setValueFactory(svf);
        
        units = UserSettings.unitsDensity;
        cc = 1.0 / UserSettings.densityConversionMap.get("vpm" + units);
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE, Double.MAX_VALUE, cc * UserSettings.defaultLaneChoice_rhovpmplane, step);
        svf.setConverter(new ModifiedDoubleStringConverter("#.####", 1));
        spA1.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE, Double.MAX_VALUE, UserSettings.defaultLaneChoice_tollcents, step);
        svf.setConverter(new ModifiedDoubleStringConverter("#.####", 1));
        spA2.setValueFactory(svf);
        
        units = UserSettings.unitsSpeed;
        step = 1.0;
        cc = UserSettings.speedConversionMap.get("kph" + units);
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.defaultQosSpeedThresholdKph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spQosSpeed.setValueFactory(svf);
        
        
        
        /// Ramp Metering //////////////////////////////////////////////////////
        
        step = 5;
        svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 86400, (int)UserSettings.defaultControlDtSeconds, (int)step);
        svf.setConverter(new ModifiedIntegerStringConverter());
        spControlDt.setValueFactory(svf);
        
        units = UserSettings.unitsFlow;
        cc = UserSettings.flowConversionMap.get("vph" + units);
        step = 10.0;
        if (units.equals("vpm"))
            step = 1;
        else if (units.equals("vps"))
            step = 0.01;
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.minGPRampMeteringRatePerLaneVph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spMinRateGP.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.minManagedRampMeteringRatePerLaneVph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spMinRateManaged.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.maxGPRampMeteringRatePerLaneVph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spMaxRateGP.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, cc * UserSettings.maxManagedRampMeteringRatePerLaneVph, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spMaxRateManaged.setValueFactory(svf);
        
        svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, UserSettings.queueOverrideTriggerThreshold, step);
        svf.setConverter(new ModifiedDoubleStringConverter());
        spQOverrideOffset.setValueFactory(svf);
        
        
        /// Measurement Units //////////////////////////////////////////////////
        
        cbUnitsLength.getItems().addAll(UserSettings.unitsLengthOptions);
        cbUnitsSpeed.getItems().addAll(UserSettings.unitsSpeedOptions);
        cbUnitsFlow.getItems().addAll(UserSettings.unitsFlowOptions);
        cbUnitsDensity.getItems().addAll(UserSettings.unitsDensityOptions);
    }
    
    
    public void initWithLatestPreferences() {
        /// Scenario Parameters ////////////////////////////////////////////////
        
        startTimeSeconds = UserSettings.defaultStartTime;
        simulationDurationSeconds = UserSettings.defaultSimulationDuration;
        startTime.setText(Misc.seconds2timestring((float)startTimeSeconds, ""));
        simulationDuration.setText(Misc.seconds2timestring((float)simulationDurationSeconds, ""));
        
        spDemandDt.getValueFactory().setValue(UserSettings.defaultDemandDtMinutes);
        spSRDt.getValueFactory().setValue(UserSettings.defaultSRDtMinutes);
        spFRFDt.getValueFactory().setValue(UserSettings.defaultFRFlowDtMinutes);
        
        spOutDt.getValueFactory().setValue((int)(UserSettings.reportingPeriodSeconds / 60));
        
        String units = UserSettings.unitsLength;
        labelMaxCellLength.setText("Default Max Cell Length (" + units + "):");
        double cc = UserSettings.lengthConversionMap.get("meters" + units);
        spMaxCellLength.getValueFactory().setValue(cc * UserSettings.defaultMaxCellLength);
        
        cbContourSpatial.getSelectionModel().select(0);
        if (!UserSettings.contourDataPerCell)
            cbContourSpatial.getSelectionModel().select(1);
        
        
        /// Traffic Flow Parameters ////////////////////////////////////////////
        units = UserSettings.unitsFlow;
        labelGPCapacity.setText("Default General Purpose Lane Capacity (" + units + "):");
        labelManagedCapacity.setText("Default Managed Lane Capacity (" + units + "):");
        labelAuxCapacity.setText("Default Auxiliary Lane Capacity (" + units + "):");
        
        cc = UserSettings.flowConversionMap.get("vph" + units);
        spGPCapacity.getValueFactory().setValue(cc * UserSettings.defaultGPLaneCapacityVph);
        spManagedCapacity.getValueFactory().setValue(cc * UserSettings.defaultManagedLaneCapacityVph);
        spAuxCapacity.getValueFactory().setValue(cc * UserSettings.defaultAuxLaneCapacityVph);
        
        units = UserSettings.unitsSpeed;
        labelGPFFSpeed.setText("Default General Purpose Lane Free Flow Speed (" + units + "):");
        labelManagedFFSpeed.setText("Default Managed Lane Free Flow Speed (" + units + "):");
        labelAuxFFSpeed.setText("Default Auxiliary Lane Free Flow Speed (" + units + "):");
        labelSpeedThresholdDelay.setText("Speed Threshold for Delay Computation (" + units + "):");
        
        cc = UserSettings.speedConversionMap.get("kph" + units);
        spGPFFSpeed.getValueFactory().setValue(cc * UserSettings.defaultGPLaneFreeFlowSpeedKph);
        spManagedFFSpeed.getValueFactory().setValue(cc * UserSettings.defaultManagedLaneFreeFlowSpeedKph);
        spAuxFFSpeed.getValueFactory().setValue(cc * UserSettings.defaultAuxLaneFreeFlowSpeedKph);
        cc = UserSettings.speedConversionMap.get("mph" + units);
        spSpeedThresholdDelay.getValueFactory().setValue(cc * UserSettings.defaultFreeFlowSpeedThresholdForDelayMph);
        
        units = UserSettings.unitsDensity;
        labelGPJamDensity.setText("Default General Purpose Lane Jam Density (" + units + "):");
        labelManagedJamDensity.setText("Default Managed Lane Jam Density (" + units + "):");
        labelAuxJamDensity.setText("Default Auxiliary Lane Jam Density (" + units + "):");
        
        cc = UserSettings.densityConversionMap.get("vpkm" + units);
        spGPJamDensity.getValueFactory().setValue(cc * UserSettings.defaultGPLaneJamDensityVpk);
        spManagedJamDensity.getValueFactory().setValue(cc * UserSettings.defaultManagedLaneJamDensityVpk);
        spAuxJamDensity.getValueFactory().setValue(cc * UserSettings.defaultAuxLaneJamDensityVpk);
        
        
        /// Lane Choice ////////////////////////////////////////////////////////
        
        spA0.getValueFactory().setValue(UserSettings.defaultLaneChoice_keep);
        
        units = UserSettings.unitsDensity;
        labelA1.setText("Default Traffic Density Influencer (1/" + units + "):");
        
        cc = 1.0 / UserSettings.densityConversionMap.get("vpm" + units);
        spA1.getValueFactory().setValue(cc * UserSettings.defaultLaneChoice_rhovpmplane);
        
        spA2.getValueFactory().setValue(UserSettings.defaultLaneChoice_tollcents);
        
        units = UserSettings.unitsSpeed;
        labelQosSpeed.setText("Default QoS Speed Threshold for Managed Lanes (" + units + "):");
        
        cc = UserSettings.speedConversionMap.get("kph" + units);
        spQosSpeed.getValueFactory().setValue(cc * UserSettings.defaultQosSpeedThresholdKph);
        
        
        /// Ramp Metering //////////////////////////////////////////////////////
        
        spControlDt.getValueFactory().setValue((int)UserSettings.defaultControlDtSeconds);
        
        units = UserSettings.unitsFlow;
        labelMinGPRate.setText("Min Metering Rate for General Purpose Lane (" + units + "):");
        labelMinManagedRate.setText("Min Metering Rate for Managed Lane (" + units + "):");
        labelMaxGPRate.setText("Max Metering Rate for General Purpose Lane (" + units + "):");
        labelMaxManagedRate.setText("Max Metering Rate for Managed Lane (" + units + "):");
        
        cc = UserSettings.flowConversionMap.get("vph" + units);
        spMinRateGP.getValueFactory().setValue(cc * UserSettings.minGPRampMeteringRatePerLaneVph);
        spMinRateManaged.getValueFactory().setValue(cc * UserSettings.minManagedRampMeteringRatePerLaneVph);
        spMaxRateGP.getValueFactory().setValue(cc * UserSettings.maxGPRampMeteringRatePerLaneVph);
        spMaxRateManaged.getValueFactory().setValue(cc * UserSettings.maxManagedRampMeteringRatePerLaneVph);
        
        spQOverrideOffset.getValueFactory().setValue(UserSettings.queueOverrideTriggerThreshold);
        
        
        /// Measurement Units //////////////////////////////////////////////////
        
        cbUnitsLength.getSelectionModel().select(UserSettings.unitsLength);
        cbUnitsSpeed.getSelectionModel().select(UserSettings.unitsSpeed);
        cbUnitsFlow.getSelectionModel().select(UserSettings.unitsFlow);
        cbUnitsDensity.getSelectionModel().select(UserSettings.unitsDensity);
    }
    
    
    
    
    
    
    
    
    
    
    /***************************************************************************
     * CALLBACKS
     ***************************************************************************/

    @FXML
    void onReset(ActionEvent event) {
        /// Scenario Parameters ////////////////////////////////////////////////
        
        startTimeSeconds = 0.0;
        simulationDurationSeconds = 28800.0;
        startTime.setText(Misc.seconds2timestring((float)startTimeSeconds, ""));
        simulationDuration.setText(Misc.seconds2timestring((float)simulationDurationSeconds, ""));
        
        spDemandDt.getValueFactory().setValue(5);
        spSRDt.getValueFactory().setValue(5);
        spFRFDt.getValueFactory().setValue(5);
        spOutDt.getValueFactory().setValue(5);
        
        String units = UserSettings.unitsLength;
        double cc = UserSettings.lengthConversionMap.get("meters" + units);
        spMaxCellLength.getValueFactory().setValue(cc * 200.0);
        cbContourSpatial.getSelectionModel().select(0);
        
        
        /// Traffic Flow Parameters ////////////////////////////////////////////
        
        units = UserSettings.unitsFlow;
        cc = UserSettings.flowConversionMap.get("vph" + units);
        spGPCapacity.getValueFactory().setValue(cc * 1900);
        spManagedCapacity.getValueFactory().setValue(cc * 1800);
        spAuxCapacity.getValueFactory().setValue(cc * 950);
        
        units = UserSettings.unitsSpeed;
        cc = UserSettings.speedConversionMap.get("kph" + units);
        spGPFFSpeed.getValueFactory().setValue(cc * 105);
        spManagedFFSpeed.getValueFactory().setValue(cc * 115);
        spAuxFFSpeed.getValueFactory().setValue(cc * 90);
        cc = UserSettings.speedConversionMap.get("mph" + units);
        spSpeedThresholdDelay.getValueFactory().setValue(cc * 45);
        
        units = UserSettings.unitsDensity;        
        cc = UserSettings.densityConversionMap.get("vpkm" + units);
        spGPJamDensity.getValueFactory().setValue(cc * 110);
        spManagedJamDensity.getValueFactory().setValue(cc * 110);
        spAuxJamDensity.getValueFactory().setValue(cc * 110);
        

        /// Lane Choice ////////////////////////////////////////////////////////
        
        spA0.getValueFactory().setValue(1.8);
        
        units = UserSettings.unitsDensity;
        cc = 1.0 / UserSettings.densityConversionMap.get("vpm" + units);
        spA1.getValueFactory().setValue(cc * 0.0115);
        
        spA2.getValueFactory().setValue(0.0053);
        
        units = UserSettings.unitsSpeed;
        cc = UserSettings.speedConversionMap.get("kph" + units);
        spQosSpeed.getValueFactory().setValue(cc * 72.4205);
        
        
        /// Ramp Metering //////////////////////////////////////////////////////
        
        spControlDt.getValueFactory().setValue(30);
        
        spMinRateGP.getValueFactory().setValue(cc * 160);
        spMinRateManaged.getValueFactory().setValue(cc * 320);
        spMaxRateGP.getValueFactory().setValue(cc * 1200);
        spMaxRateManaged.getValueFactory().setValue(cc * 1400);
        
        spQOverrideOffset.getValueFactory().setValue(0.2);
        
        
        /// Measurement Units //////////////////////////////////////////////////
        
        cbUnitsLength.getSelectionModel().select(UserSettings.unitsLengthOptions[3]);
        cbUnitsSpeed.getSelectionModel().select(UserSettings.unitsSpeedOptions[2]);
        cbUnitsFlow.getSelectionModel().select(UserSettings.unitsFlowOptions[4]);
        cbUnitsDensity.getSelectionModel().select(UserSettings.unitsDensityOptions[2]);

    }

    
    @FXML
    void onCancel(ActionEvent event) {
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }
    

    @FXML
    void onOK(ActionEvent event) {
        /// Scenario Parameters ////////////////////////////////////////////////
        
        UserSettings.defaultStartTime = startTimeSeconds;
        UserSettings.defaultSimulationDuration = simulationDurationSeconds;
        
        UserSettings.defaultDemandDtMinutes = spDemandDt.getValue();
        UserSettings.defaultSRDtMinutes = spSRDt.getValue();
        UserSettings.defaultFRFlowDtMinutes = spFRFDt.getValue();
        UserSettings.reportingPeriodSeconds = 60 * spOutDt.getValue();
        
        UserSettings.defaultMaxCellLength = (float)UserSettings.convertLength(spMaxCellLength.getValue(), UserSettings.unitsLength, "meters");
        
        UserSettings.contourDataPerCell = cbContourSpatial.getSelectionModel().getSelectedIndex() == 0;
        
        
        /// Traffic Flow Parameters ////////////////////////////////////////////
        
        String units = UserSettings.unitsFlow;
        double cc = UserSettings.flowConversionMap.get(units + "vph");
        UserSettings.defaultGPLaneCapacityVph = spGPCapacity.getValue();
        UserSettings.defaultManagedLaneCapacityVph = cc * spManagedCapacity.getValue();
        UserSettings.defaultAuxLaneCapacityVph = cc * spAuxCapacity.getValue();
        
        units = UserSettings.unitsSpeed;
        cc = UserSettings.speedConversionMap.get(units + "kph");
        UserSettings.defaultGPLaneFreeFlowSpeedKph = cc * spGPFFSpeed.getValue();
        UserSettings.defaultManagedLaneFreeFlowSpeedKph = cc * spManagedFFSpeed.getValue();
        UserSettings.defaultAuxLaneFreeFlowSpeedKph = cc * spAuxFFSpeed.getValue();
        cc = UserSettings.speedConversionMap.get(units + "mph");
        UserSettings.defaultFreeFlowSpeedThresholdForDelayMph = cc * spSpeedThresholdDelay.getValue();
        
        units = UserSettings.unitsDensity;
        cc = UserSettings.densityConversionMap.get(units + "vpkm");
        UserSettings.defaultGPLaneJamDensityVpk = cc * spGPJamDensity.getValue();
        UserSettings.defaultManagedLaneJamDensityVpk = cc * spManagedJamDensity.getValue();
        UserSettings.defaultAuxLaneJamDensityVpk = cc * spAuxJamDensity.getValue();
        
        
        /// Lane Choice ////////////////////////////////////////////////////////
        
        UserSettings.defaultLaneChoice_keep = spA0.getValue();
        
        units = UserSettings.unitsDensity;
        cc = 1.0 / UserSettings.densityConversionMap.get(units + "vpm");
        UserSettings.defaultLaneChoice_rhovpmplane = cc * spA1.getValue();
        
        UserSettings.defaultLaneChoice_tollcents = spA2.getValue();
        
        units = UserSettings.unitsSpeed;
        cc = UserSettings.speedConversionMap.get(units + "kph");
        UserSettings.defaultQosSpeedThresholdKph = cc * spQosSpeed.getValue();
        
        
        /// Ramp Metering //////////////////////////////////////////////////////
        
        UserSettings.defaultControlDtSeconds = spControlDt.getValue();
        
        units = UserSettings.unitsFlow;
        cc = UserSettings.flowConversionMap.get(units + "vph");
        UserSettings.minGPRampMeteringRatePerLaneVph = cc * spMinRateGP.getValue();
        UserSettings.minManagedRampMeteringRatePerLaneVph = cc * spMinRateManaged.getValue();
        UserSettings.maxGPRampMeteringRatePerLaneVph = cc * spMaxRateGP.getValue();
        UserSettings.maxManagedRampMeteringRatePerLaneVph = cc * spMaxRateManaged.getValue();
        
        UserSettings.queueOverrideTriggerThreshold = spQOverrideOffset.getValue();
        
        
        /// Measurement Units //////////////////////////////////////////////////
        
        UserSettings.unitsLength = cbUnitsLength.getSelectionModel().getSelectedItem();
        UserSettings.unitsSpeed = cbUnitsSpeed.getSelectionModel().getSelectedItem();
        UserSettings.unitsFlow = cbUnitsFlow.getSelectionModel().getSelectedItem();
        UserSettings.unitsDensity = cbUnitsDensity.getSelectionModel().getSelectedItem();
        
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }
    
    
    
    private void onStartTimeChange() {
        String buf = startTime.getText();
        if (buf.indexOf(':') == -1)
            return;

        startTimeSeconds = Misc.timeString2Seconds(buf);
    }
    
    
    private void onDurationChange() {
        String buf = simulationDuration.getText();
        if (buf.indexOf(':') == -1)
            return;

        simulationDurationSeconds = Misc.timeString2Seconds(buf);
    }


}
