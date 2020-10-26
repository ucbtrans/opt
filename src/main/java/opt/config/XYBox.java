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
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * XY box geometry and plotting.
 * 
 * @author akurzhan@berkeley.edu
 */
public class XYBox {
    private final double deg2rad = 0.0174533;
    public double xl = 0.0;
    public double xu = 0.0;
    public double yl = 0.0;
    public double yu = 0.0;
    
    public double angle = 0.0;
    
    public GraphicsContext gc = null;
    public Color color = Color.DARKGREY;
    public Color strokeColor = Color.WHITE;
    public int lineWidth = 2;
    public int[] dashes = null;
    
    public List<XYBox> boxes = new ArrayList();
    
    
    public XYBox() { }
    
    public XYBox(double xl, double xu, double yl, double yu) {
        this.xl = xl;
        this.xu = xu;
        this.yl = yl;
        this.yu = yu;
    }
    
    public XYBox(List<XYBox> bb) {
        if (bb == null)
            return;
        
        int sz = bb.size();
        for (int i = 0; i < sz; i++) {
            XYBox b = bb.get(i);
            boxes.add(b);
            if (i == 0) {
                xl = b.xl;
                xu = b.xu;
                yl = b.yl;
                yu = b.yu;
            } else {
                xl = Math.min(xl, b.xl);
                xu = Math.max(xu, b.xu);
                yl = Math.min(yl, b.yl);
                yu = Math.max(yu, b.yu);
            }
        }  
    }
    
    
    
    public void setBounds(double xl, double xu, double yl, double yu) {
        this.xl = xl;
        this.xu = xu;
        this.yl = yl;
        this.yu = yu;
    }
    
    
    public void setAngle(double angle) {
        this.angle = angle;
        
        if (boxes.isEmpty())
            return;
        
        boxes.forEach((b) -> { b.setAngle(angle); });
    }
    
    
    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
        
        if (boxes.isEmpty())
            return;
        
        boxes.forEach((b) -> { b.setGraphicsContext(gc); });
    }
    
    
    public void setColor(Color c) {
        color = c;
        
        if (boxes.isEmpty())
            return;
        
        boxes.forEach((b) -> { b.setColor(c); });
    }
    
    
    public void setLineWidth(int lw) {
        final int w = Math.max(lw, 1);
        
        if (boxes.isEmpty())
            return;
        
        boxes.forEach((b) -> { b.setLineWidth(w); });
    }
    
    
    public void setDashes(int[] d) {
        dashes = d;
    }
    
    
    
    public void addBox(XYBox b) {
        if (boxes.isEmpty()) {
            xl = b.xl;
            xu = b.xu;
            yl = b.yl;
            yu = b.yu;
        } else {
            xl = Math.min(xl, b.xl);
            xu = Math.max(xu, b.xu);
            yl = Math.min(yl, b.yl);
            yu = Math.max(yu, b.yu);
        }
        b.setGraphicsContext(gc);
        b.setAngle(angle);
        boxes.add(b);
    }
    
    
    public boolean isinside(double x, double y) {
        double s = Math.sin(-angle * deg2rad);
        double c = Math.cos(-angle * deg2rad);
        double x_c = xl + (xu - xl) / 2;
        double y_c = yl + (yu - yl) / 2;
        
        double x0 = x - x_c;
        double y0 = y - y_c;
        
        double n_x = x0*c - y0*s + x_c;
        double n_y = x0*s + y0*c + y_c;
        
        return (n_x >= xl) && (n_x <= xu) && (n_y >= yl) && (n_y <= yu);
    }
    
    
    public XYBox bbox() {
        double s = Math.sin(angle * deg2rad);
        double c = Math.cos(angle * deg2rad);
        double x_c = xl + (xu - xl) / 2;
        double y_c = yl + (yu - yl) / 2;
        
        double x00 = xl - x_c;
        double y00 = yl - y_c;
        x00 = x00*c - y00*s + x_c;
        y00 = x00*s + y00*c + y_c;
        
        double x01 = xl - x_c;
        double y01 = yu - y_c;
        x01 = x01*c - y01*s + x_c;
        y01 = x01*s + y01*c + y_c;
        
        double x10 = xu - x_c;
        double y10 = yl - y_c;
        x10 = x10*c - y10*s + x_c;
        y10 = x10*s + y10*c + y_c;
        
        double x11 = xu - x_c;
        double y11 = yu - y_c;
        x11 = x11*c - y11*s + x_c;
        y11 = x11*s + y11*c + y_c;
        
        double xxl = Math.min(x00, Math.min(x01, Math.min(x10, x11)));
        double xxu = Math.max(x00, Math.max(x01, Math.max(x10, x11)));
        double yyl = Math.min(y00, Math.min(y01, Math.min(y10, y11)));
        double yyu = Math.max(y00, Math.max(y01, Math.max(y10, y11)));
        
        return new XYBox(xxl, xxu, yyl, yyu);
    }
    
    
    public void make_dashes() {
        if (dashes == null)
            return;
        
        double x_c = xl + (xu - xl) / 2;
        double y_c = yl + (yu - yl) / 2;
        
        double num_lanes = dashes.length + 1;
        double ln_width = (yu - yl) / num_lanes;
        
        double y = yl + ln_width;
        
        gc.save();
        gc.translate(x_c, y_c);
        gc.rotate(angle);
        gc.translate(-x_c, -y_c);
        gc.setStroke(strokeColor);
        gc.setLineWidth(lineWidth);
        
        for (int i = 0; i < dashes.length; i++) {
            double f = dashes[i];
            if (f < 1)
                gc.setLineDashes();
            else
                gc.setLineDashes(ln_width / (f + 1), ln_width / f);
            
            gc.strokeLine(xl, y, xu, y);
            y += ln_width;
        }
        
        gc.restore();
    }
    
    
    public void fill() {
        double x_c = xl + (xu - xl) / 2;
        double y_c = yl + (yu - yl) / 2;
        
        if (!boxes.isEmpty()) {
            boxes.forEach((b) -> { b.fill(x_c, y_c); });
            make_dashes();
            return;
        }
        
        if  (gc == null)
            return;
        
        gc.save();
        gc.translate(x_c, y_c);
        gc.rotate(angle);
        gc.translate(-x_c, -y_c);
        gc.setFill(color);
        gc.fillRect(xl, yl, xu - xl, yu - yl);
        gc.restore();
        
        make_dashes();
    }
    
    public void fill(double x_c, double y_c) {
        if  (gc == null)
            return;
        
        gc.save();
        gc.translate(x_c, y_c);
        gc.rotate(angle);
        gc.translate(-x_c, -y_c);
        gc.setFill(color);
        gc.fillRect(xl, yl, xu - xl, yu - yl);
        gc.restore();
    }
    
    
    
    public void stroke() {
        if (!boxes.isEmpty()) {
            boxes.forEach((b) -> { b.stroke(xl + (xu - xl) / 2, yl + (yu - yl) / 2); });
            return;
        }
        
        if  (gc == null)
            return;
        
        double x_c = xl + (xu - xl) / 2;
        double y_c = yl + (yu - yl) / 2;
        gc.save();
        gc.translate(x_c, y_c);
        gc.rotate(angle);
        gc.translate(-x_c, -y_c);
        gc.setStroke(color);
        gc.setLineWidth(lineWidth);
        gc.strokeRect(xl, yl, xu - xl, yu - yl);
        gc.restore();
    }
    
    public void stroke(double x_c, double y_c) {
        if  (gc == null)
            return;
        
        gc.save();
        gc.translate(x_c, y_c);
        gc.rotate(angle);
        gc.translate(-x_c, -y_c);
        gc.setStroke(color);
        gc.setLineWidth(lineWidth);
        gc.strokeRect(xl, yl, xu - xl, yu - yl);
        gc.restore();
    }
    
    
}
