package com.swingsword.ssengine.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;

public class ItemUtils {
	
	public static ItemStack createItem(Material mat, int amount, int data, String name, List<String> lore) { 
		ItemStack item = new ItemStack(mat, amount, (byte) data);
		ItemMeta meta = item.getItemMeta();
		
		if(name != null) {
			meta.setDisplayName(name);
		}
		
		if(lore != null) {
			meta.setLore(lore);
		}
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	@SuppressWarnings("deprecation")
	public static String itemStackToString(ItemStack i) {
    	String item = null;
    	
    	if(i != null) {
    		item = "i=" + i.getType().getId();
    		
    		if(i.getData().getData() != (byte) 0) {
    			item = item + ";d=" + i.getData().getData();
    		}
    		if(i.getAmount() > 1) {
    			item = item + ";a=" + i.getAmount();
    		}
    		
    		if(i.getItemMeta() != null) {
    			if(i.getItemMeta().getDisplayName() != null) {
    				item = item + ";n=" + i.getItemMeta().getDisplayName().replace(ChatColor.COLOR_CHAR + "", "&");
    			}
    			if(i.getItemMeta().getLore() != null) {
	    			item = item + ";l=" + i.getItemMeta().getLore().toString().replace(ChatColor.COLOR_CHAR + "", "&").replace(", ", ",");
	    		}
	    	}
	    }
	    	
	    return item;
	}
	    
	@SuppressWarnings("deprecation")
	public static ItemStack itemStackFromString(String s) {
	    String[] st = s.split(";");
	    	
	    int id = 0;
	    byte data = 0;
	    int amount = 1;
	    String name = null;
	    List<String> lore = new ArrayList<String>();
	    	
	    for(String all : st) {
	    	if(all.contains("i=")) {
	    		id = Integer.parseInt(all.replace("i=", ""));
	    			
	    	} else if(all.contains("d=")) {
	    		data = Byte.parseByte(all.replace("d=", ""));
	    		
	    	} else if(all.contains("a=")) {
	    		amount = Integer.parseInt(all.replace("a=", ""));
	    		
	    	} else if(all.contains("n=")) {
	    		name = all.replace("n=", "").replace("&", ChatColor.COLOR_CHAR + "");
	    			
	    	} else if(all.contains("l=")) {
	    		String loreString = all.replace("l=", "").substring(1, all.length() - 3).replace("&", ChatColor.COLOR_CHAR + "").replace("_", " ").replace(", ", ",");
	    		
	    		for(String l : loreString.split(",")) {
	    			lore.add(l.replace("=", ":"));
	    		}
	    	}
	    }
	    	
	    ItemStack item = new ItemStack(Material.getMaterial(id), amount, data);
	    if(item.getItemMeta() != null) {
		    ItemMeta meta = item.getItemMeta();
		    	
		    if(name != null) {
		    	meta.setDisplayName(name);
		    }
		    if(lore.size() > 0) {
		    	meta.setLore(lore);
		    }
		    	
		    item.setItemMeta(meta);
	    }
	    	
	    return item;
	}
	
	public static ItemStack addGlow(ItemStack item){
        net.minecraft.server.v1_10_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
	
	public static String getDisplayName(ItemStack item) {
		if(item != null && item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null) {
			return item.getItemMeta().getDisplayName();
		}
		return null;
	}
	
	public static boolean hasLore(ItemStack item) {
		return item != null && item.getItemMeta() != null && item.getItemMeta().getLore() != null;
	}
	
	public static List<String> getLore(ItemStack item) {
		if(hasLore(item)) {
			return item.getItemMeta().getLore();
		}
		return null;
	}
	
	public static boolean loreContains(ItemStack item, String key) {
		for(String all : getLore(item)) {
			if(all.toLowerCase().contains(key.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	public static String getServer(ItemStack item) {
		if(item != null) {
			for(String lore : item.getItemMeta().getLore()) {
				if(lore.contains("Playing ")) {
					return ChatColor.stripColor(lore.replace("Playing ", ""));
				}
			}
		}
		return null;
	}
}
