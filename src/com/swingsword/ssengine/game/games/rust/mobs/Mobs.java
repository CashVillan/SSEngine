package com.swingsword.ssengine.game.games.rust.mobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.utils.ConfigUtils;

public class Mobs {
	
	public static List<Region> regions = new ArrayList<Region>();
    public static HashMap<Location, Inventory> crates = new HashMap<Location, Inventory>();
	public static HashMap<Location, Integer> crateTimeout = new HashMap<Location, Integer>();
	
	public static void removeMobs() {
		for(World world : Bukkit.getWorlds()) {
			for(Entity all : world.getEntities()) {
				if(all.getType() != EntityType.PLAYER) {
					all.remove();
				}
			}
		}
		
		unload();
	}
	
	public static void loadMobs() {
		ConfigUtils.updateType("mobs");
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(ConfigUtils.getConfig("zones").contains("mobZones")) {
					for(String all : ConfigUtils.getConfig("zones").getConfigurationSection("mobZones").getKeys(false)) {
						com.sk89q.worldedit.Vector loc1 = LocationUtils.locationFromString(all.split(";")[0]);
						com.sk89q.worldedit.Vector loc2 = LocationUtils.locationFromString(all.split(";")[1]);
						
						CuboidRegion r = new CuboidRegion(loc1, loc2);
						
						regions.add(new Region(r, "map", ConfigUtils.getConfig("zones").getString("mobZones." + all + ".mobs"), ConfigUtils.getConfig("zones").getInt("mobZones." + all + ".amount")));
					}
				}
			}
		}, 20L);
	}
	
	public static void unload() {
		for(Location all : crates.keySet()) {
			if(all.getBlock().getType() == Material.CHEST) {
				all.getBlock().setType(Material.AIR);
			}
		}
	}
	
	public static Region getRegion(LivingEntity ent) {
		for(Region all : regions) {
			for(Mob mob : all.mobs) {
				if(mob.entity != null) {
					if(mob.entity.equals(ent)) {
						return all;
					}
				}
			}
		}
		return null;
	}
	
	public static List<Mob> getAllMobs() {
		List<Mob> mobs = new ArrayList<Mob>();
		
		for(Region all : regions) {
			mobs.addAll(all.mobs);
		}
		
		return mobs;
	}
	
    @SuppressWarnings("deprecation")
	public static void createCrate(Location loc, Inventory inv, String name) {
    	if(loc.getBlock().getType() == Material.AIR && loc.getBlock().getType() == Material.AIR) {
	    	loc.getBlock().setTypeIdAndData(54, (byte) 0, true);
	    	Inventory crateInv = Bukkit.createInventory(null, inv.getSize(), ChatColor.DARK_GRAY + name);
	    	crateInv.setContents(inv.getContents());
			crates.put(loc, crateInv);
			crateTimeout.put(loc, 60);
			
    	} else {
    		for(ItemStack all : inv.getContents()) {
    			if(all != null) {
    				loc.getWorld().dropItemNaturally(loc, all);
    			}
    		}
    	}
    }
    
	public static Inventory fillCrate(Location loc, String type) {
    	Inventory crateInv = null;
    	
    	type = type.replace(" ", "_");
    	
    	if(GameManager.currentMap.getMapConfig().contains("mobs." + type)) {
	    	if(crates.containsKey(loc)) {
	    		crateInv = crates.get(loc);
	    		
	    	} else {
	    		crateInv = Bukkit.createInventory(null, 27);
	    	}
	    	crateInv.clear();
	    		
	    	if(getRandomLoot(type) != null) {
		    	for(String all : GameManager.currentMap.getMapConfig().getStringList("mobs." + type + "." + getRandomLoot(type))) {
		    		crateInv.addItem(com.swingsword.ssengine.utils.ItemUtils.itemStackFromString(all));
		    	}
	    	}
		}
    	return crateInv;
    }
	
	public static String getRandomLoot(String animal) {
		if(GameManager.currentMap.getMapConfig().getConfigurationSection("mobs." + animal).getKeys(false).size() > 0) {
			return (String) GameManager.currentMap.getMapConfig().getConfigurationSection("mobs." + animal).getKeys(false).toArray()[(new Random().nextInt(GameManager.currentMap.getMapConfig().getConfigurationSection("mobs." + animal).getKeys(false).size()))];
	
		} else {
			return null;
		}
	}
	
	public static boolean mobIsAlive(Entity ent) {
		return ent != null && !ent.isDead() && Bukkit.getWorld("map").getLivingEntities().contains(ent);
	}
}
