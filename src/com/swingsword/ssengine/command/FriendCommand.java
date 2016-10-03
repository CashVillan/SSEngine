package com.swingsword.ssengine.command;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.utils.FriendUtils;

public class FriendCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		Player player = null;
		
		if(arg0 instanceof Player) {
			player = (Player) arg0;
		}
		
		if(arg2.equalsIgnoreCase("friend")) {
			if(player != null) {
				if(arg3.length == 0) {
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Friend Commands:");
					player.sendMessage(ChatColor.AQUA + "[/friends] Open your friend inventory.");
					player.sendMessage(ChatColor.AQUA + "[/friend add <player>] Add a friend.");
					player.sendMessage(ChatColor.AQUA + "[/friend remove <player>] Remove a friend.");
					
				} else {
					if(arg3[0].equalsIgnoreCase("add")) {
						if(arg3.length == 2) {
							addFriend(player, arg3[1], null);
							
						} else {
							player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /friend add <player>");
						}
						
					} else if(arg3[0].equalsIgnoreCase("remove")) {
						if(arg3.length == 2) {
							removeFriend(player, arg3[1]);
							
						} else {
							player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /friend remove <player>");
						}
					}
				}
			}
			
		} else if(arg2.equalsIgnoreCase("friends")) {
			FriendUtils.openFriendInventory(player, 0);
		}

		return false;
	}
	
	//Methods
	
	public static void addFriend(final Player player, final String target, final ItemStack item) {
		if(!player.getName().equalsIgnoreCase(target)) {
			if(PlayerSessionManager.getSession(player).getAccount().getFriends().size() < 36) {
				Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						HashMap<String, Object> targetData = SQLManager.getSQL("global").getValues(SQLManager.getSQL("global").getUUID(target));
						
						if(player.isOnline()) {
							if(targetData.size() > 0) {
								if(FriendUtils.deformat((String) targetData.get("friends")).size() < 36) {
									FriendUtils.addFriend(player, target, UUID.fromString((String) targetData.get("uuid")), item);
									
								} else {
									String message = ChatColor.RED + "That person has too many friends.";
									
									if(item == null) {
										player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + message);
									} else {
										ItemMeta meta = item.getItemMeta();
										meta.setDisplayName(message);
										item.setItemMeta(meta);
									}
								}
							} else {
								String message = ChatColor.RED + "0 results for " + ChatColor.YELLOW + target;
								
								if(item == null) {
									player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + message);
								} else {
									ItemMeta meta = item.getItemMeta();
									meta.setDisplayName(message);
									item.setItemMeta(meta);
								}
							}
						}
					}
				});
			} else {
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "You have too many friends.");
			}
		} else {
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "You can't add yourself as a friend.");
		}
	}
	
	public static void removeFriend(final Player player, final String target) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				UUID targetUUID = SQLManager.getSQL("global").getUUID(target);
				
				if(player.isOnline()) {
					FriendUtils.removeFriend(player, targetUUID);
				}
			}
		});
	}
}
