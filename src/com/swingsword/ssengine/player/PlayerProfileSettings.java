package com.swingsword.ssengine.player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerProfileSettings {

	public HashMap<String, String> vars = new HashMap<String, String>();
	
	public PlayerProfileSettings(HashMap<String, String> vars) {
		this.vars = vars;
	}
	
	public String getSetting(String key) {
		return vars.get(key);
	}
	
	public String setSetting(String key, String value) {
		return vars.put(key, value);
	}
	
	public String toString() {
		String settingsString = "";
		
		for(String varKey : vars.keySet()) {
			if(settingsString.equals("")) {
				settingsString = varKey + ":" + vars.get(varKey);
				
			} else {
				settingsString = settingsString + ";/" + varKey + ":" + vars.get(varKey);
			}
		}
		
		return settingsString;
	}
	
	public static PlayerProfileSettings fromString(String settingsString) {
		HashMap<String, String> tempVars = new HashMap<String, String>();
		
		for(String var : settingsString.split(";/")) {
			String varKey = var.split(":")[0];
			String varVal = var.split(":")[1];
			
			tempVars.put(varKey, varVal);
		}
		
		return new PlayerProfileSettings(tempVars);
	}
	
	//Privacy settings
	
	public int getVisibility(String key) {
		/*
		 * 0 = blocked
		 * 1 = friends
		 * 2 = everyone
		 */
		
		return Integer.parseInt(getSetting(key));
	}
	
	public static String getName(int optionId) {
		switch(optionId) {
		case 1:
			return "Profile";
		case 2: 
			return "Invites";
		case 3:
			return "Friends";
		}
		return "";
	}
	
	public static int getId(String optionName) {
		for(int x = 1; x <= 7; x++) {
			if(getName(x).toLowerCase().equals(optionName.toLowerCase().replace(" ", "_"))) {
				return x;
			}
		}
		return 0;
	}
	
	public static int getBlockId(int visibility) {
		switch(visibility) {
		case 0:
			return 152;
		case 1: 
			return 41;
		case 2:
			return 133;
		}
		return 0;
	}
	
	public static ChatColor getColor(int visibility) {
		switch(visibility) {
		case 0:
			return ChatColor.RED;
		case 1: 
			return ChatColor.YELLOW;
		case 2:
			return ChatColor.GREEN;

		}
		return null;
	}
	
	public static String getLore(int visibility) {
		switch(visibility) {
		case 0:
			return getColor(visibility) + "Blocked - Only you can see this";
		case 1: 
			return getColor(visibility) + "Friends only - Only your friends can see this";
		case 2:
			return getColor(visibility) + "Everyone - Everyone can see this";

		}
		return "";
	}
	
	public static boolean isAllowed(Player player, PlayerAccount target, String setting) {
		if(Bukkit.getOfflinePlayer(target.getUUID()).isOnline()) {
			target = PlayerSessionManager.getSession(Bukkit.getPlayer(target.getUUID())).getAccount();
		}
		
		if(target.profileSettings.getVisibility(setting) == 2) {
			return true;
			
		} else if(target.profileSettings.getVisibility(setting) == 1) {
			final boolean youAccepted;
			final boolean hasAccepted;
			
			if(PlayerSessionManager.getSession(player).getAccount().getFriends().containsKey(target.getUUID()) && PlayerSessionManager.getSession(player).getAccount().getFriends().get(target.getUUID()) == 1) {
				youAccepted = true;	
			} else {
				youAccepted = false;
			}
			if(target.getFriends().containsKey(player.getUniqueId()) && target.getFriends().get(player.getUniqueId()) == 1) {
				hasAccepted = true;
			} else {
				hasAccepted = false;
			}
			
			if(youAccepted && hasAccepted) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
