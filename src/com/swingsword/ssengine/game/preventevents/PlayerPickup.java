package com.swingsword.ssengine.game.preventevents;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.utils.SpectatorUtils;

public class PlayerPickup implements Listener {

	@EventHandler
	public void onPlayerPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		
		if(!GameManager.currentPreventSet.canPickupItems || SpectatorUtils.spectators.contains(player)) {
			event.setCancelled(true);
		}
	}
}
