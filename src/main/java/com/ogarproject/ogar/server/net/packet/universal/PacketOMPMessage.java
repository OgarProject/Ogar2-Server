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
package com.ogarproject.ogar.server.net.packet.universal;

import com.ogarproject.ogar.server.net.packet.Packet;
import com.ogarproject.ogar.server.net.throwable.MalformedPacketException;
import io.netty.buffer.ByteBuf;

public class PacketOMPMessage extends Packet {

    public String channel;
    public byte[] data;

    public PacketOMPMessage() {}

    public PacketOMPMessage(String channel) {
        this.channel = channel;
        this.data = new byte[0];
    }

    public PacketOMPMessage(String channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }

    @Override
    public void writeData(ByteBuf buf) {
        if (data.length > 32767) {
            throw new MalformedPacketException("Exceeded max OMP message size (" + data.length + " > 32767)");
        }

        writeUTF8(buf, channel);
        buf.writeBytes(data);
    }

    @Override
    public void readData(ByteBuf buf) {
        channel = readUTF8(buf);
        int size = buf.readableBytes();
        if (size > 32767) {
            throw new MalformedPacketException("Exceeded max OMP message size (" + size + " > 32767)");
        }

        data = new byte[size];
        buf.readBytes(data);
    }
}
