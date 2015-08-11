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

import java.util.function.Supplier;

public class TickableSupplier implements Tickable {

    private final Supplier supplier;
    private boolean ticked = false;

    public TickableSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public void tick() {
        if (!ticked) {
            supplier.get();
            ticked = true;
        }
    }
}
