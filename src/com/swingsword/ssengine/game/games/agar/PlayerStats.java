package com.swingsword.ssengine.game.games.agar;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.swingsword.ssengine.stats.StatManager;

public class PlayerStats {
	
	public static HashMap<Player, PlayerStats> playerStats = new HashMap<Player, PlayerStats>();
	
	public static PlayerStats getStats(Player player) {
		return playerStats.get(player);
	}
	
	public static void removeStats(Player player) {
		playerStats.remove(player);
	}
	
	public int foodEaten = 0;
	public int highestMass = 10;
	public int timeAlive = 0;
	public int leaderboardTime = 0;
	public int cellsEaten = 0;
	public String topPosition = ":(";
	
	public PlayerStats(Player player) {
		playerStats.put(player, this);
		
		StatManager.addStat(player, "ar_lives", 1);
	}
}
