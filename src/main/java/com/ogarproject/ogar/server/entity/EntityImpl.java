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
package com.ogarproject.ogar.server.entity;

import com.ogarproject.ogar.api.entity.Entity;
import com.ogarproject.ogar.api.entity.EntityType;
import com.ogarproject.ogar.server.tick.Tickable;
import com.ogarproject.ogar.api.world.Position;
import com.ogarproject.ogar.server.world.WorldImpl;
import java.awt.Color;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EntityImpl implements Entity, Tickable {

    private static final AtomicInteger nextEntityId = new AtomicInteger(1);
    protected final int id;
    protected final EntityType type;
    protected final WorldImpl world;
    protected Position position;
    protected Color color = Color.GREEN;
    protected int consumer = 0;
    protected int mass = 10;
    protected boolean spiked = false;

    public EntityImpl(EntityType type, WorldImpl world, Position position) {
        this.id = nextEntityId.getAndIncrement();
        this.type = type;
        this.world = world;
        this.position = position;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public int getConsumer() {
        return consumer;
    }

    public void kill(int consumer) {
        this.consumer = consumer;
        world.removeEntity(this);
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getPhysicalSize() {
        return (int) Math.ceil(Math.sqrt(100 * mass));
    }

    @Override
    public int getMass() {
        return mass;
    }

    @Override
    public void setMass(int mass) {
        this.mass = mass;
    }

    @Override
    public void addMass(int mass) {
        this.mass += mass;
    }

    @Override
    public boolean isSpiked() {
        return spiked;
    }

    @Override
    public void setSpiked(boolean spiked) {
        this.spiked = spiked;
    }

    @Override
    public WorldImpl getWorld() {
        return world;
    }

    public boolean collisionCheck(double bottomY, double topY, double rightX, double leftX) {
        if (getY() > bottomY || getY() < topY || getX() > rightX || getX() < leftX) {
            return false;
        }

        return true;
    }

    /**
     * Defines whether or not this entity should be updated to clients.
     */
    public abstract boolean shouldUpdate();

    /**
     * Called on every tick.
     */
    public abstract void tick();

    public void onRemove() {
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.id;
        hash = 47 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntityImpl other = (EntityImpl) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

}
