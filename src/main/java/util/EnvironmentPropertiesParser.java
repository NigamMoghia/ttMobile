package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Created by rgupta on 8/21/2017.
 */
public class EnvironmentPropertiesParser {
    private FileInputStream stream;
    private Properties propertyFile = new Properties();

    public EnvironmentPropertiesParser() throws IOException
    {
        File file = new File("src\\main\\resources\\Connection.properties");
        //stream = new FileInputStream("C:\\Temp\\Repo\\export-Test\\src\\main\\resources\\Connection.properties");
        stream = new FileInputStream(file.getAbsolutePath());
        propertyFile.load(stream);
    }

    public String getValue(String key) {


        String locatorProperty = propertyFile.getProperty(key);
        //System.out.println(locatorProperty.toString());
        String value = locatorProperty.split(" = ")[0];

        String returnValue = null;
        if (key.equalsIgnoreCase("QA_DBDSNName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("QA_DBName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("QA_DBUserName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("QA_DBPassword")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("QA_DBPort")) {
            returnValue = value;
        }

        if (key.equalsIgnoreCase("DEV_DBDSNName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("DEV_DBName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("DEV_DBUserName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("DEV_DBPassword")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("DEV_DBPort")) {
            returnValue = value;
        }

        if (key.equalsIgnoreCase("STAGING_DBDSNName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("STAGING_DBName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("STAGING_DBUserName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("STAGING_DBPassword")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("STAGING_DBPort")) {
            returnValue = value;
        }

        if (key.equalsIgnoreCase("PRODUCTION_DBDSNName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("PRODUCTION_DBName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("PRODUCTION_DBUserName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("PRODUCTION_DBPassword")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("PRODUCTION_DBPort")) {
            returnValue = value;
        }

        if (key.equalsIgnoreCase("MySqlDBDSNName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("MySqlDBName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("MySqlDBUserName")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("MySqlDBPassword")) {
            returnValue = value;
        } else if (key.equalsIgnoreCase("MySqlDBPort")) {
            returnValue = value;
        }

        return returnValue;

    }

}
