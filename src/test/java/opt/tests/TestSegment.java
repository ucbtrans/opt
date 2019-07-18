package opt.tests;

import opt.data.AbstractLink;
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

    // MISSING //
//    test_get_upstrm_segments
//    test_get_upstrm_links
//    test_get_dnstrm_segments
//    test_get_dnstrm_links
//    test_insert_upstrm_hov_segment
//    test_insert_upstrm_mainline_segment
//    test_insert_upstrm_onramp_segment
//    test_insert_dnstrm_hov_segment
//    test_insert_dnstrm_mainline_segment
//    test_insert_dnstrm_onramp_segment
//    test_set_get_or_demand_vph

    /////////////////////////////////////
    // name and length
    /////////////////////////////////////

    @Test
    public void test_set_get_name(){
        try {
            TestData X = new TestData();
            assertEquals("segment A0",X.segment0.get_name());
            X.segment0.set_name("newname");
            assertEquals("newname",X.segment0.get_name());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_length_meters(){
        TestData X = new TestData();
        try {
            float ml_length = 3480.346f;
            X.segment2.set_length_meters(ml_length);
            assertEquals(ml_length,X.segment2.get_length_meters(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /////////////////////////////////////
    // network
    /////////////////////////////////////

    @Test
    public void test_get_links(){
        Set<AbstractLink> links0 = sX.segment0.get_links();
        assertEquals(links0.stream().map(x->x.get_id()).collect(toSet()),new HashSet(Arrays.asList(0l)));

        Set<AbstractLink> links2 = sX.segment2.get_links();
        assertEquals(links2.stream().map(x->x.get_id()).collect(toSet()),new HashSet(Arrays.asList(2l,8l,7l)));
    }

    @Ignore
    @Test
    public void test_get_upstrm_segments(){
    }

    @Ignore
    @Test
    public void test_get_upstrm_links(){
    }

    @Ignore
    @Test
    public void test_get_dnstrm_segments(){
    }

    @Ignore
    @Test
    public void test_get_dnstrm_links(){
    }

    @Ignore
    @Test
    public void test_insert_upstrm_hov_segment(){
    }

    @Ignore
    @Test
    public void test_insert_upstrm_mainline_segment(){
    }

    @Ignore
    @Test
    public void test_insert_upstrm_onramp_segment(){
    }

    @Ignore
    @Test
    public void test_insert_dnstrm_hov_segment(){
    }

    @Ignore
    @Test
    public void test_insert_dnstrm_mainline_segment(){
    }

    @Ignore
    @Test
    public void test_insert_dnstrm_onramp_segment(){
    }

    /////////////////////////////////////
    // offramp
    /////////////////////////////////////

    @Test
    public void test_has_offramp(){
        assertFalse(sX.segment0.has_offramp());
        assertTrue(sX.segment2.has_offramp());
    }

    @Test
    public void test_set_get_fr_name(){
        try {
            TestData X = new TestData();
            assertNull(X.segment0.get_fr_name());
            assertEquals("A7",X.segment2.get_fr_name());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_fr_lanes(){
        TestData X = new TestData();
        try {
            int fr_lanes = 125;
            X.segment2.set_fr_lanes(fr_lanes);
            assertEquals(fr_lanes,X.segment2.get_fr_lanes());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_fr_capacity_vphpl(){
        TestData X = new TestData();
        try {
            float fr_capacity = 123.4563f;
            X.segment2.set_fr_capacity_vphpl(fr_capacity);
            assertEquals(fr_capacity,X.segment2.get_fr_capacity_vphpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_fr_max_vehicles(){
        TestData X = new TestData();
        try {
            float fr_max_vehicles = 235567.346f;
            X.segment2.set_fr_max_vehicles(fr_max_vehicles);
            assertEquals(fr_max_vehicles,X.segment2.get_fr_max_vehicles(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_delete_offramp(){
        TestData X = new TestData();
        // delete an existing offramp
        assertTrue(X.segment0.delete_offramp());

        // try to delete an non-existing offramp
        assertFalse(X.segment2.delete_offramp());
    }

    @Test
    public void test_add_offramp(){
        TestData X = new TestData();
        assertFalse(X.segment0.has_offramp());
        X.segment0.add_offramp();
        assertTrue(X.segment0.has_offramp());
    }

    /////////////////////////////////////
    // onramp
    /////////////////////////////////////

    @Test
    public void test_has_onramp(){
        assertFalse(sX.segment0.has_onramp());
        assertTrue(sX.segment2.has_onramp());
    }

    @Test
    public void test_set_get_or_name(){
        try {
            TestData X = new TestData();
            assertNull(X.segment0.get_or_name());
            assertEquals("A8",X.segment2.get_or_name());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_or_lanes(){
        TestData X = new TestData();
        try {
            int or_lanes = 12;
            X.segment2.set_or_lanes(or_lanes);
            assertEquals(or_lanes,X.segment2.get_or_lanes());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_or_capacity_vphpl(){
        TestData X = new TestData();
        try {
            float or_capacity = 123.4563f;
            X.segment2.set_or_capacity_vphpl(or_capacity);
            assertEquals(or_capacity,X.segment2.get_or_capacity_vphpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_or_max_vehicles(){
        TestData X = new TestData();
        try {
            float or_max_vehicles = 24983.234f;
            X.segment2.set_or_max_vehicles(or_max_vehicles);
            assertEquals(or_max_vehicles,X.segment2.get_or_max_vehicles(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_delete_onramp(){
        TestData X = new TestData();

        // delete an existing onramp
        assertTrue(X.segment2.delete_onramp());

        // try to delete an non-existing onramp
        assertFalse(X.segment4.delete_onramp());
    }

    @Test
    public void test_add_onramp(){
        TestData X = new TestData();
        assertFalse(X.segment0.has_onramp());
        X.segment0.add_onramp();
        assertTrue(X.segment0.has_onramp());
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
                assertEquals("A2",X.segment2.get_ml_name());
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
        try {
            int ml_lanes = 2436;
            X.segment2.set_mixed_lanes(ml_lanes);
            assertEquals(ml_lanes,X.segment2.get_mixed_lanes());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_capacity_vphpl(){
        TestData X = new TestData();
        try {
            float ml_capacity = 3498.2356f;
            X.segment2.set_capacity_vphpl(ml_capacity);
            assertEquals(ml_capacity,X.segment2.get_capacity_vphpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_jam_density_vpmpl(){
        TestData X = new TestData();
        try {
            float ml_jam_density = 245.234f;
            X.segment2.set_jam_density_vpmpl(ml_jam_density);
            assertEquals(ml_jam_density,X.segment2.get_jam_density_vpmpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_get_freespeed_mph(){
        TestData X = new TestData();
        try {
            float ml_speed = 348934.435f;
            X.segment2.set_freespeed_mph(ml_speed);
            assertEquals(ml_speed,X.segment2.get_freespeed_mph(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


//////////////////////////////////////////////////////////////////////




    @Ignore
    @Test
    public void test_delete_segment() {
//
//        try {
//
//        Project project = load_test_project();
//        FreewayScenario scenario = project.get_scenario_with_name("scenarioA");
//            // delete the zeroth segment
//            scenario.delete_segment(0);
//
//            // delete a middle segment
//            scenario.delete_segment(4);
//
//            // delete last segment
//            scenario.delete_segment(scenario.get_num_segments()-1);
//
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
    }


    @Test
    public void test_delete_ramp() {

        try {



        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void test_set_segment_name(){
        TestData X = new TestData();
        try {
            System.out.println(X.segment2.get_name());
            X.segment2.set_name("valid name");
            System.out.println(X.segment2.get_name());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }



}
