/**
 * This file is part of Ogar.
 * <p>
 * Ogar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ogar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ogar.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ogarproject.ogar.server.net;

import com.google.common.base.Preconditions;
import com.ogarproject.ogar.api.Ogar;
import com.ogarproject.ogar.api.entity.Cell;
import com.ogarproject.ogar.api.entity.Entity;
import com.ogarproject.ogar.api.entity.EntityType;
import com.ogarproject.ogar.api.event.player.PlayerConnectedEvent;
import com.ogarproject.ogar.api.event.player.PlayerConnectingEvent;
import com.ogarproject.ogar.api.event.player.PlayerNameChangeEvent;
import com.ogarproject.ogar.api.world.Position;
import com.ogarproject.ogar.server.OgarServer;
import com.ogarproject.ogar.server.entity.EntityImpl;
import com.ogarproject.ogar.server.entity.impl.CellImpl;
import com.ogarproject.ogar.server.net.packet.Packet;
import com.ogarproject.ogar.server.net.packet.inbound.*;
import com.ogarproject.ogar.server.net.packet.outbound.PacketOutWorldBorder;
import com.ogarproject.ogar.server.net.packet.universal.PacketChat;
import com.ogarproject.ogar.server.net.packet.universal.PacketOMPMessage;
import com.ogarproject.ogar.server.net.throwable.UnhandledPacketException;
import com.ogarproject.ogar.server.physic.Vector;
import com.ogarproject.ogar.server.world.PlayerImpl;
import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class PlayerConnection {

    static Logger log = Logger.getGlobal();
    private final PlayerImpl player;
    private final Channel channel;
    private final Map<Integer, MousePosition> cellMousePositions = new HashMap<>();
    private boolean individualMovementEnabled = false;
    private MousePosition globalMousePosition;
    private ConnectionState state = ConnectionState.AUTHENTICATE;
    private int protocolVersion;
    private String authToken;

    public PlayerConnection(PlayerImpl player, Channel channel) {
        this.player = player;
        this.channel = channel;
    }

    public SocketAddress getRemoteAddress() {
        return channel.remoteAddress();
    }

    public void sendPacket(Packet packet) {
        channel.writeAndFlush(packet);
    }

    public void handle(Packet packet) {
        if (packet instanceof PacketInSetNick) {
            handle((PacketInSetNick) packet);
        } else if (packet instanceof PacketInSpectate) {
            handle((PacketInSpectate) packet);
        } else if (packet instanceof PacketInMouseMove) {
            handle((PacketInMouseMove) packet);
        } else if (packet instanceof PacketInSplit) {
            handle((PacketInSplit) packet);
        } else if (packet instanceof PacketInPressQ) {
            handle((PacketInPressQ) packet);
        } else if (packet instanceof PacketInReleaseQ) {
            handle((PacketInReleaseQ) packet);
        } else if (packet instanceof PacketInEjectMass) {
            handle((PacketInEjectMass) packet);
        } else if (packet instanceof PacketInToken) {
            handle((PacketInToken) packet);
        } else if (packet instanceof PacketInFacebookLogin) {
            handle((PacketInFacebookLogin) packet);
        } else if (packet instanceof PacketInGoogleLogin) {
            handle((PacketInGoogleLogin) packet);
        } else if (packet instanceof PacketInAuthenticate) {
            handle((PacketInAuthenticate) packet);
        } else if (packet instanceof PacketInResetConnection) {
            handle((PacketInResetConnection) packet);
        } else if (packet instanceof PacketOMPMessage) {
            handle((PacketOMPMessage) packet);
        } else if (packet instanceof PacketChat) {
            handle((PacketChat) packet);
        } else {
            throw new UnhandledPacketException("Unhandled packet: " + packet);
        }
    }

    public void handle(PacketInSetNick packet) {
        checkConnected();
        if (player.getCells().isEmpty()) {
            PlayerNameChangeEvent event = new PlayerNameChangeEvent(player, packet.nickname);
            Ogar.getServer().getPluginManager().callEvent(event);

            player.setName(event.getName());
            CellImpl entity = OgarServer.getInstance().getWorld().spawnPlayerCell(player);
            player.addCell(entity);
        }
    }

    public void handle(PacketInSpectate packet) {
        checkConnected();
    }

    public void handle(PacketInMouseMove packet) {
        checkConnected();
        if (packet.nodeId == 0) {
            cellMousePositions.clear();
            individualMovementEnabled = false;
            globalMousePosition = new MousePosition(packet.x, packet.y);
        } else {
            individualMovementEnabled = true;
            MousePosition pos = new MousePosition(packet.x, packet.y);
            if (cellMousePositions.containsKey(packet.nodeId)) {
                cellMousePositions.remove(packet.nodeId);
            }
            cellMousePositions.put(packet.nodeId, pos);
        }
    }

    public void handle(PacketInSplit packet) {
        checkConnected();
        for (Cell cell : player.getCells()) {
            if (!(cell.getMass() > OgarServer.getInstance().getConfig().player.minMassSplit)) return;
            if (player.getCells().size() >= OgarServer.getInstance().getConfig().player.maxCells) return;
            Cell newCell = cell;
            newCell.setMass(cell.getMass() / 2);
            cell.setMass(cell.getMass() / 2);
            OgarServer.getInstance().getWorld().spawnEntity(EntityType.CELL, newCell.getPosition(), player);
            player.addCell(newCell);
        }
    }

    public void handle(PacketInPressQ packet) {
        checkConnected();
    }

    public void handle(PacketInReleaseQ packet) {
        checkConnected();
    }

    public void handle(PacketInEjectMass packet) {
        checkConnected();
        for (Cell cell : player.getCells()) {
            try {
                if (!(cell.getMass() > OgarServer.getInstance().getConfig().player.minMassEject)) return;
                cell.setMass(cell.getMass() - OgarServer.getInstance().getConfig().player.minMassEject);
                Entity mass = OgarServer.getInstance().getWorld().spawnEntity(EntityType.MASS, cell.getPosition(), null);

                Position mouse;
                if (player.getConnection().isIndividualMovementEnabled())
                    mouse = new Position(getCellMousePosition(cell.getID()).getX(), getCellMousePosition(cell.getID()).getY());
                else
                    mouse = new Position(getGlobalMousePosition().getX(), getGlobalMousePosition().getY());
                mouse.add(0 - cell.getPosition().getX(), 0 - cell.getPosition().getY());
                Vector cellvec = new Vector(mouse);
                cellvec.ReverseY();
                EntityImpl entity = (EntityImpl) mass;
                entity.getPhysics().setVector(cellvec);
                entity.getPhysics().setSpeed(250);
                entity.getPhysics().setResistance(30);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void handle(PacketInToken packet) {
        Preconditions.checkState(state == ConnectionState.TOKEN, "Not expecting TOKEN");
        state = ConnectionState.CONNECTED;
        authToken = packet.token;
        PlayerConnectingEvent connectingEvent = new PlayerConnectingEvent(player.getAddress(), protocolVersion, authToken);
        Ogar.getServer().getPluginManager().callEvent(connectingEvent);
        if (connectingEvent.isCancelled()) {
            channel.close();
            return;
        }
        PlayerConnectedEvent connectedEvent = new PlayerConnectedEvent(player);
        Ogar.getServer().getPluginManager().callEvent(connectedEvent);
    }

    public void handle(PacketInFacebookLogin packet) {

    }

    public void handle(PacketInGoogleLogin packet) {

    }

    public void handle(PacketInAuthenticate packet) {
        Preconditions.checkState(state == ConnectionState.AUTHENTICATE, "Not expecting AUTHENTICATE");
        state = ConnectionState.RESET;
        protocolVersion = packet.protocolVersion;
    }

    public void handle(PacketInResetConnection packet) {
        Preconditions.checkState(state == ConnectionState.RESET, "Not expecting RESET");
        state = ConnectionState.TOKEN;

        sendPacket(new PacketOutWorldBorder(OgarServer.getInstance().getWorld().getBorder()));
        sendPacket(new PacketOMPMessage("OMP|Capable"));
    }

    public void handle(PacketOMPMessage packet) {
        if (!player.isPluginMessageCapable() && "OMP|Capable".equals(packet.channel)) {
            player.setOMPCapable(true);
        }
    }

    public void handle(PacketChat packet) {
        checkConnected();
    }

    public boolean isIndividualMovementEnabled() {
        return individualMovementEnabled;
    }

    public MousePosition getGlobalMousePosition() {
        return globalMousePosition;
    }

    public MousePosition getCellMousePosition(int id) {
        return cellMousePositions.get(id);
    }

    private void checkConnected() {
        Preconditions.checkState(state == ConnectionState.CONNECTED, "Connection is not in CONNECTED state!");
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.channel);
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
        final PlayerConnection other = (PlayerConnection) obj;
        return Objects.equals(this.channel, other.channel);
    }

    private enum ConnectionState {

        AUTHENTICATE, RESET, TOKEN, CONNECTED
    }

    public static class MousePosition {

        private final double x;
        private final double y;

        public MousePosition(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
