package com.swingsword.ssengine.game.games.rust.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.utils.ConfigUtils;

public class CreateorespawnzoneCommand implements CommandExecutor {
	
	@SuppressWarnings({ "deprecation", "static-access" })
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("createorespawnzone")) {
			if (sender instanceof Player) {
				if (sender.isOp()) {
					if (args.length > 0) {
						try {
							if (Rust.getInstance().we.getAPI().getSession((Player) sender).getRegion().getMinimumPoint() != null && Rust.getInstance().we.getAPI().getSession((Player) sender).getRegion().getMaximumPoint() != null) {
								FileConfiguration config = ConfigUtils.getConfig("zones");
								config.set("spawnZones." + LocationUtils.locationToString(Rust.getInstance().we.getAPI().getSession((Player) sender).getRegion().getMinimumPoint()) + ";" + LocationUtils.locationToString(Rust.getInstance().we.getAPI().getSession((Player) sender).getRegion().getMaximumPoint()), Integer.parseInt(args[0]));
								ConfigUtils.saveConfig(config, "zones");
								sender.sendMessage(ChatColor.GREEN + "Added Spawn zone.");
								return true;
							}
							sender.sendMessage(ChatColor.RED + "Make a WE selection.");
							return false;
						} catch (IncompleteRegionException e) {
							sender.sendMessage(ChatColor.RED + "Make a WE selection.");
						}
						return false;
					}
					sender.sendMessage(ChatColor.RED + "Wrong layout, use: '/createorespawnzone <amount>'");
					return false;
				}
				sender.sendMessage(ChatColor.RED + "No permission.");
				return false;
			}
		}
		return false;
	}
}
