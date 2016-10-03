package com.swingsword.ssengine.game.games.minestrike.guns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.Main;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.minestrike.game.DeathManager;
import com.swingsword.ssengine.game.games.minestrike.game.RewardManager;
import com.swingsword.ssengine.game.games.minestrike.listeners.EntityDamageByEntity;
import com.swingsword.ssengine.game.games.minestrike.utils.ChatUtils;
import com.swingsword.ssengine.game.team.Team;
import com.swingsword.ssengine.stats.StatManager;

import net.minecraft.server.v1_10_R1.EntityPlayer;

public class GunListener implements Listener {
    
	public static HashMap<String, Location> playerOldLoc = new HashMap<String, Location>();
	public static HashMap<String, Integer> autoing = new HashMap<String, Integer>();
	
    public void runDelayTask() {
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
    		public void run() {
    			ArrayList<Player> remove = new ArrayList<Player>();
    			for(Player all : GunData.delay.keySet()) {
    				if(GunData.delay.get(all) <= 0) {
    					remove.add(all);
    					
    				} else {
    					GunData.delay.put(all, GunData.delay.get(all) - 1);
    				}
    			}
    			for(Player all : remove) {
    				GunData.delay.remove(all);
    			}
    			
    			ArrayList<String> remove2 = new ArrayList<String>();
    			for(String all : autoing.keySet()) { 
    				if(autoing.get(all) == 1) {
    					remove2.add(all);
    				} else {
    					autoing.put(all, autoing.get(all) - 1);
    				}
    			}
    			for(String all : remove2) {
    				autoing.remove(all);
    			}
    			
        		for(Player player : GunData.reloading.keySet()) {
        			if(!Gun.isGun(player.getItemInHand()) || !Gun.getGun(player.getItemInHand().getItemMeta().getDisplayName()).equals(GunData.reloadGun.get(player))) {
        				if(player.getInventory().getItem(GunData.reloading.get(player)) != null) {
		        			ItemMeta meta = player.getInventory().getItem(GunData.reloading.get(player)).getItemMeta();
		        			
		        			String owner = "";
		        			for(String s : meta.getDisplayName().split(" ")) {
		        				if(s.contains("'s")) {
		        					owner = s + " ";
		        				}
		        			}
		        			
							meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + owner + GunData.reloadGun.get(player).getName() + " " + GunData.reloadGunAmmo.get(player) + "/" + GunData.reloadTotalGunAmmo.get(player));
							
							player.getInventory().getItem(GunData.reloading.get(player)).setItemMeta(meta);
							
							GunData.reloading.remove(player);
							GunData.reloadGun.remove(player);
							GunData.reloadGunAmmo.remove(player);
							GunData.reloadTotalGunAmmo.remove(player);
							
							player.updateInventory();
							
							player.sendMessage(ChatColor.RED + "You switched items while reloading.");
							
							ChatUtils.sendActionBar(player, ChatColor.RED + "" + ChatColor.BOLD + "Reloading Canceled");
        				}
        			}
        		}
    		}
    	}, 0L, 1L);
    }
	
    public void runZoomTask() {
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
    				if(Gun.isGun(player.getItemInHand())) {
    					Gun gun = Gun.getGun(player.getItemInHand().getItemMeta().getDisplayName());
    					
    					if(gun.getZoom() != 0) {
    						if(GunData.zoomed.containsKey(player) && !player.isSneaking()) {
    							GunData.zoomed.remove(player);
    							player.removePotionEffect(PotionEffectType.SLOW);
    							
    							if(player.getInventory().getHelmet() != null) {
	    							if(player.getInventory().getHelmet().getType() == Material.PUMPKIN) {
	    								if(GunData.playerHelmet.containsKey(player)) {
	    									player.getInventory().setHelmet(GunData.playerHelmet.get(player));
	    									GunData.playerHelmet.remove(player);
	    								} else {
	    									player.getInventory().setHelmet(null);
	    								}
	    								player.updateInventory();
	    							}
    							}
    							
    						} else if(player.isSneaking()) {
    							GunData.zoomed.put(player, gun);
    							
    							if(player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() != Material.PUMPKIN) {
    								GunData.playerHelmet.put(player, player.getInventory().getHelmet().clone());
    							}
    							player.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));
    						}
    						
    					} else {
    						if(player.getInventory().getHelmet() != null) {
    	    					if(player.getInventory().getHelmet().getType() == Material.PUMPKIN) {
    								if(GunData.playerHelmet.containsKey(player)) {
    									player.getInventory().setHelmet(GunData.playerHelmet.get(player));
    									GunData.playerHelmet.remove(player);
    								} else {
    									player.getInventory().setHelmet(null);
    								}
    								player.updateInventory();
    							}
        					}
    					}
    					
    				} else if(GunData.zoomed.containsKey(player)) {
    					if(player.getInventory().getHelmet() != null) {
	    					if(player.getInventory().getHelmet().getType() == Material.PUMPKIN) {
								if(GunData.playerHelmet.containsKey(player)) {
									player.getInventory().setHelmet(GunData.playerHelmet.get(player));
									GunData.playerHelmet.remove(player);
								} else {
									player.getInventory().setHelmet(null);
								}
								player.updateInventory();
							}
    					}
	    			}
				}
				
				List<Player> remove = new ArrayList<Player>();
    			for(Player all : GunData.zoomed.keySet()) {
    				if(all.isOnline()) {
	    				if(Gun.isGun(all.getItemInHand())) {
	    					Gun gun = Gun.getGun(all.getItemInHand().getItemMeta().getDisplayName());
	    					
	    					if(gun != null && GunData.zoomed.get(all) != null) {
		    					if(gun.equals(GunData.zoomed.get(all)) && all.isSneaking()) {
			    					all.removePotionEffect(PotionEffectType.SLOW);
			    					all.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, gun.getZoom()));
			    					
			    					if(gun.getScope() == true) {
			    						all.removePotionEffect(PotionEffectType.NIGHT_VISION);
			    						all.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 5, 1));
		    						}
	
		    					} else {
		    						remove.add(all);
		    						all.removePotionEffect(PotionEffectType.SLOW);
		    						
		    						if(gun.getScope() == true) {
		    							all.removePotionEffect(PotionEffectType.NIGHT_VISION);
		    						}
		    					}
	    					} else {
	    						remove.add(all);
		    				}
	    				} else {
	    					remove.add(all);
	    				}
    				} else {
    					remove.add(all);
    				}
    			}
    			for(Player all : remove) {
    				GunData.zoomed.remove(all);
    			}
    		}
    	}, 0L, 1L);
    }
    
    public void runLoopTask() {
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
    		@SuppressWarnings("deprecation")
			public void run() {
    			for(Player all : GunData.looping.keySet()) {
    				if(all.isOnline()) {
	    				if(Gun.isGun(all.getItemInHand())) {
	    					Gun gun = Gun.getGun(all.getItemInHand().getItemMeta().getDisplayName());
	    					
	    					if(gun.equals(GunData.looping.get(all))) {
	    						if(all.getItemInHand().getAmount() > 1) {
	    							if(Gun.canShoot(all, gun, true)) {
	    								gun.shoot(all, gun, true);
	    								
	    							}
	    						} else {
	    							GunData.looping.remove(all);
	    							GunData.delay.remove(all);
	    						}
	    					} else {
	    						GunData.looping.remove(all);
	    						GunData.delay.remove(all);
	    					}
	    				} else {
	    					GunData.looping.remove(all);
	    					GunData.delay.remove(all);
	    				}
    				} else {
    					GunData.looping.remove(all);
    					GunData.delay.remove(all);
    				}
    			}
    			
    			List<Projectile> remove = new ArrayList<Projectile>();
    			int played = 0;
    			for(Projectile all : GunData.bulletVel.keySet()) {
    				if(!all.isDead()) {
    					all.setVelocity(GunData.bulletVel.get(all));
    					
    					for(int x = 1; x <= 4 && played < 4; x++) {
    						Location blockLoc = all.getLocation().add(all.getVelocity().multiply(0.15 * x));
    						
    						if(blockLoc.getBlock().getTypeId() != 0) {
    							all.getWorld().playEffect(blockLoc, Effect.STEP_SOUND, blockLoc.getBlock().getTypeId(), 30);
    							played += 1;
    						}
    					}
    				} else {
    					remove.add(all);
    				}
    			}
    			for(Projectile all : remove) {
    				GunData.bulletVel.remove(all);
    			}
    		}
    	}, 0L, 1L);
    }
    
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if(Gun.isGun(player.getItemInHand())) {
				Gun gun = Gun.getGun(player.getItemInHand().getItemMeta().getDisplayName());
				
				gun.reload(player, gun);
			}
			
		} else if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {			
			if(Gun.isGun(player.getItemInHand())) {
				final Gun gun = Gun.getGun(player.getItemInHand().getItemMeta().getDisplayName());
				
				if(Gun.canShoot(player, gun, true)) {
					gun.shoot(player, gun, true);
									
					if(autoing.containsKey(player.getName())) {
						float bp4t = (float) (((float) gun.getRPM() / (float) 60) / (float) 5);
						
						if(bp4t > 1.0f) {
							float spacing = (float) ((float) 4 / (float) bp4t) + 0.25f;
							
							for(int x = 1; x < bp4t; x++) {
								Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
									public void run() {
										if(Gun.isGun(player.getItemInHand()) && Gun.getGun(player.getItemInHand().getItemMeta().getDisplayName()).getName().equals(gun.getName())) {
											gun.shoot(player, gun, false);
										}
									}
								}, (long) ((x) * (spacing * gun.spaceModifier)));
							}
						}
					}
					
					if(gun.getGunType() != GunType.SECONDARY) {
						autoing.put(player.getName(), 9);
					}
					
					event.setCancelled(true);
					event.setUseItemInHand(Result.DENY);
					
				} else if(!GunData.reloading.containsKey(player) && !GunData.delay.containsKey(player)) {
					gun.reload(player, gun);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerSwitchItem(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		
		if(event.getNewSlot() != 0) {
			GunData.delay.remove(player);
		} else {
			if(!GunData.delay.containsKey(player)) {
				GunData.delay.put(player, (float) 20);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		if(GunData.reloading.containsKey(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			
			if(GunData.reloading.containsKey(player)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onProjectileHit(final ProjectileHitEvent event) {
		if(GunData.bullets.containsKey(event.getEntity())) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					GunData.bullets.remove(event.getEntity());
					GunData.bulletVel.remove(event.getEntity());
					event.getEntity().remove();
				}
			}, 1L);
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		if(event.getCause().equals(TeleportCause.ENDER_PEARL)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Projectile && event.getEntity() instanceof Player) {
			Projectile proj = (Projectile) event.getDamager();
			
			if(GunData.bullets.containsKey(proj)) {
				final Player ent = (Player) event.getEntity();
				
				if(proj.getShooter() instanceof Player) {
					Player shooter = (Player) proj.getShooter();
					
					if(!Team.sameTeam(ent, shooter)) {
						EntityPlayer craftPlayer = ((CraftPlayer) ent).getHandle();
						craftPlayer.killer = ((CraftPlayer) shooter).getHandle();
						
						if(!DeathManager.dead.contains(ent.getName())) {
							//if(proj.getLocation().getY() - event.getEntity().getLocation().getY() >= 1.8d) {
							if(proj.getLocation().getY() - event.getEntity().getLocation().getY() >= 5d) {
								shooter.playSound(shooter.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.75f, 2);
								((Player) ent).playSound(shooter.getLocation(), "guns.hs", 1, 1);
								if(((Player) ent).getInventory().getItem(9) == null) {
									event.setDamage(GunData.bullets.get(proj).getHeadshotDamage());
								} else {
									event.setDamage(GunData.bullets.get(proj).getArmourHeadshotDamage());
								}
								StatManager.addStat(shooter, "cs_headshots", 1);
		
							} else if(proj.getLocation().getY() - event.getEntity().getLocation().getY() <= 1.0d) {
								shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.75f, 2);
								event.setDamage(GunData.bullets.get(proj).getLegDamage());
								
							} else {
								shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1.5f);
								if(((Player) ent).getInventory().getItem(18) == null) {
									event.setDamage(GunData.bullets.get(proj).getDamage());
								} else {
									event.setDamage(GunData.bullets.get(proj).getArmourDamage());
								}
							}
							
							if(!EntityDamageByEntity.playerDamageSet.get(ent).containsKey(shooter)) {
								EntityDamageByEntity.playerDamageSet.get(ent).put(shooter, new ArrayList<Double>());
							}
							EntityDamageByEntity.playerDamageSet.get(ent).get(shooter).add(event.getDamage());
							
							if(event.getDamage() >= ent.getHealth()) {
								Player player = (Player) ent;
								
								if(!Team.sameTeam(player, shooter)) {
									RewardManager.reward(shooter, GunData.bullets.get(proj).getKillReward(), "neutralizing an enemy with the " + GunData.bullets.get(proj).getName());
									
									if(player.getKiller() != null && GunData.bullets.get(proj) != null) {
										Bukkit.broadcastMessage(player.getKiller().getDisplayName() + ChatColor.WHITE + " killed " + player.getDisplayName() + ChatColor.WHITE + " using " + GunData.bullets.get(proj).getName());
									}
									
									DeathManager.die(player);
									player.setVelocity(new Vector(0, 3, 0));
									
								} else {
									event.setCancelled(true);
								}
							}
						} else {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				}
			} 
		}
	}
	
	public boolean isHuman(Entity ent) {
		List<EntityType> humans = Arrays.asList(EntityType.CREEPER, EntityType.PIG_ZOMBIE, EntityType.PLAYER, EntityType.SKELETON, EntityType.SNOWMAN, EntityType.VILLAGER, EntityType.WITCH, EntityType.ZOMBIE);
		
		return humans.contains(ent.getType());
	}
}
