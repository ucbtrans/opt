package opt.tests;

import opt.data.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Test
    public void test_get_freeways(){
        TestData X = new TestData();
        List<List<Segment>> fwys = X.scenario.get_freeways();
        assertEquals(6,fwys.get(0).size());
        assertEquals(2,fwys.get(1).size());
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
        ParametersFreeway params = new ParametersFreeway(100f,200f,300f,100f,200f,300f);
        Segment new_segment =  X.scenario.create_isolated_segment("Lonely segment",params,AbstractLink.Type.freeway);
        assertNotNull(new_segment);
    }

    @Test
    public void test_delete_segment() {
        try {
            TestData X = new TestData();
            Segment sA2 = X.scenario.get_segment_by_name("sA2");
            Segment sA3 = X.scenario.get_segment_by_name("sA3");
            Segment sA4 = X.scenario.get_segment_by_name("sA4");

            // delete internal segement

            Set<String> link_names = X.scenario.get_links().stream()
                    .map(link->link.get_name())
                    .collect(Collectors.toSet());

            Set<Long> node_ids = X.scenario.get_nodes().keySet();

            assertTrue(link_names.contains("lA3"));
            assertTrue(link_names.contains("lA8"));
            assertTrue(link_names.contains("lA9"));
            assertTrue(node_ids.contains(9l));
            assertTrue(node_ids.contains(10l));

            X.scenario.delete_segment(sA3);

            assertNull( X.scenario.get_segment_by_name("sA3") );
            assertTrue( sA2.get_dnstrm_segments().isEmpty() );
            assertTrue( sA4.get_upstrm_segments().isEmpty() );

            link_names = X.scenario.get_links().stream()
                    .map(link->link.get_name())
                    .collect(Collectors.toSet());

            node_ids = X.scenario.get_nodes().keySet();

            assertFalse(link_names.contains("lA3"));
            assertFalse(link_names.contains("lA8"));
            assertFalse(link_names.contains("lA9"));
            assertFalse(node_ids.contains(9l));
            assertFalse(node_ids.contains(10l));

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
