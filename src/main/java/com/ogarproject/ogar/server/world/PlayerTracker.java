/**
 * This file is part of Ogar.
 * <p>
 * Ogar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ogar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ogar.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ogarproject.ogar.server.world;

import com.google.common.collect.ImmutableList;
import com.ogarproject.ogar.api.Ogar;
import com.ogarproject.ogar.api.entity.Cell;
import com.ogarproject.ogar.server.OgarServer;
import com.ogarproject.ogar.server.entity.EntityImpl;
import com.ogarproject.ogar.server.net.PlayerConnection;
import com.ogarproject.ogar.server.net.packet.outbound.PacketOutUpdateNodes;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerTracker {

    private final PlayerImpl player;
    private final PlayerConnection conn;
    private final WorldImpl world;
    private final Set<Integer> visibleEntities = new HashSet<>();
    private final ArrayDeque<EntityImpl> removalQueue = new ArrayDeque<>();
    private double rangeX;
    private double rangeY;
    private double centerX;
    private double centerY;
    private double viewLeft;
    private double viewRight;
    private double viewTop;
    private double viewBottom;
    private long lastViewUpdateTick = 0L;

    public PlayerTracker(PlayerImpl player) {
        this.player = player;
        this.conn = player.getConnection();
        this.world = OgarServer.getInstance().getWorld();
    }

    public void remove(EntityImpl entity) {
        if (!removalQueue.contains(entity)) {
            removalQueue.add(entity);
        }
    }

    private void updateRange() {
        double totalSize = 1.0D;
        for (Cell cell : player.getCells()) {
            totalSize += cell.getPhysicalSize();
        }

        double factor = Math.pow(Math.min(64.0D / totalSize, 1), 0.4D);
        rangeX = world.getView().getBaseX() / factor;
        rangeY = world.getView().getBaseY() / factor;
    }

    private void updateCenter() {
        if (player.getCells().isEmpty()) {
            return;
        }

        int size = player.getCells().size();
        double x = 0;
        double y = 0;

        for (Cell cell : player.getCells()) {
            x += cell.getPosition().getX();
            y += cell.getPosition().getY();
        }

        this.centerX = x / size;
        this.centerY = y / size;
    }

    public void updateView() {
        updateRange();
        updateCenter();

        viewTop = centerY - rangeY;
        viewBottom = centerY + rangeY;
        viewLeft = centerX - rangeX;
        viewRight = centerX + rangeX;

        lastViewUpdateTick = world.getServer().getTick();
    }

    private List<Integer> calculateEntitiesInView() {
        return world
                .getEntities()
                .stream()
                .filter((e) -> e.getPosition().getY() <= viewBottom && e.getPosition().getY() >= viewTop && e.getPosition().getX() <= viewRight
                        && e.getPosition().getX() >= viewLeft).mapToInt((e) -> e.getID()).boxed().collect(Collectors.toList());
    }

    public List<Integer> getVisibleEntities() {
        return ImmutableList.copyOf(visibleEntities);
    }

    public void updateNodes() {
        //VALIDATE LIST
        for (Object eido : visibleEntities.toArray()) {
            int eid = (int) eido;
            if (Ogar.getWorld().getEntity(eid) != null) continue;
            visibleEntities.remove(eid);
        }
        // Process the removal queue
        Set<Integer> updates = new HashSet<>();
        Set<EntityImpl> removals = new HashSet<>();
        synchronized (removalQueue) {
            removals.addAll(removalQueue);
            removalQueue.clear();
        }

        // Update the view, if needed
        if (world.getServer().getTick() - lastViewUpdateTick >= 5) {
            updateView();

            // Get the new list of entities in view
            List<Integer> newVisible = calculateEntitiesInView();

            synchronized (visibleEntities) {
                // Destroy now-invisible entities
                for (Iterator<Integer> it = visibleEntities.iterator(); it.hasNext(); ) {
                    int id = it.next();
                    if (!newVisible.contains(id)) {
                        // Remove from player's screen
                        it.remove();
                        removals.add(world.getEntity(id));
                    }
                }

                // Add new entities to the client's screen
                for (int id : newVisible) {
                    if (!visibleEntities.contains(id)) {
                        visibleEntities.add(id);
                        updates.add(id);
                    }
                }
            }
        }

        // Update entities that need to be updated
        for (Iterator<Integer> it = visibleEntities.iterator(); it.hasNext(); ) {
            int id = it.next();
            EntityImpl entity = world.getEntity(id);
            if (entity == null) {
                // Prune invalid entity from the list
                it.remove();
                continue;
            }

            if (entity.shouldUpdate()) {
                updates.add(id);
            }
        }

        conn.sendPacket(new PacketOutUpdateNodes(world, removals, updates));
    }
}
