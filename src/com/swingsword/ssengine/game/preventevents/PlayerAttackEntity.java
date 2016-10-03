package com.swingsword.ssengine.game.preventevents;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.utils.SpectatorUtils;

public class PlayerAttackEntity implements Listener {

	@EventHandler
	public void onPlayerAttackEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			
			if(!GameManager.currentPreventSet.canAttackEntities || SpectatorUtils.spectators.contains(player)) {
				event.setCancelled(true);
			}
		}
	}
}
