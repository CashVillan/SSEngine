package com.swingsword.ssengine.command;

import java.util.Calendar;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.utils.IntegerUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class MuteCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("mute")) {
			if (sender.hasPermission("ss.mute")) {
				if (args.length >= 3) {
					if (SQLManager.getSQL("global").accountExists(args[0])) {
						if (IntegerUtils.convertTime(args[1]) != 0) {
							StringBuilder str = new StringBuilder();
							for (int i = 2; i < args.length; i++) {
								str.append(args[i] + " ");
							}
							final String reason = str.toString();
							
							Calendar now = Calendar.getInstance();
							Integer secondsmuted = IntegerUtils.convertTime(args[1]);
							now.add(Calendar.SECOND, secondsmuted);
							final long date = now.getTime().getTime();
							
							final HashMap<String, String> entries = new HashMap<>();
							entries.put("muted", "1");
							entries.put("reason", reason);
							entries.put("date", date + "");
							
							if (Bukkit.getOfflinePlayer(args[0]).isOnline()) {
								PlayerSessionManager.getSession(Bukkit.getPlayer(args[0])).getAccount().getCache().put("mute", StringUtils.mapToString(entries));
								
								sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Muted '" + Bukkit.getPlayer(args[0]).getName() + "' for : " + reason + ".");
							} else {
								Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
									public void run() {
										SQLManager.getSQL("global").setValue(SQLManager.getSQL("global").getUUID(args[0]), "mute", StringUtils.mapToString(entries));
										
										sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Muted '" + Bukkit.getOfflinePlayer(args[0]) + "' for: " + reason + ".");
										
										if (Channel.getServer(Bukkit.getOfflinePlayer(args[0]).getName()) != null) {
											Channel.updateAccount(Bukkit.getOfflinePlayer(args[0]).getName());
										}
									}
								});
							}
							return false;
						}
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Invalid time.");
						return false;
					}
					sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Player not registered.");
					return false;
				}
				sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /mute <player> <time> <reason>");
				return false;
			}
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "No Permission!");
		}
		return false;
	}
}
