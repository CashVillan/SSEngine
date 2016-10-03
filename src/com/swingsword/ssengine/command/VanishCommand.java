package com.swingsword.ssengine.command;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

	public static ArrayList<String> hidden = new ArrayList<String>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("vanish")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.hasPermission("ss.vanish")) {
					if(!isHidden(player)) {
						for(Player all : Bukkit.getOnlinePlayers()) {
							all.hidePlayer(player);
						}
						hidden.add(player.getName());
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "You're now hidden.");
						
						return true;
					} else {
						for(Player all : Bukkit.getOnlinePlayers()) {
							all.showPlayer(player);
						}
						hidden.remove(player.getName());
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "You're no longer hidden.");
						
						return true;
					}
				}
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "No permission.");
				return false;
			}
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Only players can do that.");
			return false;
		}

		return false;
	}
	
	public static void load(Player player) {
		for(String all : hidden) {
			player.hidePlayer(Bukkit.getPlayer(all));
		}
		hidden.remove(player.getName());
	}
	
	public static void reset(Player player) {
		for(Player all : Bukkit.getOnlinePlayers()) {
			all.showPlayer(player);
			player.showPlayer(all);
		}
		hidden.remove(player.getName());
	}
	
	private boolean isHidden(Player player) {
		return hidden.contains(player.getName());	
	}
}
