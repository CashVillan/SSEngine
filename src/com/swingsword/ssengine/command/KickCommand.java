package com.swingsword.ssengine.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.bungee.Channel;

public class KickCommand implements CommandExecutor {

	@SuppressWarnings({ "deprecation" })
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("kick")) {
			if (sender.hasPermission("ss.kick")) {
				if (args.length >= 2) {
					StringBuilder str = new StringBuilder();
					for (int i = 1; i < args.length; i++) {
						str.append(args[i] + " ");
					}
					String reason = str.toString();
					
					if (Bukkit.getOfflinePlayer(args[0]).isOnline()) {
						Player target = Bukkit.getPlayer(args[0]);
						target.kickPlayer(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Kicked '" + target.getName() + "' for: " + reason + ".");
						return false;
					}
					if (Channel.getServer(Bukkit.getOfflinePlayer(args[0]).getName()) != null) {
						Channel.kickPlayer(args[0], args[1]);
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Kicked '" + args[0] + "' for: " + reason + ".");
						return false;
					}
					sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Player not online!");
					return false;
				}
				sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /kick <player> <reason>");
				return false;
			}
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "No Permission.");
		}
		return false;
	}
}
