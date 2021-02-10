package opt.data;

import utils.OTMUtils;

import java.util.*;
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
            add_segment_if_connected(my_fwy_scenario.segments.get(segid));
        }
    }

    public void add_segment_if_connected(Segment segment) {

        if(segments.isEmpty()){
            segments.add(segment);
            return;
        }

        Set<Segment> up_segments = segment.get_upstrm_segments();
        Set<Segment> dn_segments = segment.get_dnstrm_segments();
        Set<Segment> adjacent_segments = new HashSet<>();
        adjacent_segments.addAll(up_segments);
        adjacent_segments.addAll(dn_segments);
        boolean is_disjoint = Collections.disjoint(segments, adjacent_segments);

        if(is_disjoint)
            return;

        Segment first_segment = segments.get(0);
        Segment last_segment = segments.get(segments.size()-1);

        // add if this segment is upstream of first and first is a source
        if(dn_segments.contains(first_segment) && first_segment.get_upstrm_segments().isEmpty())
            segments.add(0, segment);

        // add if this segment is downstream of last and last is a sink
        else if(up_segments.contains(last_segment) && last_segment.get_dnstrm_segments().isEmpty())
            segments.add(segment);

        // this link is internal
        else {
            Optional<Segment> opt_dn_segment = segments.stream()
                    .filter(dn_segments::contains)
                    .findFirst();

            Optional<Segment> opt_up_segment = segments.stream()
                    .filter(up_segments::contains)
                    .findFirst();

            if (opt_dn_segment.isPresent() && opt_up_segment.isPresent()) {
                Segment dn_segment = opt_dn_segment.get();
                Segment up_segment = opt_up_segment.get();
                int dn_index = segments.indexOf(dn_segment);
                int up_index = segments.indexOf(up_segment);
                assert(dn_index-up_index==1);
                segments.add(dn_index,segment);
            }
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

    public List<AbstractLink> get_link_sequence(){

        List<AbstractLink> links = new ArrayList<>();

        if(segments==null || segments.isEmpty())
            return links;

        // first link is the freeway|connector link
        links.add(segments.get(0).fwy);
        AbstractLink prevlink = links.get(0);

        for(int i=1;i<segments.size();i++){
            Segment prevseg = segments.get(i-1);
            Segment currseg = segments.get(i);

            // fwy -> conn
            // prevsegment.offramps contains currseg.fwy.uplink
            if(prevseg.get_frs().contains(currseg.fwy.up_link))
                links.add(currseg.fwy.up_link);

            // conn -> fwy
            // currseg.onramps contains prevseg.fwy.dnlink
            else if(currseg.get_ors().contains(prevseg.fwy.dn_link))
                links.add(prevseg.fwy.dn_link);

            // the only remaining legal case is fwy->fwy.
            // if that is not the case, we have a problem.
            // fwy -> fwy
            else assert(prevlink.dn_link==currseg.fwy);

            links.add(currseg.fwy);
            prevlink = currseg.fwy;

        }


        return links;
    }

    public boolean check_is_linear(){
        if(segments.isEmpty())
            return true;
        for(int i=1;i<segments.size();i++){
            Segment curr_segment = segments.get(i-1);
            Segment next_segment = segments.get(i);
            if(!curr_segment.get_dnstrm_segments().contains(next_segment))
                return false;
        }
        return true;
    }

}
