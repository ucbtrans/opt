package opt.tests;

import opt.UserSettings;
import opt.data.*;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test
    public void test_insert_up_segment(){
        TestData X = new TestData();
        Segment sA1 = X.scenario.get_segment_by_name("sA1");
        Segment sA2 = X.scenario.get_segment_by_name("sA2");
        Segment sA3 = X.scenario.get_segment_by_name("sA3");
        Segment sA7 = X.scenario.get_segment_by_name("sA7");
        Segment sA8 = X.scenario.get_segment_by_name("sA8");
        LinkFreeway l1 = (LinkFreeway) sA1.fwy();
        LinkFreeway l3 = (LinkFreeway) sA3.fwy();
        LinkOnramp l8 = sA3.out_ors(0);
        LinkOfframp l9 = sA3.out_frs(0);
        LinkConnector l12 = (LinkConnector) sA7.fwy();
        LinkOnramp l13 = sA8.out_ors(0);

        // case : fwy link has an upstream segment
        Segment sNEW1 = l3.insert_up_segment("sNEW1",
                UserSettings.getDefaultFreewayParams("lNEW1",500f),
                null);

        assertEquals(2l,sNEW1.fwy().get_up_link().id);
        assertEquals(3l,sNEW1.fwy().get_dn_link().id);
        assertEquals("lNEW1",sA3.fwy().get_up_link().get_name());
        assertEquals("lNEW1",sA2.fwy().get_dn_link().get_name());

        // case : connector link has an upstream segment
        Segment sNEW2 = l12.insert_up_segment("sNEW2",
                UserSettings.getDefaultFreewayParams("lNEW2",500f),
                UserSettings.getDefaultOfframpParams("lNEW3",100f) );
        assertNull(sNEW2);

        // case : onramp link has an upstream segment
        Segment sNEW3 = l13.insert_up_segment("sNEW3",
                UserSettings.getDefaultFreewayParams("lNEW2",500f),
                null );
        assertNull(sNEW3);

        // case : offramp link
        Segment sNEW4 = l9.insert_up_segment("sNEW4",
                UserSettings.getDefaultFreewayParams("lNEW2",500f),
                UserSettings.getDefaultOfframpParams("lNEW3",100f) );
        assertNull(sNEW4);

        // case : fwy link has no upstream segment
        Segment sNEW5 = l1.insert_up_segment("sNEW5",
                UserSettings.getDefaultFreewayParams("lNEW5",500f),
                null );
        assertNull(sNEW5.fwy().get_up_link());
        assertEquals(1l,sNEW5.fwy().get_dn_link().id);
        assertEquals("lNEW5",sA1.fwy().get_up_link().get_name());

        // case : connector link has no upstream segment
        // TODO

        // case : onramp link has no upstream segment
        Segment sNEW7 = l8.insert_up_segment("sNEW7",
                UserSettings.getDefaultFreewayParams("lNEW7",500f),
                null );
        assertEquals(AbstractLink.Type.connector,sNEW7.fwy().get_type());
        assertEquals(8l,sNEW7.fwy().get_dn_link().id);
        assertEquals("lNEW7",l8.get_up_link().get_name());

    }

    @Test
    public void test_insert_dn_segment(){

        TestData X = new TestData();

        Segment sA1 = X.scenario.get_segment_by_name("sA1");
        Segment sA3 = X.scenario.get_segment_by_name("sA3");
        Segment sA4 = X.scenario.get_segment_by_name("sA4");
        Segment sA7 = X.scenario.get_segment_by_name("sA7");
        Segment sA9 = X.scenario.get_segment_by_name("sA9");

        LinkFreeway l3 = (LinkFreeway) sA3.fwy();
        LinkOfframp l7 = sA1.out_frs(0);
        LinkOnramp l8 = sA3.out_ors(0);
        LinkOfframp l9 = sA3.out_frs(0);
        LinkConnector l12 = (LinkConnector) sA7.fwy();
        LinkFreeway l16 = (LinkFreeway) sA9.fwy();

        // case : fwy link has a downstream segment
        Segment sNEW1 = l3.insert_dn_segment("sNEW1",
                UserSettings.getDefaultFreewayParams("lNEW1",500f),
                null);
        assertEquals(3l,sNEW1.fwy().get_up_link().id);
        assertEquals(4l,sNEW1.fwy().get_dn_link().id);
        assertEquals("lNEW1",sA3.fwy().get_dn_link().get_name());
        assertEquals("lNEW1",sA4.fwy().get_up_link().get_name());

        // case : connector link has a dnstream segment
        Segment sNEW2 = l12.insert_dn_segment("sNEW2",
                UserSettings.getDefaultFreewayParams("yrjrjyw",500f),
                UserSettings.getDefaultOnrampParams("sdgsag",500f) );
                assertNull(sNEW2);

        // case : onramp link
        Segment sNEW3 = l8.insert_dn_segment("sNEW3",
                UserSettings.getDefaultFreewayParams("yrjrjyw",500f),
                null );
        assertNull(sNEW3);

        // case : offramp link has a downstream segment
        Segment sNEW4 = l7.insert_dn_segment("sNEW4",
                UserSettings.getDefaultFreewayParams("yrjrjyw",500f),
                null );
        assertNull(sNEW4);

        // case : fwy link has no dnstream segment
        Segment sNEW5 = l16.insert_dn_segment("sNEW5",
                UserSettings.getDefaultFreewayParams("lNEW5",500f),
                null );
        assertNull(sNEW5.fwy().get_dn_link());
        assertEquals(16l,sNEW5.fwy().get_up_link().id);
        assertEquals("lNEW5",sA9.fwy().get_dn_link().get_name());

        // case : connector link has no upstream segment
        // TODO

        // case : offramp link has no downstream segment
        Segment sNEW7 = l9.insert_dn_segment("sNEW7",
                UserSettings.getDefaultFreewayParams("lNEW7",500f),
                null );
        assertEquals(AbstractLink.Type.connector,sNEW7.fwy().get_type());
        assertEquals(9l,sNEW7.fwy().get_up_link().id);
        assertEquals("lNEW7",l9.get_dn_link().get_name());
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
        Segment sA2 = X.scenario.get_segment_by_name("sA2");
        try {
            float ml_capacity = 3498.2356f;
            sA2.fwy().set_capacity_vphpl(ml_capacity);
            assertEquals(ml_capacity,sA2.fwy().get_capacity_vphpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_jam_density_vpkpl(){
        TestData X = new TestData();
        Segment sA2 = X.scenario.get_segment_by_name("sA2");
        try {
            float ml_jam_density = 245.234f;
            sA2.fwy().set_jam_density_vpkpl(ml_jam_density);
            assertEquals(ml_jam_density,sA2.fwy().get_jam_density_vpkpl(),0.001);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_freespeed_kph(){
        TestData X = new TestData();
        Segment sA2 = X.scenario.get_segment_by_name("sA2");
        try {
            float ml_speed = 348934.435f;
            sA2.fwy().set_freespeed_kph(ml_speed);
            assertEquals(ml_speed,sA2.fwy().get_freespeed_kph(),0.001);
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


    /////////////////////////////////////
    // connect
    /////////////////////////////////////

    @Test
    public void test_connect_to_upstream(){

        TestData X = new TestData();

        // TODO GG REMOVE THE FD CONSTRUCTOR FOR PARAMETERS
        ParametersFreeway params = new ParametersFreeway(100f,200f,300f);
        Segment conn = X.scenario.create_isolated_segment("new segment",params, AbstractLink.Type.connector);

        Segment sA3 = X.scenario.get_segment_by_name("sA3");
        LinkOnramp lA8 = sA3.out_ors(0);

        lA8.connect_to_upstream(conn.fwy());

        assertTrue(lA8.get_up_link()==conn.fwy());
        assertTrue(lA8.get_up_segment()==conn);
        assertTrue(sA3.get_upstrm_links().contains(conn.fwy()));
        assertTrue(sA3.get_upstrm_segments().contains(conn));
        assertTrue(conn.get_dnstrm_links().contains(lA8));
        assertTrue(conn.get_dnstrm_segments().contains(sA3));
        assertTrue(conn.fwy().get_dn_link()==lA8);
        assertTrue(conn.fwy().get_dn_segment()==sA3);

    }

}
