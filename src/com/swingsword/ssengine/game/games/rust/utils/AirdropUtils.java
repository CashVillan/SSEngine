package com.swingsword.ssengine.game.games.rust.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bar.BarManager;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.ItemUtils;

public class AirdropUtils {
	
    public static HashMap<Integer, Integer> dragonIntProgress = new HashMap<Integer, Integer>();
    public static HashMap<Integer, EnderDragon> dragonIntDragon = new HashMap<Integer, EnderDragon>();
	public static HashMap<String, Inventory> crates = new HashMap<String, Inventory>();
	public static HashMap<String, Integer> crateTimeout = new HashMap<String, Integer>();
	public static Location pos1 = null;
	public static Location pos2 = null;
	
	@SuppressWarnings("deprecation")
	public static void saveAirdrop() {
		for(String all : crates.keySet()) {
			new SimpleLocation(all).getBlock().setType(Material.AIR);
			new SimpleLocation(all).getBlock().setData((byte) 0);
		}
		
		for(World all : Bukkit.getWorlds()) {
			for(LivingEntity ent : all.getLivingEntities()) {
				if(ent.getType() == EntityType.ENDER_DRAGON) {
					ent.remove();
				}
			}
			
			for(Entity ent : all.getEntities()) {
				if(ent.getType() == EntityType.FALLING_BLOCK) {
					ent.remove();
				}
			}
		}
	}
	
	public static void loadAirdrops() {
		ConfigUtils.updateType("airdrop");
		final FileConfiguration mapConfig = GameManager.currentMap.getMapConfig();
		
		if(mapConfig.getString("airdrop.pos1.world") != null && mapConfig.getString("airdrop.pos2.world") != null) {
			pos1 = new Location(Bukkit.getWorld("map"), mapConfig.getInt("airdropp.pos1.x"), mapConfig.getInt("airdrop.pos1.y"), mapConfig.getInt("airdrop.pos1.z"));
			pos2 = new Location(Bukkit.getWorld("map"), mapConfig.getInt("airdrop.pos2.x"), mapConfig.getInt("airdrop.pos2.y"), mapConfig.getInt("airdrop.pos2.z"));
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				ArrayList<String> removeCrate = new ArrayList<String>();
				
				for(String all : crates.keySet()) {
					if(com.swingsword.ssengine.game.games.rust.utils.ItemUtils.getContentAmount(crates.get(all)) == 0) {
						ArrayList<HumanEntity> allEnt = new ArrayList<HumanEntity>();
						if(crates.get(all) != null && crates.get(all).getViewers() != null && crates.get(all).getViewers().size() > 0) {
							for(HumanEntity ent : crates.get(all).getViewers()) {
								allEnt.add(ent);
							}
							for(HumanEntity ent : allEnt) {
								ent.closeInventory();
							}
						}
						
						removeCrate.add(all);
					}
				}
				
				for(String all : removeCrate) {
					crates.remove(all);
					crateTimeout.remove(all);
					new SimpleLocation(all).getBlock().setType(Material.AIR);
					new SimpleLocation(all).getBlock().setData((byte) 0);
				}
				
				for(World world : Bukkit.getWorlds()) {
					for(Entity ent : world.getEntities()) {
						if(ent instanceof FallingBlock) {
							FallingBlock block = (FallingBlock) ent;
							
							if(block.getBlockId() == Material.WOOL.getId() && block.getBlockData() == 13) {
								if(ent.getTicksLived() == 550) {
									FallingBlock newEnt = ent.getWorld().spawnFallingBlock(ent.getLocation(), ((FallingBlock) ent).getBlockId(), ((FallingBlock) ent).getBlockData());
									
									ent.remove();
									ent = newEnt;
								}
								
								ent.setVelocity(new Vector(0, -0.02, 0));
								ent.setFallDistance(0);
							}	
							
						} else if(ent instanceof Item) {
							Item item = (Item) ent;
							
							if(item.getItemStack().getType() == Material.WOOL && item.getItemStack().getData().getData() == 13) {
								item.remove();
							}
						}
					}
				}
			}
		}, 1L, 1L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				ArrayList<String> removeCrates = new ArrayList<String>();
				
				for(String all : crateTimeout.keySet()) {
					if(crateTimeout.get(all) - 1 == 0) {
						removeCrates.add(all);
						
					} else {
						crateTimeout.put(all, crateTimeout.get(all) - 1);
					}
				}
				
				for(String all : removeCrates) {
					for(ItemStack item : crates.get(all).getContents()) {
						if(item != null) {
							new SimpleLocation(all).getWorld().dropItemNaturally(new SimpleLocation(all).toLocation(), item);
						}
					}
					
					crates.remove(all);
					crateTimeout.remove(all);
					new SimpleLocation(all).getBlock().setType(Material.AIR);
					new SimpleLocation(all).getBlock().setData((byte) 0);
				}
			}
		}, 20L, 20L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
        	@SuppressWarnings({ "deprecation" })
			public void run() {
        		ArrayList<Integer> deleteDragon = new ArrayList<Integer>();
        		
        		for(Integer all : dragonIntProgress.keySet()) {
	        		if(dragonIntProgress.get(all) != null) {
		        		if(dragonIntProgress.get(all) - 1 > 0) {
		        			Location oldLoc = dragonIntDragon.get(all).getLocation().clone();

		        			dragonIntDragon.get(all).remove();

		        			EnderDragon newDragon = (EnderDragon) oldLoc.getWorld().spawnEntity(new Location(oldLoc.getWorld(), oldLoc.getX(), oldLoc.getY(), oldLoc.getZ() - 0.5, 0, 0), EntityType.ENDER_DRAGON);
		        			newDragon.setRemoveWhenFarAway(false);
		        			dragonIntDragon.put(all, newDragon);
		        			
		        			newDragon.setCustomNameVisible(true);
		        			newDragon.setCustomName(BarManager.currentDisplay);
		        			
		        			dragonIntProgress.put(all, dragonIntProgress.get(all) - 1);
		        			
		        			if(dragonIntProgress.get(all) == 450 || dragonIntProgress.get(all) == 500 || dragonIntProgress.get(all) == 550) {
		        				if(dragonIntProgress.get(all) != 550) {
		        					newDragon.getWorld().loadChunk(newDragon.getLocation().getChunk());
			        				
			        				for(int x = 0; x <= 1; x++) {
			        					for(int y = 0; y <= 2; y++) {
			        						for(int z = 0; z <= 1; z++) {
			        							Location loc2 = new Location(newDragon.getWorld(), newDragon.getLocation().getX() + x, newDragon.getLocation().getY() + y, newDragon.getLocation().getZ() + z);
					        					FallingBlock block = newDragon.getWorld().spawnFallingBlock(loc2, 35, (byte) 13);
					        					block.setDropItem(false);
			        						}
			        					}
			        				}
			        				
		        				} else {
		        					Random r = new Random();
		        					int drop = r.nextInt(2);
		        					
		        					if(drop == 1) {
		        						for(int x = 0; x <= 1; x++) {
				        					for(int y = 0; y <= 2; y++) {
				        						for(int z = 0; z <= 1; z++) {
				        							Location loc2 = new Location(newDragon.getWorld(), newDragon.getLocation().getX() + x, newDragon.getLocation().getY() + y, newDragon.getLocation().getZ() + z);
						        					FallingBlock block = newDragon.getWorld().spawnFallingBlock(loc2, 35, (byte) 13);
						        					block.setDropItem(false);
				        						}
				        					}
				        				}
		        					}
		        				}
		        			}
		        			
		        		} else {
		        			deleteDragon.add(all);
		        		}
	        		}
        		}
        		
        		for(Integer all : deleteDragon) {
        			dragonIntProgress.remove(all);
        			dragonIntDragon.get(all).remove();
        			dragonIntDragon.remove(all);
        		}
        		
        		for(World world : Bukkit.getWorlds()) {
					for(Entity ent : world.getEntities()) {
						if(ent instanceof EnderDragon && !dragonIntDragon.containsValue(ent)) {
							ent.remove();
						}
					}
				}
        	}
        }, 1L, 1L);
		
		double random = Math.random();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(Bukkit.getOnlinePlayers().size() < mapConfig.getInt("airdrop.minimalPlayersNeeded")) {
					Bukkit.broadcastMessage(ChatColor.GRAY + "You must reach reach " + mapConfig.getInt("airdrop.minimalPlayersNeeded") + " players online to make an airdrop possible.");
				}
			}
		}, (long) (random * mapConfig.getInt("airdrop.minimalPlayersNeededBroadcastDelay")) * 20, mapConfig.getInt("airdrop.minimalPlayersNeededBroadcastDelay") * 20);
		
		if(pos1 != null && pos2 != null) { 
			Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					if(Bukkit.getOnlinePlayers().size() >= mapConfig.getInt("airdrop.minimalPlayersNeeded")) {
						Location dropLoc = null;
						Random rx = new Random();
						Random rz = new Random();
						int x = 0;
						int z = 0;
						
						x = rx.nextInt(LocationUtils.getXDifference(pos1, pos2)) + pos1.getBlockX();
						z = rz.nextInt(LocationUtils.getZDifference(pos1, pos2)) + pos1.getBlockZ();
						dropLoc = new Location(pos1.getWorld(), x, pos1.getWorld().getHighestBlockYAt(x, z), z);
						
						dropCrate(dropLoc);
						
						for(Player all : Bukkit.getOnlinePlayers()) {
							all.playSound(all.getLocation(), "custom.airdrop", 1, 1);
						}
					}
				}
			}, (long) (random * mapConfig.getInt("airdrop.randomDropDelay")) * 20, mapConfig.getInt("airdrop.randomDropDelay") * 20);
		}
	}
	
    @SuppressWarnings("deprecation")
	public static void createCrate(Location loc, Inventory inv, String name) {
    	if(loc.getBlock().getType() == Material.AIR) {
	    	loc.getBlock().setTypeId(54);
	    	Inventory crateInv = Bukkit.createInventory(null, inv.getSize(), ChatColor.DARK_GRAY + name);
	    	crateInv.setContents(inv.getContents());
			crates.put(new SimpleLocation(loc).toString(), crateInv);
			crateTimeout.put(new SimpleLocation(loc).toString(), 900);
			
    	} else {
    		for(ItemStack all : inv.getContents()) {
    			if(all != null) {
    				loc.getWorld().dropItemNaturally(loc, all);
    			}
    		}
    	}
    }
    
    @SuppressWarnings("deprecation")
	public static void dropCrate(final Location loc) {
    	loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.STEP_SOUND, 7, 30);
		EffectUtils.playFlareEffect(loc, 6);
        
		Bukkit.broadcastMessage(ChatColor.GREEN + "A supply plane is flying over the following location: X: " + loc.getBlockX() + "; Y: " + loc.getBlockY() + "; Z: " + loc.getBlockZ());
		
        Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
        	public void run() {
        		final EnderDragon dragon = (EnderDragon) loc.getWorld().spawnEntity(loc.clone().add(0, 50, 240), EntityType.ENDER_DRAGON);
        		dragon.setRemoveWhenFarAway(false);
        		
                final int dragonInt = dragonIntProgress.size() + 1;
                dragonIntProgress.put(dragonInt, 480 * 2);
                dragonIntDragon.put(dragonInt, dragon);
                
        		//setNoAI(dragon);
        	}
        }, 100L);
	}
    
	public static Inventory fillCrate(Location loc) {
    	Inventory crateInv = null;
    	FileConfiguration mapConfig = GameManager.currentMap.getMapConfig();
    	
    	if(crates.containsKey(new SimpleLocation(loc).toString())) {
    		crateInv = crates.get(new SimpleLocation(loc).toString());
    		
    	} else {
    		crateInv = Bukkit.createInventory(null, 27);
    	}
    		crateInv.clear();
    		
	    	String path = "loots";
	    	
	    	if(getRandomCrate(path) != null) {
		    	for(String all : mapConfig.getStringList("airdrop." + path + "." + getRandomCrate(path))) {
		    		crateInv.addItem(ItemUtils.itemStackFromString(all));
		    	}
	    	}
    		
    		if(com.swingsword.ssengine.game.games.rust.utils.ItemUtils.getContentAmount(crateInv) <= 0) {
    			fillCrate(loc);
    		}
    		
    	return crateInv;
    }
	
	public static String getRandomCrate(String path) {
		FileConfiguration mapConfig = GameManager.currentMap.getMapConfig();
		if(mapConfig.getConfigurationSection("airdrop." + path).getKeys(false).size() > 0) {
			return (String) mapConfig.getConfigurationSection("airdrop." + path).getKeys(false).toArray()[(new Random().nextInt(mapConfig.getConfigurationSection("airdrop." + path).getKeys(false).size()))];
	
		} else {
			return null;
		}
	}
	
	public static Location crateNear(Location loc) {
		for(String all : crates.keySet()) {
			if(new SimpleLocation(all).toLocation().distance(loc) <= 5) {
				return new SimpleLocation(all).toLocation();
			}
		}
		return null;
	}
}
