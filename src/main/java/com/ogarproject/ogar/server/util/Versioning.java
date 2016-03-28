package com.ogarproject.ogar.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.ogarproject.ogar.api.Ogar;

public final class Versioning {
    public static String getOgarVersion() {
        String result = "Unknown-Version";

        InputStream stream = Ogar.class.getClassLoader().getResourceAsStream("META-INF/maven/com.ogarproject/ogar/api/pom.properties");
        Properties properties = new Properties();

        if (stream != null) {
            try {
                properties.load(stream);

                result = properties.getProperty("version");
            } catch (IOException ex) {
                Logger.getLogger(Versioning.class.getName()).log(Level.SEVERE, "Could not get Ogar version!", ex);
            }
        }

        return result;
    }
}
