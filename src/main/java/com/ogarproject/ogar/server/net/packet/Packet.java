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

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import java.nio.ByteOrder;

public abstract class Packet {

    /**
     * Writes packet data, excluding the packet ID, to the specified buffer.
     * <p>
     * 
     * @param buf
     */
    public abstract void writeData(ByteBuf buf);

    /**
     * Reads packet data, excluding the packet ID, from the specified buffer.
     * <p>
     * 
     * @param buf
     */
    public abstract void readData(ByteBuf buf);

    public static String readUTF8(ByteBuf in) {
        ByteBuf buffer = in.alloc().buffer();
        byte b;
        while (in.readableBytes() > 0 && (b = in.readByte()) != 0) {
            buffer.writeByte(b);
        }

        return buffer.toString(Charsets.UTF_8);
    }

    public static String readUTF16(ByteBuf in) {
        in = in.order(ByteOrder.BIG_ENDIAN);
        ByteBuf buffer = in.alloc().buffer();
        char chr;
        while (in.readableBytes() > 1 && (chr = in.readChar()) != 0) {
            buffer.writeChar(chr);
        }

        return buffer.toString(Charsets.UTF_16LE);
    }

    public static void writeUTF8(ByteBuf out, String s) {
        out.writeBytes(s.getBytes(Charsets.UTF_8));
        out.writeByte(0);
    }

    public static void writeUTF16(ByteBuf out, String s) {
        out.order(ByteOrder.BIG_ENDIAN).writeBytes(s.getBytes(Charsets.UTF_16LE));
        out.writeChar(0);
    }
}
