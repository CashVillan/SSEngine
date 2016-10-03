package com.swingsword.ssengine.game.games.rust.guns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Ammo {

	private String name;
	private Material mat;
	private String type;
	private String sound;
    
    public Ammo(String name, Material material, String type, String sound) {
        this.name = name;
        this.mat = material;
        this.type = type;
        
        GunData.allAmmo.add(this);
	}
    
    public Ammo(String name, Material material) {
        this.name = name;
        this.mat = material;
	}
    
    public String getName() {
    	return this.name;
    }
    
    public Material getMaterial() {
    	return this.mat;
    }
    
    public String getAmmoType() {
    	return this.type;
    }
    
    public String getSound() {
    	return this.sound;
    }
    
    public static Ammo getAmmo(String name) {
		Ammo ammo = null;
		
		if(name != null) {
			for(Ammo all : GunData.allAmmo) {
				if(all.getName().equals(ChatColor.stripColor(name))) {
					ammo = all;
				}
			}
		}
		return ammo;
	}
    
    public static boolean isAmmo(String name) {
		boolean ammo = false;
		
		if(name != null) {
			for(Ammo all : GunData.allAmmo) {
				if(all.getName().equals(ChatColor.stripColor(name))) {
					ammo = true;
				}
			}
		}
		return ammo;
	}
    
    public ItemStack toItemStack(int amount) {
    	ItemStack ammoItem = new ItemStack(this.mat, amount);
    	List<String> lore = Arrays.asList("Type: %type");
    	List<String> finalLore = new ArrayList<String>();
    	ItemMeta ammoItemMeta = ammoItem.getItemMeta();
    	
    	ammoItemMeta.setDisplayName(this.name);
    	for(String all : lore) {
    		String blackAll = ChatColor.GRAY + all;
    		String finalAll = blackAll.replace("%type", this.type + "");
    		finalLore.add(finalAll);
    	}
    	ammoItemMeta.setLore(finalLore);
    	ammoItem.setItemMeta(ammoItemMeta);
    	
    	return ammoItem;
    }
    
    public static boolean isAmmo(ItemStack item) {
    	boolean isAmmo = false;
    	
    	if(item != null) {
	    	if(item.getItemMeta() != null) {
	    		if(item.getItemMeta().getDisplayName() != null) {
	    			Ammo ammo = new Ammo(ChatColor.stripColor(item.getItemMeta().getDisplayName()), item.getType());
	    			
		    		if(GunData.containsAmmo(ammo)) {
			    		isAmmo = true;
	    			}
	    		}
	    	}
    	}
    	return isAmmo;
    }
    
    public static boolean hasAmmoFor(Player player, Gun gun) {
    	boolean ammo = false;
    	
		for(ItemStack all : player.getInventory().getContents()) {
			if(isAmmo(all)) {
				if(Ammo.getAmmo(all.getItemMeta().getDisplayName()).getName().equals(gun.getAmmoName())) {
					ammo = true;
				}
			}
		}
		return ammo;
	}
    
    public static int getAmmoLeft(Player player, Gun gun) {
    	if(Gun.isInteger(player.getItemInHand().getItemMeta().getDisplayName().split(" ")[player.getItemInHand().getItemMeta().getDisplayName().split(" ").length - 1])) {
    		return Integer.parseInt(player.getItemInHand().getItemMeta().getDisplayName().split(" ")[player.getItemInHand().getItemMeta().getDisplayName().split(" ").length - 1]);
    		
    	} else {
    		player.setItemInHand(Gun.getGun(ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName())).toItemStack());
    		
    		return 0;
    	}
    }
    
    public static int getAmmoSlot(Player player, Gun gun) {
    	int ammoSlot = -1;
    	
		for(ItemStack all : player.getInventory().getContents()) {
			if(isAmmo(all)) {
				if(getAmmo(all.getItemMeta().getDisplayName()).getName().equals(gun.getAmmoName())) {
					for(int x = 0; ammoSlot == -1; x++) {
						if(player.getInventory().getItem(x) != null) {
							if(player.getInventory().getItem(x).equals(all)) {
								ammoSlot = x;
							}
						}
					}
				}
			}
		}
		
		return ammoSlot;
	}
}
