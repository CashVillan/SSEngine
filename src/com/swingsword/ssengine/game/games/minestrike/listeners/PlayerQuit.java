package com.swingsword.ssengine.game.games.minestrike.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.team.Team;

public class PlayerQuit implements Listener {
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		if(CSGOGame.hasStarted()) {
			PlayerDeath.onPlayerDeath(new PlayerDeathEvent(p, null, 0, ""));
			
			if(Team.hasTeam(p)) {
				Team.getTeam(p).remove(p);
			}
			
			e.setQuitMessage(("&6" + e.getPlayer().getName() + " &7has left (&b" + (Bukkit.getOnlinePlayers().size() - 1) + "&7/&b10&7)!").replace("&", ChatColor.COLOR_CHAR + ""));
		}
		
		EntityDamageByEntity.playerDamageSet.remove(p);
	}
}
