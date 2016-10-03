package com.swingsword.ssengine.game.games.rust.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BackpackUtils {
	
    @SuppressWarnings("deprecation")
	public static void createBackpack(Location loc, Inventory inv, String name) {
    	if(loc.getBlock().getType() == Material.AIR && loc.getBlock().getRelative(0, -1, 0).getType().isSolid() && loc.getBlock().getRelative(0, -1, 0).getType().isOccluding()) {
	    	loc.getBlock().setTypeId(171);
	    	loc.getBlock().setData((byte) 5);
	    	Inventory crateInv = Bukkit.createInventory(null, inv.getSize(), ChatColor.DARK_GRAY + name + "'s backpack");
	    	crateInv.setContents(inv.getContents());
	    	
	    	for(int x = 0; x < crateInv.getSize(); x++) {
	    		if(crateInv.getItem(x) != null) {
	    			crateInv.setItem(x, inv.getItem(x));
	    		}
	    	}
	    	
			SpawnUtils.crates.put(loc, crateInv);
			SpawnUtils.crateTimeout.put(loc, 600);
			
    	} else {
    		for(ItemStack all : inv.getContents()) {
    			if(all != null) {
    				Item item = loc.getWorld().dropItemNaturally(loc, all);
    				item.setPickupDelay(20);
    			}
    		}
    	}
    }
	
    /*@SuppressWarnings("deprecation")
	public static void createBackpack(Location loc, ItemStack[] items, String name) {
    	while(loc.getBlock().getRelative(0, -1, 0).getType() == Material.AIR && loc.getBlock().getLocation().getBlockY() > 0) {
    		loc = loc.getBlock().getRelative(0, -1, 0).getLocation();
    	}
    	
    	if(loc.getBlock().getType() == Material.AIR && loc.getBlock().getRelative(0, -1, 0).getType().isSolid() && loc.getBlock().getRelative(0, -1, 0).getType().isOccluding()) {
	    	loc.getBlock().setTypeId(171);
	    	loc.getBlock().setData((byte) 5);
	    	Inventory crateInv = Bukkit.createInventory(null, items.length, ChatColor.DARK_GRAY + name + "'s backpack");
	    	crateInv.setContents(items);
	    	
	    	//for(int x = 0; x < crateInv.getSize(); x++) {
	    	//	if(crateInv.getItem(x) != null && crateInv.getItem(x).getType() != Material.AIR) {
	    	//		crateInv.setItem(x, items[x]);
	    	//	}
	    	//}
	    	
			SpawnUtils.crates.put(loc, crateInv);
			SpawnUtils.crateTimeout.put(loc, 600);
			
    	} else {
    		for(ItemStack all : items) {
    			if(all != null && all.getType() != Material.AIR) {
    				Item item = loc.getWorld().dropItemNaturally(loc, all);
    				item.setPickupDelay(20);
    			}
    		}
    	}
    }*/

}
