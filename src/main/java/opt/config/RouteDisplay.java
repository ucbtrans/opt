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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import opt.UserSettings;
import opt.data.AbstractLink;
import opt.data.LinkConnector;
import opt.data.Segment;
import opt.utils.UtilGUI;



/**
 * Class for building the route scheme for display.
 * 
 * @author akurzhan@berkeley.edu
 */
public class RouteDisplay {
    private final double dashes_thres = 3;
    private final double deg2rad = 0.0174533;
    private final double ramp_angle = 55.0;
    private final double c_gap = 100.0; // meters
    private final double h_margin = 0.05;
    
    private int maxLanes = 0;
    private int maxGPLanes = 0;
    private int maxManagedLanes = 0;
    private int maxAuxLanes = 0;
    private double routeLength = 0; // meters
    
    private final List<MultiXYBox> multiBoxes = new ArrayList<>();
    
    private Canvas canvas;
    private List<Segment> segments = null;
    private double maxRouteLength = -1;
    private int selectedIndex = -1;
    
    private Tooltip tooltip = null;
    
    private Map<AbstractLink, Color[]> ramp2colors = null;
    
    private double v_lb = 0;
    private double v_ub = 0;
    
    
    public RouteDisplay() { }
    
    public RouteDisplay(Canvas c, Tooltip tt, List<Segment> rs) {
        canvas = c;
        tooltip = tt;
        segments = rs;
    }
    
    public RouteDisplay(Canvas c, Tooltip tt, List<Segment> rs, double mrl) {
        this(c, tt, rs);
        maxRouteLength = mrl;
    }
    
    
    public void setCanvas(Canvas c) {
        canvas = c;
    }
    
    
    public void setMaxRouteLength(double mrl) {
        maxRouteLength = mrl;
    }
    
    
    public List<Segment> getRouteSegments() {
        return segments;
    }
    
    
    public void setRouteSegments(List<Segment> rs) {
        segments = rs;
        
        if (canvas == null)
            return;
        
        execute();
    }
    
    
    public void setRamp2Colors(Map<AbstractLink, Color[]> r2c) {
        ramp2colors = r2c;
    }
    
    
    public double getVLB() { return v_lb; }
    
    public double getVUB() { return v_ub; }
    
    
    public int getSelected() {
        return selectedIndex;
    }
    
    
    public void setSelected(int idx) {
        selectedIndex = idx;
        draw(1, 0);
    } 
    
    
    public void setSelected(int idx, double num_routes, double route_num) {
        selectedIndex = idx;
        draw(num_routes, route_num);
    } 
    
    
    public int getClickedMultiBox(double x, double y) {
        int sz = multiBoxes.size();
        for (int i = sz - 1; i >= 0; i--)
            if (multiBoxes.get(i).isinside(x, y)) {
                setSelected(i);
                return i;
            }
        setSelected(-1);
        return -1;
    }
    
    
    public int getClickedMultiBox(double x, double y, double num_routes, double route_num) {
        int sz = multiBoxes.size();
        for (int i = sz - 1; i >= 0; i--)
            if (multiBoxes.get(i).isinside(x, y)) {
                setSelected(i, num_routes, route_num);
                return i;
            }
        setSelected(-1, num_routes, route_num);
        return -1;
    }
    
    
    public int canvasOnMouseMoved(MouseEvent event) {
        int idx = getClickedMultiBox(event.getX(), event.getY());
        
        if (idx < 0) {
            if (tooltip != null) {
                tooltip.setText("");
                tooltip.hide();
            }
            canvas.setCursor(Cursor.DEFAULT);
            return idx;
        }
        
        AbstractLink l = segments.get(idx).fwy();
        String units = UserSettings.unitsLength;
        double len = UserSettings.lengthConversionMap.get("meters" + units) * l.get_length_meters();
        if (tooltip != null) {
            tooltip.setText(new Formatter().format("%s\n(%.2f %s)", l.get_name(), len, units).toString());
            tooltip.setTextAlignment(TextAlignment.CENTER);
        }
        
        canvas.setCursor(Cursor.HAND);
        return idx;
    }
    
    
    
    private void initRouteProperties() {
        maxLanes = 0;
        routeLength = 0;
        
        if (ramp2colors == null)
            ramp2colors = new HashMap<AbstractLink, Color[]>();
        
        int ccnt = 0;
        int numCS = UtilGUI.jfxColorPairs.length;
        
        int sz = segments.size();
        for (int i = 0; i < sz; i++) {
            AbstractLink l = segments.get(i).fwy();
            maxLanes = Math.max(maxLanes, l.get_lanes());
            maxGPLanes = Math.max(maxGPLanes, l.get_gp_lanes());
            maxManagedLanes = Math.max(maxManagedLanes, l.get_mng_lanes());
            maxAuxLanes = Math.max(maxAuxLanes, l.get_aux_lanes());
            routeLength += l.get_length_meters();
            
            if (l instanceof LinkConnector) {
                routeLength += 2 * c_gap;
                AbstractLink up = l.get_up_link();
                AbstractLink dn = l.get_dn_link();
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
    }
    
    
    
    private int[] computeDashes(AbstractLink l) {
        if (l.get_lanes() < 2)
            return null;
        
        int mng = l.get_mng_lanes();
        int aux = l.get_aux_lanes();
        int gp = l.get_gp_lanes();
                
        int[] dashes = new int[l.get_lanes() - 1];
        int ln = 1;
        int j = 0;
        while (ln < mng) {
            dashes[j] = 2;
            j++;
            ln++;
        }
        if (mng > 0) {
            dashes[j] = 2;
            if (l.get_mng_barrier())
                dashes[j] = 0;
            j++;
        }

        ln = 1;
        while (ln < gp) {
            dashes[j] = 2;
            j++;
            ln++;
        }
        if (aux > 0) {
            dashes[j] = 3;
            j++;
        }

        ln = 1;
        while (ln < aux) {
            dashes[j] = 2;
            j++;
            ln++;
        }
        
        return dashes;
    }
    
    
    
    public void computeGeometry(double num_routes, double route_num) {
        boolean rightSideRoads = UserSettings.rightSideRoads;
        
        double width = canvas.getWidth();
        double c_height = canvas.getHeight();
        double height = c_height / num_routes;
        double y0 = route_num * height;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        v_lb = y0;
        v_ub = y0 + height;
        
        initRouteProperties();
        
        multiBoxes.clear();
        double y_c = y0 + 0.5 * height;
        double ln_width = height/20;
        if (maxLanes > 10) { // we want the road segment to take about half of the canvas 
            ln_width = Math.sqrt(2)*(height/(2 * maxLanes));
        }
        boolean show_dashes = true;
            if (ln_width < dashes_thres)
                show_dashes = false;
        double y_b = y_c - 0.5 * (maxGPLanes + maxAuxLanes) * ln_width;
        
        double rl = maxRouteLength;
        if (rl < 0)
            rl = routeLength;
        double x_scale = (1 - 2 * h_margin) * width / rl;
        
        
        if (rightSideRoads) {
            double xl = (width / 2) - (x_scale * routeLength / 2);
            
            for (Segment s : segments) {
                AbstractLink l = s.fwy();
                if (l instanceof LinkConnector)
                    xl += x_scale * c_gap;
                double xu = xl + x_scale * l.get_length_meters();                
                double yl = y_b - l.get_mng_lanes() * ln_width;
                double yu = yl + l.get_lanes() * ln_width;
                int mng = l.get_mng_lanes();
                int aux = l.get_aux_lanes();
                int gp = l.get_gp_lanes();
                
                MultiXYBox mb = new MultiXYBox();
                
                int maxRampLanes = 0;
                for (AbstractLink r : s.get_ors())
                    maxRampLanes = Math.max(maxRampLanes, r.get_lanes());
                for (AbstractLink r : s.get_frs())
                    maxRampLanes = Math.max(maxRampLanes, r.get_lanes());
                double coeff = Math.min(1.0, (double)l.get_lanes()/(double)maxRampLanes);
                double r_ln_width = coeff * ln_width;
                r_ln_width = Math.min(r_ln_width, (xu - xl) / (6 * Math.abs(Math.cos(ramp_angle*deg2rad)) * maxRampLanes));
                boolean r_show_dashes = true;
                if (r_ln_width < dashes_thres)
                    r_show_dashes = false;
                double ramp_length = Math.min(xu - xl, l.get_lanes() * ln_width);
                
                
                double delta = 0.0;
                int num_ramps = l.get_segment().num_out_ors();
                for (int i = 0; i < num_ramps; i++) {
                    AbstractLink r = s.out_ors(i);
                    double rxl = xl - 0.5 * ramp_length + delta;
                    double rxu = xl + 0.5 * ramp_length + delta;
                    double ryl = yu - 0.5 * r.get_lanes() * r_ln_width; 
                    int r_mng = r.get_mng_lanes();
                    int r_gp = r.get_gp_lanes();
                    XYBox orb = new XYBox();
                    if (r_mng > 0) {
                        double ryu = ryl + r_mng * r_ln_width;
                        XYBox b = new XYBox(rxl, rxu, ryl, ryu);
                        b.setColor(Color.BLACK);
                        if (ramp2colors.containsKey(r))
                            b.setColor(ramp2colors.get(r)[1]);
                        orb.addBox(b);
                        ryl = ryu;
                    }
                    if (r_gp > 0) {
                        double ryu = ryl + r_gp * r_ln_width;
                        XYBox b = new XYBox(rxl, rxu, ryl, ryu);
                        b.setColor(Color.DARKGREY);
                        if (ramp2colors.containsKey(r))
                            b.setColor(ramp2colors.get(r)[0]);
                        orb.addBox(b);
                    }
                    if (r_show_dashes)
                        orb.setDashes(computeDashes(r));
                    orb.setLineWidth(1);
                    orb.setAngle(-ramp_angle);
                    mb.addBox(orb);
                    delta += 0.5 * ramp_length + 0.5 * r.get_lanes() * r_ln_width;
                }
                
                delta = 0.0;
                num_ramps = s.num_in_ors();
                for (int i = 0; i < num_ramps; i++) {
                    AbstractLink r = s.in_ors(i);
                    double rxl = xl - 0.5 * ramp_length + delta;
                    double rxu = xl + 0.5 * ramp_length + delta;
                    double ryl = yl - 0.5 * r.get_lanes() * r_ln_width; 
                    int r_mng = r.get_mng_lanes();
                    int r_gp = r.get_gp_lanes();
                    XYBox orb = new XYBox();
                    if (r_mng > 0) {
                        double ryu = ryl + r_mng * r_ln_width;
                        XYBox b = new XYBox(rxl, rxu, ryl, ryu);
                        b.setColor(Color.BLACK);
                        if (ramp2colors.containsKey(r))
                            b.setColor(ramp2colors.get(r)[1]);
                        orb.addBox(b);
                        ryl = ryu;
                    }
                    if (r_gp > 0) {
                        double ryu = ryl + r_gp * r_ln_width;
                        XYBox b = new XYBox(rxl, rxu, ryl, ryu);
                        b.setColor(Color.DARKGREY);
                        if (ramp2colors.containsKey(r))
                            b.setColor(ramp2colors.get(r)[0]);
                        orb.addBox(b);
                    }
                    if (r_show_dashes)
                        orb.setDashes(computeDashes(r));
                    orb.setLineWidth(1);
                    orb.setAngle(ramp_angle);
                    mb.addBox(orb);
                    delta += 0.5 * ramp_length + 0.5 * r.get_lanes() * r_ln_width;
                }
                
                
                delta = 0.0;
                num_ramps = s.num_out_frs();
                for (int i = num_ramps-1; i >= 0; i--) {
                    AbstractLink r = s.out_frs(i);
                    double rxl = xu - 0.5 * ramp_length - delta;
                    double rxu = xu + 0.5 * ramp_length - delta;
                    double ryl = yu - 0.5 * r.get_lanes() * r_ln_width; 
                    int r_mng = r.get_mng_lanes();
                    int r_gp = r.get_gp_lanes();
                    XYBox frb = new XYBox();
                    if (r_mng > 0) {
                        double ryu = ryl + r_mng * r_ln_width;
                        XYBox b = new XYBox(rxl, rxu, ryl, ryu);
                        b.setColor(Color.BLACK);
                        if (ramp2colors.containsKey(r))
                            b.setColor(ramp2colors.get(r)[1]);
                        frb.addBox(b);
                        ryl = ryu;
                    }
                    if (r_gp > 0) {
                        double ryu = ryl + r_gp * r_ln_width;
                        XYBox b = new XYBox(rxl, rxu, ryl, ryu);
                        b.setColor(Color.DARKGREY);
                        if (ramp2colors.containsKey(r))
                            b.setColor(ramp2colors.get(r)[0]);
                        frb.addBox(b);
                    }
                    if (r_show_dashes)
                        frb.setDashes(computeDashes(r));
                    frb.setLineWidth(1);
                    frb.setAngle(ramp_angle);
                    mb.addBox(frb);
                    delta += 0.5 * ramp_length + 0.5 * r.get_lanes() * r_ln_width;
                }
                
                delta = 0.0;
                num_ramps = s.num_in_frs();
                for (int i = num_ramps-1; i >= 0; i--) {
                    AbstractLink r = s.in_frs(i);
                    double rxl = xu - 0.5 * ramp_length - delta;
                    double rxu = xu + 0.5 * ramp_length - delta;
                    double ryl = yl - 0.5 * r.get_lanes() * r_ln_width; 
                    int r_mng = r.get_mng_lanes();
                    int r_gp = r.get_gp_lanes();
                    XYBox frb = new XYBox();
                    if (r_mng > 0) {
                        double ryu = ryl + r_mng * r_ln_width;
                        XYBox b = new XYBox(rxl, rxu, ryl, ryu);
                        b.setColor(Color.BLACK);
                        if (ramp2colors.containsKey(r))
                            b.setColor(ramp2colors.get(r)[1]);
                        frb.addBox(b);
                        ryl = ryu;
                    }
                    if (r_gp > 0) {
                        double ryu = ryl + r_gp * r_ln_width;
                        XYBox b = new XYBox(rxl, rxu, ryl, ryu);
                        b.setColor(Color.DARKGREY);
                        if (ramp2colors.containsKey(r))
                            b.setColor(ramp2colors.get(r)[0]);
                        frb.addBox(b);
                    }
                    if (r_show_dashes)
                        frb.setDashes(computeDashes(r));
                    frb.setLineWidth(1);
                    frb.setAngle(-ramp_angle);
                    mb.addBox(frb);
                    delta += 0.5 * ramp_length + 0.5 * r.get_lanes() * r_ln_width;
                }
                
                
                XYBox mlb = new XYBox();
                if (show_dashes)
                    mlb.setDashes(computeDashes(l));
                mlb.setLineWidth(1);
                if (mng > 0) {
                    yu = yl + mng*ln_width;
                    XYBox b = new XYBox(xl, xu, yl, yu);
                    b.setColor(Color.BLACK);
                    mlb.addBox(b);
                    yl = yu;
                }
                if (gp > 0) {
                    yu = yl + gp*ln_width;
                    XYBox b = new XYBox(xl, xu, yl, yu);
                    b.setColor(Color.DARKGREY);
                    mlb.addBox(b);
                    yl = yu;
                }
                if (aux > 0) {
                    yu = yl + aux*ln_width;
                    XYBox b = new XYBox(xl, xu, yl, yu);
                    b.setColor(Color.LIGHTGREY);
                    mlb.addBox(b);
                    yl = yu;
                }
                
                mb.addBox(mlb);
                mb.setGraphicsContext(gc);
                multiBoxes.add(mb);
                
                xl = xu;
                if (l instanceof LinkConnector)
                    xl += x_scale * c_gap;
            }
        }
        
    }
    
    
    public void draw(double num_routes, double route_num) {
        double width = canvas.getWidth();
        double c_height = canvas.getHeight();
        double height = c_height / num_routes;
        double y0 = route_num * height;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, y0, width, y0 + height);
        multiBoxes.forEach((mb) -> { mb.fill(); });

        if ((selectedIndex < 0) || (selectedIndex >= multiBoxes.size()))
            return;
        
        XYBox bb = multiBoxes.get(selectedIndex).bbox();
        bb.boxes.clear();
        bb.setColor(Color.RED);
        bb.setGraphicsContext(gc);
        bb.setLineWidth(4);
        bb.stroke();
    }
    
    
    public void execute() {
        computeGeometry(1, 0);
        draw(1, 0);
    }
    
    
    public void execute(double num_routes, double route_num) {
        computeGeometry(num_routes, route_num);
        draw(num_routes, route_num);
    }

    
}
