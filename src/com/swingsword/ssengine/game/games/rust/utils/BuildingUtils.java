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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.building.Building;
import com.swingsword.ssengine.game.games.rust.ores.Ores;

public class BuildingUtils {

	public static List<String> playerDelay = new ArrayList<String>();
	
	@SuppressWarnings("deprecation")
	public static void updatePreview(final Player player, final ItemStack heldItem, boolean forceUpdate) {		
		if(!playerDelay.contains(player.getName()) || forceUpdate) {
			if(!playerDelay.contains(player.getName())) {
				playerDelay.add(player.getName());
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						playerDelay.remove(player.getName());
					}
				}, 10);
			}
			
			Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					if (SchedulerUtils.firePreview.containsKey(player.getName())) {
						for (SimpleLocation loc : SchedulerUtils.firePreview.get(player.getName())) {
							for (Player players : Bukkit.getOnlinePlayers()) {
								players.sendBlockChange(loc.toLocation(), loc.getBlock().getType(), loc.getBlock().getData());
							}
						}
						SchedulerUtils.firePreview.remove(player.getName());
					}

					if (player.getItemInHand().getType() == Material.TORCH || SchedulerUtils.playerFlare.containsKey(player.getName())) {
						Location target = player.getLocation().clone();
						while (target.getBlock().getRelative(0, -1, 0).getType().equals(Material.AIR) && target.getBlockY() > 0 && !target.getBlock().getRelative(0, -1, 0).equals(Material.AIR)) {
							target.setY(target.getBlockY() - 1);
						}
						if(!target.getBlock().equals(Material.AIR)) {
							target.setY(target.getBlockY() - 1);
						}
						
						if (target.getBlock().getType().isSolid()) {
							for (Player players : Bukkit.getOnlinePlayers()) {
								if (target.distance(target) < 30) {
									players.sendBlockChange(target, Material.GLOWSTONE, (byte) 0);
								}
							}

							ArrayList<SimpleLocation> locs = new ArrayList<SimpleLocation>();
							locs.add(new SimpleLocation(target));
							SchedulerUtils.firePreview.put(player.getName(), locs);
						}
					}
					
					if (player.getItemInHand().getType() == Material.STEP && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Camp Fire") || player.getItemInHand().getType() == Material.ANVIL || player.getItemInHand().getType() == Material.STONE_SLAB2) {
						if (player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5) != null) {
							if ((player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getType().isOccluding() || player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getType().name().contains("STEP") && player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getData() == 8)) {
								
								Location target = player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone();

								if (target.getBlock().getType().isSolid() && !target.getBlock().getType().name().contains("LEAVE") && !target.getBlock().getRelative(0, 1, 0).getType().isSolid() && target.getBlock().getType() != Material.AIR && target.getBlock().getType() != Material.ANVIL && target.getBlock().getType() != Material.CHEST && target.getBlock().getType() != Material.getMaterial(33) && Ores.getBlockHealth(target) == -1 && !LocationUtils.directlyNextToMaterial(target.getBlock().getRelative(0, 1, 0).getLocation(), Material.IRON_DOOR_BLOCK) && !LocationUtils.directlyNextToMaterial(target.getBlock().getRelative(0, 1, 0).getLocation(), Material.WOODEN_DOOR) && target.getBlock().getRelative(0, -1, 0).getType() != Material.CHEST) {
									ArrayList<SimpleLocation> locs = new ArrayList<SimpleLocation>();
									locs.add(new SimpleLocation(target.clone().add(0, 0, 0)));
									SchedulerUtils.firePreview.put(player.getName(), locs);
									SchedulerUtils.placeLoc.put(player.getName(), new SimpleLocation(target));
									for (SimpleLocation loc : locs) {
										player.sendBlockChange(loc.toLocation(), Material.STAINED_GLASS.getId(), (byte) 5);
									}
								} else {
									ArrayList<SimpleLocation> locs = new ArrayList<SimpleLocation>();
									locs.add(new SimpleLocation(target.clone().add(0, 0, 0)));
									SchedulerUtils.firePreview.put(player.getName(), locs);
									for (SimpleLocation loc : locs) {
										player.sendBlockChange(loc.toLocation(), Material.STAINED_GLASS.getId(), (byte) 14);
									}
								}
							}
						}
					}
					
					if(player.getItemInHand().getType() == Material.BED && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Bed")) {
						if(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5) != null) {
							if(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getType().isOccluding() || player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getType() == Material.WOOD_STEP || player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getType() == Material.STEP) {
								Location target = player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone();
								
								if(target.getBlock().getType().isSolid() && !target.getBlock().getRelative(0, 1, 0).getType().isSolid()) {
									player.sendBlockChange(target.clone().add(0, 0, 0), Material.STAINED_GLASS, (byte) 5);
									ArrayList<SimpleLocation> locs = new ArrayList<SimpleLocation>();
									locs.add(new SimpleLocation(target.clone().add(0, 0, 0)));
									Rust.firePreview.put(player.getName(), locs);
									Rust.placeLoc.put(player.getName(), new SimpleLocation(target));
									
								} else {
									player.sendBlockChange(target.clone().add(0, 0, 0), Material.STAINED_GLASS, (byte) 14);
									ArrayList<SimpleLocation> locs = new ArrayList<SimpleLocation>();
									locs.add(new SimpleLocation(target.clone().add(0, 0, 0)));
									Rust.firePreview.put(player.getName(), locs);
								}
							}
						}
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(405) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Sleeping Bag")) {
						if(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5) != null) {
							if(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getType().isSolid() || player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getType() == Material.WOOD_STEP || player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getType() == Material.STEP) {
								Location target = player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone();
								boolean go = true;
								
								for(int x = -2; x <= 2; x++) {
									for(int z = -2; z <= 2; z++) {
										if(target.clone().getBlock().getRelative(x, 1, z).getType() == Material.CARPET) {
											player.sendBlockChange(target.clone().add(0, 0, 0), Material.STAINED_GLASS, (byte) 14);
											player.sendBlockChange(target.clone().add(1, 0, 0), Material.STAINED_GLASS, (byte) 14);
											ArrayList<SimpleLocation> locs = new ArrayList<SimpleLocation>();
											locs.add(new SimpleLocation(target.clone().add(0, 0, 0)));
											locs.add(new SimpleLocation(target.clone().add(1, 0, 0)));
											Rust.firePreview.put(player.getName(), locs);
											
											go = false;
										}
									}
								}
								
								if(go) {
									int x = PlayerUtils.getPlayerDirection(player).equals("N") || PlayerUtils.getPlayerDirection(player).equals("S") ? 1 : 0;
									int z = PlayerUtils.getPlayerDirection(player).equals("N") || PlayerUtils.getPlayerDirection(player).equals("S") ? 0 : 1;

									if(!target.getBlock().getRelative(0, 1, 0).getType().isSolid() && target.getBlock().getType() != Material.CARPET && target.getBlock().getRelative(x, 0, z).getType() != Material.AIR && !target.getBlock().getRelative(x, 1, z).getType().isSolid() && target.getBlock().getRelative(x, 0, z).getType() != Material.CARPET && target.getBlock().getType() != Material.CHEST && target.getBlock().getLocation().getBlock().getY() < player.getLocation().getY() && !LocationUtils.isLocationNearBlock(target, Material.IRON_DOOR_BLOCK, 2) && !LocationUtils.isLocationNearBlock(target, Material.WOODEN_DOOR, 2)) {
										player.sendBlockChange(target.clone().add(0, 0, 0), Material.STAINED_GLASS, (byte) 5);
										player.sendBlockChange(target.clone().add(x, 0, z), Material.STAINED_GLASS, (byte) 5);
										ArrayList<SimpleLocation> locs = new ArrayList<SimpleLocation>();
										locs.add(new SimpleLocation(target.clone().add(0, 0, 0)));
										locs.add(new SimpleLocation(target.clone().add(x, 0, z)));
										Rust.firePreview.put(player.getName(), locs);
										Rust.placeLoc.put(player.getName(), new SimpleLocation(target));
										
									} else {
										player.sendBlockChange(target.clone().add(0, 0, 0), Material.STAINED_GLASS, (byte) 14);
										player.sendBlockChange(target.clone().add(x, 0, z), Material.STAINED_GLASS, (byte) 14);
										ArrayList<SimpleLocation> locs = new ArrayList<SimpleLocation>();
										locs.add(new SimpleLocation(target.clone().add(0, 0, 0)));
										locs.add(new SimpleLocation(target.clone().add(z, 0, x)));
										Rust.firePreview.put(player.getName(), locs);
									}
								}
							}
						}
					}	
					
					if (Building.playerBlocks.containsKey(player.getName())) {
						for (String loc : Building.playerBlocks.get(player.getName())) {
							player.sendBlockChange(new SimpleLocation(loc).toLocation(), new SimpleLocation(loc).getBlock().getTypeId(), new SimpleLocation(loc).getBlock().getData());
						}
						
						Building.playerBlocks.remove(player.getName());
					}
					
					HashMap<String, MaterialData> blocks = new HashMap<String, MaterialData>();

					if(heldItem != null) {
						if (heldItem.getType() == Material.getMaterial(264) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Foundation".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.fm.previewFoundation(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), Material.WOOD));
				
						} else if (heldItem.getType() == Material.getMaterial(266) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Pillar".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "Pillar"));
				
						} else if (heldItem.getType() == Material.getMaterial(281) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Wall".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "Wall"));
				
						} else if (heldItem.getType() == Material.getMaterial(287) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Doorway".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "Doorway"));
				
						} else if (heldItem.getType() == Material.getMaterial(288) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Window".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "Window"));
				
						} else if (heldItem.getType() == Material.getMaterial(377) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Stairs".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "Stair"));
				
						} else if (heldItem.getType() == Material.getMaterial(296) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Ceiling".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "Ceiling"));
				
						} else if (heldItem.getType() == Material.getMaterial(336) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Foundation".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.fm.previewFoundation(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), Material.IRON_BLOCK));
				
						} else if (heldItem.getType() == Material.getMaterial(338) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Pillar".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "MetalPillar"));
				
						} else if (heldItem.getType() == Material.getMaterial(353) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Wall".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "MetalWall"));
				
						} else if (heldItem.getType() == Material.getMaterial(371) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Doorway".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "MetalDoorway"));
				
						} else if (heldItem.getType() == Material.getMaterial(372) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Window".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "MetalWindow"));
				
						} else if (heldItem.getType() == Material.getMaterial(378) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Stairs".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "MetalStair"));
				
						} else if (heldItem.getType() == Material.getMaterial(388) && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Ceiling".replace("&", ChatColor.COLOR_CHAR + ""))) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "MetalCeiling"));
				
							// Chest
						} else if (heldItem.getType() == Material.CHEST && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Large Wood Storage Box")) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "Chest1"));
				
						} else if (heldItem.getType() == Material.CHEST && heldItem.getItemMeta() != null && heldItem.getItemMeta().getDisplayName() != null && heldItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Wood Storage Box")) {
							blocks.putAll(Building.pm.previewProp(player, player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 5).getLocation().clone(), "Chest2"));
						}	
						
						for(String locString : blocks.keySet()) {
							MaterialData data = blocks.get(locString);
							player.sendBlockChange(new SimpleLocation(locString).toLocation(), data.getItemType(), data.getData());
						}
						Building.playerBlocks.put(player.getName(), blocks.keySet());
					}
				}
			});
		}
	}
}
