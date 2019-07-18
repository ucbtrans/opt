package opt.tests;

import opt.data.FreewayScenario;
import opt.data.ProjectFactory;
import opt.data.Project;
import opt.data.Segment;

import java.io.File;

import static org.junit.Assert.fail;

public abstract class AbstractTest {

    static protected String get_test_fullpath(String testname){
        return (new File("src/test/resources/" + testname)).getAbsolutePath();
    }

    public static class TestData {
        Project project;
        FreewayScenario scenario;
        Segment segment0, segment2, segment4;
        public TestData(){
            try {
                project = ProjectFactory.load_project(get_test_fullpath("project.opt"),true);
                scenario = project.get_scenario_with_name("scenarioA");
                segment0 = scenario.get_segment_with_id(0l);
                segment2 = scenario.get_segment_with_id(2l);
                segment4 = scenario.get_segment_with_id(4l);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

}
