package com.swingsword.ssengine.game.games.rust.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_10_R1.EntityInsentient;

public class MobUtils {
	
	public static boolean canSpawn = false;
			
	public static void updateMobTargets() {
		for(World world : Bukkit.getWorlds()) {
			for(LivingEntity le : world.getLivingEntities()) {
				if(le.getType() == EntityType.COW && le.getCustomName() != null && le.getCustomName().contains("Bear")) {
					if(getPlayerTarget(le) != null) {
						walkTo(le, getPlayerTarget(le).getLocation(), 1.3d);
						pull(le, getPlayerTarget(le).getLocation(), 0.05f);
					}
						
				} else if(le.getType() == EntityType.WOLF && le.getCustomName() != null && le.getCustomName().contains("Wolf")) {
					Wolf wolf = (Wolf) le;
						
					if(getPlayerTarget(le) != null) {
						wolf.setAngry(true);
						wolf.setTarget(getPlayerTarget(le));
						walkTo(wolf, getPlayerTarget(le).getLocation(), 1.6d);
						pull(le, getPlayerTarget(le).getLocation(), 0.05f);
					}
				}
			}
		}
	}
	
	public static Player getPlayerTarget(LivingEntity le) {
		for(Entity inrange : le.getNearbyEntities(15, 15, 15)) {
			if(inrange instanceof Player) {
				if(((Player) inrange).getGameMode() != GameMode.CREATIVE) {
					return (Player) inrange;
				}
			}
		}
		return null;
	}
	
	public static void walkTo(LivingEntity livingEntity, Location loc, double speed) {
    	((EntityInsentient) ((CraftLivingEntity) livingEntity).getHandle()).getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), speed);
    }
	
	public void knockback(Entity ent, Location damageLoc, float power) {
		power = power / 5;
		Vector vec = new Vector(ent.getLocation().getX() - damageLoc.getX(), ent.getLocation().getY() - damageLoc.getY(), ent.getLocation().getZ() - damageLoc.getZ());
		
		ent.setVelocity(ent.getVelocity().add(vec.multiply(power)));
	}
	
	public static void pull(Entity ent, Location damageLoc, float power) {
		power = power / 5;
		Vector vec = new Vector(damageLoc.getX() - ent.getLocation().getX(), damageLoc.getY() - ent.getLocation().getY(), damageLoc.getZ() - ent.getLocation().getZ());
		
		ent.setVelocity(ent.getVelocity().add(vec.multiply(power)));
	}
}
