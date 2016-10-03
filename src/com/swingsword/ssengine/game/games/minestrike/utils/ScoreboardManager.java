package com.swingsword.ssengine.game.games.minestrike.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.GameState;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.team.Team;
import com.swingsword.ssengine.game.threads.TimerHandler;
import com.swingsword.ssengine.utils.ScoreboardUtils;

public class ScoreboardManager {

	public static void loadScoreboard(Player player) {
		ScoreboardUtils.registerObjective(player, com.swingsword.ssengine.scoreboard.ScoreboardManager.scoreboardDisplay, DisplaySlot.SIDEBAR);
		
		ScoreboardUtils.addScore(player, "&3&lC-Terrorist".replace("&", ChatColor.COLOR_CHAR + ""), 12);
		ScoreboardUtils.addScore(player, CSGOGame.CTwins + " Rounds Won&c".replace("&", ChatColor.COLOR_CHAR + ""), 11);
		ScoreboardUtils.addScore(player, Team.getTeam("CT").getAlive() + " Player Alive&c".replace("&", ChatColor.COLOR_CHAR + ""), 10);
		ScoreboardUtils.addScore(player, null, 9);
		ScoreboardUtils.addScore(player, "&c&lTerrorist".replace("&", ChatColor.COLOR_CHAR + ""), 8);
		ScoreboardUtils.addScore(player, CSGOGame.Twins + " Rounds Won".replace("&", ChatColor.COLOR_CHAR + ""), 7);
		ScoreboardUtils.addScore(player, Team.getTeam("T").getAlive() + " Player Alive".replace("&", ChatColor.COLOR_CHAR + ""), 6);
		ScoreboardUtils.addScore(player, null, 5);
		ScoreboardUtils.addScore(player, "&e&lMoney".replace("&", ChatColor.COLOR_CHAR + ""), 4);
		ScoreboardUtils.addScore(player, "$" + CSGOGame.Money.get(player.getName()), 3);
		ScoreboardUtils.addScore(player, null, 2);
		ScoreboardUtils.addScore(player, "&e&lTime Left".replace("&", ChatColor.COLOR_CHAR + ""), 1);
		ScoreboardUtils.addScore(player, getCurrentTimer(), 0);
	}
	
	public static void loopScoreboardLoad() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(GameState.isState(GameState.IN_GAME)) {
						loadScoreboard(all);
						
						com.swingsword.ssengine.scoreboard.ScoreboardManager.doTitleLoop = true;
					}
				}
			}
		}, 20, 20);
	}
	
	public static String getCurrentTimer() {
		if(TimerHandler.getTimer("buytime") != null && TimerHandler.getTimer("buytime").getLeft() >= 5) {
			return ChatColor.RED + "" + ChatUtils.formatTime(TimerHandler.getTimer("buytime").getLeft() - 5);
			
		} else if(TimerHandler.getTimer("roundtime") != null && TimerHandler.getTimer("roundtime").getLeft() >= 0) {
			return ChatColor.WHITE + "" + ChatUtils.formatTime(TimerHandler.getTimer("roundtime").getLeft());
			
		} else if(TimerHandler.getTimer("bombtime") != null && TimerHandler.getTimer("bombtime").getLeft() >= 0) {
			return ChatColor.WHITE + "" + ChatUtils.formatTime(TimerHandler.getTimer("bombtime").getLeft()) + " (Bomb planted)";
		}

		return "";
	}
}
