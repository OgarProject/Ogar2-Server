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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.netty.buffer.ByteBuf;
import com.ogarproject.ogar.server.OgarServer;
import com.ogarproject.ogar.server.net.packet.Packet;
import com.ogarproject.ogar.server.net.throwable.WrongDirectionException;
import com.ogarproject.ogar.server.world.PlayerImpl;

public class PacketOutUpdateLeaderboardFFA extends Packet {

        private List<PlayerImpl> allParticates = new ArrayList<PlayerImpl>();
    private final OgarServer server;

    public PacketOutUpdateLeaderboardFFA(OgarServer server) {
        this.server = server;
    }

    @Override
    public void writeData(ByteBuf buf) {
        prepare();
        int max = 10;// TODO config
        if(allParticates.size() >= max)
                buf.writeInt(max);
        else
                buf.writeInt(allParticates.size());
        for(PlayerImpl player : allParticates)
        {
                 buf.writeInt(player.getCellIdAt(0));
             writeUTF16(buf, player.getName());
        }
    }

    @Override
    public void readData(ByteBuf buf) {
        throw new WrongDirectionException();
    }

    private void prepare()
        {
                for(PlayerImpl player : server.getPlayerList().getAllPlayers())
                {
                        if(player.getCells().isEmpty())
                                continue;
                        allParticates.add(player);
                }
                allParticates.sort(PLAYER_COMPARATOR);
        }

        public static final Comparator<PlayerImpl> PLAYER_COMPARATOR = (o1, o2) -> {
                if(o1.getTotalMass() > o2.getTotalMass())
                        return -1;
                if(o1.getTotalMass() < o2.getTotalMass())
                        return 1;
                return 0;
        };
}
 