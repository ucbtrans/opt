package opt.tests;

import opt.data.*;
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
            System.out.println(X.project.get_scenario_names());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void test_get_scenarios(){

    }

    @Test
    public void test_get_scenario_with_name(){
        try {
            TestData X = new TestData();
            for(String name : X.project.get_scenario_names())
                System.out.print(name + "\t" + X.project.get_scenario_with_name(name) + "\n");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_set_scenario_name(){
        try {
            TestData X = new TestData();
            Collection<String> names = X.project.get_scenario_names();
            System.out.print(names);
            String oldname = names.iterator().next();
            X.project.set_scenario_name(oldname,"newname");
            System.out.print(X.project.get_scenario_names());
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
            System.out.println(new_name + "\t" + X.project.get_scenario_with_name(new_name));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_clone_scenario(){
        try {
            TestData X = new TestData();
            X.project.clone_scenario("scenarioA","new_scenario");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }















}
