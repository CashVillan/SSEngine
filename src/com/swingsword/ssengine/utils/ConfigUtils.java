package com.swingsword.ssengine.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.swingsword.ssengine.MasterPlugin;


public class ConfigUtils {
	
	private static HashMap<String, FileConfiguration> configCache = new HashMap<String, FileConfiguration>();
	
	public static boolean configExists(String name) {
		return new File(MasterPlugin.getMasterPlugin().getDataFolder() + "/" + name + ".yml").exists();
	}
	
	public static FileConfiguration createConfig(String name) {
		System.out.println("[CONFIG] Creating a new config file... (Name: " + name + ")");
		
		File file = new File(MasterPlugin.getMasterPlugin().getDataFolder() + "/" + name + ".yml");
		copyInputStreamToFile(ConfigUtils.class.getClassLoader().getResourceAsStream("files/" + name + ".yml"), file);
		
		return YamlConfiguration.loadConfiguration(file);
	}

	public static FileConfiguration getConfig(String name) {
		if(configCache.containsKey(name)) {
			return configCache.get(name);
		}
		
		if (!new File(MasterPlugin.getMasterPlugin().getDataFolder(), "").exists()) {
			new File(MasterPlugin.getMasterPlugin().getDataFolder(), "").mkdir();
		}
		if (!configExists(name)) {
			createConfig(name);
		}

		File file = new File(MasterPlugin.getMasterPlugin().getDataFolder() + "/" + name + ".yml");

		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		configCache.put(name, config);
		
		return config;
	}
    
    public static void saveConfig(FileConfiguration config, String name) {
    	try {
    		config.save(new File(MasterPlugin.getMasterPlugin().getDataFolder() + "/" + name + ".yml"));
    	} catch (Exception e) { 
    		e.printStackTrace();
    	}
    }
    
    private static void copyInputStreamToFile(InputStream in, File file ) {
		int written = 0;
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	            written += 1;
	        }
	        out.close();
	        in.close();
	        
	        if(written == 0) {
	        	file.delete();
	        	file.deleteOnExit();
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
    
    public static void updateType(String type) {
    	System.out.println("[UPDATE] Refreshing " + type);
    	FileConfiguration config = getConfig("config");
    	for (String file : config.getConfigurationSection("files").getKeys(false)) {
    		if (config.contains("files." + file + ".type") && config.getString("files." + file + ".type").equals(type)) {
    			update(file, type);
    		}
    	}
    }

	@SuppressWarnings("deprecation")
	public static void update(final String name, String type) {
		FileConfiguration config = getConfig("config");
		if (!config.getStringList("installed").contains(name) || config.getStringList("update").contains(name)) {
			if (type.equals("jar")) {
				try {
					File file = new File("plugins/" + name + ".jar");
					URL website = new URL(config.getString("files." + name + ".http"));
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(file);

					System.out.println("[UPDATE] Updating " + name + ".jar");

					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
				} catch (Exception e) { }
			} else if (type.equals("cache")) {
				FileConfiguration cache = ConfigUtils.getConfig("cache");
				YamlConfiguration dlConfig = YamlConfiguration.loadConfiguration(StringUtils.getStream(config.getString("files." + name + ".http")));
				if (dlConfig.contains(name)) {
					if (dlConfig.isList(name)) {
						cache.set(name, dlConfig.getStringList(name));
					} else {
						cache.set(name, dlConfig.getConfigurationSection(name));
					}

					System.out.println("[UPDATE] Copying " + name + " to cache.yml");
					ConfigUtils.saveConfig(cache, "cache");
				}
			}
			if (config.getStringList("update").contains(name)) {
				List<String> update = config.getStringList("update");
				update.remove(name);
				config.set("update", update);
			}
			if (!config.getStringList("installed").contains(name)) {
				List<String> installed = config.getStringList("installed");
				installed.add(name);
				config.set("installed", installed);
			}
			
			ConfigUtils.saveConfig(config, "config");
		}
	}
}