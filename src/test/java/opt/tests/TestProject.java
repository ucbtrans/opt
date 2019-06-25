package opt.tests;

import opt.data.OPTFactory;
import opt.data.Project;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;

public class TestProject {


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
    public void test_create_scenario() {
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

    /////////////////////////////////////
    // private
    /////////////////////////////////////

    private Project load_test_project(){
        Project project = null;
        String project_file_name = TestFactory.get_test_fullpath("project.xml");
        try {
            project = OPTFactory.load_project(project_file_name,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }
}
