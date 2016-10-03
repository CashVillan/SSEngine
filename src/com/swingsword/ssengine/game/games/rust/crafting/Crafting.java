package com.swingsword.ssengine.game.games.rust.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.utils.ItemUtils;
import com.swingsword.ssengine.stats.StatManager;
import com.swingsword.ssengine.utils.ConfigUtils;

public class Crafting {

	public static HashMap<String, ItemStack> playerCraft = new HashMap<String, ItemStack>();
	public static HashMap<String, Integer> playerCraftAmount = new HashMap<String, Integer>();
	public static Map<String, List<String>> unlocked = new HashMap<String, List<String>>();

	public static ArrayList<String> settingAmount = new ArrayList<String>();
	public static ArrayList<String> confirm = new ArrayList<String>();
	public static ArrayList<String> selecting = new ArrayList<String>();

	public static void endCrafting() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			all.closeInventory();

			if (playerCraft.containsKey(all.getName()) && !settingAmount.contains(all.getName())) {
				all.getInventory().addItem(InventoryManager.itemResult.get(playerCraft.get(all.getName())));
				playerCraft.remove(all.getName());
				playerCraftAmount.remove(all.getName());
				all.setExp(0);
			}
		}

		FileConfiguration config = ConfigUtils.getConfig("data/researched");

		for (String player : unlocked.keySet()) {
			config.set(player, unlocked.get(player));
		}
		ConfigUtils.saveConfig(config, "data/researched");
	}

	public static void loadCrafting() {
		ConfigUtils.updateType("crafting");

		InventoryManager.loadInventory();
	}

	public static void progressCraft(final Player player, final int timeLeft, final int totalTime) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if (player.isOnline() && playerCraft.containsKey(player.getName())) {
					boolean workbench = true;
					if (InventoryManager.itemWorkbench.containsKey(playerCraft.get(player.getName()))) {
						workbench = !InventoryManager.itemWorkbench.get(playerCraft.get(player.getName()));

						if (workbench == false) {
							for (int x = -2; x < 3; x++) {
								for (int y = -2; y < 3; y++) {
									for (int z = -2; z < 3; z++) {
										if (player.getLocation().getBlock().getRelative(x, y, z).getType() == Material.STONE_SLAB2) {
											workbench = true;
										}
									}
								}
							}

							if (workbench == false) {
								player.sendMessage(ChatColor.RED + "You need to be close to a workbench to craft this item.");

								for (ItemStack all : InventoryManager.itemNeeded.get(playerCraft.get(player.getName()))) {
									player.getInventory().addItem(all.clone());
								}

								playerCraft.remove(player.getName());
								playerCraftAmount.remove(player.getName());
								player.setExp(0);

								return;
							}
						}
					}

					if (timeLeft - 1 < 0) {
						player.getInventory()
								.addItem(InventoryManager.itemResult.get(playerCraft.get(player.getName())));
						player.updateInventory();

						StatManager.addStat(player, "rt_items_crafted", 1);

						if (playerCraftAmount.get(player.getName()) <= 0) {
							playerCraft.remove(player.getName());
							playerCraftAmount.remove(player.getName());
							player.sendMessage(ChatColor.GREEN + "Crafting completed!");
							player.setExp(0);

						} else {
							boolean hasItems = true;
							for (ItemStack all : InventoryManager.itemNeeded.get(playerCraft.get(player.getName()))) {
								if (!ItemUtils.containsItem(player, all.clone(), all.clone().getAmount())) {
									hasItems = false;
								}
							}
							if (hasItems) {
								for (ItemStack all : InventoryManager.itemNeeded.get(playerCraft.get(player.getName()))) {
									ItemUtils.removeItem(player, all.clone(), all.clone().getAmount());
								}
								player.setExp(1);
								if (player.hasPermission("ss.donator")) {
									progressCraft(player, (InventoryManager.itemDelay.get(InventoryManager.itemResult.get(playerCraft.get(player.getName()))) * 20) / 2 - 1, InventoryManager.itemDelay.get(InventoryManager.itemResult.get(playerCraft.get(player.getName()))) * 10);
								} else {
									progressCraft(player, InventoryManager.itemDelay.get(InventoryManager.itemResult.get(playerCraft.get(player.getName()))) * 20 - 1, InventoryManager.itemDelay.get(InventoryManager.itemResult.get(playerCraft.get(player.getName()))) * 20);
								}
								playerCraftAmount.put(player.getName(), playerCraftAmount.get(player.getName()) - 1);

							} else {
								playerCraft.remove(player.getName());
								playerCraftAmount.remove(player.getName());
								player.sendMessage(ChatColor.RED + "You don't have all the needed items to keep crafting.");
								player.setExp(0);
							}
						}

					} else {
						player.removePotionEffect(PotionEffectType.SLOW);
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3, true));
						player.removePotionEffect(PotionEffectType.JUMP);
						player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10, -5, true));
						player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10, 1, true));

						player.setExp((float) ((float) timeLeft / (float) totalTime));
						progressCraft(player, timeLeft - 1, totalTime);
					}
				}
			}
		}, 1L);
	}

	public static void confirm(final Player player) {
		player.sendMessage(
				ChatColor.GREEN + "Are you sure you want to research this blueprint? Right click again to confirm.");
		confirm.add(player.getName());

		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if (player.isOnline() && confirm.contains(player.getName())) {
					player.sendMessage(ChatColor.RED + "Confirmation timed out.");
					confirm.remove(player.getName());
				}
			}
		}, 60L);
	}

	// Config

	public static void saveAll() {
		FileConfiguration config = ConfigUtils.getConfig("data/researched");

		for (String player : unlocked.keySet()) {
			config.set(player, unlocked.get(player));
		}

		ConfigUtils.saveConfig(config, "data/researched");
	}

	public static void save(String player) {
		FileConfiguration config = ConfigUtils.getConfig("data/researched");

		config.set(player, unlocked.get(player));
		ConfigUtils.saveConfig(config, "data/researched");
	}

	public static void addResearch(String player, String research) {
		if (!unlocked.containsKey(player)) {
			unlocked.put(player, new ArrayList<String>());
		}

		List<String> res = (List<String>) unlocked.get(player);
		res.add(research);

		unlocked.put(player, res);
	}

	public static boolean hasResearched(String player, String research) {
		if (!unlocked.containsKey(player)) {
			unlocked.put(player, new ArrayList<String>());
		}

		return ((List<?>) unlocked.get(player)).contains(research);
	}

	public static void loadResearch(String player) {
		FileConfiguration config = ConfigUtils.getConfig("data/researched");

		if (config.get(player) != null) {
			unlocked.put(player, config.getStringList(player));
		} else
			unlocked.put(player, new ArrayList<String>());
	}
}