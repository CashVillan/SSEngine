package com.swingsword.ssengine.game.games.minestrike.game;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.minestrike.utils.ChatUtils;
import com.swingsword.ssengine.stats.StatManager;

public class DefuseManager {

	public static Player defuser;
	static int progress = 0;
	static int defuseTime = 0;
	
	public static void defuse(Player player) {
		int time = 10;
		if(player.getInventory().getItem(7) != null) {
			time = 5;
		}
		
		defuser = player;
		progress = 20 * time;
		defuseTime = time;
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			all.playSound(player.getLocation(), "guns.c4.disarm", 1, 1);
		}
	}
	
	public static void reset() {
		progress = 0;
		
		defuseTime = 0;
		defuser = null;
	}
	
	public static void progressDefuse() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(defuser != null) {
					if(defuser.isOnline() && defuser.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 6).getLocation().toVector().equals(CSGOGame.bombLoc.toVector()) && !DeathManager.dead.contains(defuser.getName())) {
						if(progress != 0) {
							progress -= 1;
						
							if((int) ((progress / 20) + 1) < 10) {
								ChatUtils.sendTitle(defuser, 0, 5, 0, " ", "Defuse Time: " + ChatColor.RED + "00:0" + (int) ((progress / 20) + 1));
							} else {
								ChatUtils.sendTitle(defuser, 0, 5, 0, " ", "Defuse Time: " + ChatColor.RED + "00:" + (int) ((progress / 20) + 1));
							}
							
							ChatUtils.sendActionBar(defuser, getProgressBar());
							
						} else {
							StatManager.addStat(defuser, "cs_defuses", 1);

							ChatUtils.sendActionBar(defuser, " ");
							CSGOGame.winTeam("CT", "DEFUSE");
						}
						
					} else {
						ChatUtils.sendActionBar(defuser, " ");
						reset();
					}
				}
				
				progressDefuse();
			}
		}, 1);
	}
	
	public static String getProgressBar() {
		String bar = ChatColor.DARK_RED + "";
		
		for(int x = 0; x < 20; x++) {
			if((float) ((float) progress / (float) (20 * defuseTime)) > (float) ((float) x / (float) 20)) {
				bar = bar + ChatColor.DARK_RED + "█";
			} else {
				bar = bar + ChatColor.DARK_GRAY + "█";
			}
		}
		
		return bar;
	}
}
