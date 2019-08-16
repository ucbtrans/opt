/**
 * Copyright (c) 2019, Regents of the University of California
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


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import opt.config.ConnectController;
import opt.config.LinkEditorController;
import opt.config.LinkInfoController;
import opt.config.NewLinkController;
import opt.config.NewRampController;
import opt.config.ScenarioEditorController;
import opt.data.AbstractLink;
import opt.data.FreewayScenario;
import opt.data.LinkParameters;
import opt.data.ProjectFactory;
import opt.data.Project;


/**
 * This class contains callback functions for the main application window controls.
 * 
 * @author Alex Kurzhanskiy
 */
public class AppMainController {
    private Stage primaryStage = null;
    private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
//    private UserSettings userSettings = new UserSettings();
    private boolean projectModified = false;
    private String projectFilePath = null;
    private String optProjectFileDir_String = "optProjectFileDir_String";
    private File projectFileDir = null;
    private String roadLinksTreeItem = "Road Sections";
    private String routesTreeItem = "Routes";
    private String controllersTreeItem = "Controllers";
    private String eventsTreeItem = "Events";
    private Project project = null;
    
    private Map<TreeItem, Object> tree2object = new HashMap<TreeItem, Object>();
    private Map<Object, TreeItem> object2tree = new HashMap<Object, TreeItem>();
    
    private SplitPane scenarioEditorPane = null;
    private ScenarioEditorController scenarioEditorController = null;
    private SplitPane linkEditorPane = null;
    private LinkEditorController linkEditorController = null;
    private GridPane linkInfoPane = null;
    private LinkInfoController linkInfoController = null;
    private GridPane newLinkPane = null;
    private NewLinkController newLinkController = null;
    private GridPane newRampPane = null;
    private NewRampController newRampController = null;
    private GridPane connectPane = null;
    private ConnectController connectController = null;
    
    
    
    @FXML // fx:id="topPane"
    private VBox topPane; // Value injected by FXMLLoader

    @FXML // fx:id="menuFileNewProject"
    private MenuItem menuFileNewProject; // Value injected by FXMLLoader

    @FXML // fx:id="menuFileOpenProject"
    private MenuItem menuFileOpenProject; // Value injected by FXMLLoader

    @FXML // fx:id="menuFileSave"
    private MenuItem menuFileSave; // Value injected by FXMLLoader

    @FXML // fx:id="menuFileSaveAs"
    private MenuItem menuFileSaveAs; // Value injected by FXMLLoader

    @FXML // fx:id="menuFilePreferences"
    private MenuItem menuFilePreferences; // Value injected by FXMLLoader

    @FXML // fx:id="menuFileExit"
    private MenuItem menuFileExit; // Value injected by FXMLLoader

    @FXML // fx:id="projectTree"
    private TreeView<String> projectTree; // Value injected by FXMLLoader

    @FXML // fx:id="actionPane"
    private TabPane actionPane; // Value injected by FXMLLoader

    @FXML // fx:id="configTabPane"
    private Tab configTabPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="configAnchorPane"
    private AnchorPane configAnchorPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="simTabPane"
    private Tab simTabPane; // Value injected by FXMLLoader

    @FXML // fx:id="reportTabPane"
    private Tab reportTabPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="infoAnchorPane"
    private AnchorPane infoAnchorPane; // Value injected by FXMLLoader



    @FXML // fx:id="x3"
    private Font x3; // Value injected by FXMLLoader

    @FXML // fx:id="x4"
    private Color x4; // Value injected by FXMLLoader
    
    
    
    public void setPrimaryStage(Stage s) {
        primaryStage = s;
    }
    
    public void toSaveProjectOrNot() {
        if (projectModified) {
            if (opt.utils.Dialogs.ConfirmationYesNoDialog("Your project was modified...", "Do you want to save it?")) {
                if (projectFilePath == null) {
                    onClickMenuFileSaveAs(null);
                } else {
                    onClickMenuFileSave(null);
                }
            }
        }
    }
    
    
    
//    public UserSettings getUserSettings() {
//        return userSettings;
//    }
    
    
    
    public void setProjectModified(boolean val) {
        projectModified = val;
    }
    
    
    public GridPane getNewLinkPane() {
        return newLinkPane;
    }
    
    
    
    public NewLinkController getNewLinkController() {
        return newLinkController;
    }
    
    
    
    
    
    
    
    
    
    
    /***************************************************************************
     *  CALLBACKS
     **************************************************************************/
    
    @FXML
    private void onClickMenuFileNew(ActionEvent event) {
        if (projectModified) {
            toSaveProjectOrNot();
        }
        reset();
        
        LinkParameters params = UserSettings.getDefaultFreewayParams("A -> B",null);
        project = new Project("A", "A -> B",params);
        menuFileSave.setDisable(false);
        menuFileSaveAs.setDisable(false);
        Collection<FreewayScenario> scenarios = project.get_scenarios();
        for (FreewayScenario scenario : scenarios) {
             for (AbstractLink link : scenario.get_links()) {
                if (link == null)
                    continue;
                try {
                    link.set_gp_lanes(UserSettings.defaultFreewayGPLanes);
                } catch (Exception e) {
                    ;
                }
             }
        }
        populateProjectTree();
        
        projectModified = true;
    }
    
    
    @FXML
    private void onClickMenuFileOpen(ActionEvent event) {
        if (projectModified) {
            toSaveProjectOrNot();
        }
        reset();
        
        boolean validate = true; 
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open the OPT Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OPT", "*.opt"));
        String optProjectFileDir_String = prefs.get(this.optProjectFileDir_String, null);
        if (optProjectFileDir_String != null) {
            projectFileDir = new File(optProjectFileDir_String);
            if ((projectFileDir != null) && (projectFileDir.isDirectory()))
                fileChooser.setInitialDirectory(projectFileDir);
        }
        File file = fileChooser.showOpenDialog(null);
        if (file==null)
            return;
        try {
            projectFilePath = file.getAbsolutePath();
            projectFileDir = file.getParentFile();
            prefs.put(this.optProjectFileDir_String, projectFileDir.getAbsolutePath());
            project = ProjectFactory.load_project(projectFilePath, validate);
            menuFileSave.setDisable(false);
            menuFileSaveAs.setDisable(false);
            populateProjectTree();
        } catch (Exception ex) {
            opt.utils.Dialogs.ExceptionDialog("Error loading OPT project", ex);
        }
    }
   
    
   
    @FXML
    private void onClickMenuFileSave(ActionEvent event) {
        if (projectFilePath == null)
            return;
        try {
            ProjectFactory.save_project(project, projectFilePath);
            projectModified = false;
        } catch (Exception ex) {
            opt.utils.Dialogs.ExceptionDialog("Error saving OPT project", ex);
        }
    }
    
    
    
    @FXML
    private void onClickMenuFileSaveAs(ActionEvent event) {
        boolean validate = true; 
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save the OPT Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OPT", "*.opt"));
        String optProjectFileDir_String = prefs.get(this.optProjectFileDir_String, null);
        if (optProjectFileDir_String != null) {
            projectFileDir = new File(optProjectFileDir_String);
            if ((projectFileDir != null) && (projectFileDir.isDirectory()))
                fileChooser.setInitialDirectory(projectFileDir);
        }
        File file = fileChooser.showSaveDialog(null);
        if (file==null)
            return;
        try {
            projectFilePath = file.getAbsolutePath();
            projectFileDir = file.getParentFile();
            prefs.put(this.optProjectFileDir_String, projectFileDir.getAbsolutePath());
            ProjectFactory.save_project(project, projectFilePath);
            projectModified = false;
        } catch (Exception ex) {
            opt.utils.Dialogs.ExceptionDialog("Error saving OPT project", ex);
        }
    }
    
    
    
    @FXML
    private void onClickMenuFileExit(ActionEvent event) {
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }
    
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        
        // Tree action listener
        projectTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           processTreeSelection((TreeItem<String>)newValue); 
        });
        
        // Initialize link editor
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenario_editor.fxml"));
            scenarioEditorPane = loader.load();
            scenarioEditorController = loader.getController();
            scenarioEditorController.setPrimaryStage(primaryStage);
            scenarioEditorController.setAppMainController(this);
            
            loader = new FXMLLoader(getClass().getResource("/link_editor.fxml"));
            linkEditorPane = loader.load();
            linkEditorController = loader.getController();
            linkEditorController.setPrimaryStage(primaryStage);
            linkEditorController.setAppMainController(this);
            //linkEditorController.initWithLinkData(null);
            
            loader = new FXMLLoader(getClass().getResource("/link_info.fxml"));
            linkInfoPane = loader.load();
            linkInfoController = loader.getController();
            linkInfoController.setAppMainController(this);
            
            loader = new FXMLLoader(getClass().getResource("/new_link.fxml"));
            newLinkPane = loader.load();
            newLinkController = loader.getController();
            newLinkController.setAppMainController(this);
            linkEditorController.setNewLinkControllerAndScene(newLinkController, new Scene(newLinkPane));
            
            loader = new FXMLLoader(getClass().getResource("/new_ramp.fxml"));
            newRampPane = loader.load();
            newRampController = loader.getController();
            newRampController.setAppMainController(this);
            linkEditorController.setNewRampControllerAndScene(newRampController, new Scene(newRampPane));
            
            loader = new FXMLLoader(getClass().getResource("/connect_link.fxml"));
            connectPane = loader.load();
            connectController = loader.getController();
            connectController.setAppMainController(this);
            linkEditorController.setConnectControllerAndScene(connectController, new Scene(connectPane));
            
            
        } catch (IOException e) {
            opt.utils.Dialogs.ExceptionDialog("Cannot initialize UI modules...", e);
        }
        
        
    }
    
    
    
    
    
    
    
    
    
    
    
    private void populateProjectTree() {
        
        if (project == null)
            return;
        
        TreeItem<String> root = new TreeItem<String>("Project");
        Collection<FreewayScenario> scenarios = project.get_scenarios();
        for (FreewayScenario scenario : scenarios) {
            String s_nm = "Scenario: " + scenario.name;
            TreeItem<String> scenario_node = new TreeItem<String>(s_nm);
            tree2object.put(scenario_node, scenario);
            object2tree.put(scenario, scenario_node);
            
            // We'll display vehicle types on scenario level
            //TreeItem<String> commodities_node = new TreeItem<String>("Traffic Types");
            //scenario_node.getChildren().add(commodities_node);
            
            TreeItem<String> links_node = new TreeItem<String>(roadLinksTreeItem);
            scenario_node.getChildren().add(links_node);
            for (AbstractLink link : scenario.get_links()) {
                if (link == null)
                    continue;
                TreeItem<String> link_node = new TreeItem<String>(link.get_name());
                tree2object.put(link_node, link);
                object2tree.put(link, link_node);
                links_node.getChildren().add(link_node);
            }
            
            TreeItem<String> routes_node = new TreeItem<String>(routesTreeItem);    
            scenario_node.getChildren().add(routes_node);
            
            TreeItem<String> controllers_node = new TreeItem<String>(controllersTreeItem);    
            scenario_node.getChildren().add(controllers_node);
            
            TreeItem<String> events_node = new TreeItem<String>(eventsTreeItem);    
            scenario_node.getChildren().add(events_node);
            
            root.getChildren().add(scenario_node);
        }
        
        root.setExpanded(true);
        projectTree.setRoot(root);
        projectTree.setShowRoot(true);
        projectTree.refresh();
    }
    
    
    
    private void processTreeSelection(TreeItem<String> treeItem) {
        if (treeItem == null)
            return;
        if (treeItem.equals(projectTree.getRoot()))
            return;
        
        if (treeItem.isLeaf()) {
            if (treeItem.getParent().getValue() == roadLinksTreeItem) { //a link was selected
                configAnchorPane.getChildren().clear();
                configAnchorPane.getChildren().setAll(linkEditorPane);
                configAnchorPane.setTopAnchor(linkEditorPane, 0.0);
                configAnchorPane.setBottomAnchor(linkEditorPane, 0.0);
                configAnchorPane.setLeftAnchor(linkEditorPane, 0.0);
                configAnchorPane.setRightAnchor(linkEditorPane, 0.0);
                
                infoAnchorPane.getChildren().clear();
                infoAnchorPane.getChildren().setAll(linkInfoPane);
                infoAnchorPane.setTopAnchor(linkInfoPane, 0.0);
                infoAnchorPane.setBottomAnchor(linkInfoPane, 0.0);
                infoAnchorPane.setLeftAnchor(linkInfoPane, 0.0);
                infoAnchorPane.setRightAnchor(linkInfoPane, 0.0);

                AbstractLink lnk = (AbstractLink)tree2object.get(treeItem);
                if (lnk != null) {
                    linkEditorController.initWithLinkData(lnk);
                    linkInfoController.initWithLinkData(lnk);
                }
            }
        } else {
            configAnchorPane.getChildren().clear();
            infoAnchorPane.getChildren().clear();
            
            Object scenario = (FreewayScenario)tree2object.get(treeItem);
            if (scenario != null) {
                configAnchorPane.getChildren().clear();
                configAnchorPane.getChildren().setAll(scenarioEditorPane);
                configAnchorPane.setTopAnchor(scenarioEditorPane, 0.0);
                configAnchorPane.setBottomAnchor(scenarioEditorPane, 0.0);
                configAnchorPane.setLeftAnchor(scenarioEditorPane, 0.0);
                configAnchorPane.setRightAnchor(scenarioEditorPane, 0.0);
                scenarioEditorController.initWithScenarioData((FreewayScenario)scenario);
            }
        }
        
    }
    

    /**
     * This function is called when the user navigates to the given link not
     * from projectTree, but using other pointers. Now we need to change the
     * selected tree item appropriately and open the corresponding action
     * and info panels.
     * 
     * @param lnk - road link.
     */
    public void selectLink(AbstractLink lnk) {
        if (lnk == null) 
            return;
        
        TreeItem item = object2tree.get(lnk);
        if (item == null) 
            return;
        
        projectTree.getSelectionModel().select(item);  
    }
    
    
    
    public void objectNameUpdate(Object obj) {
        if (obj == null) 
            return;
        
        if (projectTree.getRoot() != null)
            projectTree.getRoot().getChildren().clear();
        
        tree2object = new HashMap<TreeItem, Object>();
        object2tree = new HashMap<Object, TreeItem>();
        
        projectModified = true;
        populateProjectTree();
        
        TreeItem item = object2tree.get(obj);
        if (item == null) 
            return;
        
        projectTree.getSelectionModel().select(item);  
    }
    
    
    public void changeScenarioName(FreewayScenario s, String new_name) {
        if (s == null)
            return;
        
        try {
            project.set_scenario_name(s.name, new_name);
        } catch (Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Cannot change scenario name...", e);
        }
        
        objectNameUpdate(s);
    }
    
    
    
    public void deleteLink(AbstractLink lnk) {
        if (lnk == null) 
            return;
        
        FreewayScenario scenario = lnk.get_segment().get_scenario();
        AbstractLink up_link = lnk.get_up_link();
        AbstractLink dn_link = lnk.get_dn_link();
        try {
            scenario.delete_segment(lnk.get_segment());
        } catch (Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Could not delete road section...", e);
            return;
        }
        
        if ((up_link != null) && (dn_link != null) &&
            (dn_link.get_type() == AbstractLink.Type.freeway)) {
            dn_link.connect_to_upstream(up_link);
        }
        
        projectModified = true;
        
        Object toDisplay = scenario;
        if (dn_link != null)
            toDisplay = dn_link;
        else if (up_link != null)
            toDisplay = up_link;
        
        if (projectTree.getRoot() != null)
            projectTree.getRoot().getChildren().clear();
        
        tree2object = new HashMap<TreeItem, Object>();
        object2tree = new HashMap<Object, TreeItem>();
        
        populateProjectTree();
        
        TreeItem item = object2tree.get(toDisplay);
        if (item == null) 
            return;
        
        projectTree.getSelectionModel().select(item);
    }





    
    
    private void reset() {
        projectModified = false;
        projectFilePath = null;
        tree2object = new HashMap<TreeItem, Object>();
        object2tree = new HashMap<Object, TreeItem>();
        
        if (projectTree.getRoot() != null)
            projectTree.getRoot().getChildren().clear();
        
        actionPane.getSelectionModel().select(configTabPane);
        configAnchorPane.getChildren().clear();
        infoAnchorPane.getChildren().clear();

    }
    
    
    
}
