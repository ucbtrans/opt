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

import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import opt.AppMainController;
import opt.UserSettings;
import opt.data.*;
import opt.utils.ModifiedDoubleStringConverter;


/**
 *
 * @author Alex Kurzhanskiy
 */
public class RouteController {
    private Stage primaryStage = null;
    private AppMainController appMainController = null;
    private Route myRoute = null;
    private FreewayScenario myScenario = null;
    private String origRouteName = null;
    private boolean ignoreChange = true;
    
    private List<Segment> routeSegments = null;
    private Segment firstSegment = null;
    private Segment lastSegment = null;
    
    @FXML // fx:id="scenarioEditorMainPane"
    private SplitPane scenarioEditorMainPane; // Value injected by FXMLLoader

    @FXML // fx:id="canvasParent"
    private AnchorPane canvasParent; // Value injected by FXMLLoader

    @FXML // fx:id="scenarioEditorCanvas"
    private Canvas scenarioEditorCanvas; // Value injected by FXMLLoader
    
    @FXML // fx:id="deleteRoute"
    private Button deleteRoute; // Value injected by FXMLLoader

    @FXML // fx:id="routeName"
    private TextField routeName; // Value injected by FXMLLoader

    @FXML // fx:id="cbOrigin"
    private ChoiceBox<String> cbOrigin; // Value injected by FXMLLoader

    @FXML // fx:id="cbDestination"
    private ChoiceBox<String> cbDestination; // Value injected by FXMLLoader

    @FXML // fx:id="listSections"
    private ListView<String> listSections; // Value injected by FXMLLoader

    
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the main app controller that is used to sync up
     *               all sub-windows.
     */
    public void setAppMainController(AppMainController ctrl) {
        appMainController = ctrl;
    }
    
    public void setPrimaryStage(Stage s) {
        primaryStage = s;
    }
    
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        routeName.textProperty().addListener((observable, oldValue, newValue) -> {
            onRouteNameChange(null);
        });
        
    }
    
    
    /**
     * This function is called every time one opens a route in the
     * configuration module.
     * @param route 
     */
    public void initWithRouteAndScenarioData(Route route, FreewayScenario scenario) {        
        if ((route == null) || (scenario == null))
            return;
        
        ignoreChange = true;
        cbOrigin.getItems().clear();
        cbDestination.getItems().clear();
        listSections.getItems().clear();
        appMainController.setLeftStatus("");
        myRoute = route;
        myScenario = scenario;
        routeName.setText(route.getName());
        origRouteName = route.getName();
        
        routeSegments = myRoute.get_segments();
        firstSegment = null;
        lastSegment = null;
        int sz = routeSegments.size();
        if (sz > 0) {
            firstSegment = routeSegments.get(0);
            lastSegment = routeSegments.get(sz-1);
        }
        for (int i = 0; i < sz; i++) {
            listSections.getItems().add(routeSegments.get(i) + " (" +
                    opt.utils.Misc.linkType2String(routeSegments.get(i).fwy().get_type()) + ")");
        }
    
        
        
        ignoreChange = false;
    }
    
    
    
    
    /***************************************************************************
     * Callbacks 
     **************************************************************************/
    
    @FXML
    private void onRouteNameChange(ActionEvent event) {
        if (ignoreChange)
            return;
        
        String nm = routeName.getText();
        if (nm.equals(""))
            nm = origRouteName;
        
        myRoute.setName(nm);
        appMainController.objectNameUpdate(myRoute);
    }
    
    @FXML
    void onDeleteRoute(ActionEvent event) {
        if (ignoreChange)
            return;
        
        String header = "You are deleting route '" + myRoute.getName() + "'...";
        String content = "Are you sure?";
        if (!opt.utils.Dialogs.ConfirmationYesNoDialog(header, content))
            return;

        appMainController.deleteRoute(myRoute, myScenario);
    }
    
    
}
