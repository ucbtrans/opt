package opt.tests;

import opt.data.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.*;

public class TestDataStructures {

    //////////////////////////////////////
    // Loading and saving projects to file
    //////////////////////////////////////

    @Test
    public void test_load_project_from_file(){
        String project_file_name = get_test_fullpath("project_new.opt");
        boolean validate = true;
        try {
            Project project = OPTFactory.load_project(project_file_name,validate);

            // print
            System.out.print(project.toString());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_save_project_to_file() {
        Project project = load_test_project();
        try {
            OPTFactory.save_project(project,get_test_fullpath("project_saved.opt"));

            Project project_saved = OPTFactory.load_project(get_test_fullpath("project_saved.opt"),false);

            System.out.println(project_saved);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /////////////////////////////////
    // Extracting information
    /////////////////////////////////

    @Test
    public void test_get_scenario_names(){
        Project project = load_test_project();
        System.out.println(project.get_scenario_names());
    }

    @Test
    public void test_get_scenario_with_name(){
        Project project = load_test_project();
        for(String name : project.get_scenario_names())
            System.out.print(name + "\t" + project.get_scenario_with_name(name) + "\n");
    }

    @Test
    public void test_get_segment_info() {
        Project project = load_test_project();
        FreewayScenario scenario = project.get_scenario_with_name("scenarioA");

        for(int i=0;i<scenario.get_num_segments();i++){

            Segment segment = scenario.get_segment(i);

            System.out.println("segment " + i);

            // mainline information
            System.out.println(String.format("\tml lanes %d, length %.1f, capacity %.1f, jam density %.1f, ff speed %.1f",
                    segment.get_ml_lanes(),
                    segment.get_ml_length_feet(),
                    segment.get_ml_capacity_vphpl(),
                    segment.get_ml_jam_density_vpmpl(),
                    segment.get_ml_freespeed_mph()));

            // offramp information
            if (segment.has_offramp()){
                System.out.println(String.format("\tofframp lanes %d, capacity %.1f, max vehicles %.1f",
                        segment.get_fr_lanes(),
                        segment.get_fr_capacity_vphpl(),
                        segment.get_fr_max_vehicles()));
            }

            // onramp information
            if (segment.has_offramp()){
                System.out.println(String.format("\tonramp lanes %d, capacity %.1f, max vehicles %.1f",
                    segment.get_or_lanes(),
                    segment.get_or_capacity_vphpl(),
                    segment.get_or_max_vehicles()));
            }
        }

    }

    @Test
    public void test_set_scenario_name(){
        try {
            Project project = load_test_project();
            Collection<String> names = project.get_scenario_names();
            System.out.print(names);
            String oldname = names.iterator().next();
            project.set_scenario_name(oldname,"newname");
            System.out.print(project.get_scenario_names());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_commodities(){
        Project project = load_test_project();
        FreewayScenario scenario = project.get_scenario_with_name("scenarioA");

        long comm_id = scenario.get_commodities().keySet().iterator().next();
        System.out.println(scenario.get_commodity_by_id(comm_id));

        Commodity new_comm = scenario.create_commodity("new commodity");
        System.out.println(new_comm);
    }

    /////////////////////////////////
    // Creating or modifying objects
    /////////////////////////////////

    @Test
    public void test_create_empty_project(){
        Project project = OPTFactory.create_empty_project();
        System.out.println(project);
    }

    @Test
    public void test_create_empty_scenario() {
        Project project = load_test_project();
        try {
            String new_name = "new_scenario";
            project.create_scenario(new_name);
            System.out.println(new_name + "\t" + project.get_scenario_with_name(new_name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_clone_scenario(){
        try {
            Project project = load_test_project();
            project.clone_scenario("scenarioA","new_scenario");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_insert_segment() {
        Project project = load_test_project();
        FreewayScenario scenario = project.get_scenario_with_name("scenarioA");

        try {

            // insert a zeroth segment
            scenario.insert_segment_upstream_from_index(0);

            // insert a middle segment
            scenario.insert_segment_upstream_from_index(4);

            // insert a last segment
            scenario.insert_segment_downstream_from_index(scenario.get_num_segments()-1);

        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void test_delete_segment() {
        Project project = load_test_project();
        FreewayScenario scenario = project.get_scenario_with_name("scenarioA");

        try {

            // delete the zeroth segment
            scenario.delete_segment(0);

            // delete a middle segment
            scenario.delete_segment(4);

            // delete last segment
            scenario.delete_segment(scenario.get_num_segments()-1);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_add_ramp() {

        Project project = load_test_project();
        FreewayScenario scenario = project.get_scenario_with_name("scenarioA");
        Segment segment = scenario.get_segment(1);

        // add offramp
        segment.set_fr_lanes(1);
        assertTrue(segment.has_offramp());

        // add onramp
        segment.set_or_lanes(1);
        assertTrue(segment.has_onramp());
    }

    @Test
    public void test_delete_ramp() {

        Project project = load_test_project();
        FreewayScenario scenario = project.get_scenario_with_name("scenarioA");

        // delete an existing offramp
        assertTrue(scenario.get_segment(2).delete_offramp());

        // try to delete an non-existing offramp
        assertFalse(scenario.get_segment(1).delete_offramp());

        // delete an existing onramp
        assertTrue(scenario.get_segment(2).delete_onramp());

        // try to delete an non-existing onramp
        assertFalse(scenario.get_segment(1).delete_onramp());
    }

    @Test
    public void test_modify_segment_attributes() {

        Project project = load_test_project();
        FreewayScenario scenario = project.get_scenario_with_name("scenarioA");
        Segment segment = scenario.get_segment(2);

        // onramp lanes
        int or_lanes = 12;
        segment.set_or_lanes(or_lanes);
        assertEquals(or_lanes,segment.get_or_lanes());

        // onramp capacity
        float or_capacity = 123.4563f;
        segment.set_or_capacity_vphpl(or_capacity);
        assertEquals(or_capacity,segment.get_or_capacity_vphpl(),0.001);

        // onramp max_vehicles
        float or_max_vehicles = 24983.234f;
        segment.set_or_max_vehicles(or_max_vehicles);
        assertEquals(or_max_vehicles,segment.get_or_max_vehicles(),0.001);

        // offramp lanes
        int fr_lanes = 125;
        segment.set_fr_lanes(fr_lanes);
        assertEquals(fr_lanes,segment.get_fr_lanes());

        // offramp capacity
        float fr_capacity = 123.4563f;
        segment.set_fr_capacity_vphpl(fr_capacity);
        assertEquals(fr_capacity,segment.get_fr_capacity_vphpl(),0.001);

        // offramp max_vehicles
        float fr_max_vehicles = 235567.346f;
        segment.set_fr_max_vehicles(fr_max_vehicles);
        assertEquals(fr_max_vehicles,segment.get_fr_max_vehicles(),0.001);

        // mainline lanes
        int ml_lanes = 2436;
        segment.set_ml_lanes(ml_lanes);
        assertEquals(ml_lanes,segment.get_ml_lanes());

        // mainline length
        float ml_length = 3480.346f;
        segment.set_ml_length_feet(ml_length);
        assertEquals(ml_length,segment.get_ml_length_feet(),0.001);

        // mainline capacity
        float ml_capacity = 3498.2356f;
        segment.set_ml_capacity_vphpl(ml_capacity);
        assertEquals(ml_capacity,segment.get_ml_capacity_vphpl(),0.001);

        // mainline jam density
        float ml_jam_density = 245.234f;
        segment.set_ml_jam_density_vpmpl(ml_jam_density);
        assertEquals(ml_jam_density,segment.get_ml_jam_density_vpmpl(),0.001);

        // mainline free flow speed
        float ml_speed = 348934.435f;
        segment.set_ml_freespeed_mph(ml_speed);
        assertEquals(ml_speed,segment.get_ml_freespeed_mph(),0.001);

    }

    @Test
    public void test_set_segment_name(){

        Project project = load_test_project();
        FreewayScenario scenario = project.get_scenario_with_name("scenarioA");
        Segment segment = scenario.get_segment(2);

        try {
            System.out.println(segment.get_name());
            segment.set_name("valid name");
            System.out.println(segment.get_name());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /////////////////////////////////////
    // run simulation
    /////////////////////////////////////

    @Test
    @Ignore
    public void test_run_all_scenarios(){
        Project project = load_test_project();
        try {
            project.run_all_scenarios();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Ignore
    public void test_run_scenario(){
        Project project = load_test_project();
        try {
            project.run_scenario("scenarioA");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /////////////////////////////////////
    // private
    /////////////////////////////////////

    private String get_test_fullpath(String testname){
        return (new File("src/test/resources/" + testname)).getAbsolutePath();
    }

    private Project load_test_project(){
        Project project = null;
        String project_file_name = get_test_fullpath("project.opt");
        try {
            project = OPTFactory.load_project(project_file_name,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }

}
