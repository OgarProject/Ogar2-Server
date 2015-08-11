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

import com.ogarproject.ogar.server.world.WorldImpl;
import com.ogarproject.ogar.server.net.packet.Packet;
import com.ogarproject.ogar.server.net.throwable.WrongDirectionException;
import io.netty.buffer.ByteBuf;

public class PacketOutWorldBorder extends Packet {

    public double left;
    public double top;
    public double right;
    public double bottom;

    public PacketOutWorldBorder() {}

    public PacketOutWorldBorder(WorldImpl.Border border) {
        this.left = border.getLeft();
        this.top = border.getTop();
        this.right = border.getRight();
        this.bottom = border.getBottom();
    }

    public PacketOutWorldBorder(double left, double top, double right, double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public void writeData(ByteBuf buf) {
        buf.writeDouble(left);
        buf.writeDouble(top);
        buf.writeDouble(right);
        buf.writeDouble(bottom);
    }

    @Override
    public void readData(ByteBuf buf) {
        throw new WrongDirectionException();
    }

}
