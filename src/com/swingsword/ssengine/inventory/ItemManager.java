package com.swingsword.ssengine.inventory;

import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.ItemUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class ItemManager {

	public static HashMap<Item, ItemStack> itemStack = new HashMap<Item, ItemStack>();

	public ItemManager() {
		for (String item : ConfigUtils.getConfig("cache").getStringList("items")) {
			List<String> configdata = StringUtils.stringToList(item);
			Integer type = Integer.parseInt(configdata.get(0));
			Integer id = Integer.parseInt(configdata.get(1));
			ItemStack itemstack = ItemUtils.itemStackFromString(configdata.get(2));

			itemStack.put(new Item(type, id, false), itemstack);
		}
	}

	public static ItemStack getStack(int type, int id) {
		for (Item all : itemStack.keySet()) {
			if (all.getType() == type && all.getID() == id) {
				return itemStack.get(all).clone();
			}
		}
		return null;
	}

	public static Item getItem(ItemStack stack) {
		for (Item all : itemStack.keySet()) {
			if (ItemUtils.itemStackToString(stack).contains(ItemUtils.itemStackToString(itemStack.get(all)))) {
				Item item = new Item(all.compile());
				item.setEquipped(ItemUtils.hasLore(stack) ? ItemUtils.loreContains(stack, "Equipped") : false);
				
				return item;
			}
		}
		return null;
	}
}