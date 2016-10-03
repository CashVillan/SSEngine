package com.swingsword.ssengine.game.games.minestrike.guns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GunData {
	public static List<Gun> guns = new ArrayList<Gun>();
	public static List<Ammo> allAmmo = new ArrayList<Ammo>();
	public static HashMap<Projectile, Gun> bullets = new HashMap<Projectile, Gun>();
	public static HashMap<Projectile, Vector> bulletVel = new HashMap<Projectile, Vector>();
	public static HashMap<Player, Float> delay = new HashMap<Player, Float>();
	public static HashMap<Player, Gun> looping = new HashMap<Player, Gun>();
	public static HashMap<Player, Gun> zoomed = new HashMap<Player, Gun>();
	public static HashMap<Player, ItemStack> playerHelmet = new HashMap<Player, ItemStack>();
	public static HashMap<Player, BukkitRunnable> reloadTasks = new HashMap<Player, BukkitRunnable>();
	
	public static HashMap<Player, Integer> reloading = new HashMap<Player, Integer>();
	public static HashMap<Player, Gun> reloadGun = new HashMap<Player, Gun>();
	public static HashMap<Player, Integer> reloadGunAmmo = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> reloadTotalGunAmmo = new HashMap<Player, Integer>();

	public static boolean containsGun(Gun gun) {
		boolean contains = false;
		
		for(Gun all : guns) {
			if(all.getName().equals(gun.getName())) {
				contains = true;
			}
		}
		return contains;
	}
	
	public static void addGun(Gun gun) {
		for(Gun all : guns) {
			if(all.getName() == gun.getName()) {
				guns.remove(all);
			}
		}
		guns.add(gun);
	}
} 
