package com.swingsword.ssengine.game.games.rust.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.utils.SpawnUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class BackpackListener implements Listener {
	
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
    	final Player player = event.getPlayer();
    	
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		if(event.getClickedBlock().getTypeId() == 171 && event.getClickedBlock().getData() == (byte) 5) {
    			if(SpawnUtils.crates.containsKey(event.getClickedBlock().getLocation())) {
    				event.setCancelled(true);
    				
    				if(player.getLocation().distance(event.getClickedBlock().getLocation().clone().add(0.5, -0.5, 0.5)) <= 1.5) {
    					Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
    						public void run() {
    							if(player.isOnline()) {
    								player.openInventory(SpawnUtils.crates.get(event.getClickedBlock().getLocation()));	
    							}
    						}
    					}, 1);
    				} else {
    					StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Not close enough to search!");
    				}
    				
    			} else {
    				event.getClickedBlock().setType(Material.AIR);
    			}
    		}
    	}
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	if(event.getBlock().getType().getId() == 171) {
    			event.setCancelled(true);
    	}
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
    	for(Block all : event.blockList()) {
    		if(all != null) {
    			if(all.getType().getId() == 171) {
    				event.blockList().remove(all);
    			}
    		}
    	}
    }
}
