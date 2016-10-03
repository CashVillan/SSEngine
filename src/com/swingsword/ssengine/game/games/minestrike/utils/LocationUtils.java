package com.swingsword.ssengine.game.games.minestrike.utils;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.team.Team;

public class LocationUtils {
			
	public static void TeleportToGame(Player player, Team team) {
		Location spawn = getRandomFreeSpawn(team, 100);
		
		if(spawn != null) {
			player.teleport(spawn);
		}
	}
	
	public static Location getRandomFreeSpawn(Team team, int triesLeft) {
		if(triesLeft > 0) {			
			Location loc = team.getSpawns().get(new Random().nextInt(team.getSpawns().size()));
			
			//TODO check if empty
				return loc.clone().add(0.5, 1, 0.5);
				
			//} else {
			//	return getRandomFreeSpawn(team, triesLeft - 1);
			//}
		} else {
			System.out.println("ERROR: NOT ENOUGH SPAWNS DEFINED!");
		}
		return null;
	}
}
