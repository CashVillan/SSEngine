package com.swingsword.ssengine.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.options.OptionHandler;
import com.swingsword.ssengine.options.OptionInventory;
import com.swingsword.ssengine.player.PlayerAccount;
import com.swingsword.ssengine.player.PlayerSessionManager;

public class BlockUtils {

	public static HashSet<UUID> getBlocks(Player player) {
		String friendString = (String) PlayerSessionManager.getSession(player).getAccount().getCache().get("blocks");

		return deformat(friendString);
	}
	
	public static HashSet<UUID> getGotBlocks(Player player) {
		String friendString = (String) PlayerSessionManager.getSession(player).getAccount().getCache().get("gotBlocks");

		return deformat(friendString);
	}
	
	public static HashSet<UUID> deformat(String blocks) {
		HashSet<UUID> blockSet = new HashSet<UUID>();
		
		if(blocks != null) {
			String[] blockList = blocks.split(";/");
			
			if(!blocks.equals("")) {
				for(String block : blockList) {
					blockSet.add(UUID.fromString(block));
				}
			}
		}
		
		return blockSet;
	}
	
	public static String format(HashSet<UUID> blocks) {
		String blockString = "";
		
		for(UUID block : blocks) {
			if(blockString == "") { 
				blockString = block.toString();
			} else {
				blockString = blockString + ";/" + block.toString();
			}
		}
		
		return blockString;
	}
	
	public static Inventory getBlockInv(final Player player, final int mode) {
		String display = "Blocked Users";
		if(mode == 1) {
			display = "Users that Blocked you";
		}
		
		final Inventory inv = Bukkit.createInventory(null, 54, display);
		
		inv.setItem(0, ItemUtils.itemStackFromString("i=46;d=3;n=&f&lBlocked users"));
		inv.setItem(2, ItemUtils.itemStackFromString("i=386;n=&f&lBlock a user"));
		ItemStack playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + "Return to your Profile", null);
		SkullMeta meta = (SkullMeta) playerHead.getItemMeta(); meta.setOwner(player.getName()); playerHead.setItemMeta(meta);
		inv.setItem(4, playerHead);
		inv.setItem(8, ItemUtils.itemStackFromString("i=46;d=3;n=&f&lUsers that blocked you"));
		
		if(PlayerSessionManager.getSession(player).getAccount().profileSettings != null && PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("vb").equals("1")) {
			inv.setItem(6, ItemUtils.itemStackFromString("i=324;n=&f&lClick to disable blocked users to see your profile"));
		} else {
			inv.setItem(6, ItemUtils.itemStackFromString("i=330;n=&f&lClick to allow blocked users to see your profile"));
		}
		
		if(mode == 0) {
			inv.setItem(0, ItemUtils.addGlow(inv.getItem(0)));
		} else {
			inv.setItem(8, ItemUtils.addGlow(inv.getItem(8)));
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(mode == 0) {
					for(UUID block : getBlocks(player)) {
						HashMap<String, Object> cache = SQLManager.getSQL("global").getValues(block);
						
						ItemStack item = InventoryUtils.getStatusPlayerItem(player, cache);
						ItemMeta meta = item.getItemMeta();
						List<String> lore = meta.getLore();
						lore.set(lore.size() - 1, lore.get(lore.size() - 1).replace("Click", "Left-Click"));
						lore.add(ChatColor.YELLOW + "Right-Click to unblock " + cache.get("name") + ".");
						meta.setLore(lore);
						item.setItemMeta(meta);
						
						inv.setItem(InventoryUtils.getFirstFreeFrom(inv, 18), item);
					}
					
				} else if(mode == 1) {
					for(UUID block : getGotBlocks(player)) {
						HashMap<String, Object> cache = SQLManager.getSQL("global").getValues(block);
						
						ItemStack item = InventoryUtils.getStatusPlayerItem(player, cache);
						
						inv.setItem(InventoryUtils.getFirstFreeFrom(inv, 18), item);
					}
				}
			}
		});
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(InventoryUtils.getFirstFreeFrom(inv, 18) == 18) {
					inv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.WHITE + "No users.", null));
				}
			}
		}, 10);
		
		return inv;
	}
	
	public static void addBlock(final Player player, final String name, final UUID target, final ItemStack item) {
		if(!player.getName().equals(name)) {
			final OptionInventory optionInv = new OptionInventory("Block " + name + "?", ChatColor.GREEN + "Confirm", ChatColor.RED + "Cancel", new Runnable() {
				public void run() {
					Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
						public void run() {
							PlayerAccount playerAcc = PlayerSessionManager.getSession(player).getAccount();
							PlayerAccount targetAcc = null;
							
							if(Bukkit.getOfflinePlayer(target).isOnline()) {
								targetAcc = PlayerSessionManager.getSession(Bukkit.getPlayer(target)).getAccount();
							}
							
							if(playerAcc.getBlocks().contains(target)) {
								String message = ChatColor.RED + "You have already blocked " + ChatColor.YELLOW + name;
			
								if(item == null) {
									player.sendMessage(message);
								} else {
									ItemMeta meta = item.getItemMeta();
									meta.setDisplayName(message);
									item.setItemMeta(meta);
								}
								
							} else {
								if(target != null) {
									HashSet<UUID> playerBlocks = playerAcc.getBlocks();
									playerBlocks.add(target);
									playerAcc.getCache().put("blocks", format(playerBlocks));
									playerAcc.saveBlocks();
									
									String message = ChatColor.GREEN + "Blocked " + ChatColor.YELLOW + name;

									Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
										public void run() {
											player.openInventory(BlockUtils.getBlockInv(player, 0));
										}
									});
									
									Notification.sendNotification(player, message);
									
									if(targetAcc != null) {
										HashSet<UUID> targetGotBlocks = deformat((String) targetAcc.getCache().get("gotBlocks"));
										targetGotBlocks.add(player.getUniqueId());
										targetAcc.getCache().put("gotBlocks", format(targetGotBlocks));
										targetAcc.saveGotBlocks();
									} else {
										HashSet<UUID> targetGotBlocks = deformat((String) SQLManager.getSQL("global").getValues(target).get("gotBlocks"));
										targetGotBlocks.add(player.getUniqueId());
										SQLManager.getSQL("global").setValue(target, "gotBlocks", format(targetGotBlocks));
									}
								}
								
								FriendUtils.removeFriend(player, target);
							}
						}
					});
				}
			}, new Runnable() {
				public void run() {
					player.closeInventory();
				}
			});
			OptionHandler.loadOptionInventory(player, optionInv);
			Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					player.openInventory(optionInv.inventory);
				}
			});
			
		} else {
			String message = ChatColor.RED + "You can't block yourself.";
			
			if(item == null) {
				player.sendMessage(message);
			} else {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(message);
				item.setItemMeta(meta);
			}
		}
	}
	
	public static void removeBlock(final Player player, final String name, final UUID target) {
		final OptionInventory optionInv = new OptionInventory("Unblock " + name + "?", ChatColor.GREEN + "Confirm", ChatColor.RED + "Cancel", new Runnable() {
			public void run() {
				Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						PlayerAccount playerAcc = PlayerSessionManager.getSession(player).getAccount();
						PlayerAccount targetAcc = null;
						
						if(Bukkit.getOfflinePlayer(target).isOnline()) {
							targetAcc = PlayerSessionManager.getSession(Bukkit.getPlayer(target)).getAccount();
						}
						
						if(playerAcc.getBlocks().contains(target)) {
							HashSet<UUID> playerBlocks = playerAcc.getBlocks();
							playerBlocks.remove(target);
							playerAcc.getCache().put("blocks", format(playerBlocks));
							playerAcc.saveBlocks();
							
							String message = ChatColor.GREEN + "Unblocked " + ChatColor.YELLOW + name;

							Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									player.openInventory(BlockUtils.getBlockInv(player, 0));
								}
							});
							
							Notification.sendNotification(player, message);
							
							if(targetAcc != null) {
								HashSet<UUID> targetGotBlocks = deformat((String) targetAcc.getCache().get("gotBlocks"));
								targetGotBlocks.remove(player.getUniqueId());
								targetAcc.getCache().put("gotBlocks", format(targetGotBlocks));
								targetAcc.saveGotBlocks();
							} else {
								HashSet<UUID> targetGotBlocks = deformat((String) SQLManager.getSQL("global").getValues(target).get("gotBlocks"));
								targetGotBlocks.remove(player.getUniqueId());
								SQLManager.getSQL("global").setValue(target, "gotBlocks", format(targetGotBlocks));
							}
						}
					}
				});
			}
		}, new Runnable() {
			public void run() {
				player.closeInventory();
			}
		});
		OptionHandler.loadOptionInventory(player, optionInv);
		Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				player.openInventory(optionInv.inventory);
			}
		});
	}
	
	public static void confirmBlock(final Player player, final String name, final UUID uuid, final ItemStack item) {
		if(!player.getName().equals(name)) {
			OptionInventory oinv = new OptionInventory("Block " + name + "?", ChatColor.GREEN + "Confirm", ChatColor.RED + "Cancel", new Runnable() {
				public void run() {
					player.closeInventory();
					addBlock(player, name, uuid, item);
				}
			}, new Runnable() {
				public void run() {
					player.closeInventory();
				}
			});
			
			OptionHandler.loadOptionInventory(player, oinv);
			player.openInventory(oinv.inventory);
			
		} else {
			String message = ChatColor.RED + "You can't block yourself.";
			
			if(item == null) {
				player.sendMessage(message);
			} else {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(message);
				item.setItemMeta(meta);
			}
		}
	}
}
