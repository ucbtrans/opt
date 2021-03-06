/**
 * Copyright (c) 2021, Regents of the University of California
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import opt.AppMainController;
import opt.UserSettings;
import opt.data.AbstractLink;
import opt.data.FreewayScenario;
import opt.data.Route;
import opt.data.Segment;
import opt.data.control.AbstractController;
import opt.data.control.ControlSchedule;
import opt.data.event.AbstractEvent;
import opt.data.event.AbstractEventLaneGroup;
import opt.data.event.EventLinkToggle;
import opt.utils.UtilGUI;

/**
 *
 * @author Alex Kurzhanskiy
 */
public class ScenarioInfoController {
    private AppMainController appMainController = null;
    private FreewayScenario myScenario = null;
    
    
    @FXML // fx:id="routeInfoMainPane"
    private ScrollPane routeInfoMainPane; // Value injected by FXMLLoader

    @FXML // fx:id="vbScenarioInfo"
    private VBox vbScenarioInfo; // Value injected by FXMLLoader

    
    
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        vbScenarioInfo.setStyle("-fx-spacing: 10;");
    }
    
    
    /**
     * This function should be called once: during the initialization.
     * @param ctrl - pointer to the main app controller that is used to sync up
     *               all sub-windows.
     */
    public void setAppMainController(AppMainController ctrl) {
        appMainController = ctrl;
    }
    
    
    /**
     * This function is called every time one opens a route.
     * 
     * @param r 
     */
    public void initWithScenarioData(FreewayScenario s) {
        if (s == null)
            return;
        
        myScenario = s;
        vbScenarioInfo.getChildren().clear();
        
        
        AbstractLink shortest = null;
        AbstractLink longest = null;
        double len = 0d;
        double lmin = Double.MAX_VALUE;
        double lmax = -1;
        int numOR = 0;
        int numFR = 0;
        int numC = 0;
        double capGP = 0d;
        
        for (AbstractLink l : myScenario.get_links()) {
            double ll = l.get_length_meters();
            len += ll;
            
            if (lmin > ll) {
                shortest = l;
                lmin = ll;
            }
            
            if (lmax < ll) {
                longest = l;
                lmax = ll;
            }
            
            if (l.get_type() == AbstractLink.Type.onramp)
                numOR++;
            
            if (l.get_type() == AbstractLink.Type.offramp)
                numFR++;
            
            if (l.get_type() == AbstractLink.Type.connector)
                numC++;
        }
        
        
    
        Label lbl = new Label(" ");
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbScenarioInfo.getChildren().add(lbl);
        
        DecimalFormat df = new DecimalFormat("#.##");      
        lbl = new Label("Total Length: " + df.format(UserSettings.lengthConversionMap.get("meters"+UserSettings.unitsLength)*len) + " " + UserSettings.unitsLength);
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbScenarioInfo.getChildren().add(lbl);
        
        lbl = new Label("Sections: " + myScenario.get_links().size());
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbScenarioInfo.getChildren().add(lbl);
        
        lbl = new Label("On-Ramps: " + numOR);
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbScenarioInfo.getChildren().add(lbl);
        
        lbl = new Label("Off-Ramps: " + numFR);
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbScenarioInfo.getChildren().add(lbl);
        
        lbl = new Label("Connectors: " + numC);
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbScenarioInfo.getChildren().add(lbl);
        
        lbl = new Label("Subnetworks: " + myScenario.get_linear_freeway_segments().size());
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbScenarioInfo.getChildren().add(lbl);
    
        Separator sep = new Separator();
        sep.setStyle(UtilGUI.dividerInfoStyle);
        vbScenarioInfo.getChildren().add(sep);
        
        
        Label header = new Label("Shortest Section:");
        header.setStyle(UtilGUI.labelInfoHeaderStyle);
        vbScenarioInfo.getChildren().add(header);
        
        if (shortest != null) {
            lbl = new Label(shortest.get_name());
            lbl.setStyle(UtilGUI.labelInfoPointerStyle);
            final AbstractLink lnk = shortest;
            final Label fl = lbl;
            lbl.setOnMouseClicked((mouseEvent) -> {
                openLink(lnk);
            });
            lbl.setOnMouseEntered((mouseEvent) -> {
                fl.setCursor(Cursor.HAND);
            });
        } else {
            lbl = new Label("None");
            lbl.setStyle(UtilGUI.labelInfoOrdinaryStyle);
        }
        vbScenarioInfo.getChildren().add(lbl);
        
        sep = new Separator();
        sep.setStyle(UtilGUI.dividerInfoStyle);
        vbScenarioInfo.getChildren().add(sep);
        
        
        header = new Label("Longest Section:");
        header.setStyle(UtilGUI.labelInfoHeaderStyle);
        vbScenarioInfo.getChildren().add(header);
        
        if (longest != null) {
            lbl = new Label(longest.get_name());
            lbl.setStyle(UtilGUI.labelInfoPointerStyle);
            final AbstractLink lnk = longest;
            final Label fl = lbl;
            lbl.setOnMouseClicked((mouseEvent) -> {
                openLink(lnk);
            });
            lbl.setOnMouseEntered((mouseEvent) -> {
                fl.setCursor(Cursor.HAND);
            });
        } else {
            lbl = new Label("None");
            lbl.setStyle(UtilGUI.labelInfoOrdinaryStyle);
        }
        vbScenarioInfo.getChildren().add(lbl);

    }
    
    
    
    
    
     
    /***************************************************************************
     * CALBACKS
     **************************************************************************/
    
    private void openLink(AbstractLink l) {
        if (l == null)
            return;

        appMainController.selectLink(l);
    }
    
    
    
}
