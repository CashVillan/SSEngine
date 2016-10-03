package com.swingsword.ssengine.game.games.minestrike.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.games.minestrike.game.DeathManager;
import com.swingsword.ssengine.game.games.minestrike.utils.ParticleEffect.ParticleType;
import com.swingsword.ssengine.game.threads.TimerHandler;

public class NadeUtils {

	public static void throwNade(Player player, final String nadeType) {
		if (!(TimerHandler.getTimer("buytime") != null && TimerHandler.getTimer("buytime").getLeft() >= 5 && CSGOGame.hasStarted())) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.playSound(player.getLocation(), "guns.pinpull", 1, 1);
			}
			
			//final ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().clone().add(0, 0.5f, 0), EntityType.ARMOR_STAND);
			//stand.setGravity(true);
			//stand.setVisible(false);
			//stand.setBasePlate(false);
			//stand.setItemInHand(new ItemStack(Material.WOOL, 1, (byte) 15));
			//stand.setRightArmPose(new EulerAngle(0, 0, 0));
			
			final Item stand = player.getWorld().dropItem(player.getEyeLocation().clone().add(0, 1, 0), new ItemStack(getMaterial(nadeType)));
			
			Vector v = player.getLocation().getDirection().clone().multiply(2);
			
			stand.setVelocity(player.getVelocity().add(v));
			
			player.setItemInHand(null);
		}
	}
	
	public static Material getMaterial(String nadeType) {
		if(nadeType.equals("he")) {
			return Material.FIREWORK_CHARGE;
		}
		if(nadeType.equals("flash")) {
			return Material.FLINT;
		}
		if(nadeType.equals("smoke")) {
			return Material.COAL;
		}
		if(nadeType.equals("fire")) {
			return Material.BRICK;
		}
		return null;
	}
	
	public static String getType(Material nadeType) {
		if(nadeType.equals(Material.FIREWORK_CHARGE)) {
			return "he";
		}
		if(nadeType.equals(Material.FLINT)) {
			return "flash";
		}
		if(nadeType.equals(Material.COAL)) {
			return "smoke";
		}
		if(nadeType.equals(Material.BRICK)) {
			return "fire";
		}
		return null;
	}
	
	public static void explode(Location loc) {
		for(int x = 0; x < 10; x++) {
			loc.getWorld().playEffect(loc, Effect.LAVA_POP, 30);
		}
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			all.playSound(loc, "guns.grenade.explode", 1, 1);
			
			if(!DeathManager.dead.contains(all.getName())) {
				if(all.getLocation().distance(loc) < 8) {
					all.damage(13 / all.getLocation().distance(loc) + 1);
				}
			}
		}
		
		ParticleEffect effect = new ParticleEffect(ParticleType.SMOKE_NORMAL, 0.01f, 1, 0.35f);
		for(int x = 0; x <= 10; x++) {
			for(int y = 0; y <= 10; y++) {
				float xMod = (float) Math.sin((float) ((float) x / (float) 10) * (float) 2 * (float) Math.PI) * (float) ((float) ((float) 7 / (float) (y + 2)));
				float zMod = (float) Math.cos((float) ((float) x / (float) 10) * (float) 2 * (float) Math.PI) * (float) ((float) ((float) 7 / (float) (y + 2)));
			
				effect.sendToLocation(loc.clone().add(xMod, y / 2, zMod));
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void smoke(final Location loc, final int progress) {
		if(progress == 0) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.playSound(loc, "guns.sg_grenade.explode", 1, 1);
			}
		}
		
		if(progress < 20) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				for(int x = 0; x < progress; x++) {
					all.playEffect(loc.clone().add(-2 + new Random().nextInt(4), new Random().nextInt(3), -2 + new Random().nextInt(4)), Effect.EXPLOSION_LARGE, 30);
				}
			}
			
		} else if(progress < 27) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.playEffect(loc.clone(), Effect.EXPLOSION_HUGE, 30);
			}
			
		} else {
			for(Player all : Bukkit.getOnlinePlayers()) {
				for(int x = 0; x < 4; x++) {
					all.playEffect(loc.clone(), Effect.EXPLOSION_HUGE, 30);
				}
			}
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(progress < 120) {
					smoke(loc, progress + 1);
				}
			}
		}, 3);
	}
	
	public static void fire(final Location loc, final int progress) {
		if(progress == 0) {
			for(Player all : Bukkit.getOnlinePlayers()) {
				//all.playSound(loc, "guns.sg_grenade.explode", 1, 1); play molly sound
			}
		}
		
		int radius = progress;
		if(radius > 12) {
			radius = 12;
		}
		
		for(int x = -(radius / 4); x <= (radius / 4); x++) {
			for(int z = -(radius / 4); z <= (radius / 4); z++) {
				if(loc.clone().add(x, 0, z).distance(loc) <= 3) {
					if(loc.clone().add(x, 0, z).getBlock().getType() == Material.AIR) {
						loc.clone().add(x, 0, z).getBlock().setType(Material.FIRE);
					}
				}
			}
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(progress < 100) {
					fire(loc, progress + 1);
					
				} else {
					for(int x = -3; x <= 3; x++) {
						for(int z = -3; z <= 3; z++) {
							if(loc.clone().add(x, 0, z).getBlock().getType() == Material.FIRE) {
								loc.clone().add(x, 0, z).getBlock().setType(Material.AIR);
							}
						}
					}
				}
			}
		}, 3);
	}
	
	public static boolean inSight(Player player, Entity ent) {
		for(Block block : player.getLineOfSight(new HashSet<Material>(Arrays.asList(Material.AIR)), 12)) {
			int modifier = (int) (player.getLocation().distance(block.getLocation()) / 2) + 2;
			
			for(int x = -modifier; x <= modifier; x++) {
				for(int y = -modifier; y <= modifier; y++) {
					for(int z = -modifier; z <= modifier; z++) {
						if(ent.getLocation().getBlock().getLocation().toVector().equals(block.getLocation().clone().add(x, y, z).toVector())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
