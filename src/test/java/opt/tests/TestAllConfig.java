package opt.tests;

import opt.data.Project;
import opt.data.ProjectFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestAllConfig extends AbstractTest {

    String testname;

    public TestAllConfig(String testname){
        this.testname = testname;
    }

    @Test
    public void test_load() {
        System.out.println(testname + " load");
        try {
            Project project = ProjectFactory.load_project(get_test_fullpath(testname),true);
            assertNotNull(project);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_save() {
        System.out.println(testname + " save");
        try {
            Project project = ProjectFactory.load_project(get_test_fullpath(testname),true);
            ProjectFactory.save_project(project,output_folder + testname + "_saved.opt");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_save_xml() {
        System.out.println(testname + " save xml");
        try {
            Project project = ProjectFactory.load_project(get_test_fullpath(testname),true);
            ProjectFactory.save_scenario(project.get_scenarios().iterator().next(),output_folder + testname + "_saved.xml",true);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Ignore
    public void test_run_all_scenarios(){
        System.out.println(testname + " run");
        try {
            Project project = ProjectFactory.load_project(get_test_fullpath(testname),true);
            project.run_all_scenarios();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
