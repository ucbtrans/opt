package opt.tests;

import opt.data.Segment;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestLink extends AbstractTest {

    /////////////////////////////////////
    // abstract methods
    /////////////////////////////////////

    @Ignore
    @Test
    public void test_get_managed_lanes(){

    }

    @Ignore
    @Test
    public void test_get_aux_lanes(){

    }

    @Ignore
    @Test
    public void test_insert_up_segment(){

    }

    @Ignore
    @Test
    public void test_insert_dn_segment(){

    }

    /////////////////////////////////////
    // basic getters
    /////////////////////////////////////

    @Ignore
    @Test
    public void test_get_type(){

    }

    @Ignore
    @Test
    public void test_is_source(){

    }

    @Ignore
    @Test
    public void test_is_sink(){

    }

    @Ignore
    @Test
    public void test_is_ramp(){

    }

    @Ignore
    @Test
    public void test_get_segment(){

    }

    /////////////////////////////////////
    // lanes
    /////////////////////////////////////

    @Ignore
    @Test
    public void test_get_gp_lanes(){

    }

    @Ignore
    @Test
    public void test_set_gp_lanes(){

    }

    /////////////////////////////////////
    // segment getters
    /////////////////////////////////////

    @Ignore
    @Test
    public void test_get_up_segment(){

    }

    @Ignore
    @Test
    public void test_get_dn_segment(){

    }

    /////////////////////////////////////
    // link parameters
    /////////////////////////////////////

    @Ignore
    @Test
    public void test_get_capacity_vphpl(){

    }

    @Ignore
    @Test
    public void test_get_jam_density_vpkpl(){

    }

    @Ignore
    @Test
    public void test_get_freespeed_kph(){

    }

    @Test
    public void test_set_capacity_vphpl(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            float ml_capacity = 3498.2356f;
            segment2.fwy.set_capacity_vphpl(ml_capacity);
            assertEquals(ml_capacity,segment2.fwy.get_capacity_vphpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_jam_density_vpkpl(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            float ml_jam_density = 245.234f;
            segment2.fwy.set_jam_density_vpkpl(ml_jam_density);
            assertEquals(ml_jam_density,segment2.fwy.get_jam_density_vpkpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_freespeed_kph(){
        TestData X = new TestData();
        Segment segment2 = X.scenario.get_segment_by_name("sA2");
        try {
            float ml_speed = 348934.435f;
            segment2.fwy.set_freespeed_kph(ml_speed);
            assertEquals(ml_speed,segment2.fwy.get_freespeed_kph(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /////////////////////////////////////
    // demands and splits
    /////////////////////////////////////

    @Ignore
    @Test
    public void test_set_demand_vph(){

    }

    @Ignore
    @Test
    public void test_set_split(){

    }


}
