package com.swingsword.ssengine.game.games.minestrike.zones;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.swingsword.ssengine.game.games.minestrike.Minestrike;
import com.swingsword.ssengine.game.games.minestrike.utils.SimpleLocation;

public class BuyZone {

	public static List<BuyZone> buyZones = new ArrayList<BuyZone>();
	
	public CuboidSelection r;
	
	public BuyZone(CuboidSelection r) {
		this.r = r;
	}
	
	public static void loadBuyZones() {
		List<String> configBuyZones = Minestrike.plugin.getLoadedMap().getMapConfig().getStringList("buyZones");
		
		for(String all : configBuyZones) {
			buyZones.add(new BuyZone(new CuboidSelection(Bukkit.getWorld(all.split(" ")[0]), new SimpleLocation(all.split(" ")[1]).toLocation(), new SimpleLocation(all.split(" ")[2]).toLocation())));
		}
	}
	
	public static void addBuyZone(CuboidSelection cs) {
		List<String> configBuyZones = Minestrike.plugin.getLoadedMap().getMapConfig().getStringList("buyZones");
		configBuyZones.add(cs.getWorld().getName() + " " + new SimpleLocation(cs.getMinimumPoint()).toString() + " " + new SimpleLocation(cs.getMaximumPoint()).toString());
		
		Minestrike.plugin.getLoadedMap().getMapConfig().set("buyZones", configBuyZones);
		Minestrike.plugin.getLoadedMap().saveMapConfig();
	}
}
