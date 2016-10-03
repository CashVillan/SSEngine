package com.swingsword.ssengine.game.games.rust.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.guns.Gun;
import com.swingsword.ssengine.game.games.rust.ores.OrePile;
import com.swingsword.ssengine.game.games.rust.ores.Ores;
import com.swingsword.ssengine.game.games.rust.utils.ItemUtils;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.game.games.rust.utils.SpawnUtils;
import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class OresListener implements Listener {
	
	public ArrayList<String> clearedChunks = new ArrayList<String>();
	
	/*@SuppressWarnings("deprecation")
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		Chunk chunk = event.getChunk();
		
		if(!clearedChunks.contains(chunk.getX() + ";" + chunk.getZ())) {
			clearedChunks.add(chunk.getX() + ";" + chunk.getZ());
			
			for(int x = 0; x < 16; x++) {
				for(int z = 0; z < 16; z++) {
					for(int y = 55; y < 100; y++) {
						Block block = chunk.getBlock(x, y, z);
						
						if(block.getType() == Material.IRON_ORE || block.getType() == Material.GOLD_ORE || block.getType() == Material.LOG && block.getData() == 3) {
							if(Ores.oph.getOrePile(block.getLocation()) == null) {
								block.setType(Material.AIR);
							}
						}
					}
				}
			}
		}
	}*/
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile proj = event.getEntity();
		
		if(proj.getShooter() instanceof Player) {
			Player player = (Player) proj.getShooter();
			
			if(player.getItemInHand().getDurability() == 0 && !Gun.isGun(player.getItemInHand())) {
				Vector direction = proj.getVelocity().clone();
				proj.teleport(proj.getLocation().add(player.getLocation().getDirection()));
				proj.setVelocity(direction);
				
				ItemUtils.runDelayBar(player, 50, 50, player.getInventory().getHeldItemSlot());
				
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamageItem(PlayerItemDamageEvent event) {
		final Player player = event.getPlayer();
		
		event.setCancelled(true);
		
		player.updateInventory();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(player.isOnline()) {
					player.updateInventory();
				}
			}
		}, 1);
	}
	
	//TODO
	/*@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onArrowHitBlock(ArrowHitBlockEvent event) {
		final Player player = (Player) event.getArrow().getShooter();
		
		if(player != null) {
			if(getBlockHealth(event.getBlock().getLocation()) > 0) {
				if(blockHealth.containsKey(event.getBlock().getLocation())) {
					blockHealth.put(event.getBlock().getLocation(), blockHealth.get(event.getBlock().getLocation()) - 1);
					
					if(blockHealth.get(event.getBlock().getLocation()) == 0) {
						if(event.getBlock().getType() == Material.BED_BLOCK) {
							if(event.getBlock().getData() > 7) {
								for(int x = -1; x <= 1; x++) {
									for(int z = -1; z <= 1; z++) {
										if(!(x == 0 && z == 0)) {
											if(event.getBlock().getRelative(x, 0, z).getType() == Material.BED_BLOCK && event.getBlock().getRelative(x, 0, z).getData() < 8) {
												event.getBlock().getRelative(x, 0, z).setType(Material.AIR);
											}
										}
									}
								}
							}
							
						} else if(event.getBlock().getType() == Material.WOODEN_DOOR) {
							if(event.getBlock().getRelative(0, -1, 0).getType() == Material.WOODEN_DOOR) {
								event.getBlock().getRelative(0, -1, 0).setType(Material.AIR);
							}
							
						} else if(event.getBlock().getType() == Material.CARPET) {
							if(event.getBlock().getData() > 7) {
								for(int x = -1; x <= 1; x++) {
									for(int z = -1; z <= 1; z++) {
										if(!(x == 0 && z == 0)) {
											if(event.getBlock().getRelative(x, 0, z).getType() == Material.CARPET) {
												event.getBlock().getRelative(x, 0, z).setType(Material.AIR);
											}
										}
									}
								}
							}
						}
		
						event.getBlock().setType(Material.AIR);
						
						blockHealth.remove(event.getBlock().getLocation());
					}
					
				} else {
					blockHealth.put(event.getBlock().getLocation(), getBlockHealth(event.getBlock().getLocation()) - 1);
				}
				
				if(blockHealth.containsKey(event.getBlock().getLocation())) {
					if(event.getArrow().getShooter() instanceof Player) {
						int health = blockHealth.get(event.getBlock().getLocation()) * (100 / getBlockHealth(event.getBlock().getLocation()));
						ChatColor color = ChatColor.GREEN;
						
						if(health < 70) {
							color = ChatColor.YELLOW;
						}
						if(health < 50) {
							color = ChatColor.DARK_RED;
						}
						
						player.sendMessage(ChatColor.GRAY + "That block has " + color + health + ChatColor.GRAY + " health remaining.");				
					}
				}
			}
		}
	}*/
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		if(Ores.cooldown.contains(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			
			if(Ores.cooldown.contains(player)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Ores.cooldown.remove(event.getPlayer());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
    	final Player player = event.getPlayer();
    	
    	if(!Ores.cooldown.contains(player)) {
			if(event.getAction().name().contains("LEFT")) {
				event.setCancelled(true);
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						if(player.isOnline()) {
							Ores.cooldown.add(player);
						}
					}
				}, 1l);
				
				int delay = 2;
				
				if(player.getItemInHand().getType().name().toLowerCase().contains("pickaxe")) {
					delay = 50;
					
				} else if(player.getItemInHand().getType().name().toLowerCase().contains("wood_axe")) {
					delay = 40;
					
				} else if(player.getItemInHand().getType().name().toLowerCase().contains("axe")) {
					delay = 20;
				}
				
				if(delay != 2) {
					ItemUtils.runDelayBar(player, delay, delay, player.getInventory().getHeldItemSlot());
				}
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						if(player.isOnline()) {
							Ores.cooldown.remove(player);
						}
					}
				}, delay);
			}
			
			if(event.getClickedBlock() != null && event.getAction() == Action.LEFT_CLICK_BLOCK) {
    			player.playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, event.getClickedBlock().getTypeId());
    			
				int d = Ores.getPower(player.getItemInHand().getType(), event.getClickedBlock());
				
    			if(d > 0) {
    				OrePile op = Ores.oph.getOrePile(event.getClickedBlock().getLocation());
    				
    				if(op != null) {
    	    			if(op.getBlocks().contains(event.getClickedBlock().getLocation())) {
    	    				Material type = event.getClickedBlock().getType();
    	    				
			    			for(String all : ConfigUtils.getConfig("cache").getStringList("ores." + type.name().toLowerCase() + "." + Ores.getTool(player.getItemInHand().getType()) + "." + Ores.getRandomDrops(type.name().toLowerCase(), Ores.getTool(player.getItemInHand().getType())))) {
			    				 
			    				ItemUtils.giveItem(player, com.swingsword.ssengine.utils.ItemUtils.itemStackFromString(all), event.getClickedBlock().getLocation().add(0.5, 0, 0.5));
			    			}
			    			
			    			if(player.hasPermission("ss.rust.doubleresources")) {
			    				for(String all : ConfigUtils.getConfig("cache").getStringList("ores." + type.name().toLowerCase() + "." + Ores.getTool(player.getItemInHand().getType()) + "." + Ores.getRandomDrops(type.name().toLowerCase(), Ores.getTool(player.getItemInHand().getType())))) {
				    				ItemUtils.giveItem(player, com.swingsword.ssengine.utils.ItemUtils.itemStackFromString(all), event.getClickedBlock().getLocation().add(0.5, 0, 0.5));
				    			}
			    			}
			    			
    						op.breakLayer(d);
    	    			}
    				} else {
    					ItemStack ore = new ItemStack(382, 4);
    					ItemMeta oremeta = ore.getItemMeta();
    					oremeta.setDisplayName(ChatColor.WHITE + "Sulfur Ore");
    					ore.setItemMeta(oremeta);
    					player.getInventory().addItem(ore);
    					
    					LocationUtils.removeNear(event.getClickedBlock().getLocation(), event.getClickedBlock().getType());
    				}
    				
    			} else {
    				if(Ores.getBlockHealth(event.getClickedBlock().getLocation()) > 0) {
    					if(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 7).equals(event.getClickedBlock())) {
	    					int health1 = Ores.getBlockHealth(event.getClickedBlock().getLocation());
	    					
	    					if(Ores.getTool(player.getItemInHand().getType()) != null) {
		    					if(Ores.blockHealth.containsKey(event.getClickedBlock().getLocation())) {
		    						Ores.blockHealth.put(event.getClickedBlock().getLocation(), Ores.blockHealth.get(event.getClickedBlock().getLocation()) - 1);
		    						
									if (Ores.blockHealth.get(event.getClickedBlock().getLocation()) == 0) {
										if (event.getClickedBlock().getType() == Material.BED_BLOCK || event.getClickedBlock().getType() == Material.CARPET && event.getClickedBlock().getData() > 7) {
											SpawnUtils.removeHome(null, event.getClickedBlock().getLocation());
											
										} else if(event.getClickedBlock().getType() == Material.WOODEN_DOOR) {
		    								if(event.getClickedBlock().getRelative(0, -1, 0).getType() == Material.WOODEN_DOOR) {
		    									event.getClickedBlock().getRelative(0, -1, 0).setType(Material.AIR);
		    								}
		    								
		    							} else if(event.getClickedBlock().getType() == Material.CHEST) {
	    									for(int x = -1; x <= 1; x++) {
	    										for(int z = -1; z <= 1; z++) {
	    											if(!(x == 0 && z == 0)) {
	    												if(event.getClickedBlock().getRelative(x, 0, z).getType() == Material.CHEST) {
	    													event.getClickedBlock().getRelative(x, 0, z).setType(Material.AIR);
	    												}
	    											}
	    										}
		    								}
		    							}
		    							
		    							event.getClickedBlock().setType(Material.AIR);
		    							
		    							Ores.blockHealth.remove(event.getClickedBlock().getLocation());
		    						}
								} else {
									Ores.blockHealth.put(event.getClickedBlock().getLocation(), health1 - 1);
								}

								if (Ores.blockHealth.containsKey(event.getClickedBlock().getLocation())) {
									int health = Ores.blockHealth.get(event.getClickedBlock().getLocation()) * (100 / Ores.getBlockHealth(event.getClickedBlock().getLocation()));
									ChatColor color = ChatColor.GREEN;

									if (health < 70) {
										color = ChatColor.YELLOW;
									}
									if (health < 50) {
										color = ChatColor.DARK_RED;
									}
									
									StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", color + "" + health + ChatColor.GRAY + "/100 health remaining.");
								}

							} else {
								StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ERROR", ChatColor.GRAY + "Use a tool to break!");
							}
						}
					}
				}
    			
    			if(event.getClickedBlock().getType() == Material.LOG && event.getClickedBlock().getData() == (byte) 1) {
    				if(Ores.getTool(player.getItemInHand().getType()) != null || new Random().nextInt(20) == 0) {
	    				ItemStack wood = new ItemStack(Material.SLIME_BALL, 1);
	    				ItemMeta wm = wood.getItemMeta();
	    				wm.setDisplayName(ChatColor.WHITE + "Wood");
	    				wood.setItemMeta(wm);
	    				
	    				player.getInventory().addItem(wood);
	    				
    				} else {
    					player.sendMessage(ChatColor.RED + "You can't break that block without a tool. Maybe if you're lucky you do get some if you try hard...");
    				}
    			}
    			
			} else if(event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(player.getItemInHand().getType().name().toLowerCase().contains("hoe")) {
					event.setUseInteractedBlock(Result.DENY);
				}
				
				if(event.getClickedBlock().getType() == Material.CHEST) {
					event.setCancelled(false);
				}
			}
			
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
				if(player.getItemInHand() != null) {
					if(player.getItemInHand().getType().name().toLowerCase().contains("door")) {
						if(event.getClickedBlock() != null) {
							if(Ores.getBlockHealth(event.getClickedBlock().getLocation()) == -1) {
								event.setCancelled(false);	
							} else {
								event.setUseItemInHand(Result.DENY);
							}
						}
					}
					
					String name = player.getItemInHand().getType().name().toLowerCase();
					
					if(name.contains("stone") || name.contains("torch") || name.contains("wool") || name.contains("pumpkin") || name.contains("snow_ball") || name.contains("egg") || name.contains("log") || name.contains("brick") || name.contains("grass") || name.contains("gravel")) {
						event.setCancelled(true);
						player.closeInventory();
					}
				}
			}
	    	
    	} else {
    		if(player.getGameMode() != GameMode.CREATIVE) {
    			event.setCancelled(true);
    		}
    	}
    	
    	if(player.isOp() && player.getGameMode() == GameMode.CREATIVE && event.getClickedBlock() != null && event.getClickedBlock().getType() != Material.CHEST && event.getClickedBlock().getType() != Material.ANVIL) {
    		event.setCancelled(false);
    	}
    }
}
