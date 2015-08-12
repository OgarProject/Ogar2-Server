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
package com.ogarproject.ogar.server.world;

import com.ogarproject.ogar.api.world.Position;
import com.google.common.collect.ImmutableList;
import com.ogarproject.ogar.api.entity.Entity;
import com.ogarproject.ogar.api.entity.EntityType;
import com.ogarproject.ogar.api.world.World;
import com.ogarproject.ogar.server.OgarServer;
import com.ogarproject.ogar.server.config.OgarConfig;
import com.ogarproject.ogar.server.entity.EntityImpl;
import com.ogarproject.ogar.server.entity.impl.CellEntityImpl;
import com.ogarproject.ogar.server.entity.impl.FoodEntityImpl;
import com.ogarproject.ogar.server.entity.impl.MassEntityImpl;
import com.ogarproject.ogar.server.entity.impl.VirusEntityImpl;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class WorldImpl implements World {

    private static final Random random = new Random();
    private final OgarServer server;
    private final TIntObjectMap<EntityImpl> entities = new TIntObjectHashMap<>();
    private final Border border;
    private final View view;

    public WorldImpl(OgarServer server) {
        this.server = server;
        this.border = new Border(server.getConfig());
        this.view = new View(server.getConfig());
    }

    public EntityImpl spawnEntity(EntityType type) {
        return spawnEntity(type, getRandomPosition());
    }

    public EntityImpl spawnEntity(EntityType type, Position position) {
        return spawnEntity(type, position, null);
    }

    public CellEntityImpl spawnPlayerCell(PlayerImpl player) {
        return spawnPlayerCell(player, getRandomPosition());
    }

    public CellEntityImpl spawnPlayerCell(PlayerImpl player, Position position) {
        return (CellEntityImpl) spawnEntity(EntityType.CELL, position, player);
    }

    private EntityImpl spawnEntity(EntityType type, Position position, PlayerImpl owner) {
        if (type == null || position == null) {
            return null;
        }

        EntityImpl entity;
        switch (type) {
            case CELL:
                if (owner == null) {
                    throw new IllegalArgumentException("Cell entities must have an owner");
                }

                entity = new CellEntityImpl(owner, this, position);
                break;
            case FOOD:
                entity = new FoodEntityImpl(this, position);
                break;
            case MASS:
                entity = new MassEntityImpl(this, position);
                break;
            case VIRUS:
                entity = new VirusEntityImpl(this, position);
                break;
            default:
                throw new IllegalArgumentException("Unsupported entity type: " + type);
        }

        entities.put(entity.getID(), entity);
        return entity;
    }

    @Override
    public void removeEntity(Entity entity) {
        removeEntity(entity.getID());
    }

    @Override
    public void removeEntity(int id) {
        if (!entities.containsKey(id)) {
            throw new IllegalArgumentException("Entity with the specified ID does not exist in the world!");
        }

        entities.remove(id).onRemove();
    }

    @Override
    public EntityImpl getEntity(int id) {
        return entities.get(id);
    }

    public List<EntityImpl> getRawEntities() {
        return ImmutableList.copyOf(entities.valueCollection());
    }

    @Override
    public Collection<Entity> getEntities() {
        return ImmutableList.copyOf(entities.valueCollection());
    }

    public Border getBorder() {
        return border;
    }

    public View getView() {
        return view;
    }

    @Override
    public OgarServer getServer() {
        return server;
    }

    public Position getRandomPosition() {
        return new Position( (random.nextDouble() * (Math.abs(border.left) + Math.abs(border.right))) / 2.0D,
                (random.nextDouble() * (Math.abs(border.top) + Math.abs(border.bottom))) / 2.0D);
    }

    public static class View {

        private final double baseX;
        private final double baseY;

        public View(OgarConfig config) {
            this.baseX = config.world.view.baseX;
            this.baseY = config.world.view.baseY;
        }

        public View(double baseX, double baseY) {
            this.baseX = baseX;
            this.baseY = baseY;
        }

        public double getBaseX() {
            return baseX;
        }

        public double getBaseY() {
            return baseY;
        }
    }

    public static class Border {

        private final double left;
        private final double top;
        private final double right;
        private final double bottom;

        public Border(OgarConfig config) {
            this.left = config.world.border.left;
            this.top = config.world.border.top;
            this.right = config.world.border.right;
            this.bottom = config.world.border.bottom;
        }

        public Border(double left, double top, double right, double bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public double getLeft() {
            return left;
        }

        public double getTop() {
            return top;
        }

        public double getRight() {
            return right;
        }

        public double getBottom() {
            return bottom;
        }
    }
}
