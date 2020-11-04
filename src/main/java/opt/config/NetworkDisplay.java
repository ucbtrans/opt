/**
 * Copyright (c) 2020, Regents of the University of California
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
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import opt.UserSettings;
import opt.data.AbstractLink;
import opt.data.FreewayScenario;
import opt.data.LinkConnector;
import opt.data.Segment;
import opt.utils.UtilGUI;

/**
 * Class for building the network scheme as an array of routes for display.
 * 
 * @author akurzhan@berkeley.edu
 */
public class NetworkDisplay {
    
    private Canvas canvas;
    private FreewayScenario myScenario;
    private List<RouteDisplay> routeDisplays = new ArrayList<RouteDisplay>();
    private int numRoutes = 0;
    
    private Map<AbstractLink, Color[]> ramp2colors = new HashMap<AbstractLink, Color[]>();
    private Tooltip tooltip = null;
    
    private int selectedRoute = -1;
    private int selectedIndex = -1;
    
    
    public NetworkDisplay() { }
    
    public NetworkDisplay(Canvas c, Tooltip tt, FreewayScenario s) {
        canvas = c;
        tooltip = tt;
        tooltip.setText("");
        myScenario = s;
        setCRColors(myScenario.get_connectors());
        setRoutes(myScenario.get_linear_freeway_segments());
    }
    
    
    public void setCanvas(Canvas c) {
        canvas = c;
    }
    
    
    private double getRouteLength(List<Segment> ls) {
        double len = 0;
        for (Segment s : ls)
            len += s.fwy().get_length_meters();
        return len;
    }
  
    
    
    public final void setCRColors(List<LinkConnector> lcl) {
        int ccnt = 0;
        int numCS = UtilGUI.jfxColorPairs.length;
        int sz = lcl.size();
        
        ramp2colors.clear();
        
        for (int i = 0; i < sz; i++) {
            AbstractLink up = lcl.get(i).get_up_link();
            AbstractLink dn = lcl.get(i).get_dn_link();
            Color[] clr = new Color[2];
            clr[0] = UtilGUI.jfxColorPairs[ccnt][0];
            clr[1] = UtilGUI.jfxColorPairs[ccnt][1];
            
            if ((up != null) && (!ramp2colors.containsKey(up)))
                ramp2colors.put(up, clr);
            
            if ((dn != null) && (!ramp2colors.containsKey(dn)))
                ramp2colors.put(dn, clr);
            
            ccnt++;
            if (ccnt >= numCS)
                ccnt = 0;
        }
    }

    
    
    public final void setRoutes(List<List<Segment>> lls) {
        numRoutes = lls.size();
        
        double maxLength = 0;
        for (List<Segment> ls : lls)
            maxLength = Math.max(maxLength, getRouteLength(ls));
        
        routeDisplays.clear();
        for (List<Segment> ls : lls) {
            RouteDisplay rd = new RouteDisplay(canvas, null, ls, maxLength);
            rd.setRamp2Colors(ramp2colors);
            routeDisplays.add(rd);
        }
    }
    
    
    
    private void setSelected() {
        for (int i = 0; i < numRoutes; i++)
           if (i == selectedRoute)
               routeDisplays.get(selectedRoute).setSelected(selectedIndex, numRoutes, i);
           else
               routeDisplays.get(i).setSelected(-1, numRoutes, i);
        
        canvas.requestFocus();
    }
    
    
    public void canvasOnMouseMoved(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        int rd = -1;
        
        for (int i = 0; i < numRoutes; i++)
            if ((y >= routeDisplays.get(i).getVLB()) &&
                (y <= routeDisplays.get(i).getVUB())) {
                rd = i;
                break;
            }

        int si = routeDisplays.get(rd).getClickedMultiBox(x, y, numRoutes, rd);
        
        selectedRoute = rd;
        selectedIndex = si;
        setSelected();
        
        if (si < 0) {
            tooltip.setText("");
            tooltip.hide();
            canvas.setCursor(Cursor.DEFAULT);
            return;
        }
        
        AbstractLink l = routeDisplays.get(rd).getRouteSegments().get(si).fwy();
        String units = UserSettings.unitsLength;
        double len = UserSettings.lengthConversionMap.get("meters" + units) * l.get_length_meters();
        tooltip.setText(new Formatter().format("%s\n(%.2f %s)", l.get_name(), len, units).toString());
        tooltip.setTextAlignment(TextAlignment.CENTER);
        canvas.setCursor(Cursor.HAND);
    }
    
    
    public Segment canvasOnMouseClicked(MouseEvent event) {
        selectedRoute = -1;
        selectedIndex = -1;
        
        double x = event.getX();
        double y = event.getY();
        
        for (int i = 0; i < numRoutes; i++)
            if ((y >= routeDisplays.get(i).getVLB()) &&
                (y <= routeDisplays.get(i).getVUB())) {
                selectedRoute = i;
                break;
            }

        selectedIndex = routeDisplays.get(selectedRoute).getClickedMultiBox(x, y);
        
        setSelected();
        
        if ((selectedRoute < 0) || (selectedIndex < 0) || (event.getClickCount() > 2))
            return null;
        
        return routeDisplays.get(selectedRoute).getRouteSegments().get(selectedIndex);
    }
    
    
    public Segment canvasOnKeyPressed(KeyEvent event) {
        if ((selectedRoute < 0) || (selectedIndex < 0))
            return null;
        
        if (event.getCode() == KeyCode.ENTER)
            return routeDisplays.get(selectedRoute).getRouteSegments().get(selectedIndex);
        
        event.consume();
        
        if (event.getCode() == KeyCode.LEFT) {
            if (selectedIndex == 0) {
                selectedRoute--;
                if (selectedRoute >= 0)
                    selectedIndex = routeDisplays.get(selectedRoute).getRouteSegments().size() - 1;
            } else
                selectedIndex--;
            
            setSelected();
            return null;
        }
        
        if (event.getCode() == KeyCode.UP) {
            if (selectedRoute == 0) {
                selectedIndex--;
                if (selectedIndex < 0)
                    selectedRoute--;
            } else {
                selectedRoute--;
                selectedIndex = routeDisplays.get(selectedRoute).getRouteSegments().size() - 1;
            }
            
            setSelected();
            return null;
        }
        
        if (event.getCode() == KeyCode.RIGHT) {
            if (selectedIndex == routeDisplays.get(selectedRoute).getRouteSegments().size() - 1) {
                selectedRoute++;
                if (selectedRoute < numRoutes)
                    selectedIndex = 0;
            } else
                selectedIndex++;
            
            setSelected();
            return null;
        }
        
        if (event.getCode() == KeyCode.DOWN) {
            if (selectedRoute == numRoutes - 1) {
                selectedIndex++;
                if (selectedIndex >= routeDisplays.get(selectedRoute).getRouteSegments().size()) {
                    selectedRoute = -1;
                    selectedIndex = -1;
                }
            } else {
                selectedRoute++;
                selectedIndex = 0;
            }
            
            setSelected();
            return null;
        }
        
        
        return null;
    }
    
    
    
    public void execute() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        
        for (int i = 0; i < numRoutes; i++)
            routeDisplays.get(i).execute(numRoutes, i);
    }
    
}
