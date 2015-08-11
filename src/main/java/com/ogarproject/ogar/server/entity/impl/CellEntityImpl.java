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
package com.ogarproject.ogar.server.entity.impl;

import com.ogarproject.ogar.api.entity.CellEntity;
import com.ogarproject.ogar.api.entity.EntityType;
import com.ogarproject.ogar.server.world.PlayerImpl;
import com.ogarproject.ogar.server.entity.EntityImpl;
import com.ogarproject.ogar.server.net.PlayerConnection;
import com.ogarproject.ogar.server.util.MathHelper;
import com.ogarproject.ogar.api.world.Position;
import com.ogarproject.ogar.server.world.WorldImpl;
import java.util.ArrayList;
import java.util.List;

public class CellEntityImpl extends EntityImpl implements CellEntity {

    private final PlayerImpl owner;
    private String name;
    private long recombineTicks = 0;

    public CellEntityImpl(PlayerImpl owner, WorldImpl world, Position position) {
        super(EntityType.CELL, world, position);
        this.owner = owner;
        this.name = owner.getName();
    }

    @Override
    public boolean shouldUpdate() {
        // Cells should always update
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerImpl getOwner() {
        return owner;
    }

    public double getSpeed() {
        return 30.0D * Math.pow(mass, -1.0D / 4.5D) * 50.0D / 40.0D;
    }

    public long getRecombineTicks() {
        return recombineTicks;
    }

    public void setRecombineTicks(long recombineTicks) {
        this.recombineTicks = recombineTicks;
    }

    @Override
    public void tick() {
        if (recombineTicks > 0) {
            recombineTicks--;
        }

        move();
        eat();
    }

    private void move() {
        int r = getPhysicalSize();

        PlayerConnection.MousePosition mouse = getOwner().getConnection().getCellMousePosition(getID());
        if (mouse == null || !getOwner().getConnection().isIndividualMovementEnabled()) {
            mouse = getOwner().getConnection().getGlobalMousePosition();
            if (mouse == null) {
                return;
            }
        }

        // Get angle
        double deltaX = mouse.getX() - getX();
        double deltaY = mouse.getY() - getY();
        double angle = Math.atan2(deltaX, deltaY);

        if (Double.isNaN(angle)) {
            return;
        }

        // Distance between mouse pointer and cell
        double distance = position.distance(mouse.getX(), mouse.getY());
        double speed = Math.min(getSpeed(), distance);

        double x1 = getX() + (speed * Math.sin(angle));
        double y1 = getY() + (speed * Math.cos(angle));

        // Collision check for the owner's other cells
        for (CellEntityImpl other : owner.getCells()) {
            if (other.equals(this)) {
                continue;
            }

            double collisionDist = other.getPhysicalSize() + r; // Minimum distance between the 2 cells
            if (!simpleCollide(other, collisionDist)) {
                continue;
            }

            // Precise collision checking
            distance = position.distance(other.getPosition());

            if (distance < collisionDist) {
                // Moving cell pushes collided cell
                double newDeltaX = other.getX() - x1;
                double newDeltaY = other.getY() - y1;
                double newAngle = Math.atan2(newDeltaX, newDeltaY);

                double move = collisionDist - distance + 5.0D;

                other.setPosition(other.getPosition().add(move * Math.sin(newAngle), move * Math.cos(newAngle)));
            }
        }

        // TODO: Fire a move event here
        // Make sure we're not passing the world border
        if (x1 < world.getBorder().getLeft()) {
            x1 = world.getBorder().getLeft();
        }
        if (x1 > world.getBorder().getRight()) {
            x1 = world.getBorder().getRight();
        }
        if (y1 < world.getBorder().getTop()) {
            y1 = world.getBorder().getTop();
        }
        if (y1 > world.getBorder().getBottom()) {
            y1 = world.getBorder().getBottom();
        }

        setPosition(new Position(x1, y1));
    }

    private void eat() {
        List<EntityImpl> edibles = new ArrayList<>();
        int r = getPhysicalSize();

        double topY = getY() - r;
        double bottomY = getY() + r;
        double leftX = getX() - r;
        double rightX = getX() + r;

        for (int otherId : owner.getTracker().getVisibleEntities()) {
            EntityImpl other = world.getEntity(otherId);
            if (other == null) {
                continue;
            }

            if (other.equals(this)) {
                continue;
            }

            if (!other.collisionCheck(bottomY, topY, rightX, leftX)) {
                continue;
            }

            double multiplier = 1.25D;
            if (other instanceof FoodEntityImpl) {
                edibles.add(other);
                continue;
            } else if (other instanceof VirusEntityImpl) {
                multiplier = 1.0D + (1D / 3D); // 1.3333...
            } else if (other instanceof CellEntityImpl) {
                CellEntityImpl otherCell = (CellEntityImpl) other;

                // Should we recombine?
                if (owner.equals(otherCell.getOwner()) && recombineTicks == 0 && otherCell.getRecombineTicks() == 0) {
                    multiplier = 1.0D;
                }
            }

            // Is the other cell big enough to eat?
            if (other.getMass() * multiplier > mass) {
                continue;
            }

            // Eating range
            double dist = position.distance(other.getPosition());
            double eatingRange = getPhysicalSize() - (other.getPhysicalSize() * 0.4D);

            if (dist > eatingRange) {
                continue;
            }

            // Sweet, let's eat!
            edibles.add(other);
        }

        // Process the list of edibles
        for (EntityImpl entity : edibles) {
            this.addMass(entity.getMass());
            entity.kill(getID());
        }
    }

    private boolean simpleCollide(CellEntityImpl other, double collisionDist) {
        return MathHelper.fastAbs(getX() - other.getX()) < (2 * collisionDist) && MathHelper.fastAbs(getY() - other.getY()) < (2 * collisionDist);
    }
}
