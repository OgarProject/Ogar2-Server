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
package com.ogarproject.ogar.server.net.packet.outbound;

import com.ogarproject.ogar.server.entity.EntityImpl;
import com.ogarproject.ogar.server.entity.impl.CellEntityImpl;
import com.ogarproject.ogar.server.net.packet.Packet;
import com.ogarproject.ogar.server.net.throwable.MalformedPacketException;
import com.ogarproject.ogar.server.net.throwable.WrongDirectionException;
import com.ogarproject.ogar.server.world.WorldImpl;
import io.netty.buffer.ByteBuf;
import java.util.Collection;

public class PacketOutUpdateNodes extends Packet {

    private final WorldImpl world;
    private final Collection<EntityImpl> removals;
    private final Collection<EntityImpl> removalsByEating;
    private final Collection<Integer> updates;

    public PacketOutUpdateNodes(WorldImpl world, Collection<EntityImpl> removals, Collection<EntityImpl> removalsByEating, Collection<Integer> updates) {
        this.world = world;
        this.removals = removals;
        this.removalsByEating = removalsByEating;
        this.updates = updates;
    }

    @Override
    public void writeData(ByteBuf buf) {
        // Removals by eating
        buf.writeShort(removalsByEating.size());
        for (EntityImpl entity : removalsByEating) {
            buf.writeInt(entity.getConsumer());
            buf.writeInt(entity.getID());
        }

        // Updates
        for (int id : updates) {
            EntityImpl entity = world.getEntity(id);
            if (entity == null) {
                // TODO - Theoretically this could be ignored, but it might cause other issues,
                // like having nonexistent entities on the player's screen. Re-evaluate this later?
                throw new MalformedPacketException("Attempted to update nonexistent entity");
            }

            buf.writeInt(entity.getID());
            buf.writeInt((int) entity.getPosition().getX());
            buf.writeInt((int) entity.getPosition().getY());
            buf.writeShort(entity.getPhysicalSize());
            buf.writeByte(entity.getColor().getRed());
            buf.writeByte(entity.getColor().getGreen());
            buf.writeByte(entity.getColor().getBlue());
            buf.writeBoolean(entity.isSpiked());
            // buf.skipBytes(18);
            if (entity instanceof CellEntityImpl) {
                CellEntityImpl cell = (CellEntityImpl) entity;
                if (cell.getName() == null) {
                    writeUTF16(buf, "");
                } else {
                    writeUTF16(buf, cell.getName());
                }
            } else {
                writeUTF16(buf, "");
            }
        }
        buf.writeInt(0);

        // General removals
        buf.writeInt(removals.size());
        for (EntityImpl entity : removals) {
            buf.writeInt(entity.getID());
        }
    }

    @Override
    public void readData(ByteBuf buf) {
        throw new WrongDirectionException();
    }

}
