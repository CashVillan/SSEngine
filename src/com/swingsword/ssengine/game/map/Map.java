package com.swingsword.ssengine.game.map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.utils.WorldUtils;

public class Map {

	public String name;
	public File directory;
	public World world;
	public WorldBorder border = null;
	private FileConfiguration mapConfig = null;
	
	public Map(String name, File directory) {
		this.name = name;
		this.directory = directory;
	}
	
	public Map createMap(final String targetWorldFolderName) {
		WorldUtils.copyWorld(directory, targetWorldFolderName);
		
		this.world = WorldUtils.loadWorld(targetWorldFolderName);
		loadMapConfig();
		
		System.out.println("[SSMinigame] Created map: '" + name.toLowerCase() + "'.");
		return this;
	}
	
	public Map loadMap(final String targetWorldFolderName) {
		this.world = WorldUtils.loadWorld(targetWorldFolderName);
		loadMapConfig();
		
		System.out.println("[SSMinigame] Loaded map: '" + name.toLowerCase() + "'.");
		return this;
	}
	
	private void loadMapConfig() {
		for(File all : directory.listFiles()) {
			if(all.getName().startsWith("config")) {
				mapConfig = YamlConfiguration.loadConfiguration(all);
				
				if(mapConfig.contains("border")) {
					border = new WorldBorder(mapConfig.getString("border"));
				}
			}
		}
	}
	
	public FileConfiguration getMapConfig() {
		return mapConfig;
	}
	
	public void saveMapConfig() {
		if(mapConfig != null) {
			for(File all : directory.listFiles()) {
				if(all.getName().startsWith("config")) {
					try {
						mapConfig.save(all);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
			}
		}
	}
	
	private static File getMapCacheFolder() {
		File dir = MasterPlugin.getServerDirectory();
		
		for (int x = 0; x < 5; x++) {
			if (x != 0) {
				dir = dir.getParentFile();
			}
			for (File files : dir.listFiles()) {
				if (files.isDirectory() && files.getName().equals("maps")) {
					return files;
				}
			}
		}
		return null;
	}
	
	public static ArrayList<Map> getMaps(String gamemode) {
		ArrayList<Map> maps = new ArrayList<Map>();
		for(File games : getMapCacheFolder().listFiles()) {
			System.out.println(games.getAbsolutePath());
			if(games.isDirectory() && games.getName().equals(gamemode)) {
				for(File map : games.listFiles()) {
					if(map.isDirectory()) {
						System.out.println(games.getAbsolutePath());
						maps.add(new Map(map.getName(), map));
					}
				}
			}
		}
		return maps;
	}
	
	public static Map getMap(String gamemode, String name) {
		for(File games : getMapCacheFolder().listFiles()) {
			if(games.isDirectory() && games.getName().equals(gamemode)) {
				for(File map : games.listFiles()) {
					if(map.isDirectory() && map.getName().equals(name)) {						
						return new Map(name, map);
					}
				}
			}
		}
		return null;
	}
}
