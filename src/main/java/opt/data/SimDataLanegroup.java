package opt.data;

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

    protected Set<Long> get_comm_ids(){
        if(vehs!=null)
            return vehs.keySet();
        else if(celldata!=null && !celldata.isEmpty())
            return celldata.iterator().next().vehs.keySet();
        else
            return new HashSet<>();
    }

    protected void set_lg_data(long commid,int [] time_indices,List<Double> flwdata,List<Double> vehdata,float sim_dt_sec,float out_dt_sec){
        int numtime = time_indices.length;
        double [] veh = new double[numtime];
        double [] flw = new double[numtime];
        double alpha = 3600d/sim_dt_sec;
        double beta = sim_dt_sec/out_dt_sec;

        if(!vehdata.isEmpty()) {
            for(int k=0;k<numtime;k++){
                int time_index = time_indices[k];
                double this_flow = time_index == 0 ? 0d : (flwdata.get(time_index) - flwdata.get(time_index - 1)) * alpha;
                flw[k] = this_flow;
                veh[k] = vehdata.get(time_index)*beta;
            }
        }

        vehs.put(commid,veh);
        flws.put(commid,flw);
    }

    protected double [] get_flw_exiting_lg(Long commid, int numtime, boolean uselgs){
        double [] X = new double[numtime];
        Map<Long, double[]> myflws = uselgs ? flws : celldata.get(celldata.size()-1).flws;
        if(commid==null)
            for(double [] list : myflws.values())
                for (int k = 0; k <numtime; k++)
                    X[k] += list[k];
        else
            if (myflws.containsKey(commid))
                X = myflws.get(commid);
        return X;
    }

    protected List<double[]> get_cell_speeds(float ffspeed_mph,double cell_length_miles){
        List<double[]> speeds = new ArrayList<>();
        for(SimCellData cd : celldata)
            speeds.add(cd.get_speed(ffspeed_mph,cell_length_miles));
        return speeds;
    }

    protected double get_sum_flw(Set<Long> commids, int k, boolean uselg) {
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

    protected double get_sum_veh(Set<Long> commids, int k, boolean uselg) {
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


}
