package com.swingsword.ssengine.game.games.minestrike.game;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.team.Team;

public class RewardManager {

	public static String lastWonTeam = "";
	public static int winStreak = 0;
	
	public static void reward(Player player, int reward, String reason) {
		CSGOGame.Money.put(player.getName(), CSGOGame.Money.get(player.getName()) + reward);
		if(CSGOGame.Money.get(player.getName()) > 16000) {
			CSGOGame.Money.put(player.getName(), 16000);
		}
		
		player.sendMessage(ChatColor.GREEN + "+$" + reward + ChatColor.RESET + ": Award for " + reason + ".");
	}
	
	public static void rewardEndRound(Team winTeam, String winMethod) {
		int reward = 0;
		
		if(winMethod.equals("TIME_UP") || winMethod.equals("ALL_DEAD")) {
			reward =  3250;
			
		} else if(winMethod.equals("DEFUSE") || winMethod.equals("EXPLODE")) {
			reward =  3500;
		}
		
		for(Player won : winTeam.getPlayers()) {
			reward(won, reward, "winning the round");
		}
		
		if(winTeam.getName().equals("CT")) {
			for(Player lost : Team.getTeam("T").getPlayers()) {
				if(winMethod.equals("TIME_UP")) {
					if(DeathManager.dead.contains(lost.getName())) {
						reward(lost, getLoseReward(winStreak), "losing the round");
					} else {
						lost.sendMessage(ChatColor.RED + "0" + ChatColor.WHITE + ": No income for running out of time and surviving.");
					}
					
				} else {
					reward(lost, getLoseReward(winStreak), "losing the round");
				}
			}
		} else {
			for(Player lost : Team.getTeam("CT").getPlayers()) {
				reward(lost, getLoseReward(winStreak), "losing the round");
			}
		}
	}
	
	public static int getLoseReward(int loseStreak) {
		if(winStreak == 1) {
			return 1400;
		} else if(winStreak == 2) {
			return 1900;
		} else if(winStreak == 3) {
			return 2400;
		} else if(winStreak == 4) {
			return 2900;
		} else {
			return 3400;
		}
	}
}
