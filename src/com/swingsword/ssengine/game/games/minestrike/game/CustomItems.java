package com.swingsword.ssengine.game.games.minestrike.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.swingsword.ssengine.utils.ItemUtils;

public class CustomItems {

	public static ItemStack backToLobby = ItemUtils.itemStackFromString("i=345;n=&bBack to Lobby");
	public static ItemStack spectate = ItemUtils.createItem(Material.BOOK, 1, (byte) 0, ChatColor.AQUA + "Spectate", null);

	public static ItemStack bomb = ItemUtils.itemStackFromString("i=46;n=&f&lBomb");
	@SuppressWarnings("deprecation")
	public static ItemStack bombTracker = ItemUtils.itemStackFromString("i=" + Material.COMPASS.getId() + ";n=&f&lBomb Tracker");

	public static ItemStack knife = ItemUtils.itemStackFromString("i=351;n=&f&lKnife");
	public static ItemStack defuseKit = ItemUtils.itemStackFromString("i=359;n=&f&lDefuse Kit;l=[&7Cost:&f $400]");
	
	public static ItemStack hegranade = ItemUtils.itemStackFromString("i=402;n=&f&lGrenade;l=[&7Cost:&f $300]");
	public static ItemStack flash = ItemUtils.itemStackFromString("i=318;n=&f&lFlashbang;l=[&7Cost:&f $200]");
	public static ItemStack smoke = ItemUtils.itemStackFromString("i=263;n=&f&lSmoke;l=[&7Cost:&f $300]");
	public static ItemStack molotov = ItemUtils.itemStackFromString("i=336;n=&f&lMolotov;l=[&7Cost:&f $400]");
	public static ItemStack incendiary = ItemUtils.itemStackFromString("i=336;n=&f&lIncendiary;l=[&7Cost:&f $600]");
	
	public static ItemStack kevlarVest = ItemUtils.itemStackFromString("i=35;d=15;n=&f&lKelvar Vest;l=[&7Cost:&f $650]");
	public static ItemStack kevlarHelmet = ItemUtils.itemStackFromString("i=35;d=5;n=&f&lKevlar Helmet;l=[&7Cost:&f $350]");
	public static ItemStack zeus = ItemUtils.itemStackFromString("i=352;n=&eZeus;l=[&7Cost:&f $400]");

	public static boolean isNade(ItemStack item) {
		return (item.getType() == hegranade.getType()) || (item.getType() == flash.getType()) || (item.getType() == smoke.getType()) || (item.getType() == molotov.getType()) || (item.getType() == incendiary.getType());
	}
	
	public static boolean isGear(ItemStack item) {
		return (item.isSimilar(kevlarHelmet) || item.isSimilar(kevlarVest) || item.isSimilar(zeus) || item.isSimilar(defuseKit));
	}
}
