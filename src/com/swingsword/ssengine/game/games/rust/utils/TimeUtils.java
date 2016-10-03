package com.swingsword.ssengine.game.games.rust.utils;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.swingsword.ssengine.game.games.rust.Rust;

public class TimeUtils {
	
	int ticks = 1;
	
	class TimeTask implements Runnable {
		
		@SuppressWarnings("rawtypes")
		public void run() {
			Iterator serverWorlds = Bukkit.getServer().getWorlds().iterator();
			
			while (serverWorlds.hasNext()) {
				String worldToLoad = ((World) serverWorlds.next()).getName();
				double timeMultiplicator = 1.0;
				long currentTime = Bukkit.getServer().getWorld(worldToLoad).getTime();
				
				if(currentTime < 12000) {
					timeMultiplicator = Rust.getInstance().getLoadedMap().getMapConfig().getDouble("time." + worldToLoad + ".day");
				} else {
					timeMultiplicator = Rust.getInstance().getLoadedMap().getMapConfig().getDouble("time." + worldToLoad + ".night");
				}
				if(timeMultiplicator != 1.0) {
					double idealTimeToAdd = timeMultiplicator * ticks - ticks;
					long finalTimeToAdd = (long) idealTimeToAdd;
					double diffTimeToAdd = idealTimeToAdd - finalTimeToAdd;
	
					if (Math.random() < Math.abs(diffTimeToAdd)) {
						if (diffTimeToAdd > 0.0D)
							finalTimeToAdd += 1L;
						else
							finalTimeToAdd -= 1L;
					}
					
					Bukkit.getServer().getWorld(worldToLoad).setTime(currentTime - finalTimeToAdd);
				}
			}
		}
	}
}
