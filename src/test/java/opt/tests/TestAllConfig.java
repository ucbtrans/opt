package opt.tests;

import opt.OTMTask;
import opt.data.FreewayScenario;
import opt.data.Project;
import opt.data.ProjectFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.OTMUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.Assert.*;

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
//    @Ignore
    public void test_run(){
        System.out.println(testname + " run");
        try {
            Project project = ProjectFactory.load_project(get_test_fullpath(testname),true);
            FreewayScenario fwyscenario = project.get_scenarios().iterator().next();
            fwyscenario.set_start_time(0f);
            fwyscenario.set_sim_duration(3600f);
            OTMTask task = new OTMTask(null,fwyscenario,null,-1,true,true,null);
            task.run_simulation(testname,output_folder,null);

            // plot
//            for(AbstractOutput output :  task.get_data()) {
//                if (output instanceof OutputLinkFlow)
//                    ((OutputLinkFlow) output).plot_for_links(null, String.format("%s/%s_link_flow.png", output_folder,prefix));
//                if (output instanceof OutputLinkVehicles)
//                    ((OutputLinkVehicles) output).plot_for_links(null, String.format("%s/%s_link_veh.png", output_folder,prefix));
//            }

            // check the output against expects
            for(String output_path : task.get_file_names()) {
                compare_files(output_path);
            }

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void compare_files(String output_path){

        File outfile = new File(output_path);
        String outname = outfile.getName();

        System.out.println("-------- " + outname + " -----------");

        ClassLoader classLoader = getClass().getClassLoader();

        URL url = classLoader.getResource("test_output/" + outname);

        if(url==null)
            fail("File not found: " + outname);

        File known_outfile = new File(url.getFile());

        ArrayList<ArrayList<Double>> f1 = OTMUtils.read_matrix_csv_file(outfile);
        ArrayList<ArrayList<Double>> f2 = OTMUtils.read_matrix_csv_file(known_outfile);
        assertEquals(f1.size(),f2.size());
        for(int i=0;i<f1.size();i++){
            ArrayList<Double> x1 = f1.get(i);
            ArrayList<Double> x2 = f2.get(i);
            assertEquals(x1.size(),x2.size());
            for(int j=0;j<x1.size();j++) {
                boolean is_same = Math.abs(x1.get(j) - x2.get(j)) < 0.1;

                if(!is_same)
                    System.out.println(String.format("%d\t%d\t%f\t%f",i,j,x1.get(j),x2.get(j)));

                assertTrue(is_same);
            }
        }
    }

}
