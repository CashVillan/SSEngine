package com.swingsword.ssengine.utils;

import java.util.ArrayList;

import com.swingsword.ssengine.game.Gamemode;
import com.swingsword.ssengine.game.map.Map;

public class BungeeUtils {

	public static ArrayList<String> getGamemodeMaps() {
		ArrayList<String> gamemodeMaps = new ArrayList<String>();
		for(Gamemode gamemode : Gamemode.values()) {
			for(Map map : Map.getMaps(gamemode.getGameClass().getSimpleName())) {
				gamemodeMaps.add(gamemode.getGameClass().getSimpleName() + ";" + map.name);
			}
		}
		
		return gamemodeMaps;
	}
	
	public static ArrayList<String> getGamemodeServerCounts() {
		ArrayList<String> gamemodeServers = new ArrayList<String>();
		for(Gamemode gamemode : Gamemode.values()) {
			if(gamemode.getServerCount() > 0) {
				gamemodeServers.add(gamemode.getGameClass().getSimpleName() + ";" + gamemode.getServerCount());
			}
		}
		
		return gamemodeServers;
	}
}
