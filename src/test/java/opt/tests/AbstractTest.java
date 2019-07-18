package opt.tests;

import opt.data.FreewayScenario;
import opt.data.OPTFactory;
import opt.data.Project;
import opt.data.Segment;

import java.io.File;
import java.util.Iterator;

import static org.junit.Assert.fail;

public abstract class AbstractTest {

    static protected String get_test_fullpath(String testname){
        return (new File("src/test/resources/" + testname)).getAbsolutePath();
    }

    public class TestData {
        Project project;
        FreewayScenario scenario;
        Segment segment0, segment1, segment2;
        public TestData(){
            try {
                project = OPTFactory.load_project(get_test_fullpath("project.opt"),true);
                scenario = project.get_scenario_with_name("scenarioA");
                Iterator<Segment> it = scenario.get_segments().iterator();

                segment0 = it.next();
                segment1 = it.next();
                segment2 = it.next();
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

}
