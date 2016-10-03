package com.swingsword.ssengine.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

	public static String locationToString(Location loc) {
		return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
	}
	
	public static Location stringToLocation(String loc) {
		return new Location(Bukkit.getWorld(loc.split(";")[0]), Integer.parseInt(loc.split(";")[1]), Integer.parseInt(loc.split(";")[2]), Integer.parseInt(loc.split(";")[3]));
	}
}
