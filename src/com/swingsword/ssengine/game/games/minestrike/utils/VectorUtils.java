package com.swingsword.ssengine.game.games.minestrike.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class VectorUtils {
	
	public static BlockFace getBounceFace(Entity ent) {
		if(ent != null) {
			BlockIterator blockIterator = new BlockIterator(ent.getWorld(), ent.getLocation().toVector(), ent.getVelocity(), 0.0D, 3);
	
			Block hitBlock = ent.getLocation().getBlock();
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
		Vector v = ent.getVelocity().clone();
		
		if(ent.getLocation().getBlock().getRelative(0, -1, 0).getType() != Material.AIR) {
			ent.teleport(ent.getLocation().clone().add(0, 0.5, 0));
			v.setY(-v.getY());
			v.multiply(0.4);
			
			return v;
		}
		
		if(blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
			v = new Vector(v.getX(), v.getY(), -v.getZ()).multiply(0.4f);
		} else {
			v = new Vector(-v.getX(), v.getY(), v.getZ()).multiply(0.4f);
		}
		
		for(int x = -1; x <= 1; x++) {
			for(int z = -1; z <= 1; z++) {
				if(ent.getLocation().clone().add(x, 0, z).getBlock().getType() != Material.AIR && !ent.getLocation().clone().add(x, 0, z).getBlock().equals(ent.getLocation().getBlock().getRelative(blockFace))) {
					if(ent.getLocation().clone().add(x, 0, z).getBlock().getType() == Material.SANDSTONE) {
						ent.getLocation().clone().add(x, 0, z).getBlock().setType(Material.BEDROCK);
					}
					
					v.add(new Vector(x / 2, 0, z / 2));
				}
			}
		}
		
		return v;
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
