package com.swingsword.ssengine.game.games.rust.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.swingsword.ssengine.game.games.rust.crafting.InventoryManager;

public class CraftCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("craft")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!player.hasPotionEffect(PotionEffectType.SLOW)) {
					player.openInventory(InventoryManager.craftInv);
					return true;
				}
				player.sendMessage(ChatColor.RED + "You can't craft right now.");
				return false;
			}
		}
		return false;
	}

}
