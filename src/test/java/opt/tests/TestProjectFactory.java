package opt.tests;

import opt.data.ProjectFactory;
import opt.data.Project;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestProjectFactory extends AbstractTest {

    @Test
    public void test_load_project_from_file(){
        String project_file_name = get_test_fullpath("project.opt");
        boolean validate = true;
        try {
            Project project = ProjectFactory.load_project(project_file_name,validate);
            assertNotNull(project);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_create_empty_project(){
        Project project = ProjectFactory.create_empty_project();
        assertNotNull(project);
    }

    @Test
    public void test_save_project_to_file() {
        try {
            TestData X = new TestData();
            ProjectFactory.save_project(X.project,get_test_fullpath("project_saved.opt"));
            Project project_saved = ProjectFactory.load_project(get_test_fullpath("project_saved.opt"),false);
            assertTrue(X.project.equals(project_saved));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
