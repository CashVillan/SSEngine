package com.swingsword.ssengine.game.games.hub;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.entity.InteractEntityManager;
import com.swingsword.ssengine.game.Game;
import com.swingsword.ssengine.game.GamePlugin;
import com.swingsword.ssengine.game.PreventionSet;
import com.swingsword.ssengine.game.games.hub.command.FlyCommand;
import com.swingsword.ssengine.game.games.hub.command.HubminCommand;
import com.swingsword.ssengine.game.games.hub.command.PortalCommand;
import com.swingsword.ssengine.game.games.hub.command.ScoreboardCommand;
import com.swingsword.ssengine.game.games.hub.listeners.BlockListener;
import com.swingsword.ssengine.game.games.hub.listeners.InteractEntityListener;
import com.swingsword.ssengine.game.games.hub.listeners.InventoryListener;
import com.swingsword.ssengine.game.games.hub.listeners.PlayerListener;
import com.swingsword.ssengine.game.games.hub.listeners.PortalListener;
import com.swingsword.ssengine.game.games.hub.utils.FestiveUtils;
import com.swingsword.ssengine.game.games.hub.utils.HubScoreboard;
import com.swingsword.ssengine.game.games.hub.utils.ParticleManager;
import com.swingsword.ssengine.game.games.hub.utils.PortalUtils;
import com.swingsword.ssengine.game.map.MapType;
import com.swingsword.ssengine.game.team.Team;

public class Hub extends GamePlugin {
	
	public static Hub plugin;
	public WorldEditPlugin worldEdit = null;
	public static boolean stopped;
	
	public Game onEnable() {
		plugin = this;
		stopped = false;
		
		this.worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					all.setFoodLevel(20);
				}
			}
		}, 3, 3);
		
		return new Game(this, 0, 50, true, false, false, false, new PreventionSet(), null, MapType.DYNAMIC, null) {
			
			@Override
			public void onStart() { }
			
			@Override
			public void onPlayerQuit(Player player) { }
			
			@Override
			public void onPlayerJoin(Player player) { }
			
			@Override
			public void onTeamJoin(Player player, Team team) { }
			
			@Override
			public void onLoad() {
				for (Entity all : Bukkit.getWorld("map").getEntities()) {
					all.remove();
				}
				
				registerListeners();
				registerCommands();
				PortalUtils.loadPortals();
				new HubScoreboard();
				new ParticleManager();
				new InteractEntityManager(true);
			}
			
			@Override
			public void onEnd() { }
		};
	}
	
	public void onDisable() {
		stopped = true;
		PortalUtils.savePortalsData();
	}
	
	public void registerListeners() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new PortalListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new PlayerListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new InventoryListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new BlockListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new InteractEntityListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new FestiveUtils(), MasterPlugin.getMasterPlugin());
	}
	
	public void registerCommands() {
		MasterPlugin.getMasterPlugin().getCommand("portal").setExecutor(new PortalCommand());
		MasterPlugin.getMasterPlugin().getCommand("scoreboard").setExecutor(new ScoreboardCommand());
		MasterPlugin.getMasterPlugin().getCommand("hubmin").setExecutor(new HubminCommand());
		MasterPlugin.getMasterPlugin().getCommand("fly").setExecutor(new FlyCommand());
	}
	
	public static Hub getInstance() {
		return plugin;
	}

}
