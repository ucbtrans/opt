package opt.data;

public class LaneChangeModel {
    double keep;
    double density_vpmileplane;
    float dt = 0f;

    public LaneChangeModel(float dt,double keep, double density_vpmileplane) {
        this.dt = dt;
        this.keep = keep;
        this.density_vpmileplane = density_vpmileplane;
    }

    public LaneChangeModel(double keep, double density_vpmileplane) {
        this.keep = keep;
        this.density_vpmileplane = density_vpmileplane;
    }

    public jaxb.Lanechanges to_jaxb(){
        jaxb.Lanechanges jlcs = new jaxb.Lanechanges();
        jaxb.Lanechange jlc = new jaxb.Lanechange();
        jlcs.getLanechange().add(jlc);
        jlc.setIsDefault(true);
        jlc.setDt(dt);
        jlc.setType("logit");
        jaxb.Parameters prams = new jaxb.Parameters();
        jlc.setParameters(prams);
        jaxb.Parameter p1 = new jaxb.Parameter();
        prams.getParameter().add(p1);
        p1.setName("keep");
        p1.setValue(String.format("%f", keep));
        jaxb.Parameter p2 = new jaxb.Parameter();
        prams.getParameter().add(p2);
        p2.setName("rho_vpkmplane");
        p2.setValue(String.format("%f",density_vpmileplane/1.609));
        return jlcs;
    }

}
