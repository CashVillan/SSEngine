package com.swingsword.ssengine.inventory;

import org.bukkit.inventory.ItemStack;

public class Item {

	private int typeId;
	private int itemId;
	private boolean equipped = false;
	
	public Item(String compiled) {
		typeId = Integer.parseInt(compiled.split(",")[0]);
		itemId = Integer.parseInt(compiled.split(",")[1].replace("-", ""));
		equipped = compiled.endsWith("-");
	}
	
	public Item(int typeId, int itemId, boolean equipped) {
		this.typeId = typeId;
		this.itemId = itemId;
		this.equipped = equipped;
	}
	
	public Item(ItemStack stack) {
		Item masterItem = ItemManager.getItem(stack);
		
		this.typeId = masterItem.typeId;
		this.itemId = masterItem.itemId;
	}
	
	public ItemStack getStack() {
		return ItemManager.getStack(typeId, itemId);
	}
	
	public int getType() {
		return typeId;
	}
	
	public int getID() {
		return itemId;
	}
	
	public boolean isEquipped() {
		return equipped;
	}
	
	public void setEquipped(boolean equipped) {
		this.equipped = equipped;
	}
	
	public String compile() {
		return typeId + "," + itemId + (equipped ? "-" : "");
	}
	
	
}
