package com.swingsword.ssengine.game.games.minestrike.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SimpleLocation {

	private String world;
	private double x;
	private double y;
	private double z;
	
	public SimpleLocation(Location loc) {
		this.world = loc.getWorld().getName();
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
	}
	
	public SimpleLocation(World world, double x, double y, double z) {
		this.world = world.getName();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public SimpleLocation(String string) {
		this.world = string.split(";")[0];
		this.x = Double.parseDouble(string.split(";")[1]);
		this.y = Double.parseDouble(string.split(";")[2]);
		this.z = Double.parseDouble(string.split(";")[3]);
	}
	
	public World getWorld() {
		return Bukkit.getWorld(world);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public Location toLocation() {
		return new Location(getWorld(), x, y, z);
	}
	
	public Block getBlock() {
		return getWorld().getBlockAt((int) x, (int) y, (int) z);
	}
	
	public String toString() {
		return world + ";" + x + ";" + y + ";" + z;
	}
}
