package com.swingsword.ssengine.game.games.rust.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.utils.LootUtils;
import com.swingsword.ssengine.stats.StatManager;
import com.swingsword.ssengine.utils.StringUtils;

public class LootListener implements Listener {
	
	@EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerInteract(final PlayerInteractEvent event) {
    	final Player player = event.getPlayer();
    	
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		if(event.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
    			event.setCancelled(true);
				
				if(player.getLocation().distance(event.getClickedBlock().getLocation().clone().add(0.5, -0.5, 0.5)) <= 2) {
					if(LootUtils.crates.get(event.getClickedBlock().getLocation()) != null) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
							public void run() {
								if(player.isOnline()) {
									player.openInventory(LootUtils.crates.get(event.getClickedBlock().getLocation()));	
									StatManager.addStat(player, "rt_crates_looted", 1);
								}
							}
						}, 1);
					}
				} else {
					StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Not close enough to search!");
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
						public void run() {
							if(player.isOnline()) {
								player.closeInventory();
							}
						}
					}, 1);
				}
    		}
    	}
    }
    
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	if(event.getBlock().getType() == Material.TRAPPED_CHEST) {
    		event.setCancelled(true);
    	}
    }
    
	@EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
    	for(Block all : event.blockList()) {
    		if(all != null) {
    			if(all.getType() == Material.TRAPPED_CHEST) {
    				event.blockList().remove(all);
    			}
    		}
    	}
    }
}
