package opt.data;

import error.OTMException;
import xml.JaxbLoader;

import java.io.*;
import java.util.*;

public class Project {

    private Map<String, FreewayScenario> scenarios = new HashMap<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Project(String scn_name,String scn_description,String sgmt_name,ParametersFreeway params){
        FreewayScenario fwyscn = new FreewayScenario(scn_name,scn_description,sgmt_name,params);
        scenarios.put(scn_name,fwyscn);
    }

    public Project(jaxb.Prj project,String folder,boolean validate) throws Exception {

        if(project.getScns()==null)
            return;

        try {

            for(jaxb.Scn jaxb_scn : project.getScns().getScn()){

                boolean has_file = jaxb_scn.getFile()!=null;
                boolean has_scenario = jaxb_scn.getScenario()!=null;

                // error conditions
                if(!(has_file ^ has_scenario))
                    throw new Exception("Error code: lsdgfj_TKK");

                String scn_name = jaxb_scn.getName();
                if(scenarios.containsKey(scn_name))
                    throw new Exception("Repeated scenario name in project file.");

                // load the scenario from a file
                jaxb.Scenario jaxb_scenario = null;
                if(has_file){
                    String scn_file = (new File(folder,jaxb_scn.getFile())).getAbsolutePath();
                    jaxb_scenario = JaxbLoader.load_scenario(scn_file,validate);
                }

                if(has_scenario)
                	jaxb_scenario = jaxb_scn.getScenario();

                // build the FreewayScenario and store
                scenarios.put(scn_name,new FreewayScenario(
                        scn_name,
                        jaxb_scn.getDescription(),
                        jaxb_scn.getSim(),
                        jaxb_scn.getLnks(),
                        jaxb_scn.getSgmts(),
                        jaxb_scn.getRoutes(),
                        jaxb_scenario ));

            }

        } catch (OTMException e) {
            throw new Exception(e.getMessage());
        }

    }

    /////////////////////////////////////
    // run
    /////////////////////////////////////

    public void run_all_scenarios() throws Exception {
        for(FreewayScenario scenario : scenarios.values())
            scenario.run_on_new_thread();
    }

    public void run_scenario(String scenario_name) throws Exception {
        if(!scenarios.containsKey(scenario_name))
            throw new Exception("Unknown scenario");
        scenarios.get(scenario_name).run_on_new_thread();
    }

    /////////////////////////////////////
    // getters / setters
    /////////////////////////////////////

    /**
     * Get all names of scenarios in the project
     * @return collection of string names
     */
    public Collection<String> get_scenario_names(){
        return scenarios.keySet();
    }

    /**
     * Get collection of scenarios in the project
     * @return Collection of FreewayScenario objects
     */
    public Collection<FreewayScenario> get_scenarios(){
        Set<FreewayScenario> x = new HashSet<>();
        x.addAll(scenarios.values());
        return x;
    }

    /**
     * Get scenario by name
     * @param name
     * @return FreewayScenario object if the name is valid. null otherwise.
     */
    public FreewayScenario get_scenario_with_name(String name){
        return scenarios.containsKey(name) ? scenarios.get(name) : null;
    }

    /**
     * Change the name of a scenario
     * @param oldname Current name of the scenario
     * @param newname New name of the scenario
     * @throws Exception
     */
    public void set_scenario_name(String oldname, String newname) throws Exception {
        if (!scenarios.containsKey(oldname))
            throw new Exception("Sceanrio name not found.");
        FreewayScenario scenario = scenarios.remove(oldname);
        scenario.name = newname;
        scenarios.put(newname, scenario);
    }

    /////////////////////////////////////
    // create scenarios
    /////////////////////////////////////

    /**
     * Create an empty scenario
     * @param name Name for the scenario
     * @throws Exception
     */
    public void create_scenario(String name) throws Exception {
        if( scenarios.containsKey(name))
            throw new Exception("The project already has a scenario by this name.");
        scenarios.put(name,new FreewayScenario(name,"",null,null,null,null,new jaxb.Scenario()));
    }

    /**
     * Clone an existing scenario.
     * @param from_name Name of the existing scenario
     * @param to_name Name of the clone
     * @throws Exception
     */
    public void clone_scenario(String from_name,String to_name) throws Exception {

        if(!scenarios.containsKey(from_name))
            throw new Exception("The project does not have a scenario by this name.");

        if(scenarios.containsKey(to_name))
            throw new Exception("The project already has a scenario by this name.");

        scenarios.put( to_name , scenarios.get(from_name).clone() );
    }

    /////////////////////////////////////
    // protected and private
    /////////////////////////////////////

    protected jaxb.Prj to_jaxb() throws Exception {
        jaxb.Prj jaxbPrj = new jaxb.Prj();
        jaxb.Scns jaxbScns = new jaxb.Scns();
        jaxbPrj.setScns(jaxbScns);

        List<jaxb.Scn> scnlist = jaxbScns.getScn();
        for(FreewayScenario fwy_scenario: scenarios.values()) {
            jaxb.Scn jScn = fwy_scenario.to_jaxb();
            scnlist.add(jScn);
        }
        return jaxbPrj;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public String toString() {
        String str = "";
        for(FreewayScenario scenario : scenarios.values()){
            str = str.concat("Scenario: " + scenario.name + " ------------\n");
            for(Segment segment : scenario.segments.values()){
                str = str.concat("segment " + segment.id + "..........\n");
                str = str.concat(segment.toString()+"\n");
            }
        }
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return scenarios.equals(project.scenarios);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scenarios);
    }
}
