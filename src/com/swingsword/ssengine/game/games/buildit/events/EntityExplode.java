package com.swingsword.ssengine.game.games.buildit.events;

import org.bukkit.Effect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplode implements Listener {

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		event.setCancelled(true);
		
		event.getEntity().getWorld().playEffect(event.getEntity().getLocation(), Effect.EXPLOSION_LARGE, 30);
	}
}
