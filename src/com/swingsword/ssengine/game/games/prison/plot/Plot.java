package com.swingsword.ssengine.game.games.prison.plot;

import org.bukkit.World;
import org.bukkit.WorldCreator;

import com.swingsword.ssengine.utils.WorldUtils;

import net.minecraft.server.v1_10_R1.MinecraftServer;
import net.minecraft.server.v1_10_R1.WorldServer;

public class Plot {
	
	public static String uuid;
	public static World world;
	
	public Plot(String uuid) {
		this.uuid = uuid;
		load();
	}
	
	private Plot load() {
		world = WorldUtils.createWorld(new WorldCreator(uuid), PlotManager.getPlotDirectory());
		
		PlotManager.loadedPlots.put(uuid, this);
		
		for (WorldServer server : MinecraftServer.getServer().worlds) {
			System.out.println("World '" + server.getWorldData().getName() + "' loaded!");
		}
		return this;
	}
	
	public void save() {
		world.save();
	}
	
	public void end() {
		//save();
		WorldUtils.unloadWorld(world);
		
		PlotManager.loadedPlots.remove(uuid);
	}

}
