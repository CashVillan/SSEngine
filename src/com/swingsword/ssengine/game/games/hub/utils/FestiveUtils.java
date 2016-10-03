package com.swingsword.ssengine.game.games.hub.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.hub.Hub;

public class FestiveUtils implements Listener {

	public static void runSnowDropper() {
		/*final int count = 3;
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					for(int x = 0; x < count; x++) {
						double modX = (new Random().nextDouble() - 0.5d) * 10d;
						double modZ = (new Random().nextDouble() - 0.5d) * 10d;
						double modY = 1.5 + new Random().nextDouble() + new Random().nextDouble();
					
						Location snowLoc = all.getEyeLocation().add(modX, modY, modZ);
						new ParticleEffect(ParticleType.FIREWORKS_SPARK, 0.3f, 1, 0).sendToLocation(snowLoc);
					}
				}
			}
		}, 1, 1);*/
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				Bukkit.getWorld("map").setWeatherDuration(9001);
				Bukkit.getWorld("map").setStorm(true);
				Bukkit.getWorld("map").setThundering(false);

			}
		}, 1, 1);
	}
	
    @EventHandler
    public void snow(BlockFormEvent event) {
    	if(event.getNewState().getData().getItemType() == Material.SNOW) {
    		event.setCancelled(true);
    	}
    }
    
    //Snowballs
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    	event.getPlayer().getInventory().setItem(2, new ItemStack(Material.SNOW_BALL, 1));
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
    	if(event.getEntity().getShooter() instanceof Player) {
    		Player player = (Player) event.getEntity().getShooter();
    	
    		if(player.getItemInHand().getType() == Material.SNOW_BALL) {
    			player.getItemInHand().setAmount(player.getItemInHand().getAmount() + 1);
    			player.updateInventory();
    		}
    	}
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onSnowballDamage(EntityDamageByEntityEvent event) {
    	double damage = 3;
    	
    	if(event.getDamager() instanceof Snowball && event.getEntity() instanceof Player) {
    		Player player = (Player) event.getEntity();
    		Player shooter = (Player) ((Projectile) event.getDamager()).getShooter();
    		
    		if(player.getItemInHand().getType() == Material.SNOW_BALL) {
	    		event.setCancelled(false);
	    		event.setDamage(damage);
	    		
	    		shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT, 0.5f, 2);
	    		
	    		if(player.getHealth() <= damage) {
	    			player.setHealth(player.getMaxHealth());
	    			shooter.setHealth(shooter.getMaxHealth());
	    			player.getWorld().playEffect(player.getLocation(), Effect.EXPLOSION_HUGE, 30);
	    			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

	    			Location spawnLoc = new Location(Bukkit.getWorld("map"), Double.parseDouble(Hub.plugin.getLoadedMap().getMapConfig().getString("spawn.x")), Double.parseDouble(Hub.plugin.getLoadedMap().getMapConfig().getString("spawn.y")), Double.parseDouble(Hub.plugin.getLoadedMap().getMapConfig().getString("spawn.z")), Float.parseFloat(Hub.plugin.getLoadedMap().getMapConfig().getString("spawn.yaw")), Float.parseFloat(Hub.plugin.getLoadedMap().getMapConfig().getString("spawn.pitch")));
	    			while(spawnLoc.getBlock().getRelative(0, -1, 0).getType().equals(Material.AIR) && spawnLoc.getBlockY() > 0) {
	    				spawnLoc.setY(spawnLoc.getBlockY() - 1);
	    			}
	    			
	    			player.teleport(spawnLoc);
	    			
	    			Bukkit.broadcastMessage(ChatColor.GRAY + player.getDisplayName() + ChatColor.GRAY + " got snow'd to death by " + shooter.getDisplayName() + ChatColor.GRAY + "!");
	    		}
    		}
    	}
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        World world = event.getEntity().getWorld();
        BlockIterator bi = new BlockIterator(world, event.getEntity().getLocation().toVector(), event.getEntity().getVelocity().normalize(), 0, 4);
        Block hit = null;
     
        while(bi.hasNext()) {
            hit = bi.next();
            if(hit.getTypeId() != 0) {
                break;
            }
        }
        
        final Block finalHit = hit;
        
        if(hit != null && hit.getType() != Material.AIR) {
        	for(Player all : Bukkit.getOnlinePlayers()) {
        		all.sendBlockChange(hit.getLocation(), Material.SNOW_BLOCK.getId(), (byte) 0);
        	}
        	
        	Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
        		public void run() {
        			for(Player all : Bukkit.getOnlinePlayers()) {
                		all.sendBlockChange(finalHit.getLocation(), finalHit.getType(), finalHit.getData());
                	}
        		}
        	}, 100);
    	}
    }
}
