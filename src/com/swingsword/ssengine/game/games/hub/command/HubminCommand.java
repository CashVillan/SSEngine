package com.swingsword.ssengine.game.games.hub.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.games.hub.Hub;

public class HubminCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("hubmin")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("ss.hubmin")) {
					if (args.length == 0) {
						sender.sendMessage(ChatColor.RED + "Usage: /hubmin <option>");
						return false;
					}
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("setspawn")) {
							FileConfiguration config = Hub.plugin.getLoadedMap().getMapConfig();
							Location loc = ((Player) sender).getLocation();
							config.set("spawn.world", loc.getWorld().getName());
							config.set("spawn.x", loc.getX());
							config.set("spawn.y", loc.getY());
							config.set("spawn.z", loc.getZ());
							config.set("spawn.yaw", loc.getYaw());
							config.set("spawn.pitch", loc.getPitch());
							
							Hub.plugin.getLoadedMap().saveMapConfig();
							
							sender.sendMessage(ChatColor.RED + "Spawn set!");
							return true;
						}
						sender.sendMessage(ChatColor.RED + "Invalid option.");
						return false;
					}
					sender.sendMessage(ChatColor.RED + "Usage: /hubmin <option>");
				}
			}
		}
		return false;
	}
}
