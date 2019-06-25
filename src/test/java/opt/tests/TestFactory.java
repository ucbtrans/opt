package opt.tests;

import opt.data.OPTFactory;
import opt.data.Project;
import org.junit.Test;

import java.io.File;

public class TestFactory {

    public static String get_test_fullpath(String testname){
        return (new File("src/test/resources/" + testname)).getAbsolutePath();
    }

    @Test
    public void test_load_project(){
        String project_file_name = get_test_fullpath("project.xml");
        boolean validate = true;
        try {
            Project project = OPTFactory.load_project(project_file_name,validate);
            System.out.println(project);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_create_empty_project(){
        Project project = OPTFactory.create_empty_project();
        System.out.println(project);
    }

}
