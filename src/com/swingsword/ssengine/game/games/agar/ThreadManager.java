package com.swingsword.ssengine.game.games.agar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.minestrike.utils.ChatUtils;
import com.swingsword.ssengine.stats.StatManager;
import com.swingsword.ssengine.utils.IntegerUtils;

public class ThreadManager {
	
	public static int ticksPassed = 0;
	public static int updateSpeed = 1;
	
	public ThreadManager() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				ticksPassed += 1;
				
				for(World world : Bukkit.getWorlds()) {
					for(Entity all : world.getEntities()) {
						if(all instanceof Item) {
							Item item = (Item) all;
							
							if(item.getItemStack().getType() == Material.ARMOR_STAND) {
								all.remove();
								
								ArmorStand stand = (ArmorStand) all.getWorld().spawnEntity(all.getLocation().add(0, -2, 0), EntityType.ARMOR_STAND);
								stand.setVisible(false);
								stand.setBasePlate(false);
								stand.setHelmet(new ItemStack(Material.WOOL, 1, (byte) new Random().nextInt(16)));
							}
						}
					}
				}
				
				while(AgarManager.food.size() < 400) {
					AgarManager.spawnFood(Bukkit.getWorld("map"));
				}
				while(AgarManager.virus.size() < 40) {
					AgarManager.spawnVirus(Bukkit.getWorld("map"), null);
				}
				
				for(Entity all : AgarManager.virus) {
					Location loc = all.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
					loc.setPitch(-90f);
					
					all.teleport(loc);
				}
				
				for(Entity all : Bukkit.getWorld("map").getEntities()) {
					if(all instanceof ArmorStand) {
						if(AgarEntity.getAgarEntity(all) == null && (all.getCustomName() == null || !IntegerUtils.isInteger(ChatColor.stripColor(all.getCustomName()))) && !AgarManager.holograms.containsKey(all)) {
							all.remove();
						}
					}
				}
				
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(all.getOpenInventory().getTitle().contains("container.crafting") && (!AgarManager.agaring.containsKey(all) || AgarManager.agaring.get(all) == null)) {
						AgarManager.spawnPlayer(all);
					}
					
					if(AgarManager.agaring.containsKey(all)) {
						ChatUtils.sendActionBar(all, ChatColor.WHITE + "" + ChatColor.BOLD + "Score: " + PlayerStats.getStats(all).highestMass);
						PlayerStats.getStats(all).timeAlive += updateSpeed;
						if(ticksPassed % (20 / updateSpeed) == 0) {
							StatManager.addStat(all, "ar_time_alive", 1);
						}
					}
				}
				
				List<Runnable> remove = new ArrayList<Runnable>();
				final List<Entity> eaten = new ArrayList<Entity>();
				for(final AgarEntity ent : AgarEntity.agarEntities) {
					if(AgarManager.mass.containsKey(ent.entity)) {
						if(AgarManager.getOwner(ent.entity) != null) {
							Player player = AgarManager.getOwner(ent.entity);
							
							if(PlayerStats.getStats(player).highestMass < AgarManager.getTotalMass(player)) {
								PlayerStats.getStats(player).highestMass = AgarManager.getTotalMass(player);
								
								if(StatManager.getStat(player, "ar_highest_mass") < AgarManager.getTotalMass(player)) {
									StatManager.setStat(player, "ar_highest_mass", AgarManager.getTotalMass(player));
								}
							}
						}
						
						if(ent.canEat) {
							for(final Entity all : ent.entity.getNearbyEntities(-0.65f, 3, -0.65f)) {
								AgarEntity nearbyEnt = AgarEntity.getAgarEntity(all);
								
								if(all instanceof Item) {
									AgarManager.mass.put(ent.entity, AgarManager.mass.get(ent.entity) + ((Item) all).getItemStack().getAmount());
									all.remove();
								}
								
								if(nearbyEnt != null && nearbyEnt.canbeEaten) {
									if(!(all instanceof Slime)) {
										if(AgarManager.virus.contains(all)) {				
											if(AgarManager.mass.get(ent.entity) > 133) {
												Runnable run = new Runnable() {
													public void run() {
														if(AgarManager.mass.get(ent.entity) > 133) {
															Player player = AgarManager.getOwner(ent.entity);
															
															for(Slime slimes : AgarManager.explode((Slime) ent.entity)) {
																AgarManager.splitSlimes.get(player).add(slimes);
															}
															
															AgarEntity.agarEntities.remove(AgarEntity.getAgarEntity(all));
			
															AgarManager.virus.remove(all);
															all.remove();
															
															AgarManager.spawnVirus(ent.entity.getWorld(), null);
															
															StatManager.addStat(AgarManager.getOwner(ent.entity), "ar_viruses_eaten", 1);
														}
													}
												};
												remove.add(run);
											}
											
										} else {
											try {			
												Runnable run = new Runnable() {
													public void run() {
														if(!eaten.contains(all)) {
															eaten.add(all);
															
															AgarManager.mass.put(ent.entity, AgarManager.mass.get(ent.entity) + 1);
															PlayerStats.getStats(AgarManager.getOwner(ent.entity)).foodEaten += 1;
															StatManager.addStat(AgarManager.getOwner(ent.entity), "ar_food_eaten", 1);
															
															AgarEntity.agarEntities.remove(AgarEntity.getAgarEntity(all));
			
															AgarManager.food.remove(all);
															all.remove();
															
															AgarManager.spawnFood(ent.entity.getWorld());
														}
													}
												};
												remove.add(run);
											} catch ( Exception e) { }
										}
										
									} else if(all instanceof Slime && AgarManager.getOwner(all) == null && !AgarManager.delay.contains(all)) {
										Runnable run = new Runnable() {
											public void run() {
												try {
													AgarManager.mass.put(ent.entity, AgarManager.mass.get(ent.entity) + AgarManager.mass.get(all));
													AgarManager.destroySlime((Slime) all);
												} catch (Exception e) { }
											}
										};
										remove.add(run);
										
									} else if(all instanceof Slime && AgarManager.mass.containsKey(all) && AgarManager.getOwner(all) != null && AgarManager.getOwner(ent.entity) != null && !AgarManager.getOwner(all).getName().equals(AgarManager.getOwner(ent.entity).getName())) {
										final Slime otherslimes = (Slime) all;
										
										if(AgarManager.mass.get(all) < AgarManager.mass.get(ent.entity)) {
											Player target = null;
											for(Player players : AgarManager.agaring.keySet()) {
												if(AgarManager.agaring.get(players).equals(otherslimes)) {
													target = players;
												}
											}
											final Player finalTarget = target;
											
											Runnable run = new Runnable() {
												public void run() {
													if(AgarManager.mass.containsKey(otherslimes)) {
														if(AgarManager.mass.containsKey(ent.entity)) {
															AgarManager.mass.put(ent.entity, AgarManager.mass.get(ent.entity) + AgarManager.mass.get(otherslimes));
															PlayerStats.getStats(AgarManager.getOwner(ent.entity)).cellsEaten += 1;
															StatManager.addStat(AgarManager.getOwner(ent.entity), "ar_cells_eaten", 1);
															
															if(finalTarget != null) {
																Slime biggestCell = null;
																for(Slime all : AgarManager.splitSlimes.get(finalTarget)) {
																	if(biggestCell == null || AgarManager.mass.get(biggestCell) < AgarManager.mass.get(all)) {
																		biggestCell = all;
																	}
																}
																
																if(biggestCell == null) {
																	AgarManager.die(finalTarget);
																	AgarManager.stopAgar(finalTarget);
																} else {
																	AgarManager.moveToSecondaryCell(finalTarget, biggestCell);
																}
															}
															
															AgarManager.destroySlime(otherslimes);
														} else {
															AgarManager.destroySlime((Slime) ent.entity);
														}
													}
												}
											};
											remove.add(run);
										}
										
									} else if(all instanceof Slime && ent.entity != all && AgarManager.agaring.values().contains(ent.entity) && AgarManager.mass.containsKey(all) && AgarManager.getOwner(ent.entity) == AgarManager.getOwner(all)) {
										if(!AgarManager.delay.contains(all)) {
											Runnable run = new Runnable() {
												public void run() {
													try {
														AgarManager.mass.put(ent.entity, AgarManager.mass.get(ent.entity) + AgarManager.mass.get(all));
														AgarManager.destroySlime((Slime) all);
													} catch (Exception e) { }
												}
											};
											remove.add(run);
										}
									}
								}
							}
						}
					}
				}
				for(Runnable all : remove) {
					all.run();
				}
				
				remove.clear();
				for(Player player : AgarManager.agaring.keySet()) {
					Slime slime = AgarManager.agaring.get(player);
					
					if(slime == null || !AgarManager.mass.containsKey(slime)) {
						AgarManager.spawnPlayer(player);
						
					} else {
						if((ticksPassed % (60 / updateSpeed)) + 1 <= AgarManager.getShrinkSpeed(AgarManager.mass.get(slime))) {
							AgarManager.mass.put(slime, AgarManager.mass.get(slime) - 1);
							
							//TODO delay
						}
						
					    slime.setSize(AgarManager.getSize(AgarManager.mass.get(slime)));
					    AgarManager.updateHeight(player, slime);
					    
					    AgarManager.holograms.get(slime).setCustomName(ChatColor.BOLD + "" + AgarManager.mass.get(slime));
					    
					    for(Slime slimes : AgarManager.splitSlimes.get(player)) {
					    	if((ticksPassed % ((60 * 20) / updateSpeed)) + 1 <= AgarManager.getShrinkSpeed(AgarManager.mass.get(slimes))) {
								AgarManager.mass.put(slimes, AgarManager.mass.get(slimes) - 1);
							}
					    	
					    	slimes.setSize(AgarManager.getSize(AgarManager.mass.get(slimes)));
						    AgarManager.updateHeight(player, slimes);
						    
						    AgarManager.holograms.get(slimes).setCustomName(ChatColor.BOLD + "" + AgarManager.mass.get(slimes) + "");
					    }
						
					    Vector vec = AgarManager.getMovementDirection(player);
	
						if(!AgarManager.stopped.contains(player)) {
							if(player.getLocation().getPitch() != 90) {
								for(Slime slimes : AgarManager.splitSlimes.get(player)) {
									if(!AgarManager.noTeleport.contains(slimes)) {
										Location loc = slimes.getLocation().add(vec.clone().multiply(AgarManager.getSpeed(AgarManager.mass.get(slimes))));
										
										if(loc.getBlockX() < 0) {
											loc.setX(0);
										}
										if(loc.getBlockZ() < 0) {
											loc.setZ(0);
										}
										if(loc.getBlockX() > AgarManager.mapSize + 1) {
											loc.setX(AgarManager.mapSize + 1);
										}
										if(loc.getBlockZ() > AgarManager.mapSize + 1) {
											loc.setZ(AgarManager.mapSize + 1);
										}
										
										slimes.teleport(loc);
									}	
								}
							
								player.setVelocity(vec.clone().multiply(AgarManager.getSpeed(AgarManager.mass.get(slime))));
								
							} else {
								for(Slime slimes : AgarManager.splitSlimes.get(player)) {
									if(!AgarManager.noTeleport.contains(slimes)) {
										Location loc = slimes.getLocation().add(AgarManager.getMovementDirection(slimes, slime).clone().multiply(AgarManager.getSpeed(AgarManager.mass.get(slimes))).multiply(updateSpeed));
										
										slimes.teleport(loc);
									}	
								}
							}
							
							slime.teleport(new Location(player.getWorld(), player.getLocation().getX(), player.getWorld().getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()), player.getLocation().getZ()));
						}
						
						if(player.isSprinting()) {
							player.setVelocity(new Vector());
						}
					}
				}
				
				
			}
		}, updateSpeed, updateSpeed);
	}
}
