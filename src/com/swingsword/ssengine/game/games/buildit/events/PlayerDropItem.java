package com.swingsword.ssengine.game.games.buildit.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.swingsword.ssengine.game.games.buildit.BuildArea;

public class PlayerDropItem implements Listener {

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if(BuildArea.buildAreaInReview != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {		
		if(BuildArea.buildAreaInReview != null) {
			event.setCancelled(true);
		}
	}
}
