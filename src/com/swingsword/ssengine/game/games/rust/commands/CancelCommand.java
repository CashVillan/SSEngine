package com.swingsword.ssengine.game.games.rust.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.swingsword.ssengine.game.games.rust.crafting.Crafting;
import com.swingsword.ssengine.game.games.rust.crafting.InventoryManager;

public class CancelCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("cancel") || cmd.getName().equalsIgnoreCase("c")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!Crafting.settingAmount.contains(player.getName())) {
					if (Crafting.playerCraft.containsKey(player.getName())) {
						for (ItemStack all : InventoryManager.itemNeeded.get(Crafting.playerCraft.get(player.getName()))) {
							player.getInventory().addItem(all.clone());
						}

						Crafting.playerCraft.remove(player.getName());
						Crafting.playerCraftAmount.remove(player.getName());
						player.sendMessage(ChatColor.GRAY + "You stopped crafting.");
						player.setExp(0);
						return true;
					}
					player.sendMessage(ChatColor.RED + "You are not currently crafting.");
					return false;
				}
			}
		}
		return false;
	}
}
