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
package com.ogarproject.ogar.server.tick;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A TickWorker is a worker that processes {@link Tickable} objects.
 */
public class TickWorker implements Runnable {

    private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
    private final int workerId;
    private final Thread thread;
    private final Queue<Tickable> queue = new ConcurrentLinkedQueue<>();
    private boolean running = false;

    public TickWorker() {
        this.workerId = NEXT_WORKER_ID.getAndIncrement();
        this.thread = new Thread(this, "Tick Worker - #" + workerId);
    }

    public void start() {
        if (!running) {
            running = true;
            thread.start();
        }
    }

    public void shutdownGracefully() {
        if (running) {
            running = false;
        }
    }

    public void shutdown() {
        if (running) {
            running = false;
            thread.interrupt();
        }
    }

    public boolean waitForShutdown() {
        try {
            if (running) {
                shutdownGracefully();
            }

            thread.join();
            return true;
        } catch (InterruptedException ex) {
            return false;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public int getWorkerId() {
        return workerId;
    }

    public int getObjectsRemaining() {
        return queue.size();
    }

    public void tick(Tickable... tickables) {
        for (Tickable t : tickables) {
            queue.add(t);
        }
    }

    public void tick(Collection<Tickable> tickables) {
        queue.addAll(tickables);
    }

    public void waitForCompletion() {
        while (!queue.isEmpty()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                // Whoops
            }
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                while (!queue.isEmpty()) {
                    queue.poll().tick();
                }

                Thread.sleep(1);
            }
        } catch (InterruptedException ex) {
            return;
        }
    }

}
