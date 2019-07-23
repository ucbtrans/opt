package opt.tests;

import opt.data.AbstractLink;
import opt.data.Commodity;
import opt.data.FreewayScenario;
import opt.data.Segment;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.*;

public class TestFreewayScenario extends AbstractTest {

    @Test
    public void test_deep_copy(){
        TestData X = new TestData();
        FreewayScenario new_scn = X.scenario.deep_copy();
        assertTrue(new_scn.equals(X.scenario));
    }

    /////////////////////////////////////
    // scenario getters
    /////////////////////////////////////

    @Test
    public void test_get_name(){
        TestData X = new TestData();
        assertEquals("scenarioA",X.scenario.get_name());
    }

    @Test
    public void test_get_links(){
        TestData X = new TestData();
        Collection<AbstractLink> links = X.scenario.get_links();
        assertNotNull(links);
        assertEquals(18,links.size());
    }

    /////////////////////////////////////
    // segment getters
    /////////////////////////////////////

    @Test
    public void test_get_segments(){
        TestData X = new TestData();
        Collection<Segment> segments = X.scenario.get_segments();
        assertNotNull(segments);
        assertEquals(11,segments.size());
    }

    @Test
    public void test_get_segment_names(){
        TestData X = new TestData();
        assertEquals(11,X.scenario.get_segment_names().size());
    }

    @Test
    public void test_get_segment_by_name(){
        TestData X = new TestData();
        assertEquals("sA1",X.scenario.get_segment_by_name("sA1").get_name());
    }

    @Test
    public void test_get_segment_with_id(){
        TestData X = new TestData();
        Segment segment = X.scenario.get_segment_with_id(0l);
        assertNotNull(segment);
    }

    /////////////////////////////////////
    // commodity getters and setters
    /////////////////////////////////////

    @Test
    public void test_get_commodities(){
        TestData X = new TestData();
        assertEquals(1,X.scenario.get_commodities().size());
    }

    @Test
    public void test_get_commodity_by_name(){
        try {
            TestData X = new TestData();
            assertNotNull(X.scenario.get_commodity_by_name("c1"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_create_commodity(){
        try {
            TestData X = new TestData();
            Commodity new_comm = X.scenario.create_commodity("new commodity");
            assertNotNull(new_comm);
            assertEquals(new_comm,X.scenario.get_commodity_by_name("new commodity"));
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }



    @Test
    public void test_delete_segment() {
        try {
            TestData X = new TestData();
            Segment segment2 = X.scenario.get_segment_by_name("sA3");
            X.scenario.delete_segment(segment2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void test_run_on_new_thread(){

    }


}
