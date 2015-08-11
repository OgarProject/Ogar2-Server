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

import com.ogarproject.ogar.server.net.packet.Packet;
import com.ogarproject.ogar.server.net.throwable.WrongDirectionException;
import io.netty.buffer.ByteBuf;

public class PacketOutUpdateLeaderboardFFA extends Packet {

    private final Entry[] entries;

    public PacketOutUpdateLeaderboardFFA(Entry[] entries) {
        this.entries = entries;
    }

    @Override
    public void writeData(ByteBuf buf) {
        buf.writeInt(entries.length);
        for (Entry entry : entries) {
            buf.writeInt(entry.getEntityId());
            writeUTF16(buf, entry.getName());
        }
    }

    @Override
    public void readData(ByteBuf buf) {
        throw new WrongDirectionException();
    }

    public static class Entry {

        private final int entityId;
        private final String name;

        public Entry(int entityId, String name) {
            this.entityId = entityId;
            this.name = name;
        }

        public int getEntityId() {
            return entityId;
        }

        public String getName() {
            return name;
        }

    }
}
