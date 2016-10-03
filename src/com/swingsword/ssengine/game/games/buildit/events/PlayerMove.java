package com.swingsword.ssengine.game.games.buildit.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.swingsword.ssengine.game.games.buildit.BuildArea;

public class PlayerMove implements Listener {

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(BuildArea.buildAreaInReview == null) {
			if(BuildArea.getArea(player) != null) {
				if(!BuildArea.getArea(player).region.contains(BukkitUtil.toVector(event.getTo()))) {
					
					player.teleport(event.getFrom());
					
					player.sendMessage(ChatColor.RED + "You can't leave the build area!");
				}
			}
			
		} else {
			if(!BuildArea.buildAreaInReview.region.contains(BukkitUtil.toVector(event.getTo()))) {
				
				player.teleport(event.getFrom());
				
				player.sendMessage(ChatColor.RED + "You can't leave the build area!");
			}
		}
	}
}
