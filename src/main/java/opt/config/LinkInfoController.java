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
package opt.config;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import opt.AppMainController;
import opt.data.AbstractLink;
import opt.data.Segment;

/**
 *
 * @author Alex Kurzhanskiy
 */
public class LinkInfoController {
    private AppMainController appMainController = null;
    private AbstractLink myLink = null;
    private List<AbstractLink> predecessors = new ArrayList<AbstractLink>();
    private List<AbstractLink> successors = new ArrayList<AbstractLink>();
    
    
    
    @FXML // fx:id="linkInfoMainPane"
    private GridPane linkInfoMainPane; // Value injected by FXMLLoader

    @FXML // fx:id="listUpstreamSections"
    private ListView<String> listUpstreamSections; // Value injected by FXMLLoader

    @FXML // fx:id="listDownstreamSections"
    private ListView<String> listDownstreamSections; // Value injected by FXMLLoader

    @FXML // fx:id="listLinkControllers"
    private ListView<String> listLinkControllers; // Value injected by FXMLLoader

    @FXML // fx:id="listLinkEvents"
    private ListView<String> listLinkEvents; // Value injected by FXMLLoader
    
    
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        listUpstreamSections.getStyleClass().add("info-list-view");
        listDownstreamSections.getStyleClass().add("info-list-view");
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
    public void initWithLinkData(AbstractLink lnk) {
        if (lnk == null)
            return;
        
        myLink = lnk;
        predecessors.clear();
        successors.clear();
        listUpstreamSections.getItems().clear();
        listDownstreamSections.getItems().clear();
        
        if (myLink.get_type() == AbstractLink.Type.freeway) {
            Segment seg = myLink.get_segment().get_upstrm_fwy_segment();
            AbstractLink ramp = myLink.get_segment().get_links().get(0);
            if (seg != null)
                predecessors.add(seg.get_links().get(1));
            if (ramp != null)
                predecessors.add(ramp);
            
            seg = myLink.get_segment().get_dnstrm_fwy_segment();
            ramp = myLink.get_segment().get_links().get(2);
            if (seg != null)
                successors.add(seg.get_links().get(1));
            if (ramp != null)
                successors.add(ramp);
        }
        if (myLink.get_type() == AbstractLink.Type.onramp) {
            successors.add(myLink.get_segment().get_links().get(1));
        }
        if (myLink.get_type() == AbstractLink.Type.offramp) {
            predecessors.add(myLink.get_segment().get_links().get(1));
        }
        
        for (int i = 0; i < predecessors.size(); i++) {
            listUpstreamSections.getItems().add(predecessors.get(i).name + " (" + predecessors.get(i).get_type() + ")");
        }
        for (int i = 0; i < successors.size(); i++) {
            listDownstreamSections.getItems().add(successors.get(i).name + " (" + successors.get(i).get_type() + ")");
        }
    }
    
    
    
    
    
    
    
    /***************************************************************************
     * CALBACKS
     **************************************************************************/

    @FXML
    void upstreamSectionSelected(MouseEvent event) {
        int idx = listUpstreamSections.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= predecessors.size()))
            return;
        appMainController.selectLink(predecessors.get(idx));
    }
    
    
    @FXML
    void downstreamSectionSelected(MouseEvent event) {
        int idx = listDownstreamSections.getSelectionModel().getSelectedIndex();
        if ((idx < 0) || (idx >= successors.size()))
            return;
        appMainController.selectLink(successors.get(idx));
    }
    
    
    
    
}
