package opt.tests;

import opt.data.OPTFactory;
import opt.data.Project;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;

public class TestFactory extends AbstractTest {

    @Test
    public void test_load_project_from_file(){
        String project_file_name = get_test_fullpath("project.opt");
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
    public void test_create_empty_project(){
        Project project = OPTFactory.create_empty_project();
        System.out.println(project);
    }

    @Ignore
    @Test
    public void test_deep_copy_scenario(){

    }

    @Test
    public void test_save_project_to_file() {
        try {
            TestData X = new TestData();
            OPTFactory.save_project(X.project,get_test_fullpath("project_saved.opt"));

            Project project_saved = OPTFactory.load_project(get_test_fullpath("project_saved.opt"),false);

            System.out.println(project_saved);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
