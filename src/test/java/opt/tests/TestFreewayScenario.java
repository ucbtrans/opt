package opt.tests;

import opt.data.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class TestFreewayScenario extends AbstractTest {

    /////////////////////////////////////
    // scenario getters
    /////////////////////////////////////

    @Test
    public void test_get_links(){
        TestData X = new TestData();
        Collection<AbstractLink> links = X.scenario.get_links();
        assertNotNull(links);
        assertEquals(16,links.stream().count());
    }

    /////////////////////////////////////
    // segment getters
    /////////////////////////////////////

    @Ignore
    @Test
    public void test_get_segments_tree(){
        TestData X = new TestData();
        List<List<Segment>> segments = X.scenario.get_segments_tree();
        assertNotNull(segments);
        assertEquals(4,segments.get(0).size());
        assertEquals(7,segments.get(1).size());
    }

    @Ignore
    @Test
    public void test_get_links_tree(){
        TestData X = new TestData();
        List<List<AbstractLink>> links = X.scenario.get_links_tree();
        assertEquals(6,links.get(0).size());
        assertEquals(12,links.get(1).size());
    }

    @Test
    public void test_get_segments(){
        TestData X = new TestData();
        Collection<Segment> segments = X.scenario.get_segments();
        assertNotNull(segments);
        assertEquals(9,segments.size());
    }

    @Test
    public void test_get_segment_names(){
        TestData X = new TestData();
        assertEquals(9,X.scenario.get_segment_names().size());
    }

    @Test
    public void test_get_segment_by_name(){
        TestData X = new TestData();
        assertEquals("sA1",X.scenario.get_segment_by_name("sA1").name);
    }

    @Test
    public void test_get_segment_with_id(){
        TestData X = new TestData();
        Segment segment = X.scenario.get_segment_with_id(0l);
        assertNotNull(segment);
    }

    /////////////////////////////////////
    // segment create / delete
    /////////////////////////////////////

    @Test
    public void test_create_isolated_segment() {
        TestData X = new TestData();
        LinkParameters params = new LinkParameters(100,200,300);
        Segment new_segment =  X.scenario.create_isolated_segment("Lonely segment","Lonely link",params,AbstractLink.Type.freeway);
        assertNotNull(new_segment);
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

    /////////////////////////////////////
    // utilities
    /////////////////////////////////////

    @Test
    public void test_is_valid_segment_name() {
        TestData X = new TestData();
        assertFalse(X.scenario.is_valid_segment_name("sA3"));
        assertTrue(X.scenario.is_valid_segment_name("invalid name"));
    }

    @Test
    public void test_is_valid_link_name() {
        TestData X = new TestData();
        assertFalse(X.scenario.is_valid_link_name("lA13"));
        assertTrue(X.scenario.is_valid_link_name("invalid name"));
    }

    /////////////////////////////////////
    // run
    /////////////////////////////////////

    @Ignore
    @Test
    public void test_run_on_new_thread(){

    }

}
