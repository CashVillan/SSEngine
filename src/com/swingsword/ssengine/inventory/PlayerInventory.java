package com.swingsword.ssengine.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.utils.InventoryUtils;
import com.swingsword.ssengine.utils.ItemUtils;
import com.swingsword.ssengine.utils.LanguageUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class PlayerInventory {

	public static ItemStack nextPage = ItemUtils.createItem(Material.BED, 1, (byte) 0, ChatColor.WHITE + "Next Page ▶", null);
	public static ItemStack prevPage = ItemUtils.createItem(Material.BED, 1, (byte) 0, ChatColor.WHITE + "◀ Previous Page", null);
	
	private String owner;
	private ArrayList<Item> items = new ArrayList<Item>();
	private boolean loaded = false;
	
	public PlayerInventory(String owner, String compiled) {
		this.owner = owner;
		
		for(String item : StringUtils.stringToList(compiled)) {
			items.add(new Item(item));
		}
	}
	
	@SuppressWarnings("deprecation")
	public PlayerInventory(final String owner) {
		this.owner = owner;
		
		if(Bukkit.getOfflinePlayer(owner).isOnline()) {
			String compiled = (String) PlayerSessionManager.getSession(Bukkit.getPlayer(owner)).getAccount().getCache().get("inv");
			
			for(String item : StringUtils.stringToList(compiled)) {
				items.add(new Item(item));
			}

			loaded = true;
			
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					String compiled = (String) SQLManager.getSQL("global").getValue(SQLManager.getSQL("global").getUUID(owner), "inv");

					for(String item : StringUtils.stringToList(compiled)) {
						items.add(new Item(item));
					}
					
					loaded = true;
				}
			});
		}
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public ArrayList<Item> getItems() {
		return items;
	}
	
	public boolean hasItem(Item item) {
		return compile().contains(item.compile().replace("-", ""));
	}
	
	public void addItem(Item item) {
		item.setEquipped(false);
		items.add(item);
		
		save();
	}
	
	public void removeItem(Item item) {
		ArrayList<Item> remove = new ArrayList<Item>();
		for(Item ownedItem : items) {
			if(ownedItem.getType() == item.getType() && ownedItem.getID() == item.getID()) {
				remove.add(ownedItem);
			}
		}
		for(Item ownedItem : remove) {
			items.remove(ownedItem);
		}
		
		save();
	}
	
	public void equipItem(Item item) {
		for(Item ownedItem : items) {
			if(ownedItem.getType() == item.getType() && ownedItem.isEquipped()) {
				ownedItem.setEquipped(false);
			}
			if(ownedItem.getType() == item.getType() && ownedItem.getID() == item.getID()) {
				ownedItem.setEquipped(true);
			}
		}
		
		save();
	}
	
	public void unequipItem(Item item) {
		for(Item ownedItem : items) {
			if(ownedItem.getType() == item.getType() && ownedItem.getID() == item.getID()) {
				ownedItem.setEquipped(false);
			}
		}
		
		save();
	}
	
	public void openInventory(final Player player, final int page) {
		final Inventory inv = Bukkit.createInventory(null, 45, owner + " - " + LanguageUtils.translate(player, "Page") + " " + page);
		
		if(loaded) {
			addItems(player, inv, page, items);
			
		} else {
			inv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, 0, ChatColor.YELLOW + LanguageUtils.translate(player, "Loading") + "...", null));
			
			Thread t = new Thread(new Runnable() {
				public void run() {
					boolean first = true;
					
					while((first || inv.getViewers().size() > 0) && !loaded && player.isOnline()) {
						first = false;
						
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) { }
					}
					
					if(loaded && player.isOnline()) {
						addItems(player, inv, page, items);
					}
				}
			});
			t.start();
		}
		
		ItemStack playerHead = null;
		if(player.getName().equals(owner)) {
			playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Return to your Profile"), null);
		} else {
			playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Return to") + " " + owner + "'s " + LanguageUtils.translate(player, "Profile"), null);
		}
		
		SkullMeta meta = (SkullMeta) playerHead.getItemMeta(); meta.setOwner(owner); playerHead.setItemMeta(meta);
		inv.setItem(4, playerHead);
		
		Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(player.isOnline()) {
					player.openInventory(inv);
				}
			}
		});
	}
	
	public void addItems(Player player, Inventory inv, int page, ArrayList<Item> items) {
		inv.setItem(18, null);
		
		int x = 0;
		for(x = 0; x < items.size(); x++) {			
			Item item = items.get(x);
			ItemStack stack = items.get(x).getStack();
			
			if(player.getName().equals(owner)) {
				ItemMeta meta = stack.getItemMeta();
				List<String> lore = ItemUtils.getLore(stack);
				if(lore == null) {
					lore = new ArrayList<String>();
				}
				
				if(item.isEquipped()) {
					lore.add("");
					lore.add(ChatColor.GRAY + "Equipped");
					lore.add("");
					lore.add(ChatColor.YELLOW + "Left-Click to unequip this item");
					
				} else {
					lore.add("");
					lore.add(ChatColor.YELLOW + "Left-Click to equip this item");
				}
				
				meta.setLore(lore);
				stack.setItemMeta(meta);
			}
			
			inv.setItem(18 + (x - (36 * (page - 1))), stack);
		}
		
		if(InventoryUtils.getFirstFreeFrom(inv, 18) == 18) {
			inv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, 0, ChatColor.WHITE + "No items.", null));
		}
		
		if(page > 1) {
			inv.setItem(0, prevPage);
		}
		if(items.size() > 36 * page) {
			inv.setItem(8, nextPage);
		}
	}
	
	private void save() {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				SQLManager.getSQL("global").setValue(SQLManager.getSQL("global").getUUID(owner), "inv", compile());
				
				Channel.updateAccount(owner);
			}
		});
	}
	
	public String compile() {
		ArrayList<String> itemStrings = new ArrayList<String>();
		for(Item item : items) {
			itemStrings.add(item.compile());
		}
		
		return StringUtils.listToString(itemStrings);
	}
}
