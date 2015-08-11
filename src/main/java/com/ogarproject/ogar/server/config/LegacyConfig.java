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
package com.ogarproject.ogar.server.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LegacyConfig {

    private final Properties props;

    public LegacyConfig(File file) throws IOException {
        props = new Properties();
        props.load(new FileReader(file));
    }

    public OgarConfig convert() {
        OgarConfig cfg = new OgarConfig();

        cfg.server.port = Integer.parseInt(props.getProperty("serverPort"));
        cfg.server.maxConnections = Integer.parseInt(props.getProperty("serverMaxConnections"));

        cfg.world.view.baseX = Integer.parseInt(props.getProperty("serverViewBaseX"));
        cfg.world.view.baseY = Integer.parseInt(props.getProperty("serverViewBaseY"));

        cfg.world.border.left = Double.parseDouble(props.getProperty("borderLeft"));
        cfg.world.border.right = Double.parseDouble(props.getProperty("borderRight"));
        cfg.world.border.top = Double.parseDouble(props.getProperty("borderTop"));
        cfg.world.border.bottom = Double.parseDouble(props.getProperty("borderBottom"));

        cfg.player.startMass = Integer.parseInt(props.getProperty("playerStartMass"));
        cfg.player.maxMass = Integer.parseInt(props.getProperty("playerMaxMass"));
        cfg.player.minMassEject = Integer.parseInt(props.getProperty("playerMinMassEject"));
        cfg.player.minMassSplit = Integer.parseInt(props.getProperty("playerMinMassSplit"));
        cfg.player.maxCells = Integer.parseInt(props.getProperty("playerMaxCells"));
        cfg.player.recombineTime = Integer.parseInt(props.getProperty("playerRecombineTime")) * 20;
        cfg.player.massDecayRate = Double.parseDouble(props.getProperty("playerMassDecayRate")) / 20.0D;
        cfg.player.minMassToDecay = Integer.parseInt(props.getProperty("playerMinMassDecay"));
        cfg.player.maxNickLength = Integer.parseInt(props.getProperty("playerMaxNickLength"));

        return cfg;
    }
}
