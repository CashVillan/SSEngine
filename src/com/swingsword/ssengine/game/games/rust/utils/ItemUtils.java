package com.swingsword.ssengine.game.games.rust.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;

public class ItemUtils {
	
	public static void runDelayBar(final Player player, final long time, final long progress, final int slot) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if (player.isOnline()) {
					if (player.getInventory().getItem(slot) != null) {
						if (progress <= 0) {
							player.getInventory().getItem(slot).setDurability((short) 0);
							player.updateInventory();
						} else {
							player.getInventory().getItem(slot).setDurability((short) (player.getInventory().getItem(slot).getType().getMaxDurability() * (float) ((float) progress / (float) time)));
							player.updateInventory();

							runDelayBar(player, time, progress - 1, slot);
						}
					}
				}
			}
		}, 1L);
	}
	
	public static void removeItem(Inventory inv, ItemStack item) {
		for (int x = 0; x < inv.getSize(); x++) {
			if (inv.getItem(x) != null) {
				if (inv.getItem(x).equals(item)) {
					inv.setItem(x, null);
					break;
				}
			}
		}
	}
	
	public static void removeItem(Player player, ItemStack item, int amount) {
		int amountRemoved = 0;

		for (int i = 0; i < 36; i++) {
			ItemStack olditem = player.getInventory().getItem(i);

			if (olditem != null && olditem.getType().equals(item.getType()) && olditem.getDurability() == item.getDurability()) {
				if (olditem.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName() != null) {
					if (olditem.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
						int item_amount = olditem.getAmount();

						if (item_amount > (amount - amountRemoved)) {
							if ((amount - amountRemoved) > 64) {
								player.getInventory().setItem(i, null);
							} else {
								olditem.setAmount(item_amount - (amount - amountRemoved));
							}
						} else {
							player.getInventory().setItem(i, null);
						}
						player.updateInventory();

						amountRemoved = amountRemoved + item_amount;

						if (amountRemoved >= amount) {
							break;
						}
					}

				} else if (olditem.getItemMeta().getDisplayName() == null && item.getItemMeta().getDisplayName() == null) {
					int item_amount = olditem.getAmount();

					if (item_amount > (amount - amountRemoved)) {
						if ((amount - amountRemoved) > 64) {
							player.getInventory().setItem(i, null);
						} else {
							olditem.setAmount(item_amount - (amount - amountRemoved));
						}
					} else {
						player.getInventory().setItem(i, null);
					}
					player.updateInventory();

					amountRemoved = amountRemoved + item_amount;

					if (amountRemoved >= amount) {
						break;
					}
				}
			}
		}
	}

	public static ItemStack changeType(ItemStack from, Material type) {
		ItemMeta fmeta = from.getItemMeta();
		String name = fmeta.getDisplayName();
		List<String> lore = fmeta.getLore();

		ItemStack newItem = new ItemStack(type, from.getAmount());
		ItemMeta meta = newItem.getItemMeta();
		if (name != null) {
			meta.setDisplayName(name);
		}
		if (lore != null) {
			meta.setLore(lore);
		}
		newItem.setItemMeta(meta);

		return newItem;
	}
	
	public static int getContentAmount(Inventory inv) {
		int amount = 0;
		
		if(inv != null) {
			for(ItemStack all : inv) {
				if(all != null) {
					if(all.getType() != Material.AIR) {
						amount = amount + 1;
					}
				}
			}
		}
		
		return amount;
	}
	
	public static int getAbleSlot(Player player, Material mat, int extra) {
		for(int x = 0; x < player.getInventory().getSize(); x++) {
			if(player.getInventory().getItem(x) != null) {
				if(player.getInventory().getItem(x).getType().equals(mat)) {
					if(player.getInventory().getItem(x).getAmount() + extra <= 64) {
						return x;
					}
				}
			}
		}
		
		return -1;
	}
	
	public static void giveItem(Player player, ItemStack i, Location backup) {
		if(invFull(player, i.getType(), i.getAmount()) == true) {
			player.sendMessage(ChatColor.RED + "Your inventory is full!");
			Item e = player.getWorld().dropItemNaturally(backup, i);
			e.setVelocity(new Vector(0, 0.5f, 0));
					
		} else {
			if(ItemUtils.getAbleSlot(player, i.getType(), i.getAmount()) != -1) {
				player.getInventory().getItem(ItemUtils.getAbleSlot(player, i.getType(), i.getAmount())).setAmount(player.getInventory().getItem(ItemUtils.getAbleSlot(player, i.getType(), i.getAmount())).getAmount() + i.getAmount());
				
			} else {
				player.getInventory().addItem(i);
			}
		}
	}
	
	public static boolean invFull(Player player, Material mat, int extra) {
		for(int x = 0; x < player.getInventory().getSize(); x++) {
			if(player.getInventory().getItem(x) != null) {
				if(player.getInventory().getItem(x).getType().equals(mat)) {
					if(player.getInventory().getItem(x).getAmount() + extra <= 64) {
						return false;
					}
				}
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean containsItem(Player player, ItemStack item, int amount) {
		int foundAmount = 0;
		
		for(int i = 0; i < 36; i++) {
            ItemStack olditem =  player.getInventory().getItem(i);
            
            if(olditem != null && olditem.getType().equals(item.getType()) && olditem.getDurability() == item.getDurability()) {
            		if(olditem.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName() != null) {
            			
            			if(olditem.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
            				foundAmount = foundAmount + olditem.getAmount();
            				
            				if(foundAmount >= amount) {
                				return true;
                			}
            			}
            		} else if(olditem.getItemMeta().getDisplayName() == null && item.getItemMeta().getDisplayName() == null) {
            			foundAmount = foundAmount + olditem.getAmount();
            			
            			if(foundAmount >= amount) {
            				return true;
            			}
            		}
            }
		}
		return false;
	}
	
	public boolean loreContains(ItemStack item, String key) {
		if(item != null && item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
			List<String> lore = item.getItemMeta().getLore();
			
			for(String all : lore) {
				if(all.equalsIgnoreCase(key)) {
					return true;
				}
			}
		}
		
		return false;
	}
		
	public static boolean loreContains(List<String> lore, String key) {
		for(String all : lore) {
			if(ChatColor.stripColor(all).equalsIgnoreCase(key)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static List<String> getLore(ItemStack item) {
		if(item != null && item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
			return item.getItemMeta().getLore();
		}
		return null;
	}
}
