package opt;

import opt.data.FreewayScenario;
import opt.data.Project;
import opt.data.ProjectFactory;
import opt.utils.Version;

import java.io.FileWriter;
import java.io.IOException;

public class Benchmarker {

    FileWriter log = null;

    long time_start;
    float duration;
    public float runtime;

    public Benchmarker(String logfile,String description, float duration){
        this.duration = duration;
        try {
            log = new FileWriter(logfile);
            log.write(description + "\n");
            log.write(String.format("otm\t%s\n", Version.getOTMSimGitHash()));
            log.write(String.format("opt\t%s\n",Version.getOPTGitHash()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(boolean celloutput, boolean lgoutput){
        runtime = Float.NaN;
        try {

            start_time();
            String project_file_name = "/home/gomes/Desktop/test/benchmark/benchmark.opt";
            Project project = ProjectFactory.load_project(project_file_name,false);
            log.write(String.format("load_opt\t%f\n",get_elapsed()));

            FreewayScenario fwyscenario = project.get_scenarios().iterator().next();
            fwyscenario.set_start_time(0f);
            fwyscenario.set_sim_duration(duration);

            OTMTask task = new OTMTask(null,fwyscenario,300f,-1,true,true,this);

            task.run_simulation(this,celloutput,lgoutput);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally {
            try{
                if(log!=null)
                    log.close();
            }
            catch(IOException e){
                System.err.println(e);
            }
        }

    }

    public void write(String str){
        try {
            if(str.equals("run")) {
                float elapsed = get_elapsed();
                runtime = elapsed;
                log.write(String.format("%s\t%f\n",str,elapsed));
            }
            else
                log.write(String.format("%s\t%f\n",str,get_elapsed()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start_time(){
        time_start = System.currentTimeMillis();
    }

    public float get_elapsed(){
        long t = System.currentTimeMillis();
        float e = (t-time_start)/1000f;
        time_start = t;
        return e;
    }

}
