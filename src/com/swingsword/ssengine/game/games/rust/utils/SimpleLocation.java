package com.swingsword.ssengine.game.games.rust.utils;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.sk89q.worldedit.regions.CuboidRegion;

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
		this.world = string.split(",")[0];
		this.x = Double.parseDouble(string.split(",")[1]);
		this.y = Double.parseDouble(string.split(",")[2]);
		this.z = Double.parseDouble(string.split(",")[3]);
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
		return world + "," + x + "," + y + "," + z;
	}
	
	//
	
	public static Location getRandomLocation(CuboidRegion r, World w, int tries) {
		if(tries > 0) {
			Location pos1 = new Location(w, r.getMinimumPoint().getX(), r.getMinimumPoint().getY(), r.getMinimumPoint().getZ());
			Location pos2 = new Location(w, r.getMaximumPoint().getX(), r.getMaximumPoint().getY(), r.getMaximumPoint().getZ());
			Random rx = new Random();
			Random rz = new Random();
			int x = 0;
			int z = 0;
			x = rx.nextInt(getXDifference(pos1, pos2)) + pos1.getBlockX();
			z = rz.nextInt(getZDifference(pos1, pos2)) + pos1.getBlockZ();
			Location loc = new Location(w, x, w.getHighestBlockYAt(x, z), z);
			
			if(loc.getBlock().getType() == Material.AIR && loc.getBlock().getRelative(0, -1, 0).getType() == Material.GRASS) {
				return loc.add(0.5, 0, 0.5);
				
			} else {
				return getRandomLocation(r, w, tries - 1);
			}
		}
		return null;
	}
	
	public static int getXDifference(Location pos1, Location pos2) {
		int x1 = pos1.getBlockX();
		int x2 = pos2.getBlockX();
		
		if(x1 > x2) {
			return x1 - x2;
		} else {
			return x2 - x1;
		}
	}
	
	public static int getZDifference(Location pos1, Location pos2) {
		int z1 = pos1.getBlockZ();
		int z2 = pos2.getBlockZ();
		
		if(z1 > z2) {
			return z1 - z2;
		} else {
			return z2 - z1;
		}
	}
}
