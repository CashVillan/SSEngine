package com.swingsword.ssengine.game.games.rust.listeners;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.guns.Gun;
import com.swingsword.ssengine.game.games.rust.utils.ArmorUtils;
import com.swingsword.ssengine.game.games.rust.utils.BackpackUtils;
import com.swingsword.ssengine.game.games.rust.utils.CookingUtils;
import com.swingsword.ssengine.game.games.rust.utils.EffectUtils;
import com.swingsword.ssengine.game.games.rust.utils.PropUtils;
import com.swingsword.ssengine.game.games.rust.utils.RadiationUtils;
import com.swingsword.ssengine.game.games.rust.utils.SimpleLocation;
import com.swingsword.ssengine.game.games.rust.utils.SpawnUtils;
import com.swingsword.ssengine.stats.StatManager;

import net.techcable.npclib.NPCLib;

public class PlayerListener implements Listener {
	
	public PlayerListener() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!player.getWorld().getName().equals("map")) {
						SpawnUtils.respawnPlayer(player, SpawnUtils.getRandomSpawn());
					}
				}
			}
		}, 5, 5);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		
		player.setCanPickupItems(true);
		player.removePotionEffect(PotionEffectType.WATER_BREATHING);
		player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 999999, 0));
		for(Player all : Bukkit.getOnlinePlayers()) {
			player.showPlayer(all);
		}
		
		if (!RadiationUtils.playerRad.containsKey(player.getName())) {
			RadiationUtils.playerRad.put(player.getName(), 0);
		}
		if (!PropUtils.playerFood.containsKey(player.getName())) {
			PropUtils.playerFood.put(player.getName(), 1500);
		}
		
		Location loc = player.getLocation();
		
		while(loc.getBlock().getRelative(0, -1, 0).getType().equals(Material.AIR) && loc.getBlockY() > 0) {
			loc.setY(loc.getBlockY() - 1);
		}
		
		if(ArmorUtils.wearsArmour(player)) {
			Gun.playSound(player, "custom.armour", 50);
		}
		
		player.teleport(loc);
		
	}

	@SuppressWarnings("static-access")
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		Rust.plugin.firePreview.remove(player.getName());
		Rust.plugin.placeLoc.remove(player.getName());
		Rust.plugin.playerFlare.remove(player.getName());
		Rust.plugin.bleeding.remove(player.getName());
		Rust.plugin.clickDelay.remove(player.getName());
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		event.setRespawnLocation(player.getLocation());
		
		SpawnUtils.processDeath(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(player.isOnline()) {
					SpawnUtils.processDeath(player);
				}
			}
		}, 5);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Entity finalentity = event.getEntity();
		new BukkitRunnable() {
			public void run() {
				try {
					Object nmsPlayer = finalentity.getClass().getMethod("getHandle").invoke(finalentity);
					Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);

					Class<?> EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");

					Field mcServer = con.getClass().getDeclaredField("minecraftServer");
					mcServer.setAccessible(true);
					Object mcserver = mcServer.get(con);

					Object playerList = mcserver.getClass().getDeclaredMethod("getPlayerList").invoke(mcserver);
					Method moveToWorld = playerList.getClass().getMethod("moveToWorld", EntityPlayer, int.class, boolean.class);
					moveToWorld.invoke(playerList, nmsPlayer, 0, false);
				} catch (Exception e) { }
			}
		}.runTask(MasterPlugin.getMasterPlugin());

		Player player = event.getEntity();

		if (SpawnUtils.oldDeaths.contains(player)) {
			SpawnUtils.oldDeaths.remove(player);
		}

		event.setDeathMessage(null);
		event.getDrops().clear();

		if (player.getInventory().getHelmet() != null) {
			player.getLocation().getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getHelmet());
		}
		if (player.getInventory().getChestplate() != null) {
			player.getLocation().getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getChestplate());
		}
		if (player.getInventory().getLeggings() != null) {
			player.getLocation().getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getLeggings());
		}
		if (player.getInventory().getBoots() != null) {
			player.getLocation().getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getBoots());
		}

		BackpackUtils.createBackpack(player.getLocation().getBlock().getLocation(), player.getInventory(), player.getName());
		player.getInventory().clear();
		
		if (player.getKiller() != null) {
			StatManager.addStat(player.getKiller(), "rt_kills", 1);
		}

		StatManager.addStat(player, "rt_deaths", 1);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			if(SpawnUtils.dead.contains(player.getName())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() != null) {
			if (NPCLib.getNPCRegistry(MasterPlugin.getMasterPlugin()).isNPC(event.getTarget())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			// Player player = (Player) event.getEntity();

			event.setCancelled(true);

			/*
			 * if(playerFood.get(player.getName()) > 500) {
			 * event.setCancelled(false); //}
			 * 
			 * if(bleeding.contains(player)) { event.setCancelled(true); }
			 */
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			event.setCancelled(true);
			player.setFoodLevel(18);
		}
	}

	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		if (event.getItemDrop().getItemStack().getType() == Material.TORCH) {
			if (event.getPlayer().getOpenInventory().getCursor().getAmount() == 0) {
				event.getItemDrop().getItemStack().setAmount(event.getPlayer().getItemInHand().getAmount() + 1);
				event.getPlayer().setItemInHand(null);
			}
		}
	}
	
	@EventHandler
	public void onWeatherChange(final WeatherChangeEvent event) {

		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if (event.getWorld().hasStorm()) {
					event.getWorld().setStorm(false);
				}
				if (event.getWorld().isThundering()) {
					event.getWorld().setThundering(false);
				}
			}
		}, 1l);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		final Player player = event.getPlayer();

		if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				@SuppressWarnings("static-access")
				public void run() {
					if (player.isOnline()) {
						if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
							PropUtils.playerFood.put(player.getName(), 1500);
							RadiationUtils.playerRad.put(player.getName(), 0);
							Rust.plugin.bleeding.remove(player.getName());
						}
					}
				}
			}, 1l);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		final Player player = event.getPlayer();

		if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			event.setCancelled(true);
		}
	}
	
	@SuppressWarnings({ "deprecation", "static-access" })
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		boolean interacted = false;

		if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			event.setCancelled(false);
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Block block = event.getClickedBlock();

			/*if(block.getType() == Material.SPONGE) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						player.sendBlockChange(block.getLocation(), 0, (byte) 0);
					}
				}, 1);
			}*/
			
			if (block.getType().name().contains("DOOR")) {
				interacted = true;
			}

			if (block.getLocation().getBlock().getType() == Material.STEP && block.getRelative(0, -1, 0).getType().isSolid()) {
				if (CookingUtils.fireInventory.containsKey(new SimpleLocation(block.getLocation()).toString())) {
					player.openInventory(CookingUtils.fireInventory.get(new SimpleLocation(block.getLocation()).toString()));
					CookingUtils.invDelay.put(CookingUtils.fireInventory.get(new SimpleLocation(block.getLocation()).toString()), 1440);
					interacted = true;
				}

			} else if (block.getLocation().getBlock().getType() == Material.ANVIL) {
				if (CookingUtils.furnaceInventory.containsKey(new SimpleLocation(event.getClickedBlock().getLocation()).toString())) {
					player.openInventory(CookingUtils.furnaceInventory.get(new SimpleLocation(event.getClickedBlock().getLocation()).toString()));
					interacted = true;
				}

			} else if (block.getLocation().getBlock().getType() == Material.CHEST) {
				interacted = true;
			}

			if (interacted == true) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
			}
		}

		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.CHEST) || event.getAction() == Action.RIGHT_CLICK_AIR) && !interacted) {
			if (player.getItemInHand() != null && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null) {
				if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Small Medkit")) {
					if (!PropUtils.healing.contains(player)) {
						event.setCancelled(true);

						player.sendMessage(ChatColor.GREEN + "You started healing yourself.");

						if (Rust.plugin.bleeding.contains(player.getName())) {
							Rust.plugin.bleeding.remove(player.getName());
							player.sendMessage(ChatColor.GREEN + "You stopped the bleeding!");
						}

						if (player.getHealth() < player.getMaxHealth()) {
							if (player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);

							} else {
								player.setItemInHand(null);
							}
							PropUtils.healing.add(player);
							PropUtils.healPlayer(player, 2);

						} else {
							player.sendMessage(ChatColor.RED + "You are already fully healed.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "You are already healing yourself.");
					}

				} else if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Large Medkit")) {
					if (!PropUtils.healing.contains(player)) {
						event.setCancelled(true);

						player.sendMessage(ChatColor.GREEN + "You started healing yourself.");

						if (Rust.plugin.bleeding.contains(player.getName())) {
							Rust.plugin.bleeding.remove(player.getName());
							player.sendMessage(ChatColor.GREEN + "You stopped the bleeding!");
						}

						if (player.getHealth() < player.getMaxHealth()) {
							if (player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);

							} else {
								player.setItemInHand(null);
							}
							PropUtils.healing.add(player);
							PropUtils.healPlayer(player, 10);

						} else {
							player.sendMessage(ChatColor.RED + "You are already fully healed.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "You are already healing yourself.");
					}

				} else if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Bandage")) {
					event.setCancelled(true);

					if (Rust.plugin.bleeding.contains(player.getName())) {
						if (player.getItemInHand().getAmount() > 1) {
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);

						} else {
							player.setItemInHand(null);
						}
						player.sendMessage(ChatColor.GREEN + "You stopped the bleeding!");
						Rust.plugin.bleeding.remove(player.getName());

					} else {
						player.sendMessage(ChatColor.RED + "You are not bleeding.");
					}

				} else if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Blood Draw Kit")) {
					event.setCancelled(true);
					player.closeInventory();

					if (player.getHealth() - 5 > 0) {
						player.setHealth(player.getHealth() - 5d);
						//Main.getInstance().updateScoreboard(player);

						ItemStack blood = new ItemStack(Material.getMaterial(372), 3);
						ItemMeta bloodm = blood.getItemMeta();
						bloodm.setDisplayName(ChatColor.WHITE + "Blood");
						blood.setItemMeta(bloodm);
						
						player.getInventory().addItem(blood);
						player.updateInventory();

						player.sendMessage(ChatColor.GREEN + "You collected your own blood.");

					} else {
						player.sendMessage(ChatColor.RED + "You would kill yourself by taking blood.");
					}

				} else if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Anti-Radiation Pills")) {
					event.setCancelled(true);

					if (RadiationUtils.playerRad.get(player.getName()) > 0) {
						if (player.getItemInHand().getAmount() > 1) {
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);

						} else {
							player.setItemInHand(null);
						}
						player.sendMessage(ChatColor.GREEN + "You took the radiation pills.");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);

						if (RadiationUtils.playerRad.get(player.getName()) > 200) {
							RadiationUtils.playerRad.put(player.getName(), RadiationUtils.playerRad.get(player.getName()) - 200);
						} else {
							RadiationUtils.playerRad.put(player.getName(), 0);
						}

					} else {
						player.sendMessage(ChatColor.RED + "You are not radiated.");
					}
				}
			}

			if (player.getItemInHand() != null) {
				if (player.getItemInHand().getType() == Material.APPLE) {
					event.setCancelled(true);

					PropUtils.eat(player, 200);

				} else if (player.getItemInHand().getType() == Material.BREAD) {
					event.setCancelled(true);

					PropUtils.eat(player, 150);

				} else if (player.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
					event.setCancelled(true);

					PropUtils.eat(player, 300);

				} else if (player.getItemInHand().getType() == Material.COOKED_CHICKEN) {
					event.setCancelled(true);

					PropUtils.eat(player, 500);

				} else if (player.getItemInHand().getType() == Material.RAW_CHICKEN) {
					event.setCancelled(true);

					PropUtils.eat(player, 80);

					if (player.getHealth() - 2 > 0) {
						player.setHealth(player.getHealth() - 2);
					} else {
						player.damage(player.getHealth());
					}

					player.sendMessage(ChatColor.RED + "Raw food is bad for you!");

				} else if (player.getItemInHand().getType() == Material.EMPTY_MAP) {
					event.setCancelled(true);
					player.updateInventory();
				}
			}
		}

		if (!interacted && !event.isCancelled()) {
			if (event.getAction().name().contains("RIGHT")) {
				if (player.getItemInHand() != null) {
					if (player.getItemInHand().getType() == Material.BLAZE_ROD) {
						event.setCancelled(true);

						if (player.getItemInHand().getAmount() > 1) {
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);

						} else {
							player.setItemInHand(null);
						}

						final Item item = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.BLAZE_ROD));
						item.setVelocity(player.getEyeLocation().getDirection());
						item.setPickupDelay(9001);

						EffectUtils.playFlareEffect(item.getLocation(), 90);

						Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
							public void run() {
								if (item != null) {
									item.remove();

									for (Player all : Bukkit.getOnlinePlayers()) {
										if (all.getLocation().distance(item.getLocation()) < 30) {
											for (int x = -1; x <= 1; x++) {
												for (int z = -1; z <= 1; z++) {
													all.sendBlockChange(item.getLocation().getBlock().getRelative(x, -1, z).getLocation(), item.getLocation().getBlock().getRelative(x, -1, z).getType(), item.getLocation().getBlock().getRelative(x, -1, z).getData());
												}
											}
										}
									}
								}
							}
						}, 900l);
					}
				}

			} else if (event.getAction().name().contains("LEFT")) {
				if (player.getItemInHand() != null) {
					if (player.getItemInHand().getType() == Material.BLAZE_ROD) {
						event.setCancelled(true);

						Rust.plugin.playerFlare.put(player.getName(), player.getInventory().getHeldItemSlot());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if(event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();
			
			if(SpawnUtils.dead.contains(player.getName())) {
				if(SpawnUtils.getRandomSpawn() != null) {
					SpawnUtils.respawnPlayer(player, SpawnUtils.getRandomSpawn());
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			
			if(event.getInventory().getTitle().equals("Select Option")) {
				event.setCancelled(true);
				
				if(event.getCurrentItem().getTypeId() != 0) {
					if(event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("at")) {
						if(SpawnUtils.getRandomHome(player) != null) {
							final Location home = SpawnUtils.getRandomHome(player).clone().add(0.5, 0, 0.5);
							
							SpawnUtils.respawnPlayer(player, home);
							
							SpawnUtils.homeDelay.add(home);
							Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									SpawnUtils.homeDelay.remove(home);
								}
							}, 3 * 60 * 20);
							
						}
					} else if(event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("respawn")) {
						if(SpawnUtils.getRandomSpawn() != null) {
							SpawnUtils.respawnPlayer(player, SpawnUtils.getRandomSpawn());
						}
					}
				}
			}
		}
	}
}
