package com.swingsword.ssengine.game.games.hub.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.DisplaySlot;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.utils.ExpUtils;
import com.swingsword.ssengine.utils.InboxUtils;
import com.swingsword.ssengine.utils.IntegerUtils;
import com.swingsword.ssengine.utils.ItemUtils;
import com.swingsword.ssengine.utils.LanguageUtils;
import com.swingsword.ssengine.utils.ScoreboardUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class HubScoreboard {
	
	public HubScoreboard() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (PlayerSessionManager.playerSession.get(all.getName()) != null && PlayerSessionManager.getSession(all).getAccount().isLoaded()) {
						List<String> rows = getRows(all);

						ScoreboardUtils.registerObjective(all, com.swingsword.ssengine.scoreboard.ScoreboardManager.scoreboardDisplay, DisplaySlot.SIDEBAR);

						for (int x = 0; x < rows.size(); x++) {
							ScoreboardUtils.addScore(all, rows.get(x), rows.size() - x);
						}
					}
				}
			}
		}, 60, 60);
	}
	
	public static List<String> getRows(Player player) {
		List<String> rows = new ArrayList<String>();
		
		HashMap<Integer, String> layout;
		try {
			HashMap<String, String> sb = StringUtils.stringToMap((String) PlayerSessionManager.getSession(player).getAccount().getCache().get("sb"));
			
			if (!sb.containsKey("hub")) {
				PlayerSessionManager.getSession(player).getAccount().addScoreboard("hub", "r6;la;ce;mb;w4");
			}
			
			layout = deformat(player, StringUtils.stringToMap((String) PlayerSessionManager.getSession(player).getAccount().getCache().get("sb")).get("hub"));
		} catch (Exception e) {
			layout = deformat(player, "r6;la;ce;mb;w4");
		}
		
		for(int x = 0; x < layout.size(); x++) {
			String row = layout.get(x);
			
			rows.add(row.split(":")[0]);
			rows.add(getSpaces(rows, row.split(":")[1]));
			
			if(x != layout.size() - 1) {
				rows.add(null);
			}
		}
		
		return rows;
	}
	
	public static HashMap<Integer, String> deformat(Player player, String layoutString) {
		String[] layout = layoutString.split(";");
		
		HashMap<Integer, String> layoutMap = new HashMap<Integer, String>();
		
		for(int x = 0 ; x < layout.length; x++) {
			String option = layout[x].toCharArray()[0] + "";
			char color = layout[x].toCharArray()[1];
			
			String optionString = null;
			String optionDisplay = null;
			
			if(option.equals("s")) {
				optionString = LanguageUtils.translate(player, "Server");
				optionDisplay = Channel.serverName;
				
			} else if (option.equals("r")) {
				optionString = LanguageUtils.translate(player, "Rank");
				optionDisplay = LanguageUtils.translate(player, "none");
				if (PlayerSessionManager.getSession(player).getAccount().getRanks().size() >= 1) {
					optionDisplay = PlayerSessionManager.getSession(player).getAccount().getMainRank().name;
				}
				
			} else if(option.equals("c")) {
				optionString = LanguageUtils.translate(player, "Credits");
				optionDisplay = PlayerSessionManager.getSession(player).getAccount().getCache().get("credits") + "";
			
			} else if(option.equals("w")) {
				optionString = LanguageUtils.translate(player, "Website");
				optionDisplay = "swingsword.com";
			
			} else if(option.equals("o")) {
				optionString = LanguageUtils.translate(player, "Online Friends");
				int online = 0;
				for(UUID all : PlayerSessionManager.getSession(player).getAccount().getFriends().keySet()) {
					if(Bukkit.getOfflinePlayer(all).isOnline()) {
						online += 1;
					}
				}
				optionDisplay = online + "";
			
			} else if(option.equals("g")) {
				optionString = LanguageUtils.translate(player, "Gamerscore");
				optionDisplay = "0";
			
			} else if(option.equals("a")) {
				optionString = LanguageUtils.translate(player, "Achievements");
				optionDisplay = "0";
			
			} else if(option.equals("l")) {
				optionString = LanguageUtils.translate(player, "Level");
				optionDisplay = ExpUtils.getLevel(PlayerSessionManager.getSession(player).getAccount().getExp()) + " (" + PlayerSessionManager.getSession(player).getAccount().getExp() + "/" + ExpUtils.expNeeded(ExpUtils.getLevel(PlayerSessionManager.getSession(player).getAccount().getExp()) + 1) + ")";
			
			} else if(option.equals("m")) {
				optionString = LanguageUtils.translate(player, "Messages");
				optionDisplay = InboxUtils.getMessages(player.getUniqueId()).size() + "";
			}
			
			optionString = ChatColor.getByChar(color) + "" + ChatColor.BOLD + optionString + ChatColor.COLOR_CHAR + "" + x;
			layoutMap.put(x, optionString + ":" + optionDisplay);
		}
		
		return layoutMap;
	}
	
	public static String changeLayout(String oldLayout, String targetOption, String changedOption, String color) {
		String[] layout = oldLayout.split(";");
		String layoutString = "";
		
		boolean changed = false;
		for(int x = 0; x < layout.length; x++) {
			String add = "";
			
			if(layout[x].startsWith(targetOption) && !changed) {
				add = changedOption + color;
				changed = true;
			} else {
				add = layout[x];
			}
			
			if(layout.length > x + 1) {
				add = add + ";";
			}
			
			layoutString += add;
		}
		
		return layoutString;
	}
	
	public static String getOption(Player player, int option) {
		return deformat(player, StringUtils.stringToMap((String) PlayerSessionManager.getSession(player).getAccount().getCache().get("sb")).get("hub")).get(option);
	}
	
	public static String getOption(String option) {
		if(option.equals("s")) {
			return "Server";
			
		} else if(option.equals("r")) {
			return "Rank";
		
		} else if(option.equals("c")) {
			return "Credits";
		
		} else if(option.equals("w")) {
			return "Website";
		
		} else if(option.equals("o")) {
			return "Online friends";
			
		} else if(option.equals("g")) {
			return "Gamerscore";
		
		} else if(option.equals("a")) {
			return "Achievements";
			
		} else if(option.equals("l")) {
			return "Level";
		
		} else if(option.equals("m")) {
			return "Messages";
		}
		return null;
	}
	
	public static String getChar(String c) {
		if(c.equals("Server")) {
			return "s";
			
		} else if(c.equals("Rank")) {
			return "r";
		
		} else if(c.equals("Credits")) {
			return "c";
		
		} else if(c.equals("Website")) {
			return "w";
		
		} else if(c.equals("Online friends")) {
			return "o";
			
		} else if(c.equals("Gamerscore")) {
			return "g";
		
		} else if(c.equals("Achievements")) {
			return "a";
			
		} else if(c.equals("Level")) {
			return "l";
		
		} else if(c.equals("Messages")) {
			return "m";
		}
		return null;
	}
	
	public static String getOption(int x) {
		if(x == 0) {
			return "Server";
			
		} else if(x == 1) {
			return "Rank";
		
		} else if(x == 2) {
			return "Credits";
		
		} else if(x == 3) {
			return "Website";
		
		} else if(x == 4) {
			return "Online friends";
			
		} else if(x == 5) {
			return "Gamerscore";
		
		} else if(x == 6) {
			return "Achievements";
			
		} else if(x == 7) {
			return "Level";
		
		} else if(x == 8) {
			return "Messages";
		}
		return null;
	}
	
	public static Inventory getScoreboardInv(Player player) {
		Inventory inv = Bukkit.createInventory(null, 27, "Scoreboard");
		HashMap<Integer, String> layout = deformat(player, StringUtils.stringToMap((String) PlayerSessionManager.getSession(player).getAccount().getCache().get("sb")).get("hub"));
		
		for(int x = 11; x <= 15; x++) {
			String option = layout.get(x - 11);
			ChatColor color = ChatColor.getByChar(option.toCharArray()[1]);
			
			inv.setItem(x, ItemUtils.createItem(Material.WOOL, 1, IntegerUtils.chatColorToWool(color), color + "Option " + (x - 10), Arrays.asList(ChatColor.GRAY + "Option: " + ChatColor.stripColor(option.split(":")[0]), ChatColor.GRAY + "Color: " + color + color.name().replace("_", " "), "", ChatColor.YELLOW + "Left-Click to edit Type!", ChatColor.YELLOW + "Right-Click to edit Color!")));
		}
		
		return inv;
	}
	
	public static String getSpaces(List<String> rows, String row) {
		if(rows.contains(row)) {
			return getSpaces(rows, row + " ");
		} else {
			return row;
		}
	}
}