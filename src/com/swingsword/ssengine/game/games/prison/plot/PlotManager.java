package com.swingsword.ssengine.game.games.prison.plot;

import java.io.File;
import java.util.HashMap;
import com.swingsword.ssengine.MasterPlugin;

public class PlotManager {
	
	private static File plotDir;
	public static HashMap<String, Plot> loadedPlots = new HashMap<>();
	
	public static void clearPlots() {
		for (Plot plots : loadedPlots.values()) {
			plots.end();
		}
	}

	public static File getPlotDirectory() {
		if (plotDir != null) {
			return plotDir;
		} else {
			File dir = MasterPlugin.getDataDirectory();
			for (File files : dir.listFiles()) {
				if (files.isDirectory() && files.getName().equals("prison_plots")) {
					plotDir = files;
				}
			}
		}
		return plotDir;
	}
}