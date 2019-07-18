package opt.tests;

import opt.data.ProjectFactory;
import opt.data.Project;
import org.junit.Test;

import static org.junit.Assert.fail;

public class TestProjectFactory extends AbstractTest {

    @Test
    public void test_load_project_from_file(){
        String project_file_name = get_test_fullpath("project.opt");
        boolean validate = true;
        try {
            Project project = ProjectFactory.load_project(project_file_name,validate);

            // print
            System.out.print(project.toString());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_create_empty_project(){
        Project project = ProjectFactory.create_empty_project();
        System.out.println(project);
    }


    @Test
    public void test_save_project_to_file() {
        try {
            TestData X = new TestData();
            ProjectFactory.save_project(X.project,get_test_fullpath("project_saved.opt"));

            Project project_saved = ProjectFactory.load_project(get_test_fullpath("project_saved.opt"),false);

            System.out.println(project_saved);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
