package com.swingsword.ssengine.game.games.rust.building;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.utils.ConfigUtils;

public class ConfigManager {
	
	public ArrayList<FileConfiguration> configs = new ArrayList<FileConfiguration>();
	
	public void loadConfigs() {
		for(File files : new File(MasterPlugin.getMasterPlugin().getDataFolder().getAbsolutePath() + "/data").listFiles()) {
			if(files.getName().contains("container")) {
				configs.add(YamlConfiguration.loadConfiguration(files));
			}
		}
		
		if(configs.size() == 0) {
			FileConfiguration config = ConfigUtils.createConfig("data/container1");
			configs.add(config);
		}
		
		if(new File(MasterPlugin.getMasterPlugin().getDataFolder().getAbsolutePath() + "/data/container" + (configs.size() - 1)).length() > 1000000) {
			FileConfiguration config = ConfigUtils.createConfig("data/container" + (configs.size() + 1));
			configs.add(config);
		}
	}
}
