package com.swingsword.ssengine.game.games.rust.mobs;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.utils.MobUtils;
import com.swingsword.ssengine.game.games.rust.utils.SimpleLocation;

public class Mob {
	
	public Region r;
	public LivingEntity entity;
	
	public boolean isRespawning;
	
	public Mob(Region r, LivingEntity ent) {
		this.entity = ent;
		
		this.r = r;
	}
	
	public void spawn() {
		if(entity != null) {
			entity.remove();
			entity = null;
		}
		
		entity = getRandomMob();
	}
	
	public void despawn() {
		if(entity != null) {
			entity.remove();
			entity = null;
		}
	}
	
	public void delaySpawn(final Mob mob) {
		isRespawning = true;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				mob.spawn();
			}
		}, 20 * 60 * 10);
	}
	
	@SuppressWarnings("deprecation")
	private LivingEntity getRandomMob() {
		MobUtils.canSpawn = true;
		
		String[] mobs = r.types.split(",");
		Random ra = new Random();
		int ran = ra.nextInt(mobs.length);
		String mob = mobs[ran];
		LivingEntity tempEnt = null;
		
		Location loc = SimpleLocation.getRandomLocation(r.region, Bukkit.getWorld("map"), 50);
		
		if(loc != null && loc.getChunk().isLoaded()) {
			if(mob.equalsIgnoreCase("chicken")) {
				tempEnt = (LivingEntity) Bukkit.getWorld("map").spawnEntity(loc, EntityType.CHICKEN);
				tempEnt.setCustomName("Chicken");
				tempEnt.setCustomNameVisible(true);
				tempEnt.setMaxHealth(5d);
				tempEnt.setHealth(5d);
				
			} else if(mob.equalsIgnoreCase("bear")) {
				tempEnt = (LivingEntity) Bukkit.getWorld("map").spawnEntity(loc, EntityType.COW);
				tempEnt.setCustomName("Bear");
				tempEnt.setCustomNameVisible(true);
				tempEnt.setMaxHealth(50d);
				tempEnt.setHealth(50d);
				
			} else if(mob.equalsIgnoreCase("rbear")) {
				tempEnt = (LivingEntity) Bukkit.getWorld("map").spawnEntity(loc, EntityType.COW);
				tempEnt.setCustomName("Radiated Bear");
				tempEnt.setCustomNameVisible(true);
				tempEnt.setMaxHealth(40d);
				tempEnt.setHealth(40d);
				
			} else if(mob.equalsIgnoreCase("pig")) {
				tempEnt = (LivingEntity) Bukkit.getWorld("map").spawnEntity(loc, EntityType.PIG);
				tempEnt.setCustomName("Pig");
				tempEnt.setCustomNameVisible(true);
				tempEnt.setMaxHealth(24d);
				tempEnt.setHealth(24d);
				tempEnt.setRemoveWhenFarAway(false);
				
			} else if(mob.equalsIgnoreCase("wolf")) {
				tempEnt = (LivingEntity) Bukkit.getWorld("map").spawnEntity(loc, EntityType.WOLF);
				tempEnt.setCustomName("Wolf");
				tempEnt.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 1));
				tempEnt.setCustomNameVisible(true);
				tempEnt.setMaxHealth(25d);
				tempEnt.setHealth(25d);
				
			} else if(mob.equalsIgnoreCase("rwolf")) {
				tempEnt = (LivingEntity) Bukkit.getWorld("map").spawnEntity(loc, EntityType.WOLF);
				tempEnt.setCustomName("Radiated Wolf");
				tempEnt.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 1));
				tempEnt.setCustomNameVisible(true);
				tempEnt.setMaxHealth(16d);
				tempEnt.setHealth(16d);
			}
			
			tempEnt.setRemoveWhenFarAway(false);
		}
		
		MobUtils.canSpawn = false;
		isRespawning = false;
		
		return tempEnt;
	}
}
