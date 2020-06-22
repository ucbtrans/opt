package opt.tests;

import opt.data.FreewayScenario;
import opt.data.Project;
import opt.data.ProjectFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TestProject extends AbstractTest {


    @Test
    @Ignore
    public void test_load(){
        try {
            String optfile = "/home/gomes/Desktop/101/uuu.opt";
            Project project = ProjectFactory.load_project(optfile,true);
//            FreewayScenario scenario = project.get_scenario_with_name("scenarioA");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /////////////////////////////////////
    // run
    /////////////////////////////////////

    @Test
    @Ignore
    public void test_run_all_scenarios(){
        try {
            TestData X = new TestData("project2.opt");
            X.project.run_all_scenarios();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Ignore
    public void test_run_scenario(){
        try {
            TestData X = new TestData("project2.opt");
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
            TestData X = new TestData("project2.opt");
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
            TestData X = new TestData("project2.opt");
            Collection<FreewayScenario> x = X.project.get_scenarios();
            assertEquals(1,x.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_get_scenario_with_name(){
        try {
            TestData X = new TestData("project2.opt");
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
            TestData X = new TestData("project2.opt");
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
            TestData X = new TestData("project2.opt");
            String new_name = "new_scenario";
            X.project.create_scenario(new_name);
            assertNotNull(X.project.get_scenario_with_name(new_name));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

//    GG: This test is not running because clone is incorrectly implemented. In AbstractParameters.clone it calls a
//    constructor for the abstract class with an itemized argument list, from the abstract class. This does not work
//    because a FreewayParameters object needs a constructor with aux_lanes. Better would be to override
//    public AbstractParameters(AbstractParameters that).
    @Ignore
    @Test
    public void test_clone_scenario(){
        try {
            TestData X = new TestData("project2.opt");
            X.project.clone_scenario("scenarioA","new_scenario");
            assertTrue(
                    X.project.get_scenario_with_name("scenarioA")
                    .equals(X.project.get_scenario_with_name("new_scenario")) );
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
