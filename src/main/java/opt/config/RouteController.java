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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
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
    private RouteChoiceController routeChoiceController = null;
    private Scene routeChoiceScene = null;
    private Route myRoute = null;
    private String origRouteName = null;
    private boolean ignoreChange = true;
    private Set<Segment> visited = new HashSet<Segment>();
    private int selectedRouteIndex = -1;
    
    private List<Segment> allSegments = new ArrayList<Segment>();
    private Set<Segment> originCandidates = new HashSet<Segment>();
    private Set<Segment> destinationCandidates = new HashSet<Segment>();
    private List<Segment> originList = new ArrayList<Segment>();
    private List<Segment> destinationList = new ArrayList<Segment>();
    private List<Segment> routeSegments = null;
    private Segment firstSegment = null;
    private Segment lastSegment = null;
    
    private RouteDisplay routeDisplay = null;
    
    
    @FXML // fx:id="scenarioEditorMainPane"
    private SplitPane scenarioEditorMainPane; // Value injected by FXMLLoader

    @FXML // fx:id="canvasParent"
    private AnchorPane canvasParent; // Value injected by FXMLLoader

    @FXML // fx:id="routeEditorCanvas"
    private Canvas routeEditorCanvas; // Value injected by FXMLLoader
    
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
    
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the route choice controller that is used to choose
     *               between available routes.
     */
    public void setRouteChoiceControllerAndScene(RouteChoiceController ctrl, Scene scn) {
        routeChoiceController = ctrl;
        routeChoiceScene = scn;
        routeChoiceScene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
        routeChoiceController.setRouteController(this);
    }
    
    
    public void setSelectedRouteIndex(int idx) {
        selectedRouteIndex = idx;
    }
    
    
    
    
    /***************************************************************************
     * Auxiliary 
     **************************************************************************/
    
    private List<List<Segment>> launchDFT(Segment origin, Segment destination, boolean back) {
        List<List<Segment>> result = new ArrayList<List<Segment>>();
        List<Segment> sub = new ArrayList<Segment>();
        Segment seg, target;
        Set<Segment> children;
        
        if (!back) { // going forward starting from the origin
            if (origin == null) // nothing to start with, hence return an empty list of lists
                return result;
            seg = origin;
            target = destination;
            children = seg.get_dnstrm_segments();
        } else { // going backward starting from the destination
            if (destination == null) // nothing to start with, hence return an empty list of lists
                return result;
            seg = destination;
            target = origin;
            children = seg.get_upstrm_segments();
        }
        
        int sz = children.size();
        
        while ((sz == 1) && (!visited.contains(seg)) && (!seg.equals(target))) { // traverse the single available path
            sub.add(seg);
            visited.add(seg);
            seg = children.iterator().next();
            
            if (!back)
                children = seg.get_dnstrm_segments();
            else
                children = seg.get_upstrm_segments();
            sz = children.size();
        }
        
        if (seg.equals(target)) {
            /*if (!visited.contains(seg)) {
                visited.add(seg);
            }*/
            sub.add(seg);
            
            for (Segment s : sub)
                if (visited.contains(s))
                    visited.remove(s);
            
            result.add(sub);
            return result;
        }
        
        if (visited.contains(seg)) {
            if (target == null)
                result.add(sub);
            return result;
        }
        
        if (sz < 1) {
            if (target == null) {
                sub.add(seg);
                visited.add(seg);
                result.add(sub);
            }
            return result;
        }
        
        // If we reached this point, then the network bifurcates:
        // there are multiple children.
        // Here we need to invoke recursion...
        sub.add(seg);
        visited.add(seg);
        Iterator<Segment> it = children.iterator();
        while (it.hasNext()) {
            seg = it.next();
            List<List<Segment>> subres;
            if (!back)
                subres = launchDFT(seg, destination, back);
            else
                subres = launchDFT(origin, seg, back);
            
            for (List<Segment> subroute : subres) {
                List<Segment> new_rt = new ArrayList<Segment>(sub);
                subroute.forEach((s) -> { new_rt.add(s); });
                result.add(new_rt);
            }
        }
        
        return result;
    }
    
    private List<List<Segment>> computeRoutes(Segment origin, Segment destination, boolean back) {
        visited.clear();
        return launchDFT(origin, destination, back);
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
                    nm = "Goes through '" + seg.fwy().get_name() + "'";
                    break;
                }
            }
            uniqueDescriptions.add(nm);
        }
        
        return uniqueDescriptions;
    }
    
    private void setRoute() {
        if ((firstSegment == null) || (lastSegment == null))
            return;
        
        selectedRouteIndex = -1;
        
        List<List<Segment>> seg_sequences = computeRoutes(firstSegment, lastSegment, false);
        if (seg_sequences.size() < 2) {
            routeSegments = seg_sequences.get(0);
        } else {
            List<String> choices = getUniqueRouteDescriptions(seg_sequences);
            Stage inputStage = new Stage();
            inputStage.initOwner(primaryStage);
            inputStage.setScene(routeChoiceScene);
            String title = "Choose Route";
            routeChoiceController.initWithRouteChoices(choices);
            inputStage.setTitle(title);
            inputStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
            inputStage.initModality(Modality.APPLICATION_MODAL);
            inputStage.setResizable(false);
            inputStage.showAndWait();
            if ((selectedRouteIndex >= 0) && (selectedRouteIndex < seg_sequences.size())) {
                routeSegments = seg_sequences.get(selectedRouteIndex);
            }
        }
        
        routeDisplay.setRouteSegments(routeSegments);
        
        int sz = routeSegments.size();
        if (sz > 0) {
            firstSegment = routeSegments.get(0);
            lastSegment = routeSegments.get(sz-1);
        }
        myRoute.set_segments(routeSegments);
        updateSectionList();
        appMainController.setProjectModified(true);
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
        
        routeEditorCanvas.widthProperty().bind(canvasParent.widthProperty());
        routeEditorCanvas.heightProperty().bind(canvasParent.heightProperty());

        routeEditorCanvas.widthProperty().addListener((observable, oldValue, newValue) -> {
           if ((ignoreChange) || (routeDisplay == null))
               return;
           routeDisplay.execute();
        });
        
        routeEditorCanvas.heightProperty().addListener((observable, oldValue, newValue) -> {
           if ((ignoreChange) || (routeDisplay == null))
               return;
           routeDisplay.execute();
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
        
        selectedRouteIndex = -1;
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
        
        routeDisplay = new RouteDisplay(routeEditorCanvas, routeSegments);
        routeDisplay.execute();
        
        ignoreChange = false;
    }
    
    
    
    
    
    
    
    /***************************************************************************
     * Callbacks 
     **************************************************************************/
    
    @FXML
    void linksOnMouseClick(MouseEvent event) {
        if (ignoreChange)
            return;
        
        int idx = listSections.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= routeSegments.size()))
            return;
        
        routeDisplay.setSelected(idx);
        
        if (event.getClickCount() == 2) {
            appMainController.selectLink(routeSegments.get(idx).fwy());
        }
    }
    
    @FXML
    void linksOnKeyPressed(KeyEvent event) {
        if (ignoreChange)
            return;
        
        event.consume();
        
        int idx = listSections.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= routeSegments.size()))
            return;
            
        if (event.getCode() == KeyCode.ENTER) {
            appMainController.selectLink(routeSegments.get(idx).fwy());
            return;
        }
        
        if ((event.getCode() == KeyCode.LEFT) ||
            (event.getCode() == KeyCode.UP) ||
            (event.getCode() == KeyCode.RIGHT) ||
            (event.getCode() == KeyCode.DOWN)) {
            if (event.getCode() == KeyCode.LEFT)
                idx--;
            if (event.getCode() == KeyCode.RIGHT)
                idx++;
            
            if ((idx < 0) || (idx >= routeSegments.size())) {
                listSections.getSelectionModel().clearSelection();
                idx = -1;
            }
            else
                listSections.getSelectionModel().select(idx);
            routeDisplay.setSelected(idx);
            listSections.requestFocus();
        }
    }
    
    @FXML
    private void onRouteNameChange(ActionEvent event) {
        if (ignoreChange)
            return;
        
        String nm = routeName.getText();
        if (!nm.equals("")) {
            myRoute.setName(nm);
            appMainController.objectNameUpdate(myRoute);
        }
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
        filterDestinationCadidates(fromOrigin);
        cbDestinationUpdate();
        setRoute();
        
        ignoreChange = false;
    }
    
    void onDestinationChange() {
        if (ignoreChange)
            return;
        
        ignoreChange = true;
        
        int idx = cbDestination.getSelectionModel().getSelectedIndex();
        lastSegment = destinationList.get(idx);
        List<List<Segment>> fromDestination = computeRoutes(null, lastSegment, true);
        filterOriginCadidates(fromDestination);
        cbOriginUpdate();
        setRoute();
        
        ignoreChange = false;
    }
    
    
    @FXML
    void canvasOnMouseClicked(MouseEvent event) {
        if (ignoreChange)
            return;
        
        double x = event.getX();
        double y = event.getY();
        
        int idx = routeDisplay.getClickedMultiBox(x, y);
        
        if ((idx < 0) || (idx >= routeSegments.size())) {
            listSections.getSelectionModel().clearSelection();
            return;
        }
        
        if (event.getClickCount() == 2) {
            appMainController.selectLink(routeSegments.get(idx).fwy());
        } else if (event.getClickCount() == 1) {
            listSections.getSelectionModel().select(idx);
            listSections.requestFocus();
            listSections.getFocusModel().focus(idx);
        }    
    }
    
    @FXML
    void canvasOnKeyPressed(KeyEvent event) {
        if (ignoreChange)
            return;
        
        int idx = routeDisplay.getSelected();
        if ((idx < 0) || (idx >= routeSegments.size()))
            return;
        
        if (event.getCode() == KeyCode.ENTER) {
            appMainController.selectLink(routeSegments.get(idx).fwy());
            return;
        }
        
        if ((event.getCode() == KeyCode.LEFT) ||
            (event.getCode() == KeyCode.UP) ||
            (event.getCode() == KeyCode.RIGHT) ||
            (event.getCode() == KeyCode.DOWN)) {
            if (event.getCode() == KeyCode.LEFT)
                idx--;
            if (event.getCode() == KeyCode.RIGHT)
                idx++;
            
            if ((idx < 0) || (idx >= routeSegments.size())) {
                listSections.getSelectionModel().clearSelection();
                idx = -1;
            }
            else
                listSections.getSelectionModel().select(idx);
            routeDisplay.setSelected(idx);
            listSections.requestFocus();
        }
        
        //event.consume();
    }

    
    
}
