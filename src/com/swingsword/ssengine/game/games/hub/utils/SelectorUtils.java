package com.swingsword.ssengine.game.games.hub.utils;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.swingsword.ssengine.server.ServerManager;
import com.swingsword.ssengine.utils.ItemUtils;

public class SelectorUtils {
	
	public static void openGamesMenu(Player p) {
		Inventory inv = Bukkit.createInventory(null, 45, "Games Menu");
		
		inv.setItem(9, ItemUtils.createItem(Material.TNT, 1, 0, ChatColor.AQUA + "Counter Strike" + ChatColor.GRAY + " Shooter", Arrays.asList("", ChatColor.WHITE + "desc", ChatColor.WHITE + "desc", "", ChatColor.GREEN + "" + ServerManager.getOnlineCount("cs") + ChatColor.WHITE + " others playing!")));
		inv.setItem(11, ItemUtils.createItem(Material.WORKBENCH, 1, 0, ChatColor.AQUA + "Build Battle" + ChatColor.GRAY + " Building", Arrays.asList("", ChatColor.WHITE + "desc", ChatColor.WHITE + "desc", "", ChatColor.GREEN + "" + ServerManager.getOnlineCount("agar") + ChatColor.WHITE + " others playing!")));
		inv.setItem(9 + 18, ItemUtils.createItem(Material.STONE_AXE, 1, 0, ChatColor.AQUA + "Rust" + ChatColor.GRAY + " Solo/Team Survival", Arrays.asList("", ChatColor.WHITE + "desc", ChatColor.WHITE + "desc", "", ChatColor.GREEN + "" + ServerManager.getOnlineCount("rust") + ChatColor.WHITE + " others playing!")));
		inv.setItem(11 + 18, ItemUtils.createItem(Material.SLIME_BALL, 1, 0, ChatColor.AQUA + "Agar" + ChatColor.GRAY + " Competitive", Arrays.asList("", ChatColor.WHITE + "desc", ChatColor.WHITE + "desc", "", ChatColor.GREEN + "" + ServerManager.getOnlineCount("agar") + ChatColor.WHITE + " others playing!")));
		
		inv.setItem(13, ItemUtils.createItem(Material.WEB, 1, 0, ChatColor.RED + "Coming soon!", Arrays.asList("")));
		inv.setItem(15, ItemUtils.createItem(Material.WEB, 1, 0, ChatColor.RED + "Coming soon!", Arrays.asList("")));
		inv.setItem(17, ItemUtils.createItem(Material.WEB, 1, 0, ChatColor.RED + "Coming soon!", Arrays.asList("")));
		inv.setItem(13 + 18, ItemUtils.createItem(Material.WEB, 1, 0, ChatColor.RED + "Coming soon!", Arrays.asList("")));
		inv.setItem(15 + 18, ItemUtils.createItem(Material.WEB, 1, 0, ChatColor.RED + "Coming soon!", Arrays.asList("")));
		inv.setItem(17 + 18, ItemUtils.createItem(Material.WEB, 1, 0, ChatColor.RED + "Coming soon!", Arrays.asList("")));
		
		p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		p.openInventory(inv);
	}
}
