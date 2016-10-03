package com.swingsword.ssengine.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.options.OptionHandler;
import com.swingsword.ssengine.options.OptionInventory;
import com.swingsword.ssengine.server.ServerManager;

public class HubCommand implements CommandExecutor {

	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, final String[] args) {
		Player player = null;
		
		if(sender instanceof Player) {
			player = (Player) sender;
		}
		
		final Player finalPlayer = player;
		
		if(commandLabel.equalsIgnoreCase("hub")) {
			if(player != null) {
				if(!ServerManager.currentMotd.contains("Hub")) {
					OptionInventory oinv = new OptionInventory("Go to the hub?", ChatColor.GREEN + "Confirm", ChatColor.RED + "Cancel", new Runnable() {
						public void run() {
							finalPlayer.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Going to hub.");
							
							Channel.sendToServer(finalPlayer, MasterPlugin.getMasterPlugin().channel.getBestHub());
						}
					}, new Runnable() {
						public void run() {
							finalPlayer.closeInventory();
						}
					});
					OptionHandler.loadOptionInventory(player, oinv);
					player.openInventory(oinv.inventory);
					
				} else {
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "You are already on a hub.");
				}
			}
		}

		return false;
	}
}
