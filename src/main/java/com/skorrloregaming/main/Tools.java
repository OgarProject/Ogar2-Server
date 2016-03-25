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
package com.skorrloregaming.main;

import java.util.logging.Logger;

import com.ogarproject.ogar.server.OgarServer;
import com.ogarproject.ogar.server.world.PlayerImpl;

public class Tools {
	public static PlayerImpl ConvertClientToPlayer(String clientID){
		for (PlayerImpl player : OgarServer.getInstance().getPlayerList().getAllPlayers()){
			if (player.getConnection().getRemoteAddress().toString().split(":")[1].equalsIgnoreCase(clientID)){
				return player;
			}
		}
		Logger.getGlobal().info("A severe internal error has occured trying to fetch client player!");
		return null;
	}
}
