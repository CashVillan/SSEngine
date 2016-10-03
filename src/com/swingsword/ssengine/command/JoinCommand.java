package com.swingsword.ssengine.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.server.ServerManager;

public class JoinCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, final String[] arg3) {
		Player player = null;
		
		if(arg0 instanceof Player) {
			player = (Player) arg0;
		}
	
		if(player != null) {
			if(arg3.length == 0) {
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /join <server>");
				
			} else {
				player.openInventory(ServerManager.getJoinInventory(player, arg3[0]));
			}
		}
		return false;
	}
}
