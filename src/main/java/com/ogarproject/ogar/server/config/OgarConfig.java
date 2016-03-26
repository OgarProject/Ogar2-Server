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

public class OgarConfig {

    public Server server = new Server();
    public World world = new World();
    public Player player = new Player();

    public static class Server {

        public int port = 443;
        public int maxConnections = 100;
        public String ip = "localhost";
        
    }

    public static class World {

        public View view = new View();
        public Border border = new Border();
        public Food food = new Food();
        public Virus virus = new Virus();

        public static class View {

            public double baseX = 1024;
            public double baseY = 592;
        }

        public static class Border {

            public double left = 0;
            public double right = 6000;
            public double top = 0;
            public double bottom = 6000;
        }

        public static class Food {

            public int spawnInterval = 20; // In ticks
            public int spawnPerInterval = 10; // How many food to spawn per interval
            public int startAmount = 100; // The amount of food to start the world with
            public int maxAmount = 500; // The maximum amount of food in the world at once
            public int foodSize = 1; // The size of food spawned on the map
            
        }
        
        public static class Virus {
            public int virusSize = 150; // The size of a virus spawned on the map
            public int startAmount = 50; // The amount of viruses to start the world with
        }
        
    }

    public static class Player {

        public int startMass = 35;
        public int maxMass = 22500;
        public int minMassEject = 32;
        public int minMassSplit = 36;
        public int maxCells = 16;
        public int recombineTime = 30 * 20; // In ticks
        public double massDecayRate = 0.0001D; // Mass lost per tick
        public int minMassToDecay = 9;
        public int maxNickLength = 15;
    }
}
