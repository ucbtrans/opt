package opt.tests;

import opt.data.FreewayScenario;
import opt.data.Project;
import opt.data.ProjectFactory;
import opt.simulation.OTMTask;
import org.junit.Test;

import static org.junit.Assert.fail;

public class TestSimulation extends AbstractTest {

    @Test
    public void test_sequential(){
        load_task().run_simulation();
    }

    @Test
    public void test_parallel(){
        Thread th = new Thread(load_task());
        th.setDaemon(true);
        th.start();
    }

    private static OTMTask load_task(){

        String project_file_name = get_test_fullpath("project_saved.opt");
        boolean validate = true;
        try {
            Project project = ProjectFactory.load_project(project_file_name,validate);
            FreewayScenario fwy_scenario = project.get_scenarios().iterator().next();

            float start_time = 0f;
            float duration = 600f;
            int progbar_steps = 10;

            return new OTMTask(null,fwy_scenario,start_time,duration,progbar_steps);

        } catch (Exception e) {
            fail(e.getMessage());
        }
        return null;
    }
}
