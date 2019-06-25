package opt.data;

import error.OTMException;
import runner.ScenarioFactory;
import xml.JaxbLoader;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Project {

    private Map<String, FreewayScenario> scenarios = new HashMap<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public Project(){}

    public Project(jaxb.Prj project,String folder,boolean validate) throws Exception {

        if(project.getScns()==null)
            return;

        try {

            for(jaxb.Scn jaxb_scn : project.getScns().getScn()){

                String scn_file = (new File(folder,jaxb_scn.getFile())).getAbsolutePath();
                String scn_name = jaxb_scn.getName();

                if(scenarios.containsKey(scn_name))
                    throw new Exception("Repeated scenario name in project file.");

                jaxb.Scenario jaxb_scenario = JaxbLoader.load_scenario(scn_file,validate);
                runner.Scenario scenario =  ScenarioFactory.create_scenario(jaxb_scenario,validate);
                scenarios.put(scn_name,new FreewayScenario(scenario));
            }

        } catch (OTMException e) {
            throw new Exception(e.getMessage());
        }

    }


    /////////////////////////////////////
    // getters / setters
    /////////////////////////////////////

    public Collection<String> get_scenario_names(){
        return scenarios.keySet();
    }

    public FreewayScenario get_scenario_with_name(String name){
        return scenarios.containsKey(name) ? scenarios.get(name) : null;
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
        scenarios.put(name,new FreewayScenario(new runner.Scenario()));
    }

    /**
     * Clone an existing scenario.
     * @param from_name Name of the existing scenario
     * @param to_name Name of the clone
     * @throws Exception
     */
    public void clone_scenario(String from_name,String to_name) throws Exception {

        throw new Exception("NOT IMPLEMENTED YET.");

//        if(!scenarios.containsKey(from_name))
//            throw new Exception("The project does not have a scenario by this name.");
//
//        if(scenarios.containsKey(to_name))
//            throw new Exception("The project already has a scenario by this name.");

        // TODO : GG Implement deep_copy
//        scenarios.put( to_name , ScenarioFactory.deep_copy(scenarios.get(from_name)) );
    }
}
