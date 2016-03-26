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

import com.ogarproject.ogar.api.entity.EntityType;
import com.ogarproject.ogar.api.entity.Food;
import com.ogarproject.ogar.server.OgarServer;
import com.ogarproject.ogar.server.entity.EntityImpl;
import com.ogarproject.ogar.api.world.Position;
import com.ogarproject.ogar.server.world.WorldImpl;

public class FoodImpl extends EntityImpl implements Food {

    public FoodImpl(WorldImpl world, Position position) {
        super(EntityType.FOOD, world, position);
        this.mass = OgarServer.getInstance().getConfig().world.food.foodSize;
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }

    @Override
    public void tick() {
    }
}
