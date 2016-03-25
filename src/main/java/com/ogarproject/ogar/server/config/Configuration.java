package com.ogarproject.ogar.server.config;
/**
 * This file is part of Ogar.
 *
 * Ogar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ogar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ogar.  If not, see <http://www.gnu.org/licenses/>.
 */


import java.io.FileReader;
import java.util.Properties;

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
