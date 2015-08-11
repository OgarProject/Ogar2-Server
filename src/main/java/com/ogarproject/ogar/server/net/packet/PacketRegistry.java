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
package com.ogarproject.ogar.server.net.packet;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import com.ogarproject.ogar.server.net.packet.inbound.*;
import com.ogarproject.ogar.server.net.packet.outbound.*;
import com.ogarproject.ogar.server.net.packet.universal.*;

public class PacketRegistry {

    public static final ProtocolDirection CLIENTBOUND = new ProtocolDirection("CLIENTBOUND");
    public static final ProtocolDirection SERVERBOUND = new ProtocolDirection("SERVERBOUND");

    static {
        // Clientbound packets
        CLIENTBOUND.registerPacket(16, PacketOutUpdateNodes.class);
        CLIENTBOUND.registerPacket(20, PacketOutClearNodes.class);
        CLIENTBOUND.registerPacket(21, PacketOutDrawLine.class);
        CLIENTBOUND.registerPacket(32, PacketOutAddNode.class);
        CLIENTBOUND.registerPacket(49, PacketOutUpdateLeaderboardFFA.class);
        CLIENTBOUND.registerPacket(64, PacketOutWorldBorder.class);
        CLIENTBOUND.registerPacket(240, PacketOMPMessage.class);

        // Serverbound packets
        SERVERBOUND.registerPacket(0, PacketInSetNick.class);
        SERVERBOUND.registerPacket(1, PacketInSpectate.class);
        SERVERBOUND.registerPacket(16, PacketInMouseMove.class);
        SERVERBOUND.registerPacket(17, PacketInSplit.class);
        SERVERBOUND.registerPacket(18, PacketInPressQ.class);
        SERVERBOUND.registerPacket(19, PacketInReleaseQ.class);
        SERVERBOUND.registerPacket(21, PacketInEjectMass.class);
        SERVERBOUND.registerPacket(80, PacketInToken.class);
        SERVERBOUND.registerPacket(81, PacketInFacebookLogin.class);
        SERVERBOUND.registerPacket(240, PacketOMPMessage.class);
        SERVERBOUND.registerPacket(254, PacketInAuthenticate.class);
        SERVERBOUND.registerPacket(255, PacketInResetConnection.class);
    }

    // Static-use class
    private PacketRegistry() {}

    public static class ProtocolDirection {

        private final TIntObjectMap<Class<? extends Packet>> packetClasses = new TIntObjectHashMap<>(10, 0.5F);
        private final TObjectIntMap<Class<? extends Packet>> reverseMapping = new TObjectIntHashMap<>(10, 0.5F, -1);
        private final String name;

        private ProtocolDirection(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        private void registerPacket(int packetId, Class<? extends Packet> clazz) {
            if (packetClasses.containsKey(packetId)) {
                throw new IllegalArgumentException("Packet with ID " + packetId + " is already registered for " + this + "!");
            }

            if (reverseMapping.containsKey(clazz)) {
                throw new IllegalArgumentException("Packet with class " + clazz + " is already registered for " + this + "!");
            }

            packetClasses.put(packetId, clazz);
            reverseMapping.put(clazz, packetId);
        }

        public int getPacketId(Class<? extends Packet> clazz) {
            return reverseMapping.get(clazz);
        }

        public Class<? extends Packet> getPacketClass(int packetId) {
            return packetClasses.get(packetId);
        }

        public Packet constructPacket(int packetId) {
            Class<? extends Packet> clazz = getPacketClass(packetId);
            if (clazz == null) {
                return null;
            }

            try {
                return clazz.newInstance();
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public String toString() {
            return "ProtocolDirection{" + "name=" + name + '}';
        }
    }
}
