package com.swingsword.ssengine.options;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.swingsword.ssengine.utils.ItemUtils;

public class OptionInventory {
	
	public String name;
	public ItemStack desc;
	public ItemStack acceptItem;
	public ItemStack denyItem;
	public Runnable onAccept;
	public Runnable onDeny;
	
	public Inventory inventory;
	
	public OptionInventory(String name, String acceptName, String denyName, Runnable onAccept, Runnable onDeny) {
		this.name = name;
		this.desc = ItemUtils.createItem(Material.REDSTONE_COMPARATOR, 1, (byte) 0, ChatColor.GRAY + "Choose an option.", null);
		this.acceptItem = ItemUtils.createItem(Material.EMERALD_BLOCK, 1, (byte) 0, acceptName, null);
		this.denyItem = ItemUtils.createItem(Material.REDSTONE_BLOCK, 1, (byte) 0, denyName, null);
		this.onAccept = onAccept;
		this.onDeny = onDeny;
		
		Inventory inv = Bukkit.createInventory(null, 27, name);
		
		inv.setItem(0, denyItem);
		inv.setItem(1, denyItem);
		inv.setItem(2, denyItem);
		inv.setItem(9, denyItem);
		inv.setItem(10, denyItem);
		inv.setItem(11, denyItem);
		inv.setItem(18, denyItem);
		inv.setItem(19, denyItem);
		inv.setItem(20, denyItem);
		
		inv.setItem(13, desc);
		
		inv.setItem(6, acceptItem);
		inv.setItem(7, acceptItem);
		inv.setItem(8, acceptItem);
		inv.setItem(15, acceptItem);
		inv.setItem(16, acceptItem);
		inv.setItem(17, acceptItem);
		inv.setItem(24, acceptItem);
		inv.setItem(25, acceptItem);
		inv.setItem(26, acceptItem);
		
		this.inventory = inv;
	}
}
