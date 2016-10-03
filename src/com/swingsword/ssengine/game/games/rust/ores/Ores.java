package com.swingsword.ssengine.game.games.rust.ores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.games.rust.listeners.OresListener;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.utils.ConfigUtils;

public class Ores {
	
	public static OrePileHandler oph = new OrePileHandler();
	public static OresListener pl = new OresListener();
    public static ArrayList<Player> cooldown = new ArrayList<Player>();
    public static HashMap<Location, Integer> blockHealth = new HashMap<Location, Integer>();
    
	public static void saveOres() {
		List<OrePile> remove = new ArrayList<OrePile>();
		for (OrePile all : oph.orePiles) {
			remove.add(all);
		}
		for (OrePile all : remove) {
			oph.resetPile(all);
		}
	}

	public static void loadOres() {
		ConfigUtils.updateType("ores");
		Thread t = new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				World world = Bukkit.getWorld("map");

				for (Chunk chunk : world.getLoadedChunks()) {
					int bx = chunk.getX() << 4;
					int bz = chunk.getZ() << 4;

					for (int xx = bx; xx < bx + 16; xx++) {
						for (int zz = bz; zz < bz + 16; zz++) {
							for (int yy = 0; yy < 128; yy++) {
								Material mat = Material.getMaterial(world.getBlockTypeIdAt(xx, yy, zz));
								Material matAbove = Material.getMaterial(world.getBlockTypeIdAt(xx, yy + 1, zz));

								if (mat == Material.IRON_ORE || mat == Material.GOLD_ORE || mat == Material.LOG && world.getBlockAt(xx, yy + 1, zz).getData() == 3) {
									world.getBlockAt(xx, yy, zz).setType(Material.AIR);
									world.getBlockAt(xx, yy, zz).setData((byte) 0);

								} else if (mat == Material.DIRT && matAbove == Material.AIR) {
									world.getBlockAt(xx, yy, zz).setType(Material.GRASS);
								}
							}
						}
					}
				}
			}
		});
		t.run();

		if(ConfigUtils.getConfig("zones").contains("oreZones")) {
			for (String all : ConfigUtils.getConfig("zones").getConfigurationSection("oreZones").getKeys(false)) {
				com.sk89q.worldedit.Vector loc1 = LocationUtils.locationFromString(all.split(";")[0]);
				com.sk89q.worldedit.Vector loc2 = LocationUtils.locationFromString(all.split(";")[1]);
	
				CuboidRegion r = new CuboidRegion(loc1, loc2);
	
				doReplace(new Location(Bukkit.getWorld("map"), r.getMinimumPoint().getX(), r.getMinimumPoint().getY(), r.getMinimumPoint().getZ()), new Location(Bukkit.getWorld("map"), r.getMaximumPoint().getX(), r.getMaximumPoint().getY(), r.getMaximumPoint().getZ()));
	
				for (int x = 0; x < ConfigUtils.getConfig("zones").getInt("oreZones." + all); x++) {
					spawnRandomOre(r);
				}
			}
		}
	}

	public static void spawnRandomOre(CuboidRegion r) {
		Location loc = LocationUtils.getRandomLocation(r, Bukkit.getWorld("map"));

		if (loc.getBlock().getRelative(0, -1, 0).getType() == Material.GRASS) {
			int ra = new Random().nextInt(4);

			if (ra <= 1) {
				oph.createOrePile(loc, Material.LOG, r);
			} else if (ra == 2) {
				oph.createOrePile(loc, Material.GOLD_ORE, r);
			} else if (ra == 3) {
				oph.createOrePile(loc, Material.IRON_ORE, r);
			}

		} else {
			spawnRandomOre(r);
		}
	}

	@SuppressWarnings("deprecation")
	public static void doReplace(Location min, Location max) {
		for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
			for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					Block blk = min.getWorld().getBlockAt(new Location(min.getWorld(), x, y, z));
					if (blk.getType() == Material.IRON_ORE || blk.getType() == Material.GOLD_ORE || blk.getType() == Material.LOG && blk.getData() == 3) {
						blk.setType(Material.AIR);
					}
				}
			}
		}
	}

	public static void resetPiles(final CuboidRegion r, final int delay, final int space, final boolean last) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (all.isOp()) {
						all.sendMessage("Pile reset loop " + (delay / space) + " started.");
					}
				}

				doReplace(new Location(Bukkit.getWorld("map"), r.getMinimumPoint().getX(), 0, r.getMinimumPoint().getZ()), new Location(Bukkit.getWorld("map"), r.getMaximumPoint().getX(), Bukkit.getWorld("map").getMaxHeight(), r.getMaximumPoint().getZ()));

				for (Player all : Bukkit.getOnlinePlayers()) {
					if (all.isOp()) {
						all.sendMessage("Pile reset loop " + (delay / space) + " done.");
					}
				}

				if (last) {
					for (String all : ConfigUtils.getConfig("zones").getConfigurationSection("oreZones").getKeys(false)) {
						com.sk89q.worldedit.Vector loc1 = LocationUtils.locationFromString(all.split(";")[0]);
						com.sk89q.worldedit.Vector loc2 = LocationUtils.locationFromString(all.split(";")[1]);

						CuboidRegion r = new CuboidRegion(loc1, loc2);

						for (int x = 0; x < ConfigUtils.getConfig("zones").getInt("oreZones." + all); x++) {
							spawnRandomOre(r);
						}
					}

					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all.isOp()) {
							all.sendMessage("Finished pile reset.");
						}
					}
				}
			}
		}, delay);
	}
	
	@SuppressWarnings("deprecation")
	public static int getPower(Material mat, Block block) {
		if(getTool(mat) != null) {
			if(block.getType() == Material.LOG) {
				if(block.getData() == 3) {
					return GameManager.currentMap.getMapConfig().getInt("ores.log.oreDamage." + getTool(mat));
				}
			} else {
				return GameManager.currentMap.getMapConfig().getInt("ores." + block.getType().name().toLowerCase() + ".oreDamage." + getTool(mat));
			}
		}
		return -1;
	}
	
	public static String getTool(Material mat) {
		if(mat.name().toLowerCase().contains("wood_axe")) {
			return "ClayBall";
			
		} else if(mat.name().toLowerCase().contains("iron_pickaxe")) {
			return "IronPickaxe";
			
		} else if(mat.name().toLowerCase().contains("stone_axe")) {
			return "StoneAxe";
			
		} else if(mat.name().toLowerCase().contains("iron_axe")) {
			return "IronAxe";
		}
		return null;
	}
	
	public static String getRandomDrops(String type, String tool) {
		if(GameManager.currentMap.getMapConfig().getConfigurationSection("ores." + type.toLowerCase() + "." + tool) != null && GameManager.currentMap.getMapConfig().getConfigurationSection("ores." + type.toLowerCase() + "." + tool).getKeys(false).size() > 0) {
			return (String) GameManager.currentMap.getMapConfig().getConfigurationSection("ores." + type.toLowerCase() + "." + tool).getKeys(false).toArray()[(new Random().nextInt(GameManager.currentMap.getMapConfig().getConfigurationSection("ores." + type.toLowerCase() + "." + tool).getKeys(false).size()))];
	
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static int getBlockHealth(Location loc) {
		if(loc.getBlock().getType() == Material.STONE_SLAB2) {
			return 40;
			
		} else if(loc.getBlock().getType() == Material.STEP && loc.getBlock().getData() == 3) {
			return 20;
		
		} else if(loc.getBlock().getType() == Material.WOODEN_DOOR) {
			return 100;
		
		} else if(loc.getBlock().getType() == Material.ANVIL) {
			return 40;
		
		} else if(loc.getBlock().getType() == Material.CHEST) {
			return 15;
		
		} else if(loc.getBlock().getType() == Material.CARPET) {
			return 25;
		
		} else if(loc.getBlock().getType() == Material.BED_BLOCK) {
			return 30;
		}
		
		return -1;
	}
	
	public void runDelayBar(final Player player, final long time, final long progress, final int slot) {
    	Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
    			if(player.isOnline()) {
    				if(player.getInventory().getItem(slot) != null) {
						if(progress <= 0) {
							player.getInventory().getItem(slot).setDurability((short) 0);
							
						} else {
							player.getInventory().getItem(slot).setDurability((short) ((player.getInventory().getItem(slot).getType().getMaxDurability() * (float) ((float) progress / (float) time)) + 1));
							
							runDelayBar(player, time, progress - 1, slot);
						}
						
						player.updateInventory();
    				}
    			}
    		}
    	}, 1L);
    }
}