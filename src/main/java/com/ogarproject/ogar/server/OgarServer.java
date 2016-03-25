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
package com.ogarproject.ogar.server;

import com.ogarproject.ogar.server.world.WorldImpl;
import com.ogarproject.ogar.api.Ogar;
import com.ogarproject.ogar.api.Server;
import com.ogarproject.ogar.api.plugin.Messenger;
import com.ogarproject.ogar.api.plugin.PluginManager;
import com.ogarproject.ogar.api.plugin.Scheduler;
import com.ogarproject.ogar.server.config.OgarConfig;
import com.ogarproject.ogar.server.config.JsonConfiguration;
import com.ogarproject.ogar.server.config.LegacyConfig;
import com.ogarproject.ogar.server.gui.ServerCLI;
import com.ogarproject.ogar.server.gui.ServerGUI;
import com.ogarproject.ogar.server.net.NetworkManager;
import com.ogarproject.ogar.server.plugin.SimpleScheduler;
import com.ogarproject.ogar.server.tick.TickWorker;
import com.ogarproject.ogar.server.tick.Tickable;
import com.ogarproject.ogar.server.tick.TickableSupplier;
import com.ogarproject.ogar.server.world.PlayerImpl;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.skorrloregaming.ogar.main.Commands;
import org.skorrloregaming.ogar.text.ChatColor;

public class OgarServer implements Server {

    private static OgarServer instance;
    public static final Logger log = Logger.getGlobal();
    private final PlayerList playerList = new PlayerList(this);
    private final File configurationFile = new File("config.json");
    private final boolean debugMode = Boolean.getBoolean("debug");
    private final Set<TickWorker> tickWorkers = new HashSet<>();
    private final Messenger messenger = new Messenger();
    private final SimpleScheduler scheduler = new SimpleScheduler(this);
    private int tickThreads = Integer.getInteger("tickThreads", 1);
    private NetworkManager networkManager;
    private PluginManager pluginManager;
    private WorldImpl world;
    private OgarConfig configuration;
    private long tick = 0L;
    private boolean running;

    public static void main(String[] args) throws Throwable {
        OgarServer.instance = new OgarServer();
        OgarServer.instance.run();
    }

    public static OgarServer getInstance() {
        return instance;
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public PlayerList getPlayerList() {
        return playerList;
    }

    @Override
    public WorldImpl getWorld() {
        return world;
    }

    @Override
    public Messenger getMessenger() {
        return messenger;
    }
    
    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    public boolean isDebugging() {
        return debugMode;
    }

    private void convertLegacyConfig() {
        File file = new File("gameserver.ini");
        if (file.isFile()) {
            log.info("I see you have a legacy configuration file from Ogar version 1 (gameserver.ini). I'll go ahead and convert that for you.");
            try {
                LegacyConfig legacy = new LegacyConfig(file);
                this.configuration = legacy.convert();
                saveConfig();
                file.renameTo(new File("gameserver.ini.converted"));
                log.info("Successfully converted Ogar version 1 configuration!");
                log.info("Your old configuration was renamed to \"gameserver.ini.converted\".");
                log.info("Do not rename this back to \"gameserver.ini\", or your Ogar 2 configuration will be overwritten!");
            } catch (IOException ex) {
                log.log(Level.WARNING, "Legacy configuration conversion failed.", ex);
            }
        }
    }

    private void setupLogging() {
        log.setUseParentHandlers(false);

        LogFormatter formatter = new LogFormatter();

        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(formatter);
        if (isDebugging()) {
            log.setLevel(Level.FINEST);
            ch.setLevel(Level.FINEST);
        } else {
            log.setLevel(Level.INFO);
            ch.setLevel(Level.INFO);
        }
        log.addHandler(ch);

        try {
            FileHandler fh = new FileHandler("server.log");
            fh.setFormatter(formatter);
            if (isDebugging()) {
                fh.setLevel(Level.FINEST);
            } else {
                ch.setLevel(Level.INFO);
            }
            log.addHandler(fh);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Error while adding FileHandler to logger. Logs will not be output to a file.", ex);
        }

    }

    public void loadConfig() {
        this.configuration = JsonConfiguration.load(configurationFile, OgarConfig.class);
        log.info("Loaded configuration from " + configurationFile + ".");
    }

    public void saveConfig() {
        if (configuration == null) {
            configuration = new OgarConfig();
        }

        configuration.save(configurationFile);
        log.info("Saved configuration to " + configurationFile + ".");
    }

    public OgarConfig getConfig() {
        return configuration;
    }

    public long getTick() {
        return tick;
    }

    private void run() {
        if (ServerGUI.isHeadless()) {
            Thread thread = new Thread(new ServerCLI(this), "Console Command Handler");
            thread.setDaemon(true);
            thread.start();
        } else {
            ServerGUI.spawn(this);
        }

        Ogar.setServer(this);
        pluginManager = new PluginManager(this);

        setupLogging();
        log.info("SkorrloreGaming-Productions - Modified Ogar 2 starting."+ChatColor.RESET);
        if (debugMode) {
            log.info("Debug mode is enabled; additional information will be logged.");
        }

        // Create the tick workers
        if (tickThreads < 1) {
            tickThreads = 1;
        }
        if (tickThreads > 1) {
            log.warning("Use of multiple tick threads is experimental and may be unstable!");
        }

        for (int i = 0; i < tickThreads; i++) {
            tickWorkers.add(new TickWorker());
        }

        convertLegacyConfig();
        if (!configurationFile.isFile()) {
            saveConfig();
        }
        loadConfig();

        world = new WorldImpl(this);

        log.info("Loading plugins.");
        try {
            File pluginDirectory = new File("plugins");
            if (!pluginDirectory.exists()) {
                pluginDirectory.mkdirs();
            }
            pluginManager.loadPlugins(pluginDirectory);
        } catch (Throwable t) {
            log.log(Level.SEVERE, "Failed to load plugins", t);
        }

        log.info("Enabling plugins.");
        pluginManager.enablePlugins();

        networkManager = new NetworkManager(this);
        try {
            networkManager.start();
        } catch (IOException | InterruptedException ex) {
            log.info("Failed to start server! "+ex.getMessage());
            if (ServerGUI.isSpawned()) {
            	System.exit(1);
            } else {
                System.exit(1);
            }
        }

        // Start the tick workers
        tickWorkers.forEach(TickWorker::start);

        running = true;
        while (running) {
            try {
                // To make the tick loop adaptive, we measure the start and end times.
                // This allows us to ensure that there is around 20 ticks per second.
                long startTime = System.currentTimeMillis();
                tick++;

                // Tick the world
                world.tick(this::tick);

                // Update nodes
                for (PlayerImpl player : playerList.getAllPlayers()) {
                    tick(player.getTracker()::updateNodes);
                }

                // Wait for the tick workers to finish
                tickWorkers.forEach(TickWorker::waitForCompletion);
                
                // Tick the plugin scheduler
                scheduler.serverTick(tick);

                long tickDuration = System.currentTimeMillis() - startTime;
                if (tickDuration < 50) {
                    // We can sleep for at least 1ms
                    log.finer("Tick took " + tickDuration + "ms, sleeping for a bit");
                    Thread.sleep(50 - tickDuration);
                } else {
                    // No sleep allowed, move on to the next tick
                    log.finer("Tick took " + tickDuration + "ms (which is >=50ms), no time for sleep");
                }
            } catch (InterruptedException ex) {
                break;
            }
        }

        // Shut down tick workers
        // We initiate all shutdowns before waiting on them to reduce shutdown time
        tickWorkers.forEach(TickWorker::shutdownGracefully);
        tickWorkers.forEach(TickWorker::waitForShutdown);

        // Shut down network manager
        networkManager.shutdown();

        // Disable plugins
        log.info("Disabling plugins...");
        pluginManager.disablePlugins();

        log.info("Successfully stopped server!");
        try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.exit(-1);
    }

    public void handleCommand(String s) {
        s = s.trim();
        if (s.isEmpty()) {
            return;
        }
        Commands.onCommand(s);
    }

    public void shutdown() {
        running = false;
    }

    private void tick(Tickable... tickables) {
        for (Tickable t : tickables) {
            TickWorker bestWorker = null;
            for (TickWorker w : tickWorkers) {
                if (bestWorker == null) {
                    bestWorker = w;
                    continue;
                }

                if (w.getObjectsRemaining() < bestWorker.getObjectsRemaining()) {
                    bestWorker = w;
                }
            }

            bestWorker.tick(t);
        }
    }

    private void tick(Supplier... suppliers) {
        for (Supplier s : suppliers) {
            tick(new TickableSupplier(s));
        }
    }

    private static class LogFormatter extends Formatter {

        private static final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder(df.format(new Date(record.getMillis())));
            sb.append(" [");
            sb.append(record.getLevel());
            sb.append("] ");
            sb.append(formatMessage(record));
            sb.append('\n');
            return sb.toString();
        }
    }
}
