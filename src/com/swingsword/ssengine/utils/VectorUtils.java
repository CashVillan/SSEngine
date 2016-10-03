package com.swingsword.ssengine.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class VectorUtils {
	
	public static String vectorToString(Vector loc) {
		return loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
	}
	
	public static Vector stringToVector(String loc) {
		return new Vector(Integer.parseInt(loc.split(";")[0]), Integer.parseInt(loc.split(";")[1]), Integer.parseInt(loc.split(";")[2]));
	}
	
	public static BlockFace getBounceFace(Entity ent) {
		if(ent != null) {
			BlockIterator blockIterator = new BlockIterator(ent.getWorld(), ent.getLocation().toVector(), ent.getVelocity(), 0.0D, 3);
	
			Block previousBlock = ent.getLocation().getBlock();
			Block nextBlock = blockIterator.next();
			while (blockIterator.hasNext()) {
				previousBlock = nextBlock;
				nextBlock = blockIterator.next();
				
				if (nextBlock.getType() != Material.AIR) {
					return nextBlock.getFace(previousBlock);
				}
			}
		}
		
		return null;
	}
	
	public static Vector getBounceVector(Entity ent, BlockFace blockFace) {
		//Vector mirrorDirection = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
		Vector v = ent.getVelocity();
		
		if(blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
			return new Vector(v.getX(), v.getY() + 0.2f, -v.getZ()).multiply(0.4f);
		} else {
			return new Vector(-v.getX(), v.getY() + 0.2f, v.getZ()).multiply(0.4f);
		}
	}
	
	public static void applySlowGravity(Entity ent, float multiplier) {
		Vector modifiedVec = ent.getVelocity();
		
		if(ent.getVelocity().getY() < 0) {
			modifiedVec = ent.getVelocity().clone().setY(ent.getVelocity().getY() / multiplier);
			
		} else if(ent.getVelocity().getY() > 0 && ent.getLocation().getBlock().getRelative(0, -1, 0).getType() != Material.AIR) {
			modifiedVec = ent.getVelocity().clone().setY(ent.getVelocity().getY() * multiplier);
		}
		
		ent.setVelocity(modifiedVec);
	}
}
