package com.swingsword.ssengine.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerAccount;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.utils.InboxUtils;

public class MessageCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		Player player = null;
		
		if(arg0 instanceof Player) {
			player = (Player) arg0;
		}
		
		if(player != null) {
			String target = "";
			String message = "";
			
			if(arg3.length > 1) {
				target = arg3[0];
				for(int x = 1; x < arg3.length; x++) {
					message = message + " " + arg3[x];
				}
								
				sendMessage(player, target, message);
				
			} else {
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /" + arg2 + " <player> <message>");
			}
		}
		
		return false;
	}
	
	public static void sendMessage(final Player player, final String target, String message) {
		if(PlayerSessionManager.getSession(player).getAccount().isMuted()) {
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "You can't send messages when you're muted.");
			
			return;
		}
		
		if(player.getName().toLowerCase().equals(target.toLowerCase())) {
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "You can't send yourself a message.");
			
			return;
		}
		
		if(message.startsWith(" ")) {
			message = message.replaceFirst(" ", "");
		}
		
		final String finalMessage = message;
		
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				UUID targetUUID = SQLManager.getSQL("global").getUUID(target);
				
				if(targetUUID != null) {
					PlayerAccount account = new PlayerAccount(SQLManager.getSQL("global").getValues(targetUUID));
					
					String rank = "";
					try {
						rank = PlayerSessionManager.getSession(player).getAccount().getMainRank().getRankDisplay();
					} catch (Exception e) { }
					
					String sentMsg = ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RESET + rank + ChatColor.AQUA + player.getName() + ChatColor.WHITE + " " + finalMessage;
	
					if(!account.getBlocks().contains(player.getUniqueId())) {
						if(new PlayerAccount(SQLManager.getSQL("global").getValues(SQLManager.getSQL("global").getUUID(target))).profileSettings.getSetting("msg").equals("1")) {
							Channel.sendInboxMessage(player.getName(), target, sentMsg);
						}
						
						player.sendMessage(ChatColor.AQUA + "You" + ChatColor.GRAY + "" + ChatColor.BOLD + " > " + account.getRanksDisplay() + ChatColor.AQUA + target + ChatColor.RESET + "" + ChatColor.WHITE + " " + finalMessage);
						
						InboxUtils.addMessage(targetUUID, player, finalMessage);
						
					} else {
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "You can't send that player a message.");
					}
				} else {
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "That player isn't registered.");
				}
			}
		});
	}
}
