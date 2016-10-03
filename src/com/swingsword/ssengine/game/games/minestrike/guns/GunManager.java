package com.swingsword.ssengine.game.games.minestrike.guns;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import com.swingsword.ssengine.MasterPlugin;

public class GunManager implements Listener, CommandExecutor {
	
	public static GunManager plugin;
	
	public final GunListener gl = new GunListener();
	
	GunData data = new GunData();
	
	@SuppressWarnings({ "static-access", "deprecation" })
	public void loadGuns() {
		this.plugin = this;
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(gl, MasterPlugin.getMasterPlugin());
		
		gl.runDelayTask();
		gl.runZoomTask();
		gl.runLoopTask();
		
		//Pistols
		
		//CZ / barretas?
		
		Gun p2000 = new Gun("P2000", Material.getMaterial(2258), 0, 1, "CT");
		p2000.setType(GunType.SECONDARY);
		p2000.setMagazineSize(13);
		p2000.setSpawnAmmo(52);
		p2000.setBullets(1);
		p2000.setAccurracy(4);
		p2000.setKnockback(0);
		p2000.setRPM(600);
		p2000.setZoom(0, false);
		p2000.setReloadTime(2);
		p2000.setPrice(200);
		p2000.setKillReward(300);
		p2000.setSound("guns.p2000.fire");
		//
		p2000.setDamage(6.8);
		p2000.setHeadshotDamage(28);
		p2000.setLegDamage(5.2);
		p2000.setArmourDamage(3.4);
		p2000.setArmourHeadshotDamage(14);
		//
		p2000.load();
		
		Gun glock18 = new Gun("Glock-18", Material.getMaterial(2266), 1, 1, "T");
		glock18.setType(GunType.SECONDARY);
		glock18.setMagazineSize(20);
		glock18.setSpawnAmmo(120);
		glock18.setBullets(1);
		glock18.setAccurracy(4);
		glock18.setKnockback(0);
		glock18.setRPM(800);
		glock18.setZoom(0, false);
		glock18.setReloadTime(2);
		glock18.setPrice(400);
		glock18.setKillReward(300);
		glock18.setSound("guns.glock18.fire");
		//
		glock18.setDamage(4.8);
		glock18.setHeadshotDamage(19.6);
		glock18.setLegDamage(3.6);
		glock18.setArmourDamage(2.4);
		glock18.setArmourHeadshotDamage(10.2);
		//
		glock18.load();
		
		Gun p250 = new Gun("P250", Material.getMaterial(2259), 2, 2, "ALL");
		p250.setType(GunType.SECONDARY);
		p250.setMagazineSize(13);
		p250.setSpawnAmmo(26);
		p250.setBullets(1);
		p250.setAccurracy(4);
		p250.setKnockback(0);
		p250.setRPM(600);
		p250.setZoom(0, false);
		p250.setReloadTime(2);
		p250.setPrice(300);
		p250.setKillReward(300);
		p250.setSound("guns.p250.fire");
		//
		p250.setDamage(6.8);
		p250.setHeadshotDamage(27.6);
		p250.setLegDamage(5.2);
		p250.setArmourDamage(5.2);
		p250.setArmourHeadshotDamage(21.4);
		//
		p250.load();
		
		Gun tec9 = new Gun("Tec-9", Material.DIAMOND_HOE, 3, 3, "T");
		tec9.setType(GunType.SECONDARY);
		tec9.setMagazineSize(24);
		tec9.setSpawnAmmo(120);
		tec9.setBullets(1);
		tec9.setAccurracy(4);
		tec9.setKnockback(0);
		tec9.setRPM(700);
		tec9.setZoom(0, false);
		tec9.setReloadTime(2);
		tec9.setPrice(500);
		tec9.setKillReward(300);
		tec9.setSound("guns.tec9.fire");
		//
		tec9.setDamage(6.6);
		tec9.setHeadshotDamage(26.4);
		tec9.setLegDamage(4.8);
		tec9.setArmourDamage(5.8);
		tec9.setArmourHeadshotDamage(23.8);
		//
		tec9.load();
		
		Gun deagle = new Gun("Desert Eagle", Material.getMaterial(2256), 4, 4, "ALL");
		deagle.setType(GunType.SECONDARY);
		deagle.setMagazineSize(7);
		deagle.setSpawnAmmo(35);
		deagle.setBullets(1);
		deagle.setAccurracy(4);
		deagle.setKnockback(0);
		deagle.setRPM(400);
		deagle.setZoom(0, false);
		deagle.setReloadTime(2);
		deagle.setPrice(700);
		deagle.setKillReward(300);
		deagle.setSound("guns.deagle.fire");
		//
		deagle.setDamage(12.4);
		deagle.setHeadshotDamage(49.8);
		deagle.setLegDamage(9.2);
		deagle.setArmourDamage(11.6);
		deagle.setArmourHeadshotDamage(46.4);
		//
		deagle.load();
		
		Gun fiveseven = new Gun("Five-SeveN", Material.DIAMOND_HOE, 5, 3, "CT");
		fiveseven.setType(GunType.SECONDARY);
		fiveseven.setMagazineSize(20);
		fiveseven.setSpawnAmmo(100);
		fiveseven.setBullets(1);
		fiveseven.setAccurracy(4);
		fiveseven.setKnockback(0);
		fiveseven.setRPM(600);
		fiveseven.setZoom(0, false);
		fiveseven.setReloadTime(2);
		fiveseven.setPrice(500);
		fiveseven.setKillReward(300);
		fiveseven.setSound("guns.fiveseven.fire");
		//
		fiveseven.setDamage(6.2);
		fiveseven.setHeadshotDamage(25.2);
		fiveseven.setLegDamage(4.6);
		fiveseven.setArmourDamage(5.6);
		fiveseven.setArmourHeadshotDamage(25.2);
		//
		fiveseven.load();
		
		//Heavy
		
		Gun nova = new Gun("Nova", Material.DIAMOND_PICKAXE, 6, 10, "ALL");
		nova.setType(GunType.PRIMARY);
		nova.setMagazineSize(8);
		nova.setSpawnAmmo(32);
		nova.setBullets(9);
		nova.setAccurracy(4);
		nova.setKnockback(0);
		nova.setRPM(180);
		nova.setZoom(0, false);
		nova.setReloadTime(2);
		nova.setPrice(1200);
		nova.setKillReward(900);
		nova.setSound("guns.nova.fire");
		//
		nova.setDamage(5.2);
		nova.setHeadshotDamage(19.2);
		nova.setLegDamage(3.8);
		nova.setArmourDamage(2.6);
		nova.setArmourHeadshotDamage(10.6);
		//
		nova.load();
		
		//damages todo
		
		Gun xm1014 = new Gun("XM1014", Material.DIAMOND_AXE, 7, 11, "ALL");
		xm1014.setType(GunType.PRIMARY);
		xm1014.setMagazineSize(7);
		xm1014.setSpawnAmmo(32);
		xm1014.setBullets(9);
		xm1014.setAccurracy(4);
		xm1014.setKnockback(0);
		xm1014.setRPM(300);
		xm1014.setZoom(0, false);
		xm1014.setReloadTime(3);
		xm1014.setPrice(2000);
		xm1014.setKillReward(900);
		xm1014.setSound("guns.xm1014.fire");
		//
		xm1014.setDamage(4);
		xm1014.setHeadshotDamage(16);
		xm1014.setLegDamage(3);
		xm1014.setArmourDamage(3.2);
		xm1014.setArmourHeadshotDamage(12.8);
		//
		xm1014.load();
		
		Gun mag7 = new Gun("MAG-7", Material.GOLD_PICKAXE, 8, 12, "CT");
		mag7.setType(GunType.PRIMARY);
		mag7.setMagazineSize(5);
		mag7.setSpawnAmmo(32);
		mag7.setBullets(9);
		mag7.setAccurracy(4);
		mag7.setKnockback(0);
		mag7.setRPM(71);
		mag7.setZoom(0, false);
		mag7.setReloadTime(2);
		mag7.setPrice(1800);
		mag7.setKillReward(900);
		mag7.setSound("guns.mag7.fire");
		//
		mag7.setDamage(6);
		mag7.setHeadshotDamage(24);
		mag7.setLegDamage(4.4);
		mag7.setArmourDamage(4.4);
		mag7.setArmourHeadshotDamage(18);
		//
		mag7.load();
		
		Gun m249 = new Gun("M249", Material.getMaterial(2260), 9, 13, "ALL");
		m249.setType(GunType.PRIMARY);
		m249.setMagazineSize(13);
		m249.setSpawnAmmo(36);
		m249.setBullets(1);
		m249.setAccurracy(4);
		m249.setKnockback(0.6f);
		m249.setRPM(750);
		m249.setZoom(0, false);
		m249.setReloadTime(6);
		m249.setPrice(5200);
		m249.setKillReward(300);
		m249.setSound("guns.m249.fire");
		//
		m249.setDamage(6.4);
		m249.setHeadshotDamage(25.6);
		m249.setLegDamage(4.8);
		m249.setArmourDamage(5);
		m249.setArmourHeadshotDamage(20.4);
		//
		m249.load();
		
		Gun negev = new Gun("Negev", Material.STONE_HOE, 10, 14, "ALL");
		negev.setType(GunType.PRIMARY);
		negev.setMagazineSize(150);
		negev.setSpawnAmmo(200);
		negev.setBullets(1);
		negev.setAccurracy(12);
		negev.setKnockback(0.6f);
		negev.setRPM(700);
		negev.setZoom(0, false);
		negev.setReloadTime(6);
		negev.setPrice(5700);
		negev.setKillReward(300);
		negev.setSound("guns.negev.fire");
		//
		negev.setDamage(7);
		negev.setHeadshotDamage(20);
		negev.setLegDamage(5.2);
		negev.setArmourDamage(5.2);
		negev.setArmourHeadshotDamage(20);
		//
		negev.load();
		
		Gun sawedoff = new Gun("Sawed-Off", Material.IRON_AXE, 11, 12, "T");
		sawedoff.setType(GunType.PRIMARY);
		sawedoff.setMagazineSize(7);
		sawedoff.setSpawnAmmo(32);
		sawedoff.setBullets(9);
		sawedoff.setAccurracy(4);
		sawedoff.setKnockback(0);
		sawedoff.setRPM(71);
		sawedoff.setZoom(0, false);
		sawedoff.setReloadTime(3);
		sawedoff.setPrice(1200);
		sawedoff.setKillReward(900);
		sawedoff.setSound("guns.sawedoff.fire");
		//
		sawedoff.setDamage(6.4);
		sawedoff.setHeadshotDamage(20);
		sawedoff.setLegDamage(4.8);
		sawedoff.setArmourDamage(4.8);
		sawedoff.setArmourHeadshotDamage(19.2);
		//
		sawedoff.load();
		
		//MSG
		
		Gun mp9 = new Gun("MP9", Material.WOOD_AXE, 12, 19, "CT");
		mp9.setType(GunType.PRIMARY);
		mp9.setMagazineSize(30);
		mp9.setSpawnAmmo(120);
		mp9.setBullets(1);
		mp9.setAccurracy(4);
		mp9.setKnockback(0);
		mp9.setRPM(857);
		mp9.setZoom(0, false);
		mp9.setReloadTime(2);
		mp9.setPrice(1250);
		mp9.setKillReward(600);
		mp9.setSound("guns.mp9.fire");
		mp9.spaceModifier = 1.4f;
		//
		mp9.setDamage(5.2);
		mp9.setHeadshotDamage(20);
		mp9.setLegDamage(3.8);
		mp9.setArmourDamage(3);
		mp9.setArmourHeadshotDamage(12.2);
		//
		mp9.load();
		
		Gun mp7 = new Gun("MP7", Material.WOOD_HOE, 13, 20, "ALL");
		mp7.setType(GunType.PRIMARY);
		mp7.setMagazineSize(30);
		mp7.setSpawnAmmo(120);
		mp7.setBullets(1);
		mp7.setAccurracy(4);
		mp7.setKnockback(0);
		mp7.setRPM(800);
		mp7.setZoom(0, false);
		mp7.setReloadTime(3);
		mp7.setPrice(1700);
		mp7.setKillReward(600);
		mp7.setSound("guns.mp7.fire");
		mp9.spaceModifier = 2.2f;
		//
		mp7.setDamage(5.6);
		mp7.setHeadshotDamage(20);
		mp7.setLegDamage(4.2);
		mp7.setArmourDamage(3.4);
		mp7.setArmourHeadshotDamage(14.2);
		//
		mp7.load();
		
		Gun ump45 = new Gun("UMP-45", Material.IRON_HOE, 14, 21, "ALL");
		ump45.setType(GunType.PRIMARY);
		ump45.setMagazineSize(25);
		ump45.setSpawnAmmo(100);
		ump45.setBullets(1);
		ump45.setAccurracy(4);
		ump45.setKnockback(0);
		ump45.setRPM(750);
		ump45.setZoom(0, false);
		ump45.setReloadTime(4);
		ump45.setPrice(1200);
		ump45.setKillReward(600);
		ump45.setSound("guns.ump45.fire");
		//
		ump45.setDamage(7);
		ump45.setHeadshotDamage(20);
		ump45.setLegDamage(5);
		ump45.setArmourDamage(4.4);
		ump45.setArmourHeadshotDamage(18);
		//
		ump45.load();
		
		Gun p90 = new Gun("P90", Material.DIAMOND_SPADE, 15, 22, "ALL");
		p90.setType(GunType.PRIMARY);
		p90.setMagazineSize(64);
		p90.setSpawnAmmo(120);
		p90.setBullets(1);
		p90.setAccurracy(4);
		p90.setKnockback(0);
		p90.setRPM(857);
		p90.setZoom(0, false);
		p90.setReloadTime(3);
		p90.setPrice(2350);
		p90.setKillReward(300);
		p90.setSound("guns.p90.fire");
		p90.spaceModifier = 1.5f;
		//
		p90.setDamage(5);
		p90.setHeadshotDamage(20);
		p90.setLegDamage(3.8);
		p90.setArmourDamage(3.4);
		p90.setArmourHeadshotDamage(14.2);
		//
		p90.load();
		
		Gun bizon = new Gun("PP-Bizon", Material.GOLD_HOE, 16, 23, "ALL");
		bizon.setType(GunType.PRIMARY);
		bizon.setMagazineSize(64);
		bizon.setSpawnAmmo(120);
		bizon.setBullets(1);
		bizon.setAccurracy(4);
		bizon.setKnockback(0);
		bizon.setRPM(750);
		bizon.setZoom(0, false);
		bizon.setReloadTime(2);
		bizon.setPrice(1400);
		bizon.setKillReward(600);
		bizon.setSound("guns.bizon.fire");
		//
		bizon.setDamage(5.4);
		bizon.setHeadshotDamage(20);
		bizon.setLegDamage(4);
		bizon.setArmourDamage(3);
		bizon.setArmourHeadshotDamage(12.2);
		//
		bizon.load();
		
		Gun mac10 = new Gun("MAC-10", Material.IRON_PICKAXE, 17, 19, "T");
		mac10.setType(GunType.PRIMARY);
		mac10.setMagazineSize(30);
		mac10.setSpawnAmmo(100);
		mac10.setBullets(1);
		mac10.setAccurracy(4);
		mac10.setKnockback(0);
		mac10.setRPM(800);
		mac10.setZoom(0, false);
		mac10.setReloadTime(3);
		mac10.setPrice(1050);
		mac10.setKillReward(600);
		mac10.setSound("guns.mac10.fire");
		mp9.spaceModifier = 1.5f;
		//
		mac10.setDamage(5.6);
		mac10.setHeadshotDamage(20);
		mac10.setLegDamage(4.2);
		mac10.setArmourDamage(3.2);
		mac10.setArmourHeadshotDamage(13);
		//
		mac10.load();
		
		//Rifles
		
		Gun galil = new Gun("Galil AR", Material.IRON_SPADE, 18, 28, "T");
		galil.setType(GunType.PRIMARY);
		galil.setMagazineSize(35);
		galil.setSpawnAmmo(90);
		galil.setBullets(1);
		galil.setAccurracy(4);
		galil.setKnockback(0);
		galil.setRPM(666);
		galil.setZoom(0, false);
		galil.setReloadTime(3);
		galil.setPrice(2000);
		galil.setKillReward(300);
		galil.setSound("guns.galil.fire");
		//
		galil.setDamage(5.2);
		galil.setHeadshotDamage(20);
		galil.setLegDamage(4.4);
		galil.setArmourDamage(4.6);
		galil.setArmourHeadshotDamage(18.4);
		//
		galil.load();
		
		Gun ak47 = new Gun("AK-47", Material.STONE_AXE, 19, 29, "T");
		ak47.setType(GunType.PRIMARY);
		ak47.setMagazineSize(30);
		ak47.setSpawnAmmo(90);
		ak47.setBullets(1);
		ak47.setAccurracy(4);
		ak47.setKnockback(0);
		ak47.setRPM(600);
		ak47.setZoom(0, false);
		ak47.setReloadTime(3);
		ak47.setPrice(2700);
		ak47.setKillReward(300);
		ak47.setSound("guns.ak47.fire");
		ak47.spaceModifier = 2.5f;
		//
		ak47.setDamage(7);
		ak47.setHeadshotDamage(20);
		ak47.setLegDamage(5.2);
		ak47.setArmourDamage(5.4);
		ak47.setArmourHeadshotDamage(20);
		//
		ak47.load();
		
		Gun ssg08 = new Gun("SSG-08", Material.STONE_PICKAXE, 20, 30, "ALL");
		ssg08.setType(GunType.PRIMARY);
		ssg08.setMagazineSize(10);
		ssg08.setSpawnAmmo(90);
		ssg08.setBullets(1);
		ssg08.setAccurracy(0);
		ssg08.setKnockback(0);
		ssg08.setRPM(48);
		ssg08.setZoom(3, true);
		ssg08.setReloadTime(4);
		ssg08.setPrice(1700);
		ssg08.setKillReward(300);
		ssg08.setSound("guns.ssg08.fire");
		//
		ssg08.setDamage(17.6);
		ssg08.setHeadshotDamage(20);
		ssg08.setLegDamage(13.2);
		ssg08.setArmourDamage(14.8);
		ssg08.setArmourHeadshotDamage(20);
		//
		ssg08.load();
		
		Gun sg553 = new Gun("SG-553", Material.GOLD_SPADE, 21, 31, "T");
		sg553.setType(GunType.PRIMARY);
		sg553.setMagazineSize(30);
		sg553.setSpawnAmmo(90);
		sg553.setBullets(1);
		sg553.setAccurracy(0);
		sg553.setKnockback(0);
		sg553.setRPM(666);
		sg553.setZoom(3, true);
		sg553.setReloadTime(3);
		sg553.setPrice(3000);
		sg553.setKillReward(300);
		sg553.setSound("guns.sg553.fire");
		//
		sg553.setDamage(6);
		sg553.setHeadshotDamage(20);
		sg553.setLegDamage(4.4);
		sg553.setArmourDamage(6);
		sg553.setArmourHeadshotDamage(20);
		//
		sg553.load();
		
		Gun awp = new Gun("AWP", Material.getMaterial(2257), 22, 32, "ALL");
		awp.setType(GunType.PRIMARY);
		awp.setMagazineSize(10);
		awp.setSpawnAmmo(30);
		awp.setBullets(1);
		awp.setAccurracy(0);
		awp.setKnockback(0);
		awp.setRPM(41);
		awp.setZoom(10, true);
		awp.setReloadTime(4);
		awp.setPrice(4750);
		awp.setKillReward(100);
		awp.setSound("guns.awp.fire");
		//
		awp.setDamage(40);
		awp.setHeadshotDamage(40);
		awp.setLegDamage(17.2);
		awp.setArmourDamage(20);
		awp.setArmourHeadshotDamage(20);
		//
		awp.load();
		
		Gun g3sg1 = new Gun("G3SG1", Material.GOLD_AXE, 23, 33, "T");
		g3sg1.setType(GunType.PRIMARY);
		g3sg1.setMagazineSize(20);
		g3sg1.setSpawnAmmo(90);
		g3sg1.setBullets(1);
		g3sg1.setAccurracy(0);
		g3sg1.setKnockback(0);
		g3sg1.setRPM(240);
		g3sg1.setZoom(5, true);
		g3sg1.setReloadTime(5);
		g3sg1.setPrice(5000);
		g3sg1.setKillReward(300);
		g3sg1.setSound("guns.g3sg1.fire");
		//
		g3sg1.setDamage(15.8);
		g3sg1.setHeadshotDamage(20);
		g3sg1.setLegDamage(11.8);
		g3sg1.setArmourDamage(13);
		g3sg1.setArmourHeadshotDamage(20);
		//
		g3sg1.load();
		
		Gun famas = new Gun("FAMAS", Material.STONE_SPADE, 24, 28, "CT");
		famas.setType(GunType.PRIMARY);
		famas.setMagazineSize(25);
		famas.setSpawnAmmo(90);
		famas.setBullets(1);
		famas.setAccurracy(4);
		famas.setKnockback(0);
		famas.setRPM(666);
		famas.setZoom(0, false);
		famas.setReloadTime(3);
		famas.setPrice(2250);
		famas.setKillReward(300);
		famas.setSound("guns.famas.fire");
		//
		famas.setDamage(6);
		famas.setHeadshotDamage(20);
		famas.setLegDamage(4.4);
		famas.setArmourDamage(4.2);
		famas.setArmourHeadshotDamage(16.8);
		//
		famas.load();
		
		Gun m4a4 = new Gun("M4A4", Material.WOOD_PICKAXE, 25, 29, "CT");
		m4a4.setType(GunType.PRIMARY);
		m4a4.setMagazineSize(30);
		m4a4.setSpawnAmmo(90);
		m4a4.setBullets(1);
		m4a4.setAccurracy(4);
		m4a4.setKnockback(0);
		m4a4.setRPM(666);
		m4a4.setZoom(0, false);
		m4a4.setReloadTime(3);
		m4a4.setPrice(3100);
		m4a4.setKillReward(300);
		m4a4.setSound("guns.m4a4.fire");
		//
		m4a4.setDamage(6.4);
		m4a4.setHeadshotDamage(20);
		m4a4.setLegDamage(4.8);
		m4a4.setArmourDamage(4.6);
		m4a4.setArmourHeadshotDamage(18.4);
		//
		m4a4.load();
		
		Gun aug = new Gun("AUG", Material.getMaterial(2267), 26, 31, "CT");
		aug.setType(GunType.PRIMARY);
		aug.setMagazineSize(30);
		aug.setSpawnAmmo(90);
		aug.setBullets(1);
		aug.setAccurracy(4);
		aug.setKnockback(0);
		aug.setRPM(666);
		aug.setZoom(3, true);
		aug.setReloadTime(4);
		aug.setPrice(3300);
		aug.setKillReward(300);
		aug.setSound("guns.aug.fire");
		//
		aug.setDamage(5.6);
		aug.setHeadshotDamage(20);
		aug.setLegDamage(4.2);
		aug.setArmourDamage(5);
		aug.setArmourHeadshotDamage(20);
		//
		aug.load();
		
		Gun scar20 = new Gun("SCAR-20", Material.WOOD_SPADE, 27, 33, "CT");
		scar20.setType(GunType.PRIMARY);
		scar20.setMagazineSize(20);
		scar20.setSpawnAmmo(90);
		scar20.setBullets(1);
		scar20.setAccurracy(0);
		scar20.setKnockback(0);
		scar20.setRPM(240);
		scar20.setZoom(5, true);
		scar20.setReloadTime(3);
		scar20.setPrice(5000);
		scar20.setKillReward(300);
		scar20.setSound("guns.scar20.fire");
		//
		scar20.setDamage(15.8);
		scar20.setHeadshotDamage(20);
		scar20.setLegDamage(11.8);
		scar20.setArmourDamage(13);
		scar20.setArmourHeadshotDamage(20);
		//
		scar20.load();
		
		SkinnedGun m4a4howl = new SkinnedGun("M4A4", Material.FIREBALL, 101, "Howl");
		m4a4howl.copyData(m4a4);
		m4a4howl.load();
		
		SkinnedGun m4a4desertstrike = new SkinnedGun("M4A4", Material.ARROW, 102, "Desert Strike");
		m4a4desertstrike.copyData(m4a4);
		m4a4desertstrike.load();
	}
	
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		Player player = (Player) arg0;
		
		if(arg2.equalsIgnoreCase("guns")) {
			if(player.isOp()) {
				for(Gun all : GunData.guns) {
					player.getInventory().addItem(all.toItemStack(true, player.getName()));
				}
				
			} else {
				player.sendMessage(ChatColor.RED + "No permission.");
			}
		}
		
		return false;
	}
	
	public boolean loreContains(ItemStack item, String key) {
		if(item != null && item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
			List<String> lore = item.getItemMeta().getLore();
			
			for(String all : lore) {
				if(all.equalsIgnoreCase(key)) {
					return true;
				}
			}
		}
		
		return false;
	}
		
	public boolean loreContains(List<String> lore, String key) {
		for(String all : lore) {
			if(ChatColor.stripColor(all).equalsIgnoreCase(key)) {
				return true;
			}
		}
		
		return false;
	}
	
	public List<String> getLore(ItemStack item) {
		if(item != null && item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
			return item.getItemMeta().getLore();
		}
		return null;
	}
}
