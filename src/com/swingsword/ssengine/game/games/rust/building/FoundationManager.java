package com.swingsword.ssengine.game.games.rust.building;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.game.games.rust.utils.PlayerUtils;
import com.swingsword.ssengine.game.games.rust.utils.SimpleLocation;
import com.swingsword.ssengine.utils.ConfigUtils;

public class FoundationManager {
    
    private ArrayList<Foundation> foundations = new ArrayList<Foundation>();
    
    @SuppressWarnings("deprecation")
	public HashMap<String, MaterialData> previewFoundation(Player player, Location loc, Material mate) {
    	if(Building.canBuildThere(loc)) {
	    	Location savedLoc = loc.clone();
	    	HashMap<String, MaterialData> previewBlocks = new HashMap<String, MaterialData>();
	    	
	    	if(mate == Material.WOOD) {
				ArrayList<String> locs = new ArrayList<String>();
				
				byte info = (byte) 0;
				if(loc.getBlock().getType() == Material.GRASS && loc.clone().add(0, 0, 0).getBlock().getType().isSolid() || loc.getBlock().getType() == Material.STONE && loc.clone().add(0, 0, 0).getBlock().getType().isSolid() || loc.getBlock().getType() == Material.WOOD && Building.fm.isFoundation(loc) && loc.clone().add(0, 0, 0).getBlock().getType().isSolid()) {
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
					
					for(int x = -2; x < 3; x = x + 0) {
						for(int y = -2; y < 1; y = y + 0) {
							for(int z = -2; z < 3; z = z + 0) {
								Location loc1 = loc.clone().add(x, y, z);
								
								if(Building.fm.getFoundation(loc1) == null) {
									//if((y == 0 && x == 2 || y == 0 && x == -2) && (y == 0 && z == 2 || y == 0 && z == -2)) {
									if(y == 0) {
									//if(y == 0 && x == 2 || y == 0 && x == -2 || y == 0 && z == 2 || y == 0 && z == -2) {
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
											previewBlocks.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										//}
									}
									locs.add(new SimpleLocation(loc1).toString());
								}
								
								if(info == (byte) 5) {
									if(Building.fm.getFoundations().size() > 0) {			
										for(Foundation all : Building.fm.getFoundations()) {
											if(reset == false) {
												if(//all.getOldBlocks().contains(loc1) && loc1.getBlock().getType() == Material.WOOD || 
														all.getOldBlocks().containsKey(new SimpleLocation(loc).toString()) && loc.getBlock().getType() == Material.WOOD) {
													
													for(String loc2 : previewBlocks.keySet()) {
														//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
													}
													locs.clear();
													x = -2;
													y = -2;
													z = -2;
													info = (byte) 9;
													reset = true;
								
													if(PlayerUtils.getPlayerDirection(player) == "N") {
														loc = all.getLocation().clone().add(0, 0, -4);
														
													} else if(PlayerUtils.getPlayerDirection(player) == "S") {
														loc = all.getLocation().clone().add(0, 0, 4);
														
													} else if(PlayerUtils.getPlayerDirection(player) == "E") {
														loc = all.getLocation().clone().add(4, 0, 0);
														
													} else if(PlayerUtils.getPlayerDirection(player) == "W") {
														loc = all.getLocation().clone().add(-4, 0, 0);
													}
													
													if(!loc.getBlock().getType().isSolid()) {
														for(String loc2 : previewBlocks.keySet()) {
															//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
														}
														locs.clear();
														x = -2;
														y = -2;
														z = -2;
														info = (byte) 14;
														reset = true;
													}
												}
											}
										}
									}
								}
								
								if(info == (byte) 5 || info == (byte) 9) {
									if(isNearbyFoundation(loc, Building.fm.getFoundation(savedLoc))) {
										for(String loc2 : previewBlocks.keySet()) {
											//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
										}
										locs.clear();
										x = -2;
										y = -2;
										z = -2;
										info = (byte) 14;
										reset = true;
									}
								}
								if(info == (byte) 5) {
									if(y == 0) {
										if(loc1.getBlock().getRelative(0, 1, 0).getType().isSolid()) {
											for(String loc2 : previewBlocks.keySet()) {
												//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
											}
											locs.clear();
											x = -2;
											y = -2;
											z = -2;
											info = (byte) 14;
											reset = true;
										}
									}
								}
								if(info == (byte) 5) {
									if(y == 0) {
										if(loc1.getBlock().getRelative(0, 0, 0).getType() == Material.AIR && info != (byte) 14) {
											for(String loc2 : previewBlocks.keySet()) {
												//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
											}
											locs.clear();
											x = -2;
											y = -2;
											z = -2;
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
				
	    	} else if(mate == Material.IRON_BLOCK) {
	    		ArrayList<String> locs = new ArrayList<String>();
				
				byte info = (byte) 0;
				if(loc.getBlock().getType() == Material.GRASS && loc.clone().add(0, 0, 0).getBlock().getType().isSolid() || loc.getBlock().getType() == Material.STONE && loc.clone().add(0, 0, 0).getBlock().getType().isSolid() || loc.getBlock().getType() == Material.IRON_BLOCK && Building.fm.isFoundation(loc) && loc.clone().add(0, 0, 0).getBlock().getType().isSolid()) {
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
					
					for(int x = -2; x < 3; x = x + 0) {
						for(int y = -2; y < 1; y = y + 0) {
							for(int z = -2; z < 3; z = z + 0) {
								Location loc1 = loc.clone().add(x, y, z);
								
								if(Building.fm.getFoundation(loc1) == null) {
									//if(y == 0 && x == 2 || y == 0 && x == -2 || y == 0 && z == 2 || y == 0 && z == -2) {
									if(y == 0) {
									//if((y == 0 && x == 2 || y == 0 && x == -2) && (y == 0 && z == 2 || y == 0 && z == -2)) {
										//if(player.getLocation().distance(loc1) > 2) {
											//player.sendBlockChange(loc1, mat, info);
											previewBlocks.put(new SimpleLocation(loc1).toString(), new MaterialData(mat, info));
										//}
									}
									locs.add(new SimpleLocation(loc1).toString());
								}
								
								if(info == (byte) 5) {
									if(Building.fm.getFoundations().size() > 0) {			
										for(Foundation all : Building.fm.getFoundations()) {
											if(reset == false) {
												if(//all.getOldBlocks().contains(loc1) && loc1.getBlock().getType() == Material.WOOD || 
														all.getOldBlocks().containsKey(new SimpleLocation(loc).toString()) && loc.getBlock().getType() == Material.IRON_BLOCK) {
													for(String loc2 : previewBlocks.keySet()) {
														//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
													}
													locs.clear();
													x = -2;
													y = -2;
													z = -2;
													info = (byte) 9;
													reset = true;
								
													if(PlayerUtils.getPlayerDirection(player) == "N") {
														loc = all.getLocation().clone().add(0, 0, -4);
														
													} else if(PlayerUtils.getPlayerDirection(player) == "S") {
														loc = all.getLocation().clone().add(0, 0, 4);
														
													} else if(PlayerUtils.getPlayerDirection(player) == "E") {
														loc = all.getLocation().clone().add(4, 0, 0);
														
													} else if(PlayerUtils.getPlayerDirection(player) == "W") {
														loc = all.getLocation().clone().add(-4, 0, 0);
													}
													
													if(!loc.getBlock().getType().isSolid()) {
														for(String loc2 : previewBlocks.keySet()) {
															//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
														}
														locs.clear();
														x = -2;
														y = -2;
														z = -2;
														info = (byte) 14;
														reset = true;
													}
												}
											}
										}
									}
								}
								
								if(info == (byte) 5 || info == (byte) 9) {
									if(isNearbyFoundation(loc, Building.fm.getFoundation(savedLoc))) {
										for(String loc2 : previewBlocks.keySet()) {
											//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
										}
										locs.clear();
										x = -2;
										y = -2;
										z = -2;
										info = (byte) 14;
										reset = true;
									}
								}
								if(info == (byte) 5) {
									if(y == 0) {
										if(loc1.getBlock().getRelative(0, 1, 0).getType().isSolid()) {
											for(String loc2 : previewBlocks.keySet()) {
												//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
											}
											locs.clear();
											x = -2;
											y = -2;
											z = -2;
											info = (byte) 14;
											reset = true;
										}
									}
								}
								if(info == (byte) 5) {
									if(y == 0) {
										if(loc1.getBlock().getRelative(0, 0, 0).getType() == Material.AIR && info != (byte) 14) {
											for(String loc2 : previewBlocks.keySet()) {
												//player.sendBlockChange(new SimpleLocation(loc2).toLocation(), new SimpleLocation(loc2).getBlock().getTypeId(), new SimpleLocation(loc2).getBlock().getData());
											}
											locs.clear();
											x = -2;
											y = -2;
											z = -2;
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
	    	}
	    	
	    	return previewBlocks;
    	}
		return new HashMap<String, MaterialData>();
	}
    
	public void createFoundation(Player player, Location loc, Material mat) {
		if (Building.canBuildThere(loc)) {
			if (getFoundation(loc) == null) {
				HashMap<String, String> oldBlocks = new HashMap<String, String>();
				if (Building.playerBlocks.get(player.getName()) != null) {
					if (Building.playerBuildLoc.containsKey(player.getName())) {
						if (player.getItemInHand().getAmount() > 1) {
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
						} else {
							player.setItemInHand(null);
						}
						for (String loc1 : Building.playerBlocks.get(player.getName())) {
							oldBlocks.put(loc1, "Foundation");
							new SimpleLocation(loc1).getBlock().setType(mat);
						}
						Building.playerBlocks.remove(player.getName());
						player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0);
						player.sendMessage(ChatColor.GREEN + "Foundation Placed.");

						Foundation f = new Foundation(loc, oldBlocks);
						addFoundation(f);

						Building.fm.saveFoundation(f);
						Building.playerBuildLoc.remove(player.getName());

					} else {
						player.sendMessage(ChatColor.RED + "Invalid Foundation Placement!");
					}
				}
			} else {
				player.sendMessage(ChatColor.RED + "There is already a Foundation on that location.");
			}
		}		
	}
    
    public void addFoundation(Foundation f) {
    	foundations.add(f);
    }
    
    public void removeFoundation(Foundation f) {
    	foundations.remove(f);
    }
    
    public ArrayList<Foundation> getFoundations() {
    	return foundations;
    }
    
    public boolean isFoundation(Location loc) {
    	boolean isf = false;
    	
    	for(Foundation all : foundations) {
    		if(all.getOldBlocks().containsKey(new SimpleLocation(loc).toString()) && !all.getPropLocations().contains(new SimpleLocation(loc).toString())) {
    			isf = true;
    		}
    	}
    	
    	return isf;
    }
    
    public Foundation getFoundation(Location loc) {
    	for(Foundation all : foundations) {
    		if(all.getOldBlocks().containsKey(new SimpleLocation(loc).toString())) {
    			return all;
    		}
    	}
		return null;
    }
    
    public Foundation getPropFoundation(Location loc) {
    	for(Foundation all : getFoundations()) {
    		if(all.getPropLocations().contains(new SimpleLocation(loc).toString())) {
    			return all;
    		}
    	}
		return null;
    }
    
    public boolean isNearbyFoundation(Location loc, Foundation ignoref) {
    	ArrayList<Foundation> ignoreflist = new ArrayList<Foundation>();
    	
    	if(ignoref != null) {
    		ignoreflist.add(ignoref);
    		ignoreflist = getNearbyFoundations(ignoref.getLocation(), ignoreflist);
    	}
    	
    	if(loc != null) {
	    	for(Foundation all : getFoundations()) {
	    		if(all.getLocation().distance(loc) < 9 && !ignoreflist.contains(all)) {
	    			return true;
	    		}
	    	}
    	} 
    	return false;
    }
    
    public ArrayList<Foundation> getNearbyFoundations(Location loc, ArrayList<Foundation> foundations) {
    	if(getFoundation(loc.clone().add(4, 0, 0)) != null && !foundations.contains(getFoundation(loc.clone().add(4, 0, 0)))) {
    		foundations.add(getFoundation(loc.clone().add(4, 0, 0)));
    		foundations = getNearbyFoundations(loc.clone().add(4, 0, 0), foundations);
    	}
    	if(getFoundation(loc.clone().add(-4, 0, 0)) != null && !foundations.contains(getFoundation(loc.clone().add(-4, 0, 0)))) {
    		foundations.add(getFoundation(loc.clone().add(-4, 0, 0)));
    		foundations = getNearbyFoundations(loc.clone().add(-4, 0, 0), foundations);
    	}
    	if(getFoundation(loc.clone().add(0, 0, 4)) != null && !foundations.contains(getFoundation(loc.clone().add(0, 0, 4)))) {
    		foundations.add(getFoundation(loc.clone().add(0, 0, 4)));
    		foundations = getNearbyFoundations(loc.clone().add(0, 0, 4), foundations);
    	}
    	if(getFoundation(loc.clone().add(0, 0, -4)) != null && !foundations.contains(getFoundation(loc.clone().add(0, 0, -4)))) {
    		foundations.add(getFoundation(loc.clone().add(0, 0, -4)));
    		foundations = getNearbyFoundations(loc.clone().add(0, 0, -4), foundations);
    	}
    	
    	return foundations;
    }
    
    /*public void saveFoundation(Foundation f) {
	Building.getConfig().createSection("foundations." + Building.LocationToString(f.getLocation()));
	
	ArrayList<String> propList = new ArrayList<String>();
	for(Location all : f.getPropLocations()) {
		propList.add(Building.LocationToString(all));
	}
	
	Building.getConfig().set("foundations." + Building.LocationToString(f.getLocation()) + ".props", propList);
	
	HashMap<String, String> oldBlockList = new HashMap<String, String>();
	for(Location all : f.getOldBlocks().keySet()) {
		oldBlockList.put(Building.LocationToString(all), f.getOldBlocks().get(all));
	}
	for(String all : oldBlockList.keySet()) {
		Building.getConfig().set("foundations." + Building.LocationToString(f.getLocation()) + ".oldBlocks." + all, oldBlockList.get(all));
	}
	
	Building.saveConfig();
}

public void loadFoundation(Location loc) {
	HashMap<Location, String> oldBlocks = new HashMap<Location, String>();

	for(String all : Building.getConfig().getConfigurationSection("foundations." + Building.LocationToString(loc) + ".oldBlocks").getKeys(false)) {
		oldBlocks.put(Building.LocationFromString(all), Building.getConfig().getString("foundations." + Building.LocationToString(loc) + ".oldBlocks." + all));
	}
	
	Foundation f = new Foundation(loc, oldBlocks);
	foundations.add(f);
	
	for(String all : Building.getConfig().getStringList("foundations." + Building.LocationToString(loc) + ".props")) {
		f.loadProp(Building.LocationFromString(all));
	}
}*/
    
    public void saveFoundation(final Foundation f) {
    	Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
    		public void run() {
    			FileConfiguration config = null;
    	    	String configName = null;
    	    			
    	    	
    	    	for(int x = 0; x < Building.configmanager.configs.size(); x++) {
    	    		FileConfiguration all = Building.configmanager.configs.get(x);
    	    		if(all.contains("foundations." + f.location)) {
    	    			config = all;
    	    			configName = "data/container" + (x + 1);
    	    		}
    	    	}
    	    	if(config == null) {
    	    		if(new File(MasterPlugin.getMasterPlugin().getDataFolder().getAbsolutePath() + "/data/container" + (Building.configmanager.configs.size() - 1)).length() > 1000000) {
    	    			FileConfiguration newConfig = ConfigUtils.createConfig("data/container" + Building.configmanager.configs.size() + 1);
    	    			Building.configmanager.configs.add(newConfig);
    	    		}
    	    		
    	    		config = Building.configmanager.configs.get(Building.configmanager.configs.size() - 1);
    	    		configName = "data/container" + Building.configmanager.configs.size();
    	    	}
    	    	
    			config.createSection("foundations." + f.location);
    			
    			ArrayList<String> propList = new ArrayList<String>();
    			for(String all : f.getPropLocations()) {
    				propList.add(LocationUtils.RealLocationToString(new SimpleLocation(all).toLocation()));
    			}
    			
    			config.set("foundations." + LocationUtils.RealLocationToString(f.getLocation()) + ".props", propList);
    			
    			HashMap<String, String> oldBlockList = new HashMap<String, String>();
    			for(String all : f.getOldBlocks().keySet()) {
    				oldBlockList.put(LocationUtils.RealLocationToString(new SimpleLocation(all).toLocation()), f.getOldBlocks().get(all));
    			}
    			for(String all : oldBlockList.keySet()) {
    				config.set("foundations." + LocationUtils.RealLocationToString(f.getLocation()) + ".oldBlocks." + all, oldBlockList.get(all));
    			}
    			
    			ConfigUtils.saveConfig(config, configName);
    		}
    	});
	}
    
	public void loadFoundation(FileConfiguration config, Location loc) {
    	HashMap<String, String> oldBlocks = new HashMap<String, String>();
    	
    	if(config.contains("foundations." + LocationUtils.RealLocationToString(loc) + ".oldBlocks") && LocationUtils.RealLocationToString(loc) != null) {
	    	for(String all : config.getConfigurationSection("foundations." + LocationUtils.RealLocationToString(loc) + ".oldBlocks").getKeys(false)) {
	    		oldBlocks.put(new SimpleLocation(LocationUtils.RealLocationFromString(all)).toString(), config.getString("foundations." + LocationUtils.RealLocationToString(loc) + ".oldBlocks." + all));
	    	}
	    	
	    	Foundation f = new Foundation(loc, oldBlocks);
	    	foundations.add(f);
	    	
	    	for(String all : config.getStringList("foundations." + LocationUtils.RealLocationToString(loc) + ".props")) {
	    		f.loadProp(LocationUtils.RealLocationFromString(all));
	    	}
    	}
	}
}
