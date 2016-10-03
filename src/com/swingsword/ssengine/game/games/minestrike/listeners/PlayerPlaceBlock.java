package com.swingsword.ssengine.game.games.minestrike.listeners;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.games.minestrike.game.CustomItems;
import com.swingsword.ssengine.game.games.minestrike.game.DeathManager;
import com.swingsword.ssengine.game.games.minestrike.game.RewardManager;
import com.swingsword.ssengine.game.games.minestrike.utils.ChatUtils;
import com.swingsword.ssengine.game.games.minestrike.zones.BombSite;
import com.swingsword.ssengine.game.team.Team;
import com.swingsword.ssengine.game.threads.TimerHandler;
import com.swingsword.ssengine.stats.StatManager;

public class PlayerPlaceBlock implements Listener {

	public static int bombTime = 45; 
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = (Player) event.getPlayer();
		
		event.setCancelled(true);
		if(player.getLocation().clone().add(0, -1.5, 0).getBlock().getType() == Material.AIR && event.getBlockPlaced().equals(player.getLocation().getBlock().getRelative(0, -1, 0))) {
			player.teleport(player.getLocation().getBlock().getLocation().add(0.5, -1, 0.5));
		}
		
		if(player.getItemInHand().getType() == Material.TNT) {
			for(BombSite site : CSGOGame.sites) {
				if(site.r.contains(event.getBlockPlaced().getLocation())) {
					plant(player, event.getBlockAgainst().getLocation().toVector(), event.getBlockPlaced().getLocation());
					
				}
			}
		}
		
		if(player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
			event.setCancelled(false);
		}
	}
	
	//Plant
	
	public static void bombSoundLoop() {
		if(TimerHandler.getTimer("bombtime") != null) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					for(Player all : Bukkit.getOnlinePlayers()) {
						all.playSound(all.getLocation(), "guns.c4.beep", 1, 1);
					}
					
					bombSoundLoop();
				}
			}, (long) ((float) (((float) TimerHandler.getTimer("bombtime").getLeft() + 5) / (float) bombTime) * (float) 20));
		}
	}
	
	public static void plant(final Player player, final Vector loc, final Location placeLoc) {
		if(placeLoc.getBlock().getRelative(0, -1, 0).getType() != Material.AIR) {
			player.getInventory().setItem(7, null);
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(Team.getTeam(player) != null && Team.getTeam(all).getName().equals("T")) {
					all.sendMessage(ChatColor.GOLD + player.getName() + " (RADIO): I'm planting the bomb.");
				}
			}
			
			TimerHandler.createTimer("plantBomb", 4 * 20, 1, new Runnable() {
				public void run() {				
					if(player.isOnline() && player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 6).getLocation().toVector().equals(loc)) {
						ChatUtils.sendActionBar(player, getProgressBar());
						
					} else {
						TimerHandler.getTimer("plantBomb").cancel();
						ChatUtils.sendActionBar(player, " ");
						player.getInventory().setItem(7, CustomItems.bomb);
					}
				}
			}, null, new Runnable() {
				public void run() {
					ChatUtils.sendActionBar(player, " ");
					
					placeLoc.getBlock().setType(Material.TNT);
					CSGOGame.bombLoc = placeLoc;
					player.getInventory().setItem(7, CustomItems.bombTracker);
					
					StatManager.addStat(player, "cs_plants", 1);

					for(Player all : Bukkit.getOnlinePlayers()) {
						player.playSound(all.getLocation(), "game.plant", 1, 1);
						ChatUtils.sendTitle(all, 5, 20, 5, ChatColor.RED + "Alert", "The bomb has been planted. 45 seconds to detonation.");
					}
					
					for(Player t : Team.getTeam("T").getPlayers()) {
						RewardManager.reward(t, 800, "planting the bomb");
					}
					
					if(TimerHandler.getTimer("roundtime") != null) {
						TimerHandler.getTimer("roundtime").cancel();
					}
					
					TimerHandler.createTimer("bombtime", bombTime, 20, null, null, new Runnable() {
						public void run() {
							for(int x = -1; x <= 1; x++) {
								for(int y = 0; y <= 1; y++) {
									for(int z = -1; z <= 1; z++) {
										CSGOGame.bombLoc.getWorld().playEffect(CSGOGame.bombLoc.clone().add(x * 3, y * 3, z * 3), Effect.MOBSPAWNER_FLAMES, 1);
										CSGOGame.bombLoc.getWorld().playEffect(CSGOGame.bombLoc.clone().add(x * 5, y * 5, z * 5), Effect.EXPLOSION_HUGE, 1);
									}
								}
							}
							
							for(Player all : Bukkit.getOnlinePlayers()) {
								for(float x = 0; x <= 2; x += 0.25f) {
									all.playSound(CSGOGame.bombLoc, Sound.ENTITY_GENERIC_EXPLODE, 1, x);
								}
								
								if(!DeathManager.dead.contains(all.getName()) && all.getLocation().distance(CSGOGame.bombLoc) < 35) {
									double val = all.getLocation().distance(CSGOGame.bombLoc);
									double damage = 25 - Math.pow(val * 0.05f, 3f);
									
									if(damage > 0) {
										if(all.getHealth() <= damage) {
											DeathManager.die(all);
										} else {
											all.damage(damage);
										}
									}
								}
							}
							
							CSGOGame.winTeam("T", "EXPLODE");
						}
					});
					bombSoundLoop();
				}
			});
		}
	}
	
	public static String getProgressBar() {
		String bar = ChatColor.DARK_RED + "";
		
		for(int x = 20; x > 0; x--) {
			if((float) ((float) TimerHandler.getTimer("plantBomb").getLeft() / (float) (20 * 4)) > (float) ((float) x / (float) 20)) {
				bar = bar + ChatColor.DARK_GRAY + "█";
			} else {
				bar = bar + ChatColor.DARK_RED + "█";
			}
		}
		
		return bar;
	}
}
