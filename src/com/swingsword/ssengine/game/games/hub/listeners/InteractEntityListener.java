package com.swingsword.ssengine.game.games.hub.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import com.swingsword.ssengine.entity.InteractEntity;

public class InteractEntityListener implements Listener {

	@EventHandler
    public void onEntityTargetEvent(final EntityTargetEvent event) {
        if(InteractEntity.getInteractEntity(event.getEntity()) != null) {
			event.setCancelled(true);
        }
    }
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(InteractEntity.getInteractEntity(event.getEntity()) != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(InteractEntity.getInteractEntity(event.getEntity()) != null) {
			InteractEntity ent = InteractEntity.getInteractEntity(event.getEntity());
			event.setCancelled(true);
			
			if(event.getDamager() instanceof Player) {
				((Player) event.getDamager()).chat(ent.command);
			}
		}
	}
	
}
