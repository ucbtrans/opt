package opt.data;

import opt.utils.Misc;

import java.util.*;

public class SimDataLanegroup {

    private Map<Long, double []> vehs;    // commid->vehs over time [veh]
    private Map<Long, double []> flws;    // commid->flw over time [vph]

    protected List<SimCellData> celldata;

    public SimDataLanegroup(models.fluid.FluidLaneGroup lg, Set<Long> commids,boolean storecelldata,boolean storelgdata,int numtime){

        if(storelgdata){
            vehs = new HashMap<>();
            flws = new HashMap<>();
            for(Long commid : commids) {
                vehs.put(commid, new double[numtime]);
                flws.put(commid, new double[numtime]);
            }
        }

        if(storecelldata){
            celldata = new ArrayList<>();
            for(int i=0;i<lg.cells.size();i++)
                celldata.add(new SimCellData(commids,numtime));
        }

    }

    private int numtime(){
        return vehs.values().iterator().next().length;
    }

    protected Set<Long> get_comm_ids(){
        if(vehs!=null)
            return vehs.keySet();
        else if(celldata!=null && !celldata.isEmpty())
            return celldata.iterator().next().vehs.keySet();
        else
            return new HashSet<>();
    }

    protected void set_lg_data(long commid,List<Double> flwdata,List<Double> vehdata,float sim_dt_sec,float out_dt_sec){
        int numtime = vehdata.size();
        double [] veh = new double[numtime];
        double [] flw = new double[numtime];
        double alpha = 3600d/out_dt_sec;
        double beta = sim_dt_sec/out_dt_sec;

        if(!vehdata.isEmpty()) {
            for(int k=0;k<numtime;k++){
                double this_flow = k == 0 ? 0d : (flwdata.get(k) - flwdata.get(k - 1)) * alpha;
                flw[k] = this_flow;
                veh[k] = vehdata.get(k)*beta;
            }
        }

        vehs.put(commid,veh);
        flws.put(commid,flw);
    }

    protected double [] get_flw_exiting_lg(Set<Long> commids, int numtime, boolean uselgs){
        double [] X = new double[numtime];
        Map<Long, double[]> myflws = uselgs ? flws : celldata.get(celldata.size()-1).flws;
        if(commids==null)
            for(double [] list : myflws.values())
                for (int k = 0; k <numtime; k++)
                    X[k] += list[k];
        else
            for(Long commid : commids) {
                double [] x = myflws.get(commid);
                for (int k = 0; k < numtime; k++)
                    X[k] += x[k];
            }
        return X;
    }

    protected double get_sum_flw_for_time(Set<Long> commids, int k, boolean uselg) {
        if(commids==null)
            commids = get_comm_ids();
        double X = 0d;
        if(uselg)
            for(Long cid : commids)
                X += flws.get(cid)[k];
        else
            for (SimCellData cd : celldata)
                for(Long cid : commids)
                    X += cd.flws.get(cid)[k];
        return X;
    }

    protected double [] get_sum_flw(Set<Long> commids, boolean uselg) {
        double [] X = new double[numtime()];
        if(uselg)
            for(Long cid : commids)
                Misc.add_in_place(X,flws.get(cid));
        else
            for (SimCellData cd : celldata)
                for(Long cid : commids)
                    Misc.add_in_place(X,cd.flws.get(cid));
        return X;
    }

    protected double get_sum_veh_for_time(Set<Long> commids, int k, boolean uselg) {
        if(commids==null)
            commids = get_comm_ids();
        double X = 0d;
        if(uselg)
            for(Long cid : commids)
                X += vehs.get(cid)[k];
        else
            for (SimCellData cd : celldata)
                for(Long cid : commids)
                    X += cd.vehs.get(cid)[k];
        return X;
    }

    protected double [] get_sum_veh(Set<Long> commids, boolean uselg) {

        double [] X = new double[numtime()];
        if(uselg)
            for(Long cid : commids)
                Misc.add_in_place(X,vehs.get(cid));
        else
            for (SimCellData cd : celldata)
                for(Long cid : commids)
                    Misc.add_in_place(X,cd.vehs.get(cid));
        return X;
    }

    protected List<double[]> get_cell_speeds(float ffspeed_mph,double cell_length_miles){
        List<double[]> speeds = new ArrayList<>();
        for(SimCellData cd : celldata)
            speeds.add(cd.get_speed(ffspeed_mph,cell_length_miles));
        return speeds;
    }

    protected List<double[]> get_cell_flows(Set<Long>commids){
        List<double[]> flows = new ArrayList<>();
        for(SimCellData cd : celldata)
            flows.add(cd.get_cell_flw(commids));
        return flows;
    }

    protected List<double[]> get_cell_vehs(Set<Long>commids){
        List<double[]> vehs = new ArrayList<>();
        for(SimCellData cd : celldata)
            vehs.add(cd.get_cell_veh(commids));
        return vehs;
    }

}
