package opt.utils;

import utils.OTMUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Version {


    public static String getOTMBaseGitHash(){
        InputStream inputStream = OTMUtils.class.getResourceAsStream("/otm-base.properties");
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
        return properties.getProperty("base.git");
    }

    public static String getOTMSimGitHash(){
        InputStream inputStream = runner.OTM.class.getResourceAsStream("/otm-sim.properties");
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
        InputStream inputStream = runner.OTM.class.getResourceAsStream("/opt.properties");
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
