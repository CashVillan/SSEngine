package com.swingsword.ssengine.utils;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class HologramUtils {

	private static HashMap<String, ArmorStand> holograms = new HashMap<String, ArmorStand>();
	
	public static void unload() {
		for(ArmorStand all : holograms.values()) {
			all.remove();
		}
	}
	
	public static void create(String id, Location loc, String text) {
		removeIfExists(id);
		
		ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		stand.setRemoveWhenFarAway(false);
		stand.setVisible(false);
		stand.setGravity(false);
		
		stand.setCustomName(text.replace("&", ChatColor.COLOR_CHAR + ""));
		stand.setCustomNameVisible(true);
		
		holograms.put(id, stand);
	}
	
	public static void update(String id, String text) {
		if(exists(id)) {
			holograms.get(id).setCustomName(text.replace("&", ChatColor.COLOR_CHAR + ""));
		}
	}
	
	public static boolean exists(String id) {
		return holograms.containsKey(id);
	}
	
	private static void removeIfExists(String id) {
		if(exists(id)) {
			holograms.get(id).remove();
			holograms.remove(id);
		}
	}
}
