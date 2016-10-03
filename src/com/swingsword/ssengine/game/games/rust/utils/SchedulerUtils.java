package com.swingsword.ssengine.game.games.rust.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.guns.Gun;
import com.swingsword.ssengine.game.games.rust.guns.GunData;
import com.swingsword.ssengine.game.games.rust.guns.Guns;
import com.swingsword.ssengine.game.games.rust.mobs.Mob;
import com.swingsword.ssengine.game.games.rust.mobs.Mobs;
import com.swingsword.ssengine.game.games.rust.mobs.Region;
import com.swingsword.ssengine.stats.StatManager;

public class SchedulerUtils extends Rust {

	public static int ticksPassed = 0;
	
	public static void loadSchedulers() {
		ScoreboardManager.loopScoreboardLoad();
		
		// Food

		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (all.isSprinting()) {
						if (PropUtils.playerFood.get(all.getName()) > 0) {
							PropUtils.playerFood.put(all.getName(), PropUtils.playerFood.get(all.getName()) - 1);
						}
					}
				}
			}
		}, 20l, 30L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (PropUtils.playerFood.get(all.getName()) > 1) {
						PropUtils.playerFood.put(all.getName(), PropUtils.playerFood.get(all.getName()) - 1);
					}
				}
			}
		}, 20l, 240L);

		// Radiation

		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (RadiationUtils.isInRadZone(all.getLocation())) {
						RadiationUtils.playerRad.put(all.getName(), RadiationUtils.playerRad.get(all.getName()) + 4);
						all.playSound(all.getLocation(), "custom.radiation", 1, 1);

					} else {
						RadiationUtils.playerRad.put(all.getName(), RadiationUtils.playerRad.get(all.getName()) - 4);

						if (RadiationUtils.playerRad.get(all.getName()) < 0) {
							RadiationUtils.playerRad.put(all.getName(), 0);
						}
					}
				}
			}
		}, 20L, 20L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (RadiationUtils.playerRad.get(all.getName()) >= 500) {
						all.damage(2d);
						all.sendMessage(ChatColor.RED + "You are taking extreme amounts of radiation!");
					}
				}

				ArrayList<String> remove = new ArrayList<String>();
				for (String all : bleeding) {
					if (Bukkit.getPlayer(all).getHealth() - 0.4d > 0) {
						Bukkit.getPlayer(all).setHealth(Bukkit.getPlayer(all).getHealth() - 0.4d);
					} else {
						remove.add(all);
						Bukkit.getPlayer(all).damage(Bukkit.getPlayer(all).getHealth());
						Bukkit.getPlayer(all).sendMessage(ChatColor.RED + "Too late, you bled to death...");
					}
				}

				for (String all : remove) {
					bleeding.remove(all);
				}
			}
		}, 200L, 200L);

		// Torch Material.TORCH

		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (all.getItemInHand().getType() == Material.TORCH) {
						if (all.getItemInHand().getAmount() - 1 > 0) {
							all.getItemInHand().setAmount(all.getItemInHand().getAmount() - 1);

						} else {
							all.setItemInHand(null);
						}
					}
				}
			}
		}, 80l, 80l);

		// Regen

		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (!bleeding.contains(all.getName())) {
						if (PropUtils.playerFood.get(all.getName()) == 0 && all.getHealth() - 0.2d >= 0) {
							if (all.getHealth() - 0.2d > 0) {
								all.setHealth(all.getHealth() - 0.2d);
							} else {
								all.damage(all.getHealth());
							}

						} else if (PropUtils.playerFood.get(all.getName()) >= 500 && all.getHealth() + 0.2d <= 20) {
							all.setHealth(all.getHealth() + 0.2d);
						}
					}
				}
			}
		}, 20l, 120L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					StatManager.addStat(all, "rt_time_played", 1);
				}
			}
		}, 20, 20);
		
		// Cooking

		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				ticksPassed++;

				if(ticksPassed % 5 == 0) {
					ItemStack toggle = new ItemStack(Material.INK_SACK);
					toggle.setDurability((short) 6);
					ItemMeta togglem = toggle.getItemMeta();
					togglem.setDisplayName(ChatColor.RED + "Off");
					toggle.setItemMeta(togglem);
	
					for (String all : CookingUtils.furnaceInventory.keySet()) {
						boolean contains = false;
	
						if (CookingUtils.containsFurnaceFuel(CookingUtils.furnaceInventory.get(all))) {
							for (ItemStack items : CookingUtils.furnaceInventory.get(all).getContents()) {
								if (items != null) {
									if (items.getData().getData() == 13) {
										contains = true;
									}
								}
							}
							if (contains) {
								for (int x = 3; x < 7; x++) {
									new ParticleUtils(ParticleUtils.ParticleType.FLAME, 0.005, 2, 0.1).sendToLocation(new SimpleLocation(all).toLocation().clone().add(0.5, (float) 0.1 + (float) ((float) x / (float) 10), 0.5));
									new ParticleUtils(ParticleUtils.ParticleType.SMOKE_NORMAL, 0.005, 1, 0.1).sendToLocation(new SimpleLocation(all).toLocation().clone().add(0.5, (float) 1 + (float) ((float) new Random().nextInt(7) / (float) 5), 0.5));
								}
								if (new Random().nextInt(7) == 0) {
									new SimpleLocation(all).getWorld().playSound(new SimpleLocation(all).toLocation().clone().add(0.5, 0.1, 0.5), Sound.BLOCK_FIRE_AMBIENT, 0.25F, 1);
								}
							}
						} else {
							if (CookingUtils.furnaceInventory.get(all).getItem(13).getDurability() != 6 && (byte) (new SimpleLocation(all).getBlock().getData() - 4) != 3) {
								CookingUtils.furnaceInventory.get(all).setItem(13, toggle);
								new SimpleLocation(all).getBlock().setData((byte) (new SimpleLocation(all).getBlock().getData() - 4));
							}
						}
					}
	
					for (String all : CookingUtils.fireInventory.keySet()) {
						boolean contains = false;
	
						if (CookingUtils.containsFireFuel(CookingUtils.fireInventory.get(all))) {
							for (ItemStack items : CookingUtils.fireInventory.get(all).getContents()) {
								if (items != null) {
									if (items.getData().getData() == 13) {
										contains = true;
									}
								}
							}
							if (contains) {
								new ParticleUtils(ParticleUtils.ParticleType.FLAME, 0.005, 2, 0.1).sendToLocation(new SimpleLocation(all).toLocation().clone().add(0.5, (float) 0.1, 0.5));
	
								for (int x = 5; x < 7; x++) {
									new ParticleUtils(ParticleUtils.ParticleType.SMOKE_NORMAL, 0.005, 1, 0.1).sendToLocation(new SimpleLocation(all).toLocation().clone().add(0.5, (float) 0.1 + (float) ((float) new Random().nextInt(7) / (float) 6), 0.5));
								}
								if (new Random().nextInt(7) == 0) {
									new SimpleLocation(all).getWorld().playSound(new SimpleLocation(all).toLocation().clone().add(0.5, 0.1, 0.5), Sound.BLOCK_FIRE_AMBIENT, 0.25F, 1);
								}
							}
						} else {
							CookingUtils.fireInventory.get(all).setItem(13, toggle);
						}
					}
				}
			}
		}, 1L, 1L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				ticksPassed++;

				for (Player all : Bukkit.getOnlinePlayers()) {
					all.setFoodLevel(18);
				}
			}
		}, 10L, 10L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Inventory inv : CookingUtils.fireInventory.values()) {
					List<Integer> slots = new ArrayList<Integer>(Arrays.asList(3, 5, 21, 23));

					if(inv.getViewers().size() > 0) {
						for (int slot : slots) {
							ItemStack item = inv.getItem(slot).clone();
	
							if (inv.getItem(slot).getType() == Material.SLIME_BALL) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.STICK));
	
							} else if (inv.getItem(slot).getType() == Material.STICK) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.COAL));
	
							} else if (inv.getItem(slot).getType() == Material.COAL) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.INK_SACK));
								inv.getItem(slot).setDurability((short) 11);
	
							} else if (inv.getItem(slot).getType() == Material.INK_SACK && slot == 23) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.SLIME_BALL));
								inv.getItem(slot).setDurability((short) 0);
	
							} else if (inv.getItem(slot).getType() == Material.COAL) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.INK_SACK));
								inv.getItem(slot).setDurability((short) 11);
	
							} else if (inv.getItem(slot).getType() == Material.INK_SACK && slot == 5) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.COAL));
								inv.getItem(slot).setDurability((short) 1);
							} else if (inv.getItem(slot).getType() == Material.RAW_CHICKEN) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.SEEDS));
	
							} else if (inv.getItem(slot).getType() == Material.SEEDS && slot == 3) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.RAW_CHICKEN));
	
							} else if (inv.getItem(slot).getType() == Material.COOKED_CHICKEN) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.SEEDS));
	
							} else if (inv.getItem(slot).getType() == Material.SEEDS && slot == 21) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.COOKED_CHICKEN));
							}
						}
					}
				}

				for (Inventory inv : CookingUtils.furnaceInventory.values()) {
					List<Integer> slots = new ArrayList<Integer>(Arrays.asList(3, 5, 21, 23));

					if(inv.getViewers().size() > 0) {
						for (int slot : slots) {
							ItemStack item = inv.getItem(slot).clone();
	
							if (inv.getItem(slot).getType() == Material.SPECKLED_MELON) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.FERMENTED_SPIDER_EYE));
	
							} else if (inv.getItem(slot).getType() == Material.FERMENTED_SPIDER_EYE) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.EMPTY_MAP));
	
							} else if (inv.getItem(slot).getType() == Material.EMPTY_MAP) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.SEEDS));
	
							} else if (inv.getItem(slot).getType() == Material.SEEDS && slot == 3) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.SPECKLED_MELON));
	
							} else if (inv.getItem(slot).getType() == Material.SLIME_BALL) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.STICK));
	
							} else if (inv.getItem(slot).getType() == Material.STICK) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.COAL));
	
							} else if (inv.getItem(slot).getType() == Material.COAL) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.INK_SACK));
								inv.getItem(slot).setDurability((short) 11);
	
							} else if (inv.getItem(slot).getType() == Material.INK_SACK && slot == 23) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.SLIME_BALL));
								inv.getItem(slot).setDurability((short) 0);
	
							} else if (inv.getItem(slot).getType() == Material.FLINT) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.GLOWSTONE_DUST));
	
							} else if (inv.getItem(slot).getType() == Material.GLOWSTONE_DUST) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.LEATHER));
	
							} else if (inv.getItem(slot).getType() == Material.LEATHER) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.SEEDS));
	
							} else if (inv.getItem(slot).getType() == Material.SEEDS && slot == 21) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.FLINT));
	
							} else if (inv.getItem(slot).getType() == Material.COAL) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.INK_SACK));
								inv.getItem(slot).setDurability((short) 11);
	
							} else if (inv.getItem(slot).getType() == Material.INK_SACK && slot == 5) {
								inv.setItem(slot, ItemUtils.changeType(item, Material.COAL));
								inv.getItem(slot).setDurability((short) 1);
							}
						}
					}
				}

				// Flare

				for (String all : playerFlare.keySet()) {
					if (playerFlare.get(all) != Bukkit.getPlayer(all).getInventory().getHeldItemSlot()) {
						ItemStack item = Bukkit.getPlayer(all).getInventory().getItem(playerFlare.get(all));
						item.setAmount(item.getAmount() - 1);
						if (item.getAmount() <= 0) {
							item = null;
						}

						Bukkit.getPlayer(all).getInventory().setItem(playerFlare.get(all), item);
						playerFlare.remove(all);
					}
				}
			}
		}, 10L, 10L);

		//Guns
		
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
    		}
    	}, 1L, 1L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
    				if(Gun.isGun(player.getItemInHand())) {
    					Gun gun = Gun.getGun(ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName()));
    					
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
    					
    				} else if(GunData.zoomed.containsKey(player)) {
						player.sendMessage("stop");

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
				
				ArrayList<Player> remove = new ArrayList<Player>();
    			for(Player all : GunData.zoomed.keySet()) {
    				if(all.isOnline()) {
	    				if(Gun.isGun(all.getItemInHand())) {
	    					Gun gun = Gun.getGun(all.getItemInHand().getItemMeta().getDisplayName());
	    					
	    					if(gun != null && GunData.zoomed.get(all) != null) {
		    					if(gun.equals(GunData.zoomed.get(all)) && all.isSneaking()) {
			    					all.removePotionEffect(PotionEffectType.SLOW);
			    					all.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, gun.getZoom()));
			    					
			    					if(ItemUtils.getLore(all.getItemInHand()) != null) {
			    						if(ItemUtils.loreContains(ItemUtils.getLore(all.getItemInHand()), "Holo Sight")) {
			    							if(!GunData.playerHelmet.containsKey(all)) {
			    								if(all.getInventory().getHelmet() != null && all.getInventory().getHelmet().getType() != Material.PUMPKIN) {
			    									GunData.playerHelmet.put(all, all.getInventory().getHelmet().clone());
			    								}
			    							}
			    							all.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));
			    						}
			    					}
			    					
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
    	}, 10L, 10L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
    		@SuppressWarnings("deprecation")
			public void run() {
    			for(Player all : GunData.looping.keySet()) {
    				if(all.isOnline()) {
	    				if(Gun.isGun(all.getItemInHand())) {
	    					Gun gun = Gun.getGun(all.getItemInHand().getItemMeta().getDisplayName());
	    					
	    					if(gun.equals(GunData.looping.get(all))) {
	    						if(all.getItemInHand().getAmount() > 1) {
	    							if(Gun.canShoot(all, gun)) {
	    								gun.shoot(all, gun);
	    								
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
    			for(Projectile all : GunData.bulletVel.keySet()) {
    				if(!all.isDead()) {
    					all.setVelocity(GunData.bulletVel.get(all));
    					
    					for(int x = 1; x <= 4; x++) {
    						Location blockLoc = all.getLocation().add(all.getVelocity().multiply(0.15 * x));
    						all.getWorld().playEffect(blockLoc, Effect.STEP_SOUND, blockLoc.getBlock().getTypeId(), 30);
    					}
    					
    				} else {
    					remove.add(all);
    				}
    			}
    			for(Projectile all : remove) {
    				GunData.bulletVel.remove(all);
    			}
    		}
    	}, 1L, 1L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(Guns.firePreview.containsKey(all)) {
						for(Location loc : Guns.firePreview.get(all)) {
							all.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
						}
						Guns.firePreview.remove(all);
					}
					
					if(ItemUtils.getLore(all.getItemInHand()) != null) {
						if(ItemUtils.loreContains(ItemUtils.getLore(all.getItemInHand()), "Flashlight Mod") && Guns.f.contains(all)) {
							Location target = all.getLocation().add(0, -1, 0);

							if(target.getBlock().getType().isSolid() && target.getBlock().getType().isOccluding()) {
								all.sendBlockChange(target, Material.GLOWSTONE, (byte) 5);
								ArrayList<Location> locs = new ArrayList<Location>();
								locs.add(target);
								Guns.firePreview.put(all, locs);
								
							} else {
								target = target.add(0, -1, 0);
								
								if(target.getBlock().getType().isSolid() && target.getBlock().getType().isOccluding()) {
									all.sendBlockChange(target, Material.GLOWSTONE, (byte) 5);
									ArrayList<Location> locs = new ArrayList<Location>();
									locs.add(target);
									Guns.firePreview.put(all, locs);
								}
							}
						}
					}
				}
			}
		}, 5L, 5L);
		
		//mobs
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Region all : Mobs.regions) {
					all.reloadAliveMobs();
				}
			}
		}, 20 * 60 * 8, 20 * 60 * 8);
			
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(World world : Bukkit.getWorlds()) {
					for(LivingEntity all : world.getLivingEntities()) {
						if(all.getType() == EntityType.COW) {
							for(Entity inrange : all.getNearbyEntities(1, 1, 1)) {
								if(inrange instanceof Player) {
									if(((Player) inrange).getGameMode() != GameMode.CREATIVE && !((Player) inrange).hasPotionEffect(PotionEffectType.INVISIBILITY)) {
										((Player) inrange).damage(2d);
									}
								}
							}
							
						} else if(all.getType() == EntityType.CHICKEN || all.getType() == EntityType.PIG) {
							for(Entity inrange : all.getNearbyEntities(5, 5, 5)) {
								if(inrange instanceof Player) {
									Location loc = new Location(all.getWorld(), all.getLocation().getX(), 0, all.getLocation().getZ());
									
									if(all.getLocation().getBlockX() > inrange.getLocation().getBlockX()) {
										loc.setX(loc.getX() + 6);
										
									} else if(all.getLocation().getBlockX() < inrange.getLocation().getBlockX()) {
										loc.setX(loc.getX() - 6);
									}
									if(all.getLocation().getBlockZ() > inrange.getLocation().getBlockZ()) {
										loc.setZ(loc.getZ() + 6);
										
									} else if(all.getLocation().getBlockZ() < inrange.getLocation().getBlockZ()) {
										loc.setZ(loc.getZ() - 6);
									}
									
									loc.setY(loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockY()));
									MobUtils.walkTo(all, loc, 1.5d);
								}
							}
						}
					}
				}
			}
		}, 6L, 6L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
				
				for(Mob mob : Mobs.getAllMobs()) {
					LivingEntity all = mob.entity;
					
					if(Bukkit.getWorld("map").getLivingEntities().contains(all)) {
						if(all.getCustomName() != null && all.getCustomName().contains("Radiated")) {
							all.getWorld().playEffect(all.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
						}
						
					} else {
						remove.add(all);
					}
				}
				
				for(LivingEntity all : remove) {
					if(Mobs.getRegion(all) != null) {
						Mobs.getRegion(all).getMob(all).despawn();
					}
				}
				
				for(Mob all : Mobs.getAllMobs()) {
					if(!Mobs.mobIsAlive(all.entity) && !all.isRespawning) {
						all.delaySpawn(all);
					}
				}
				
				MobUtils.updateMobTargets();
				
				for(LivingEntity all : Bukkit.getWorld("map").getLivingEntities()) {
					if((!(all instanceof Player) && !(all instanceof Projectile)) && Mobs.getRegion(all) == null && !all.getType().equals(EntityType.ARMOR_STAND)) {
						all.remove();
					}
				}
			}
		}, 3L, 3L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				ArrayList<Location> removeCrates = new ArrayList<Location>();

				for (Location all : Mobs.crateTimeout.keySet()) {
					if (((Integer) Mobs.crateTimeout.get(all)).intValue() - 1 == 0) {
						removeCrates.add(all);
						
					} else {
						Mobs.crateTimeout.put(all, Integer.valueOf(((Integer) Mobs.crateTimeout.get(all)).intValue() - 1));
					}
				}

				for (Location all : removeCrates) {
					Mobs.crates.remove(all);
					Mobs.crateTimeout.remove(all);
					all.getBlock().setType(Material.AIR);
					all.getBlock().setData((byte) 0);
				}
				
				int alive = 0;
				for(Mob all : Mobs.getAllMobs()) {
					if(!Mobs.mobIsAlive(all.entity)) {
					} else {
						alive++;
					}
				}
				
				int respawning = 0;
				for(Mob all : Mobs.getAllMobs()) {
					if(all.isRespawning) {
						respawning++;
					}
				}
				
				//Bukkit.broadcastMessage("Mobs alive: " + alive + " Mobs respawning: " + respawning + " Mobs Created: " + Mobs.getAllMobs().size());
			}
		}, 20L, 20L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				ArrayList<Location> remove = new ArrayList<Location>();
				for(Location all : Mobs.crates.keySet()) {
					if(ItemUtils.getContentAmount(Mobs.crates.get(all)) == 0) {
						remove.add(all);
					}
				}
				
				for(Location all : remove) {
					all.getWorld().playEffect(all, Effect.STEP_SOUND, all.getBlock().getTypeId());
					all.getBlock().setType(Material.AIR);
					Mobs.crates.remove(all);
				}
			}
		}, 5L, 5L);
	}
}
