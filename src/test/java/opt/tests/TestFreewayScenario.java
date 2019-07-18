package opt.tests;

import opt.data.AbstractLink;
import opt.data.Commodity;
import opt.data.FreewayScenario;
import opt.data.Segment;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TestFreewayScenario extends AbstractTest {

    @Test
    public void test_deep_copy(){
        TestData X = new TestData();
        FreewayScenario new_scn = X.scenario.deep_copy();
        assertTrue(new_scn.equals(X.scenario));
    }

    @Test
    public void test_get_segments(){
        TestData X = new TestData();
        FreewayScenario scenario = X.project.get_scenario_with_name("scenarioA");
        Collection<Segment> segments = scenario.get_segments();
        assertNotNull(segments);
        assertEquals(7,segments.size());
    }

    @Test
    public void test_get_links(){
        TestData X = new TestData();
        FreewayScenario scenario = X.project.get_scenario_with_name("scenarioA");
        Collection<AbstractLink> links = scenario.get_links();
        assertNotNull(links);
        assertEquals(12,links.size());
    }

    @Test
    public void test_get_commodities(){
        TestData X = new TestData();
        FreewayScenario scenario = X.project.get_scenario_with_name("scenarioA");
        assertEquals(1,scenario.get_commodities().size());
    }

    @Test
    public void test_get_commodity_by_name(){
        try {
            TestData X = new TestData();
            FreewayScenario scenario = X.project.get_scenario_with_name("scenarioA");
            assertNotNull(scenario.get_commodity_by_name("c1"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_create_commodity(){
        try {
            TestData X = new TestData();
            FreewayScenario scenario = X.project.get_scenario_with_name("scenarioA");
            Commodity new_comm = scenario.create_commodity("new commodity");
            assertNotNull(new_comm);
            assertEquals(new_comm,scenario.get_commodity_by_name("new commodity"));
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Ignore
    @Test
    public void test_run_on_new_thread(){

    }


}
