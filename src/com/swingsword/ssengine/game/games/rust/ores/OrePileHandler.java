package com.swingsword.ssengine.game.games.rust.ores;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;

import com.sk89q.worldedit.regions.CuboidRegion;

public class OrePileHandler {
    
    public ArrayList<OrePile> orePiles = new ArrayList<OrePile>();
    
    public void createOrePile(Location center, Material type, CuboidRegion r) {
    	orePiles.add(new OrePile(center, null, type, r));
    }
    
    @SuppressWarnings("deprecation")
	public void resetPile(OrePile p) {
		for(Location all : p.blocks) {
			all.getChunk().load();
			
			all.getWorld().playEffect(all, Effect.STEP_SOUND, all.getBlock().getTypeId(), 30);
			all.getBlock().setType(Material.AIR);
		}
		
		for(int x = -5; x <= 5; x++) {
			for(int y = -5; y <= 5; y++) {
				for(int z = -5; z <= 5; z++) {
					if(p.getBlocks().get(0).getBlock().getRelative(x, y, z).getType() == Material.DIRT) {
						p.getBlocks().get(0).getBlock().getRelative(x, y, z).setType(Material.GRASS);
					}
				}
			}
		}
		p.blocks.clear();
		
		orePiles.remove(p);
	}
    
    public OrePile getOrePile(Location loc) {
    	for(OrePile all : orePiles) {
    		if(all.getBlocks().contains(loc.getBlock().getLocation())) {
    			return all;
    		}
    	}
		return null;
    }
}
