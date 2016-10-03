package com.swingsword.ssengine.game.games.hub.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;

import com.swingsword.ssengine.utils.ConfigUtils;

public class PortalUtils {
	
	public static Map<String, String> portalData = new HashMap<String, String>();
	
	public static void loadPortals(){
		try {
			for (String key : ConfigUtils.getConfig("cache").getConfigurationSection("portals").getKeys(false)) {
	    	    String value = ConfigUtils.getConfig("cache").getString("portals." + key);
	    	    portalData.put("portals." + key, value);
	    	}
	    } catch(NullPointerException e) { }
	}
	
	public static void savePortalsData() {
		FileConfiguration config = ConfigUtils.getConfig("cache");
		for (Entry<String, String> entry : portalData.entrySet()) {
			config.set("portals." + entry.getKey(), entry.getValue());
		}
		ConfigUtils.saveConfig(config, "cache");
	}
}
