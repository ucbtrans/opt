package opt.data;

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
import java.util.HashMap;
import java.util.Map;

public class ProjectFactory {

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
    public static Project create_empty_project(String scn_name, String sgmt_name, ParametersFreeway link_params) {
        return new Project(scn_name,sgmt_name,link_params);
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

        // save project file
        create_marshaller(jaxb.Prj.class).marshal(project.to_jaxb(), file_info.get_project_file());
    }

    /////////////////////////////////////
    // class
    /////////////////////////////////////

    public static class FileInfo {
        String folder;
        String file_name;
        static String project_extension = "opt";
        static String scenario_extension = "xml";
        public FileInfo(String filePath){
            File f = new File(filePath);
            if (f.isDirectory())
                return;
            folder = f.getParent();
            String name = f.getName();
            final int lastPeriodPos = name.lastIndexOf('.');
            if (lastPeriodPos <= 0)
                file_name = name;
            else
                file_name = name.substring(0, lastPeriodPos);
        }
        public File get_project_file(){
            return new File(folder,file_name + "." + project_extension);
        }
        public File get_scenario_file(String scenario_name){
            // remove whitespace
            scenario_name.replaceAll("\\s+","");
            return new File(folder,file_name + "_" + scenario_name + "." + scenario_extension);
        }
    }

    /////////////////////////////////////
    // private statics
    /////////////////////////////////////

    private static Unmarshaller create_unmarshaller() throws JAXBException, SAXException {
        JAXBContext jaxbContext = JAXBContext.newInstance(jaxb.Prj.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//        InputStream resourceAsStream = ProjectFactory.class.getResourceAsStream("/opt.xsd");
//        Schema schema = sf.newSchema(new StreamSource(resourceAsStream));
//        unmarshaller.setSchema(schema);
        return unmarshaller;
    }

    private static Marshaller create_marshaller(Class clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller marshaller = jaxbContext.createMarshaller();

        //Required formatting??
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return marshaller;
    }

}
