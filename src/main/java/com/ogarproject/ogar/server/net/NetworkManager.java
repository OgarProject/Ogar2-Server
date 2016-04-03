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
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.io.IOException;

public class NetworkManager {

    private final OgarServer server;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    public NetworkManager(OgarServer server) {
        this.server = server;
    }

    public void start() throws IOException, InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler()).childHandler(new ClientInitializer(server));

        channel = b.bind(server.getConfig().server.port).sync().channel();

        OgarServer.log.info("Server successfully started on port " + server.getConfig().server.port + ".");
    }

    public boolean shutdown() {
        if (channel == null) {
            return false;
        }

        try {
            channel.close().sync();
            return true;
        } catch (InterruptedException ex) {
            return false;
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class ClientInitializer extends ChannelInitializer<SocketChannel> {

        private final OgarServer server;

        public ClientInitializer(OgarServer server) {
            this.server = server;
        }

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            try {
                ch.config().setOption(ChannelOption.IP_TOS, 0x18);
            } catch (ChannelException ex) {
                // IP_TOS not supported by platform, ignore
            }
            ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);

            ch.pipeline().addLast(new HttpServerCodec());
            ch.pipeline().addLast(new HttpObjectAggregator(65536));
            ch.pipeline().addLast(new WebSocketHandler());
            ch.pipeline().addLast(new PacketDecoder());
            ch.pipeline().addLast(new PacketEncoder());
            ch.pipeline().addLast(new ClientHandler(server));
        }
    }
}
