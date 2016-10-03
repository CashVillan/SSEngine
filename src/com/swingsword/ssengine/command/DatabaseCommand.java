package com.swingsword.ssengine.command;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.database.SQL;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.utils.StringUtils;

public class DatabaseCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("db")) {
			if (sender.hasPermission("ss.db")) {
				if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("convertrank")) {
						if (args.length == 3) {
							inputRank(sender, args[1], args[2]);
						}
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /db convertrank <player> <rank>");
						return false;
					}
					if (args[0].equalsIgnoreCase("reset")) {
						if (args.length == 2) {
							for (SQL sql : SQLManager.getSQLs()) {
								sql.removeAccount(args[1]);
							}
							Channel.kickPlayer(args[1], "Your account was reset.");
							
							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Deleted " + args[1] + "'s account successfully.");
							return false;
						}
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /db reset <player>");
						return false;
					}
					if (args[1].equalsIgnoreCase("get")) {
						if (args.length == 4) {
							getValue(args[0], sender, args[2], args[3]);
							return false;
						}
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /db <db> get <player> <key>");
						return false;
					}
					if (args[1].equalsIgnoreCase("getall")) {
						if (args.length == 3) {
							getValues(args[0], sender, args[2]);
							return false;
						}
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /db <db> getall <player>");
						return false;
					}
					if (args[1].equalsIgnoreCase("set")) {
						if (args.length == 5) {
							setValue(args[0], sender, args[2], args[3], args[4]);
							return false;
						}
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /db <db> set <player> <key> <value>");
						return false;
					}
					if (args[1].equalsIgnoreCase("addvalue")) {
						if (args.length == 5) {
							addValue(args[0], sender, args[2], args[3], args[4]);
							return false;
						}
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /db <db> addvalue <player> <key> <value>");
						return false;
					}
					if (args[1].equalsIgnoreCase("removevalue")) {
						if (args.length == 5) {
							removeValue(args[0], sender, args[2], args[3], args[4]);
							return false;
						}
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Usage: /db <db> removevalue <player> <key> <value>");
						return false;
					}
				}
				sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Database Commands:");
				sender.sendMessage(ChatColor.AQUA + "[/db <db> get <player> <key>] Get a value for a player account.");
				sender.sendMessage(ChatColor.AQUA + "[/db <db> getall <player>] Get all values for a player account.");
				sender.sendMessage(ChatColor.AQUA + "[/db <db> set <player> <key> <value>] Set a value for a player account.");
				sender.sendMessage(ChatColor.AQUA + "[/db <db> addvalue <player> <key> <value>] Add a rank to a player account.");
				sender.sendMessage(ChatColor.AQUA + "[/db <db> removevalue <player> <key> <value>] Remove a rank from a player account.");
				sender.sendMessage(ChatColor.AQUA + "[/db reset <player>] Reset all values for a player account.");
				return false;
			}
			sender.sendMessage(ChatColor.RED + "No Permission!");
		}
		return false;
	}

	// methods

	public void getValue(String db, final CommandSender sender, final String target, final String key) {
		if (SQLManager.getSQL(db).isConnected()) {
			Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					if (SQLManager.getSQL("global").accountExists(target)) {
						Object value = null;
						if (Bukkit.getOfflinePlayer(target).isOnline()) {
							value = PlayerSessionManager.getSession(Bukkit.getPlayer(target)).getAccount().getCache().get(key);
						} else {
							value = SQLManager.getSQL("global").getValue(SQLManager.getSQL("global").getUUID(target), key);
						}

						if (value != null) {
							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "The key '" + key + "' for player '" + target + "' is currently set to '" + value.toString() + "'.");
						} else {
							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "That key isn't defined.");
						}
					} else {
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "That player isn't registered.");
					}
				}
			});
		} else {
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Could not connect to the database.");
		}
	}

	public void getValues(String db, final CommandSender sender, final String target) {
		if (SQLManager.getSQL(db).isConnected()) {
			Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					if (SQLManager.getSQL("global").accountExists(target)) {
						HashMap<String, Object> values = new HashMap<>();
						
						if (Bukkit.getOfflinePlayer(target).isOnline()) {
							values = PlayerSessionManager.getSession(Bukkit.getPlayer(target)).getAccount().getCache();
						}
						values = SQLManager.getSQL("global").getValues(SQLManager.getSQL("global").getUUID(target));

						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "All info about " + target + ":");
						for (String key : values.keySet()) {
							sender.sendMessage(ChatColor.GREEN + key + ": " + values.get(key));
						}

					} else {
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "That player isn't registered.");
					}
				}
			});
		} else {
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Could not connect to the database.");
		}
	}

	public void setValue(String db, final CommandSender sender, final String target, final String key, final String value) {
		if (SQLManager.getSQL(db).isConnected()) {
			Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					if (SQLManager.getSQL("global").accountExists(target)) {
						if (Bukkit.getOfflinePlayer(target).isOnline()) {
							Player targetPlayer = Bukkit.getPlayer(target);

							PlayerSessionManager.getSession(targetPlayer).getAccount().getCache().put(key, value);
							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Set '" + key + "' to '" + value + "' for player '" + target + "'.");
							return;
						} else {
							SQLManager.getSQL("global").setValue(SQLManager.getSQL("global").getUUID(target), key, value);

							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Set '" + key + "' to '" + value + "' for player '" + target + "'.");

							try {
								if (Channel.getServer(target) != null) {
									Channel.updateAccount(target);
								}
							} catch (Exception e) { }
						}
					} else {
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "That player isn't registered.");
					}
				}
			});
		} else {
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Could not connect to the database.");
		}
	}

	public void addValue(String db, final CommandSender sender, final String target, final String key, final String value) {
		if (SQLManager.getSQL(db).isConnected()) {
			Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					if (SQLManager.getSQL("global").accountExists(target)) {
						ArrayList<String> list = new ArrayList<String>();

						if (Bukkit.getOfflinePlayer(target).isOnline()) {
							Player targetPlayer = Bukkit.getPlayer(target);

							list = StringUtils.stringToList((String) PlayerSessionManager.getSession(targetPlayer).getAccount().getCache().get(key));
							if (!list.contains(value)) {
								list.add(value);
							}

							PlayerSessionManager.getSession(targetPlayer).getAccount().getCache().put(key, StringUtils.listToString(list));
							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Added '" + value + "' to '" + key + "' for player '" + target + "'.");
							return;
							
						} else {
							list = StringUtils.stringToList((String) SQLManager.getSQL("global").getValue(SQLManager.getSQL("global").getUUID(target), key));
							
							if (!list.contains(value)) {
								list.add(value);
							}
							
							SQLManager.getSQL("global").setValue(SQLManager.getSQL("global").getUUID(target), key, StringUtils.listToString(list));
							
							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Added '" + value + "' to '" + key + "' for player '" + target + "'.");
							try {
								String server = Channel.getServer(target);
								if (server != null) {
									Channel.updateAccount(target);
								}
							} catch (Exception e) { }
						}
					} else {
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "That player isn't registered.");
					}
				}
			});
		} else {
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Could not connect to the database.");
		}
	}

	public void removeValue(String db, final CommandSender sender, final String target, final String key, final String value) {
		if (SQLManager.getSQL(db).isConnected()) {
			Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					if (SQLManager.getSQL("global").accountExists(target)) {
						ArrayList<String> list = new ArrayList<String>();

						if (Bukkit.getOfflinePlayer(target).isOnline()) {
							Player targetPlayer = Bukkit.getPlayer(target);

							list = StringUtils.stringToList((String) PlayerSessionManager.getSession(targetPlayer).getAccount().getCache().get(key));
							list.remove(value);

							PlayerSessionManager.getSession(targetPlayer).getAccount().getCache().put(key, StringUtils.listToString(list));
							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Removed '" + value + "' from '" + key + "' for player '" + target + "'.");
							return;
							
						} else {
							list = StringUtils.stringToList((String) SQLManager.getSQL("global").getValue(SQLManager.getSQL("global").getUUID(target), key));
							list.remove(value);

							SQLManager.getSQL("global").setValue(SQLManager.getSQL("global").getUUID(target), key, StringUtils.listToString(list));

							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Removed '" + value + "' from '" + key + "' for player '" + target + "'.");
							try {
								if (Channel.getServer(target) != null) {
									Channel.updateAccount(target);
								}
							} catch (Exception e) { }
						}
					} else {
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "That player isn't registered.");
					}
				}
			});
		} else {
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Could not connect to the database.");
	}
	}

	public void inputRank(CommandSender sender, String target, String rank) {
		String newRank = null;
		if (rank.equals("associate") || rank.equals("rookie")) {
			newRank = "lord";
		}
		if (rank.equals("soldier")) {
			newRank = "emperor";
		}
		if (rank.equals("enforcer") || rank.equals("mobster")) {
			newRank = "prince";
		}
		if (rank.equals("underboss") || rank.equals("godfather")) {
			newRank = "king";
		}
		
		convertRank(sender, target, newRank);
	}
	

	public void convertRank(final CommandSender sender, final String target, final String rank) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				ArrayList<String> ranks = StringUtils.stringToList((String) SQLManager.getSQL("global").getValue(SQLManager.getSQL("global").getUUID(target), "ranks"));
				boolean passed = true;
				if (rank.equals("king")) {
					ranks.remove("prince");
					ranks.remove("emperor");
					ranks.remove("lord");
				}
				if (rank.equals("prince")) {
					if (ranks.contains("king")) {
						passed = false;
					}
					ranks.remove("emperor");
					ranks.remove("lord");
				}
				if (rank.equals("emperor")) {
					if (ranks.contains("king") || ranks.contains("prince")) {
						passed = false;
					}
					ranks.remove("lord");
				}
				if (rank.equals("lord")) {
					if (ranks.contains("king") || ranks.contains("prince") || ranks.contains("emperor")) {
						passed = false;
					}
				}
				
				if (passed) {
					setValue("global", sender, target, "ranks", StringUtils.listToString(ranks));
				}
			}
		});
	}
}