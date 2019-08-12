/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opt.utils;

import opt.data.AbstractLink.Type;
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
    
    
    
    
    
    
    
    
    
    
    
}
