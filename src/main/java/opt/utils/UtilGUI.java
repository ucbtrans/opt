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

package opt.utils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javafx.stage.FileChooser;
import org.jfree.chart.ChartUtilities;


/**
 * Utilities for plotting.
 * @author Alex Kurzhanskiy
 */
public class UtilGUI {
	
    /**
     * Returns n-dimensional array of colors for given nx3 integer array of RGB values. 
     */
    public static Color[] getColorScale(int[][] rgb) {
        if (rgb == null)
            return null;
        Color[] clr = new Color[rgb.length];
        for (int i = 0; i < rgb.length; i++) {
            float[] hsb =  Color.RGBtoHSB(rgb[i][0], rgb[i][1], rgb[i][2], null);
            clr[i] = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
        }
        return clr;
    }

    /**
     * Returns blue-yellow-red color scale.
     */
    public static Color[] byrColorScale() {
        int[][] rgb = {
            {0, 0, 0},
            {0, 0, 159},
            {0, 0, 191},
            {0, 0, 223},
            {0, 0, 255},
            {0, 32, 255},
            {0, 64, 255},
            {0, 96, 255},
            {0, 128, 255},
            {0, 159, 255},
            {0, 191, 255},
            {0, 223, 255},
            {0, 255, 255},
            {32, 255, 223},
            {64, 255, 191},
            {96, 255, 159},
            {128, 255, 128},
            {159, 255, 96},
            {191, 255, 64},
            {223, 255, 32},
            {255, 255, 0},
            {255, 223, 0},
            {255, 191, 0},
            {255, 159, 0},
            {255, 128, 0},
            {255, 96, 0},
            {255, 64, 0},
            {255, 32, 0},
            {255, 0, 0},
            {223, 0, 0},
            {191, 0, 0}
        };
        return getColorScale(rgb);
    }

    /**
     * Returns green-yellow-red-black color scale.
     */
    public static Color[] gyrkColorScale() {
        int[][] rgb = {
            {0, 0, 0},
            {0, 164, 0},
            {19, 174, 0},
            {41, 184, 0},
            {65, 194, 0},
            {91, 204, 0},
            {119, 215, 0},
            {150, 225, 0},
            {183, 235, 0},
            {218, 245, 0},
            {255, 255, 0},
            {255, 202, 0},
            {255, 180, 0},
            {255, 157, 0},
            {255, 135, 0},
            {255, 112, 0},
            {255, 90, 0},
            {255, 67, 0},
            {255, 45, 0},
            {255, 22, 0},
            {255, 0, 0},
            {229, 0, 0},
            {206, 0, 0},
            {183, 0, 0},
            {160, 0, 0},
            {137, 0, 0},
            {113, 0, 0},
            {90, 0, 0},
            {67, 0, 0},
            {44, 0, 0},
            {21, 0, 0}
        };
        return getColorScale(rgb);
    }

    /**
     * Returns black-red-yellow-green color scale.
     */
    public static Color[] krygColorScale() {
        int[][] rgb = {
            {0, 0, 0},
            {21, 0, 0},
            {44, 0, 0},
            {67, 0, 0},
            {90, 0, 0},
            {113, 0, 0},
            {137, 0, 0},
            {160, 0, 0},
            {183, 0, 0},
            {206, 0, 0},
            {229, 0, 0},
            {255, 0, 0},
            {255, 22, 0},
            {255, 45, 0},
            {255, 67, 0},
            {255, 90, 0},
            {255, 112, 0},
            {255, 135, 0},
            {255, 157, 0},
            {255, 180, 0},
            {255, 202, 0},
            {255, 255, 0},
            {218, 245, 0},
            {183, 235, 0},
            {150, 225, 0},
            {119, 215, 0},
            {91, 204, 0},
            {65, 194, 0},
            {41, 184, 0},
            {19, 174, 0},
            {0, 164, 0}
        };
        return getColorScale(rgb);
    }

    /**
     * Returns black and white color scale.
     */
    public static Color[] bwColorScale() {
        int[][] rgb = {
            {0, 0, 0},
            {8, 8, 8},
            {17, 17, 17},
            {25, 25, 25},
            {34, 34, 34},
            {42, 42, 42},
            {51, 51, 51},
            {59, 59, 59},
            {68, 68, 68},
            {76, 76, 76},
            {85, 85, 85},
            {93, 93, 93},
            {102, 102, 102},
            {110, 110, 110},
            {119, 119, 119},
            {127, 127, 127},
            {136, 136, 136},
            {144, 144, 144},
            {153, 153, 153},
            {161, 161, 161},
            {170, 170, 170},
            {178, 178, 178},
            {187, 187, 187},
            {195, 195, 195},
            {204, 204, 204},
            {212, 212, 212},
            {221, 221, 221},
            {229, 229, 229},
            {238, 238, 238},
            {246, 246, 246},
            {255, 255, 255}
        };
        return getColorScale(rgb);
    }

    /**
     * Returns color based on 0-9 scale ranging from green to yellow.
     */
    public static Color gyColor(int i) {
        int[][] rgb = {
            {0, 164, 0},
            {19, 174, 0},
            {41, 184, 0},
            {65, 194, 0},
            {91, 204, 0},
            {119, 215, 0},
            {150, 225, 0},
            {183, 235, 0},
            {218, 245, 0},
            {255, 255, 0}
        };
        int ii = 0;
        if (i > 9)
            ii = 9;
        else
            ii = Math.max(i, ii);
        float[] hsb = Color.RGBtoHSB(rgb[ii][0], rgb[ii][1], rgb[ii][2], null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    /**
     * Returns color based on 0-9 scale ranging from yellow to red.
     */
    public static Color yrColor(int i) {
        int[][] rgb = {
            {255, 202, 0},
            {255, 180, 0},
            {255, 157, 0},
            {255, 135, 0},
            {255, 112, 0},
            {255, 90, 0},
            {255, 67, 0},
            {255, 45, 0},
            {255, 22, 0},
            {255, 0, 0}
        };
        int ii = 0;
        if (i > 9)
            ii = 9;
        else
            ii = Math.max(i, ii);
        float[] hsb =  Color.RGBtoHSB(rgb[ii][0], rgb[ii][1], rgb[ii][2], null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    /**
     * Returns color based on 0-9 scale ranging from red to black.
     */
    public static Color rkColor(int i) {
        int[][] rgb = {
            {229, 0, 0},
            {206, 0, 0},
            {183, 0, 0},
            {160, 0, 0},
            {137, 0, 0},
            {113, 0, 0},
            {90, 0, 0},
            {67, 0, 0},
            {44, 0, 0},
            {21, 0, 0}
        };
        int ii = 0;
        if (i > 9)
            ii = 9;
        else
            ii = Math.max(i, ii);
        float[] hsb =  Color.RGBtoHSB(rgb[ii][0], rgb[ii][1], rgb[ii][2], null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    /**
     * Returns color based on 0-9 scale ranging from black to green.
     */
    public static Color kgColor(int i) {
        int[][] rgb = {
            {21, 0, 0},
            {99, 0, 0},
            {177, 0, 0},
            {255, 0, 0},
            {255, 85, 0},
            {255, 170, 0},
            {255, 255, 0},
            {150, 225, 0},
            {65, 194, 0},
            {0, 164, 0}
        };
        int ii = 0;
        if (i > 9)
            ii = 9;
        else
            ii = Math.max(i, ii);
        float[] hsb =  Color.RGBtoHSB(rgb[ii][0], rgb[ii][1], rgb[ii][2], null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
    
    
    
    /**
     * A handler for the export to PNG option in the context menu.
     */
    private void handleExportToPNG() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export to PNG");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Portable Network Graphics (PNG)", "*.png");
        chooser.getExtensionFilters().add(filter);
        /*File file = chooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try {
                ChartUtilities.saveChartAsPNG(file, this.canvas.getChart(), 2*(int)getWidth(), 2*(int)getHeight());
            } catch (IOException ex) {
                // FIXME: show a dialog with the error
                throw new RuntimeException(ex);
            }
        }*/   
    }

    /**
     * A handler for the export to JPEG option in the context menu.
     */
    private void handleExportToJPEG() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export to JPEG");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JPEG", "*.jpg");
        chooser.getExtensionFilters().add(filter);
        /*File file = chooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try {
                ChartUtilities.saveChartAsJPEG(file, this.canvas.getChart(), 2*(int)getWidth(), 2*(int)getHeight());
            } catch (IOException ex) {
                // FIXME: show a dialog with the error
                throw new RuntimeException(ex);
            }
        }  */      
    }
	
}