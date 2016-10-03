package com.swingsword.ssengine.game.preventevents;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.utils.SpectatorUtils;

public class PlayerBreak implements Listener {

	@EventHandler
	public void onPlayerBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if(!GameManager.currentPreventSet.canBreak || SpectatorUtils.spectators.contains(player)) {
			if(player.isOp() && player.getGameMode() == GameMode.CREATIVE) { 
				
			} else {
				event.setCancelled(true);
			}
		}
	}
}
