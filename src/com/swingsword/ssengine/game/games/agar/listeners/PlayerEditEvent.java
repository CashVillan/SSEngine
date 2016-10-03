package com.swingsword.ssengine.game.games.agar.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerEditEvent implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(!player.isOp()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		event.setCancelled(true);
		
		if(event.getWhoClicked().getOpenInventory().getTopInventory().getTitle().contains("Agar")) {
			event.getWhoClicked().closeInventory();
		}
	}
}
