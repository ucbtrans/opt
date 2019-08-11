/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opt.utils;

import opt.data.AbstractLink.Type;

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
    
}
