package com.swingsword.ssengine.game.games.rust.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuicideCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("suicide")) {
			if (sender instanceof Player) {
				((Player) sender).damage(((Player) sender).getHealth());
			}
		}
		return false;
	}
}
