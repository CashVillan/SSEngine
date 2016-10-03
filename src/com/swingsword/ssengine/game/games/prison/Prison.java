package com.swingsword.ssengine.game.games.prison;

import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.Game;
import com.swingsword.ssengine.game.GamePlugin;
import com.swingsword.ssengine.game.PreventionSet;
import com.swingsword.ssengine.game.games.prison.command.PlotCommand;
import com.swingsword.ssengine.game.games.prison.plot.Plot;
import com.swingsword.ssengine.game.games.prison.plot.PlotManager;
import com.swingsword.ssengine.game.map.MapType;
import com.swingsword.ssengine.game.team.Team;

public class Prison extends GamePlugin {
	
	@Override
	public Game onEnable() {
		
		MasterPlugin.getMasterPlugin().getCommand("plot").setExecutor(new PlotCommand());
		
		PreventionSet set = new PreventionSet();
		set.doDamage = true;
		set.canAttackPlayers = true;
		set.canAttackEntities = true;
		set.canBreak = true;
		set.canBuild = true;
		set.canPickupItems = true;
		
		return new Game(this, 0, 50, true, false, false, false, set, null, MapType.STATIC, null) {

			@Override
			public void onEnd() {
				PlotManager.clearPlots();
			}
			
			@Override
			public void onLoad() { }
			
			@Override
			public void onPlayerJoin(Player player) {
				new Plot(player.getUniqueId().toString());
			}

			@Override
			public void onPlayerQuit(Player player) {
				PlotManager.loadedPlots.get(player.getUniqueId().toString()).end();
			}

			@Override
			public void onTeamJoin(Player player, Team team) { }
			
			@Override
			public void onStart() {
			}
		};
	}

	@Override
	public void onDisable() {
		
	}

}
