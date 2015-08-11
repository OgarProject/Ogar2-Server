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
/**
 * This file is part of Ogar.
 *
 * Ogar is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Ogar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Ogar. If not, see <http://www.gnu.org/licenses/>.
 */
package com.ogarproject.ogar.server.net;

import com.ogarproject.ogar.api.event.player.PlayerDisconnectEvent;
import com.ogarproject.ogar.server.OgarServer;
import com.ogarproject.ogar.server.world.PlayerImpl;
import com.ogarproject.ogar.server.net.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.logging.Level;

public class ClientHandler extends SimpleChannelInboundHandler<Packet> {

    private final OgarServer server;
    private PlayerImpl player;

    public ClientHandler(OgarServer server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.player = new PlayerImpl(ctx.channel());
        server.getPlayerList().addPlayer(player);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        PlayerDisconnectEvent event = new PlayerDisconnectEvent(player);
        server.getPluginManager().callEvent(event);

        server.getPlayerList().removePlayer(player);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        player.getConnection().handle(packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        server.getLogger().log(Level.SEVERE, "Encountered exception in pipeline for client at " + ctx.channel().remoteAddress() + "; disconnecting client.", cause);
        ctx.channel().close();
    }
}
