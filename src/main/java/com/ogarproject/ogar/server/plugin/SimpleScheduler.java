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
package com.ogarproject.ogar.server.plugin;

import com.google.common.base.Preconditions;
import com.ogarproject.ogar.api.plugin.Plugin;
import com.ogarproject.ogar.api.plugin.Scheduler;
import com.ogarproject.ogar.server.OgarServer;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class SimpleScheduler implements Scheduler {

    private final OgarServer server;
    private final AtomicInteger nextTaskId = new AtomicInteger(1);
    private final Map<Integer, RegisteredTask> tasks = new ConcurrentHashMap<>();
    private final Executor asyncExecutor = Executors.newCachedThreadPool();

    public SimpleScheduler(OgarServer server) {
        this.server = server;
    }

    @Override
    public int runTaskLater(Plugin plugin, Runnable task, long delay) {
        checkConditions(plugin, task, delay);
        int id = nextTaskId.incrementAndGet();
        tasks.put(id, new RegisteredTask(id, plugin, task, delay, 0L, false, false, server.getTick()));
        return id;
    }

    @Override
    public int runAsyncTaskLater(Plugin plugin, Runnable task, long delay) {
        checkConditions(plugin, task, delay);
        int id = nextTaskId.incrementAndGet();
        tasks.put(id, new RegisteredTask(id, plugin, task, delay, 0L, false, true, server.getTick()));
        return id;
    }

    @Override
    public int runTaskTimer(Plugin plugin, Runnable task, long delay, long interval) {
        checkConditions(plugin, task, delay);
        Preconditions.checkArgument(interval > 0L, "Interval must be greater than zero");
        int id = nextTaskId.incrementAndGet();
        tasks.put(id, new RegisteredTask(id, plugin, task, delay, interval, true, false, server.getTick()));
        return id;
    }

    @Override
    public int runAsyncTaskTimer(Plugin plugin, Runnable task, long delay, long interval) {
        checkConditions(plugin, task, delay);
        Preconditions.checkArgument(interval > 0L, "Interval must be greater than zero");
        int id = nextTaskId.incrementAndGet();
        tasks.put(id, new RegisteredTask(id, plugin, task, delay, interval, true, true, server.getTick()));
        return id;
    }

    @Override
    public boolean cancelTask(int id) {
        return tasks.remove(id) != null;
    }
    
    public void serverTick(long currentTick) {
        for (Iterator<RegisteredTask> it = tasks.values().iterator(); it.hasNext();) {
            RegisteredTask rt = it.next();
            if (rt.tick(currentTick)) {
                it.remove();
            }
        }
    }

    private void checkConditions(Plugin plugin, Runnable task, long delay) {
        Preconditions.checkNotNull(plugin, "Plugin must not be null");
        Preconditions.checkState(plugin.isEnabled(), "Plugin must be enabled");
        Preconditions.checkNotNull(task, "Task must not be null");
        Preconditions.checkArgument(delay > 0L, "Delay must be greater than zero");
    }

    private class RegisteredTask {

        private final int id;
        private final Plugin plugin;
        private final Runnable task;
        private final long delay;
        private final long interval;
        private final boolean repeating;
        private final boolean async;
        private long nextTick = 0L;

        public RegisteredTask(int id, Plugin plugin, Runnable task, long delay, long interval, boolean repeating, boolean async, long currentTick) {
            this.id = id;
            this.plugin = plugin;
            this.task = task;
            this.delay = delay;
            this.interval = interval;
            this.repeating = repeating;
            this.async = async;
            this.nextTick = currentTick + delay;
        }

        public int getID() {
            return id;
        }

        public Plugin getPlugin() {
            return plugin;
        }

        public Runnable getTask() {
            return task;
        }

        public long getDelay() {
            return delay;
        }

        public long getInterval() {
            return interval;
        }

        public boolean isRepeating() {
            return repeating;
        }

        public boolean isAsync() {
            return async;
        }

        /**+
         * @return true if this task should be removed from the task list
         */
        public boolean tick(long currentTick) {
            if (currentTick >= nextTick) {
                // Task should be run
                if (async) {
                    SimpleScheduler.this.asyncExecutor.execute(() -> {
                        try {
                            task.run();
                        } catch (Exception ex) {
                            server.getLogger().log(Level.SEVERE, "Error while executing task ID #" + id + " for plugin " + plugin.getPluginInfo().name(), ex);
                        }
                    });
                } else {
                    try {
                        task.run();
                    } catch (Exception ex) {
                        server.getLogger().log(Level.SEVERE, "Error while executing task ID #" + id + " for plugin " + plugin.getPluginInfo().name(), ex);
                    }
                }

                if (repeating) {
                    // Reschedule the task
                    nextTick = currentTick + interval;
                    return false;
                } else {
                    return true;
                }
            }
            
            return false;
        }
    }
}
