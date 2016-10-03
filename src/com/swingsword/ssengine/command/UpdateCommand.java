package com.swingsword.ssengine.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.socket.ClientInstance;
import com.swingsword.ssengine.utils.ConfigUtils;

import socket.SocketMessage;

public class UpdateCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("update")) {
			if (sender.hasPermission("ss.update")) {
				if (args.length >= 1) {
					FileConfiguration config = ConfigUtils.getConfig("config");
					if (config.contains("files." + args[0] + ".http") && config.contains("files." + args[0] + ".type")) {
						Boolean global = false;
						Boolean reboot = false;
						
						for (int i = 1; i < args.length; i++) {
							if (args[i].equals("-g")) {
								global = true;
							} else if (args[i].equals("-r")) {
								reboot = true;
							}
						}

						if (global) {
							ClientInstance.sc.sendMessage(new SocketMessage("update", " ", args[0]).toString());
						} else {
							ClientInstance.sc.sendMessage(new SocketMessage("update", MasterPlugin.getServerName(), args[0]).toString());
						}

						if (reboot) {
							if (global) {
								ClientInstance.sc.sendMessage(new SocketMessage("stop", " ", "").toString());
							} else {
								ClientInstance.sc.sendMessage(new SocketMessage("stop", MasterPlugin.getServerName(), "").toString());
							}
						}
						
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "'" + args[0] + "' sent to " + config.getString("files." + args[0] + ".type") + ".");
						return true;
					}
					sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "File not found!");
					return false;
				}
				sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /update <name> -r -g -<gametype>");
				return false;
			}
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "No Permission!");
		}
		return false;
	}
}
