package com.swingsword.ssengine.game.games.rust.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.mobs.Mobs;
import com.swingsword.ssengine.game.games.rust.utils.MobUtils;
import com.swingsword.ssengine.stats.StatManager;

public class MobsListener implements Listener {
	
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
    	final Player player = event.getPlayer();
    	
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		if(event.getClickedBlock().getType() == Material.CHEST) {
    			if(Mobs.crates.containsKey(event.getClickedBlock().getLocation())) {
    				event.setCancelled(true);
    				
    				if(player.getLocation().distance(event.getClickedBlock().getLocation().clone().add(0.5, -0.5, 0.5)) <= 1.5) {
    					Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
    						public void run() {
    							if(player.isOnline()) {
    								player.openInventory(Mobs.crates.get(event.getClickedBlock().getLocation()));	
    							}
    						}
    					}, 1);
    				}
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	if(event.getBlock().getType() == Material.CHEST) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
    	for(Block all : event.blockList()) {
    		if(all != null) {
    			if(all.getType() == Material.CHEST) {
    				event.blockList().remove(all);
    			}
    		}
    	}
    }

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if (!(event.getEntity().getType() == EntityType.ARMOR_STAND)) {
			if (event.getEntity() instanceof LivingEntity) {
				LivingEntity ent = (LivingEntity) event.getEntity();

				if (MobUtils.canSpawn == false && ent.getType() != EntityType.ENDER_DRAGON && ent.getType() != EntityType.PLAYER) {
					ent.remove();
				}
			} else {
				if (event.getEntity() instanceof Item) {
					Item item = (Item) event.getEntity();

					if (item.getItemStack().getType() == Material.EGG && item.getItemStack().getItemMeta().getDisplayName() == null || item.getItemStack().getType() == Material.CARPET && item.getItemStack().getDurability() == 5) {
						item.remove();
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player) {
			if(event.getDamager() instanceof Wolf) {
				((Player) event.getEntity()).damage(event.getDamage());
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
			LivingEntity ent = (LivingEntity) event.getEntity();
			
			event.setDroppedExp(0);
			
			if(ent.getKiller() != null) {
				StatManager.addStat(ent.getKiller(), "rt_mobs_killed", 1);
			}
			
			if(ent.getType() == EntityType.COW && ent.getCustomName() != null && ent.getCustomName().contains("Bear")) {
				event.getDrops().clear();
				
				if(ent.getLocation().getBlock().getType() == Material.AIR) {
	    			Inventory crateInv = Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName());
	    			Mobs.createCrate(ent.getLocation().getBlock().getLocation(), crateInv, ent.getCustomName() + " Loot");
	    			
    			} else {
        			for(ItemStack all : Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName()).getContents()) {
        				if(all != null) {
        					ent.getLocation().getBlock().getWorld().dropItemNaturally(ent.getLocation().getBlock().getLocation(), all);
        				}
        			}
    			}
				
			} else if(ent.getType() == EntityType.COW && ent.getCustomName() != null && ent.getCustomName().contains("Radiated Bear")) {
				event.getDrops().clear();
				
				if(ent.getLocation().getBlock().getType() == Material.AIR) {
					Inventory crateInv = Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName());
	    			Mobs.createCrate(ent.getLocation().getBlock().getLocation(), crateInv, ent.getCustomName() + " Loot");
	    			
    			} else {
        			for(ItemStack all : Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName()).getContents()) {
        				if(all != null) {
        					ent.getLocation().getBlock().getWorld().dropItemNaturally(ent.getLocation().getBlock().getLocation(), all);
        				}
        			}
    			}
				
			} else if(ent.getType() == EntityType.WOLF && ent.getCustomName() != null && ent.getCustomName().contains("Wolf")) {
				event.getDrops().clear();
				
				if(ent.getLocation().getBlock().getType() == Material.AIR) {
					Inventory crateInv = Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName());
	    			Mobs.createCrate(ent.getLocation().getBlock().getLocation(), crateInv, ent.getCustomName() + " Loot");
	    			
    			} else {
        			for(ItemStack all : Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName()).getContents()) {
        				if(all != null) {
        					ent.getLocation().getBlock().getWorld().dropItemNaturally(ent.getLocation().getBlock().getLocation(), all);
        				}
        			}
				}
				
			} else if(ent.getType() == EntityType.WOLF && ent.getCustomName() != null && ent.getCustomName().contains("Radiated Wolf")) {
				event.getDrops().clear();
				
				if(ent.getLocation().getBlock().getType() == Material.AIR) {
					Inventory crateInv = Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName());
	    			Mobs.createCrate(ent.getLocation().getBlock().getLocation(), crateInv, ent.getCustomName() + " Loot");
	    			
    			} else {
        			for(ItemStack all : Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName()).getContents()) {
        				if(all != null) {
        					ent.getLocation().getBlock().getWorld().dropItemNaturally(ent.getLocation().getBlock().getLocation(), all);
        				}
        			}
				}
			
			} else if(ent.getType() == EntityType.CHICKEN && ent.getCustomName() != null && ent.getCustomName().contains("Chicken")) {
				event.getDrops().clear();
				
				if(ent.getLocation().getBlock().getType() == Material.AIR) {
					Inventory crateInv = Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName());
	    			Mobs.createCrate(ent.getLocation().getBlock().getLocation(), crateInv, ent.getCustomName() + " Loot");
	    			
    			} else {
        			for(ItemStack all : Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName()).getContents()) {
        				if(all != null) {
        					ent.getLocation().getBlock().getWorld().dropItemNaturally(ent.getLocation().getBlock().getLocation(), all);
        				}
        			}
				}
			
			} else if(ent.getType() == EntityType.PIG && ent.getCustomName() != null && ent.getCustomName().contains("Pig")) {
				event.getDrops().clear();
				
				if(ent.getLocation().getBlock().getType() == Material.AIR) {
					Inventory crateInv = Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName());
	    			Mobs.createCrate(ent.getLocation().getBlock().getLocation(), crateInv, ent.getCustomName() + " Loot");
	    			
    			} else {
        			for(ItemStack all : Mobs.fillCrate(ent.getLocation().getBlock().getLocation(), ent.getCustomName()).getContents()) {
        				if(all != null) {
        					ent.getLocation().getBlock().getWorld().dropItemNaturally(ent.getLocation().getBlock().getLocation(), all);
        				}
        			}
				}
			}
			
			if(Mobs.getRegion(ent) != null) {
				Mobs.getRegion(ent).getMob(ent).despawn();
			}
		}
	}
}
