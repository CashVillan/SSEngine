package com.swingsword.ssengine.game.games.hub.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;

public class ParticleManager {

	int ticksPassed = 0;
	
	public ParticleManager() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("static-access")
			public void run() {
				ticksPassed++;
				
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(player.getName().equals("Markcreator")) {
						float length = 60;
						
						float stage = ticksPassed % length;
						double x = 0;
						double y = stage / 17f;
						double z = 0;

						if(stage == 0) {
							player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 0.5f, 1);
						}
						if(stage == length - 1) {
							player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_BLAST, 0.5f, 1);
							
							for(int i = 0; i < 20; i++) {
								ParticleEffect.CLOUD.display(new Vector().getRandom().add(new Vector(-0.5, -0.5, -0.5)), 0.25f, player.getLocation().clone().add(x, y, z), 30);
							}
							
						} else {
							x = Math.sin(Math.PI * (float) 4 * ((stage / length) + 1)) * ((length - stage) / length) * 1.5f;
							z = Math.cos(Math.PI * (float) 4 * ((stage / length) + 1)) * ((length - stage) / length) * 1.5f;
							
							ParticleEffect.FIREWORKS_SPARK.display(new Vector(), 0f, player.getLocation().clone().add(x, y, z), 30);
						}
					}
				}
			}
		}, 1, 1);
	}
	
}
