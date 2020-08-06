package opt.config;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.data.AbstractLink;
import opt.data.control.ControlSchedule;
import opt.data.control.ControllerRampMeterOpen;
import opt.data.control.ScheduleEntry;
import opt.utils.Misc;

public class RampMeterOpen {

    private LinkEditorController linkEditorController = null;
    private AbstractLink myLink = null;
    private ControlSchedule mySchedule;
    private ControllerRampMeterOpen myController = null;
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
    }

    public void initWithLinkAndController(AbstractLink lnk, ControlSchedule schedule, ScheduleEntry entry, boolean isnew) {
        myLink = lnk;
        mySchedule = schedule;
        myController = (ControllerRampMeterOpen) entry.get_cntrl();
        this.isnew = isnew;
        origStartTime = entry.get_start_time();
        textStartTime.setText(Misc.seconds2timestring(origStartTime, ""));
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

        mySchedule.update(startSeconds,myController);
        
        linkEditorController.setProjectModified(true);

        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }


}
