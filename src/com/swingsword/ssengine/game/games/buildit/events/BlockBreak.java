package com.swingsword.ssengine.game.games.buildit.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.swingsword.ssengine.game.games.buildit.BuildArea;

public class BlockBreak implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if(BuildArea.buildAreaInReview == null) {
			if(BuildArea.getArea(player) == null || !BuildArea.getArea(player).region.contains(BukkitUtil.toVector(event.getBlock().getLocation().add(0.5, 0, 0.5)))) {
				event.setCancelled(true);
				
				player.sendMessage(ChatColor.RED + "You can't build outside your area!");
			} else {
				event.setCancelled(false);
			}
			
		} else {
			event.setCancelled(true);
		}
	}
}
