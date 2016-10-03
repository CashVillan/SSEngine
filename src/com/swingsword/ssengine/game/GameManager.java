package com.swingsword.ssengine.game;

import org.bukkit.Bukkit;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.map.Map;
import com.swingsword.ssengine.utils.LobbyUtils;

public class GameManager {
		
	public static void startGame(String name, String map) {
		if(currentGame == null && !delay) {
			try {
				currentMapName = map;
				Class<? extends GamePlugin> targetGame = getGameClass(name);
				
				if(targetGame != null) {
					delay = true;
					Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
						public void run() {
							delay = false;
						}
					}, 100);
					
					GamePlugin gamePlugin = getGameClass(name).newInstance();
					System.out.println("[GameManager] Loaded game: '" + gamePlugin.getName() + "'.");
					
				} else {
					System.out.println("[GameManager] Could not load game: '" + name + "'.");
				}
				
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	public static Class<? extends GamePlugin> getGameClass(String name) {
		for(Gamemode gamemode : Gamemode.values()) {
			if(gamemode.getGameClass().getSimpleName().equals(name)) {
				return gamemode.getGameClass();
			}
		}
		return null;
	}
	
	//Current game
	
	public static boolean delay = false;
	public static Game currentGame = null;
	public static Map currentMap = null;
	public static String currentMapName = null;
	public static PreventionSet currentPreventSet = LobbyUtils.getLobbyPreventionSet();
}
