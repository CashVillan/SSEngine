package com.swingsword.ssengine.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.player.PlayerSettings;

public class SettingsUtils {

	public static Inventory getSettingsInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, 54, "Settings - Global Settings");
		PlayerSettings settings = PlayerSessionManager.getSession(player).getAccount().settings;
		
		inv.setItem(4, ItemUtils.createItem(Material.BED, 1, 0, ChatColor.WHITE + "" + ChatColor.BOLD + "Back", null));
		
		inv.setItem(9, ItemUtils.createItem(Material.EYE_OF_ENDER, 1, 0, getColor(settings.settings[0]) + "Visibility", null));
		inv.setItem(9 + 9, ItemUtils.createItem(Material.INK_SACK, 1, getDye(settings.settings[0]), getColor(settings.settings[0]) + "Visibility", null));
		inv.setItem(11, ItemUtils.createItem(Material.PAPER, 1, 0, getColor(settings.settings[1]) + "Chat Visibility", null));
		inv.setItem(11 + 9, ItemUtils.createItem(Material.INK_SACK, 1, getDye(settings.settings[1]), getColor(settings.settings[1]) + "Chat Visibility", null));
		inv.setItem(13, ItemUtils.createItem(Material.BOOK, 1, 0, getColor(settings.settings[2]) + "Requests in Chat", null));
		inv.setItem(13 + 9, ItemUtils.createItem(Material.INK_SACK, 1, getDye(settings.settings[2]), getColor(settings.settings[2]) + "Requests in Chat", null));
		inv.setItem(15, ItemUtils.createItem(Material.NOTE_BLOCK, 1, 0, getColor(settings.settings[3]) + "Chat Alerts", null));
		inv.setItem(15 + 9, ItemUtils.createItem(Material.INK_SACK, 1, getDye(settings.settings[3]), getColor(settings.settings[3]) + "Chat Alerts", null));
		inv.setItem(17, ItemUtils.createItem(Material.TNT, 1, 0, getColor(settings.settings[4]) + "/hub Protection", null));
		inv.setItem(17 + 9, ItemUtils.createItem(Material.INK_SACK, 1, getDye(settings.settings[4]), getColor(settings.settings[4]) + "/hub Protection", null));
		inv.setItem(40, ItemUtils.createItem(Material.EXP_BOTTLE, 1, 0, getColor(settings.settings[5]) + "Increased Speed", null));
		inv.setItem(40 + 9, ItemUtils.createItem(Material.INK_SACK, 1, getDye(settings.settings[5]), getColor(settings.settings[5]) + "Increased Speed", null));

		return inv;
	}
	
	public static ChatColor getColor(boolean bool) {
		if(bool) {
			return ChatColor.GREEN;
		} else {
			return ChatColor.GRAY;
		}
	}
	
	public static byte getDye(boolean bool) {
		if(bool) {
			return 10;
		} else {
			return 8;
		}
	}
	
	public static void applySettings(Player player) {
		PlayerSettings settings = PlayerSessionManager.getSession(player).getAccount().settings;
		
		for(Player all : Bukkit.getOnlinePlayers()) { //TODO on join too
			if(settings.settings[0]) {
				player.showPlayer(all);
			} else {
				player.hidePlayer(all);
			}
		}
			
		player.removePotionEffect(PotionEffectType.SPEED);
		if(settings.settings[5] && GameManager.currentGame.gamePlugin.getName().contains("Hub")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 2));
		}
	}
}
