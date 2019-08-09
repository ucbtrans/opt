package opt.tests;

import opt.data.FreewayScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TestProject extends AbstractTest {

    /////////////////////////////////////
    // run
    /////////////////////////////////////

    @Test
    @Ignore
    public void test_run_all_scenarios(){
        try {
            TestData X = new TestData();
            X.project.run_all_scenarios();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Ignore
    public void test_run_scenario(){
        try {
            TestData X = new TestData();
            X.project.run_scenario("scenarioA");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /////////////////////////////////////
    // getters / setters
    /////////////////////////////////////

    @Test
    public void test_get_scenario_names(){
        try {
            TestData X = new TestData();
            Collection<String> x = X.project.get_scenario_names();
            assertEquals(1,x.size());
            assertTrue(x.contains("scenarioA"));
            assertFalse(x.contains("scenarioB"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_get_scenarios(){
        try {
            TestData X = new TestData();
            Collection<FreewayScenario> x = X.project.get_scenarios();
            assertEquals(1,x.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_get_scenario_with_name(){
        try {
            TestData X = new TestData();
            for(String name : X.project.get_scenario_names())
                assertNotNull(X.project.get_scenario_with_name(name));
            assertNull(X.project.get_scenario_with_name("bad name"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_scenario_name(){
        try {
            TestData X = new TestData();
            X.project.set_scenario_name("scenarioA","newname");
            Collection<String> names = X.project.get_scenario_names();
            assertTrue(names.contains("newname"));
            assertFalse(names.contains("scenarioA"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /////////////////////////////////////
    // create scenarios
    /////////////////////////////////////

    @Test
    public void test_create_scenario() {
        try {
            TestData X = new TestData();
            String new_name = "new_scenario";
            X.project.create_scenario(new_name);
            assertNotNull(X.project.get_scenario_with_name(new_name));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_clone_scenario(){
        try {
            TestData X = new TestData();
            X.project.clone_scenario("scenarioA","new_scenario");
            assertTrue(
                    X.project.get_scenario_with_name("scenarioA")
                    .equals(X.project.get_scenario_with_name("new_scenario")) );
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
