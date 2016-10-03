package com.swingsword.ssengine.game.games.buildit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.threads.TimerHandler;
import com.swingsword.ssengine.utils.ScoreboardUtils;

public class ScoreboardManager {
	
	public ScoreboardManager() {
		com.swingsword.ssengine.scoreboard.ScoreboardManager.doTitleLoop = false;
		
		loopScoreboardLoad();
	}
	
	public static void loadScoreboard(Player player) {
		ScoreboardUtils.registerObjective(player, com.swingsword.ssengine.scoreboard.ScoreboardManager.scoreboardDisplay, DisplaySlot.SIDEBAR);
		
		ScoreboardUtils.addScore(player, "&b&lTheme".replace("&", ChatColor.COLOR_CHAR + ""), 7);
		ScoreboardUtils.addScore(player, ("&f" + BuildIt.theWord).replace("&", ChatColor.COLOR_CHAR + ""), 6);
		ScoreboardUtils.addScore(player, null, 5);
		
		if(TimerHandler.getTimer("reviewWait") == null && TimerHandler.getTimer("buildTime") != null) {
			ScoreboardUtils.addScore(player, "&e&lBuild Time".replace("&", ChatColor.COLOR_CHAR + ""), 4);
			ScoreboardUtils.addScore(player, getTimeString(TimerHandler.getTimer("buildTime").getLeft()).replace("&", ChatColor.COLOR_CHAR + ""), 3);
		} else {
			ScoreboardUtils.addScore(player, "&e&lReview Time".replace("&", ChatColor.COLOR_CHAR + ""), 4);
			ScoreboardUtils.addScore(player, getTimeString(TimerHandler.getTimer("reviewWait").getLeft()).replace("&", ChatColor.COLOR_CHAR + ""), 3);
		}
		
		ScoreboardUtils.addScore(player, null, 2);
		ScoreboardUtils.addScore(player, "&a&lPlayers".replace("&", ChatColor.COLOR_CHAR + ""), 1);
		ScoreboardUtils.addScore(player, ("&f" + Bukkit.getOnlinePlayers().size()).replace("&", ChatColor.COLOR_CHAR + ""), 0);
	}
	
	public static void loopScoreboardLoad() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(BuildIt.theWord != null) {
						loadScoreboard(all);
						
						com.swingsword.ssengine.scoreboard.ScoreboardManager.doTitleLoop = true;
						
					} else {
						ScoreboardUtils.loadLobbyScoreboard(all);
					}
				}
				
				if(TimerHandler.getTimer("buildTime") != null && TimerHandler.getTimer("buildTime").getLeft() <= 10) {
					for(Player all : Bukkit.getOnlinePlayers()) {
						all.playSound(all.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 2);
					}
				}
			}
		}, 20, 20);
	}
	
	public static String getTimeString(int seconds) {
		int minutes = seconds / 60;
		int second = seconds % 60;
		String secondString = second + "";
		if(second < 10) {
			secondString = "0" + second;
		}
		
		ChatColor color = ChatColor.WHITE;
		
		if(second <= 10) {
			if(second % 2 == 0) {
				color = ChatColor.RED;
			}
		}
		
		return color + "" + minutes + ":" + secondString;
	}
}
