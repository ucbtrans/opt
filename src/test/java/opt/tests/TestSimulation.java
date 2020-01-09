package opt.tests;

import opt.data.FreewayScenario;
import opt.data.Project;
import opt.data.ProjectFactory;
import opt.simulation.OTMTask;
import org.junit.Test;

import static org.junit.Assert.fail;

public class TestSimulation extends AbstractTest {

    @Test
    public void test_run_simulation(){

        String project_file_name = get_test_fullpath("project2_rm.opt");
        boolean validate = true;
        try {
            Project project = ProjectFactory.load_project(project_file_name,validate);
            FreewayScenario fwy_scenario = project.get_scenarios().iterator().next();

            float duration = 600f;
            long sim_delay = 0l;
            float refresh_seconds = 10f;

            OTMTask otm_task = new OTMTask(fwy_scenario,duration,sim_delay,refresh_seconds);

            // single-thread run
            otm_task.run_simulation();


            // multi-thread run
//            Thread th = new Thread(otm_task);
//            th.setDaemon(true);
//            th.start();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
