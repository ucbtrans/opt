package opt.tests;

import opt.data.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;

public class TestSegment extends AbstractTest {

    private static TestData sX;

    static{
        sX = new TestData();
    }

    /////////////////////////////////////
    // getters / setters
    /////////////////////////////////////

    @Test
    public void test_set_get_length_meters(){
        TestData X = new TestData();
        try {
            float ml_length = 3480.346f;
            Segment segment = X.scenario.get_segment_by_name("sA2");
            segment.set_length_meters(ml_length);
            assertEquals(ml_length,segment.get_length_meters(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_get_links(){
        Segment segment0 = sX.scenario.get_segment_by_name("sA1");
        List<AbstractLink> links1 = segment0.get_links();
        assertEquals(links1.stream()
                .filter(x->x!=null)
                .map(x->x.id).collect(toSet()),new HashSet(Arrays.asList(1l,7l)));

        Segment segment2 = sX.scenario.get_segment_by_name("sA2");
        List<AbstractLink> links2 = segment2.get_links();
        assertEquals(links2.stream()
                .filter(x->x!=null)
                .map(x->x.id).collect(toSet()),new HashSet(Arrays.asList(2l)));
    }

    @Ignore
    @Test
    public void test_num_in_ors(){
    }

    @Ignore
    @Test
    public void test_num_out_ors(){
    }

    @Ignore
    @Test
    public void test_num_in_frs(){
    }

    @Ignore
    @Test
    public void test_num_out_frs(){
    }

    @Ignore
    @Test
    public void test_in_ors(){
    }

    @Ignore
    @Test
    public void test_out_ors(){
    }

    @Ignore
    @Test
    public void test_in_frs(){
    }

    @Ignore
    @Test
    public void test_out_frs(){
    }

    ////////////////////////////////////////
    // add / delete segments
    ////////////////////////////////////////

    @Ignore
    @Test
    public void test_add_up_segment(){
//        TestData X = new TestData();
//        Segment segment = X.scenario.get_segment_by_name("sA4").insert_dnstrm_mainline_segment();
//        assertNotNull(segment);
    }

    @Ignore
    @Test
    public void test_add_dn_segment(){
//        TestData X = new TestData();
//        Segment segment = X.scenario.get_segment_by_name("sA4").insert_dnstrm_offramp_segment();
//        assertNotNull(segment);
    }

    ////////////////////////////////////////
    // add / delete ramps
    ////////////////////////////////////////

    @Test
    public void test_add_in_or(){

    }

    @Test
    public void test_add_out_or(){
        TestData X = new TestData();
        Segment segment = X.scenario.get_segment_by_name("sA1");
        assertTrue(segment.num_out_ors()==0);
        LinkParameters params = new LinkParameters(100f,200f,300f);
        LinkOnramp or = segment.add_out_or(params);
        assertTrue(or.is_ramp());
        assertTrue(or.is_source());
        assertTrue(segment.out_ors(0)==or);
        assertTrue(segment.num_out_ors()==1);
    }

    @Test
    public void test_add_in_fr(){
        TestData X = new TestData();
        Segment segment = X.scenario.get_segment_by_name("sA1");
        assertTrue(segment.num_in_frs()==0);
        LinkParameters params = new LinkParameters(100f,200f,300f);
        LinkOfframp fr = segment.add_in_fr(params);
        assertTrue(fr.is_ramp());
        assertTrue(fr.is_sink());
        assertTrue(segment.in_frs(0)==fr);
        assertTrue(segment.num_in_frs()==1);
    }

    @Ignore
    @Test
    public void test_add_out_fr(){
//        try {
//            TestData X = new TestData();
//            Segment segment0 = X.scenario.get_segment_by_name("sA2");
//            assertFalse(segment0.has_offramp());
//            LinkParameters params = new LinkParameters(100,200,300);
//            segment0.add_offramp(params);
//            assertTrue(segment0.has_offramp());
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
    }

    @Ignore
    @Test
    public void test_delete_in_or(){

    }

    @Ignore
    @Test
    public void test_delete_out_or(){
        TestData X = new TestData();

        Segment sA7 = X.scenario.get_segment_by_name("sA7");
        Segment sA8 = X.scenario.get_segment_by_name("sA8");
        LinkConnector lA12 = (LinkConnector) sA7.fwy();
        LinkOnramp lA13 = sA8.get_ors().get(0);
        LinkFreeway lA15 = (LinkFreeway) sA8.fwy();

        assertTrue( lA12.get_dn_link()!=null && lA12.get_dn_link()==lA13 );
        assertTrue( lA13.get_up_link()!=null && lA13.get_up_link()==lA12 );
        assertTrue( lA13.get_dn_link()!=null && lA13.get_dn_link()==lA15 );

        sA8.delete_out_or(lA13);

        assertTrue( lA12.get_dn_link()==null );

    }

    @Ignore
    @Test
    public void test_delete_in_fr(){
    }

    @Test
    public void test_delete_out_fr(){

        TestData X = new TestData();
        Segment sA1 = X.scenario.get_segment_by_name("sA1");
        LinkOfframp lA7 = sA1.out_frs(0);

        Segment sA7 = X.scenario.get_segment_by_name("sA7");
        LinkConnector lA12 = (LinkConnector) sA7.fwy();

        System.out.println(lA12.get_up_link());
        boolean xxx = sA1.delete_out_fr(lA7);
        System.out.println(lA12.get_up_link());


    }

    /////////////////////////////////////
    // segment and link getters
    /////////////////////////////////////

    @Test
    public void test_get_upstrm_segments(){
        Segment segment = sX.scenario.get_segment_by_name("sA9");
        Set<Segment> x = segment.get_upstrm_segments();
        assertEquals(1,x.size());
        assertEquals("sA8",x.iterator().next().name);
    }

    @Test
    public void test_get_upstrm_links(){
        Segment segment = sX.scenario.get_segment_by_name("sA8");
        Set<AbstractLink> x = segment.get_upstrm_links();
        assertEquals(1,x.size());
        assertEquals(new HashSet<>(Arrays.asList(12l)),x.stream().map(link->link.id).collect(toSet()));
    }

    @Test
    public void test_get_dnstrm_segments(){
        Segment segment = sX.scenario.get_segment_by_name("sA1");
        Set<Segment> x = segment.get_dnstrm_segments();
        assertEquals(2,x.size());
        assertEquals(new HashSet<>(Arrays.asList("sA7","sA2")),x.stream().map(s->s.name).collect(toSet()));
    }

    @Test
    public void test_get_dnstrm_links(){
        Segment segment = sX.scenario.get_segment_by_name("sA1");
        Set<AbstractLink> x = segment.get_dnstrm_links();
        assertEquals(2,x.size());
        assertEquals(new HashSet<>(Arrays.asList(2l,12l)),x.stream().map(link->link.id).collect(toSet()));
    }



}
