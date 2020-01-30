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

import java.text.DecimalFormat;
import java.util.Map;
import opt.data.AbstractLink.Type;
import opt.data.Commodity;
import opt.data.FreewayScenario;

/**
 *
 * @author Alex Kurzhanskiy
 */
public class Misc {
    
    
    public static String linkType2String(Type t) {
        if (t == Type.freeway)
            return "Freeway";
        else if (t == Type.connector)
            return "Connector";
        else if (t == Type.onramp)
            return "On-Ramp";
        else if (t == Type.offramp)
            return "Off-Ramp";
        
        return "Unknown";
    }
    
    
    
    public static String validateAndCorrectLinkName(String link_name, FreewayScenario scenario) {
        String corrected_name = link_name;
        if (scenario == null)
            return corrected_name;
        
        if ((!scenario.is_valid_link_name(link_name)) ||
            (!scenario.is_valid_segment_name(link_name))) {
            int count = 1;
            corrected_name = link_name + "(" + count + ")";
            while ((!scenario.is_valid_link_name(corrected_name)) ||
                   (!scenario.is_valid_segment_name(corrected_name))) {
                count++;
                corrected_name = link_name + "(" + count + ")";
            }
        }
        return corrected_name;
    }
    
    
    public static String validateAndCorrectVehicleTypeName(String vt_name, FreewayScenario scenario, Commodity comm) {
        String corrected_name = vt_name;
        if (scenario == null)
            return corrected_name;
        if ((comm != null) && comm.get_name().equals(corrected_name))
            return corrected_name;
        
        int count = 1;
        Commodity another_comm = scenario.get_commodity_by_name(corrected_name);
        while (another_comm != null) {
            corrected_name = vt_name + "(" + count + ")";
            another_comm = scenario.get_commodity_by_name(corrected_name);
            count++;
        }
        
        return corrected_name;
    }
    
    
    public static String minutes2timeString(int minutes) {
        String timeStr = "";
        int t = minutes;
        if (t < 0) {
            timeStr += "-";
            t = -t;
        }
        
        DecimalFormat df = new DecimalFormat("00");
        int h = t / 60;
        int m = t - 60*h; 
        timeStr += df.format(h) + ":" + df.format(m);
        
        return timeStr;
    }
    
    
    public static String seconds2timestring(float seconds, String divider) {
        String timeStr = "";
        int t = Math.round(seconds);
        if (t < 0) {
            timeStr += "-";
            t = -t;
        }
        
        DecimalFormat df = new DecimalFormat("00");
        int h = t / 3600;
        int m = (t - 3600*h) / 60; 
        timeStr += df.format(h) + divider + df.format(m);
        
        return timeStr;
    }
    
    
    public static int timeString2Seconds(String time_str) {
        String[] comps = (time_str+":00").split(":");
        if (comps[0].length() < 1)
            comps[0] = "00";
        else if (comps[0].length() < 2)
            comps[0] = "0" + comps[0];
        
        if (comps[1].length() < 1)
            comps[1] = "00";
        else if (comps[1].length() < 2)
            comps[1] = "0" + comps[1];
        
        int seconds = 3600*Integer.parseInt(comps[0]) + 60*Integer.parseInt(comps[1]);
        
        if (comps.length > 2)
            if (comps[2].length() > 0)
                seconds += Integer.parseInt(comps[2]);
        
        return seconds;
    }
    
    
    
    
    
    
}
