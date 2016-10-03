package com.swingsword.ssengine.game.games.dev;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
		sender.sendMessage("Test!");
		
		return false;
	}
}
