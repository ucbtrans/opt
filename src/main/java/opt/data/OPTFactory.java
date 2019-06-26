package opt.data;

import jaxb.Prj;
import jaxb.Roadparam;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OPTFactory {

    /**
     * Load a project from an XML file.
     * @param filename Fully qualifies file name
     * @param validate Run scenario validation and print errors / warnings
     * @return Project object
     * @throws Exception
     */
    public static Project load_project(String filename,boolean validate) throws Exception {
        try {
            File project_file = new File(filename);
            return new Project((jaxb.Prj) create_unmarshaller().unmarshal(project_file),project_file.getParent(),validate);
        } catch(org.xml.sax.SAXException e){
            throw new Exception(e);
        }  catch (JAXBException e) {
            throw new Exception(e);
        }
    }

    /**
     * Create an empty project.
     * @return Project object
     */
    public static Project create_empty_project(){
        return new Project();
    }

    /**
     * Create a deep copy of a given scenario
     * @param scn_org FreewayScenario object
     * @return new FreewayScenario object
     */
    public static FreewayScenario deep_copy_scenario(FreewayScenario scn_org){
        FreewayScenario scn_cpy = new FreewayScenario();
        scn_cpy.jscenario = deep_copy_jscenario(scn_org.jscenario);
        scn_cpy.segments = new ArrayList<>();
        for(Segment sgm_org : scn_org.segments)
            scn_cpy.segments.add(deep_copy_segment(sgm_org,scn_cpy));
        scn_cpy.reset_max_ids();
        return scn_cpy;
    }

    /**
     * Save project to xml files
     * @param project
     * @param filePath Full path and name for the output file. (
     * @throws Exception
     */
    public static void save_project(Project project,String filePath) throws Exception {

        // parse the filePath
        FileInfo file_info = new FileInfo(filePath);

        // save each of the scenarios
        Map<String,String> scenario_file_names = new HashMap<>();
        for(String scenario_name : project.get_scenario_names()){
            FreewayScenario scenario = project.get_scenario_with_name(scenario_name);
            File scenario_file = file_info.get_scenario_file(scenario_name);
            scenario_file_names.put(scenario_name,scenario_file.getName());
            create_marshaller(jaxb.Scenario.class).marshal(scenario.to_jaxb(), scenario_file);
        }

        // save project file
        create_marshaller(jaxb.Prj.class).marshal(project.to_jaxb(scenario_file_names), file_info.get_project_file());

    }


    /////////////////////////////////////
    // private
    /////////////////////////////////////

    private static Unmarshaller create_unmarshaller() throws JAXBException, SAXException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Prj.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream resourceAsStream = OPTFactory.class.getResourceAsStream("/opt.xsd");
        Schema schema = sf.newSchema(new StreamSource(resourceAsStream));
        unmarshaller.setSchema(schema);
        return unmarshaller;
    }

    private static Marshaller create_marshaller(Class clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller marshaller = jaxbContext.createMarshaller();

        //Required formatting??
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return marshaller;
    }

    private static jScenario deep_copy_jscenario(jScenario jscn_org) {
        jScenario jscn_cpy = new jScenario();

        // create new nodes
        for (Map.Entry<Long, jNode> e : jscn_org.nodes.entrySet())
            jscn_cpy.nodes.put(e.getKey(), new jNode(e.getKey()));

        // create new links
        for (Map.Entry<Long, jLink> e : jscn_org.links.entrySet()) {
            long link_id = e.getKey();
            jLink link_org = e.getValue();

            jLink new_link = new jLink(
                    link_id,
                    link_org.start_node_id,
                    link_org.end_node_id,
                    link_org.full_lanes,
                    link_org.length,
                    link_org.is_mainline,
                    link_org.is_ramp,
                    link_org.is_source,
                    link_org.capacity_vphpl,
                    link_org.jam_density_vpkpl,
                    link_org.ff_speed_kph);

            jscn_cpy.links.put(link_id,new_link);
        }

        // set node inlinks and outlinks
        for (jNode node_cpy : jscn_cpy.nodes.values()){
            jNode node_org = jscn_org.nodes.get(node_cpy.id);
            for(jLink link_org : node_org.out_links)
                node_cpy.out_links.add(jscn_cpy.links.get(link_org.id));
            for(jLink link_org : node_org.in_links)
                node_cpy.in_links.add(jscn_cpy.links.get(link_org.id));
        }

//        // set road parameters
//        for(Map.Entry<Long,jaxb.Roadparam> e : jscn_org.road_params.entrySet()) {
//            long rp_id = e.getKey();
//            jaxb.Roadparam rp = e.getValue();
//            jaxb.Roadparam rp_cpy = new Roadparam();
//            rp_cpy.setId(rp_id);
//            rp_cpy.setName(rp.getName());
//            rp_cpy.setCapacity(rp.getCapacity());
//            rp_cpy.setJamDensity(rp.getJamDensity());
//            rp_cpy.setSpeed(rp.getSpeed());
//            jscn_cpy.road_params.put(rp_id, rp_cpy);
//        }

        return jscn_cpy;
    }

    private static Segment deep_copy_segment(Segment seg_org,FreewayScenario scenario){
        Segment seg_cpy = new Segment();
        seg_cpy.fwy_scenario = scenario;
        seg_cpy.ml = scenario.jscenario.links.get(seg_org.ml.id);
        seg_cpy.or = seg_org.or==null ? null : scenario.jscenario.links.get(seg_org.or.id);
        seg_cpy.fr = seg_org.fr==null ? null : scenario.jscenario.links.get(seg_org.fr.id);
        return seg_cpy;
    }

    public static class FileInfo {
        String folder;
        String file_name;
        String extension;
        public FileInfo(String filePath){
            File f = new File(filePath);
            if (f.isDirectory())
                return;
            folder = f.getParent();
            String name = f.getName();
            final int lastPeriodPos = name.lastIndexOf('.');
            if (lastPeriodPos <= 0) {
                file_name = name;
                extension = "xml";
            }
            else
            {
                file_name = name.substring(0, lastPeriodPos);
                extension = name.substring(lastPeriodPos+1);
            }
        }
        public File get_project_file(){
            return new File(folder,file_name + "." + extension);
        }
        public File get_scenario_file(String scenario_name){
            // remove whitespace
            scenario_name.replaceAll("\\s+","");
            return new File(folder,file_name + "_" + scenario_name + "." + extension);
        }
    }

}
