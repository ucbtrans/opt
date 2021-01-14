package opt.data;

public class LaneChangeModel {

    double alpha;
    double epsilon;
    double gamma;

    public LaneChangeModel(double alpha, double epsilon, double gamma) {
        this.alpha = alpha;
        this.epsilon = epsilon;
        this.gamma = gamma;
    }

    public jaxb.Lanechanges to_jaxb(){
        jaxb.Lanechanges jlcs = new jaxb.Lanechanges();
        jlcs.setType("linklinear");
        jaxb.Lanechange jlc = new jaxb.Lanechange();
        jlcs.getLanechange().add(jlc);
        jaxb.Parameters prams = new jaxb.Parameters();
        jlc.setParameters(prams);
        jaxb.Parameter p1 = new jaxb.Parameter();
        prams.getParameter().add(p1);
        p1.setName("alpha");
        p1.setValue(String.format("%f", alpha));
        jaxb.Parameter p2 = new jaxb.Parameter();
        prams.getParameter().add(p2);
        p2.setName("epsilon");
        p2.setValue(String.format("%f",epsilon));

        jaxb.Parameter p3 = new jaxb.Parameter();
        prams.getParameter().add(p3);
        p3.setName("gamma");
        p3.setValue(String.format("%f",gamma));

        return jlcs;
    }

}
