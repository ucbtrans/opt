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
package opt.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;



/**
 * This class contains functions that call modal dialogs for  OPT application.
 * 
 * @author Alex Kurzhanskiy
 */
public class Dialogs {
    
    public static void WarningDialog(String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Attention!");
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Dialogs.class.getResourceAsStream("/OPT_icon.png")));
        alert.showAndWait();
    }
    
    
    
    public static void ErrorDialog(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Dialogs.class.getResourceAsStream("/OPT_icon.png")));
        alert.showAndWait();
    }
    
    
    
    public static void ExceptionDialog(String header, Exception ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception!");
        alert.setHeaderText(header);
        alert.setContentText(ex.getMessage());
        
        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stack trace:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Dialogs.class.getResourceAsStream("/OPT_icon.png")));
        alert.showAndWait();
    }
    
    
    
    public static boolean ConfirmationYesNoDialog(String h, String c) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(h);
        alert.setContentText(c);
        ButtonType buttonTypeOne = new ButtonType("Yes", ButtonData.OK_DONE);
        ButtonType buttonTypeTwo = new ButtonType("No", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Dialogs.class.getResourceAsStream("/OPT_icon.png")));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne)
            return true;
        return false;
    }
    
    
    
    public static int Confirmation3ButtonDialog(String h, String c, String bt1, String bt2, String bt3) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(h);
        alert.setContentText(c);
        ButtonType buttonTypeOne = new ButtonType(bt1);
        ButtonType buttonTypeTwo = new ButtonType(bt2);
        ButtonType buttonTypeCancel = new ButtonType(bt3, ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Dialogs.class.getResourceAsStream("/OPT_icon.png")));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeCancel)
            return -1;
        if (result.get() == buttonTypeTwo)
            return 1;
        return 0;
    }
    
    
    
    
}
