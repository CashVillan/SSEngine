package com.swingsword.ssengine.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerSessionManager;

@SuppressWarnings("unused")
public class UnmuteCommand implements CommandExecutor {
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("unmute")) {
			if (sender.hasPermission("ss.mute")) {
				if (args.length == 1) {
					if (SQLManager.getSQL("global").accountExists(args[0])) {
						if (Bukkit.getOfflinePlayer(args[0]).isOnline()) {
							if (!PlayerSessionManager.getSession(Bukkit.getPlayer(args[0])).getAccount().getCache().get("mute").equals("")) {
								PlayerSessionManager.getSession(Bukkit.getPlayer(args[0])).getAccount().getCache().put("mute", "");

								sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA +  "Unmuted '" + Bukkit.getPlayer(args[0]).getName() + "'.");
								return false;
							}
							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Player not muted.");
						} else {
							Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									if (!SQLManager.getSQL("global").getValue(SQLManager.getSQL("global").getUUID(args[0]), "mute").equals("")) {
										SQLManager.getSQL("global").setValue(SQLManager.getSQL("global").getUUID(args[0]), "mute", "");
										
										sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Unmuted '" + Bukkit.getPlayer(args[0]).getName() + "'.");
										
										if (Channel.getServer(Bukkit.getOfflinePlayer(args[0]).getName()) != null) {
											Channel.updateAccount(Bukkit.getOfflinePlayer(args[0]).getName());
										}
										return;
									}
									sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Player not muted.");
								}
							});
						}
						return false;
					}
					sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Player not registered.");
					return false;
				}
				sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /unmute <player>");
				return false;
			}
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "No Permission!");
		}
		return false;
	}
}