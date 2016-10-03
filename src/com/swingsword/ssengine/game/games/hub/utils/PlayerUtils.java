package com.swingsword.ssengine.game.games.hub.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.utils.LanguageUtils;

public class PlayerUtils {

	public static void loadPlayerInventory(Player p) {
		ItemStack profileInvItem = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		ItemStack gameMenuInvItem = new ItemStack(Material.COMPASS);
		ItemStack lobbySelectorInvItem = new ItemStack(Material.BOOK);
		ItemStack scoreboardItem = new ItemStack(Material.PAINTING);
		ItemStack settingsBarItem = new ItemStack(Material.REDSTONE_COMPARATOR);

		SkullMeta profileInvItemMeta = (SkullMeta) profileInvItem.getItemMeta();
		ItemMeta gameMenuInvItemMeta = gameMenuInvItem.getItemMeta();
		ItemMeta lobbySelectorInvItemMeta = lobbySelectorInvItem.getItemMeta();
		ItemMeta scoreboardItemMeta = scoreboardItem.getItemMeta();
		ItemMeta settingsBarItemMeta = settingsBarItem.getItemMeta();

		profileInvItemMeta.setOwner(p.getName());

		profileInvItemMeta.setDisplayName(ChatColor.AQUA + LanguageUtils.translate(p, "Your Profile"));
		gameMenuInvItemMeta.setDisplayName(ChatColor.AQUA + LanguageUtils.translate(p, "Games Menu"));
		lobbySelectorInvItemMeta.setDisplayName(ChatColor.AQUA + LanguageUtils.translate(p, "Hub Selector"));
		scoreboardItemMeta.setDisplayName(ChatColor.AQUA + "/scoreboard");
		settingsBarItemMeta.setDisplayName(ChatColor.AQUA + "/settings");

		profileInvItem.setItemMeta(profileInvItemMeta);
		gameMenuInvItem.setItemMeta(gameMenuInvItemMeta);
		lobbySelectorInvItem.setItemMeta(lobbySelectorInvItemMeta);
		scoreboardItem.setItemMeta(scoreboardItemMeta);
		settingsBarItem.setItemMeta(settingsBarItemMeta);

		p.getInventory().setHeldItemSlot(0);
		p.getInventory().clear();
		p.getInventory().setItem(4, profileInvItem);
		p.getInventory().setItem(0, gameMenuInvItem);
		p.getInventory().setItem(1, lobbySelectorInvItem);
		p.getInventory().setItem(7, scoreboardItem);
		p.getInventory().setItem(8, settingsBarItem);
	}
}
