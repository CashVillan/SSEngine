package com.swingsword.ssengine.game.games.agar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.Game;
import com.swingsword.ssengine.game.GamePlugin;
import com.swingsword.ssengine.game.PreventionSet;
import com.swingsword.ssengine.game.games.agar.listeners.PlayerEditEvent;
import com.swingsword.ssengine.game.games.agar.listeners.PlayerMove;
import com.swingsword.ssengine.game.map.MapType;
import com.swingsword.ssengine.game.team.Team;

public class Agar extends GamePlugin implements Listener {

	public static Agar plugin;
	
	public void onDisable() {
		for(Player all : AgarManager.agaring.keySet()) {
			AgarManager.stopAgar(all);
		}
		for(AgarEntity ent : AgarEntity.agarEntities) {
			ent.entity.remove();
		}
	}

	public Game onEnable() {
		plugin = this;
		
		Bukkit.getServer().getPluginManager().registerEvents(this, MasterPlugin.getMasterPlugin());
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					all.setFoodLevel(20);
					all.setSaturation(10);
				}
			}
		}, 3, 3);
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule keepInventory true");
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new AgarManager(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new PlayerMove(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new PlayerEditEvent(), MasterPlugin.getMasterPlugin());
		
		return new Game(this, 0, 24, true, false, false, true, new PreventionSet(), null, MapType.DYNAMIC, null) {

			@Override
			public void onEnd() {
			}

			@Override
			public void onLoad() {
				List<Entity> remove = new ArrayList<Entity>();
				for(Entity all : Bukkit.getWorld("map").getEntities()) {
					if(!(all instanceof Player)) {
						remove.add(all);
					}
				}
				for(Entity all : remove) {
					all.remove();
				}
			}

			@Override
			public void onPlayerJoin(Player player) {
				Game.resetPlayer(player);
				player.setGameMode(GameMode.CREATIVE);
				AgarManager.spawnPlayer(player);
			}

			@Override
			public void onPlayerQuit(Player player) {
				AgarManager.stopAgar(player);
			}

			public void onTeamJoin(Player player, Team team) { }
			
			@Override
			public void onStart() {
				for(Entity all : Bukkit.getWorld("map").getEntities()) {
					if(!(all instanceof Player)) {
						all.remove();
					}
				}
				
				for(Player all : Bukkit.getOnlinePlayers()) {
					AgarManager.spawnPlayer(all);
				}
				
				new ScoreboardManager();
				new ThreadManager();
			}
		};
	}
	
	public static boolean isInArea(Location loc) {
		if(loc.getBlockX() > AgarManager.mapSize + 1 || loc.getBlockX() < 0 || loc.getBlockZ() > AgarManager.mapSize + 1 || loc.getBlockZ() < 0) {
			return false;
		}
		return true;
	}
	
	public static String getTimeString(int seconds) {
	    int hours = (int) seconds / 3600;
	    int remainder = (int) seconds - hours * 3600;
	    int mins = remainder / 60;
	    remainder = remainder - mins * 60;
	    int secs = remainder;
	    
	    if(hours > 0) {
	    	return hours + ":" + mins + ":" + secs;
	    } else {
	    	return mins + ":" + secs;
	    }
	}
}