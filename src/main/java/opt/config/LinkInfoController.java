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

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
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

/**
 *
 * @author Alex Kurzhanskiy
 */
public class LinkInfoController {
    private AppMainController appMainController = null;
    private AbstractLink myLink = null;
    private FreewayScenario myScenario = null;
    
    private List<AbstractLink> predecessors = new ArrayList<>();
    private List<Label> preLabels = new ArrayList<>();
    private List<AbstractLink> successors = new ArrayList<>();
    private List<Label> succLabels = new ArrayList<>();
    
    private List<Route> routes = new ArrayList<>();
    private List<Label> rLabels = new ArrayList<>();
    
    private List<ControlSchedule> lanePolicies = new ArrayList<>();
    private List<Label> pLabels = new ArrayList<>();
    
    private List<AbstractEvent> events = new ArrayList<>();
    private List<Label> eLabels = new ArrayList<>();
    
    
    @FXML // fx:id="linkInfoMainPane"
    private ScrollPane linkInfoMainPane; // Value injected by FXMLLoader

    @FXML // fx:id="vbLinkInfo"
    private VBox vbLinkInfo; // Value injected by FXMLLoader

    
    
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        vbLinkInfo.setStyle("-fx-spacing: 10;");
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
     * This function is called every time one opens a link in the
     * configuration module.
     * @param lnk 
     */
    public void initWithScenarioAndLinkData(AbstractLink lnk) {
        if (lnk == null)
            return;
        
        myLink = lnk;
        myScenario = lnk.get_segment().get_scenario();
        
        vbLinkInfo.getChildren().clear();
        
        fillUpstream();
        fillDownstream();
        fillRoutes();
        fillLanePolicies();
        fillEvents();
        
        
        
        
        
        
    }
    
    
    private void fillUpstream() {
        predecessors.clear();
        preLabels.clear();
        
        Label header = new Label("Upstream Sections:");
        header.setStyle(UtilGUI.labelInfoHeaderStyle);
        vbLinkInfo.getChildren().add(header);        
        
        AbstractLink neighbor = myLink.get_up_link();
        Label ptr;
        if (neighbor != null) {
            ptr = new Label(neighbor.get_name());
            predecessors.add(neighbor);
            preLabels.add(ptr);
        }
        if (myLink.get_type() == AbstractLink.Type.freeway) {
            int num_ramps = myLink.get_segment().num_in_ors();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink or = myLink.get_segment().in_ors(i);
                AbstractLink up = or.get_up_link();
                if (up != null) {
                    ptr = new Label(up.get_name());
                    predecessors.add(up);
                    preLabels.add(ptr);
                }
            }
            num_ramps = myLink.get_segment().num_out_ors();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink or = myLink.get_segment().out_ors(i);
                AbstractLink up = or.get_up_link();
                if (up != null) {
                    ptr = new Label(up.get_name());
                    predecessors.add(up);
                    preLabels.add(ptr);
                }
            }
        }
        if (preLabels.size() > 0)
            for (Label p : preLabels) {
                vbLinkInfo.getChildren().add(p);
                p.setStyle(UtilGUI.labelInfoPointerStyle);
                p.setOnMouseClicked((mouseEvent) -> {
                    openNeighbor(p);
                });
                p.setOnMouseEntered((mouseEvent) -> {
                    p.setCursor(Cursor.HAND);
                });
            }
        else {
            ptr = new Label("None");
            vbLinkInfo.getChildren().add(ptr);
            ptr.setStyle(UtilGUI.labelInfoOrdinaryStyle);
        }
        
        Separator sep = new Separator();
        sep.setStyle(UtilGUI.dividerInfoStyle);
        vbLinkInfo.getChildren().add(sep);
    }
    
    
    private void fillDownstream() {
        successors.clear();
        succLabels.clear();
        
        Label header = new Label("Downstream Sections:");
        header.setStyle(UtilGUI.labelInfoHeaderStyle);
        vbLinkInfo.getChildren().add(header);
        
        AbstractLink neighbor = myLink.get_dn_link();
        Label ptr;
        if (neighbor != null) {
            ptr = new Label(neighbor.get_name());
            successors.add(neighbor);
            succLabels.add(ptr);
        }
        if (myLink.get_type() == AbstractLink.Type.freeway) {
            int num_ramps = myLink.get_segment().num_in_frs();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink fr = myLink.get_segment().in_frs(i);
                AbstractLink dn = fr.get_dn_link();
                if (dn != null) {
                    ptr = new Label(dn.get_name());
                    successors.add(dn);
                    succLabels.add(ptr);
                }
            }
            num_ramps = myLink.get_segment().num_out_frs();
            for (int i = 0; i < num_ramps; i++) {
                AbstractLink fr = myLink.get_segment().out_frs(i);
                AbstractLink dn = fr.get_dn_link();
                if (dn != null) {
                    ptr = new Label(dn.get_name());
                    successors.add(dn);
                    succLabels.add(ptr);
                }
            }
        }
        
        if (succLabels.size() > 0)
            for (Label p : succLabels) {
                vbLinkInfo.getChildren().add(p);
                p.setStyle(UtilGUI.labelInfoPointerStyle);
                p.setOnMouseClicked((mouseEvent) -> {
                    openNeighbor(p);
                });
                p.setOnMouseEntered((mouseEvent) -> {
                    p.setCursor(Cursor.HAND);
                });
            }
        else {
            ptr = new Label("None");
            vbLinkInfo.getChildren().add(ptr);
            ptr.setStyle(UtilGUI.labelInfoOrdinaryStyle);
        }
        
        Separator sep = new Separator();
        sep.setStyle(UtilGUI.dividerInfoStyle);
        vbLinkInfo.getChildren().add(sep);
    }
    
    
    private void fillRoutes() {
        routes.clear();
        rLabels.clear();
        
        Label header = new Label("Routes:");
        header.setStyle(UtilGUI.labelInfoHeaderStyle);
        vbLinkInfo.getChildren().add(header);
        
        Label ptr;
        for (Route r : myScenario.get_routes()) {
            boolean found = false;
            for (Segment s : r.get_segments()) {
                for (AbstractLink l : s.get_links())
                    if (l.equals(myLink)) {
                        ptr = new Label(r.getName());
                        routes.add(r);
                        rLabels.add(ptr);
                        found = true;
                        break;
                    }
                if (found)
                    break;
            }
        }
        
        if (rLabels.size() > 0)
            for (Label p : rLabels) {
                vbLinkInfo.getChildren().add(p);
                p.setStyle(UtilGUI.labelInfoPointerStyle);
                p.setOnMouseClicked((mouseEvent) -> {
                    openRoute(p);
                });
                p.setOnMouseEntered((mouseEvent) -> {
                    p.setCursor(Cursor.HAND);
                });
            }
        else {
            ptr = new Label("None");
            vbLinkInfo.getChildren().add(ptr);
            ptr.setStyle(UtilGUI.labelInfoOrdinaryStyle);
        }
        
        Separator sep = new Separator();
        sep.setStyle(UtilGUI.dividerInfoStyle);
        vbLinkInfo.getChildren().add(sep);
    }
    
    
    private void fillLanePolicies() {
        lanePolicies.clear();
        pLabels.clear();
        
        Label header = new Label("Lane Policies:");
        header.setStyle(UtilGUI.labelInfoHeaderStyle);
        vbLinkInfo.getChildren().add(header);
        
        Label ptr;
        for (ControlSchedule cs : myScenario.get_schedules_for_controltype(AbstractController.Type.LgRestrict))
            for (AbstractLink l : cs.get_links())
                if (l.equals(myLink)) {
                    ptr = new Label(cs.get_name());
                    lanePolicies.add(cs);
                    pLabels.add(ptr);
                    break;
                }
        
        if (pLabels.size() > 0)
            for (Label p : pLabels) {
                vbLinkInfo.getChildren().add(p);
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
            vbLinkInfo.getChildren().add(ptr);
            ptr.setStyle(UtilGUI.labelInfoOrdinaryStyle);
        }
        
        Separator sep = new Separator();
        sep.setStyle(UtilGUI.dividerInfoStyle);
        vbLinkInfo.getChildren().add(sep);
    }
    
    
    private void fillEvents() {
        events.clear();
        eLabels.clear();
        
        Label header = new Label("Events:");
        header.setStyle(UtilGUI.labelInfoHeaderStyle);
        vbLinkInfo.getChildren().add(header);
        
        Label ptr;
        for (AbstractEvent e : myScenario.get_events()) {
            List<AbstractLink> ll;
            if (e instanceof EventLinkToggle)
                ll = ((EventLinkToggle)e).get_links();
            else
                ll = ((AbstractEventLaneGroup)e).get_links();
            for (AbstractLink l : ll)
                if (l.equals(myLink)) {
                    ptr = new Label(e.name);
                    events.add(e);
                    eLabels.add(ptr);
                    break;
                }
        }
        
        if (eLabels.size() > 0)
            for (Label p : eLabels) {
                vbLinkInfo.getChildren().add(p);
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
            vbLinkInfo.getChildren().add(ptr);
            ptr.setStyle(UtilGUI.labelInfoOrdinaryStyle);
        }
    }
    
    
    
    
    
    
    
    /***************************************************************************
     * CALBACKS
     **************************************************************************/

    void openNeighbor(Label l) {
        if (l == null)
            return;
        
        List<AbstractLink> links = predecessors;
        int idx = preLabels.indexOf(l);
        if ((idx < 0) || (idx >= links.size())) {
            links = successors;
            idx = succLabels.indexOf(l);
            if ((idx < 0) || (idx >= links.size()))
                return;
        }
        
        appMainController.selectLink(links.get(idx));
    }
    
    
    void openRoute(Label l) {
        if (l == null)
            return;
        
        int idx = rLabels.indexOf(l);
        if ((idx < 0) || (idx >= routes.size()))
            return;
    
        appMainController.selectRoute(routes.get(idx));
    }
    
    
    void openLanePolicies(Label l) {
        if (l == null)
            return;
        
        int idx = pLabels.indexOf(l);
        if ((idx < 0) || (idx >= lanePolicies.size()))
            return;
    
        appMainController.openLanePolicies(lanePolicies.get(idx));
    }
    
    
    void openEvents(Label l) {
        if (l == null)
            return;
        
        int idx = eLabels.indexOf(l);
        if ((idx < 0) || (idx >= events.size()))
            return;
    
        appMainController.openEvents(events.get(idx));
    }
    
    
}
