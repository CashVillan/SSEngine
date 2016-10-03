package com.swingsword.ssengine.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.utils.ProfileUtils;

public class ProfileCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("profile")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				ProfileUtils.openProfileMenu(player);
			}
		}
		return false;
	}

}
