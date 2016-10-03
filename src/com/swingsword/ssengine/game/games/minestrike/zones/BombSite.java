package com.swingsword.ssengine.game.games.minestrike.zones;

import java.util.List;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.swingsword.ssengine.game.games.minestrike.Minestrike;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.games.minestrike.utils.SimpleLocation;

public class BombSite {
	
	public CuboidSelection r;
	
	public BombSite(CuboidSelection r) {
		this.r = r;
	}
	
	public static void loadBombSite() {
		List<String> sites = Minestrike.plugin.getLoadedMap().getMapConfig().getStringList("bombSites");
		
		for(String all : sites) {
			if(all.split(" ").length > 2) {
				CSGOGame.sites.add(new BombSite(new CuboidSelection(Bukkit.getWorld(all.split(" ")[0]), new SimpleLocation(all.split(" ")[1]).toLocation(), new SimpleLocation(all.split(" ")[2]).toLocation())));
			} else {
				System.out.println("ERROR: NO BOMBSITE DEFINED!");
			}
		}
	}
	
	public static void addBombSite(CuboidSelection cs) {
		List<String> sites = Minestrike.plugin.getLoadedMap().getMapConfig().getStringList("bombSites");
		
		sites.add(cs.getWorld().getName() + " " + new SimpleLocation(cs.getMinimumPoint()).toString() + " " + new SimpleLocation(cs.getMaximumPoint()).toString());
		
		Minestrike.plugin.getLoadedMap().getMapConfig().set("bombSites", sites);
		Minestrike.plugin.getLoadedMap().saveMapConfig();
	}
}
