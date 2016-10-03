package com.swingsword.ssengine.game.games.minestrike.game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.minestrike.utils.NadeUtils;
import com.swingsword.ssengine.game.games.minestrike.utils.VectorUtils;

public class BounceManager {

	public BounceManager() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(World world : Bukkit.getWorlds()) {
					for(Entity ent : world.getEntities()) {
						if(ent instanceof Item && CustomItems.isNade(((Item) ent).getItemStack())) {
							Material type = ((Item) ent).getItemStack().getType();
							
							//VectorUtils.applySlowGravity(ent, 1.1f);
							
							BlockFace face = VectorUtils.getBounceFace(ent);
							if(face != null) {
								ent.setVelocity(VectorUtils.getBounceVector(ent, face));
								
								if(ent.getVelocity().length() > 0.1f) {
									for(Player all : Bukkit.getOnlinePlayers()) {
										if(all.getLocation().distance(ent.getLocation()) < 15) {
											if(type == Material.FIREWORK_CHARGE || type == Material.BRICK) {
												all.playSound(ent.getLocation(), "guns.grenade.bounce", 1, 1);
											} else if(type == Material.COAL || type == Material.FLINT) {
												all.playSound(ent.getLocation(), "guns.sg_grenade.bounce", 1, 1);
											}
										
										}
									}
								} else {
									String nadeType = NadeUtils.getType(type);
									
									if(!ent.isDead()) {
										if(nadeType.equals("he")) {
											NadeUtils.explode(ent.getLocation());
										}
										
										if(nadeType.equals("flash")) {
											for(Player all : Bukkit.getOnlinePlayers()) {
												all.playSound(ent.getLocation(), "guns.flashbang.explode", 1, 1);
												
												if(NadeUtils.inSight(all, ent)) {
													all.removePotionEffect(PotionEffectType.BLINDNESS);
													all.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 75, 1));
												}
											}
										}
										
										if(nadeType.equals("smoke")) {
											NadeUtils.smoke(ent.getLocation(), 0);
										}
										
										if(nadeType.equals("fire")) {
											NadeUtils.fire(ent.getLocation(), 0);
										}
										
										ent.remove();
									}
								}
							}
						}
					}
				}
			}
		}, 1, 1);
	}
	
}
