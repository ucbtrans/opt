package opt.data;

import jaxb.Prj;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.InputStream;

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

}
