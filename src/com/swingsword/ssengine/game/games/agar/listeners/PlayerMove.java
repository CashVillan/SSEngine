package com.swingsword.ssengine.game.games.agar.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.game.games.agar.Agar;
import com.swingsword.ssengine.game.games.agar.AgarManager;

public class PlayerMove implements Listener {

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(AgarManager.agaring.containsKey(player)) {
			if(!Agar.isInArea(event.getTo())) {
				Location from = event.getFrom().clone();
				from.setYaw(from.getYaw() + 180);
				player.teleport(from);
				
				player.sendMessage(ChatColor.RED + "You can't leave the arena.");
			}
			
			if(event.getTo().getX() != event.getFrom().getX() || event.getTo().getZ() != event.getFrom().getZ()) {
				if(AgarManager.stopped.contains(player)) {
					AgarManager.stopped.remove(player);
				}
			}

			if(event.getTo().getY() != event.getFrom().getY()) {
				player.teleport(event.getFrom());
			
				if(!AgarManager.delay.contains(player)) {
					if(event.getTo().getY() > event.getFrom().getY()) {
						for(Slime all : AgarManager.split(player)) {
							AgarManager.splitSlimes.get(player).add(all);
						}
						AgarManager.delay(player, 10);
					}
					
					if(event.getTo().getY() < event.getFrom().getY()) {
						if(!AgarManager.stopped.contains(player)) {
							player.setVelocity(new Vector());
							AgarManager.stopped.add(player);
						} else {
							AgarManager.stopped.remove(player);
						}
						AgarManager.delay(player, 10);
					}
				}
			}
		}
	}
}
