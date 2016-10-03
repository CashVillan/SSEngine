package com.swingsword.ssengine.game.games.agar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.stats.StatManager;
import com.swingsword.ssengine.utils.ScoreboardUtils;

public class ScoreboardManager {
	
	public ScoreboardManager() {
		com.swingsword.ssengine.scoreboard.ScoreboardManager.doTitleLoop = true;
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					List<String> lines = getLeaderboard(all);
					
					ScoreboardUtils.registerObjective(all, com.swingsword.ssengine.scoreboard.ScoreboardManager.scoreboardDisplay, DisplaySlot.SIDEBAR);
										
					for(int x = 0; x < lines.size(); x++) {
						String name = lines.get(x).split(" ")[lines.get(x).split(" ").length - 1];
						Player scorePlayer = Bukkit.getPlayer(name);
						
						for(String entry : all.getScoreboard().getEntries()) {
							if(entry.contains(" " + scorePlayer.getName())) {
								ScoreboardUtils.removeScore(all, entry);
							}
						}
						ScoreboardUtils.addScore(all, lines.get(x), AgarManager.getTotalMass(scorePlayer));
					}
				}
			}
		}, 20, 20);
	}

	public List<String> getLeaderboard(Player player) {
		List<String> lines = new ArrayList<String>();
		List<String> usedPlayers = new ArrayList<String>();
		
		for(Player all : AgarManager.agaring.keySet()) {
			Player highestPlayer = null;
			for(Player players : AgarManager.agaring.keySet()) {
				if((highestPlayer == null || AgarManager.getTotalMass(players) > AgarManager.getTotalMass(highestPlayer)) && !usedPlayers.contains(players.getName())) {
					highestPlayer = players;
				}
			}
			
			if(highestPlayer != null) {
				usedPlayers.add(highestPlayer.getName());
				
				if(lines.size() < 10) {
					if(PlayerStats.getStats(highestPlayer).topPosition.equals(":(") || Integer.parseInt(PlayerStats.getStats(highestPlayer).topPosition) < (lines.size() + 1)) {
						PlayerStats.getStats(highestPlayer).topPosition = (lines.size() + 1) + "";
					}
					if(highestPlayer.equals(player)) {
						PlayerStats.getStats(highestPlayer).leaderboardTime += 1;
						StatManager.addStat(highestPlayer, "ar_leaderboard_time", 1);
					}
					
					lines.add(ChatColor.BOLD + "" + (lines.size() + 1) + ". " + highestPlayer.getName());
				}
			}
		}
		return lines;
	}
}
