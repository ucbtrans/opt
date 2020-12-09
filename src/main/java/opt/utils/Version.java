package opt.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Version {


    public static String getOTMSimGitHash(){
        InputStream inputStream = cmd.OTM.class.getResourceAsStream("/otm-sim.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read properties file", e);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
        return properties.getProperty("sim.git");
    }

    public static String getOPTGitHash(){
        InputStream inputStream = cmd.OTM.class.getResourceAsStream("/opt.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read properties file", e);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
        return properties.getProperty("opt.git");
    }

}
