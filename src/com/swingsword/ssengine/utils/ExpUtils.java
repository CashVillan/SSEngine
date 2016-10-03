
package com.swingsword.ssengine.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.player.PlayerSessionManager;

public class ExpUtils {

	public static int expNeeded(int level) {
		return expData.get(level);
	}
	
	public static int getLevel(int exp) {
		int level = 0;
		
		while(expNeeded(level) <= exp) {
			//exp = exp - expNeeded(level);
			
			level = level + 1;
		}
		
		return level - 1;
	}
	
	public static int getCurrentExp(int exp) {
		int level = 0;
		
		while(expNeeded(level) <= exp) {
			level = level + 1;
		}
		
		return exp;
	}
	
	public static void startAnimateExpBar(final Player player, final int exp) {
		if(player.getLevel() == 0 && player.getExp() == 0 || exp < expNeeded(player.getLevel())) {
			player.setLevel(getLevel(exp));
			player.setExp((float) ((float) getCurrentExp(exp) / (float) expNeeded(getLevel(exp) + 1)));
			
		} else {
			animateExpBar(player, exp - expNeeded(player.getLevel()));
		}
	}
	
	public static void animateExpBar(final Player player, final double exp) {
		float speed = 1;
		final double percent = (float) ((float) getCurrentExp((int) exp) / (float) expNeeded(getLevel((int) exp) + 1));
		
		if(percent - player.getExp() < 0.2 && player.getLevel() == getLevel(PlayerSessionManager.getSession(player).getAccount().getExp())) {
			speed = (float) ((float)  10 - (50 * (percent - player.getExp())));
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				double finalExp = exp;
				
				if(exp > 0) {
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 2);
					
					player.setExp((float) (player.getExp() + 0.01));
					
					finalExp = exp;
					
					if(player.getExp() >= 1 && player.getLevel() < getLevel(PlayerSessionManager.getSession(player).getAccount().getExp())) {
						player.setLevel(player.getLevel() + 1);
						player.setExp(0);
						
						Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.GREEN + "" + ChatColor.BOLD + " leveled up to level " + player.getLevel() + "!");
						
						player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1, 0);
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
						
						for(int x = 0; x < 10; x++) {
							player.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 30);
						}
						player.playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
					}
					
					if(player.getExp() < percent || player.getLevel() != getLevel(PlayerSessionManager.getSession(player).getAccount().getExp())) {
						animateExpBar(player, finalExp);
					}
				}
			}
		}, (long) speed);
	}
	
	@SuppressWarnings("serial")
	private static HashMap<Integer, Integer> expData = new HashMap<Integer, Integer>() {{
		put(0, 0);
		put(1, 800);
		put(2, 2100);
		put(3, 3800);
		put(4, 6100);
		put(5, 9500);
		put(6, 12500);
		put(7, 16000);
		put(8, 19800);
		put(9, 24000);
		put(10, 28500);
	}};
}
