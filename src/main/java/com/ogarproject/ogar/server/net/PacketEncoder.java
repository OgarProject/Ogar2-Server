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
package com.ogarproject.ogar.server.net;

import com.ogarproject.ogar.server.OgarServer;
import com.ogarproject.ogar.server.net.packet.Packet;
import com.ogarproject.ogar.server.net.packet.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import java.nio.ByteOrder;
import java.util.List;

public class PacketEncoder extends MessageToMessageEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer().order(ByteOrder.LITTLE_ENDIAN);
        int packetId = PacketRegistry.CLIENTBOUND.getPacketId(packet.getClass());
        if (packetId == -1) {
            throw new IllegalArgumentException("Provided packet is not registered as a clientbound packet!");
        }

        buf.writeByte(packetId);
        packet.writeData(buf);
        out.add(new BinaryWebSocketFrame(buf));

        OgarServer.log.finest("Sent packet ID " + packetId + " (" + packet.getClass().getSimpleName() + ") to " + ctx.channel().remoteAddress());
    }

}
