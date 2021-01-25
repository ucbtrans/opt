/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.image.Image;

import opt.utils.UtilGUI;



/**
 * Main app window.
 * 
 * @author akurzhan@berkeley.edu
 */
public class AppMain extends Application {
    
    protected AppMainController main_controller;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        int[] dims = UtilGUI.getWindowDims(1250, 920);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/opt_main.fxml"));
        VBox root = (VBox)loader.load();
        main_controller = loader.getController();
        main_controller.setPrimaryStage(primaryStage);
        Scene scene = new Scene(root, dims[0], dims[1]);
        //setUserAgentStylesheet(STYLESHEET_CASPIAN);
        setUserAgentStylesheet(STYLESHEET_MODENA);
        scene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
        primaryStage.setTitle("OPT");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
        UserSettings.load();
    }
    
    public void stop() throws IOException {
        UserSettings.save();
        main_controller.toSaveProjectOrNot();
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
