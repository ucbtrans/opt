package opt.tests;

import javafx.concurrent.Task;
import opt.data.FreewayScenario;
import opt.data.Project;
import opt.data.ProjectFactory;
import opt.simulation.OTMTask;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.fail;

public class TestSimulation extends AbstractTest {

    class MyRunnable implements Runnable {

        private final long waitTime;

        public MyRunnable(int timeInMillis)
        {
            this.waitTime = timeInMillis;
        }

        @Override
        public void run()
        {
            try {
                // sleep for user given millisecond
                // before checking again

                for(int i=0;i<100;i++){
                    Thread.sleep(waitTime);
                    System.out.print(".");
                }

                // return current thread name
                System.out.println(Thread
                        .currentThread()
                        .getName());
            }

            catch (InterruptedException ex) {
                Logger.getLogger(MyRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    @Test
    public void test_task(){

// create two object of MyRunnable class
        // for FutureTask and sleep 1000, 2000
        // millisecond before checking again
        MyRunnable myrunnableobject1 = new MyRunnable(100);

        FutureTask<String>
                futureTask1 = new FutureTask<>(myrunnableobject1,
                "FutureTask1 is complete");

        // create thread pool of 2 size for ExecutorService
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // submit futureTask1 to ExecutorService
        executor.submit(futureTask1);


        while (true) {
            try {

                // if both future task complete
                if (futureTask1.isDone()) {

                    System.out.println("FutureTask Complete");

                    // shut down executor service
                    executor.shutdown();
                    return;
                }

                if (!futureTask1.isDone()) {

                    // wait indefinitely for future
                    // task to complete
                    System.out.println("FutureTask1 output = "
                            + futureTask1.get());
                }


            }

            catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        }

    }


    @Test
    public void test_run_simulation(){

        String project_file_name = get_test_fullpath("project2_rm.opt");
        boolean validate = true;
        try {
            Project project = ProjectFactory.load_project(project_file_name,validate);
            FreewayScenario fwy_scenario = project.get_scenarios().iterator().next();

            float start_time = 0f;
            float duration = 600f;
            int progbar_steps = 10;

            OTMTask otm_task = new OTMTask(null,fwy_scenario,start_time,duration,progbar_steps);

            // single-thread run
//            otm_task.run_simulation();


            // multi-thread run
            Thread th = new Thread(otm_task);
            th.setDaemon(true);
            th.start();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
