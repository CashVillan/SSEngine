package com.swingsword.ssengine.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.PreventionSet;

public class LobbyUtils {
	
	public static void teleportToLobby(Player player) {
		player.teleport(new Location(Bukkit.getWorld("lobby"), 0.5, 64, 0.5));
	}
	
	public static PreventionSet getLobbyPreventionSet() {
		return new PreventionSet();
	}
}
