package com.swingsword.ssengine.game.games.minestrike.guns;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Ammo {
    
    public static int getAmmoLeft(Player player, Gun gun, ItemStack item) {
    	if(item != null && Gun.isInteger(item.getItemMeta().getDisplayName().split(" ")[item.getItemMeta().getDisplayName().split(" ").length - 1].split("/")[0])) {
    		return Integer.parseInt(item.getItemMeta().getDisplayName().split(" ")[item.getItemMeta().getDisplayName().split(" ").length - 1].split("/")[0]);
    		
    	} else {
    		if(item.getItemMeta().getDisplayName() != null && Gun.getGun(ChatColor.stripColor(item.getItemMeta().getDisplayName())).toItemStack(true, player.getName()) != null) {
    			player.setItemInHand(Gun.getGun(ChatColor.stripColor(item.getItemMeta().getDisplayName())).toItemStack(true, player.getName()));
    		
    			return 0;
    		}
    	}
		return 0;
    }
    
    public static int getTotalAmmoLeft(Player player, Gun gun) {
    	if(player.getItemInHand() != null && Gun.isInteger(player.getItemInHand().getItemMeta().getDisplayName().split(" ")[player.getItemInHand().getItemMeta().getDisplayName().split(" ").length - 1].split("/")[1])) {
    		return Integer.parseInt(player.getItemInHand().getItemMeta().getDisplayName().split(" ")[player.getItemInHand().getItemMeta().getDisplayName().split(" ").length - 1].split("/")[1]);
    		
    	} else {
    		player.setItemInHand(gun.toItemStack(true, player.getName()));
    		return gun.getSpawnAmmo();
    	}
    }
}
