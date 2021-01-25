package opt.data.event;

import jaxb.Event;
import opt.data.AbstractLink;
import opt.data.FDparams;
import opt.data.LaneGroupType;

import java.util.List;

public class EventLanegroupFD extends AbstractEventLaneGroup {

    protected FDparams fd_mult;

    public EventLanegroupFD(long id, String type, float timestamp, String name,List<AbstractLink> links,LaneGroupType lgtype,FDparams fd_mult) throws Exception {
        super(id, type, timestamp, name, links, lgtype);
        this.fd_mult = fd_mult;

        if(fd_mult !=null){
            if(fd_mult.capacity_vphpl==null || fd_mult.capacity_vphpl<0)
                throw new Exception("delta_fd.capacity_vphpl==null || delta_fd.capacity_vphpl<0");
            if(fd_mult.jam_density_vpkpl==null || fd_mult.jam_density_vpkpl<0)
                throw new Exception("delta_fd.jam_density_vpkpl==null || delta_fd.jam_density_vpkpl<0");
            if(fd_mult.ff_speed_kph==null || fd_mult.ff_speed_kph<0)
                throw new Exception("delta_fd.ff_speed_kph==null || delta_fd.ff_speed_kph<0");
        }
    }

    @Override
    public Event to_jaxb() {
        jaxb.Event jevent = super.to_jaxb();

        jaxb.Parameters pmtrs = new jaxb.Parameters();
        jevent.setParameters(pmtrs);

        if(fd_mult !=null){

            jaxb.Parameter pcap = new jaxb.Parameter();
            pmtrs.getParameter().add(pcap);
            pcap.setName("capacity");
            pcap.setValue(String.format("%f", fd_mult.capacity_vphpl));

            jaxb.Parameter pjmd = new jaxb.Parameter();
            pmtrs.getParameter().add(pjmd);
            pjmd.setName("jam_density");
            pjmd.setValue(String.format("%f", fd_mult.jam_density_vpkpl));

            jaxb.Parameter pspd = new jaxb.Parameter();
            pmtrs.getParameter().add(pspd);
            pspd.setName("speed");
            pspd.setValue(String.format("%f", fd_mult.ff_speed_kph));
        }
        return jevent;
    }


    /////////////////////
    // API
    /////////////////////
    
    public FDparams get_fd_mult() {
        return fd_mult;
    }

    public void set_fdmult_to_null(){
        fd_mult=null;
    }

    public void set_capacity_mult(float x){
        if(fd_mult==null)
            fd_mult=new FDparams(1f,1f,1f);
        if(x>=0f)
            fd_mult.capacity_vphpl=x;
    }

    public void set_jamdensity_mult(float x){
        if(fd_mult==null)
            fd_mult=new FDparams(1f,1f,1f);
        if(x>=0f)
            fd_mult.jam_density_vpkpl=x;
    }

    public void set_ffspeed_mult(float x){
        if(fd_mult==null)
            fd_mult=new FDparams(1f,1f,1f);
        if(x>=0f)
            fd_mult.ff_speed_kph=x;
    }

}
