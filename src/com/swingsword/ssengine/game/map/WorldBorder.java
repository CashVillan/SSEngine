package com.swingsword.ssengine.game.map;

import org.bukkit.Location;

public class WorldBorder {

	private int minX;
	private int minZ;
	private int maxX;
	private int maxZ;
	
	public WorldBorder(String loc) {
		minX = Integer.parseInt(loc.split(";/")[0].split(";")[0]);
		minZ = Integer.parseInt(loc.split(";/")[0].split(";")[1]);
		maxX = Integer.parseInt(loc.split(";/")[1].split(";")[0]);
		maxZ = Integer.parseInt(loc.split(";/")[1].split(";")[1]);
	}

	public boolean containsBlock(Location v) {
		final double x = v.getX(), z = v.getZ();

		return x >= minX && x < maxX + 1 && z >= minZ && z < maxZ + 1;
	}
}