package com.swingsword.ssengine.game.games.rust.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.utils.ConfigUtils;

public class AddspawnCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("addspawn")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.isOp()) {
					FileConfiguration config = ConfigUtils.getConfig("zones");
					List<String> spawns = config.getStringList("spawns");
					spawns.add(LocationUtils.RealLocationToString(player.getLocation()));
					config.set("spawns", spawns);
					ConfigUtils.saveConfig(config, "zones");
					
					player.sendMessage(ChatColor.GREEN + "Added spawn.");
					return true;
				}
				player.sendMessage(ChatColor.RED + "No permission.");
			}
		}
		return false;
	}
}
