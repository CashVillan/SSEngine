package com.swingsword.ssengine.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.party.PartyManager;

public class PartyUtils {

	public static Inventory getPartyInv(final Player player, final int mode) {
		String display = "Party";
		if(mode == 1) {
			display = "Invites";
		}
		
		final Inventory inv = Bukkit.createInventory(null, 54, display);
		
		inv.setItem(0, ItemUtils.itemStackFromString("i=397;d=3;n=&f&lParty"));
		inv.setItem(2, ItemUtils.itemStackFromString("i=386;n=&f&lInvite a user"));
		ItemStack playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GRAY + "Return to your Profile", null);
		SkullMeta meta = (SkullMeta) playerHead.getItemMeta(); meta.setOwner(player.getName()); playerHead.setItemMeta(meta);
		inv.setItem(4, playerHead);
		inv.setItem(8, ItemUtils.itemStackFromString("i=397;d=3;n=&f&lInvites"));
		
		//if(PlayerSessionManager.getSession(player).getAccount().profileSettings != null && PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("vb").equals("1")) {
		//	inv.setItem(6, ItemUtils.itemStackFromString("i=324;n=&f&lDisallow blocked users to see your profile"));
		//} else {
		//	inv.setItem(6, ItemUtils.itemStackFromString("i=330;n=&f&lAllow blocked users to see your profile"));
		//}
		
		if(mode == 0) {
			if(PartyManager.getParty(player.getName(), false) != null) {
				for(String players : PartyManager.getParty(player.getName(), false).players) {
					ItemStack playersHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + players, null);
					SkullMeta playersMeta = (SkullMeta) playersHead.getItemMeta(); playersMeta.setOwner(players); playersHead.setItemMeta(playersMeta);
					inv.setItem(InventoryUtils.getFirstFreeFrom(inv, 18), playersHead);
				}
				
				if(InventoryUtils.getFirstFreeFrom(inv, 18) == 18) {
					inv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.WHITE + "No members.", null));
				}
				
			} else {
				inv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.WHITE + "No party.", null));
			}
			
		} else if(mode == 1) {
			if(PartyManager.getParty(player.getName(), false) != null) {
				for(String players : PartyManager.getParty(player.getName(), false).invites) {
					ItemStack playersHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + players, null);
					SkullMeta playersMeta = (SkullMeta) playersHead.getItemMeta(); playersMeta.setOwner(players); playersHead.setItemMeta(playersMeta);
					inv.setItem(InventoryUtils.getFirstFreeFrom(inv, 18), playersHead);
				}
				
				if(InventoryUtils.getFirstFreeFrom(inv, 18) == 18) {
					inv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.WHITE + "No invites.", null));
				}
			
			} else {
				inv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.WHITE + "No party.", null));
			}
		}
		
		return inv;
	}
}
