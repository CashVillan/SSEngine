package com.swingsword.ssengine.stats;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.achievements.Achievement;
import com.swingsword.ssengine.achievements.AchievementManager;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.utils.StringUtils;

public class StatManager {

	private static HashMap<Player, HashMap<String, String>> stats = new HashMap<Player, HashMap<String, String>>();
	
	public StatManager() {
		try {
			for(Player all : Bukkit.getOnlinePlayers()) {
				loadPlayerStats(all);
			}
			
			Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					for(Player all : Bukkit.getOnlinePlayers()) {
						addStat(all, "g_time_ingame", 1);
					}
				}
			}, 20 * 60, 20 * 60);
		} catch (Exception e) { }
	}
	
	public static boolean statsLoaded(Player player) {
		return stats.containsKey(player);
	}
	
	public static HashMap<String, String> getStats(HashMap<String, Object> playerCache) {
		if(gamesDBAccessable()) {
			if(Bukkit.getOfflinePlayer(UUID.fromString(playerCache.get("uuid") + "")).isOnline()) {
				Player player = Bukkit.getPlayer(UUID.fromString(playerCache.get("uuid") + ""));
			
				if(statsLoaded(player)) {
					return stats.get(player);
				}	
			}
			
			return StringUtils.stringToMap((String) SQLManager.getSQL("games").getValue(UUID.fromString(playerCache.get("uuid") + ""), "stats"));
		}
		return null;
	}
	
	public static void loadPlayerStats(final Player player) {
		if(gamesDBAccessable()) {
			HashMap<String, String> fetchedStats = StringUtils.stringToMap((String) SQLManager.getSQL("games").getValue(player.getUniqueId(), "stats"));
			
			stats.put(player, fetchedStats);
		}
	}
	
	public static int getStat(Player player, String key) {
		if(gamesDBAccessable()) {
			HashMap<String, Object> cache = PlayerSessionManager.getSession(player).getAccount().getCache();
			
			if(getStats(cache).containsKey(key)) {
				return Integer.parseInt((String) getStats(cache).get(key));
			} else {
				return 0;
			}
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public static void addStat(Player player, String key, int increment) {
		if(gamesDBAccessable()) {
			HashMap<String, String> oldStats = null;
			if(statsLoaded(player)) {
				oldStats = (HashMap<String, String>) stats.get(player).clone();
			}
						
			if(!statsLoaded(player)) {
				loadPlayerStats(player);
			}
			
			if(statsLoaded(player)) {
				if(!stats.get(player).containsKey(key)) {
					stats.get(player).put(key, increment + "");
				} else {
					stats.get(player).put(key, (Integer.parseInt(stats.get(player).get(key)) + increment) + "");
				}
			}
			
			for(Achievement all : AchievementManager.getAchievements()) {
				if(all.meetsRequirements(stats.get(player)) && oldStats != null && !all.meetsRequirements(oldStats)) {
					all.awardAchievement(player);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void setStat(Player player, String key, int amount) {
		if(gamesDBAccessable()) {
			HashMap<String, String> oldStats = null;
			if(statsLoaded(player)) {
				oldStats = (HashMap<String, String>) stats.get(player).clone();
			}
			
			if(!statsLoaded(player)) {
				loadPlayerStats(player);
			}
			
			if(statsLoaded(player)) {
				stats.get(player).put(key, amount + "");
			}
			
			for(Achievement all : AchievementManager.getAchievements()) {
				if(all.meetsRequirements(stats.get(player)) && oldStats != null && !all.meetsRequirements(oldStats)) {
					all.awardAchievement(player);
				}
			}
		}
	}
	
	public static void saveStatChanges(final Player player, boolean shutdown) {
		if(gamesDBAccessable()) {
			if(statsLoaded(player)) {
				if(!shutdown) {
					Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
						public void run() {
							SQLManager.getSQL("games").setValue(player.getUniqueId(), "stats", StringUtils.mapToString(stats.get(player)));	
							
							forceQuit(player);
						}
					});
					
				} else {
					SQLManager.getSQL("games").setValue(player.getUniqueId(), "stats", StringUtils.mapToString(stats.get(player)));	
					
					forceQuit(player);
				}
			}
		}
	}
	
	public static void saveAllStats(boolean shutdown) {
		for(Player all : Bukkit.getOnlinePlayers()) {
			saveStatChanges(all, shutdown);
		}
	}
	
	public static HashMap<String, String> syncStats(HashMap<String, String> map1, HashMap<String, String> map2) {
		for(String key : map2.keySet()) {
			if(map1.containsKey(key)) {
				map1.put(key, (Integer.parseInt(map1.get(key)) + Integer.parseInt(map2.get(key))) + "");
			} else {
				map1.put(key, Integer.parseInt(map2.get(key)) + "");
			}
		}
		
		return map1;
	}
	
	public static void forceQuit(Player player) {
		stats.remove(player);
	}
	
	public static boolean gamesDBAccessable() {
		return (SQLManager.getSQL("games") != null && SQLManager.getSQL("games").isConnected());
	}
}
