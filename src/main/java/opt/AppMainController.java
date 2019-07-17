/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opt;


import error.OTMException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import opt.data.FreewayScenario;
import opt.data.OPTFactory;
import opt.data.Project;
import opt.data.Segment;


/**
 *
 * @author akurz
 */
public class AppMainController {
    
    private String projectFilePath;
    private File projectFileDir = null;
    private Project project = null;
    private Map<TreeItem, Object> tree2object = new HashMap<TreeItem, Object>();
    private Map<Object, TreeItem> object2tree = new HashMap<Object, TreeItem>();
    
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

    @FXML // fx:id="actionTabPane"
    private Tab actionTabPane; // Value injected by FXMLLoader

    @FXML // fx:id="x3"
    private Font x3; // Value injected by FXMLLoader

    @FXML // fx:id="x4"
    private Color x4; // Value injected by FXMLLoader
    
    
    
    
    
    
    
    
    
    
    
    
    @FXML
    private void onClickMenuFileOpen(ActionEvent event) {
        boolean validate = true; 
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open the OPT Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OPT", "*.opt"));
        if (projectFileDir != null)
            fileChooser.setInitialDirectory(projectFileDir);
        File file = fileChooser.showOpenDialog(null);
        if (file==null)
            return;
        try {
            projectFilePath = file.getAbsolutePath();
            projectFileDir = file.getParentFile();
            project = OPTFactory.load_project(projectFilePath, validate);
            menuFileSave.setDisable(false);
            menuFileSaveAs.setDisable(false);
            populateProjectTree(project);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    
    @FXML
    private void onClickMenuFileSave(ActionEvent event) {
        if (projectFilePath == null)
            return;
        try {
            OPTFactory.save_project(project, projectFilePath);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    
    @FXML
    private void onClickMenuFileSaveAs(ActionEvent event) {
        boolean validate = true; 
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save the OPT Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OPT", "*.opt"));
        if (projectFileDir != null)
            fileChooser.setInitialDirectory(projectFileDir);
        File file = fileChooser.showSaveDialog(null);
        if (file==null)
            return;
        try {
            projectFilePath = file.getAbsolutePath();
            projectFileDir = file.getParentFile();
            OPTFactory.save_project(project, projectFilePath);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    
    @FXML
    private void onClickMenuFileExit(ActionEvent event) {
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.close();
    }
    
    
    
    private void populateProjectTree(Project project) {
        
        if (project == null)
            return;
        
        TreeItem<String> root = new TreeItem<String>("Project");
        Collection<String> scenario_names = project.get_scenario_names();
        for (String sn : scenario_names) {
            TreeItem<String> scenario_node = new TreeItem<String>(sn);
            FreewayScenario scenario = project.get_scenario_with_name(sn);
            tree2object.put(scenario_node, scenario);
            object2tree.put(scenario, scenario_node);
            
            scenario_node.getChildren().add(new TreeItem<String>("Road Segments"));
            for (Segment segment : scenario.get_segments()) {
                TreeItem<String> segment_node = new TreeItem<String>(segment.toString());
                scenario_node.getChildren().add(segment_node);
            }
            
            
            root.getChildren().add(scenario_node);
        }
        
        
        projectTree.setRoot(root);
        projectTree.setShowRoot(true);
        projectTree.refresh();
    }
    
    
    
    
    
    
    
    
    
}
