package com.swingsword.ssengine.server;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.swingsword.ssengine.utils.ItemUtils;
import com.swingsword.ssengine.utils.LanguageUtils;

public class Server {
	
	public static ItemStack toItemStack(Player player, String name, String motd) {
		String[] data = motd.split(";");
		
		if(data.length > 1) {
			String gamemode = data[1];
			int online = Integer.parseInt(data[2]);
			int max = Integer.parseInt(data[3]);
			int mode = 1;
			String map = null;
			
			if(data.length > 4 && !data[4].equals("null")) {
				mode = Integer.parseInt(data[4]);
			}
			
			Material mat = Material.REDSTONE_BLOCK;
			if(mode == 0) {
				mat = Material.EMERALD_BLOCK;
			}
			if(mode == 1) {
				mat = Material.REDSTONE_BLOCK;
			}
			
			if(data.length == 6) {
				map = data[5];
			}
			
			ArrayList<String> lore = new ArrayList<String>();
			lore.addAll(Arrays.asList(ChatColor.GRAY + LanguageUtils.translate(player, "Players") + ": " + ChatColor.WHITE + online + "/" + max));
			if(gamemode != null && !gamemode.equals("null")) {
				lore.add(ChatColor.GRAY + LanguageUtils.translate(player, "Gamemode") + ": " + ChatColor.WHITE + gamemode);
			}
			if(map != null && !map.equals("null") && !map.equals("map")) {
				lore.add(ChatColor.GRAY + LanguageUtils.translate(player, "Map") + ": " + ChatColor.WHITE + map);
			}
			
			return ItemUtils.createItem(mat, online, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + name, lore);
		}
		return null;
	}
}
