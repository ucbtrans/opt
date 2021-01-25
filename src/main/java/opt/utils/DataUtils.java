package opt.utils;

import opt.data.AbstractLink;
import opt.data.LaneGroup;
import opt.data.LinkOnramp;
import opt.data.LinkOfframp;
import opt.data.LinkConnector;

import java.util.*;

public class DataUtils {
    
    public static String linkInfo(AbstractLink l) {
        if (l == null)
            return "";
        
        String info = l.get_name();
        String type = "Freeway";
        
        if (l instanceof LinkOnramp)
            type = "On-Ramp";
        else if (l instanceof LinkOfframp)
            type = "Off-Ramp";
        else if (l instanceof LinkConnector)
            type = "Connector";
        
        
        
        return info;
    }

    public static List<LaneGroup> read_lanegroups(String str, Map<Long, AbstractLink> links) throws Exception {

        List<LaneGroup> X = new ArrayList();

        // READ LANEGROUP STRING
        String [] a0 = str.split(",");
        if(a0.length<1)
            throw new Exception("Poorly formatted string. (CN_23v4-str0)");
        for(String lg_str : a0){
            String [] a1 = lg_str.split("[(]");

            if(a1.length!=2)
                throw new Exception("Poorly formatted string. (90hm*@$80)");

            Long linkid = Long.parseLong(a1[0]);
            AbstractLink link = links.get(linkid);

            if(link==null)
                throw new Exception("Poorly formatted string. (24n2349))");

            String [] a2 = a1[1].split("[)]");

            if(a2.length!=1)
                throw new Exception("Poorly formatted string. (3g50jmdrthk)");

            if(a2[0].length()==0)
                throw new Exception("Poorly formatted string. (9834hg39)");

            int [] lanes = read_lanes(a2[0]);

            X.add(new LaneGroup(linkid,lanes));
        }

        return X;
    }

    public static int[] read_lanes(String str){
        int [] x = new int[2];

        String[] strsplit = str.split("#");
        if (strsplit.length == 2) {
            x[0] = Integer.parseInt(strsplit[0]);
            x[1] = Integer.parseInt(strsplit[1]);
        } else {
            x[0] = -1;
            x[1] = -1;
        }
        return x;
    }
}
