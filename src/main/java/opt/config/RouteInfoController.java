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
import opt.UserSettings;


/**
 *
 * @author Alex Kurzhanskiy
 */
public class RouteInfoController {
    private AppMainController appMainController = null;
    private Route myRoute = null;
    private FreewayScenario myScenario = null;
    
    private List<ControlSchedule> lanePolicies = new ArrayList<>();
    private List<Label> pLabels = new ArrayList<>();
    
    private List<AbstractEvent> events = new ArrayList<>();
    private List<Label> eLabels = new ArrayList<>();
    
    
    @FXML // fx:id="routeInfoMainPane"
    private ScrollPane routeInfoMainPane; // Value injected by FXMLLoader

    @FXML // fx:id="vbRouteInfo"
    private VBox vbRouteInfo; // Value injected by FXMLLoader

    
    
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        vbRouteInfo.setStyle("-fx-spacing: 10;");
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
    public void initWithScenarioAndRouteData(Route r) {
        if (r == null)
            return;
        
        myRoute = r;
        myScenario = r.get_scenario();
        
        vbRouteInfo.getChildren().clear();
        
        fillAttributes();
        fillLanePolicies();
        fillEvents();
    }
    
    
    
    private void fillAttributes() {
        AbstractLink shortest = null;
        AbstractLink longest = null;
        double len = 0d;
        double lmin = Double.MAX_VALUE;
        double lmax = -1;
        int numOR = 0;
        int numFR = 0;
        double capGP = 0d;
        
        for (AbstractLink l : myRoute.get_link_sequence()) {
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
        }
        
        for (Segment s : myRoute.get_segments()) {
            numOR += s.get_ors().size();
            numFR += s.get_frs().size();
        }
    
        Label lbl = new Label(" ");
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbRouteInfo.getChildren().add(lbl);
        
        DecimalFormat df = new DecimalFormat("#.##");      
        lbl = new Label("Length: " + df.format(UserSettings.lengthConversionMap.get("meters"+UserSettings.unitsLength)*len) + " " + UserSettings.unitsLength);
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbRouteInfo.getChildren().add(lbl);
        
        lbl = new Label("Sections: " + myRoute.get_link_sequence().size());
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbRouteInfo.getChildren().add(lbl);
        
        lbl = new Label("On-Ramps: " + numOR);
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbRouteInfo.getChildren().add(lbl);
        
        lbl = new Label("Off-Ramps: " + numFR);
        lbl.setStyle(UtilGUI.labelInfoHeaderStyle2);
        vbRouteInfo.getChildren().add(lbl);
    
        Separator sep = new Separator();
        sep.setStyle(UtilGUI.dividerInfoStyle);
        vbRouteInfo.getChildren().add(sep);
        
        
        Label header = new Label("Shortest Section:");
        header.setStyle(UtilGUI.labelInfoHeaderStyle);
        vbRouteInfo.getChildren().add(header);
        
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
        vbRouteInfo.getChildren().add(lbl);
        
        sep = new Separator();
        sep.setStyle(UtilGUI.dividerInfoStyle);
        vbRouteInfo.getChildren().add(sep);
        
        
        header = new Label("Longest Section:");
        header.setStyle(UtilGUI.labelInfoHeaderStyle);
        vbRouteInfo.getChildren().add(header);
        
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
        vbRouteInfo.getChildren().add(lbl);
        
        sep = new Separator();
        sep.setStyle(UtilGUI.dividerInfoStyle);
        vbRouteInfo.getChildren().add(sep);
    }
    
    
    
    private void fillLanePolicies() {
        lanePolicies.clear();
        pLabels.clear();
        
        Label header = new Label("Lane Policies:");
        header.setStyle(UtilGUI.labelInfoHeaderStyle);
        vbRouteInfo.getChildren().add(header);
        
        Label ptr;
        for (ControlSchedule cs : myScenario.get_schedules_for_controltype(AbstractController.Type.LgRestrict))
            for (AbstractLink l : cs.get_links()) {
                int idx = myRoute.get_link_sequence().indexOf(l);
                if (idx >= 0) {
                    ptr = new Label(cs.get_name());
                    lanePolicies.add(cs);
                    pLabels.add(ptr);
                    break;
                }
            }
        
        if (pLabels.size() > 0)
            for (Label p : pLabels) {
                vbRouteInfo.getChildren().add(p);
                p.setStyle(UtilGUI.labelInfoPointerStyle);
                p.setOnMouseClicked((mouseEvent) -> {
                    openLanePolicies(p);
                });
                p.setOnMouseEntered((mouseEvent) -> {
                    p.setCursor(Cursor.HAND);
                });
            }
        else {
            ptr = new Label("None");
            vbRouteInfo.getChildren().add(ptr);
            ptr.setStyle(UtilGUI.labelInfoOrdinaryStyle);
        }
        
        Separator sep = new Separator();
        sep.setStyle(UtilGUI.dividerInfoStyle);
        vbRouteInfo.getChildren().add(sep);
    }
    
    
    private void fillEvents() {
        events.clear();
        eLabels.clear();
        
        Label header = new Label("Events:");
        header.setStyle(UtilGUI.labelInfoHeaderStyle);
        vbRouteInfo.getChildren().add(header);
        
        Label ptr;
        for (AbstractEvent e : myScenario.get_events()) {
            List<AbstractLink> ll;
            if (e instanceof EventLinkToggle)
                ll = ((EventLinkToggle)e).get_links();
            else
                ll = ((AbstractEventLaneGroup)e).get_links();
            for (AbstractLink l : ll) {
                int idx = myRoute.get_link_sequence().indexOf(l);
                if (idx >= 0) {
                    ptr = new Label(e.name);
                    events.add(e);
                    eLabels.add(ptr);
                    break;
                }
            }
        }
        
        if (eLabels.size() > 0)
            for (Label p : eLabels) {
                vbRouteInfo.getChildren().add(p);
                p.setStyle(UtilGUI.labelInfoPointerStyle);
                p.setOnMouseClicked((mouseEvent) -> {
                    openEvents(p);
                });
                p.setOnMouseEntered((mouseEvent) -> {
                    p.setCursor(Cursor.HAND);
                });
            }
        else {
            ptr = new Label("None");
            vbRouteInfo.getChildren().add(ptr);
            ptr.setStyle(UtilGUI.labelInfoOrdinaryStyle);
        }
    }
    
    
    
    
    
    
    
    /***************************************************************************
     * CALBACKS
     **************************************************************************/
    
    private void openLink(AbstractLink l) {
        if (l == null)
            return;

        appMainController.selectLink(l);
    }
    
    
    private void openLanePolicies(Label l) {
        if (l == null)
            return;
        
        int idx = pLabels.indexOf(l);
        if ((idx < 0) || (idx >= lanePolicies.size()))
            return;
    
        appMainController.openLanePolicies(lanePolicies.get(idx));
    }
    
    
    private void openEvents(Label l) {
        if (l == null)
            return;
        
        int idx = eLabels.indexOf(l);
        if ((idx < 0) || (idx >= events.size()))
            return;
    
        appMainController.openEvents(events.get(idx));
    }
    
    
}
