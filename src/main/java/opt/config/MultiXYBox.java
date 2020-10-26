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
 * Manipulation with multiple XYBox objects.
 * 
 * @author akurzhan@berkeley.edu
 */
public class MultiXYBox {
    public double xl = 0.0;
    public double xu = 0.0;
    public double yl = 0.0;
    public double yu = 0.0;
    
    
    public List<XYBox> boxes = new ArrayList();
    
    
    public MultiXYBox() { }
    
    
    public MultiXYBox(List<XYBox> bb) {
        if (bb == null)
            return;
        
        int sz = bb.size();
        for (int i = 0; i < sz; i++) {
            XYBox b = bb.get(i).bbox();
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
    
    public void setAngle(double angle) {
        boxes.forEach((b) -> { b.setAngle(angle); });
    }
    
    
    public void setGraphicsContext(GraphicsContext gc) {
        boxes.forEach((b) -> { b.setGraphicsContext(gc); });
    }
    
    
    public void setColor(Color c) {
        boxes.forEach((b) -> { b.setColor(c); });
    }
    
    
    public void addBox(XYBox b) {
        XYBox bb = b.bbox();
        if (boxes.isEmpty()) {
            xl = bb.xl;
            xu = bb.xu;
            yl = bb.yl;
            yu = bb.yu;
        } else {
            xl = Math.min(xl, bb.xl);
            xu = Math.max(xu, bb.xu);
            yl = Math.min(yl, bb.yl);
            yu = Math.max(yu, bb.yu);
        }
        boxes.add(b);
    }
    
    
    public boolean isinside(double x, double y) {
        boolean in = false;
        for (XYBox b : boxes)
            in |= b.isinside(x, y);
        return in;
    }
    
    
    public XYBox bbox() {
       XYBox box = new XYBox();
       for (XYBox b : boxes)
           if (Math.abs(b.angle) < 0.1)
           box.addBox(b.bbox());
       
       return box;
    }
    
    
    public void fill() {
        boxes.forEach((b) -> { b.fill(); });
    }
    
    
    
    
    
}
