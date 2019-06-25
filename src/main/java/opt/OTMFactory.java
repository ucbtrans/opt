package opt;

public class OTMFactory {

    public Project load_project(String project_file_name){

        jaxb.Project jaxb_project = null;
        return new Project(jaxb_project);
    }

}
