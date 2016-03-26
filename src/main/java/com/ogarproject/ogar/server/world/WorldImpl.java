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
import com.ogarproject.ogar.api.CellOwner;
import com.ogarproject.ogar.api.entity.Entity;
import com.ogarproject.ogar.api.entity.EntityType;
import com.ogarproject.ogar.api.world.World;
import com.ogarproject.ogar.server.OgarServer;
import com.ogarproject.ogar.server.config.OgarConfig;
import com.ogarproject.ogar.server.config.OgarConfig.World.Food;
import com.ogarproject.ogar.server.entity.EntityImpl;
import com.ogarproject.ogar.server.entity.impl.CellImpl;
import com.ogarproject.ogar.server.entity.impl.FoodImpl;
import com.ogarproject.ogar.server.entity.impl.MassImpl;
import com.ogarproject.ogar.server.entity.impl.VirusImpl;
import com.ogarproject.ogar.server.tick.Tickable;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import jline.internal.Log;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class WorldImpl implements World {

    private final Random random = new Random(System.nanoTime());
    private final OgarServer server;
    private final TIntObjectMap<EntityImpl> entities = new TIntObjectHashMap<>();
    private final int[] entityCounts = new int[EntityType.values().length];
    private int totalEntities = 0;
    private final Border border;
    private final View view;

    public WorldImpl(OgarServer server) {
        this.server = server;
        this.border = new Border(server.getConfig());
        this.view = new View(server.getConfig());

        for (int i = 0; i < server.getConfig().world.food.startAmount; i++) {
            spawnEntity(EntityType.FOOD);
        }
        
        for (int i = 0; i < server.getConfig().world.virus.startAmount; i++) {
            spawnEntity(EntityType.VIRUS);
        }
        
    }

    @Override
    public EntityImpl spawnEntity(EntityType type) {
        return spawnEntity(type, getRandomPosition());
    }

    @Override
    public EntityImpl spawnEntity(EntityType type, Position position) {
        return spawnEntity(type, position, null);
    }

    public CellImpl spawnPlayerCell(PlayerImpl player) {
        return spawnPlayerCell(player, getRandomPosition());
    }

    public CellImpl spawnPlayerCell(PlayerImpl player, Position position) {
        return (CellImpl) spawnEntity(EntityType.CELL, position, player);
    }

    public EntityImpl spawnEntity(EntityType type, Position position, CellOwner owner) {
        if (type == null || position == null) {
            return null;
        }

        EntityImpl entity;
        switch (type) {
            case CELL:
                if (owner == null) {
                    throw new IllegalArgumentException("Cell entities must have an owner");
                }

                entity = new CellImpl(owner, this, position);
                break;
            case FOOD:
                entity = new FoodImpl(this, position);
                break;
            case MASS:
                entity = new MassImpl(this, position);
                break;
            case VIRUS:
                entity = new VirusImpl(this, position);
                break;
            default:
                throw new IllegalArgumentException("Unsupported entity type: " + type);
        }

        entities.put(entity.getID(), entity);
        entityCounts[type.ordinal()]++;
        totalEntities++;
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

        EntityImpl entity = entities.remove(id);
        entity.onRemove();
        entityCounts[entity.getType().ordinal()]--;
        totalEntities--;

        // TODO: Limit to viewbox?
        server.getPlayerList().getAllPlayers().stream().map(PlayerImpl::getTracker).forEach((t) -> t.remove(entity));
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

    public int getCellCount() {
        return entityCounts[EntityType.CELL.ordinal()];
    }

    public int getFoodCount() {
        return entityCounts[EntityType.FOOD.ordinal()];
    }

    public int getVirusCount() {
        return entityCounts[EntityType.VIRUS.ordinal()];
    }

    public int getMassCount() {
        return entityCounts[EntityType.MASS.ordinal()];
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
        return new Position((random.nextDouble() * (Math.abs(border.left) + Math.abs(border.right))),
                (random.nextDouble() * (Math.abs(border.top) + Math.abs(border.bottom))));
    }

    private void spawnFood() {
        int spawnedFood = 0;
        while (getFoodCount() < server.getConfig().world.food.maxAmount && spawnedFood < server.getConfig().world.food.spawnPerInterval) {
            spawnEntity(EntityType.FOOD);
            spawnedFood++;
        }
    }

    public void tick(Consumer<Tickable> serverTick) {
        try{
            if (server.getTick() % server.getConfig().world.food.spawnInterval == 0) {
                spawnFood();
            }

            for (EntityImpl entity : entities.valueCollection()) {
                serverTick.accept(entity);
            }
        } catch (Exception ex){
            Logger.getGlobal().warning("An internal error has occured while rendering, continuing to suppress...");
        }
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
