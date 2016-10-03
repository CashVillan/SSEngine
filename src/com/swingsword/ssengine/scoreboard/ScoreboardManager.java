package com.swingsword.ssengine.scoreboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.utils.ScoreboardUtils;

public class ScoreboardManager {
	
	public static HashMap<Player, List<String>> playerGamemodeTeams = new HashMap<Player, List<String>>();
	
	public ScoreboardManager() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@Override
			public void run() {
				progressLoop();
			}
		}, 3, 3);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (PlayerSessionManager.playerSession.containsKey(all.getName()) && PlayerSessionManager.playerSession.get(all.getName()).hasBeenLoaded && PlayerSessionManager.playerSession.get(all.getName()).created) {
						if (playerGamemodeTeams.containsKey(all)) {
							if (PlayerSessionManager.getSession(all).getAccount().getMainRank() != null) {
								ScoreboardUtils.getTeam(PlayerSessionManager.getSession(all).getAccount().getMainRank().name).removePlayer(all);
							}
							for (String team : playerGamemodeTeams.get(all)) {
								ScoreboardUtils.getTeam(team).addPlayer(all);
							}
						} else if (PlayerSessionManager.getSession(all).getAccount().getMainRank() != null) {
							ScoreboardUtils.getTeam(PlayerSessionManager.getSession(all).getAccount().getMainRank().name).addPlayer(all);
						}

						ScoreboardUtils.syncTeams(all);
					}
				}
			}
		}, 60, 60);
	}
	
	public static boolean doTitleLoop = true;
	private static int loopProgress = 0;
	public static String scoreboardDisplay = " ";
	
	public static void progressLoop() {
		loopProgress += 1;
		
		List<String> display = Arrays.asList("S", "w", "i", "n", "g", "S", "w", "o", "r", "d");
		String newDisplay = ChatColor.BOLD + "";
		
		for(int x = 0; x < display.size(); x++) {
			if(x - 1 == loopProgress) {
				newDisplay = newDisplay + ChatColor.AQUA + "" + ChatColor.BOLD + display.get(x);
			} else if(x == loopProgress) {
				newDisplay = newDisplay + ChatColor.GOLD + "" + ChatColor.BOLD + display.get(x);
			} else {
				newDisplay = newDisplay + display.get(x);
			}
		}
		if(loopProgress <= display.size()) {
			scoreboardDisplay = newDisplay;
		}
		
		if(loopProgress > display.size()) {
			if(loopProgress % 3 == 0 && loopProgress <= display.size() + 15) {
				if(scoreboardDisplay.contains(ChatColor.AQUA + "")) {
					scoreboardDisplay = ChatColor.BOLD + "SwingSword";
				} else {
					scoreboardDisplay = ChatColor.AQUA + "" + ChatColor.BOLD + ChatColor.stripColor(scoreboardDisplay);
				}
			}
		}
		
		if(loopProgress - 30 == display.size()) {
			loopProgress = -2;
			progressLoop();
		}
		
		updateScoreboardNames();
	}
	
	public static void updateScoreboardNames() {
		if(doTitleLoop) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(all.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
					all.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(scoreboardDisplay);
				}
			}
		}
	}
}
