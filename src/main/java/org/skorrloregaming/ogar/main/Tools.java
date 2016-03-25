package org.skorrloregaming.ogar.main;

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
