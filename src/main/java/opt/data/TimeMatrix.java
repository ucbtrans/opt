package opt.data;

import opt.utils.Misc;

import java.util.*;

public class TimeMatrix {

    public float [] time;
    public List<LinkLaneGroupCell> space;
    public double[][] values;   // space x time
    public double min_value;    // exclude nans
    public double max_value;    // exclude nans
    public List<Double> cell_lengths_miles;

    TimeMatrix(float [] time, List<AbstractLink> routelinks, SimDataScenario data, boolean haslgdata, LaneGroupType lgtype){
        this(time,routelinks,data,haslgdata,new HashSet<>(Arrays.asList(lgtype)));
    }

    TimeMatrix(float [] time, List<AbstractLink> routelinks, SimDataScenario data, boolean haslgdata, Set<LaneGroupType> lgtypes){
        this.time = time;

        // determine spatial resolution
        space = new ArrayList<>();
        cell_lengths_miles = new ArrayList<>();

        for(AbstractLink link : routelinks){
            SimDataLink lkdata = data.linkdata.get(link.id);

            // keep only the lanegroup types that this link actually has
            Set<LaneGroupType> mylgtypes = new HashSet<>();
            mylgtypes.addAll(lgtypes);
            mylgtypes.retainAll(lkdata.lgData.keySet());

            if(haslgdata) {
                space.add(new LinkLaneGroupCell(link.id, mylgtypes, -1));
                cell_lengths_miles.add(lkdata.link_length_miles);
            }
            else{
                if(!mylgtypes.isEmpty()){
                    double cl = lkdata.cell_length();
                    for(int i=0;i<lkdata.numcells();i++) {
                        space.add(new LinkLaneGroupCell(link.id, mylgtypes, i));
                        cell_lengths_miles.add(cl);
                    }
                }
                else{
                    space.add(new LinkLaneGroupCell(link.id, null, -1));
                    cell_lengths_miles.add(lkdata.link_length_miles);
                }
            }
        }

        values = new double[space.size()][time.length];
        this.min_value = Double.POSITIVE_INFINITY;
        this.max_value = Double.NEGATIVE_INFINITY;

    }

    public void add_timeseries(int i,double [] v){

        // special case when I want to add a row of nans
        if(v==null){
            for(int k=0;k<time.length;k++)
                values[i][k] = Double.NaN;
            return;
        }

        Misc.add_in_place(values[i],v);

        double z = Arrays.stream(v).filter(x->!Double.isNaN(x)).max().getAsDouble();
        max_value = z>max_value ? z : max_value;

        z = Arrays.stream(v).filter(x->!Double.isNaN(x)).min().getAsDouble();
        min_value = z<min_value ? z : min_value;
    }

    public void fill_with_nans(int i){
        for(int k=0;k<time.length;k++)
            values[i][k] = Double.NaN;
    }

    public float get_dt(){
        return time[1]-time[0];
    }

    public String print_time(){
        String str = "";
        for(float t : time)
            str += String.format("%.2f\n",t);
        return str;
    }

    public String print_values(){
        String str = "";
        for(double[] v : values){
            for(int i = 0; i < v.length - 1; ++i)
                str += String.format("%.2f,",v[i]);
            str += String.format("%.2f;\n",v[v.length-1]);
        }
        return str;
    }

    public class LinkLaneGroupCell{
        final long linkid;
        final Set<LaneGroupType> lgtypes;
        final int cell;
        public LinkLaneGroupCell(long link,Set<LaneGroupType> lgtypes,int cell){
            this.linkid =link;
            this.lgtypes = lgtypes;
            this.cell = cell;
        }
    }

    @Override
    public String toString() {
        return print_values();
    }
}
