package com.swingsword.ssengine.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.achievements.Achievement;
import com.swingsword.ssengine.achievements.AchievementManager;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerAccount;
import com.swingsword.ssengine.player.PlayerProfileSettings;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.stats.StatManager;

public class StatsUtils {

	public static void openStatsInventory(final Player player, final String target) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			@Override
			@SuppressWarnings("deprecation")
			public void run() {
				if (SQLManager.getSQL("global").accountExists(target)) {
					if (Bukkit.getOfflinePlayer(target).isOnline()) {
						Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
							public void run() {
								player.openInventory(getStatsInventory(player, target, PlayerSessionManager.getSession(Bukkit.getPlayer(target)).getAccount().getCache()));	
							}
						});
						return;
						
					} else {
						final PlayerAccount acc = new PlayerAccount(SQLManager.getSQL("global").getUUID(target));
						
						if (player.isOnline()) {
							Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									player.openInventory(getStatsInventory(player, target, acc.getCache()));
								}
							});
						}
					}
					return;
				}
				player.sendMessage(ChatColor.RED + "Player not registered.");
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	public static Inventory getStatsInventory(Player viewer, String player, HashMap<String, Object> playerCache) {
		final Inventory inv = Bukkit.createInventory(null, 54, "Achievements & Stats");
		HashMap<String, String> stats = StatManager.getStats(playerCache);
		
		ItemStack playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(viewer, "Return to your Profile"), null);
		if(!player.equals(playerCache.get("name") + "")) {
			playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(viewer, "Return to ") + playerCache.get("name") + "'s " + LanguageUtils.translate(viewer, "Profile"), null);
			
		} else {
			if(PlayerProfileSettings.fromString((String) playerCache.get("profileSettings")).getSetting("am").equals("1")) {
				inv.setItem(8, ItemUtils.createItem(Material.SIGN, 1, 0, ChatColor.WHITE + "" + ChatColor.BOLD + "Click to disable achievement chat notifications", null));
			} else {
				inv.setItem(8, ItemUtils.createItem(Material.SIGN, 1, 0, ChatColor.WHITE + "" + ChatColor.BOLD + "Click to enable achievement chat notifications", null));
			}
		}
		
		SkullMeta meta = (SkullMeta) playerHead.getItemMeta(); meta.setOwner(playerCache.get("name") + ""); playerHead.setItemMeta(meta);
		inv.setItem(4, playerHead);
		
		inv.setItem(0, ItemUtils.createItem(Material.getMaterial(175), 1, 0, ChatColor.WHITE + "" + ChatColor.BOLD + "Gamerscore", Arrays.asList(ChatColor.WHITE + "" + playerCache.get("gamerscore") + "")));
		
		inv.setItem(22, ItemUtils.createItem(Material.BEACON, 1, 0, ChatColor.AQUA + "Global", getStats("g", stats, true)));

		inv.setItem(18, ItemUtils.createItem(Material.TNT, 1, 0, ChatColor.AQUA + "CounterStrike", getStats("cs", stats, true)));
		inv.setItem(20, ItemUtils.createItem(Material.WORKBENCH, 1, 0, ChatColor.AQUA + "BuildIt", getStats("bi", stats, true)));
		inv.setItem(24, ItemUtils.createItem(Material.STONE_AXE, 1, 0, ChatColor.AQUA + "Rust", getStats("rt", stats, true)));
		inv.setItem(26, ItemUtils.createItem(Material.SLIME_BALL, 1, 0, ChatColor.AQUA + "Agar", getStats("ar", stats, true)));

		inv.setItem(36, ItemUtils.createItem(Material.WEB, 1, 0, ChatColor.WHITE + "Nothing here.", null));
		inv.setItem(38, ItemUtils.createItem(Material.WEB, 1, 0, ChatColor.WHITE + "Nothing here.", null));
		inv.setItem(40, ItemUtils.createItem(Material.WEB, 1, 0, ChatColor.WHITE + "Nothing here.", null));
		inv.setItem(42, ItemUtils.createItem(Material.WEB, 1, 0, ChatColor.WHITE + "Nothing here.", null));
		inv.setItem(44, ItemUtils.createItem(Material.WEB, 1, 0, ChatColor.WHITE + "Nothing here.", null));

		return inv;
	}
	
	public static void openGamemodeInventory(final Player player, final String gamemode, final String target) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			@Override
			@SuppressWarnings("deprecation")
			public void run() {
				if (SQLManager.getSQL("global").accountExists(target)) {
					if (Bukkit.getOfflinePlayer(target).isOnline()) {
						Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
							public void run() {
								player.openInventory(getGamemodeInventory(gamemode, PlayerSessionManager.getSession(Bukkit.getPlayer(target)).getAccount().getCache()));
							}
						});
						return;
						
					} else {
						final PlayerAccount acc = new PlayerAccount(SQLManager.getSQL("global").getUUID(target));
						
						if (player.isOnline()) {
							Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									player.openInventory(getGamemodeInventory(gamemode, acc.getCache()));
								}
							});
						}
					}
					return;
				}
				player.sendMessage(ChatColor.RED + "Player not registered.");
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	public static Inventory getGamemodeInventory(String gamemode, HashMap<String, Object> cache) {
		String title = gamemode + " - " + cache.get("name") + "";
		if(title.length() > 32) {
			title = title.substring(0, 31);
		}
		
		final Inventory inv = Bukkit.createInventory(null, 54, title);
		HashMap<String, String> stats = StatManager.getStats(cache);
		
		inv.setItem(4, ItemUtils.createItem(Material.BED, 1, 0, ChatColor.WHITE + "" + ChatColor.BOLD + "Go back", null));
		inv.setItem(13, ItemUtils.createItem(Material.BOOK, 1, 0, ChatColor.WHITE + "" + ChatColor.BOLD + "Stats", getStats(getPrefix(gamemode), stats, false)));
		
		List<Achievement> achievements = AchievementManager.getAchievementsWithPrefix(getPrefix(gamemode));
		for(int x = 0; x < achievements.size(); x++) {
			int modifier = x >= 5 ? 8 : 0;
			int slot = 18 + modifier + (x * 2);
			
			Material mat = Material.GLASS_BOTTLE;
			ChatColor color = ChatColor.RED;
			if(achievements.get(x).meetsRequirements(stats)) {
				mat = Material.getMaterial(373);
				color = ChatColor.GREEN;
			}
			
			inv.setItem(slot, ItemUtils.createItem(mat, achievements.get(x).reward, 0, color + achievements.get(x).name, Arrays.asList(ChatColor.WHITE + achievements.get(x).lore, "",  ChatColor.GRAY + "" + achievements.get(x).reward + " Gamerscore Points")));
		}
		
		return inv;
	}
	
	public static String getPrefix(String gamemode) {
		switch (gamemode.toLowerCase()) {
		case "counterstrike":
			return "cs";
		case "buildit":
			return "bi";
		case "global":
			return "g";
		case "agar":
			return "ar";
		case "rust":
			return "rt";
		}
		
		return gamemode;
	}
	
	public static List<String> getStats(String prefix, HashMap<String, String> stats, boolean manyDetails) {
		List<String> lore = new ArrayList<String>();
		
		for(String all : stats.keySet()) {
			if(all.split("_")[0].equals(prefix)) {
				String key = WordUtils.capitalize(all.replaceFirst(all.split("_")[0] + "_", "").replace("_", " "));
				String value = stats.get(all);
				if(all.contains("time")) {
					if(value == null) { value = "0"; }
					value = StringUtils.getTimeString(Integer.parseInt(value));
				}
				
				lore.add(ChatColor.GRAY + key + ": " + ChatColor.WHITE + value);
			}
		}
		for(String all : defaultStats) {
			if(!stats.containsKey(all)) {
				if(all.split("_")[0].equals(prefix)) {
					String key = WordUtils.capitalize(all.replaceFirst(all.split("_")[0] + "_", "").replace("_", " "));
					String value = "0";
					if(all.contains("time")) {
						value = StringUtils.getTimeString(Integer.parseInt(value));
					}
					
					lore.add(ChatColor.GRAY + key + ": " + ChatColor.WHITE + value);
				}
			}
		}
		
		if(lore.size() == 0) {
			lore.add(ChatColor.RED + "No stats about this gamemode");
		}
		
		if(manyDetails) {
			lore.add("");
			
			for(Achievement all : AchievementManager.getAchievementsWithPrefix(prefix)) {
				if(all.meetsRequirements(stats)) {
					lore.add(ChatColor.GREEN + all.name);
				} else {
					lore.add(ChatColor.RED + all.name);
				}
			}
			
			lore.add("");
			lore.add(ChatColor.YELLOW + "Click for more info!");
		}
		
		return lore;
	}
	
	public static List<String> defaultStats = Arrays.asList(
		"g_time_ingame",
		"g_games_played",
		"g_wins",
		
		"ar_food_eaten",
		"ar_cells_eaten",
		"ar_time_alive",
		"ar_highest_mass",
		"ar_viruses_eaten",
		
		"rt_time_played",
		"rt_kills",
		"rt_deaths",
		"rt_headshots",
		"rt_mobs_killed",
		"rt_items_crafted",
		"rt_airdrops_called",
		"rt_airdrops_looted",
		"rt_crates_looted"
		
		
	);		
}
