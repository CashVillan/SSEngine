package com.swingsword.ssengine.game.games.rust.ores;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.utils.ConfigUtils;

public class OrePile {
	
	Location center = null;
	public ArrayList<Location> blocks = new ArrayList<Location>();
	private int stages = 20;
	private int breakStage = this.stages;
	private Material mat = null;
	public CuboidRegion r;
	
	public OrePile(Location center, ArrayList<Location> blocks, Material mat, CuboidRegion r) {
		this.center = center;
		this.mat = mat;
		this.r = r;
		
		if(blocks != null) {
			this.blocks = blocks;
		}
		
		setLayer(3, this.mat);
		
		stages = GameManager.currentMap.getMapConfig().getInt("ores." + this.mat.name().toLowerCase() + ".oreHealth");
		breakStage = stages;
	}
	
	public ArrayList<Location> getBlocks() {
		return blocks;
	}
	
	public void setBlocks(ArrayList<Location> blocks) {
		this.blocks = blocks;
	}
	
	public Location getCenter() {
		return this.center;
	}
	
	public void breakLayer(int power) {
		for(Location all : blocks) {
			if(all.getBlock().getRelative(0, -1, 0).getType() == Material.DIRT) {
				all.getBlock().getRelative(0, -1, 0).setType(Material.GRASS);
			}
		}

		if((float) this.breakStage / (float) this.stages > 0.66f && (float) (this.breakStage - power) / (float) this.stages <= 0.66f) {
			for (Location all : blocks) {
				all.getWorld().playEffect(all, Effect.SMOKE, 5);
			}
			this.setLayer(2, mat);
		} 
		if((float) this.breakStage / (float) this.stages > 0.33f && (float) (this.breakStage - power) / (float) this.stages <= 0.33f) {
			for (Location all : blocks) {
				all.getWorld().playEffect(all, Effect.SMOKE, 5);
			}
			this.setLayer(1, mat);
		}
		
		this.breakStage = this.breakStage - power;
		
		if(this.breakStage <= 0) {
			for (Location all : blocks) {
				all.getWorld().playEffect(all, Effect.SMOKE, 5);
			}
			Ores.oph.resetPile(this);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					Ores.spawnRandomOre(r);
				}
			}, 20 * 60 * 10);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setLayer(int layer, Material mat) {
		ArrayList<Vector> locs = new ArrayList<Vector>();
		
		if(layer == 3) {
			locs = new ArrayList<Vector>(Arrays.asList(
					new Vector(0, 0, 0), 
	    			new Vector(0, 1, 0), 
	    			new Vector(1, 1, 0),
	    			new Vector(1, 1, 1),
	    			new Vector(1, 0, 0),
	    			new Vector(0, 0, 1),
	    			new Vector(-1, 0, 0),
	    			new Vector(0, 0, -1),
	    			new Vector(1, 0, 1),
	    			new Vector(1, 0, -1)
	    			));
			
		} else if(layer == 2) {
			locs = new ArrayList<Vector>(Arrays.asList(
					new Vector(0, 0, 0), 
	    			new Vector(0, 1, 0), 
	    			new Vector(1, 1, 0),
	    			new Vector(1, 0, 1),
	    			new Vector(1, 0, 0),
	    			new Vector(0, 0, 1),
	    			new Vector(-1, 0, 0)
	    			));
		
		} else if(layer == 1) {
			locs = new ArrayList<Vector>(Arrays.asList(
	    			new Vector(0, 0, 0), 
	    			new Vector(0, 1, 0),
	    			new Vector(0, 0, 1),
	    			new Vector(-1, 0, 0)
	    			));
		}
		
		ArrayList<Location> remove = new ArrayList<Location>();
    	for(Location all : this.blocks) {
			all.getWorld().playEffect(all, Effect.STEP_SOUND, all.getBlock().getTypeId(), 30);
			all.getBlock().setType(Material.AIR);
			all.getBlock().setData((byte) 0);
			remove.add(all);
			
			if(all.getBlock().getRelative(0, -1, 0).getType() == Material.DIRT) {
				all.getBlock().getRelative(0, -1, 0).setType(Material.GRASS);
			}
		}
    	
    	for(Location all : remove) {
			blocks.remove(all);
		}
    	
    	for(Vector all : locs) {
    		if(center.clone().add(all).getBlock().getType() == Material.AIR) {
    			blocks.add(center.clone().add(all));
    			center.clone().add(all).getBlock().setType(mat);
    			if(mat == Material.LOG) {
    				center.clone().add(all).getBlock().setData((byte) 3);
    			}
    		}
    	}
	}
}
