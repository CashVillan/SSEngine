package com.swingsword.ssengine.game.games.rust.guns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.utils.ArmorUtils;

public class Guns {
	
	public static HashMap<Player, Inventory> playerEquip = new HashMap<Player, Inventory>();
	public static HashMap<Player, List<Location>> firePreview = new HashMap<Player, List<Location>>();
	public static ArrayList<Player> f = new ArrayList<Player>();
	
	static HashMap<Player, Integer> playerArmour = new HashMap<Player, Integer>();
	
	public static void loadGuns() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(!playerArmour.containsKey(all)) {
						playerArmour.put(all, ArmorUtils.getArmour(all));
					}
					if(playerArmour.get(all) != ArmorUtils.getArmour(all)) {
						Gun.playSound(all, "custom.armour", 10);
						playerArmour.put(all, ArmorUtils.getArmour(all));
					}
				}
			}
		}, 1L, 1L);
		
		Gun p250 = new Gun("P250", Material.WOOD_HOE);
		p250.setDamage(6);
		p250.setHeadshotDamage(16);
		p250.setMagazineSize(8);
		p250.setAmmoName("9mm Ammo");
		p250.setBullets(1);
		p250.setAccurracy(4);
		p250.setKnockback(0);
		p250.setRPM(250);
		p250.setZoom(2, false);
		p250.setReloadTime(2);
		p250.setSound("custom.p250");
		p250.load();
		
		Gun MP5 = new Gun("MP5A4", Material.GOLD_HOE);
		MP5.setDamage(5);
		MP5.setHeadshotDamage(12);
		MP5.setMagazineSize(27);
		MP5.setAmmoName("9mm Ammo");
		MP5.setBullets(1);
		MP5.setAccurracy(4);
		MP5.setKnockback(0);
		MP5.setRPM(400);
		MP5.setZoom(2, false);
		MP5.setReloadTime(5);
		MP5.setSound("custom.mp5");
		MP5.load();
		
		Gun ninemm = new Gun("9mm Pistol", Material.WOOD_HOE);
		ninemm.setDamage(5);
		ninemm.setHeadshotDamage(11);
		ninemm.setMagazineSize(8);
		ninemm.setAmmoName("9mm Ammo");
		ninemm.setBullets(1);
		ninemm.setAccurracy(4);
		ninemm.setKnockback(0);
		ninemm.setRPM(250);
		ninemm.setZoom(2, false);
		ninemm.setReloadTime(2);
		ninemm.setSound("custom.9mm");
		ninemm.load();
	
		Gun m4 = new Gun("M4", Material.IRON_HOE);
		m4.setDamage(7);
		m4.setHeadshotDamage(17);
		m4.setMagazineSize(31);
		m4.setAmmoName("556 Ammo");
		m4.setBullets(1);
		m4.setAccurracy(4);
		m4.setKnockback(0);
		m4.setRPM(1200);
		m4.setZoom(3, false);
		m4.setReloadTime(3);
		m4.setSound("custom.m4");
		m4.load();
		
		Gun ba = new Gun("Bolt Action Rifle", Material.DIAMOND_HOE);
		ba.setDamage(17);
		ba.setHeadshotDamage(24);
		ba.setMagazineSize(3);
		ba.setAmmoName("556 Ammo");
		ba.setBullets(1);
		ba.setAccurracy(5);
		ba.setKnockback(0);
		ba.setRPM(30);
		ba.setZoom(5, false);
		ba.setReloadTime(4);
		ba.setSound("custom.boltaction");
		ba.load();
		
		Gun shotgun = new Gun("Shotgun", Material.STONE_HOE);
		shotgun.setDamage(16);
		shotgun.setHeadshotDamage(shotgun.getDamage());
		shotgun.setMagazineSize(12);
		shotgun.setAmmoName("Shotgun Shells");
		shotgun.setBullets(10);
		shotgun.setAccurracy(20);
		shotgun.setKnockback(0);
		shotgun.setRPM(50);
		shotgun.setZoom(2, false);
		shotgun.setReloadTime(3);
		shotgun.setSound("custom.shotgun");
		shotgun.load();
		
		Gun wow = new Gun("Wow", Material.DIAMOND_SPADE);
		wow.setDamage(16);
		wow.setHeadshotDamage(shotgun.getDamage());
		wow.setMagazineSize(64);
		wow.setAmmoName("Shotgun Shells");
		wow.setBullets(10);
		wow.setAccurracy(10);
		wow.setKnockback(0);
		wow.setRPM(300);
		wow.setZoom(2, false);
		wow.setReloadTime(3);
		wow.setSound("custom.shotgun");
		wow.load();
		
		new Ammo("9mm Ammo", Material.GOLDEN_CARROT, AmmoType.CLIP(), null);
		new Ammo("556 Ammo", Material.GHAST_TEAR, AmmoType.CLIP(), null);
		new Ammo("Shotgun Shells", Material.REDSTONE, AmmoType.SHELL(), null);
	}
}
