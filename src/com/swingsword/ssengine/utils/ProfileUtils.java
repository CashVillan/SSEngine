package com.swingsword.ssengine.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.player.PlayerSessionManager;

public class ProfileUtils {

	public static void openProfileMenu(Player p) {
		Inventory profileInv = Bukkit.createInventory(p, InventoryType.CHEST, LanguageUtils.translate(p, "Your Profile"));

		profileInv.setItem(10, ItemUtils.createItem(Material.DIAMOND, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(p, "Achievements"), Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(p, "Click to view your Achievements"))));
		profileInv.setItem(11, ItemUtils.createItem(Material.EXP_BOTTLE, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(p, "Leaderboard"), Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(p, "Click to view Leaderboards"))));
		profileInv.setItem(12, ItemUtils.createItem(Material.CHEST, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(p, "Inventory"), Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(p, "Click to view your Inventory"))));
		profileInv.setItem(13, PlayerSessionManager.getSession(p).getAccount().toItemStack(p, Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(p, "Click to edit your Settings"))));
		profileInv.setItem(14, ItemUtils.createItem(Material.BEACON, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(p, "Friends List"), Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(p, "Click to manage your Friends"))));
		profileInv.setItem(15, ItemUtils.createItem(Material.RED_ROSE, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(p, "Inbox"), Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(p, "Click to view your Inbox"))));
		profileInv.setItem(16, ItemUtils.createItem(Material.TNT, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(p, "Blocked Users"), Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(p, "Click to manage your Blocked Users"))));

		p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		p.openInventory(profileInv);
	}

	public static void openProfileDetailMenu(Player p) {
		Inventory profileDetailsInv = Bukkit.createInventory(p, InventoryType.CHEST, ChatColor.AQUA + LanguageUtils.translate(p, "Profile Details"));

		p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		p.openInventory(profileDetailsInv);
	}

	public static void openProfileSettingsMenu(Player p) {
		Inventory inv = Bukkit.createInventory(null, 27, LanguageUtils.translate(p, "Settings"));

		ItemStack playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(p, "Return to your Profile"), null);
		SkullMeta meta = (SkullMeta) playerHead.getItemMeta(); meta.setOwner(p.getName()); playerHead.setItemMeta(meta);
		inv.setItem(4, playerHead);
		inv.setItem(9, ItemUtils.createItem(Material.PUMPKIN, 1, 0, ChatColor.AQUA + LanguageUtils.translate(p, "Privacy Settings"), Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(p, "Click to edit your Privacy Settings"))));

		inv.setItem(12, ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(p, "Language"), Arrays.asList(ChatColor.GRAY + LanguageUtils.translate(p, "Current") + ": " + ChatColor.WHITE + PlayerSessionManager.getSession(p).getAccount().profileSettings.getSetting("lang"), "", ChatColor.YELLOW + LanguageUtils.translate(p, "Click to change your Language"))));
		inv.setItem(14, ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(p, "Location"), Arrays.asList(ChatColor.GRAY + LanguageUtils.translate(p, "Current") + ": " + ChatColor.WHITE + PlayerSessionManager.getSession(p).getAccount().profileSettings.getSetting("loc"))));

		ItemStack sex = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.AQUA + LanguageUtils.translate(p, "Sex"), Arrays.asList(ChatColor.GRAY + LanguageUtils.translate(p, "Current") + ": "  + ChatColor.WHITE + LanguageUtils.translate(p, StringUtils.formatSex(PlayerSessionManager.getSession(p).getAccount().profileSettings.getSetting("sex"))), "", ChatColor.YELLOW + LanguageUtils.translate(p, "Click to change your Sex")));
		SkullMeta sexMeta = (SkullMeta) sex.getItemMeta();
		if(PlayerSessionManager.getSession(p).getAccount().profileSettings.getSetting("sex").equals("f")) {
			sexMeta.setOwner("MHF_Alex");
		}
		sex.setItemMeta(sexMeta);
		
		inv.setItem(13, sex);
		
		inv.setItem(17, ItemUtils.createItem(Material.BEACON, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(p, "Global Settings"), Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(p, "Click to edit your Global Settings"))));

		p.closeInventory();
		p.openInventory(inv);
	}

	public static void openEditProfileSettingMenu(Player p, String optionName) {
		List<Integer> blocked = Arrays.asList(0, 1, 2, 9, 10, 11, 18, 19, 20);
		List<Integer> friends = Arrays.asList(3, 4, 5, 12, 13, 14, 21, 22, 23);
		List<Integer> everyone = Arrays.asList(6, 7, 8, 15, 16, 17, 24, 25, 26);

		Inventory inv = Bukkit.createInventory(null, 27, "Set " + optionName + " visibility");

		for (int block : blocked) {
			inv.setItem(block, ItemUtils.createItem(Material.REDSTONE_BLOCK, 1, (byte) 0, ChatColor.RED + LanguageUtils.translate(p, "Blocked"), Arrays.asList("")));
		}
		for (int friend : friends) {
			inv.setItem(friend, ItemUtils.createItem(Material.GOLD_BLOCK, 1, (byte) 0, ChatColor.YELLOW + LanguageUtils.translate(p, "Friends Only"), Arrays.asList("")));
		}
		for (int all : everyone) {
			inv.setItem(all, ItemUtils.createItem(Material.EMERALD_BLOCK, 1, (byte) 0, ChatColor.GREEN + LanguageUtils.translate(p, "Everyone"), Arrays.asList("")));
		}

		p.closeInventory();
		p.openInventory(inv);
	}
}
