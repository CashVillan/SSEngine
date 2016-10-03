package com.swingsword.ssengine.achievements;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.player.PlayerSessionManager;

public class Achievement {

	public String name;
	public String lore;
	public String prefix;
	public int reward;
	public HashMap<String, Integer> requirements = new HashMap<String, Integer>();
	
	public Achievement(String name, String lore, String prefix, HashMap<String, Integer> requirements, int reward) {
		this.name = name;
		this.lore = lore;
		this.prefix = prefix;
		this.reward = reward;
		
		if(requirements != null) {
			this.requirements = requirements;
		}
		
		AchievementManager.achievements.add(this);
	}
	
	public boolean meetsRequirements(HashMap<String, String> stats) {
		boolean allTrue = true;
		
		for(String key : requirements.keySet()) {
			if(stats != null && stats.containsKey(key) && Integer.parseInt(stats.get(key)) >= requirements.get(key)) {
			} else {
				allTrue = false;
			}
		}
		
		return allTrue;
	}
	
	public void awardAchievement(final Player player) {	
		PlayerSessionManager.getSession(player).getAccount().addGamerscore(reward);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(player.isOnline()) {
					if(PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("am") == null || PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("am").equals("1")) {
						//player.sendMessage(name + " - " + lore);
					}
				}
			}
		}, 10);
	}
}
