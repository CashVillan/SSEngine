package com.swingsword.ssengine.game.games.prison.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.utils.ScoreboardUtils;

public class Board {
	
	public Board() {
		com.swingsword.ssengine.scoreboard.ScoreboardManager.doTitleLoop = true;
		
		loopScoreboardLoad();
	}
	
	public static void loadScoreboard(Player player) {
		ScoreboardUtils.registerObjective(player, com.swingsword.ssengine.scoreboard.ScoreboardManager.scoreboardDisplay, DisplaySlot.SIDEBAR);
		
		ScoreboardUtils.addScore(player, ("&f" + Bukkit.getOnlinePlayers().size()).replace("&", ChatColor.COLOR_CHAR + ""), 0);
	}

	public static void loopScoreboardLoad() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					loadScoreboard(all);
				}
			}
		}, 20, 20);
	}
}