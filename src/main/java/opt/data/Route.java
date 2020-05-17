package opt.data;

import utils.OTMUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Route {

    protected final long id;
    protected FreewayScenario my_fwy_scenario;
    protected String name;
    protected List<Segment> segments;

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Route(FreewayScenario my_fwy_scenario,Long id,String name){
        this.id = id;
        this.name = name;
        this.my_fwy_scenario = my_fwy_scenario;
        this.segments = new ArrayList<>();
    }

    public Route(FreewayScenario my_fwy_scenario,jaxb.Route jroute) throws Exception {
        this.id = jroute.getId();
        this.name = jroute.getName();
        this.my_fwy_scenario = my_fwy_scenario;
        this.segments = new ArrayList<>();
        if ((jroute.getSgmts() == null) || jroute.getSgmts().equals(""))
            return;
        for(Long segid : OTMUtils.csv2longlist(jroute.getSgmts())) {
            if(!my_fwy_scenario.segments.containsKey(segid))
                throw new Exception("Bad segment id in route");
            segments.add(my_fwy_scenario.segments.get(segid));
        }
    }

    @Override
    public Route clone(){
        Route new_route = new Route(this.my_fwy_scenario,this.id,this.name);
        new_route.segments.addAll(this.segments);
        return new_route;
    }

    protected jaxb.Route to_jaxb(){
        jaxb.Route route = new jaxb.Route();
        route.setId(id);
        route.setName(name);
        route.setSgmts(OTMUtils.comma_format(
                segments.stream().map(s->s.id).collect(Collectors.toList())
        ));
        return route;
    }

    /////////////////////////////////////
    // getters and setters
    /////////////////////////////////////

    public long getId() {
        return id;
    }
    
    public FreewayScenario get_scenario() {
        return my_fwy_scenario;
    }

    public List<Segment> get_segments() {
        return segments.stream().collect(Collectors.toList());
    }

    public List<Long> get_segment_ids() {
        return segments.stream().map(s->s.id).collect(Collectors.toList());
    }
    
    public void set_segments(List<Segment> segs) {
        if (segs == null)
            return;
        segments = new ArrayList<>(segs);
    }
    
    public void add_segment_ids(List<Long> segmentids) {
        for(Long segid : segmentids){
            if(!my_fwy_scenario.segments.containsKey(segid))
                continue;
            add_segment(my_fwy_scenario.segments.get(segid));
        }
    }

    public void add_segment(Segment segment) {
        if(segments.isEmpty()){
            segments.add(segment);
            return;
        }

        int numseg = segments.size();

        // this segment goes upstream of the first segment
        if(segments.get(0).get_upstrm_segments().contains(segment)){
            segments.add(0,segment);
            return;
        }

        // or this segment goes downstream of the last segment
        if(segments.get(numseg-1).get_dnstrm_segments().contains(segment)){
            segments.add(segment);
            return;
        }
    }

    public void remove_segment_ids(Set<Long> segmentids) throws Exception {
        for (Long segid : segmentids) {
            if (!my_fwy_scenario.segments.containsKey(segid))
                continue;
            remove_segment(my_fwy_scenario.segments.get(segid));
        }
    }

    public void remove_segment(Segment segment) throws Exception {
        if(!segments.contains(segment))
            throw new Exception("The route does not contain this segment");

        int numseg = segments.size();

        if(segments.get(0)!=segment && segments.get(numseg-1)!=segment)
            throw new Exception("This segment is not first or last in the route");

        segments.remove(segment);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
