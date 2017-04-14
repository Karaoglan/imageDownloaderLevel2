package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 * Created by bk on 11/04/2017.
 */
public class PropertyReader {

    InputStream inputStream;

    public String getProperty(String property) throws IOException {
        String propertyResult = "";
        Properties prop = new Properties();

        try {
             String propFileName = "socket.properties";
             inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }

        // get the property value and print it out
        return prop.getProperty(property);
    }
}
