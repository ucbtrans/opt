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
package opt;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.data.FreewayScenario;




public class SimulationController {
    
    private OTMTask taskOTM = null;
    

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    
    @FXML // fx:id="topPane"
    private GridPane topPane; // Value injected by FXMLLoader

    @FXML // fx:id="labelProgress"
    private Label labelProgress; // Value injected by FXMLLoader
    
    @FXML // fx:id="barProgress"
    private ProgressBar barProgress; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader

    

    

    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        barProgress.progressProperty().addListener((ov, oldValue, newValue) -> {
            int progress = (int)Math.max(0, 100 * barProgress.getProgress());
            labelProgress.setText("" + progress + " %");
        });
    }
    
    
    public void initWithScenarioData(AppMainController c, FreewayScenario s) {
        try {
            // Set the number of divisions of the progress bar
            int progbar_steps = 50;
            boolean celloutput = opt.UserSettings.contourDataPerCell;
            taskOTM = new OTMTask(c, s, (float)UserSettings.reportingPeriodSeconds, progbar_steps, celloutput, !celloutput, null);

            Thread th = new Thread(taskOTM);
            th.setDaemon(true);
            th.start();
        }
        catch(Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Error running OPT project", e);
            Stage stage = (Stage) topPane.getScene().getWindow();
            stage.close();
        }
    }
    
    
    
    public void bindProgressBar(ReadOnlyDoubleProperty prop) {
        barProgress.progressProperty().bind(prop);
    }

    public void unbindProgressBar() {
        barProgress.progressProperty().unbind();
        
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }
    
    
    
    
    
    
    
    @FXML
    void cancelSimulation(ActionEvent event) {
        taskOTM.cancel();
        //unbindProgressBar();
    }
    
}
