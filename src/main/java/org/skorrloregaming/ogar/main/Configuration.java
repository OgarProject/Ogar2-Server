package org.skorrloregaming.ogar.main;

import java.io.FileReader;
import java.util.Properties;

import com.ogarproject.ogar.server.config.OgarConfig;

import jline.internal.Log;

public class Configuration {

    public static OgarConfig load(String file){
        try(FileReader reader = new FileReader(file)){
            Properties properties = new Properties();
            properties.load(reader);
            OgarConfig conf = new OgarConfig();
            conf.server.port = Integer.parseInt(properties.getProperty("port"));
            int borderSize = Integer.parseInt(properties.getProperty("borderSize"));
            conf.world.border.right = borderSize;
            conf.world.border.bottom = borderSize;
            conf.server.ip = properties.getProperty("ip");
            conf.player.maxCells = Integer.parseInt(properties.getProperty("maxCells"));
            conf.player.maxMass = Integer.parseInt(properties.getProperty("maxMass"));
            conf.player.startMass = Integer.parseInt(properties.getProperty("startMass"));
            conf.player.recombineTime = Integer.parseInt(properties.getProperty("recombineTime"));
            conf.server.maxConnections = Integer.parseInt(properties.getProperty("maxPlayers"));
            return conf;
        }catch (Exception ex){
            Log.info("An internal error has occured whilist reading configuration file!");
            return new OgarConfig();
        }
    }
    
}
