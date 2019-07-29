package opt.tests;

import opt.data.AbstractLink;
import opt.data.Segment;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;

public class TestSegment extends AbstractTest {

    private static TestData sX;

    static{
        sX = new TestData();
    }

    /////////////////////////////////////
    // name and length
    /////////////////////////////////////

    @Test
    public void test_set_get_name(){
        try {
            TestData X = new TestData();
            Segment segment = X.scenario.get_segment_by_name("sA1");
            assertEquals("sA1",segment.get_name());
            segment.set_name("newname");
            assertEquals("newname",segment.get_name());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

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

    /////////////////////////////////////
    // network
    /////////////////////////////////////

    @Test
    public void test_get_links(){
        Segment segment0 = sX.scenario.get_segment_by_name("sA1");
        Set<AbstractLink> links1 = segment0.get_links();
        assertEquals(links1.stream().map(x->x.get_id()).collect(toSet()),new HashSet(Arrays.asList(1l,7l)));

        Segment segment2 = sX.scenario.get_segment_by_name("sA2");
        Set<AbstractLink> links2 = segment2.get_links();
        assertEquals(links2.stream().map(x->x.get_id()).collect(toSet()),new HashSet(Arrays.asList(2l)));
    }

    @Test
    public void test_get_upstrm_segments(){
        Segment segment = sX.scenario.get_segment_by_name("sA9");
        Set<Segment> x = segment.get_upstrm_segments();
        assertEquals(1,x.size());
        assertEquals("sA8",x.iterator().next().get_name());
    }

    @Test
    public void test_get_upstrm_links(){
        Segment segment = sX.scenario.get_segment_by_name("sA8");
        Set<AbstractLink> x = segment.get_upstrm_links();
        assertEquals(2,x.size());
        assertEquals(new HashSet<>(Arrays.asList(14l,12l)),x.stream().map(link->link.get_id()).collect(toSet()));
    }

    @Test
    public void test_get_dnstrm_segments(){
        Segment segment = sX.scenario.get_segment_by_name("sA1");
        Set<Segment> x = segment.get_dnstrm_segments();
        assertEquals(2,x.size());
        assertEquals(new HashSet<>(Arrays.asList("sA7","sA2")),x.stream().map(s->s.get_name()).collect(toSet()));
    }

    @Test
    public void test_get_dnstrm_links(){
        Segment segment = sX.scenario.get_segment_by_name("sA1");
        Set<AbstractLink> x = segment.get_dnstrm_links();
        assertEquals(2,x.size());
        assertEquals(new HashSet<>(Arrays.asList(2l,12l)),x.stream().map(link->link.get_id()).collect(toSet()));
    }

    @Ignore
    @Test
    public void test_insert_upstrm_hov_segment(){
    }

    @Test
    public void test_insert_upstrm_mainline_segment(){
        TestData X = new TestData();
        Segment segment = X.scenario.get_segment_by_name("sA3").insert_upstrm_mainline_segment();
        assertNotNull(segment);
    }

    @Test
    public void test_insert_upstrm_onramp_segment(){
        TestData X = new TestData();
        Segment segment = X.scenario.get_segment_by_name("sA6").insert_upstrm_onramp_segment();
        assertNotNull(segment);
    }

    @Ignore
    @Test
    public void test_insert_dnstrm_hov_segment(){
    }

    @Test
    public void test_insert_dnstrm_mainline_segment(){
        TestData X = new TestData();
        Segment segment = X.scenario.get_segment_by_name("sA4").insert_dnstrm_mainline_segment();
        assertNotNull(segment);
    }

    @Test
    public void test_insert_dnstrm_offramp_segment(){
        TestData X = new TestData();
        Segment segment = X.scenario.get_segment_by_name("sA4").insert_dnstrm_offramp_segment();
        assertNotNull(segment);
    }

    /////////////////////////////////////
    // offramp
    /////////////////////////////////////

    @Test
    public void test_has_offramp(){
        assertFalse(sX.scenario.get_segment_by_name("sA2").has_offramp());
        assertTrue(sX.scenario.get_segment_by_name("sA1").has_offramp());
    }

    @Test
    public void test_set_get_fr_name(){
        try {
            TestData X = new TestData();
            assertNull(X.scenario.get_segment_by_name("sA2").get_fr_name());
            assertEquals("lA7",X.scenario.get_segment_by_name("sA1").get_fr_name());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_fr_lanes(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            int fr_lanes = 125;
            segment2.set_fr_lanes(fr_lanes);
            assertEquals(fr_lanes,segment2.get_fr_lanes());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_fr_capacity_vphpl(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            float fr_capacity = 123.4563f;
            segment2.set_fr_capacity_vphpl(fr_capacity);
            assertEquals(fr_capacity,segment2.get_fr_capacity_vphpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_fr_max_vehicles(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            float fr_max_vehicles = 235567.346f;
            segment2.set_fr_max_vehicles(fr_max_vehicles);
            assertEquals(fr_max_vehicles,segment2.get_fr_max_vehicles(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_delete_offramp(){
        TestData X = new TestData();

        // try to delete an non-existing offramp
        assertFalse(X.scenario.get_segment_by_name("sA2").delete_offramp());

        // delete an existing offramp
        assertTrue(X.scenario.get_segment_by_name("sA4").delete_offramp());
    }

    @Test
    public void test_add_offramp(){
        TestData X = new TestData();
        Segment segment0 = X.scenario.get_segment_by_name("sA2");
        assertFalse(segment0.has_offramp());
        segment0.add_offramp();
        assertTrue(segment0.has_offramp());
    }

    /////////////////////////////////////
    // onramp
    /////////////////////////////////////

    @Test
    public void test_has_onramp(){
        assertFalse(sX.scenario.get_segment_by_name("sA2").has_onramp());
        assertTrue(sX.scenario.get_segment_by_name("sA3").has_onramp());
    }

    @Test
    public void test_set_get_or_name(){
        try {
            TestData X = new TestData();
            assertNull(X.scenario.get_segment_by_name("sA1").get_or_name());
            assertEquals("lA8",X.scenario.get_segment_by_name("sA3").get_or_name());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_or_lanes(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            int or_lanes = 12;
            segment2.set_or_lanes(or_lanes);
            assertEquals(or_lanes,segment2.get_or_lanes());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_or_capacity_vphpl(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            float or_capacity = 123.4563f;
            segment2.set_or_capacity_vphpl(or_capacity);
            assertEquals(or_capacity,segment2.get_or_capacity_vphpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_or_max_vehicles(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            float or_max_vehicles = 24983.234f;
            segment2.set_or_max_vehicles(or_max_vehicles);
            assertEquals(or_max_vehicles,segment2.get_or_max_vehicles(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_delete_onramp(){
        TestData X = new TestData();

        // delete an existing onramp
        Segment segment2 = X.scenario.get_segment_by_name("sA3");
        assertTrue(segment2.delete_onramp());

        // try to delete an non-existing onramp
        Segment segment4 = X.scenario.get_segment_by_name("sA4");
        assertFalse(segment4.delete_onramp());
    }

    @Test
    public void test_add_onramp(){
        TestData X = new TestData();
        Segment segment = X.scenario.get_segment_by_name("sA1");
        assertFalse(segment.has_onramp());
        segment.add_onramp();
        assertTrue(segment.has_onramp());
    }

    @Ignore
    @Test
    public void test_set_get_or_demand_vph(){
        try {

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /////////////////////////////////////
    // mainline
    /////////////////////////////////////

    @Ignore
    @Test
    public void test_set_get_hov_lanes(){
    }

    @Test
    public void test_set_get_ml_name(){
        try {
            try {
                TestData X = new TestData();
                Segment segment2 = X.scenario.get_segment_by_name("sA2");
                assertEquals("lA2",segment2.get_fwy_name());
            } catch (Exception e) {
                fail(e.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_mixed_lanes(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            int ml_lanes = 2436;
            segment2.set_mixed_lanes(ml_lanes);
            assertEquals(ml_lanes,segment2.get_mixed_lanes());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_capacity_vphpl(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            float ml_capacity = 3498.2356f;
            segment2.set_capacity_vphpl(ml_capacity);
            assertEquals(ml_capacity,segment2.get_capacity_vphpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_jam_density_vpmpl(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            float ml_jam_density = 245.234f;
            segment2.set_jam_density_vpkpl(ml_jam_density);
            assertEquals(ml_jam_density,segment2.get_jam_density_vpkpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_freespeed_mph(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            float ml_speed = 348934.435f;
            segment2.set_freespeed_kph(ml_speed);
            assertEquals(ml_speed,segment2.get_freespeed_kph(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


//////////////////////////////////////////////////////////////////////





    @Test
    public void test_delete_ramp() {

        try {



        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


}
