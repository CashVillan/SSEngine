package com.swingsword.ssengine.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

import com.swingsword.ssengine.MasterPlugin;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.EntityTracker;
import net.minecraft.server.v1_10_R1.EnumDifficulty;
import net.minecraft.server.v1_10_R1.EnumGamemode;
import net.minecraft.server.v1_10_R1.IDataManager;
import net.minecraft.server.v1_10_R1.MinecraftServer;
import net.minecraft.server.v1_10_R1.ServerNBTManager;
import net.minecraft.server.v1_10_R1.WorldData;
import net.minecraft.server.v1_10_R1.WorldManager;
import net.minecraft.server.v1_10_R1.WorldServer;
import net.minecraft.server.v1_10_R1.WorldSettings;
import net.minecraft.server.v1_10_R1.WorldType;

public class WorldUtils {
	
	public static World loadWorld(String targetWorldFolderName) {
		WorldCreator wc = new WorldCreator(targetWorldFolderName);
		World world = wc.createWorld();
		
		return world;
	}
	
	public static void copyWorld(File clipboard, String targetWorldFolderName) {
		File worldFolder = new File(MasterPlugin.getServerDirectory().getAbsolutePath() + "/" + targetWorldFolderName);
		if(!worldFolder.exists()) {
			worldFolder.mkdir();
		}
		FileUtils.pasteFile(clipboard, worldFolder, new ArrayList<String>(Arrays.asList("uid.dat", "session.dat")));
	}
	
    @SuppressWarnings("deprecation")
	public static World createWorld(WorldCreator creator, File dir) {
        Validate.notNull(creator, "Creator may not be null");

        String name = creator.name();
        ChunkGenerator generator = creator.generator();
        World world = Bukkit.getWorld(name);
        WorldType type = WorldType.getType(creator.type().getName());
        boolean generateStructures = creator.generateStructures();

        if (world != null) {
            return world;
        }

        if ((dir.exists()) && (!dir.isDirectory())) {
            throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
        }

        if (generator == null) {
            generator = getGenerator(name);
        }
        
        System.out.println("########### 77");

        int dimension = CraftWorld.CUSTOM_DIMENSION_OFFSET + MinecraftServer.getServer().worlds.size();
        boolean used = false;
        do {
            for (WorldServer server : MinecraftServer.getServer().worlds) {
                used = server.dimension == dimension;
                if (used) {
                    dimension++;
                    break;
                }
            }
        } while(used);
        boolean hardcore = false;

        System.out.println("########### 92");
        
        IDataManager sdm = new ServerNBTManager(dir, name, true, MinecraftServer.getServer().getDataConverterManager());
        WorldData worlddata = sdm.getWorldData();
        WorldSettings worldSettings = null;
        if (worlddata == null) {
            worldSettings = new WorldSettings(creator.seed(), EnumGamemode.getById(Bukkit.getDefaultGameMode().getValue()), generateStructures, hardcore, type);
            worldSettings.setGeneratorSettings(creator.generatorSettings());
            worlddata = new WorldData(worldSettings, name);
        }
        worlddata.checkName(name); // CraftBukkit - Migration did not rewrite the level.dat; This forces 1.8 to take the last loaded world as respawn (in this case the end)
        WorldServer internal = (WorldServer) new WorldServer(MinecraftServer.getServer(), sdm, worlddata, dimension, MinecraftServer.getServer().methodProfiler, creator.environment(), generator).b();

        /*if (!(MinecraftServer.getServer().worlds.contains(name.toLowerCase(java.util.Locale.ENGLISH)))) {
            return null;
        }*/

        if (worldSettings != null) {
            internal.a(worldSettings);
        }
        
        System.out.println("########### 113");
        
        internal.scoreboard = MinecraftServer.getServer().server.getScoreboardManager().getMainScoreboard().getHandle();

        internal.tracker = new EntityTracker(internal);
        internal.addIWorldAccess(new WorldManager(MinecraftServer.getServer(), internal));
        internal.worldData.setDifficulty(EnumDifficulty.EASY);
        internal.setSpawnFlags(true, true);
        MinecraftServer.getServer().worlds.add(internal);

        if (generator != null) {
            internal.getWorld().getPopulators().addAll(generator.getDefaultPopulators(internal.getWorld()));
        }

        System.out.println("########### 127");
        
        Bukkit.getPluginManager().callEvent(new WorldInitEvent(internal.getWorld()));
        System.out.print("Preparing start region for level " + (MinecraftServer.getServer().worlds.size() - 1) + " (Seed: " + internal.getSeed() + ")");

        if (internal.getWorld().getKeepSpawnInMemory()) {
            short short1 = 196;
            long i = System.currentTimeMillis();
            for (int j = -short1; j <= short1; j += 16) {
                for (int k = -short1; k <= short1; k += 16) {
                    long l = System.currentTimeMillis();

                    if (l < i) {
                        i = l;
                    }

                    if (l > i + 1000L) {
                        int i1 = (short1 * 2 + 1) * (short1 * 2 + 1);
                        int j1 = (j + short1) * (short1 * 2 + 1) + k + 1;

                        System.out.println("Preparing spawn area for " + name + ", " + (j1 * 100 / i1) + "%");
                        i = l;
                    }

                    BlockPosition chunkcoordinates = internal.getSpawn();
                    internal.getChunkProviderServer().getChunkAt(chunkcoordinates.getX() + j >> 4, chunkcoordinates.getZ() + k >> 4);
                }
            }
        }
        Bukkit.getPluginManager().callEvent(new WorldLoadEvent(internal.getWorld()));
        return internal.getWorld();
    }
    
	private static ChunkGenerator getGenerator(String world) {
		ConfigurationSection section = YamlConfiguration.loadConfiguration(new File(Bukkit.getWorldContainer().getAbsolutePath() + "/bukkit.yml")).getConfigurationSection("worlds");
		ChunkGenerator result = null;

		if (section != null) {
			section = section.getConfigurationSection(world);

			if (section != null) {
				String name = section.getString("generator");

				if ((name != null) && (!name.equals(""))) {
					String[] split = name.split(":", 2);
					String id = (split.length > 1) ? split[1] : null;
					Plugin plugin = Bukkit.getPluginManager().getPlugin(split[0]);

					if (plugin == null) {
						Bukkit.getLogger().severe("Could not set generator for default world '" + world + "': Plugin '"
								+ split[0] + "' does not exist");
					} else if (!plugin.isEnabled()) {
						Bukkit.getLogger().severe("Could not set generator for default world '" + world + "': Plugin '"
								+ plugin.getDescription().getFullName() + "' is not enabled yet (is it load:STARTUP?)");
					} else {
						try {
							result = plugin.getDefaultWorldGenerator(world, id);
							if (result == null) {
								Bukkit.getLogger().severe("Could not set generator for default world '" + world + "': Plugin '"
										+ plugin.getDescription().getFullName() + "' lacks a default world generator");
							}
						} catch (Throwable t) {
							plugin.getLogger().log(Level.SEVERE, "Could not set generator for default world '" + world
									+ "': Plugin '" + plugin.getDescription().getFullName(), t);
						}
					}
				}
			}
		}

		return result;
	}
	
    /*public static boolean unloadWorld(World world) {
        if (world == null) {
            return false;
        }

        WorldServer handle = ((CraftWorld) world).getHandle();

        
        for (WorldServer world2 : MinecraftServer.getServer().worlds) {
        	System.out.println("World '" + world2.getWorldData().getName() + "' loaded!");
        }
        
        if (!(MinecraftServer.getServer().worlds.contains(handle))) {
            return false;
        }

        if (!(handle.dimension > 1)) {
            return false;
        }

        System.out.println("3");
        
        if (handle.players.size() > 0) {
            return false;
        }

        WorldUnloadEvent e = new WorldUnloadEvent(handle.getWorld());
        Bukkit.getPluginManager().callEvent(e);

        MinecraftServer.getServer().worlds.remove(world.getName().toLowerCase(java.util.Locale.ENGLISH));
        MinecraftServer.getServer().worlds.remove(MinecraftServer.getServer().worlds.indexOf(handle));
        
        for (World world2 : Bukkit.getServer().getWorlds()) {
        	System.out.println("### World '" + world2.getName() + "' loaded! ###");
        }
        return true;
    }*/
	
    public static void unloadWorld(World world) {
        for (WorldServer world2 : MinecraftServer.getServer().worlds) {
        	System.out.println("World '" + world2.getWorldData().getName() + "' loaded!");
        }
        
        Bukkit.unloadWorld(world, false);
        
        for (World world2 : Bukkit.getServer().getWorlds()) {
        	System.out.println("### World '" + world2.getName() + "' loaded! ###");
        }
    }
}
