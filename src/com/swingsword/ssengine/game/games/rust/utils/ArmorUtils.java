package com.swingsword.ssengine.game.games.rust.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorUtils {
	
	public static boolean wearsArmour(Player player) {
		if(player.getInventory().getHelmet() != null || player.getInventory().getChestplate() != null || player.getInventory().getLeggings() != null || player.getInventory().getBoots() != null) {
			return true;
			
		} else {
			return false;
		}
	}
	
	public static int getArmour(Player player) {
		int amount = 0;
		
		if(player.getInventory().getHelmet() != null) {
			amount = amount + 1;
		}
		if(player.getInventory().getChestplate() != null) {
			amount = amount + 1;
		}
		if(player.getInventory().getLeggings() != null) {
			amount = amount + 1;
		}
		if(player.getInventory().getBoots() != null) {
			amount = amount + 1;
		}
		return amount;
	}

	public static float getTotalProtection(Player player) {
		float percentage = 100;
		
		float helmet = getPercentage(player.getInventory().getHelmet());
		float vest = getPercentage(player.getInventory().getChestplate());
		float pants = getPercentage(player.getInventory().getLeggings());
		float boots = getPercentage(player.getInventory().getBoots());
		
		return (percentage - (helmet + vest + pants + boots)) / 100;
	}
	
	public static float getPercentage(ItemStack item) {
		if(item != null) {
			if(item.getItemMeta() != null) {
				if(item.getItemMeta().getDisplayName() != null) {
					String type = ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ")[0];
					String armour = ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ")[1];
					
					if(armour.equalsIgnoreCase("Helmet")) {
						if(type.equalsIgnoreCase("Cloth")) {
							return 5;
							
						} else if(type.equalsIgnoreCase("Rad")) {
							return 2.5f;
							
						} else if(type.equalsIgnoreCase("Leather")) {
							return 5;
							
						} else if(type.equalsIgnoreCase("Kevlar")) {
							return 12.5f;
						}
						
					} else if(armour.equalsIgnoreCase("Vest")) {
						if(type.equalsIgnoreCase("Cloth")) {
							return 5;
							
						} else if(type.equalsIgnoreCase("Rad")) {
							return 2.5f;
							
						} else if(type.equalsIgnoreCase("Leather")) {
							return 5;
							
						} else if(type.equalsIgnoreCase("Kevlar")) {
							return 15;
						}
						
					} else if(armour.equalsIgnoreCase("Pants")) {
						if(type.equalsIgnoreCase("Cloth")) {
							return 2.5f;
							
						} else if(type.equalsIgnoreCase("Rad")) {
							return 2.5f;
							
						} else if(type.equalsIgnoreCase("Leather")) {
							return 5;
							
						} else if(type.equalsIgnoreCase("Kevlar")) {
							return 7.5f;
						}
						
					} else if(armour.equalsIgnoreCase("Boots")) {
						if(type.equalsIgnoreCase("Cloth")) {
							return 2.5f;
							
						} else if(type.equalsIgnoreCase("Rad")) {
							return 2.5f;
							
						} else if(type.equalsIgnoreCase("Leather")) {
							return 5;
							
						} else if(type.equalsIgnoreCase("Kevlar")) {
							return 7.5f;
						}
					}
				}
			}
		}
		return 0;
	}
}
