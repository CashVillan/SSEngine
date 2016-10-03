package com.swingsword.ssengine.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.utils.ProfileUtils;

public class SettingsCommand implements CommandExecutor { 
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("settings")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (SQLManager.getSQL("global").isConnected()) {
					if (PlayerSessionManager.getSession(player).getAccount().isLoaded()) {
						player.closeInventory();
						ProfileUtils.openProfileSettingsMenu(player);
					}
				}
			}
		}
		return false;
	}

}
