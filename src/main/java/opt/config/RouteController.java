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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private String origRouteName = null;
    private boolean ignoreChange = true;
    
    private List<Segment> allSegments = new ArrayList<Segment>();
    private Set<Segment> originCandidates = new HashSet<Segment>();
    private Set<Segment> destinationCandidates = new HashSet<Segment>();
    private List<Segment> originList = new ArrayList<Segment>();
    private List<Segment> destinationList = new ArrayList<Segment>();
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
    
    
    
    
    
    /***************************************************************************
     * Auxiliary 
     **************************************************************************/
    
    private List<List<Segment>> computeRoutes(Segment origin, Segment destination, boolean back) {
        List<List<Segment>> result = new ArrayList<List<Segment>>();
        
        
        return result;
    }
    
    private void filterOriginCadidates(List<List<Segment>> seg_lists) {
        originCandidates.clear();
        for (List<Segment> seg_list : seg_lists)
            for (Segment seg : seg_list)
                if (!originCandidates.contains(seg))
                    originCandidates.add(seg);
    }
    
    private void filterDestinationCadidates(List<List<Segment>> seg_lists) {
        destinationCandidates.clear();
        for (List<Segment> seg_list : seg_lists)
            for (Segment seg : seg_list)
                if (!destinationCandidates.contains(seg))
                    destinationCandidates.add(seg);
    }
    
    private void cbOriginUpdate() {
        cbOrigin.getItems().clear();
        originList.clear();
        
        int selected = -1;
        for (Segment seg : allSegments) 
            if (originCandidates.contains(seg)) {
                cbOrigin.getItems().add(seg.fwy().get_name());
                originList.add(seg);
                if (seg.equals(firstSegment))
                    selected = originList.size() - 1;
            }
        
        if (selected >= 0)
            cbOrigin.getSelectionModel().select(selected);
    }
    
    private void cbDestinationUpdate() {
        cbDestination.getItems().clear();
        destinationList.clear();
        
        int selected = -1;
        for (Segment seg : allSegments) 
            if (destinationCandidates.contains(seg)) {
                cbDestination.getItems().add(seg.fwy().get_name());
                destinationList.add(seg);
                if (seg.equals(lastSegment))
                    selected = destinationList.size() - 1;
            }
        
        if (selected >= 0)
            cbDestination.getSelectionModel().select(selected);
    }
    
    private void updateSectionList() {
        listSections.getItems().clear();
        for (Segment seg : routeSegments)
            listSections.getItems().add(seg.fwy().get_name() + " (" +
                opt.utils.Misc.linkType2String(seg.fwy().get_type()) + ")");
    }
    
    
    
    private List<String> getUniqueRouteDescriptions(List<List<Segment>> seg_lists) {
        List<String> uniqueDescriptions = new ArrayList<String>();
        for (List<Segment> route : seg_lists) {
            String nm = "Shortest route";
            for (Segment seg : route) {
                boolean unique = true;
                for (List<Segment> ort : seg_lists) {
                    if (ort.equals(route))
                        continue; // skip the route if it is myself
                    if (ort.contains(seg)) {
                        unique = false;
                        break;
                    }
                }
                if (unique) {
                    nm = seg.fwy().get_name();
                    break;
                }
            }
            uniqueDescriptions.add(nm);
        }
        
        return uniqueDescriptions;
    }
    
    private void setNewRoute() {
        if ((firstSegment == null) || (lastSegment == null))
            return;
        
        List<List<Segment>> seg_sequences = computeRoutes(firstSegment, lastSegment, false);
        if (seg_sequences.size() < 2) {
            routeSegments = seg_sequences.get(0);
        } else {
            List<String> choices = getUniqueRouteDescriptions(seg_sequences);
        }
        
        int sz = routeSegments.size();
        if (sz > 0) {
            firstSegment = routeSegments.get(0);
            lastSegment = routeSegments.get(sz-1);
        }
        myRoute.set_segments(routeSegments);
        updateSectionList();
    }
    
    
    
    
    
    
    /***************************************************************************
     * Initialization 
     **************************************************************************/
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        routeName.textProperty().addListener((observable, oldValue, newValue) -> {
            onRouteNameChange(null);
        });
        
        cbOrigin.valueProperty().addListener((observable, oldValue, newValue) -> {
            onOriginChange();
        });
        
        cbDestination.valueProperty().addListener((observable, oldValue, newValue) -> {
            onDestinationChange();
        });
    }
    
    
    /**
     * This function is called every time one opens a route in the
     * configuration module.
     * @param route 
     */
    public void initWithRouteData(Route route) {        
        if (route == null)
            return;
        
        ignoreChange = true;
        cbOrigin.getItems().clear();
        cbDestination.getItems().clear();
        appMainController.setLeftStatus("");
        myRoute = route;
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
        updateSectionList();
        
        allSegments.clear();
        for (List<Segment> seg_list : myRoute.get_scenario().get_linear_freeway_segments())
            for (Segment segment : seg_list)
                allSegments.add(segment);
        for (AbstractLink conn : myRoute.get_scenario().get_connectors())
            allSegments.add(conn.get_segment());
        
        if ((firstSegment == null) || (lastSegment == null)) {
            firstSegment = null;
            lastSegment = null;
            originCandidates.clear();
            destinationCandidates.clear();
            
            for (Segment seg : allSegments) {
                originCandidates.add(seg);
                destinationCandidates.add(seg);
            }
        } else {
            List<List<Segment>> fromOrigin = computeRoutes(firstSegment, null, false);
            filterDestinationCadidates(fromOrigin);
            List<List<Segment>> fromDestination = computeRoutes(null, lastSegment, true);
            filterOriginCadidates(fromDestination);
        }
        
        cbOriginUpdate();
        cbDestinationUpdate();
        
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

        appMainController.deleteRoute(myRoute);
    }
    
    void onOriginChange() {
        if (ignoreChange)
            return;
        
        ignoreChange = true;
        
        int idx = cbOrigin.getSelectionModel().getSelectedIndex();
        firstSegment = originList.get(idx);
        List<List<Segment>> fromOrigin = computeRoutes(firstSegment, null, false);
        //filterDestinationCadidates(fromOrigin);
        cbDestinationUpdate();
        //setNewRoute();
        
        ignoreChange = false;
    }
    
    void onDestinationChange() {
        if (ignoreChange)
            return;
        
        ignoreChange = true;
        
        int idx = cbDestination.getSelectionModel().getSelectedIndex();
        lastSegment = destinationList.get(idx);
        List<List<Segment>> fromDestination = computeRoutes(null, lastSegment, true);
        //filterOriginCadidates(fromDestination);
        cbOriginUpdate();
        //setNewRoute();
        
        ignoreChange = false;
    }
    
    
}
