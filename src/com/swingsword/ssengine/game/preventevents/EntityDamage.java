package com.swingsword.ssengine.game.preventevents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.swingsword.ssengine.game.GameManager;

public class EntityDamage implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(!GameManager.currentPreventSet.doDamage) {
			event.setCancelled(true);
		}
	}
}
