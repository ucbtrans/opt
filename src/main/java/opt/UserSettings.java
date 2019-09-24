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
import java.util.Map;
import opt.data.ParametersFreeway;
import opt.data.ParametersRamp;


/**
 * This class contains user settings and provides methods for necessary conversions.
 *
 * @author Alex Kurzhanskiy
 */
public class UserSettings {

    public static boolean rightSideRoads = true; // Read from user settings file
    
    public static String[] unitsLengthOptions = {"meters", "feet", "kilometers", "miles"};
    public static String unitsLength = unitsLengthOptions[3]; // Read from user settings file
    
    public static String[] unitsSpeedOptions = {"mps", "fps", "mph", "kph"};
    public static String unitsSpeed = unitsSpeedOptions[2]; // Read from user settings file
    
    public static String[] unitsFlowOptions = {"vps", "vpm", "vp5m", "vp15m", "vph"};
    public static String unitsFlow = unitsFlowOptions[4]; // Read from user settings file
    
    public static String[] unitsDensityOptions = {"vpmtr", "vpf", "vpm", "vpkm"};
    public static String unitsDensity = unitsDensityOptions[2]; // Read from user settings file
    
    
    public static double defaultMergePriority = 0.5; // ramp merge priority
    
    public static double defaultRampLengthMeters = 322; // meters
    
    public static int defaultOnrampGPLanes = 1;
    public static int defaultOfframpGPLanes = 1;
    public static int defaultConnectorGPLanes = 2;
    public static int defaultFreewayGPLanes = 3;
    
    public static int defaultOnrampManagedLanes = 1;
    public static int defaultOfframpManagedLanes = 0;
    public static int defaultConnectorManagedLanes = 1;
    public static int defaultFreewayManagedLanes = 1;
    
    public static int defaultOnrampAuxLanes = 0;
    public static int defaultOfframpAuxLanes = 0;
    public static int defaultConnectorAuxLanes = 0;
    public static int defaultFreewayAuxLanes = 0;
    
    public static double defaultGPLaneCapacityVph = 1900;
    public static double defaultManagedLaneCapacityVph = 1800;
    public static double defaultAuxLaneCapacityVph = 950;
    
    public static double defaultGPLaneFreeFlowSpeedKph = 105;
    public static double defaultManagedLaneFreeFlowSpeedKph = 115;
    public static double defaultAuxLaneFreeFlowSpeedKph = 90;
    
    public static double defaultGPLaneJamDensityVpk = 110;
    public static double defaultManagedLaneJamDensityVpk = 110;
    public static double defaultAuxLaneJamDensityVpk = 110;
    
    
    
    
    // Length conversion
    private static Map<String, Double> lengthConversionMap = new HashMap<String, Double>();
    static {
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


    // Speed conversion
    private static Map<String, Double> speedConversionMap = new HashMap<String, Double>();
    static {
        speedConversionMap.put("mpsmps", new Double(1));
        speedConversionMap.put("mpsfps", new Double(3.28084));
        speedConversionMap.put("mpsmph", new Double(2.23694));
        speedConversionMap.put("mpskph", new Double(3.6));
        speedConversionMap.put("fpsmps", new Double(0.3048));
        speedConversionMap.put("fpsfps", new Double(1));
        speedConversionMap.put("fpsmph", new Double(0.681818));
        speedConversionMap.put("fpskph", new Double(1.09728));
        speedConversionMap.put("mphmps", new Double(0.44704));
        speedConversionMap.put("mphfps", new Double(1.46667));
        speedConversionMap.put("mphmph", new Double(1));
        speedConversionMap.put("mphkph", new Double(1.60936));
        speedConversionMap.put("kphmps", new Double(0.277778));
        speedConversionMap.put("kphfps", new Double(0.911344));
        speedConversionMap.put("kphmph", new Double(0.621371));
        speedConversionMap.put("kphkph", new Double(1));
    }
    
    
    // Flow conversion (vehicles per <unit of time>: vps, vpm, vp5m, vp15m, vph)
    private static Map<String, Double> flowConversionMap = new HashMap<String, Double>();
    static {
        flowConversionMap.put("vpsvps", new Double(1));
        flowConversionMap.put("vpsvpm", new Double(60));
        flowConversionMap.put("vpsvp5m", new Double(300));
        flowConversionMap.put("vpsvp15m", new Double(900));
        flowConversionMap.put("vpsvph", new Double(3600));
        flowConversionMap.put("vpmvps", new Double(1.0/60.0));
        flowConversionMap.put("vpmvpm", new Double(1));
        flowConversionMap.put("vpmvp5m", new Double(5));
        flowConversionMap.put("vpmvp15m", new Double(15));
        flowConversionMap.put("vpmvph", new Double(60));
        flowConversionMap.put("vp5mvps", new Double(1.0/300.0));
        flowConversionMap.put("vp5mvpm", new Double(1.0/5.0));
        flowConversionMap.put("vp5mvp5m", new Double(1));
        flowConversionMap.put("vp5mvp15m", new Double(3));
        flowConversionMap.put("vp5mvph", new Double(12));
        flowConversionMap.put("vp15mvps", new Double(1.0/900.0));
        flowConversionMap.put("vp15mvpm", new Double(1.0/15.0));
        flowConversionMap.put("vp15mvp5m", new Double(1.0/3.0));
        flowConversionMap.put("vp15mvp15m", new Double(1));
        flowConversionMap.put("vp15mvph", new Double(4));
        flowConversionMap.put("vphvps", new Double(1.0/3600.0));
        flowConversionMap.put("vphvpm", new Double(1.0/60.0));
        flowConversionMap.put("vphvp5m", new Double(1.0/12.0));
        flowConversionMap.put("vphvp15m", new Double(1.0/4.0));
        flowConversionMap.put("vphvph", new Double(1));
    }
    
    
    // Density conversion (vehicles per <unit of length>: vpmtr, vpf, vpm, vpkm)
    private static Map<String, Double> densityConversionMap = new HashMap<String, Double>();
    static {
        densityConversionMap.put("vpmtrvpmtr", new Double(1)); // mtr = meter => vpmtr = vehicles per meter
        densityConversionMap.put("vpmtrvpf", new Double(0.3048));
        densityConversionMap.put("vpmtrvpm", new Double(1609.34));
        densityConversionMap.put("vpmtrvpkm", new Double(1000));
        densityConversionMap.put("vpfvpmtr", new Double(3.28084));
        densityConversionMap.put("vpfvpf", new Double(1));
        densityConversionMap.put("vpfvpm", new Double(5280));
        densityConversionMap.put("vpfvpkm", new Double(3280.84));
        densityConversionMap.put("vpmvpmtr", new Double(0.000621371));
        densityConversionMap.put("vpmvpf", new Double(0.000189394));
        densityConversionMap.put("vpmvpm", new Double(1));
        densityConversionMap.put("vpmvpkm", new Double(0.621371));
        densityConversionMap.put("vpkmvpmtr", new Double(0.001));
        densityConversionMap.put("vpkmvpf", new Double(0.0003048));
        densityConversionMap.put("vpkmvpm", new Double(1.60934));
        densityConversionMap.put("vpkmvpkm", new Double(1));
    }
    
    
    
    public static ParametersRamp getDefaultOfframpParams(String name, Float length) {
        return new ParametersRamp(
                name,
                false,
                defaultOfframpGPLanes,
                defaultOfframpManagedLanes,
                false,
                false,
                length!=null ? length : 100f,
                (float)defaultGPLaneCapacityVph,
                (float)defaultGPLaneJamDensityVpk,
                (float)defaultGPLaneFreeFlowSpeedKph,
                (float)defaultManagedLaneCapacityVph,
                (float)defaultManagedLaneJamDensityVpk,
                (float)defaultManagedLaneFreeFlowSpeedKph);
    }


    public static ParametersRamp getDefaultOnrampParams(String name, Float length) {
        return new ParametersRamp(
                name,
                false,
                defaultOnrampGPLanes,
                defaultOnrampManagedLanes,
                false,
                false,
                length!=null ? length : 100f,
                (float)defaultGPLaneCapacityVph,
                (float)defaultGPLaneJamDensityVpk,
                (float)defaultGPLaneFreeFlowSpeedKph,
                (float)defaultManagedLaneCapacityVph,
                (float)defaultManagedLaneJamDensityVpk,
                (float)defaultManagedLaneFreeFlowSpeedKph);
    }

    public static ParametersFreeway getDefaultConnectorParams(String name, Float length) {
        return new ParametersFreeway(
                name,
                defaultConnectorGPLanes,
                defaultConnectorManagedLanes,
                false,
                false,
                defaultConnectorAuxLanes,
                length!=null ? length : 100f,
                (float)defaultGPLaneCapacityVph,
                (float)defaultGPLaneJamDensityVpk,
                (float)defaultGPLaneFreeFlowSpeedKph,
                (float)defaultManagedLaneCapacityVph,
                (float)defaultManagedLaneJamDensityVpk,
                (float)defaultManagedLaneFreeFlowSpeedKph,
                (float)defaultAuxLaneCapacityVph,
                (float)defaultAuxLaneJamDensityVpk,
                (float)defaultAuxLaneFreeFlowSpeedKph);
    }

    public static ParametersFreeway getDefaultFreewayParams(String name, Float length) {
        return new ParametersFreeway(
                name,
                defaultFreewayGPLanes,
                defaultFreewayManagedLanes,
                false,
                false,
                defaultFreewayAuxLanes,
                length!=null ? length : 100f,
                (float)defaultGPLaneCapacityVph,
                (float)defaultGPLaneJamDensityVpk,
                (float)defaultGPLaneFreeFlowSpeedKph,
                (float)defaultManagedLaneCapacityVph,
                (float)defaultManagedLaneJamDensityVpk,
                (float)defaultManagedLaneFreeFlowSpeedKph,
                (float)defaultAuxLaneCapacityVph,
                (float)defaultAuxLaneJamDensityVpk,
                (float)defaultAuxLaneFreeFlowSpeedKph);
    }
    

    
    public static double convertLength(double value, String fromUnits, String toUnits) {
        Double res = lengthConversionMap.get(fromUnits + toUnits);
        if (res == null)
            res = new Double(1);
        return res * value;
    }
    
    public static double convertSpeed(double value, String fromUnits, String toUnits) {
        Double res = speedConversionMap.get(fromUnits + toUnits);
        if (res == null)
            res = new Double(1);
        return res * value;
    }
    
    public static double convertFlow(double value, String fromUnits, String toUnits) {
        Double res = flowConversionMap.get(fromUnits + toUnits);
        if (res == null)
            res = new Double(1);
        return res * value;
    }
    
    public static double convertDensity(double value, String fromUnits, String toUnits) {
        Double res = densityConversionMap.get(fromUnits + toUnits);
        if (res == null)
            res = new Double(1);
        return res * value;
    }
    
    
    
    
    
}
