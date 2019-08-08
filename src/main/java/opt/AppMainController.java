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
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import opt.config.LinkEditorController;
import opt.data.Link;
import opt.data.FreewayScenario;
import opt.data.ProjectFactory;
import opt.data.Project;


/**
 * This class contains callback functions for the main application window controls.
 * 
 * @author Alex Kurzhanskiy
 */
public class AppMainController {
    
    private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
    private UserSettings userSettings = new UserSettings();
    private boolean projectModified = false;
    private String projectFilePath = null;
    private String optProjectFileDir_String = "optProjectFileDir_String";
    private File projectFileDir = null;
    private String roadLinksTreeItem = "Road Sections";
    private Project project = null;
    
    private Map<TreeItem, Object> tree2object = new HashMap<TreeItem, Object>();
    private Map<Object, TreeItem> object2tree = new HashMap<Object, TreeItem>();
    
    private SplitPane linkEditorPane = null;
    private LinkEditorController linkEditorController = null;
    
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
    
    
    
    public UserSettings getUserSettings() {
        return userSettings;
    }
    
    
    
    public void setProjectModified(boolean val) {
        projectModified = val;
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
        
        opt.utils.Dialogs.ErrorDialog("Cannot create new OPT project...", "The code is not written. You must be patient!");
        
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
            populateProjectTree(project);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/link_editor.fxml"));
            linkEditorPane = loader.load();
            linkEditorController = loader.getController();
            linkEditorController.setAppMainController(this);
            linkEditorController.initWithLinkData(null);
        } catch (IOException e) {
            opt.utils.Dialogs.ExceptionDialog("Cannot initialize section editor...", e);
        }
        
        
    }
    
    
    
    
    
    
    
    
    
    
    
    private void populateProjectTree(Project project) {
        
        if (project == null)
            return;
        
        TreeItem<String> root = new TreeItem<String>("Project");
        Collection<FreewayScenario> scenarios = project.get_scenarios();
        for (FreewayScenario scenario : scenarios) {
            TreeItem<String> scenario_node = new TreeItem<String>(scenario.get_name());
            tree2object.put(scenario_node, scenario);
            object2tree.put(scenario, scenario_node);
            
            // We'll display vehicle types on scenario level
            //TreeItem<String> commodities_node = new TreeItem<String>("Traffic Types");
            //scenario_node.getChildren().add(commodities_node);
            
            TreeItem<String> links_node = new TreeItem<String>(roadLinksTreeItem);
            scenario_node.getChildren().add(links_node);
            for (Link link : scenario.get_links()) {
                if (link == null)
                    continue;
                TreeItem<String> link_node = new TreeItem<String>(link.get_name());
                tree2object.put(link_node, link);
                object2tree.put(link, link_node);
                links_node.getChildren().add(link_node);
            }
            
            
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
        
        if (treeItem.isLeaf()) {
            if (treeItem.getParent().getValue() == roadLinksTreeItem) { //a link was selected
                configAnchorPane.getChildren().clear();
                configAnchorPane.getChildren().setAll(linkEditorPane);
                configAnchorPane.setTopAnchor(linkEditorPane, 0.0);
                configAnchorPane.setBottomAnchor(linkEditorPane, 0.0);
                configAnchorPane.setLeftAnchor(linkEditorPane, 0.0);
                configAnchorPane.setRightAnchor(linkEditorPane, 0.0);

                Link lnk = (Link)tree2object.get(treeItem);
                if (lnk != null) {
                    linkEditorController.initWithLinkData(lnk);
                }
            }
        } else {
            ; //TODO
        }
        
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
    }
    
    
    
}
