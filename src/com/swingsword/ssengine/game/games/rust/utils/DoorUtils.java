package com.swingsword.ssengine.game.games.rust.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.Door;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.utils.ConfigUtils;

public class DoorUtils {

	public static HashMap<String, List<Location>> doorPreview = new HashMap<String, List<Location>>();
	
	public static void loadDoors() {
		FileConfiguration doorsConfig = ConfigUtils.getConfig("data/doors");
		if(!doorsConfig.contains("doors")) {
			doorsConfig.createSection("doors");
			ConfigUtils.saveConfig(doorsConfig, "data/doors");
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				List<String> remove = new ArrayList<String>();
				for(String all : doorPreview.keySet()) {
					if(!Bukkit.getOfflinePlayer(all).isOnline()) {
						remove.add(all);
					}
				}
				for(String all : Rust.placeLoc.keySet()) {
					if(!Bukkit.getOfflinePlayer(all).isOnline()) {
						remove.add(all);
					}
				}
				for(String all : remove) {
					doorPreview.remove(all);
					Rust.placeLoc.remove(all);
				}
				
				for(Player all : Bukkit.getOnlinePlayers()) {
					Player player = all;
					
					if(player.isSneaking() && (LocationUtils.nextToMaterial(player.getLocation(), Material.IRON_DOOR_BLOCK) || LocationUtils.nextToMaterial(player.getLocation(), Material.WOODEN_DOOR))) {
						Location doorLoc = LocationUtils.nextToMaterial(player.getLocation(), Material.IRON_DOOR_BLOCK) ? LocationUtils.nextToMaterialLoc(player.getLocation(), Material.IRON_DOOR_BLOCK) : LocationUtils.nextToMaterialLoc(player.getLocation(), Material.WOODEN_DOOR);
						
						if(!((Door) doorLoc.getBlock().getState().getData()).isOpen()) {
							Location loc = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
							loc.setYaw(player.getLocation().getYaw() + 180);
							player.teleport(loc);
							
							if(player.isOnGround()) {
								player.setVelocity(new Vector(-player.getLocation().getDirection().getX(), 0.1, -player.getLocation().getDirection().getBlockZ()));
							} else {
								player.setVelocity(new Vector(-player.getLocation().getDirection().getX() * 0.2, 0, -player.getLocation().getDirection().getBlockZ() * 0.2));
							}
							
							player.sendMessage(ChatColor.RED + "You got pushed back to prevent block glitching.");
						}
					}
					
					if(doorPreview.containsKey(all.getName())) {
						for(Location loc : doorPreview.get(all.getName())) {
							all.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
						}
						doorPreview.remove(all.getName());
					}
					
					if(all.getItemInHand().getType() == Material.IRON_DOOR || all.getItemInHand().getType() == Material.WOOD_DOOR) {
						if(all.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5) != null) {
							if(all.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getType().isSolid()) {
								Location target = all.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone();
								
								if((target.getBlock().getType() == Material.WOOD || target.getBlock().getType() == Material.IRON_BLOCK || target.getBlock().getType() == Material.WOOD_STEP || target.getBlock().getType() == Material.STEP) && (target.clone().add(0, 3, 0).getBlock().getType() == Material.WOOD || target.clone().add(0, 3, 0).getBlock().getType() == Material.IRON_BLOCK)) {
									all.sendBlockChange(target.clone().add(0, 2, 0), Material.STAINED_GLASS, (byte) 5);
									ArrayList<Location> locs = new ArrayList<Location>();
									locs.add(target.clone().add(0, 2, 0));
									locs.add(target.clone().add(0, 1, 0));
									doorPreview.put(all.getName(), locs);
									Rust.placeLoc.put(all.getName(), new SimpleLocation(target));
									
								} else {
									all.sendBlockChange(target.clone().add(0, 2, 0), Material.STAINED_GLASS, (byte) 14);
									ArrayList<Location> locs = new ArrayList<Location>();
									locs.add(target.clone().add(0, 2, 0));
									locs.add(target.clone().add(0, 1, 0));
									doorPreview.put(all.getName(), locs);
								}
							}
						}
						
					} else if(all.getItemInHand().getType() == Material.IRON_FENCE) {
						if(all.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5) != null) {
							if(all.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getType().isSolid()) {
								Location target = all.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone();
								
								if((target.getBlock().getType() == Material.WOOD_STEP || target.getBlock().getType().getId() == 44) && (target.clone().add(0, 1, 0).getBlock().getType() == Material.AIR) && (target.clone().add(0, 2, 0).getBlock().getType() == Material.WOOD_STEP || target.clone().add(0, 2, 0).getBlock().getType().getId() == 44)) {
									all.sendBlockChange(target.clone().add(0, 1, 0), Material.STAINED_GLASS, (byte) 5);
									ArrayList<Location> locs = new ArrayList<Location>();
									locs.add(target.clone().add(0, 2, 0));
									locs.add(target.clone().add(0, 1, 0));
									doorPreview.put(all.getName(), locs);
									Rust.placeLoc.put(all.getName(), new SimpleLocation(target));
									
								} else {
									all.sendBlockChange(target.clone().add(0, 2, 0), Material.STAINED_GLASS, (byte) 14);
									ArrayList<Location> locs = new ArrayList<Location>();
									locs.add(target.clone().add(0, 2, 0));
									locs.add(target.clone().add(0, 1, 0));
									doorPreview.put(all.getName(), locs);
								}
							}
						}
					}
				}
			}
		}, 5L, 5L);
	}
}
