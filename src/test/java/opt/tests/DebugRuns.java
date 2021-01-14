package opt.tests;

import opt.data.*;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;

public class DebugRuns extends AbstractTest {

    @Ignore
    @Test
    public void test_convert_to_otm() {
        try {
            String optfile = "/home/gomes/Desktop/x/test.opt";
            String otmfile = "/home/gomes/Desktop/x/test_save.xml";
            Project project = ProjectFactory.load_project(optfile,true);
            FreewayScenario scenario = project.get_scenarios().iterator().next();
            ProjectFactory.save_scenario(scenario, otmfile,true);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
