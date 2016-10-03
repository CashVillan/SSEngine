package com.swingsword.ssengine.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerSessionManager;

public class InboxUtils {
	
	public static HashMap<String, String> getMessages(UUID target) {
		String messageString = (String) SQLManager.getSQL("global").getValue(target, "inbox");
		
		if(messageString != null && !messageString.equals("")) {
			return deformat(messageString);
		} else {
			return new HashMap<String, String>();
		}
	}
	
	public static void addMessage(UUID target, Player player, String message) {
		HashMap<String, String> messages = getMessages(target);
		messages.put(player.getName(), message + "~;" + new SimpleDateFormat("dd/M/yyyy hh;mm a").format(new Date()));
		SQLManager.getSQL("global").setValue(target, "inbox", format(messages));
		
		if(Bukkit.getOfflinePlayer(target).isOnline()) {
			InboxUtils.reopenInbox(Bukkit.getPlayer(target));
		}
	}

	public static void removeMessage(UUID target, String sender) {
		HashMap<String, String> messages = getMessages(target);
		messages.remove(sender);
		SQLManager.getSQL("global").setValue(target, "inbox", format(messages));
		
		if(Bukkit.getOfflinePlayer(target).isOnline()) {
			InboxUtils.reopenInbox(Bukkit.getPlayer(target));
		}
	}
	
	public static HashMap<String, String> deformat(String messageString) {
		HashMap<String, String> messages = new HashMap<String, String>();
		
		if(messageString != null) {
			String[] messageList = messageString.split(";/");
			
			for(String messageComponent : messageList) {
				String sender = messageComponent.split(":")[0];
				
				String message = "";
				if(messageComponent.split(":").length > 1) {
					message = messageComponent.split(":")[1];
				}
				
				messages.put(sender, message);
			}
		}
		
		return messages;
	}
	
	public static String format(HashMap<String, String> messages) {
		String messageString = "";
		
		for(String sender : messages.keySet()) {
			if(messageString == "") { 
				messageString = sender + ":" + messages.get(sender);
			} else {
				messageString = messageString + ";/" + sender + ":" + messages.get(sender);
			}
		}
		
		return messageString;
	}
	
	public static Inventory getInboxInv(final Player player, final int mode) {
		String display = LanguageUtils.translate(player, "Inbox");
		if(mode == 1) {
			display = LanguageUtils.translate(player, "Inbox") + " - " + LanguageUtils.translate(player, "Delete Messages");
		}
		
		final Inventory inv = Bukkit.createInventory(null, 54, display);
		
		inv.setItem(0, ItemUtils.createItem(Material.getMaterial(38), 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Inbox"), null));
		inv.setItem(2, ItemUtils.createItem(Material.getMaterial(386), 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Send a Message"), null));
		ItemStack playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Return to your Profile"), null);
		SkullMeta meta = (SkullMeta) playerHead.getItemMeta(); meta.setOwner(player.getName()); playerHead.setItemMeta(meta);
		inv.setItem(4, playerHead);
		inv.setItem(6, ItemUtils.createItem(Material.getMaterial(46), 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Delete Messages"), null));
		String msgToggleType = "enable";
		if(PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("msg").equals("1")) {
			msgToggleType = "disable";
		}
		inv.setItem(8, ItemUtils.createItem(Material.getMaterial(323), 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + LanguageUtils.translate(player, "Click to") + " " + LanguageUtils.translate(player, msgToggleType) + " " + LanguageUtils.translate(player, "messages to display in chat"), null));
		
		if(mode == 1) {
			inv.setItem(6, ItemUtils.addGlow(inv.getItem(6)));
		} else {
			inv.setItem(0, ItemUtils.addGlow(inv.getItem(0)));
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				HashMap<String, String> messages = getMessages(player.getUniqueId());
				
				if(mode == 0) {
					for(String sender : messages.keySet()) {
						try {
							Date dateSent = new SimpleDateFormat("dd/M/yyyy hh;mm a").parse(messages.get(sender).split("~;")[1]);
							long tillRemove = (60 * 60 * 24 * 7) - (new Date().getTime() - dateSent.getTime()) / 1000;
							
							if(tillRemove < 0) {
								removeMessage(player.getUniqueId(), sender);
								
							} else {
								ItemStack item = ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + "Message from " + sender, Arrays.asList(ChatColor.WHITE + messages.get(sender).split("~;")[0], ChatColor.GRAY + messages.get(sender).split("~;")[1].replace(";", ":"), ChatColor.GRAY + "Expires in " + (int) (((tillRemove / 60) / 60) / 24) + " days", "", ChatColor.YELLOW + "Left-Click to open " + sender + "'s Profile.", ChatColor.YELLOW + "Right-Click to reply."));
								
								inv.setItem(InventoryUtils.getFirstFreeFrom(inv, 18), item);
							}
							
						} catch (ParseException e) { }
					}
					
					if(Channel.playerSessionRequests.containsKey(player.getName())) {
						List<String> remove = new ArrayList<String>();
						for(String requests : Channel.playerSessionRequests.get(player.getName()).split(";/")) {
							String sender = requests.split("/;")[0];
							if(requests.contains("/;")) {
								String date = requests.split("/;")[1].replace("_", " ");
								boolean invite = Boolean.parseBoolean(date.split("~")[1]);
								date = date.split("~")[0];
										
								if(Channel.getServer(sender) != null) {
									if(!invite) {
										ItemStack item = ItemUtils.createItem(Material.EMPTY_MAP, 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + "Request from " + sender, Arrays.asList(ChatColor.WHITE + sender + " wants to join your session.", ChatColor.GRAY + date, "", ChatColor.YELLOW + "Left-Click to accept request.", ChatColor.YELLOW + "Right-Click to open " + sender + "'s Profile."));
								
										inv.setItem(InventoryUtils.getFirstFreeFrom(inv, 18), item);
									} else {
										ItemStack item = ItemUtils.createItem(Material.EMPTY_MAP, 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + "Invite from " + sender, Arrays.asList(ChatColor.WHITE + sender + " wants you to join their session.", ChatColor.GRAY + date, "", ChatColor.YELLOW + "Left-Click to accept invite.", ChatColor.YELLOW + "Right-Click to open " + sender + "'s Profile."));
										
										inv.setItem(InventoryUtils.getFirstFreeFrom(inv, 18), item);
									}
								} else {
									remove.add(requests);
								}
							}
						}
						for(String all : remove) {
							 Channel.playerSessionRequests.put(player.getName(), Channel.playerSessionRequests.get(player.getName()).replace(all, ""));
						}
					}
					
				} else if(mode == 1) {
					for(String sender : messages.keySet()) {
						ItemStack item = ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.WHITE + "" + ChatColor.BOLD + "Message from " + sender, Arrays.asList(ChatColor.AQUA + messages.get(sender), "", ChatColor.YELLOW + "Click to remove message."));
						
						inv.setItem(InventoryUtils.getFirstFreeFrom(inv, 18), item);
					}
				}
				
				if(InventoryUtils.getFirstFreeFrom(inv, 18) == 18) {
					inv.setItem(18, ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.WHITE + "Empty.", null));
				}
			}
		});
		
		return inv;
	}
	
	public static void reopenInbox(final Player player) {
		if(player.getOpenInventory().getTopInventory().getTitle() != null) {
			String title = player.getOpenInventory().getTopInventory().getTitle();
			Inventory inv = null;
			
			if(title.contains("Inbox")) {
				inv = InboxUtils.getInboxInv(player, 0);
				
			} else if(title.toLowerCase().contains("messages")) {
				inv = InboxUtils.getInboxInv(player, 1);
				
			}
			
			if(inv != null) {
				final Inventory finalInv = inv;
				
				Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						player.openInventory(finalInv);
					}
				});
			}
		}
	}
}
