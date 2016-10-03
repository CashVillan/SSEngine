package com.swingsword.ssengine.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SpectatorUtils {

	public static ArrayList<Player> spectators = new ArrayList<Player>();
	
	public static void setSpectating(Player player, boolean spectate) {
		if(spectate) {
			spectators.add(player);

			player.setGameMode(GameMode.CREATIVE);
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.hidePlayer(player);
			}
			
		} else {
			spectators.remove(player);

			player.setGameMode(GameMode.SURVIVAL);
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.showPlayer(player);
			}
		}
	}
	
	public static void resetVisibility(Player player) {
		for(Player all : Bukkit.getOnlinePlayers()) {
			all.showPlayer(player);
			player.showPlayer(all);
		}
	}
}
