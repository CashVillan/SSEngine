package com.swingsword.ssengine.game;

import java.util.Date;

import com.swingsword.ssengine.game.map.Map;

public abstract class GamePlugin {

	private Game game = null;
	
	public GamePlugin() {
		game = onEnable();
	}
	
	public abstract Game onEnable();
	public abstract void onDisable();
	
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	public Game getGame() {
		return game;
	}
	
	public Date getExpire() {
		
		return null;
	}
	
	public Map getLoadedMap() {
		return GameManager.currentMap;
	}
}
