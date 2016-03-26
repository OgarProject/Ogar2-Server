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

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import com.ogarproject.ogar.api.Player;
import com.ogarproject.ogar.api.entity.Cell;
import com.ogarproject.ogar.server.OgarServer;
import com.ogarproject.ogar.server.world.PlayerImpl;

public class Commands {
	
	public static HashMap<Integer, Player> players = new HashMap<>();
	
	public static boolean registerPlayer(){
		try{
			
			return true;
		}catch (Exception ex){
			return false;
		}
	}
	
	public static boolean unregisterPlayer(){
		try{
			
			return true;
		}catch (Exception ex){
			return false;
		}
	}
	
	public static void onCommand(String s){
        switch (s.toLowerCase().split(" ")[0]) {
            case "help":
                Logger.getGlobal().info("Command listing for Ogar 2.0:");
                Logger.getGlobal().info("\thelp\t\t");
                Logger.getGlobal().info("\tstop\t\t");
                Logger.getGlobal().info("Command listing for OgarioProject:");
                Logger.getGlobal().info("\tlist\t\t");
                Logger.getGlobal().info("\tnick <ClientID> <Nickname>\t\t");
                Logger.getGlobal().info("\tmass <ClientID> <Mass>\t\t");
                break;
            case "stop":
                OgarServer.getInstance().shutdown();
                break;
            case "list":
            	Logger.getGlobal().info("Currently connected players ("+OgarServer.getInstance().getPlayerList().getAllPlayers().toArray().length+"):");
                Collection<PlayerImpl> players = OgarServer.getInstance().getPlayerList().getAllPlayers();
                for (PlayerImpl ob : players){
                	Logger.getGlobal().info("IP: "+ob.getConnection().getRemoteAddress().toString().split(":")[0].split("/")[1]+" - Client ID: "+ob.getConnection().getRemoteAddress().toString().split(":")[1]+" - Name: "+ob.getName());
                }
                break;
            case "mass":
            	try{
                	PlayerImpl targetPlayer = Tools.ConvertClientToPlayer(s.split(" ")[1]);
                	for (Cell cell : targetPlayer.getCells()){
                		cell.setMass(Integer.parseInt(s.split(" ")[2]));
                	}
                	Logger.getGlobal().info(targetPlayer.getName()+"s mass has been successfully set to "+s.split(" ")[2]+"!");
            	}catch (Exception ex){
                	Logger.getGlobal().info("An internal error has occured whilist performing this command!");
            	}
            	break;
            case "nick":
            	try{
                	PlayerImpl targetPlayer = Tools.ConvertClientToPlayer(s.split(" ")[1]);
                	Logger.getGlobal().info(targetPlayer.getName()+" has been successfully renamed to "+s.split(" ")[2]+"!");
                	targetPlayer.setName(s.split(" ")[2]);
            	}catch (Exception ex){
                	Logger.getGlobal().info("An internal error has occured whilist performing this command!");
            	}
            	break;
            default:
            	Logger.getGlobal().info("Unknown command. Type \"help\" for help.");
                break;
        }
	}
	
}
