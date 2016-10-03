package com.swingsword.ssengine.game.games.rust.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.guns.GunData;
import com.swingsword.ssengine.game.games.rust.ores.Ores;
import com.swingsword.ssengine.game.games.rust.utils.ArmorUtils;
import com.swingsword.ssengine.game.games.rust.utils.PropUtils;
import com.swingsword.ssengine.game.games.rust.utils.RadiationUtils;
import com.swingsword.ssengine.stats.StatManager;
import com.swingsword.ssengine.utils.StringUtils;

public class DamageListener implements Listener {
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		Rust.getInstance();
		if (Rust.placeLoc.containsKey(player.getName())) {
			Rust.getInstance();
			Rust.placeLoc.remove(player.getName());
		}

		if (player.getGameMode() != GameMode.CREATIVE) {
			if (!event.getFrom().getBlock().getRelative(0, -1, 0).getType().isSolid() && event.getTo().getBlock().getRelative(0, -1, 0).getType().isSolid()) {
				if (player.getFallDistance() >= 6.5f) {
					Random r = new Random();

					if (player.getFallDistance() > 28) {
						StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Fell to your death!");
						player.damage(player.getHealth());

						return;
					}

					if (r.nextInt(4) < 3) {
						player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BIG_FALL, 1, 1);
						player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);

						StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Broke a leg!");
						player.damage(6.6d);
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 55 * 20, 3));
						player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 55 * 20, -10));
					}

					Random r2 = new Random();

					if (r2.nextInt(4) < 3) {
						Rust.getInstance();
						if (!Rust.bleeding.contains(player.getName())) {
							player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BIG_FALL, 1, 1);
							player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);

							Rust.getInstance();
							Rust.bleeding.add(player.getName());
							StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Bleeding! Use a medkit or bandage!");
						}
					}
				}
			}
		}

		if (RadiationUtils.isInRadZone(event.getTo()) && !RadiationUtils.isInRadZone(event.getFrom())) {
			StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Entering Radiation Zone!");
		} else if (RadiationUtils.isInRadZone(event.getFrom()) && !RadiationUtils.isInRadZone(event.getTo())) {
			StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Leaving Radiation Zone!");
		}

		if ((event.getTo().getBlock().getType() == Material.STATIONARY_WATER || event.getTo().getBlock().getType() == Material.WATER) && !player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			player.damage(player.getHealth());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		Player damager = null;
		Projectile proj = null;
		
		if (event.getCause().equals(DamageCause.FALL)) {
			event.setCancelled(true);
			return;
		}
		
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event;
			if (entityEvent.getDamager() instanceof Player) {
				damager = (Player) entityEvent.getDamager();
			}
			if (entityEvent.getDamager() instanceof Projectile) {
				proj = (Projectile) entityEvent.getDamager();
				if (proj.getShooter() instanceof Player) {
					damager = (Player) proj.getShooter();
				}
			}
		}

		if (damager != null) {
			if (Ores.cooldown.contains(damager)) {
				event.setCancelled(true);
				return;
			}
			if (event.getCause() == DamageCause.PROJECTILE) {
				if (GunData.bullets.containsKey(proj)) {
					if (entity instanceof Player && proj.getLocation().getY() - entity.getLocation().getY() >= 1.8d) {
						damager.playSound(damager.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
						event.setDamage(GunData.bullets.get(proj).getHeadshotDamage());

						StatManager.addStat(damager, "rt_headshots", 1);
					} else {
						damager.playSound(damager.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
						event.setDamage(GunData.bullets.get(proj).getDamage());
					}
				}
			} else if (event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
				Block targetBlock = damager.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 10);
				if(targetBlock.getLocation().clone().add(0.5f, 0.5f, 0.5f).distance(damager.getEyeLocation()) < damager.getEyeLocation().distance(entity.getLocation())) {
					event.setCancelled(true);
					return;
				}
				
				if (damager.getItemInHand().getType().name().toLowerCase().contains("pickaxe")) {
					event.setDamage((double) 7.0);
				} else if (damager.getItemInHand().getType().name().toLowerCase().contains("iron_axe")) {
					event.setDamage((double) 5.0);
				} else if (damager.getItemInHand().getType().name().toLowerCase().contains("stone_axe")) {
					event.setDamage((double) 3.0);
				} else if (damager.getItemInHand().getType().name().toLowerCase().contains("wood_axe")) {
					event.setDamage((double) 4.0);
				} else {
					damager.sendMessage(ChatColor.RED + "Please use a tool to PvP!");
					event.setCancelled(true);
					return;
				}
			}
		}
		
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (PropUtils.healing.contains(player)) {
				PropUtils.healing.remove(player);

				StringUtils.sendTitle(player, 5, 40, 5, ChatColor.YELLOW + "ALERT", ChatColor.RED + "Healing interrupted!");
			}
			event.setDamage(event.getDamage() * ArmorUtils.getTotalProtection(player));
		}
		entity.setVelocity(new Vector(0, 0, 0));
	}
}
