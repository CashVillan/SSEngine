package com.swingsword.ssengine.game.games.rust.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.swingsword.ssengine.game.games.rust.utils.C4Utils;

public class C4Listener implements Listener {
	
	@EventHandler
	public void onExplode(BlockExplodeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(player.getItemInHand() != null && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.WHITE + "C4")) {
				event.setCancelled(true);
				Location blockLoc = event.getClickedBlock().getLocation().add(event.getBlockFace().getModX(), event.getBlockFace().getModY(), event.getBlockFace().getModZ());
				if(blockLoc.getBlock().getType() == Material.AIR) {
					if(C4Utils.getNearFoundation(blockLoc) != null && new Location(blockLoc.getWorld(), blockLoc.getX(), (double) C4Utils.getC4Y(C4Utils.getNearFoundation(blockLoc).getLocation(), blockLoc.getBlockY()), blockLoc.getZ()).getBlock().getType() != Material.TNT) {
						blockLoc = new Location(blockLoc.getWorld(), blockLoc.getX(), (double) C4Utils.getC4Y(C4Utils.getNearFoundation(blockLoc).getLocation(), blockLoc.getBlockY()), blockLoc.getZ());
						
						blockLoc.getBlock().setType(Material.TNT);
						blockLoc.getWorld().playEffect(blockLoc, Effect.STEP_SOUND, 46);
						
						C4Utils.primeTNT(blockLoc, 4);
						
						if(player.getItemInHand().getAmount() > 1) {
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							
						} else {
							player.setItemInHand(null);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(C4Utils.locs.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
		
		if(event.getBlock().getType().name().contains("DOOR")) {
			event.getBlock().getDrops().clear();
		}
	}
}
