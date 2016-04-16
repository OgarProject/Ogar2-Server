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

import java.awt.Color;

import io.netty.buffer.ByteBuf;
import com.ogarproject.ogar.server.net.packet.Packet;
import com.ogarproject.ogar.server.world.PlayerImpl;

/**
 * @autor Calypso
 */

public class PacketChat extends Packet
{
	public String message;
	public String chatname = "SERVER";
	public PlayerImpl player;
	public Color color = Color.RED;

	// TODO notused
	public int flags;

	public PacketChat(){}

	public PacketChat(PlayerImpl sender, String message)
	{
		this.message = message;
		player = sender;
	}

	public PacketChat(String chatname, String message, Color namecolor)
	{
		this.message = message;
		this.chatname = chatname;
		this.color = namecolor;
	}

	public PacketChat(String message, Color namecolor)
	{
		this.message = message;
		this.color = namecolor;
	}

	public PacketChat(String chatname, String message)
	{
		this.message = message;
		this.chatname = chatname;
	}
	
	public PacketChat(String message)
	{
		this.message = message;
	}

	@Override
	public void writeData(ByteBuf buf) {

		if(player != null)
		{
			String nick = player.getName();
			if(nick.isEmpty())
			{
				if(player.getCells().isEmpty())
					nick = "Наблюдатель";
				else
					nick = "An unnamed cell";
			}
	
			Color color = Color.BLACK;
			if(!player.getCells().isEmpty())
				color = player.getCellsColor();
			
			// flag
			buf.writeByte(flags);

			buf.writeByte(color.getRed());
			buf.writeByte(color.getGreen());
			buf.writeByte(color.getBlue());

	        writeUTF16(buf, nick);
	        writeUTF16(buf, message);
		}

		else
		{
			// flag
			buf.writeByte(flags);
			buf.writeByte(color.getRed());
			buf.writeByte(color.getGreen());
			buf.writeByte(color.getBlue());
			writeUTF16(buf, chatname);
			writeUTF16(buf, message);
		}
	}

	@Override
	public void readData(ByteBuf buf) {
		flags = buf.readByte();
		message = readUTF16(buf);
	}
}
