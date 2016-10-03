package com.swingsword.ssengine.game.games.rust.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.swingsword.ssengine.utils.ConfigUtils;

public class RadiationUtils {
	
	public static ArrayList<CuboidRegion> regions = new ArrayList<CuboidRegion>();
	public static HashMap<String, Integer> playerRad = new HashMap<String, Integer>();
	
	public static void loadRadiation() {
		for (String all : ConfigUtils.getConfig("zones").getStringList("radZones")) {
			com.sk89q.worldedit.Vector loc1 = LocationUtils.locationFromString(all.split(";")[0]);
			com.sk89q.worldedit.Vector loc2 = LocationUtils.locationFromString(all.split(";")[1]);

			CuboidRegion r = new CuboidRegion(loc1, loc2);
			regions.add(r);
		}
	}
	
	public static boolean isInRadZone(Location loc) {
		for (CuboidRegion r : RadiationUtils.regions) {
			if (r.contains(new com.sk89q.worldedit.Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))) {
				return true;
			}
		}
		return false;
	}
}
