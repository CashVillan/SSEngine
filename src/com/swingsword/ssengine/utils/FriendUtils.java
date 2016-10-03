package com.swingsword.ssengine.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerAccount;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.stats.StatManager;

public class FriendUtils {
	
	public static HashMap<UUID, Integer> getFriends(Player player) {
		String friendString = (String) PlayerSessionManager.getSession(player).getAccount().getCache().get("friends");

		return deformat(friendString);
	}
	
	public static HashMap<UUID, Integer> deformat(String friends) {
		HashMap<UUID, Integer> friendMap = new HashMap<UUID, Integer>();
		
		if(friends != null) {
			String[] friendList = friends.split(";/");
			
			if(!friends.equals("")) {
				for(String friend : friendList) {
					friendMap.put(UUID.fromString(friend.split(":")[0]), Integer.parseInt(friend.split(":")[1]));
				}
			}
		}
		
		return friendMap;
	}
	
	public static String format(HashMap<UUID, Integer> friends) {
		String friendString = "";
		
		for(UUID friend : friends.keySet()) {
			if(friendString == "") { 
				friendString = friend.toString() + ":" + friends.get(friend);
			} else {
				friendString = friendString + ";/" + friend.toString() + ":" + friends.get(friend);
			}
		}
		
		return friendString;
	}
	
	public static void addFriend(final Player player, final String name, final UUID target, final ItemStack item) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				PlayerAccount playerAcc = PlayerSessionManager.getSession(player).getAccount();
				PlayerAccount targetAcc = null;
				
				if(Bukkit.getOfflinePlayer(target).isOnline()) {
					targetAcc = PlayerSessionManager.getSession(Bukkit.getPlayer(target)).getAccount();
				}
				
				if(playerAcc.getFriends().containsKey(target)) {
					if(playerAcc.getFriends().get(target) == 0) {
						HashMap<UUID, Integer> playerFriends = playerAcc.getFriends();
						
						playerFriends.put(target, 1);
						playerAcc.getCache().put("friends", format(playerFriends));
						playerAcc.saveFriends();
						
						player.sendMessage(ChatColor.GREEN + "Accepted friend request!");
						
						if(StatManager.getStat(player, "b_friends") < 10 && StatManager.getStat(player, "b_friends") < playerFriends.size()) {
							StatManager.setStat(player, "b_friends", playerFriends.size());
						}
						
					} else {
						String message = ChatColor.RED + "You are already friends with " + ChatColor.YELLOW + name;

						if(item == null) {
							player.sendMessage(message);
						} else {
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName(message);
							item.setItemMeta(meta);
						}
					}
					
				} else {
					if(target != null) {
						HashMap<UUID, Integer> playerFriends = playerAcc.getFriends();
						playerFriends.put(target, 1);
						playerAcc.getCache().put("friends", format(playerFriends));
						playerAcc.saveFriends();
	
						if(targetAcc != null) {
							HashMap<UUID, Integer> targetFriends = targetAcc.getFriends();
							targetFriends.put(player.getUniqueId(), 0);
							targetAcc.getCache().put("friends", format(targetFriends));
							targetAcc.saveFriends();
						} else {
							HashMap<UUID, Integer> targetFriends = deformat((String) SQLManager.getSQL("global").getValue(target, "friends"));
							targetFriends.put(player.getUniqueId(), 0);
							SQLManager.getSQL("global").setValue(target, "friends", format(targetFriends));
						}
						
						String message = ChatColor.GREEN + "Friend request sent to " + ChatColor.YELLOW + name;
						
						if(StatManager.getStat(player, "b_friends") < 10 && StatManager.getStat(player, "b_friends") < playerFriends.size()) {
							StatManager.setStat(player, "b_friends", playerFriends.size());
						}
						
						Notification.sendNotification(player, message);
					}
				}
			}
		});
	}
	
	public static void removeFriend(final Player player, final UUID target) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				PlayerAccount playerAcc = PlayerSessionManager.getSession(player).getAccount();
				PlayerAccount targetAcc = null;
				
				if(Bukkit.getOfflinePlayer(target).isOnline()) {
					targetAcc = PlayerSessionManager.getSession(Bukkit.getPlayer(target)).getAccount();
				}
				
				if(playerAcc.getFriends().containsKey(target)) {
					HashMap<UUID, Integer> playerFriends = playerAcc.getFriends();
					playerFriends.remove(target);
					playerAcc.getCache().put("friends", format(playerFriends));
					playerAcc.saveFriends();
					
					if(targetAcc != null) {
						HashMap<UUID, Integer> targetFriends = targetAcc.getFriends();
						targetFriends.remove(player.getUniqueId());
						targetAcc.getCache().put("friends", format(targetFriends));
						targetAcc.saveFriends();
					} else {
						HashMap<UUID, Integer> targetFriends = deformat((String) SQLManager.getSQL("global").getValue(target, "friends"));
						targetFriends.remove(player.getUniqueId());
						SQLManager.getSQL("global").setValue(target, "friends", format(targetFriends));
					}
					
					player.sendMessage(ChatColor.GREEN + "Removed friend.");
				}
			}
		});
	}
	
	public static void openFriendInventory(final Player player, final int mode) {
		player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		
		String display = "";
		if(mode == 0) {
			display = LanguageUtils.translate(player, "Friends");
		} else if(mode == 1) {
			display = LanguageUtils.translate(player, "Friends") + " - " + LanguageUtils.translate(player, "Requests");
		} else if(mode == 2) {
			display = LanguageUtils.translate(player, "Friends") + " - " + LanguageUtils.translate(player, "Delete Friends");
		}
		
		final Inventory friendsInv = Bukkit.createInventory(player, 54, display);
		player.openInventory(friendsInv);
		
		friendsInv.setItem(0, ItemUtils.createItem(Material.BEACON, 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Friends"), null));
		friendsInv.setItem(2, ItemUtils.createItem(Material.BOOK_AND_QUILL, 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Add Friends"), null));
		ItemStack playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Return to your Profile"), null);
		SkullMeta meta = (SkullMeta) playerHead.getItemMeta(); meta.setOwner(player.getName()); playerHead.setItemMeta(meta);
		friendsInv.setItem(4, playerHead);
		friendsInv.setItem(6, ItemUtils.createItem(Material.TNT, 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Delete Friends"), null));
		friendsInv.setItem(8, ItemUtils.createItem(Material.EYE_OF_ENDER, 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Requests"), null));
		
		if(mode == 0) {
			friendsInv.setItem(0, ItemUtils.addGlow(friendsInv.getItem(0)));
		} else if(mode == 2) {
			friendsInv.setItem(6, ItemUtils.addGlow(friendsInv.getItem(6)));
		} else {
			friendsInv.setItem(8, ItemUtils.addGlow(friendsInv.getItem(8)));
		}
		
		for(final UUID all : PlayerSessionManager.getSession(player).getAccount().getFriends().keySet()) {
			Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					final HashMap<String, Object> data = SQLManager.getSQL("global").getValues(all);
					
					final String name = (String) data.get("name");
					
					final boolean youAccepted;
					final boolean hasAccepted;
					
					if(PlayerSessionManager.getSession(player).getAccount().getFriends().get(all) == 1) {
						youAccepted = true;	
					} else {
						youAccepted = false;
					}
					if(FriendUtils.deformat((String) data.get("friends")).get(player.getUniqueId()) == 1) {
						hasAccepted = true;
					} else {
						hasAccepted = false;
					}
					
					MasterPlugin.getMasterPlugin().channel.requestPlayerServer(name);
					
					Thread t = new Thread(new Runnable() {
						public void run() {
							int triesLeft = 250;
							
							while(!Channel.playerServer.containsKey(name) && triesLeft != -1) {
								try {
									Thread.sleep(1);
									triesLeft = triesLeft - 1;
									
									ItemStack friendItem = null;
									
									if(Channel.playerServer.containsKey(name)) {
										final String server = Channel.playerServer.get(name);
										
										friendItem = getFriendItem(player, data, youAccepted, hasAccepted, server);
										Channel.playerServer.remove(name);
										
									} else if(triesLeft == 0) {
										friendItem = getFriendItem(player, data, youAccepted, hasAccepted, null);
									}
									
									if(friendItem != null) {
										triesLeft = -1;
										
										final ItemStack finalFriendItem = friendItem;
										
										Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
											public void run() {
												if(((mode == 0 || mode == 2) && hasAccepted && youAccepted) || (mode == 1 && (!hasAccepted || !youAccepted))) {
													friendsInv.setItem(InventoryUtils.getFirstFreeFrom(friendsInv, 18), finalFriendItem);
												}
											}
										});
									}
									
								} catch (InterruptedException e) { }
							}
						}
					});
					t.run();
				}
			});
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(InventoryUtils.getFirstFreeFrom(friendsInv, 18) == 18) {
					if(mode != 1) {
						friendsInv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.WHITE + LanguageUtils.translate(player, "No friends") + ".", null));
					} else {
						friendsInv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.WHITE + LanguageUtils.translate(player, "No requests") + ".", null));
					}
				}
			}
		}, 10);
	}
	
	public static ItemStack getFriendItem(Player player, HashMap<String, Object> friendData, boolean youAccepted, boolean hasAccepted, String server) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		
		meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + friendData.get("name"));
		meta.setOwner((String) friendData.get("name"));
		
		List<String> lore = new ArrayList<String>();
		
		if(!youAccepted) {
			lore.add(ChatColor.GRAY + "Request is pending");
			lore.add("");
			lore.add(ChatColor.YELLOW + "Left-Click to " + ChatColor.GREEN + "accept");
			lore.add(ChatColor.YELLOW + "Right-Click to " + ChatColor.RED + "deny");
			
		} else if(!hasAccepted) {
			lore.add(ChatColor.GRAY + "Request is pending");
			lore.add("");
			lore.add(ChatColor.YELLOW + "Click to cancel request");
			
		} else if(youAccepted && hasAccepted) {
			if(server != null) {
				lore.add("&7&lStatus: &aOnline".replace("&", ChatColor.COLOR_CHAR + ""));
				lore.add(ChatColor.GRAY + "Playing " + ChatColor.YELLOW + server);
				
			} else {
				lore.add("&7&lStatus: &cOffline".replace("&", ChatColor.COLOR_CHAR + ""));
				lore.add(ChatColor.GRAY + "Last seen " + getLastOnlineString((String) friendData.get("lastOnline")));
			}
			
			lore.add("");
			if(friendData.get("rank") == "") {
				lore.add(ChatColor.GRAY + "Rank: No rank");
			} else {
				lore.add(ChatColor.GRAY + "Rank: " + friendData.get("rank"));
			}
			lore.add(ChatColor.GRAY + "Credits: " + ChatColor.YELLOW + friendData.get("credits"));
			lore.add(ChatColor.GRAY + "Level: " + ChatColor.GOLD + "" + ExpUtils.getLevel(Integer.parseInt((String) friendData.get("exp"))) + ChatColor.GRAY + " " + "(" + ChatColor.GREEN + ExpUtils.getCurrentExp(Integer.parseInt((String) friendData.get("exp"))) + ChatColor.GRAY + "/" + ChatColor.GREEN + ExpUtils.expNeeded(ExpUtils.getLevel(Integer.parseInt((String) friendData.get("exp"))) + 1) + ChatColor.GRAY + ")");
			lore.add("");
			lore.add(ChatColor.YELLOW + "Click to view " + friendData.get("name") + "'s profile.");
		
		}
		
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static String getLastOnlineString(String lastOnline) {
		String online = ChatColor.GRAY + "some time ago";
		
		SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd_HH:mm:ss");  
		Date d1 = null;
		Date d2 = null;
		try {
		    d1 = format.parse(lastOnline.split(" ")[0]);
		    d2 = new Date();
		} catch (ParseException e) { }    

		if(d2 != null && d1 != null) {
			long diff = d2.getTime() - d1.getTime();
			long diffMinutes = diff / (60 * 1000) % 60;       
			long diffHours = diff / (60 * 60 * 1000);             
			
			if(lastOnline.split(" ").length > 1) {
				if(diffHours > 0) {
					online = diffHours + " hours ago playing " + lastOnline.split(" ")[1];
				} else {
					online = diffMinutes + " minutes ago playing " + lastOnline.split(" ")[1];
				}
			} else {
				online = diffMinutes + " minutes ago";
			}
		}
		
		return online;
	}
}
