package com.swingsword.ssengine.game.games.rust.building;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.game.games.rust.utils.PlayerUtils;
import com.swingsword.ssengine.game.games.rust.utils.SimpleLocation;

public class PropManager {
    
	@SuppressWarnings("deprecation")
	List<Material> forbidden = Arrays.asList(Material.getMaterial(44), Material.getMaterial(182), Material.getMaterial(145), Material.CHEST, Material.LEAVES, Material.LEAVES_2, Material.WOODEN_DOOR, Material.IRON_DOOR);
	private static HashMap<Chunk, ArrayList<String>> playersChunks = new HashMap<>();
	
    @SuppressWarnings("deprecation")
	public HashMap<String, MaterialData> previewProp(Player player, Location loc, String prop) {
    	if(loc.getChunk().isLoaded() && Building.canBuildThere(loc)) {
    		HashMap<String, MaterialData> locs = new HashMap<String, MaterialData>();
			Location savedLoc = loc.clone();
			
			Foundation foundation = Building.fm.getFoundation(loc);
			Foundation propFoundation = Building.fm.getPropFoundation(loc);
			
			if(prop.equalsIgnoreCase("Pillar")) {
				byte info = (byte) 0;
				
				if(Building.fm.isFoundation(loc) && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid() && loc.getBlock().getType() == Material.WOOD) {
					info = (byte) 5;
					
				} else if(propFoundation != null) {
					if(!loc.clone().add(0, 1, 0).getBlock().getType().isSolid() && loc.getBlock().getType() == Material.LOG) {
						info = (byte) 5;
					} else {
						info = (byte) 14;
					}
					
				} else {
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					for(int x = 0; x < 1; x = x + 0) {
						for(int y = 1; y < 4; y = y + 0) {
							for(int z = 0; z < 1; z = z + 0) {
								Location loc1 = loc.clone().add(x, y, z);
								////if(player.getLocation().distance(loc1) > 2) {
									//player.sendBlockChange(loc1, mat, info);
								//}
								locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
								
								if(info != (byte) 14) {
									if(getRelativePropLocation(loc, foundation).getBlockX() % 2 != 0 || getRelativePropLocation(loc, foundation).getBlockZ() % 2 != 0) {
										for(String loc2 : locs.keySet()) {
											//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
										}
										locs.clear();
										x = 0;
										y = 1;
										z = 0;
										info = (byte) 14;
										reset = true;
									}
								}
								if(info != (byte) 14) {
									if(x == 0 && y == 3 && z == 0) {
										if(loc1.getBlock().getType() == Material.WOOD_STEP) {
											for(String loc2 : locs.keySet()) {
												//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
											}
											locs.clear();
											x = 0;
											y = 1;
											z = 0;
											info = (byte) 14;
											reset = true;
										}
									}
								}
								
								if(reset == false) {
									z = z + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								y = y + 1;
							}
							reset = false;
						}
						
						if(reset == false) {
							x = x + 1;
						}
						reset = false;
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
				
			} else if(prop.equalsIgnoreCase("Wall")) {
				byte info = (byte) 0;
				
				if(Building.fm.isFoundation(loc) || propFoundation != null) {
					info = (byte) 5;
					
				} else {
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					if(PlayerUtils.getPlayerDirection(player).equals("N") || PlayerUtils.getPlayerDirection(player).equals("S")) {
						for(int x = -1; x < 2; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										////if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
									
										if(info != (byte) 14) {
											if(loc.clone().add(-2, 1, 0).getBlock().getType() != Material.LOG || loc.clone().add(-2, 2, 0).getBlock().getType() != Material.LOG || loc.clone().add(2, 1, 0).getBlock().getType() != Material.LOG || loc.clone().add(2, 2, 0).getBlock().getType() != Material.LOG) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.WOOD) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
					} else {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = -1; z < 2; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc.clone().add(0, 1, -2).getBlock().getType() != Material.LOG || loc.clone().add(0, 2, -2).getBlock().getType() != Material.LOG || loc.clone().add(0, 1, 2).getBlock().getType() != Material.LOG || loc.clone().add(0, 2, 2).getBlock().getType() != Material.LOG) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = -1;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.WOOD) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
			
			} else if(prop.equalsIgnoreCase("Doorway")) {
				byte info = (byte) 0;
				
				if(Building.fm.isFoundation(loc) || propFoundation != null) {
					info = (byte) 5;
					
				} else {
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					if(PlayerUtils.getPlayerDirection(player).equals("N") || PlayerUtils.getPlayerDirection(player).equals("S")) {
						for(int x = -1; x < 2; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									if(!(x == 0 && y == 1 && z == 0) && !(x == 0 && y == 2 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
									
										if(info != (byte) 14) {
											if(loc.clone().add(-2, 1, 0).getBlock().getType() != Material.LOG || loc.clone().add(-2, 2, 0).getBlock().getType() != Material.LOG || loc.clone().add(2, 1, 0).getBlock().getType() != Material.LOG || loc.clone().add(2, 2, 0).getBlock().getType() != Material.LOG) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.WOOD) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
					} else {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = -1; z < 2; z = z + 0) {
									if(!(x == 0 && y == 1 && z == 0) && !(x == 0 && y == 2 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc.clone().add(0, 1, -2).getBlock().getType() != Material.LOG || loc.clone().add(0, 2, -2).getBlock().getType() != Material.LOG || loc.clone().add(0, 1, 2).getBlock().getType() != Material.LOG || loc.clone().add(0, 2, 2).getBlock().getType() != Material.LOG) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = -1;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.WOOD) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
			
			} else if(prop.equalsIgnoreCase("Window")) {
				byte info = (byte) 0;
				
				if(Building.fm.isFoundation(loc) || propFoundation != null) {
					info = (byte) 5;
					
				} else {
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					if(PlayerUtils.getPlayerDirection(player).equals("N") || PlayerUtils.getPlayerDirection(player).equals("S")) {
						for(int x = -1; x < 2; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									if(!(x == 0 && y == 2 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
									
										if(info != (byte) 14) {
											if(loc.clone().add(-2, 1, 0).getBlock().getType() != Material.LOG || loc.clone().add(-2, 2, 0).getBlock().getType() != Material.LOG || loc.clone().add(2, 1, 0).getBlock().getType() != Material.LOG || loc.clone().add(2, 2, 0).getBlock().getType() != Material.LOG) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.WOOD) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
					} else {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = -1; z < 2; z = z + 0) {
									if(!(x == 0 && y == 2 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc.clone().add(0, 1, -2).getBlock().getType() != Material.LOG || loc.clone().add(0, 2, -2).getBlock().getType() != Material.LOG || loc.clone().add(0, 1, 2).getBlock().getType() != Material.LOG || loc.clone().add(0, 2, 2).getBlock().getType() != Material.LOG) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = -1;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.WOOD) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
				
			} else if(prop.equalsIgnoreCase("Stair")) {
				byte info = (byte) 0;
				
				if(Building.fm.isFoundation(loc) || propFoundation != null && loc.getBlock().getType() == Material.WOOD || propFoundation != null && loc.getBlock().getType() == Material.WOOD_STEP) {
					info = (byte) 5;
				} else {
					info = (byte) 14;
				}
				
				if(loc.clone().add(2, 1, 2).getBlock().getType() != Material.LOG || 
						loc.clone().add(-2, 1, -2).getBlock().getType() != Material.LOG || 
						loc.clone().add(-2, 1, 2).getBlock().getType() != Material.LOG || 
						loc.clone().add(2, 1, -2).getBlock().getType() != Material.LOG) {
					
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					if(PlayerUtils.getPlayerDirection(player).equals("N")) {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc1.getBlock().getType() != Material.AIR) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
						//player.sendBlockChange(loc.clone().add(-1, 3, 1), mat, info);
						//player.sendBlockChange(loc.clone().add(0, 3, 1), mat, info);
						//player.sendBlockChange(loc.clone().add(1, 3, 1), mat, info);
						locs.put(new SimpleLocation(loc.clone().add(-1, 3, 1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(0, 3, 1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(1, 3, 1)).toString(), new MaterialData(mat, info));
						
					} else if(PlayerUtils.getPlayerDirection(player).equals("E")) {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc1.getBlock().getType() != Material.AIR) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
						//player.sendBlockChange(loc.clone().add(-1, 3, -1), mat, info);
						//player.sendBlockChange(loc.clone().add(-1, 3, 0), mat, info);
						//player.sendBlockChange(loc.clone().add(-1, 3, 1), mat, info);
						locs.put(new SimpleLocation(loc.clone().add(-1, 3, -1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(-1, 3, 0)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(-1, 3, 1)).toString(), new MaterialData(mat, info));
						
					} else if(PlayerUtils.getPlayerDirection(player).equals("S")) {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc1.getBlock().getType() != Material.AIR) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
										
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
									
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
								
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
						//player.sendBlockChange(loc.clone().add(-1, 3, -1), mat, info);
						//player.sendBlockChange(loc.clone().add(0, 3, -1), mat, info);
						//player.sendBlockChange(loc.clone().add(1, 3, -1), mat, info);
						locs.put(new SimpleLocation(loc.clone().add(-1, 3, -1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(0, 3, -1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(1, 3, -1)).toString(), new MaterialData(mat, info));
						
					} else if(PlayerUtils.getPlayerDirection(player).equals("W")) {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc1.getBlock().getType() != Material.AIR) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
										
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
									
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
								
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
						//player.sendBlockChange(loc.clone().add(1, 3, -1), mat, info);
						//player.sendBlockChange(loc.clone().add(1, 3, 0), mat, info);
						//player.sendBlockChange(loc.clone().add(1, 3, 1), mat, info);
						locs.put(new SimpleLocation(loc.clone().add(1, 3, -1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(1, 3, 0)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(1, 3, 1)).toString(), new MaterialData(mat, info));
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
				
			} else if(prop.equalsIgnoreCase("Ceiling")) {
				byte info = (byte) 0;
				
				if(propFoundation != null) {
					if(getRelativePropLocation(loc, propFoundation).getBlockY() % 3 == 0) {
						
						if(loc.getBlock().getType() == Material.WOOD || loc.getBlock().getType() == Material.WOOD_STEP) {
							Material mat = loc.getBlock().getType();
							byte data = loc.getBlock().getData();
							Location oldLoc = loc.clone();
							
							//if(!loc.getBlock().getType().name().contains("DOOR") && !loc.getBlock().getRelative(0, 1, 0).getType().name().contains("DOOR")) {
							//	loc.getBlock().setTypeIdAndData(0, (byte) 0, false);
							//}
							
							if(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.WOOD)), 6) != null && getRelativePropLocation(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.WOOD)), 6).getLocation(), Building.fm.getPropFoundation(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.WOOD)), 6).getLocation())) != null && getRelativePropLocation(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.WOOD)), 6).getLocation(), Building.fm.getPropFoundation(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.WOOD)), 6).getLocation())).getBlockY() % 3 == 0) {
								loc = player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.WOOD)), 6).getLocation();
								savedLoc = player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.WOOD)), 6).getLocation();
							}
							
							//if(!oldLoc.getBlock().getType().name().contains("DOOR") && !oldLoc.getBlock().getRelative(0, 1, 0).getType().name().contains("DOOR")) {
								//oldLoc.getBlock().setTypeIdAndData(mat.getId(), data, false);
							//}
						}
						
						if(savedLoc.clone().add(0, 0, 0).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(4, 0, 0).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(4, 0, 4).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(0, 0, 4).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(2, 0, 2).getBlock().getType() == Material.AIR) {
							info = (byte) 5;
							loc = savedLoc.clone().add(2, 0, 2);
							
						} else if(savedLoc.clone().add(0, 0, 0).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(4, 0, 0).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(4, 0, -4).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(0, 0, -4).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(2, 0, -2).getBlock().getType() == Material.AIR) {
							info = (byte) 5;
							loc = savedLoc.clone().add(2, 0, -2);
							
						} else if(savedLoc.clone().add(0, 0, 0).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(-4, 0, 0).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(-4, 0, -4).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(0, 0, -4).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(-2, 0, -2).getBlock().getType() == Material.AIR) {
							info = (byte) 5;
							loc = savedLoc.clone().add(-2, 0, -2);
							
						} else if(savedLoc.clone().add(0, 0, 0).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(-4, 0, 0).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(-4, 0, 4).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(0, 0, 4).getBlock().getType().equals(Material.LOG) && savedLoc.clone().add(-2, 0, 2).getBlock().getType() == Material.AIR) {
							info = (byte) 5;
							loc = savedLoc.clone().add(-2, 0, 2);
							
						} else {
							info = (byte) 14;
						}
						
					} else {
						info = (byte) 14;
					}
					
				} else {
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(savedLoc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(loc.getBlock().getType() != Material.AIR) {
					info = (byte) 14;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					if(player.getLocation().distance(loc) > 2) {
						//player.sendBlockChange(loc, mat, info);
					}
					
					for(int x = -2; x < 3; x = x + 0) {
						for(int y = 0; y < 1; y = y + 0) {
							for(int z = -2; z < 3; z = z + 0) {
								Location loc1 = loc.clone().add(x, y, z);
								locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
									
								if(info != (byte) 14) {
									if(loc1.getBlock().getType() != Material.AIR && x == 0 && y == 0 && z == 0) {
										for(String loc2 : locs.keySet()) {
											//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
										}
										locs.clear();
										x = 0;
										y = 2;
										z = 0;
										info = (byte) 14;
										reset = true;
									}
								}
								
								if(reset == false) {
									z = z + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								y = y + 1;
							}
							reset = false;
						}
						
						if(reset == false) {
							x = x + 1;
						}
						reset = false;
					}
					
					

					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(savedLoc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
				
				//METALHERE//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
				
			} else if(prop.equalsIgnoreCase("MetalPillar")) {
				byte info = (byte) 0;
				
				if(Building.fm.isFoundation(loc) && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid() && loc.getBlock().getType() == Material.IRON_BLOCK) {
					info = (byte) 5;
					
				} else if(propFoundation != null) {
					if(!loc.clone().add(0, 1, 0).getBlock().getType().isSolid() && loc.getBlock().getType() == Material.QUARTZ_BLOCK) {
						info = (byte) 5;
					} else {
						info = (byte) 14;
					}
					
				} else {
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					for(int x = 0; x < 1; x = x + 0) {
						for(int y = 1; y < 4; y = y + 0) {
							for(int z = 0; z < 1; z = z + 0) {
								Location loc1 = loc.clone().add(x, y, z);
								//if(player.getLocation().distance(loc1) > 2) {
									//player.sendBlockChange(loc1, mat, info);
								//}
								locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
								
								if(info != (byte) 14) {
									if(getRelativePropLocation(loc, foundation).getBlockX() % 2 != 0 || getRelativePropLocation(loc, foundation).getBlockZ() % 2 != 0) {
										for(String loc2 : locs.keySet()) {
											//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
										}
										locs.clear();
										x = 0;
										y = 1;
										z = 0;
										info = (byte) 14;
										reset = true;
									}
								}
								if(info != (byte) 14) {
									if(x == 0 && y == 3 && z == 0) {
										if(loc1.getBlock().getType() == Material.getMaterial(44)) {
											for(String loc2 : locs.keySet()) {
												//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
											}
											locs.clear();
											x = 0;
											y = 1;
											z = 0;
											info = (byte) 14;
											reset = true;
										}
									}
								}
								
								if(reset == false) {
									z = z + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								y = y + 1;
							}
							reset = false;
						}
						
						if(reset == false) {
							x = x + 1;
						}
						reset = false;
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
				
			} else if(prop.equalsIgnoreCase("MetalWall")) {
				byte info = (byte) 0;
				
				if(Building.fm.isFoundation(loc) || propFoundation != null) {
					info = (byte) 5;
					
				} else {
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					if(PlayerUtils.getPlayerDirection(player).equals("N") || PlayerUtils.getPlayerDirection(player).equals("S")) {
						for(int x = -1; x < 2; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
									
										if(info != (byte) 14) {
											if(loc.clone().add(-2, 1, 0).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(-2, 2, 0).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(2, 1, 0).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(2, 2, 0).getBlock().getType() != Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
					} else {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = -1; z < 2; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc.clone().add(0, 1, -2).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(0, 2, -2).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(0, 1, 2).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(0, 2, 2).getBlock().getType() != Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = -1;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
			
			} else if(prop.equalsIgnoreCase("MetalDoorway")) {
				byte info = (byte) 0;
				
				if(Building.fm.isFoundation(loc) || propFoundation != null) {
					info = (byte) 5;
					
				} else {
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					if(PlayerUtils.getPlayerDirection(player).equals("N") || PlayerUtils.getPlayerDirection(player).equals("S")) {
						for(int x = -1; x < 2; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									if(!(x == 0 && y == 1 && z == 0) && !(x == 0 && y == 2 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
									
										if(info != (byte) 14) {
											if(loc.clone().add(-2, 1, 0).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(-2, 2, 0).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(2, 1, 0).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(2, 2, 0).getBlock().getType() != Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
					} else {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = -1; z < 2; z = z + 0) {
									if(!(x == 0 && y == 1 && z == 0) && !(x == 0 && y == 2 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc.clone().add(0, 1, -2).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(0, 2, -2).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(0, 1, 2).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(0, 2, 2).getBlock().getType() != Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = -1;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
			
			} else if(prop.equalsIgnoreCase("MetalWindow")) {
				byte info = (byte) 0;
				
				if(Building.fm.isFoundation(loc) || propFoundation != null) {
					info = (byte) 5;
					
				} else {
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					if(PlayerUtils.getPlayerDirection(player).equals("N") || PlayerUtils.getPlayerDirection(player).equals("S")) {
						for(int x = -1; x < 2; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									if(!(x == 0 && y == 2 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
									
										if(info != (byte) 14) {
											if(loc.clone().add(-2, 1, 0).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(-2, 2, 0).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(2, 1, 0).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(2, 2, 0).getBlock().getType() != Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
					} else {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = -1; z < 2; z = z + 0) {
									if(!(x == 0 && y == 2 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc.clone().add(0, 1, -2).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(0, 2, -2).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(0, 1, 2).getBlock().getType() != Material.QUARTZ_BLOCK || loc.clone().add(0, 2, 2).getBlock().getType() != Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = -1;
												info = (byte) 14;
												reset = true;
											}
										}
										
										if(info != (byte) 14) {
											if(loc1.clone().getBlock().getType() == Material.QUARTZ_BLOCK) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = -1;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
				
			} else if(prop.equalsIgnoreCase("MetalStair")) {
				byte info = (byte) 0;
				
				if(Building.fm.isFoundation(loc) || propFoundation != null && loc.getBlock().getType() == Material.getMaterial(44) || propFoundation != null && loc.getBlock().getType() == Material.IRON_BLOCK) {
					info = (byte) 5;
				} else {
					info = (byte) 14;
				}
				
				if(loc.clone().add(2, 1, 2).getBlock().getType() != Material.QUARTZ_BLOCK || 
						loc.clone().add(-2, 1, -2).getBlock().getType() != Material.QUARTZ_BLOCK || 
						loc.clone().add(-2, 1, 2).getBlock().getType() != Material.QUARTZ_BLOCK || 
						loc.clone().add(2, 1, -2).getBlock().getType() != Material.QUARTZ_BLOCK) {
					
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					if(PlayerUtils.getPlayerDirection(player).equals("N")) {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc1.getBlock().getType() != Material.AIR) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
						//player.sendBlockChange(loc.clone().add(-1, 3, 1), mat, info);
						//player.sendBlockChange(loc.clone().add(0, 3, 1), mat, info);
						//player.sendBlockChange(loc.clone().add(1, 3, 1), mat, info);
						locs.put(new SimpleLocation(loc.clone().add(-1, 3, 1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(0, 3, 1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(1, 3, 1)).toString(), new MaterialData(mat, info));
						
					} else if(PlayerUtils.getPlayerDirection(player).equals("E")) {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc1.getBlock().getType() != Material.AIR) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
						//player.sendBlockChange(loc.clone().add(-1, 3, -1), mat, info);
						//player.sendBlockChange(loc.clone().add(-1, 3, 0), mat, info);
						//player.sendBlockChange(loc.clone().add(-1, 3, 1), mat, info);
						locs.put(new SimpleLocation(loc.clone().add(-1, 3, -1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(-1, 3, 0)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(-1, 3, 1)).toString(), new MaterialData(mat, info));
						
					} else if(PlayerUtils.getPlayerDirection(player).equals("S")) {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
										//}
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc1.getBlock().getType() != Material.AIR) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
										
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
									
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
								
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
						//player.sendBlockChange(loc.clone().add(-1, 3, -1), mat, info);
						//player.sendBlockChange(loc.clone().add(0, 3, -1), mat, info);
						//player.sendBlockChange(loc.clone().add(1, 3, -1), mat, info);
						locs.put(new SimpleLocation(loc.clone().add(-1, 3, -1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(0, 3, -1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(1, 3, -1)).toString(), new MaterialData(mat, info));
							
					} else if(PlayerUtils.getPlayerDirection(player).equals("W")) {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 1; y < 4; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									//if(!(x == 0 && y == 1 && z == 0)) {
										Location loc1 = loc.clone().add(x, y, z);
										//player.sendBlockChange(loc1, mat, info);
										locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										
										if(info != (byte) 14) {
											if(loc1.getBlock().getType() != Material.AIR) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 1;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									//}
										
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
									
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
								
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
						//player.sendBlockChange(loc.clone().add(1, 3, -1), mat, info);
						//player.sendBlockChange(loc.clone().add(1, 3, 0), mat, info);
						//player.sendBlockChange(loc.clone().add(1, 3, 1), mat, info);
						locs.put(new SimpleLocation(loc.clone().add(1, 3, -1)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(1, 3, 0)).toString(), new MaterialData(mat, info));
						locs.put(new SimpleLocation(loc.clone().add(1, 3, 1)).toString(), new MaterialData(mat, info));
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
				
			} else if(prop.equalsIgnoreCase("MetalCeiling")) {
				byte info = (byte) 0;
				
				if(propFoundation != null) {
					if(getRelativePropLocation(loc, propFoundation).getBlockY() % 3 == 0) {
						
						if(loc.getBlock().getType() == Material.IRON_BLOCK || loc.getBlock().getType() == Material.getMaterial(44)) {
							Material mat = loc.getBlock().getType();
							byte data = loc.getBlock().getData();
							Location oldLoc = loc.clone();
							
							//if(!loc.getBlock().getType().name().contains("DOOR") && !loc.getBlock().getRelative(0, 1, 0).getType().name().contains("DOOR")) {
							//	loc.getBlock().setTypeIdAndData(0, (byte) 0, false);
							//}
							
							if(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.QUARTZ_BLOCK)), 6) != null && getRelativePropLocation(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.QUARTZ_BLOCK)), 6).getLocation(), Building.fm.getPropFoundation(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.QUARTZ_BLOCK)), 6).getLocation())) != null && getRelativePropLocation(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.QUARTZ_BLOCK)), 6).getLocation(), Building.fm.getPropFoundation(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.QUARTZ_BLOCK)), 6).getLocation())).getBlockY() % 3 == 0) {
								loc = player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.QUARTZ_BLOCK)), 6).getLocation();
								savedLoc = player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR, Material.QUARTZ_BLOCK)), 6).getLocation();
							}
							
							//if(!oldLoc.getBlock().getType().name().contains("DOOR") && !oldLoc.getBlock().getRelative(0, 1, 0).getType().name().contains("DOOR")) {
							//	oldLoc.getBlock().setTypeIdAndData(mat.getId(), data, false);
							//}
						}
						
						if(savedLoc.clone().add(0, 0, 0).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(4, 0, 0).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(4, 0, 4).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(0, 0, 4).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(2, 0, 2).getBlock().getType() == Material.AIR) {
							info = (byte) 5;
							loc = savedLoc.clone().add(2, 0, 2);
							
						} else if(savedLoc.clone().add(0, 0, 0).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(4, 0, 0).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(4, 0, -4).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(0, 0, -4).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(2, 0, -2).getBlock().getType() == Material.AIR) {
							info = (byte) 5;
							loc = savedLoc.clone().add(2, 0, -2);
							
						} else if(savedLoc.clone().add(0, 0, 0).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(-4, 0, 0).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(-4, 0, -4).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(0, 0, -4).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(-2, 0, -2).getBlock().getType() == Material.AIR) {
							info = (byte) 5;
							loc = savedLoc.clone().add(-2, 0, -2);
							
						} else if(savedLoc.clone().add(0, 0, 0).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(-4, 0, 0).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(-4, 0, 4).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(0, 0, 4).getBlock().getType().equals(Material.QUARTZ_BLOCK) && savedLoc.clone().add(-2, 0, 2).getBlock().getType() == Material.AIR) {
							info = (byte) 5;
							loc = savedLoc.clone().add(-2, 0, 2);
							
						} else {
							info = (byte) 14;
						}
						
					} else {
						info = (byte) 14;
					}
					
				} else {
					info = (byte) 14;
				}
				
				Material mat = Material.AIR;
				if(savedLoc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(loc.getBlock().getType() != Material.AIR) {
					info = (byte) 14;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					if(player.getLocation().distance(loc) > 2) {
						//player.sendBlockChange(loc, mat, info);
					}
					
					for(int x = -2; x < 3; x = x + 0) {
						for(int y = 0; y < 1; y = y + 0) {
							for(int z = -2; z < 3; z = z + 0) {
								Location loc1 = loc.clone().add(x, y, z);
								locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
									
								if(info != (byte) 14) {
									if(loc1.getBlock().getType() != Material.AIR && x == 0 && y == 0 && z == 0) {
										for(String loc2 : locs.keySet()) {
											//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
										}
										locs.clear();
										x = 0;
										y = 2;
										z = 0;
										info = (byte) 14;
										reset = true;
									}
								}
								
								if(reset == false) {
									z = z + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								y = y + 1;
							}
							reset = false;
						}
						
						if(reset == false) {
							x = x + 1;
						}
						reset = false;
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(savedLoc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
				
				//--------------------------------CHEST
				
			} else if(prop.equalsIgnoreCase("Chest1")) {
				byte info = (byte) 5;
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					if(PlayerUtils.getPlayerDirection(player).equals("N") || PlayerUtils.getPlayerDirection(player).equals("S")) {
						for(int x = -1; x < 1; x = x + 0) {
							for(int y = 0; y < 1; y = y + 0) {
								for(int z = 0; z < 1; z = z + 0) {
									Location loc1 = loc.clone().add(x, y, z);
									//if(player.getLocation().distance(loc1) > 2) {
										//player.sendBlockChange(loc1, mat, info);
									//}
									locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
									
									if(info != (byte) 14) {
										if(loc1.getBlock().getType() == Material.AIR || loc1.getBlock().getRelative(0, 1, 0).getType() != Material.AIR || loc1.getBlock().getType() == Material.CHEST || !loc1.getBlock().getType().isSolid() || loc1.getBlock().getType() == Material.CHEST || loc1.getBlock().getRelative(0, 1, 0).getType() == Material.CHEST || !loc1.getBlock().getType().isSolid() || loc1.getBlock().getType() == Material.BED_BLOCK || forbidden.contains(loc1.getBlock().getType())) {
											for(String loc2 : locs.keySet()) {
												//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).toLocation().getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
											}
											locs.clear();
											x = -1;
											y = 0;
											z = 0;
											info = (byte) 14;
											reset = true;
										}
									}
									
									if(info != (byte) 14) {
										for(int x2 = -1; x2 <= 1; x2++) {
											for(int z2 = -1; z2 <= 1; z2++) {
												if(loc1.getBlock().getRelative(x2, 1, z2).getChunk().isLoaded() && loc1.getBlock().getRelative(x2, 1, z2).getType() == Material.CHEST || !loc1.getBlock().getType().isSolid() || loc1.getBlock().getType() == Material.BED_BLOCK || com.swingsword.ssengine.game.games.rust.utils.LocationUtils.directlyNextToMaterial(loc1.clone().add(0, 1, 0), "DOOR")) {
													for(String loc2 : locs.keySet()) {
														//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
													}
													locs.clear();
													x = -1;
													y = 0;
													z = 0;
													info = (byte) 14;
													reset = true;
												}
											}
										}
									}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
						
					} else {
						for(int x = 0; x < 1; x = x + 0) {
							for(int y = 0; y < 1; y = y + 0) {
								for(int z = -1; z < 1; z = z + 0) {
									Location loc1 = loc.clone().add(x, y, z);
									//if(player.getLocation().distance(loc1) > 2) {
										//player.sendBlockChange(loc1, mat, info);
									//}
									locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
									
									if(info != (byte) 14) {
										if(loc1.getBlock().getType() == Material.AIR || loc1.getBlock().getRelative(0, 1, 0).getType() != Material.AIR || loc1.getBlock().getType() == Material.CHEST || loc1.getBlock().getRelative(0, 1, 0).getType() == Material.CHEST || !loc1.getBlock().getType().isSolid() || loc1.getBlock().getType() == Material.BED_BLOCK || forbidden.contains(loc1.getBlock().getType())) {
											for(String loc2 : locs.keySet()) {
												//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
											}
											locs.clear();
											x = 0;
											y = 0;
											z = -1;
											info = (byte) 14;
											reset = true;
										}
									}
									
									if(info != (byte) 14) {
										for(int x2 = -1; x2 <= 1; x2++) {
											for(int z2 = -1; z2 <= 1; z2++) {
												if(loc1.getBlock().getRelative(x2, 1, z2).getChunk().isLoaded() && loc1.getBlock().getRelative(x2, 1, z2).getType() == Material.CHEST || !loc1.getBlock().getType().isSolid() || loc1.getBlock().getType() == Material.BED_BLOCK || com.swingsword.ssengine.game.games.rust.utils.LocationUtils.directlyNextToMaterial(loc1.clone().add(0, 1, 0), "DOOR")) {
													for(String loc2 : locs.keySet()) {
														//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
													}
													locs.clear();
													x = 0;
													y = 0;
													z = -1;
													info = (byte) 14;
													reset = true;
												}
											}
										}
									}
									
									if(reset == false) {
										z = z + 1;
									}
									reset = false;
								}
								
								if(reset == false) {
									y = y + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								x = x + 1;
							}
							reset = false;
						}
					}
					
					
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
			
			} else if(prop.equalsIgnoreCase("Chest2")) {
				byte info = (byte) 5;
				
				Material mat = Material.AIR;
				if(loc.getBlock().getType() != Material.AIR) {
					mat = Material.STAINED_GLASS;
					
				} else {
					mat = Material.AIR;
				}
				
				if(mat != Material.AIR) {
					boolean reset = false;
					
					for(int x = 0; x < 1; x = x + 0) {
						for(int y = 0; y < 1; y = y + 0) {
							for(int z = 0; z < 1; z = z + 0) {
								Location loc1 = loc.clone().add(x, y, z);
								//if(player.getLocation().distance(loc1) > 2) {
									//player.sendBlockChange(loc1, mat, info);
								//}
								locs.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
								
								if(info != (byte) 14) {
									if(loc1.getBlock().getType() == Material.AIR || loc1.getBlock().getRelative(0, 1, 0).getType() != Material.AIR || loc1.getBlock().getType() == Material.CHEST || !loc1.getBlock().getType().isSolid() || loc1.getBlock().getType() == Material.BED_BLOCK || forbidden.contains(loc1.getBlock().getType())) {
										for(String loc2 : locs.keySet()) {
											//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
										}
										locs.clear();
										x = 0;
										y = 0;
										z = 0;
										info = (byte) 14;
										reset = true;
									}
								}
								
								if(info != (byte) 14) {
									for(int x2 = -1; x2 <= 1; x2++) {
										for(int z2 = -1; z2 <= 1; z2++) {
											if(loc1.getBlock().getRelative(x2, 1, z2).getChunk().isLoaded() && loc1.getBlock().getRelative(x2, 1, z2).getType() == Material.CHEST || !loc1.getBlock().getType().isSolid() || loc1.getBlock().getType() == Material.BED_BLOCK || com.swingsword.ssengine.game.games.rust.utils.LocationUtils.directlyNextToMaterial(loc1.clone().add(0, 1, 0), "DOOR")) {
												for(String loc2 : locs.keySet()) {
													//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
												}
												locs.clear();
												x = 0;
												y = 0;
												z = 0;
												info = (byte) 14;
												reset = true;
											}
										}
									}
								}
								
								if(reset == false) {
									z = z + 1;
								}
								reset = false;
							}
							
							if(reset == false) {
								y = y + 1;
							}
							reset = false;
						}
						
						if(reset == false) {
							x = x + 1;
						}
						reset = false;
					}
					
					if(info != (byte) 14) {
						Building.playerBuildLoc.put(player.getName(), new SimpleLocation(loc).toString());
					} else {
						Building.playerBuildLoc.remove(player.getName());
					}
				}
			}
			
			return locs;
    	}
    	return new HashMap<String, MaterialData>();
    }
    
	@SuppressWarnings("deprecation")
	public void createProp(Player player, Location loc, String prop) {
		if(Building.canBuildThere(loc)) {
			Foundation foundation = Building.fm.getFoundation(loc);

	    	if(prop.equalsIgnoreCase("Pillar")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
		    		if(foundation != null && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
		    			if(getRelativePropLocation(loc, foundation).getBlockX() % 2 == 0 && getRelativePropLocation(loc, foundation).getBlockZ() % 2 == 0) {
							if(player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							} else {
								player.setItemInHand(null);
							}

							player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);

							foundation.addProp(player.getWorld(), loc.clone().add(0, 1, 0), prop, Material.LOG, (byte) 0);
							for (String all : Building.playerBlocks.get(player.getName())) {
								foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.LOG, (byte) 0);
							}
							Building.fm.saveFoundation(foundation);
							Building.playerBuildLoc.remove(player.getName());
						} else {
							player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
						}
					} else {
						player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
					}
				} else {
					player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
				}

			} else if (prop.equalsIgnoreCase("Wall")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
		    		if(foundation != null && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
							if(player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							} else {
								player.setItemInHand(null);
							}
							player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
							
							foundation.addProp(player.getWorld(), loc.clone().add(0, 1, 0), prop, Material.WOOD, (byte) 0);
							for(String all : Building.playerBlocks.get(player.getName())) {
								foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.WOOD, (byte) 0);
							}
							Building.fm.saveFoundation(foundation);
							Building.playerBuildLoc.remove(player.getName());
		    			} else {
				    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
				    	}
		    	} else {
		    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
		    	}
	    	
	    	} else if(prop.equalsIgnoreCase("Doorway")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
		    		if(foundation != null && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
							if(player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							} else {
								player.setItemInHand(null);
							}
							player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
							
							for(String all : Building.playerBlocks.get(player.getName())) {
								foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.WOOD, (byte) 0);
							}
							foundation.addProp(player.getWorld(), loc.clone().add(0, 2, 0), prop, Material.AIR, (byte) 0);
								
							Building.fm.saveFoundation(foundation);
							Building.playerBuildLoc.remove(player.getName());
		    			} else {
				    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
				    	}
		    	} else {
		    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
		    	}
	    	
	    	} else if(prop.equalsIgnoreCase("Window")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
		    		if(foundation != null && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
							if(player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							} else {
								player.setItemInHand(null);
							}
							player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
							
							for(String all : Building.playerBlocks.get(player.getName())) {
								foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.WOOD, (byte) 0);
							}
							foundation.addProp(player.getWorld(), loc.clone().add(0, 3, 0), prop, Material.WOOD_STEP, (byte) 8);
							foundation.addProp(player.getWorld(), loc.clone().add(0, 2, 0), prop, Material.AIR, (byte) 0);
							foundation.addProp(player.getWorld(), loc.clone().add(0, 1, 0), prop, Material.WOOD_STEP, (byte) 0);
							
							Building.fm.saveFoundation(foundation);
							Building.playerBuildLoc.remove(player.getName());
		    			} else {
				    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
				    	}
		    	} else {
		    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
		    	}
	    	
	    	} else if(prop.equalsIgnoreCase("Stair")) {
	        	if(Building.playerBuildLoc.containsKey(player.getName())) {
	    					if(player.getItemInHand().getAmount() > 1) {
	    						player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
	    					} else {
	    						player.setItemInHand(null);
	    					}
	    					player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
	    					
	    					for(String all : Building.playerBlocks.get(player.getName())) {
	    						if(getRelativePropLocation(new SimpleLocation(all).toLocation(), foundation).getBlockX() != 0 || getRelativePropLocation(new SimpleLocation(all).toLocation(), foundation).getBlockZ() != 0) {
	    							foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.WOOD_STEP, (byte) 8);
	    						}
	    					}
	    					
	    					foundation.addProp(player.getWorld(), loc.clone().add(0, 1, 0), prop, Material.WOOD, (byte) 0);
	    					foundation.addProp(player.getWorld(), loc.clone().add(0, 2, 0), prop, Material.WOOD, (byte) 0);
	    					foundation.addProp(player.getWorld(), loc.clone().add(0, 3, 0), prop, Material.WOOD, (byte) 0);
	    					
	    					if(loc.clone().add(-1, 3, 0).getBlock().getType().equals(Material.WOOD_STEP)) {
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 1, 1), prop, Material.WOOD_STEP, (byte) 0);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 1, 1), prop, Material.WOOD_STEP, (byte) 8);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 2, 0), prop, Material.WOOD_STEP, (byte) 0);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 2, -1), prop, Material.WOOD_STEP, (byte) 8);
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 3, -1), prop, Material.WOOD_STEP, (byte) 0);
	    						
	    					} else if(loc.clone().add(1, 3, 0).getBlock().getType().equals(Material.WOOD_STEP)) {
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 1, -1), prop, Material.WOOD_STEP, (byte) 0);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 1, -1), prop, Material.WOOD_STEP, (byte) 8);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 2, 0), prop, Material.WOOD_STEP, (byte) 0);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 2, 1), prop, Material.WOOD_STEP, (byte) 8);
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 3, 1), prop, Material.WOOD_STEP, (byte) 0);
	    					
	    					} else if(loc.clone().add(0, 3, 1).getBlock().getType().equals(Material.WOOD_STEP)) {
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 1, 0), prop, Material.WOOD_STEP, (byte) 0);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 1, -1), prop, Material.WOOD_STEP, (byte) 8);
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 2, -1), prop, Material.WOOD_STEP, (byte) 0);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 2, -1), prop, Material.WOOD_STEP, (byte) 8);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 3, 0), prop, Material.WOOD_STEP, (byte) 0);
	    						
	    					} else {
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 1, 0), prop, Material.WOOD_STEP, (byte) 0);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 1, 1), prop, Material.WOOD_STEP, (byte) 8);
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 2, 1), prop, Material.WOOD_STEP, (byte) 0);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 2, 1), prop, Material.WOOD_STEP, (byte) 8);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 3, 0), prop, Material.WOOD_STEP, (byte) 0);
	    					}
	    					Building.fm.saveFoundation(foundation);
	    					Building.playerBuildLoc.remove(player.getName());
	    	    } else {
	    	    	player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
	    	    }
	        	
	    	} else if(prop.equalsIgnoreCase("Ceiling")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
						if(player.getItemInHand().getAmount() > 1) {
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
						} else {
							player.setItemInHand(null);
						}
						player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
							
						for(String all : Building.playerBlocks.get(player.getName())) {
							//if(!all.getBlock().getType().isSolid()) {
								foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.WOOD_STEP, (byte) 8);
							//}
						}
						Building.fm.saveFoundation(foundation);	
						Building.playerBuildLoc.remove(player.getName());
			    } else {
			    	player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
			    }
	    		
	    		//METALHERE//-----------------------------------------------------------------------------------------------------------------------------------------------------------
	    		
	    	} else if(prop.equalsIgnoreCase("MetalPillar")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
		    		if(foundation != null && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid()
		    				) {
		    			
		    			if(getRelativePropLocation(loc, foundation).getBlockX() % 2 == 0 && getRelativePropLocation(loc, foundation).getBlockZ() % 2 == 0) {
							if(player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							} else {
								player.setItemInHand(null);
							}
							player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
							
							foundation.addProp(player.getWorld(), loc.clone().add(0, 1, 0), prop, Material.QUARTZ_BLOCK, (byte) 0);
							
							for(String all : Building.playerBlocks.get(player.getName())) {
								foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.QUARTZ_BLOCK, (byte) 0);
							}
							
							Building.fm.saveFoundation(foundation);
							Building.playerBuildLoc.remove(player.getName());
							
		    			} else {
				    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
				    	}
			    	} else {
			    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
			    	}
		    	} else {
		    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
		    	}
	    		
	    	} else if(prop.equalsIgnoreCase("MetalWall")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
		    		if(foundation != null && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
							if(player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							} else {
								player.setItemInHand(null);
							}
							player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
							
							foundation.addProp(player.getWorld(), loc.clone().add(0, 1, 0), prop, Material.IRON_BLOCK, (byte) 0);
							
							for(String all : Building.playerBlocks.get(player.getName())) {
								foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.IRON_BLOCK, (byte) 0);
							}
							
							Building.fm.saveFoundation(foundation);
							Building.playerBuildLoc.remove(player.getName());
							
		    			} else {
				    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
				    	}
		    	} else {
		    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
		    	}
	    	
	    	} else if(prop.equalsIgnoreCase("MetalDoorway")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
		    		if(foundation != null && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
							if(player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							} else {
								player.setItemInHand(null);
							}
							player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
							
							for(String all : Building.playerBlocks.get(player.getName())) {
								foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.IRON_BLOCK, (byte) 0);
							}
							foundation.addProp(player.getWorld(), loc.clone().add(0, 2, 0), prop, Material.AIR, (byte) 0);
								
							Building.fm.saveFoundation(foundation);
							Building.playerBuildLoc.remove(player.getName());
		    			} else {
				    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
				    	}
		    	} else {
		    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
		    	}
	    	
	    	} else if(prop.equalsIgnoreCase("MetalWindow")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
		    		if(foundation != null && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
							if(player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							} else {
								player.setItemInHand(null);
							}
							player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
							
							for(String all : Building.playerBlocks.get(player.getName())) {
								foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.IRON_BLOCK, (byte) 0);
							}
							foundation.addProp(player.getWorld(), loc.clone().add(0, 3, 0), prop, Material.getMaterial(44), (byte) 15);
							foundation.addProp(player.getWorld(), loc.clone().add(0, 2, 0), prop, Material.AIR, (byte) 0);
							foundation.addProp(player.getWorld(), loc.clone().add(0, 1, 0), prop, Material.getMaterial(44), (byte) 7);
							
							Building.fm.saveFoundation(foundation);
							Building.playerBuildLoc.remove(player.getName());
		    			} else {
				    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
				    	}
		    	} else {
		    		player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
		    	}
	    	
	    	} else if(prop.equalsIgnoreCase("MetalStair")) {
	        	if(Building.playerBuildLoc.containsKey(player.getName())) {
	    					if(player.getItemInHand().getAmount() > 1) {
	    						player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
	    					} else {
	    						player.setItemInHand(null);
	    					}
	    					player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);

	    					for(String all : Building.playerBlocks.get(player.getName())) {
	    						foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.getMaterial(44), (byte) 15);
	    					}
	    							
	    					foundation.addProp(player.getWorld(), loc.clone().add(0, 1, 0), prop, Material.IRON_BLOCK, (byte) 0);
	    					foundation.addProp(player.getWorld(), loc.clone().add(0, 2, 0), prop, Material.IRON_BLOCK, (byte) 0);
	    					foundation.addProp(player.getWorld(), loc.clone().add(0, 3, 0), prop, Material.IRON_BLOCK, (byte) 0);
	    					
	    					if(loc.clone().add(-1, 3, 0).getBlock().getType().equals(Material.getMaterial(44))) {
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 1, 1), prop, Material.getMaterial(44), (byte) 7);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 1, 1), prop, Material.getMaterial(44), (byte) 15);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 2, 0), prop, Material.getMaterial(44), (byte) 7);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 2, -1), prop, Material.getMaterial(44), (byte) 15);
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 3, -1), prop, Material.getMaterial(44), (byte) 7);
	    						
	    					} else if(loc.clone().add(1, 3, 0).getBlock().getType().equals(Material.getMaterial(44))) {
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 1, -1), prop, Material.getMaterial(44), (byte) 7);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 1, -1), prop, Material.getMaterial(44), (byte) 15);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 2, 0), prop, Material.getMaterial(44), (byte) 7);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 2, 1), prop, Material.getMaterial(44), (byte) 15);
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 3, 1), prop, Material.getMaterial(44), (byte) 7);
	    					
	    					} else if(loc.clone().add(0, 3, 1).getBlock().getType().equals(Material.getMaterial(44))) {
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 1, 0), prop, Material.getMaterial(44), (byte) 7);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 1, -1), prop, Material.getMaterial(44), (byte) 15);
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 2, -1), prop, Material.getMaterial(44), (byte) 7);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 2, -1), prop, Material.getMaterial(44), (byte) 15);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 3, 0), prop, Material.getMaterial(44), (byte) 7);
	    						
	    					} else {
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 1, 0), prop, Material.getMaterial(44), (byte) 7);
	    						foundation.addProp(player.getWorld(), loc.clone().add(-1, 1, 1), prop, Material.getMaterial(44), (byte) 15);
	    						foundation.addProp(player.getWorld(), loc.clone().add(0, 2, 1), prop, Material.getMaterial(44), (byte) 7);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 2, 1), prop, Material.getMaterial(44), (byte) 15);
	    						foundation.addProp(player.getWorld(), loc.clone().add(1, 3, 0), prop, Material.getMaterial(44), (byte) 7);
	    					}
	    					Building.fm.saveFoundation(foundation);
	    					Building.playerBuildLoc.remove(player.getName());
	    	    } else {
	    	    	player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
	    	    }
	       
	    	} else if(prop.equalsIgnoreCase("MetalCeiling")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
						if(player.getItemInHand().getAmount() > 1) {
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
						} else {
							player.setItemInHand(null);
						}
						player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
							
						for(String all : Building.playerBlocks.get(player.getName())) {
							//if(!all.getBlock().getType().isSolid()) {
								foundation.addProp(player.getWorld(), new SimpleLocation(all).toLocation(), prop, Material.getMaterial(44), (byte) 15);
							//}
						}
						Building.fm.saveFoundation(foundation);	
						Building.playerBuildLoc.remove(player.getName());
			    } else {
			    	player.sendMessage(ChatColor.RED + "Invalid Prop Placement!");
			    }
	    	
	    		// ----------------------------- CHEST
	    		
	    	} else if(prop.equalsIgnoreCase("Chest1")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
					if(player.getItemInHand().getAmount() > 1) {
						player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
					} else {
						player.setItemInHand(null);
					}
					
					player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
					
					for(String locs : Building.playerBlocks.get(player.getName())) {
						new SimpleLocation(locs).toLocation().add(0, 1, 0).getBlock().setType(Material.CHEST);
					}
					
					Building.playerBuildLoc.remove(player.getName());
		    	} else {
		    		player.sendMessage(ChatColor.RED + "Invalid placement.");
		    	}
	    	
	    	} else if(prop.equalsIgnoreCase("Chest2")) {
	    		if(Building.playerBuildLoc.containsKey(player.getName())) {
					if(player.getItemInHand().getAmount() > 1) {
						player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
					} else {
						player.setItemInHand(null);
					}
					
					player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
					
					for(String locs : Building.playerBlocks.get(player.getName())) {
						new SimpleLocation(locs).toLocation().add(0, 1, 0).getBlock().setType(Material.CHEST);
						
						if(PlayerUtils.getPlayerDirection(player).equalsIgnoreCase("e")) {
							new SimpleLocation(locs).toLocation().add(0, 1, 0).getBlock().setData((byte) 4);
						} else if(PlayerUtils.getPlayerDirection(player).equalsIgnoreCase("n")) {
							new SimpleLocation(locs).toLocation().add(0, 1, 0).getBlock().setData((byte) 3);
						} else if(PlayerUtils.getPlayerDirection(player).equalsIgnoreCase("w")) {
							new SimpleLocation(locs).toLocation().add(0, 1, 0).getBlock().setData((byte) 5);
						} else {
							new SimpleLocation(locs).toLocation().add(0, 1, 0).getBlock().setData((byte) 0);
						}
					}
					
					Building.playerBuildLoc.remove(player.getName());
		    	} else {
		    		player.sendMessage(ChatColor.RED + "Invalid placement.");
		    	}
	    	}
		}
    }
	
	public Vector getRelativePropLocation(Location loc, Foundation f) {
		if(loc != null && f != null) {
			return new Vector(loc.getBlockX() - f.getLocation().getBlockX(), loc.getBlockY() - f.getLocation().getBlockY(), loc.getBlockZ() - f.getLocation().getBlockZ());
		}
		return null;
	}
	
	public String getWallOrientation(String cord) {
		if(cord.equals("N") || cord.equals("S")) {
			return "U";
		} else {
			return "S";
		}
	}
}
