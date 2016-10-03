package com.swingsword.ssengine.utils;

import org.bukkit.configuration.file.FileConfiguration;

public class OptionsUtils {
	
	public static void loadOptions() {
		FileConfiguration config = ConfigUtils.getConfig("config");
		if (Boolean.getBoolean(config.get("options.weather") + "") == false) {
			
		}
		if (Boolean.getBoolean(config.get("options.pvp") + "") == false) {
			
		}
		if (Boolean.getBoolean(config.get("options.building") + "") == false) {
			
		}
		if (Boolean.getBoolean(config.get("options.natural-mobs") + "") == false) {
			
		}
	}

}
