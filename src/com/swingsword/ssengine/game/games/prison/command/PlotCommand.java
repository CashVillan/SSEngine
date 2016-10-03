package com.swingsword.ssengine.game.games.prison.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.games.prison.plot.Plot;

import net.md_5.bungee.api.ChatColor;

public class PlotCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("plot")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length >= 1) {
					player.sendMessage("invalid plot command");
					return false;
				} else {
					Plot plot = new Plot(player.getUniqueId().toString());
					player.teleport(plot.world.getSpawnLocation());
					player.sendMessage(ChatColor.RED + plot.world.getSpawnLocation().toString());
				}
			}
		}
		return false;
	}

}
