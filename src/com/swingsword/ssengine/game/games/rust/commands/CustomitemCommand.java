package com.swingsword.ssengine.game.games.rust.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.utils.ItemUtils;

public class CustomitemCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("customitem")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				
				if (player.isOp()) {
					if(args.length < 1) {
						player.sendMessage(ChatColor.RED + "Usage: /customitem <itemdata>");
						
					} else {
						player.getInventory().addItem(ItemUtils.itemStackFromString(args[0].replace("_", " &f")));
					}
					
					return true;
				}
				player.sendMessage(ChatColor.RED + "No permission.");
			}
		}
		return false;
	}
}
