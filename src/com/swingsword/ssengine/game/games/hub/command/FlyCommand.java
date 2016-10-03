package com.swingsword.ssengine.game.games.hub.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.player.PlayerSessionManager;

public class FlyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("ss.donator")) {
				boolean flying = PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("fly") != null && PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("fly").equals("1");
				
				if(PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("fly") != null) {
					PlayerSessionManager.getSession(player).getAccount().profileSettings.setSetting("fly", flying ? "0" : "1");
					player.setAllowFlight(!flying);
					
				} else {
					PlayerSessionManager.getSession(player).getAccount().profileSettings.setSetting("fly", "1");
					player.setAllowFlight(true);
				}
				
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Toggled flight " + (!flying ? "on" : "off") + ".");
				
			} else {
				player.sendMessage(ChatColor.RED + "You need to donate to unlock this perk.");
			}
		}
		return false;
	}
}