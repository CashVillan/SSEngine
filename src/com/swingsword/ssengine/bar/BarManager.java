package com.swingsword.ssengine.bar;

public class BarManager {
	
	private static int currentLine = 0;
	private static int ticksPassed = 0;
	
	public static String currentDisplay = "";
	
	/*public BarManager() {
		final FileConfiguration cache = ConfigUtils.getConfig("cache");
		
		if(cache.getStringList("bar").size() > 0) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					ticksPassed++;
					
					if(ticksPassed % 60 == 0) {
						if(cache.getStringList("bar").size() - 1 == currentLine) {
							currentLine = 0;
						} else {
							currentLine += 1;
						}
					}
					
					String line = cache.getStringList("bar").get(currentLine).replace("&", ChatColor.COLOR_CHAR + "");
					for(Player all : Bukkit.getOnlinePlayers()) {
						UtilTextTop.displayProgress(line, 1, all);
						currentDisplay = line;
					}
					
				}
			}, 1, 1);
		}
	}*/
}
