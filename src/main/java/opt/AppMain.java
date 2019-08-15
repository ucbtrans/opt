/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;

/**
 *
 * @author akurz
 */
public class AppMain extends Application {
    
    protected AppMainController main_controller;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/opt_main.fxml"));
        VBox root = (VBox)loader.load();
        main_controller = loader.getController();
        main_controller.setPrimaryStage(primaryStage);
        Scene scene = new Scene(root, 1200, 900);
        //setUserAgentStylesheet(STYLESHEET_CASPIAN);
        setUserAgentStylesheet(STYLESHEET_MODENA);
        scene.getStylesheets().add(getClass().getResource("/opt.css").toExternalForm());
        primaryStage.setTitle("OPT");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void stop() throws IOException {
        main_controller.toSaveProjectOrNot();
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
