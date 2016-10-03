package com.swingsword.ssengine.game.games.rust.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.utils.ConfigUtils;

public class Building implements Listener {
	

	public static ConfigManager configmanager = new ConfigManager();
	public final Foundation f = new Foundation(this);
	public final static FoundationManager fm = new FoundationManager();
	public final static PropManager pm = new PropManager();

	public static HashMap<String, String> oldLocation = new HashMap<String, String>();
	public static HashMap<String, Set<String>> playerBlocks = new HashMap<String, Set<String>>();
	public static HashMap<String, String> playerBuildLoc = new HashMap<String, String>();
	public static HashMap<String, Boolean> canPlace = new HashMap<String, Boolean>();

	public static void saveBuilding() {
		for(int x = 0; x < configmanager.configs.size(); x++) {
			FileConfiguration all = configmanager.configs.get(x);
			
			ConfigUtils.saveConfig(all, "data/container" + (x + 1));
		}
	}
	
	public static void loadBuilding() {
		configmanager.loadConfigs();
		
		for(String all : ConfigUtils.getConfig("zones").getStringList("noBuildZones")) {
			com.sk89q.worldedit.Vector loc1 = LocationUtils.locationFromString(all.split(";")[0]);
			com.sk89q.worldedit.Vector loc2 = LocationUtils.locationFromString(all.split(";")[1]);
			
			CuboidRegion r = new CuboidRegion(loc1, loc2);
			noBuildZones.add(r);
		}
		
		for(FileConfiguration config : configmanager.configs) {
			if(config.contains("foundations")) {
				for(String all : config.getConfigurationSection("foundations").getKeys(false)) {
					fm.loadFoundation(config, LocationUtils.RealLocationFromString(all));
				}
			}
		}
	}
	
	//NoBuild regions 
	
	public static List<CuboidRegion> noBuildZones = new ArrayList<CuboidRegion>();
	
	public static boolean canBuildThere(Location loc) {
		if(loc != null) {
			com.sk89q.worldedit.Vector vec = new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ());
			
			for(CuboidRegion all : noBuildZones) {
				if(all.contains(vec)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean holdingBuildItem(Player player) {
		if(player.getItemInHand().getType() == Material.getMaterial(264) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Foundation".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(266) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Pillar".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(281) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Wall".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(287) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Doorway".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(288) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Window".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(377) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Stairs".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(296) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Ceiling".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(336) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Foundation".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(338) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Pillar".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(353) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Wall".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(371) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Doorway".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(372) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Window".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(378) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Stair".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
		} else if(player.getItemInHand().getType() == Material.getMaterial(388) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Ceiling".replace("&", ChatColor.COLOR_CHAR + ""))) {
			return true;
			
			//Chest
		} else if(player.getItemInHand().getType() == Material.CHEST && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Large Wood Storage Box")) {
			return true;
		
		} else if(player.getItemInHand().getType() == Material.CHEST && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Wood Storage Box")) {
			return true;
		}
		
		return false;
	}
}

