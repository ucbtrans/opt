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
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        
        VBox root = (VBox)FXMLLoader.load(getClass().getResource("/opt_main.fxml"));
        Scene scene = new Scene(root, 1200, 800);
        //setUserAgentStylesheet(STYLESHEET_CASPIAN);
        setUserAgentStylesheet(STYLESHEET_MODENA);
        primaryStage.setTitle("OPT");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/OPT_icon.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
