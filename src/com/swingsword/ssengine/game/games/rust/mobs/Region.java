package com.swingsword.ssengine.game.games.rust.mobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.sk89q.worldedit.regions.CuboidRegion;

public class Region {
	
	List<Mob> mobs = new ArrayList<Mob>();
	public CuboidRegion region;
	String world;
	String types;
	
	public Region(CuboidRegion r, String w, String types, int amount) {
		this.region = r;
		this.world = "map";
		this.types = types;
		
		if(world == null) {
			world = "map";
		}
		
		load(amount);
	}
	
	public void load(int mobCount) {
		for(int x = 0; x < mobCount; x++) {
			Mob mob = new Mob(this, null);
			
			mobs.add(mob);
			
			mob.spawn();
		}
	}
	
	public void unload() {
		for(Mob all : mobs) {
			all.despawn();
		}
	}
	
	public Mob getMob(LivingEntity ent) {
		for(Mob all : mobs) {
			if(all.entity != null) {
				if(all.entity.equals(ent)) {
					return all;
				}
			}
		}
		return null;
	}
	
	public void reloadAliveMobs() {
		for(Mob all : mobs) {
			if(!all.isRespawning) {
				all.spawn();
			}
		}
	}
}
