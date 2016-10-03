package com.swingsword.ssengine.game.games.minestrike.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class EntityRegainHealth implements Listener {

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if(event.getRegainReason() != RegainReason.CUSTOM) {
			event.setCancelled(true);
		}
	}
	
}
