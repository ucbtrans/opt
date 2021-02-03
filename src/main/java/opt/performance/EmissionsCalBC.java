/**
 * Copyright (c) 2021, Regents of the University of California
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

package opt.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import opt.UserSettings;
import opt.data.AbstractLink;
import opt.data.Commodity;
import opt.data.Commodity.EmissionsClass;
import opt.data.LaneGroupType;
import opt.data.SimDataLink;
import opt.data.SimDataScenario;
import org.jfree.data.xy.XYDataItem;


/**
 * Cal-BC calculation of emission parameters.
 * 
 * @author Alex Kurzhanskiy
 */
public class EmissionsCalBC {
    public static int numParams = 14;
    public static int yearA = 2020;
    public static int yearB = 2040;
    public static int currentYear = 2020;
    
    public static String[] eParams = {"CO", "CO2", "NOX", "PM10", "SOX", "VOC", "PM2.5"};
    
    private static String defaultLookupTable = "emissionsCalBCLookupTable.csv";
    
    private static String lookupTable = null;
    private static String header = "Mode,Speed,CO,CO2,NOX,PM10,SOX,VOC,PM2.5,CO,CO2,NOX,PM10,SOX,VOC,PM2.5";
    
    private static List<String> keys = new ArrayList<>();
    private static Map<String, double[]> cv2params = new HashMap<>();
    
    private static int minV = 0;
    private static int maxV = 0;
    
    private static List<Commodity> listVT = null;
    private static int numVT;
    private static Set<LaneGroupType> lgset_gp = new HashSet<LaneGroupType>();
    private static Set<LaneGroupType> lgset_mng = new HashSet<LaneGroupType>();
    private static Set<LaneGroupType> lgset_aux = new HashSet<LaneGroupType>();

    
    
    private static BufferedReader getDefaultReader() {
        InputStream inputStream;
        inputStream = EmissionsCalBC.class.getClassLoader().getResourceAsStream(defaultLookupTable);
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        return reader;
    }
    
    public static void load() {
        keys.clear();
        cv2params.clear();
        if (UserSettings.userOPTDir != null)
            lookupTable = UserSettings.userOPTDir + File.separator + "emissionsCalBCLookupTable.csv";
        
        File lookupFile = new File(lookupTable);
           
        BufferedReader reader;
        try {
            if (lookupFile.exists())
                reader = new BufferedReader(new FileReader(lookupTable));
            else
                reader = getDefaultReader();
            String line = reader.readLine();
            String[] subs = line.split(",");
            yearA = Integer.parseInt(subs[0]);
            yearB = Integer.parseInt(subs[1]);
            line = reader.readLine();
            while (line != null) {
                if (line.compareTo(header) == 0) {// skip header
                    line = reader.readLine();
                    continue;
                }
                subs = line.split(",");
                if ((subs[0].compareTo("Auto") != 0) && (subs[0].compareTo("Truck") != 0) && (subs[0].compareTo("Bus") != 0))
                    continue;
                
                String key = subs[0] + "," + subs[1];
                maxV = Math.max(maxV, Integer.parseInt(subs[1]));
                double[] params = new double[numParams];
                int len = Math.min(numParams, subs.length - 2);
                for (int i = 0; i < numParams; i++)
                    params[i] = 0d;
                for (int i = 0; i < len; i++)
                    params[i] = Double.parseDouble(subs[i + 2]);
                
                keys.add(key);
                cv2params.put(key, params);
                
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Error reading Cal-BC emissions table", e);
        }
        
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        currentYear = Math.max(yearA, Math.min(currentYear, yearB));
        
        lgset_gp.clear();
        lgset_mng.clear();
        lgset_aux.clear();
        lgset_gp.add(LaneGroupType.gp);
        lgset_mng.add(LaneGroupType.mng);
        lgset_aux.add(LaneGroupType.aux);
        
        save();
    }
    
    
    
    public static void setListVT(List<Commodity> lvt) {
        listVT = lvt;
        numVT = 0;
        if (lvt != null)
            numVT = listVT.size();
    }
    
    
    public static double[] computeParamAggregates(AbstractLink link, SimDataLink sdata, double[] cumVals) {
        int paramCount = numParams / 2;
        double[] res = cumVals;
        if (res == null) {
            res = new double[paramCount];
            for (int i = 0; i < paramCount; i++)
                res[i] = 0;
        }
        double dt = sdata.get_speed(lgset_gp).get_dt() / 3600d;
        
        res = addArraysDouble(res, computeEmissionsParamsForLaneGroup(link, sdata, lgset_gp, dt));
        if (link.has_mng())
            res = addArraysDouble(res, computeEmissionsParamsForLaneGroup(link, sdata, lgset_mng, dt));
        if (link.has_aux())
            res = addArraysDouble(res, computeEmissionsParamsForLaneGroup(link, sdata, lgset_aux, dt));
        
        return res;
    }
    
    
    public static double[] computeParamAggregates(List<AbstractLink> links, SimDataScenario sdata) {
        int paramCount = numParams / 2;
        double[] res = new double[paramCount];
        for (int i = 0; i < paramCount; i++)
            res[i] = 0;
        double dt = sdata.get_dt_sec() / 3600d;
        
        for (AbstractLink l : links)
            res = addArraysDouble(res, computeParamAggregates(l, sdata.linkdata.get(l.id), res));
        
        return res;
    }
    
    
    
    private static void save() {
        if (lookupTable == null)
            return;
        
        try {
            FileWriter myWriter = new FileWriter(lookupTable);
            String buf = "" + yearA + "," + yearB;
            myWriter.write(buf + "\n");
            myWriter.write(header);
            
            for (String key : keys) {
                myWriter.write("\n");
                buf = key;
                for (int i = 0; i < numParams; i++)
                    buf += "," + cv2params.get(key)[i];
                myWriter.write(buf);
            }
            myWriter.close();
        } catch (Exception e) {
            opt.utils.Dialogs.ExceptionDialog("Error writing Cal-BC emissions table to file", e);
        }
    }


    
    
    
    /***************************************************************************
     * 
     * Utilities
     *
     ***************************************************************************/

    private static double[] getEmissionParams(EmissionsClass ec, double v_mph) {
        int sz = numParams / 2;
        int v = (int)Math.round(v_mph);
        v = Math.min(Math.max(minV, v), maxV);
        String key = ec + "," + v;
        
        double[] res = new double[sz];
        for (int i = 0; i < sz; i++)
            res[i] = 0d;
        
        if (cv2params.containsKey(key)) {
            double[] vals = cv2params.get(key);
            for (int i = 0; i < sz; i++)
                res[i] = interpolate(vals[i], vals[i + sz]);
        }
        
        return res;
    }
    
    
    
    private static double[] computeEmissionsParamsForLaneGroup(AbstractLink link, SimDataLink sdata, Set<LaneGroupType> lgt, double dt) {
        int paramCount = numParams / 2;
        double[] res = new double[paramCount];
        for (int i = 0; i < paramCount; i++)
            res[i] = 0;
        
        List<XYDataItem> speed = sdata.get_speed(lgt).get_XYSeries("lg").getItems();
        int sz = speed.size();
        for (Commodity c : listVT) {
            List<XYDataItem> vehs = sdata.get_veh(lgset_gp, cset(c)).get_XYSeries(c.get_name()).getItems();
            sz = Math.min(sz, vehs.size());
            for  (int i = 0; i < sz; i++) {
                double v_mph = speed.get(i).getYValue();
                double[] ep = getEmissionParams(c.get_eclass(), v_mph);
                //System.err.println("Speed: " + v_mph + ";\tdt: " + dt + ";\tvehs: " + vehs.get(i).getYValue());
                for  (int j = 0; j < paramCount; j++)
                    res[j] += vehs.get(i).getYValue() * v_mph * dt * ep[j];
            }
        }

        return res;
    }
    
    
    private static double interpolate(double a, double b) {
        double mult = (double)(currentYear - yearA) / (double)(yearB - yearA);
        double delta = b - a;
        return a + mult * delta;
    }
    
    
    private static double[] addArraysDouble(double[] a, double[] b) {
        int sz = Math.min(a.length, b.length);
        for (int i = 0; i < sz; i++)
            a[i] += b[i];
        return a;
    }
    
    
    private static Set<Long> cset(Commodity c) {
        Set<Long> s = new HashSet<Long>();
        s.add(c.getId());
        return s;
    }
    
    
    
    
}


