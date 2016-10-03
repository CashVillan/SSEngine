package com.swingsword.ssengine.player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class PlayerSessionManager {

	public static HashMap<String, PlayerSession> playerSession = new HashMap<String, PlayerSession>();
	public static HashMap<Player, PermissionAttachment> playerAttachment = new HashMap<Player, PermissionAttachment>();
	
	public PlayerSessionManager() {
		for(Player players : Bukkit.getOnlinePlayers()) {
			startSession(players);
		}
	}
	
	public static PlayerSession getSession(Player player) {
		PlayerSession session = playerSession.get(player.getName());
		
		return session;
	}

	public static void startSession(Player player) {
		boolean portedFirstLoad = false;
		if (playerSession.get(player.getName()) != null) {
			portedFirstLoad = getSession(player).hasBeenLoaded;
			endSession(player, false, true);
		}

		PlayerSession session = new PlayerSession(player);
		session.hasBeenLoaded = portedFirstLoad;
		playerSession.put(player.getName(), session);
	}
	
	public static void endSession(final Player player, boolean shutdown, boolean save) {
		playerAttachment.remove(player);
		if (playerSession.get(player.getName()) != null && save) {
			playerSession.get(player.getName()).getAccount().saveAccount(shutdown);
		}
	}
	
	public static void removeSession(final String name) {
		playerSession.remove(name);
	}
}
