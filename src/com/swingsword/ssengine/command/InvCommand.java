package com.swingsword.ssengine.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.inventory.PlayerInventory;

public class InvCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("inv")) {
			if (sender instanceof Player) {
				final Player player = (Player) sender;
				
				if (SQLManager.getSQL("global").isConnected()) {
					if (args.length == 1) {
						openInv(player, args[0]);
						return false;
					}
					sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage /inv <player>");
					return false;
				}
				sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Could not connect to the database.");
			}
		}
		return false;
	}
	
	public static void openInv(final Player player, final String target) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			@Override
			public void run() {
				if (SQLManager.getSQL("global").accountExists(target)) {
					new PlayerInventory(target).openInventory(player, 1);
					return;
				}
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Player not registered.");
			}
		});
	}
}
