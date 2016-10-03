package com.swingsword.ssengine.game.games.rust.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.utils.ConfigUtils;

public class LootUtils {
	
	public static HashMap<Location, Inventory> crates = new HashMap<Location, Inventory>();
	
	@SuppressWarnings("deprecation")
	public static void removeLoot() {
		for(Location all : crates.keySet()) {
			if(all.getBlock().getType() == Material.getMaterial(33)) {
				all.getBlock().setType(Material.AIR);
			}
		}
	}
	
	public static void loadLoot() {
		ConfigUtils.updateType("loot");
		
		for(String all : ConfigUtils.getConfig("zones").getStringList("crates")) {
			createCrate(LocationUtils.RealLocationFromString(all), fillCrate(LocationUtils.RealLocationFromString(all)), "Loot Crate");
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				ArrayList<Location> remove = new ArrayList<Location>();
				for(Location all : crates.keySet()) {
					if(ItemUtils.getContentAmount(crates.get(all)) == 0) {
						remove.add(all);
					}
				}
				
				for(Location all : remove) {
					all.getWorld().playEffect(all, Effect.STEP_SOUND, all.getBlock().getTypeId());
					all.getBlock().setType(Material.AIR);
					crates.remove(all);
					
					spawnNewCrate(all);
				}
			}
		}, 5L, 5L);
	}
	
	public static void spawnNewCrate(final Location loc) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				createCrate(loc, fillCrate(loc), "Crate");
			}
		}, 20 * 60 * 8);
	}
	
   @SuppressWarnings("deprecation")
	public static void createCrate(Location loc, Inventory inv, String name) {
    	loc.getBlock().setType(Material.TRAPPED_CHEST);
    	loc.getWorld().playEffect(loc, Effect.STEP_SOUND, loc.getBlock().getTypeId());
    	Inventory crateInv = Bukkit.createInventory(null, inv.getSize(), ChatColor.DARK_GRAY + name);
    	crateInv.setContents(inv.getContents());
		crates.put(loc, crateInv);
    }
    
	public static Inventory fillCrate(Location loc) {
    	Inventory crateInv = null;
    	
    	if(crates.containsKey(loc)) {
    		crateInv = crates.get(loc);
    		
    	} else {
    		crateInv = Bukkit.createInventory(null, 27);
    	}
    	crateInv.clear();
    	
    	if(getRandomCrate() != null) {
	    	for(String all : GameManager.currentMap.getMapConfig().getStringList("loot." + getRandomCrate())) {
	    		crateInv.addItem(com.swingsword.ssengine.utils.ItemUtils.itemStackFromString(all));
	    	}
    	}
    	
    	return crateInv;
    }
	
	public static String getRandomCrate() {
		FileConfiguration mapConfig = GameManager.currentMap.getMapConfig();
		if(mapConfig.getConfigurationSection("loot").getKeys(false).size() > 0) {
			return (String) mapConfig.getConfigurationSection("loot").getKeys(false).toArray()[(new Random().nextInt(mapConfig.getConfigurationSection("loot").getKeys(false).size()))];
	
		} else {
			return null;
		}
	}
}
