package com.swingsword.ssengine.game.games.rust.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.game.games.rust.utils.MathUtils;
import com.swingsword.ssengine.utils.ConfigUtils;

public class SetcodeCommand implements CommandExecutor {
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("setcode")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length == 1) {
					if (player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 10) != null) {
						Location loc = player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 10).getLocation();

						if (loc.getBlock().getType() == Material.IRON_DOOR_BLOCK || loc.getBlock().getType() == Material.WOODEN_DOOR) {
							if (loc.getBlock().getData() >= 8) {
								loc = loc.getBlock().getRelative(BlockFace.DOWN).getLocation();
							}

							if (ConfigUtils.getConfig("data/doors").getConfigurationSection("doors").contains(LocationUtils.RealLocationToString(loc))) {
								if (ConfigUtils.getConfig("data/doors").getString("doors." + LocationUtils.RealLocationToString(loc) + ".owner").equals(player.getUniqueId().toString())) {
									if (args[0].length() == 4 && MathUtils.isInteger(args[0])) {
										FileConfiguration config = ConfigUtils.getConfig("data/doors");
										config.set("doors." + LocationUtils.RealLocationToString(loc) + ".code", args[0]);
										config.set("doors." + LocationUtils.RealLocationToString(loc) + ".people", new ArrayList<String>());
										ConfigUtils.saveConfig(config, "data/doors");
										
										player.sendMessage(ChatColor.GREEN + "The door code has been set to '" + args[0] + "'.");
										return true;
									}
									player.sendMessage(ChatColor.RED + "Usage: /setcode <4 numbers>");
									return false;
								}
								player.sendMessage(ChatColor.RED + "That door is not yours.");
								return false;
							}
							player.sendMessage(ChatColor.RED + "That door is not registered.");
							return false;
						}
						player.sendMessage(ChatColor.RED + "You need to look at a door.");
						return false;
					}
					player.sendMessage(ChatColor.RED + "You need to look at a door.");
					return false;
				}
				player.sendMessage(ChatColor.RED + "Usage: /setcode <4 numbers>");
				return false;
			}
		}
		return false;
	}
}
