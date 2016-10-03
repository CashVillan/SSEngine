package com.swingsword.ssengine.game.games.rust.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.regions.CuboidRegion;

public class LocationUtils {
	
	public static boolean isLocationNearBlock(Location loc, Material type, int radius) {
		final World world = loc.getWorld();

		for (int y = 1; y > -radius; y--) {
			for (int x = 1; x > -radius; x--) {
				for (int z = 1; z > -radius; z--) {
					Block scan = world.getBlockAt((int) loc.getX() + x,
							(int) loc.getY() + y, (int) loc.getZ() + z);

					if (type == scan.getType()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean nextToMaterial(Location loc, Material mat) {
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (loc.getBlock().getRelative(x, y, z).getType() == mat) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean directlyNextToMaterial(Location loc, Material mat) {
		List<Vector> locs = Arrays.asList(new Vector(1, 0, 0), new Vector(-1, 0, 0), new Vector(0, 0, 1), new Vector(0, 0, -1));
		
		for (Vector vec : locs) {
			if (loc.getBlock().getRelative(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()).getType() == mat) {
				return true;
			}
		}
		return false;
	}

	public static boolean directlyNextToMaterial(Location loc, String key) {
		List<Vector> locs = Arrays.asList(new Vector(1, 0, 0), new Vector(-1, 0, 0), new Vector(0, 0, 1), new Vector(0, 0, -1));
		
		for (Vector vec : locs) {
			if (loc.getBlock().getRelative(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()).getType().name().toLowerCase().contains(key.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	public static Location nextToMaterialLoc(Location loc, Material mat) {
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (loc.getBlock().getRelative(x, y, z).getType() == mat) {
						return loc.getBlock().getRelative(x, y, z).getLocation();
					}
				}
			}
		}
		return null;
	}
	
	public static boolean nextToMaterialHorizontal(Location loc, Material mat) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (loc.getBlock().getRelative(x, 0, z).getType() == mat) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean nextToMaterialHorizontalWithType(Location loc, Material mat, String type) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if(C4Utils.getType(loc.getBlock().getRelative(x, 0, z).getLocation()) != null) {
					if (loc.getBlock().getRelative(x, 0, z).getType() == mat && C4Utils.getType(loc.getBlock().getRelative(x, 0, z).getLocation()).contains(type)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static String RealLocationToString(Location loc) {
		return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
	}

	public static Location RealLocationFromString(String string) {
		string = string.replace(".0", "");
		
		return new Location(Bukkit.getWorld(string.split(",")[0]), Integer.parseInt(string.split(",")[1]), Integer.parseInt(string.split(",")[2]), Integer.parseInt(string.split(",")[3]));
	}

	public static String locationToString(com.sk89q.worldedit.Vector loc) {
		return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
	}
	
	public static com.sk89q.worldedit.Vector locationFromString(String string) {
		string = string.replace(".0", "");
		
		return new com.sk89q.worldedit.Vector(Integer.parseInt(string.split(",")[0]), Integer.parseInt(string.split(",")[1]), Integer.parseInt(string.split(",")[2]));
	}
	
	public static boolean XZbiggerThan(Location pos1, Location pos2) {
		return pos1.getBlockX() < pos2.getBlockX() && pos1.getBlockZ() < pos2.getBlockZ();
	}
	
	public static int getXDifference(Location pos1, Location pos2) {
		int x1 = pos1.getBlockX();
		int x2 = pos2.getBlockX();
		
		int newx1 = Math.abs(x1);
		int newx2 = Math.abs(x2);
		
		if(newx1 > newx2) {
			return newx1 - newx2;
		} else {
			return newx2 - newx1;
		}
	}
	
	public static int getZDifference(Location pos1, Location pos2) {
		int z1 = pos1.getBlockZ();
		int z2 = pos2.getBlockZ();
		
		int newz1 = Math.abs(z1);
		int newz2 = Math.abs(z2);
		
		if(newz1 > newz2) {
			return newz1 - newz2;
		} else {
			return newz2 - newz1;
		}
	}
	
	public static Location getRandomLocation(CuboidRegion r, World w) {
		Location pos1 = new Location(w, r.getMinimumPoint().getX(), r.getMinimumPoint().getY(), r.getMinimumPoint().getZ());
		Location pos2 = new Location(w, r.getMaximumPoint().getX(), r.getMaximumPoint().getY(), r.getMaximumPoint().getZ());
		Random rx = new Random();
		Random rz = new Random();
		int x = 0;
		int z = 0;
		x = rx.nextInt(getXDifference(pos1, pos2)) + pos1.getBlockX();
		z = rz.nextInt(getZDifference(pos1, pos2)) + pos1.getBlockZ();
		Location loc = new Location(w, x, w.getHighestBlockYAt(x, z), z);

		if (loc.getBlock().getType() == Material.AIR) {
			return loc;
		} else {
			return getRandomLocation(r, w);
		}
	}
	
	public static void removeNear(Location loc, Material mat) {
		if(loc.getBlock().getType() == mat) {
			loc.getBlock().setType(Material.AIR);
		}
		
		List<Location> locs = new ArrayList<Location>();
		
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				for(int z = -1; z <= 1; z++) {
					Location relLoc = loc.getBlock().getRelative(x, y, z).getLocation();
					
					if(relLoc.getBlock().getType() == mat) {
						locs.add(relLoc);
						
						relLoc.getBlock().setType(Material.AIR);
					}
				}
			}
		}
		
		for(Location locList : locs) {
			removeNear(locList, mat);
		}
	}	
}
