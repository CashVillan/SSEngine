package com.swingsword.ssengine.game.games.rust.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.building.Building;
import com.swingsword.ssengine.game.games.rust.building.Foundation;

public class C4Utils {
	
	public static ArrayList<Location> locs = new ArrayList<Location>();
	static HashMap<Location, Integer> propDamage = new HashMap<Location, Integer>();
	static ArrayList<Location> temp = new ArrayList<Location>();
	
	public static void primeTNT(final Location loc, final int time) {
		if(!locs.contains(loc)) {
			locs.add(loc);
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(time == 0) {
					blowTNT(loc);
					
				} else {
					loc.getWorld().playSound(loc, Sound.UI_BUTTON_CLICK, 1, 2);
					loc.getWorld().playEffect(loc.clone().add(0.5, 1, 0.5), Effect.SMOKE, 4);
					
					primeTNT(loc, time - 1);
				}
			}
		}, 20L);
	}
	
	@SuppressWarnings("deprecation")
	public static void blowTNT(Location loc) {
		loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		loc.getWorld().playEffect(loc, Effect.EXPLOSION_LARGE, 30);
		loc.getWorld().playEffect(loc, Effect.STEP_SOUND, 46);
		loc.getBlock().setType(Material.AIR);
		locs.remove(loc);
		
		Location[] locs = new Location[] { loc.getBlock().getRelative(1, 0, 0).getLocation(), loc.getBlock().getRelative(0, 0, 1).getLocation(), loc.getBlock().getRelative(-1, 0, 0).getLocation(), loc.getBlock().getRelative(0, 0, -1).getLocation() };

		for(Location relb : locs) {
			Location rel = relb.getBlock().getRelative(0, 0, 0).getLocation();
			
			if(getType(rel) != null) {
				damageProp(rel, getType(rel));
				
			} else if(rel.getBlock().getType() == Material.IRON_DOOR_BLOCK || rel.getBlock().getType() == Material.IRON_FENCE) {
				damageProp(rel, "");
			}
				
			if(rel.getBlock().getType() == Material.WOODEN_DOOR) {
				rel.getWorld().playEffect(rel, Effect.STEP_SOUND, rel.getBlock().getTypeId());
				rel.getBlock().setType(Material.AIR);
			}
		}
	}
	
	public static String getType(Location loc) {
		for(Foundation all : Building.fm.getFoundations()) {			
			if(all.getOldBlocks().containsKey(new SimpleLocation(loc).toString())) {
				return all.getOldBlocks().get(new SimpleLocation(loc).toString());
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static void damageProp(Location prop, String type) {
		if(prop != null && type != null || prop.getBlock().getType() == Material.IRON_DOOR_BLOCK || prop.getBlock().getType() == Material.IRON_FENCE)  {
			ArrayList<String> types = new ArrayList<String>( Arrays.asList("wall", "doorway", "window", "stair", "metalwall", "metalwall", "metaldoorway", "metalwindow", "metalstair") );
			
			if(types.contains(type.toLowerCase()) && prop.getBlock().getType() != Material.IRON_DOOR_BLOCK && prop.getBlock().getType() != Material.IRON_FENCE) {
				Location[] locs = new Location[] { prop.getBlock().getLocation(), prop.getBlock().getRelative(1, 0, 0).getLocation(), prop.getBlock().getRelative(0, 0, 1).getLocation(), prop.getBlock().getRelative(-1, 0, 0).getLocation(), prop.getBlock().getRelative(0, 0, -1).getLocation() };	
				
				for(Location relb : locs) {
					//for(int y = -1; y <= 1; y++) {
						Location all = relb.getBlock().getRelative(0, 0, 0).getLocation();
						
						if(getType(all) != null && getType(all).equals(type) && Building.fm.getPropFoundation(all) == Building.fm.getPropFoundation(prop)) {
							all.getWorld().playEffect(all, Effect.STEP_SOUND, all.getBlock().getTypeId());
							
							if(propDamage.containsKey(all)) {
								if(propDamage.get(all) - 1 <= 0) {
									breakProp(all, type);
									
									propDamage.remove(all);
									
								} else {
									propDamage.put(all, propDamage.get(all) - 1);
								}
								
							} else {
								int damage = getDamage(type);
								
								if(damage - 1 > 0) {
									propDamage.put(all, damage - 1);
								} else {
									breakProp(all, type);
								}
							}
						}
					//}
				}
				
			} else if(prop.getBlock().getType() == Material.IRON_DOOR_BLOCK || prop.getBlock().getType() == Material.IRON_FENCE) {
				prop.getWorld().playEffect(prop, Effect.STEP_SOUND, prop.getBlock().getTypeId());
				
				if(propDamage.containsKey(prop)) {
					if(propDamage.get(prop) - 1 <= 0) {
						breakProp(prop, type);
						
						propDamage.remove(prop);
						
					} else {
						propDamage.put(prop, propDamage.get(prop) - 1);
					}
					
				} else {
					int damage = 2;
					
					if(damage - 1 > 0) {
						propDamage.put(prop, damage - 1);
					} else {
						breakProp(prop, "");
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void breakProp(Location loc, String type) {
		if(loc != null)  {
			Location[] locs = new Location[] { loc.getBlock().getLocation(), loc.getBlock().getRelative(1, 0, 0).getLocation(), loc.getBlock().getRelative(0, 0, 1).getLocation(), loc.getBlock().getRelative(-1, 0, 0).getLocation(), loc.getBlock().getRelative(0, 0, -1).getLocation() };	
			
			if(loc.getBlock().getType() != Material.IRON_FENCE) {
				for(Location relb : locs) {
					for(int y = 1; y >= -1; y--) {
						Location all = relb.getBlock().getRelative(0, y, 0).getLocation();
						
						if(getType(all) != null && (getType(all).equals(type) || (getType(all).toLowerCase().contains("ceiling") && all.getBlock().getType().name().contains("WOOD"))) && all.getBlock().getType() != Material.IRON_FENCE) {
							all.getWorld().playEffect(all, Effect.STEP_SOUND, all.getBlock().getTypeId());
							Material oldType = all.getBlock().getType();
							
							if(all.getBlock().getRelative(0, -1, 0).getType().name().contains("DOOR")) {
								all.getBlock().getRelative(0, -1, 0).setTypeIdAndData(0, (byte) 0 , false);
								all.getBlock().setTypeIdAndData(0, (byte) 0 , false);

							} else if(all.getBlock().getRelative(0, 1, 0).getType().name().contains("DOOR")) {
								all.getBlock().setTypeIdAndData(0, (byte) 0 , false);
								all.getBlock().getRelative(0, 1, 0).setTypeIdAndData(0, (byte) 0 , false);
								
							} else {
								all.getBlock().setType(Material.AIR);
							}
							
							propDamage.remove(all);
							temp.remove(all);
							
							if(LocationUtils.nextToMaterialHorizontalWithType(all.getBlock().getLocation(), Material.WOOD_STEP, "Ceiling") || LocationUtils.nextToMaterialHorizontalWithType(all.getBlock().getLocation(), Material.getMaterial(44), "Ceiling") || (all.getBlock().getRelative(0, 1, 0).getType() != Material.AIR && y == 2)) {
								if(oldType.name().contains("WOOD")) {
									all.getBlock().setTypeIdAndData(Material.WOOD_STEP.getId(), (byte) 8, true);
									
								} else if(oldType == Material.IRON_BLOCK) {
									all.getBlock().setTypeIdAndData(44, (byte) 15, true);	
								}
								
							} else if(getType(all.getBlock().getRelative(0, -1, 0).getLocation()) != null && getType(all.getBlock().getRelative(0, -1, 0).getLocation()).toLowerCase().contains("pillar")) {
								if(all.getBlock().getRelative(0, -1, 0).getType() == Material.LOG) {
									all.getBlock().setTypeIdAndData(Material.LOG.getId(), (byte) 0, true);
									
								} else if(all.getBlock().getRelative(0, -1, 0).getType() == Material.QUARTZ_BLOCK) {
									all.getBlock().setTypeIdAndData(Material.QUARTZ_BLOCK.getId(), (byte) 0, true);
								}
							}
							
							if(all.getBlock().getType() != Material.WOOD_STEP && all.getBlock().getType() != Material.getMaterial(44)) {
								Building.fm.getPropFoundation(all).getOldBlocks().remove(new SimpleLocation(all).toString());
								Building.fm.getPropFoundation(all).getPropLocations().remove(new SimpleLocation(all).toString());
							}
							
						} else if(all.getBlock().getType() == Material.IRON_FENCE || all.getBlock().getType() == Material.IRON_DOOR_BLOCK) {
							all.getWorld().playEffect(all, Effect.STEP_SOUND, all.getBlock().getTypeId());
							
							if(all.getBlock().getRelative(0, -1, 0).getType().name().contains("DOOR")) {
								all.getBlock().getRelative(0, -1, 0).breakNaturally(null);
							} else {
								all.getBlock().breakNaturally(null);
							}
						}
					}
				}
				
			} else {
				loc.getWorld().playEffect(loc, Effect.STEP_SOUND, loc.getBlock().getTypeId());
				
				if(loc.getBlock().getRelative(0, -1, 0).getType().name().contains("DOOR")) {
					loc.getBlock().getRelative(0, -1, 0).breakNaturally(null);
				} else {
					loc.getBlock().breakNaturally(null);
				}
			}
		}
	}
	
	public static int getDamage(String type) {
		if(type.equalsIgnoreCase("wall")) {
			return 2;
			
		} else if(type.equalsIgnoreCase("doorway")) {
			return 2;
			
		} else if(type.equalsIgnoreCase("window")) {
			return 2;
			
		} else if(type.equalsIgnoreCase("metalwall")) {
			return 4;
			
		} else if(type.equalsIgnoreCase("metaldoorway")) {
			return 4;
			
		} else if(type.equalsIgnoreCase("metalwindow")) {
			return 4;
			
		}
		return 999;
	}
	
	public static Foundation getNearFoundation(Location loc) {
		for(int x = -1; x <= 1; x++) {
			for(int z = -1; z <= 1; z++) {
				Foundation f = Building.fm.getPropFoundation(loc.clone().getBlock().getRelative(x, 0, z).getLocation());
				
				if(f != null) {
					return f;
				}
			}
		}
		return null;
	}
	
	public static int getC4Y(Location floc, int c4h) {
		return floc.getBlockY() + ((((c4h - (floc.getBlockY() + 1)) / 3) + 1) * 3) - 1;
	}
}
