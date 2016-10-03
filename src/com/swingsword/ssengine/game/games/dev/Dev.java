package com.swingsword.ssengine.game.games.dev;

import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.Game;
import com.swingsword.ssengine.game.GamePlugin;
import com.swingsword.ssengine.game.PreventionSet;
import com.swingsword.ssengine.game.map.MapType;
import com.swingsword.ssengine.game.team.Team;

public class Dev extends GamePlugin {
	
	public static Dev plugin;
	
	public Game onEnable() {
		
		return new Game(this, 0, 50, true, false, false, false, new PreventionSet(), null, MapType.STATIC, null) {
			
			@Override
			public void onStart() { }
			
			@Override
			public void onPlayerQuit(Player player) { }
			
			@Override
			public void onPlayerJoin(Player player) { }
			
			@Override
			public void onTeamJoin(Player player, Team team) { }
			
			@Override
			public void onLoad() {
				MasterPlugin.getMasterPlugin().getCommand("test").setExecutor(new TestCommand());
			}
			
			@Override
			public void onEnd() { }
		};
	}
	
	public void onDisable() { }
	
	
	public static Dev getInstance() {
		return plugin;
	}
}
