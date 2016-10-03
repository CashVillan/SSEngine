package com.swingsword.ssengine.game.games.minestrike.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.team.Team;

public class PlayerDeath implements Listener {

	@EventHandler
	public static void onPlayerDeath(PlayerDeathEvent e) {
		e.setDeathMessage(null);
	}
	
	public static void checkWin() {
		if(Team.getTeam("CT").getAlive() == 0) {
			CSGOGame.winTeam("T", "ALL_DEAD");
		}
	
		if(Team.getTeam("T").getAlive() == 0 && CSGOGame.bombLoc == null) {
			CSGOGame.winTeam("CT", "ALL_DEAD");
		}
	}
}
