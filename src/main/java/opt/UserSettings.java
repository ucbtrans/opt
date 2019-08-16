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
package opt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import opt.data.LinkParameters;




/**
 * This class contains user settings and provides methods for necessary conversions.
 *
 * @author Alex Kurzhanskiy
 */
public class UserSettings {
    public boolean rightSideRoads = true;
    private String[] unitsLengthOptions = {"meters", "feet", "kilometers", "miles"};
    public String unitsLength = "miles";
    
    
    
    public double defaultRampLengthMeters = 322; // meters
    
    public int defaultOnrampGPLanes = 1;
    public int defaultOfframpGPLanes = 1;
    public int defaultConnectorGPLanes = 2;
    public int defaultFreewayGPLanes = 3;
    
    public int defaultOnrampManagedLanes = 1;
    public int defaultOfframpManagedLanes = 0;
    public int defaultConnectorManagedLanes = 1;
    public int defaultFreewayManagedLanes = 1;
    
    public int defaultOnrampAuxLanes = 0;
    public int defaultOfframpAuxLanes = 0;
    public int defaultConnectorAuxLanes = 0;
    public int defaultFreewayAuxLanes = 0;
    
    public double defaultGPLaneCapacityVph = 1900;
    public double defaultManagedLaneCapacityVph = 1800;
    public double defaultAuxLaneCapacityVph = 950;
    
    public double defaultGPLaneFreeFlowSpeedKph = 105;
    public double defaultManagedLaneFreeFlowSpeedKph = 115;
    public double defaultAuxLaneFreeFlowSpeedKph = 90;
    
    public double defaultGPLaneJamDensityVpk = 110;
    public double defaultManagedLaneJamDensityVpk = 110;
    public double defaultAuxLaneJamDensityVpk = 110;
    
    
    
    
    
    private Map<String, Double> lengthConversionMap = new HashMap<String, Double>();
    
    
    public UserSettings() {
        lengthConversionMap.put("metersmeters", new Double(1));
        lengthConversionMap.put("metersfeet", new Double(3.28084));
        lengthConversionMap.put("meterskilometers", new Double(0.001));
        lengthConversionMap.put("metersmiles", new Double(0.000621371));
        lengthConversionMap.put("feetmeters", new Double(0.3048));
        lengthConversionMap.put("feetfeet", new Double(1));
        lengthConversionMap.put("feetkilometers", new Double(0.0003048));
        lengthConversionMap.put("feetmiles", new Double(0.000189394));
        lengthConversionMap.put("kilometersmeters", new Double(1000));
        lengthConversionMap.put("kilometersfeet", new Double(3280.84));
        lengthConversionMap.put("kilometerskilometers", new Double(1));
        lengthConversionMap.put("kilometersmiles", new Double(0.621371));
        lengthConversionMap.put("milesmeters", new Double(1609.34));
        lengthConversionMap.put("milesfeet", new Double(5280));
        lengthConversionMap.put("mileskilometers", new Double(1.60934));
        lengthConversionMap.put("milesmiles", new Double(1));
        
    }


    public LinkParameters getDefaultOfframpParams(String name,Float length) {
        return new LinkParameters(
                name,
                defaultOfframpGPLanes,
                defaultOfframpManagedLanes,
                defaultOfframpAuxLanes,
                length!=null ? length : 100f,
                (float)defaultGPLaneCapacityVph,
                (float)defaultGPLaneJamDensityVpk,
                (float)defaultGPLaneFreeFlowSpeedKph);
    }


    public LinkParameters getDefaultOnrampParams(String name,Float length) {
        return new LinkParameters(
                name,
                defaultOnrampGPLanes,
                defaultOnrampManagedLanes,
                defaultOnrampAuxLanes,
                length!=null ? length : 100f,
                (float)defaultGPLaneCapacityVph,
                (float)defaultGPLaneJamDensityVpk,
                (float)defaultGPLaneFreeFlowSpeedKph);
    }

    public LinkParameters getDefaultConnectorParams(String name,Float length) {
        return new LinkParameters(
                name,
                defaultConnectorGPLanes,
                defaultConnectorManagedLanes,
                defaultConnectorAuxLanes,
                length!=null ? length : 100f,
                (float)defaultGPLaneCapacityVph,
                (float)defaultGPLaneJamDensityVpk,
                (float)defaultGPLaneFreeFlowSpeedKph);
    }

    public LinkParameters getDefaultFreewayParams(String name,Float length) {
        return new LinkParameters(
                name,
                defaultFreewayGPLanes,
                defaultFreewayManagedLanes,
                defaultFreewayAuxLanes,
                length!=null ? length : 100f,
                (float)defaultGPLaneCapacityVph,
                (float)defaultGPLaneJamDensityVpk,
                (float)defaultGPLaneFreeFlowSpeedKph);
    }
    
    
    
    
    
    
    
    public double convertLength(double value, String fromUnits, String toUnits) {
        Double res = lengthConversionMap.get(fromUnits + toUnits);
        if (res == null)
            res = new Double(1);
        return res * value;
    }
    
    
    
    
    
}
