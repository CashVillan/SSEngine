package com.swingsword.ssengine.game.games.rust.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.StringUtils;

import net.techcable.npclib.NPC;

public class SpawnUtils {
	
	public static ArrayList<String> dead = new ArrayList<String>();
	public static ArrayList<Player> oldDeaths = new ArrayList<Player>();	
	public static ArrayList<Location> homeDelay = new ArrayList<Location>();
	
	public static HashMap<String, NPC> playerNPC = new HashMap<String, NPC>();
	public static HashMap<Location, Inventory> crates = new  HashMap<Location, Inventory>();
	public static HashMap<Location, Integer> crateTimeout = new HashMap<Location, Integer>();
	
	@SuppressWarnings("deprecation")
	public static void restartSpawns() {
		Iterator<String> i = dead.iterator();
		while(i.hasNext()) {
			Player all = Bukkit.getPlayer(i.next());
		
			if(getRandomSpawn() != null) {
				respawnPlayer(all, getRandomSpawn());
			}
		}
		
		for (Location all : crates.keySet()) {
			all.getBlock().setType(Material.AIR);
			all.getBlock().setData((byte) 0);
		}
	}

	public static void loadSpawns() {
		FileConfiguration deadPlayers = ConfigUtils.getConfig("data/deadPlayers");
				
		if(!deadPlayers.contains("deadPlayers")) {
			if(!deadPlayers.contains("deadPlayers")) {
				deadPlayers.createSection("deadPlayers");
				ConfigUtils.saveConfig(deadPlayers, "data/deadPlayers");
			}
		}
		
		if(!deadPlayers.contains("playerHomes")) {
			if(!deadPlayers.contains("playerHomes")) {
				deadPlayers.createSection("playerHomes");
				ConfigUtils.saveConfig(deadPlayers, "data/playerHomes");
			}
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				ArrayList<Location> remove = new ArrayList<Location>();
				
				for(Location all : crates.keySet()) {
					if(ItemUtils.getContentAmount((Inventory) crates.get(all)) == 0) {
						ArrayList<HumanEntity> allEnt = new ArrayList<HumanEntity>();
						for (HumanEntity ent : ((Inventory) crates.get(all)).getViewers()) {
							allEnt.add(ent);
						}
						for (HumanEntity ent : allEnt) {
							ent.closeInventory();
						}

						remove.add(all);
					}
				}
				for(Location all : remove) {
					crates.remove(all);
					crateTimeout.remove(all);
					all.getBlock().setType(Material.AIR);
					all.getBlock().setData((byte) 0);
				}
			}
		}, 5L, 5L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				ArrayList<Location> removeCrates = new ArrayList<Location>();

				for (Location all : crateTimeout.keySet()) {
					if (((Integer) crateTimeout.get(all)).intValue() - 1 == 0) {
						removeCrates.add(all);
					} else {
						crateTimeout.put(all, Integer.valueOf(((Integer) crateTimeout.get(all)).intValue() - 1));
					}
				}

				for (Location all : removeCrates) {
					crates.remove(all);
					crateTimeout.remove(all);
					all.getBlock().setType(Material.AIR);
					all.getBlock().setData((byte) 0);
				}
			}
		}, 20L, 20L);
	}
	
	public static Inventory getInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, 9, "Select Option");
		
		ItemStack r = new ItemStack(Material.PAINTING);
		ItemMeta rm = r.getItemMeta();
		rm.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Respawn");
		List<String> rlore = new ArrayList<String>();
		rlore.add("");
		rlore.add(ChatColor.YELLOW + "Click to respawn at a random location.");
		rm.setLore(rlore);
		r.setItemMeta(rm);
		inv.setItem(0, r);
		
		ItemStack home = new ItemStack(Material.BED);
		ItemMeta homem = home.getItemMeta();
		homem.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "At A Camp");
		List<String> hlore = new ArrayList<String>();
		List<String> homes = getHomes(player);
		hlore.add(ChatColor.GRAY + "You can only use a home once every 3 minutes.");
		hlore.add(ChatColor.GRAY + "" + getAvailableHomes(player, homes) + "/" + homes.size() + " homes available");
		hlore.add("");
		if (getAvailableHomes(player, homes) != 0) {
			hlore.add(ChatColor.YELLOW + "Click to respawn at your home.");
		} else {
			hlore.add(ChatColor.RED + "No available homes.");
		}
		
		
		homem.setLore(hlore);
		home.setItemMeta(homem);
		inv.setItem(8, home);
		
		return inv;
	}
	
	public static Integer getAvailableHomes(Player player, List<String> homes) {
		Integer output = 0;
		
		for (String home : homes) {
			if (homeNotUsed(home)) {
				output = output + 1;
			}
		}
		return output;
	}
	
	public static Location getRandomSpawn() {
		List<String> spawns = ConfigUtils.getConfig("zones").getStringList("spawns");
		Random r = new Random();
		
		if(spawns.size() > 0) {
			return LocationUtils.RealLocationFromString(spawns.get(r.nextInt(spawns.size())));
			
		} else {
			return null;
		}
	}
	
	public static Location getRandomHome(Player player) {
		List<String> spawns = getHomes(player);
		Random r = new Random();
		
		List<String> remove = new ArrayList<String>();
		for(String spawn : spawns) {
			if(LocationUtils.RealLocationFromString(spawn).getBlock().getRelative(0, -1, 0).getType() != Material.BED_BLOCK && LocationUtils.RealLocationFromString(spawn).getBlock().getType() != Material.CARPET || !homeNotUsed(spawn)) {
				remove.add(spawn);
			}
		}
		for(String all : remove) {
			spawns.remove(all);
		}
		
		if(spawns.size() > 0) {
			return LocationUtils.RealLocationFromString(spawns.get(r.nextInt(spawns.size())));
			
		} else {
			return null;
		}
	}
	
	public static void addHome(Player player, Location loc) {
		FileConfiguration config = ConfigUtils.getConfig("data/playerHomes");
		
		List<String> spawns = config.getStringList("playerHomes." + player.getUniqueId().toString());
		spawns.add(LocationUtils.RealLocationToString(loc));
		config.set("playerHomes." + player.getUniqueId().toString(), spawns);

		ConfigUtils.saveConfig(config, "data/playerHomes");
	}

	public static void removeHome(Player player, Location loc) {
		FileConfiguration config = ConfigUtils.getConfig("data/playerHomes");
		if (player != null) {
			List<String> homes = SpawnUtils.getHomes(player);

			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					if(loc.clone().add(x, 0, z).getBlock().getType() == Material.CARPET || loc.clone().add(x, 0, z).getBlock().getType() == Material.BED_BLOCK) {
						homes.remove(LocationUtils.RealLocationToString(loc.clone().add(x, 0, z)));
						loc.clone().add(x, 0, z).getBlock().setType(Material.AIR);
					}
				}
			}
			
			config.set("playerHomes." + player.getUniqueId().toString(), homes);
			
			StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT" ,ChatColor.GRAY + "Home Removed!");
		} else {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					if(loc.clone().add(x, 0, z).getBlock().getType() == Material.CARPET || loc.clone().add(x, 0, z).getBlock().getType() == Material.BED_BLOCK) {
						String locString = LocationUtils.RealLocationToString(loc.clone().add(x, 0, z));
						loc.clone().add(x, 0, z).getBlock().setType(Material.AIR);

						for (String homeOwner : config.getConfigurationSection("playerHomes").getKeys(false)) {
							List<String> homes = config.getStringList("playerHomes." + homeOwner);
							if (homes.remove(locString)) {
								config.set("playerHomes." + homeOwner, homes);
							}
						}
					}
				}
			}
		}
		
		ConfigUtils.saveConfig(config, "data/playerHomes");
	}
	
	public static List<String> getHomes(Player player) {
		return ConfigUtils.getConfig("data/playerHomes").getStringList("playerHomes." + player.getUniqueId().toString());
	}
	
	public static void respawnPlayer(Player player, Location loc) {
		dead.remove(player.getName());
		for(Player all : Bukkit.getOnlinePlayers()) {
			all.showPlayer(player);
		}
		player.setCanPickupItems(true);
		player.closeInventory();
		
		player.teleport(loc.clone().add(0.5, 0.5, 0.5));
		
		equipPlayer(player);
		
		RadiationUtils.playerRad.put(player.getName(), 0);
		Rust.bleeding.remove(player.getName());
		PropUtils.playerFood.put(player.getName(), 1500);
		player.setCanPickupItems(true);
		player.setHealth(player.getMaxHealth());
	}

	public static void processDeath(final Player player) {
		player.setHealth(player.getMaxHealth());

		player.setCanPickupItems(false);

		if (!player.getOpenInventory().getTopInventory().getTitle().contains("Select Option")) {
			player.openInventory(getInventory(player));
		}

		if(!dead.contains(player.getName())) {
			dead.add(player.getName());
		}
		for(Player all : Bukkit.getOnlinePlayers()) {
			all.hidePlayer(player);
		}
	}

	public static void equipPlayer(Player player) {
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		player.getInventory().setHeldItemSlot(0);
		
		ItemStack rock = new ItemStack(Material.WOOD_AXE);
		ItemMeta rm = rock.getItemMeta();
		rm.setDisplayName(ChatColor.WHITE + "Rock");
		rock.setItemMeta(rm);
		
		ItemStack bandage = new ItemStack(Material.BOOK, 2);
		ItemMeta bm = bandage.getItemMeta();
		bm.setDisplayName(ChatColor.WHITE + "Bandage");
		bandage.setItemMeta(bm);
		
		ItemStack torch = new ItemStack(Material.TORCH, 64);
		
		player.getInventory().addItem(rock);
		player.getInventory().addItem(bandage);
		player.getInventory().addItem(torch);
	}
	
	public static boolean homeNotUsed(String loc) {
		for(Location all : homeDelay) {
			if(LocationUtils.RealLocationToString(all).equalsIgnoreCase(loc)) {
				return false;
			}
		}
		return true;
	}
}
