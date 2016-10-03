package com.swingsword.ssengine.game.games.hub.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PortalListener implements Listener {
	
	/*private Map<String, Boolean> statusData = new HashMap<String, Boolean>();
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!statusData.containsKey(player.getName())) {
			statusData.put(player.getName(), false);
		}
		Block block = player.getWorld().getBlockAt(player.getLocation());
		String data = block.getWorld().getName() + "#" + String.valueOf(block.getX()) + "#" + String.valueOf(block.getY()) + "#" + String.valueOf(block.getZ());
		if (PortalUtils.portalData.containsKey(data)) {
			if (!statusData.get(player.getName())) {
				statusData.put(player.getName(), true);
				String destination = PortalUtils.portalData.get(data);
				MasterPlugin.getMasterPlugin().channel.sendToServer(player, destination);
			}
		} else {
			if (statusData.get(player.getName())) {
				statusData.put(player.getName(), false);
			}
		}
	}*/
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(event.getTo().getBlock().getType() == Material.PORTAL && event.getFrom().getBlock().getType() != Material.PORTAL) {
			event.setCancelled(true);
			
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 2);
			player.sendMessage(ChatColor.RED + "Sorry, portal don't work yet, please use the mobs to join servers!");
			
			Vector vec = player.getLocation().getDirection().clone();
			vec.multiply(-1);
			vec.setY(0.5f);
			player.setVelocity(vec);
		}
	}
}
