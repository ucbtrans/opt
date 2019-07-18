package opt.tests;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestSegment extends AbstractTest {

    /////////////////////////////////////
    // name and length
    /////////////////////////////////////

    @Ignore
    @Test
    public void test_set_get_name(){

        try {

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

    @Ignore
    @Test
    public void test_get_links(){
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

    @Ignore
    @Test
    public void test_has_offramp(){
    }

    @Ignore
    @Test
    public void test_set_get_fr_name(){

        try {

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
        assertTrue(X.segment2.delete_offramp());

        // try to delete an non-existing offramp
        assertFalse(X.segment1.delete_offramp());
    }

    @Ignore
    @Test
    public void test_add_offramp(){
    }

    /////////////////////////////////////
    // onramp
    /////////////////////////////////////

    @Ignore
    @Test
    public void test_has_onramp(){
    }

    @Ignore
    @Test
    public void test_set_get_or_name(){
        try {

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
        assertFalse(X.segment1.delete_onramp());
    }

    @Ignore
    @Test
    public void test_add_onramp(){
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

    @Ignore
    @Test
    public void test_set_get_ml_name(){
        try {

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
