package com.swingsword.ssengine.game.games.rust.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.utils.AirdropUtils;
import com.swingsword.ssengine.game.games.rust.utils.SimpleLocation;
import com.swingsword.ssengine.stats.StatManager;

public class AirdropListener implements Listener {
    
    @EventHandler
    public void stopDragonDamage(EntityExplodeEvent event) {
    	Entity ent = event.getEntity();
    	
    	if(ent instanceof EnderDragon) {
    		event.setCancelled(true);
    	}
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
    	if (event.getEntity() instanceof FallingBlock) {
    		if(event.getTo() == Material.WOOL && ((FallingBlock) event.getEntity()).getBlockData() == 13) {
    			if(event.getBlock().getType() == Material.AIR) {
	    			Inventory crateInv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Crate");
	    			AirdropUtils.createCrate(event.getBlock().getLocation(), crateInv, "Supply Crate");
	    			AirdropUtils.fillCrate(event.getBlock().getLocation());
	    			
    			} else {
    				event.setCancelled(true);
        			event.getEntity().remove();
        			
        			for(ItemStack all : AirdropUtils.fillCrate(event.getBlock().getLocation()).getContents()) {
        				if(all != null) {
        					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), all);
        				}
        			}
    			}
    		}
    	}
    	
    	for(Entity all : event.getEntity().getNearbyEntities(6, 6, 6)) {
    		if(all instanceof FallingBlock) {
    			FallingBlock block = (FallingBlock) all;
    			
    			if(block.getMaterial() == Material.WOOL && block.getBlockData() == 13) {
    				block.remove();
    			}
    		}
    	}
    	
    	for(int x = 0; x <= 1; x++) {
    		for(int z = 0; z <= 1; z++) {
    			for(int y = 0; y <= 3; y++) {
					Block block = event.getEntity().getLocation().getBlock().getRelative(x, y, z);
					
					if(block.getType() == Material.AIR) {
						block.setTypeIdAndData(35, (byte) 13, true);
					
						AirdropUtils.crates.put(new SimpleLocation(block.getLocation()).toString(), AirdropUtils.crates.get(new SimpleLocation(event.getBlock().getLocation().getBlock().getLocation()).toString()));
						AirdropUtils.crateTimeout.put(new SimpleLocation(block.getLocation()).toString(), 900);
					}
    			}
    		}
    	}
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
    	final Player player = event.getPlayer();
    	
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		if(player.getItemInHand().getType() == Material.FIREWORK_CHARGE) {
    			if(player.getItemInHand().getAmount() > 1) {
    				player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
  
    			} else {
    				player.setItemInHand(null);
    			}
    			
    			AirdropUtils.dropCrate(event.getClickedBlock().getLocation());
    			StatManager.addStat(player, "rt_airdrops_called", 1);
    		}
    	}
    	
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		if(event.getClickedBlock().getType() == Material.WOOL && event.getClickedBlock().getData() == 13) {
    			if(AirdropUtils.crates.containsKey(new SimpleLocation(event.getClickedBlock().getLocation()).toString())) {
    				event.setCancelled(true);
    				
					Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
						public void run() {
							if(player.isOnline()) {
								player.openInventory(AirdropUtils.crates.get(new SimpleLocation(event.getClickedBlock().getLocation()).toString()));
								StatManager.addStat(player, "rt_airdrops_looted", 1);
							}
						}
					}, 1);
					
    			} else {
    				removeNearby(event.getClickedBlock().getLocation(), Material.WOOL);
    			}
    		}
    	}
    }
    
    public static void removeNearby(Location loc, Material type) {
    	for(int x = -1; x <= 1; x++) {
    		for(int y = -1; y <= 1; y++) {
    			for(int z = -1; z <= 1; z++) {
    				if(loc.getBlock().getRelative(x, y, z).getType() == type) {
    					loc.getBlock().getRelative(x, y, z).setType(Material.AIR);
    					
    					removeNearby(loc.getBlock().getRelative(x, y, z).getLocation(), type);
    				}
    			}
    		}
    	}
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	if(event.getBlock().getType() == Material.WOOL && event.getBlock().getData() == 13) {
    		event.setCancelled(true);
    	}
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
    	for(Block all : event.blockList()) {
    		if(all != null) {
    			if(all.getType() == Material.WOOL && all.getData() == 13) {
    				event.blockList().remove(all);
    			}
    		}
    	}
    }
    
    public static int getLargest(int one, int two) {
    	if(one > two) {
    		return one;
    	}
    	return two;
    }
    public static int getSmallest(int one, int two) {
    	if(one > two) {
    		return two;
    	}
    	return one;
    }
}
