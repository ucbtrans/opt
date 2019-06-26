package opt.tests;

import opt.data.FreewayScenario;
import opt.data.OPTFactory;
import opt.data.Project;
import opt.data.Segment;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

public class TestDataStructures {

    //////////////////////////////////////
    // Loading and saving projects to file
    //////////////////////////////////////

    @Test
    public void test_load_project_from_file(){
        String project_file_name = get_test_fullpath("project.xml");
        boolean validate = true;
        try {
            Project project = OPTFactory.load_project(project_file_name,validate);

            // print
            System.out.print(project.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_save_project_to_file() {
        Project project = load_test_project();
        project.save_to_file(get_test_fullpath("project_saved.xml"));
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
    @Ignore
    public void test_clone_scenario(){
        Project project = load_test_project();
        Collection<String> scenario_names = project.get_scenario_names();
        String new_name = "new_scenario";
        try {
            project.clone_scenario(scenario_names.iterator().next(),new_name);
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void test_add_ramp() {
        // TODO GG IMPLEMENT THIS
    }

    @Test
    @Ignore
    public void test_delete_ramp() {
        // TODO GG IMPLEMENT THIS
    }

    @Test
    @Ignore
    public void test_modify_segment_attributes() {
        // TODO GG IMPLEMENT THIS
    }

    /////////////////////////////////////
    // private
    /////////////////////////////////////

    private String get_test_fullpath(String testname){
        return (new File("src/test/resources/" + testname)).getAbsolutePath();
    }

    private Project load_test_project(){
        Project project = null;
        String project_file_name = get_test_fullpath("project.xml");
        try {
            project = OPTFactory.load_project(project_file_name,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }

}
