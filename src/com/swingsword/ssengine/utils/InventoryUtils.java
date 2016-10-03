package com.swingsword.ssengine.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.player.PlayerAccount;
import com.swingsword.ssengine.player.PlayerProfileSettings;
import com.swingsword.ssengine.player.PlayerSessionManager;

public class InventoryUtils {

	public static int getSlotsNeeded(int items) {
		for(int x = items; x % 9 != 0 || x == 0; x++) {
			items = x;
		}

		return items + 1;
	}

	public static int getFirstFreeFrom(Inventory inv, int slot) {
		for(int x = slot; x < inv.getSize(); x++) {
			if(inv.getItem(x) == null) {
				return x;
			}
		}
		return -1;
	}
	
	public static Inventory getEmptyPlayerInventory(Player player, String target) {
		return Bukkit.createInventory(null, 27, target + "'s " +  LanguageUtils.translate(player, "Profile"));
	}
	
	public static void openPlayerInventory(final Player player, final HashMap<String, Object> playerData) {
		if(player.getName().equals(playerData.get("name"))) {
			ProfileUtils.openProfileMenu(player);
			
			return;
		}
		
		if(BlockUtils.deformat((String) playerData.get("blocks")).contains(player.getUniqueId()) && new PlayerAccount(playerData).profileSettings.getSetting("vb").equals("0")) {
			player.sendMessage(ChatColor.RED + LanguageUtils.translate(player, "You can't view this profile"));
			
			return;
		}
		
		Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				final Inventory inv = getEmptyPlayerInventory(player, (String) playerData.get("name"));
				
				inv.setItem(11, ItemUtils.createItem(Material.DIAMOND, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(player, "Achievements"), Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(player, "Click to view Achievements"))));
				inv.setItem(12, ItemUtils.createItem(Material.CHEST, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(player, "Inventory"), Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(player, "Click to view Inventory"))));
				inv.setItem(14, ItemUtils.createItem(Material.BOOK_AND_QUILL, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(player, "Send a Message"), Arrays.asList("", ChatColor.YELLOW + LanguageUtils.translate(player, "Click to send a Message"))));
				
				inv.setItem(13, new PlayerAccount(playerData).toItemStack(player, Arrays.asList("", ChatColor.GRAY + LanguageUtils.translate(player, "Loading") + "...")));
				
				Thread t = new Thread(new Runnable() {
					public void run() {
						List<String> extra = new ArrayList<String>();
						extra.add("");
						
						String server = Channel.getServer((String) playerData.get("name"));
						if(server != null) {
							extra.add(ChatColor.GREEN + LanguageUtils.translate(player, "Online") + ChatColor.GRAY + " " + LanguageUtils.translate(player, "playing") + " " + server);
							extra.add("");
							
							if(PlayerProfileSettings.isAllowed(player, new PlayerAccount(playerData), "2")) {
								extra.add(ChatColor.YELLOW + LanguageUtils.translate(player, "Left-Click to invite to Session"));
							}
							if(PlayerProfileSettings.isAllowed(player, new PlayerAccount(playerData), "3")) {
								extra.add(ChatColor.YELLOW + LanguageUtils.translate(player, "Right-Click to request to join Session"));
							}
						} else {
							String lastTime = FriendUtils.getLastOnlineString((String) playerData.get("lastOnline"));
							String lastServer = lastTime.split(" playing ")[1];
							lastTime = lastTime.split(" playing ")[0];
							
							extra.add(ChatColor.RED + LanguageUtils.translate(player, "Offline") + ChatColor.GRAY + " " + LanguageUtils.translate(player, "last seen") + " " + lastTime);
							extra.add(ChatColor.GRAY + LanguageUtils.translate(player, "playing") + " " + lastServer);
						}
						
						inv.setItem(13, new PlayerAccount(playerData).toItemStack(player, extra));
					}
				});
				t.start();
				
				String friend = ChatColor.YELLOW + "Left-Click to add " + (String) playerData.get("name") + " as Friend";
				if(FriendUtils.deformat((String) PlayerSessionManager.getSession(player).getAccount().getCache().get("friends")).containsKey(UUID.fromString(playerData.get("uuid") + "")) && FriendUtils.deformat((String) PlayerSessionManager.getSession(player).getAccount().getCache().get("friends")).get(UUID.fromString(playerData.get("uuid") + "")) == 1) {
					friend = ChatColor.YELLOW + "Left-Click to unfriend " + (String) playerData.get("name");
				}
				String block = ChatColor.YELLOW + "Right-Click to block " + (String) playerData.get("name");
				if(BlockUtils.getBlocks(player).contains(UUID.fromString((String) playerData.get("uuid")))) {
					block = ChatColor.YELLOW + "Right-Click to unblock " + (String) playerData.get("name");
				}
				
				inv.setItem(15, ItemUtils.createItem(Material.REDSTONE_COMPARATOR, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(player, "Options"), Arrays.asList("", friend, block)));

				if(PlayerProfileSettings.isAllowed(player, new PlayerAccount(playerData), "3")) {
					inv.setItem(22, ItemUtils.createItem(Material.BOOK, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translate(player, "Friends List"), Arrays.asList("", ChatColor.YELLOW + "Click to see " + (String) playerData.get("name") + "'s Friends.")));
				}
				
				/*if(FriendUtils.deformat((String) playerData.get("friends")).containsKey(player.getUniqueId())) {
					inv.setItem(15, ItemUtils.itemStackFromString("i=359;n=&bUnfriend " + playerData.get("name") + ";l=[ ,&eClick to unfriend player!]"));
				} else {
					inv.setItem(15, ItemUtils.itemStackFromString("i=359;n=&bFriend " + playerData.get("name") + ";l=[ ,&eClick to friend player!]"));
				}
				
				if(BlockUtils.getBlocks(player).contains(UUID.fromString((String) playerData.get("uuid")))) {
					inv.setItem(16, ItemUtils.itemStackFromString("i=46;n=&bUnblock communications;l=[&7Manage using /block, ,&eClick to unblock player!]"));
				} else {
					inv.setItem(16, ItemUtils.itemStackFromString("i=46;n=&bBlock communications;l=[&7Manage using /block, ,&eClick to block player!]"));
				}*/
				
				//Get player session info
				/*Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						final String name = (String) playerData.get("name");
						
						inv.setItem(12, ItemUtils.createItem(Material.COMPASS, 1, (byte) 0, ChatColor.AQUA + "Play with " + playerData.get("name"), Arrays.asList(ChatColor.GRAY + "Loading...")));

						MasterPlugin.getMasterPlugin().channel.requestPlayerServer(name);
						
						Thread t = new Thread(new Runnable() {
							@SuppressWarnings("unused")
							public void run() {
								int triesLeft = 250;
								
								while(!Channel.playerServer.containsKey(name) && triesLeft != -1) {
									try {
										Thread.sleep(1);
										triesLeft = triesLeft - 1;
										
										String serverName = null;
										
										if(Channel.playerServer.containsKey(name)) {
											final String server = Channel.playerServer.get(name);
											
											serverName = server;
											Channel.playerServer.remove(name);
											
										} else if(triesLeft == 0) {
											inv.setItem(12, ItemUtils.createItem(Material.COMPASS, 1, (byte) 0, ChatColor.AQUA + "Play with " + playerData.get("name"), Arrays.asList("&7&lStatus: &cOffline".replace("&", ChatColor.COLOR_CHAR + ""), FriendUtils.getLastOnlineString((String) playerData.get("lastOnline")))));

											return;
										}
										
										if(serverName != null) {
											triesLeft = -1;
											
											final String finalServerName = serverName;
											
											Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
												public void run() {
													inv.setItem(12, ItemUtils.createItem(Material.COMPASS, 1, (byte) 0, ChatColor.AQUA + "Play with " + playerData.get("name"), Arrays.asList("&7&lStatus: &aOnline".replace("&", ChatColor.COLOR_CHAR + ""), "", ChatColor.YELLOW + "Left-Click to join game", ChatColor.YELLOW + "Right-Click to invite to game")));
												}
											});
										}
										
									} catch (InterruptedException e) { }
								}
							}
						});
						t.run();
					}
				});*/
				
				player.openInventory(inv);
			}
		});
	}
	
	public static ItemStack getStatusPlayerItem(Player player, HashMap<String, Object> data) {
		String server = null;
		if(Bukkit.getOfflinePlayer(UUID.fromString((String) data.get("uuid"))).isOnline()) {
			server = Channel.serverName;
		}
		
		ItemStack item = FriendUtils.getFriendItem(player, data, true, true, server);
		
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		for(int x = lore.size() - 2; x > 2; x--) {
			lore.remove(x);
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
}
