package com.swingsword.ssengine.game.games.buildit;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.Game;
import com.swingsword.ssengine.game.GamePlugin;
import com.swingsword.ssengine.game.PreventionSet;
import com.swingsword.ssengine.game.games.buildit.events.BlockBreak;
import com.swingsword.ssengine.game.games.buildit.events.BlockPlace;
import com.swingsword.ssengine.game.games.buildit.events.EntityExplode;
import com.swingsword.ssengine.game.games.buildit.events.PlayerDropItem;
import com.swingsword.ssengine.game.games.buildit.events.PlayerInteract;
import com.swingsword.ssengine.game.games.buildit.events.PlayerMove;
import com.swingsword.ssengine.game.map.MapType;
import com.swingsword.ssengine.game.team.Team;
import com.swingsword.ssengine.game.threads.TimerHandler;

public class BuildIt extends GamePlugin {
	
	public static BuildIt plugin;
	
	public static String theWord = null;
	
	@Override
	public Game onEnable() {
		plugin = this;
		
		PreventionSet set = new PreventionSet();
		set.canAttackEntities = false;
		set.canAttackPlayers = false;
		set.canBreak = true;
		set.canBuild = true;
		set.canPickupItems = true;
		set.doDamage = false;
		
		return new Game(this, 2, 24, false, true, true, true, set, null, MapType.DYNAMIC, null) {

			@Override
			public void onEnd() {
				for(BuildArea all : BuildArea.areas) {
					all.clearArea();
				}
			}

			@Override
			public void onLoad() { }

			@Override
			public void onPlayerJoin(Player player) { }

			@Override
			public void onPlayerQuit(Player player) { }

			@Override
			public void onTeamJoin(Player player, Team team) { }
			
			@Override
			public void onStart() {
				PluginManager pm = Bukkit.getPluginManager();
				pm.registerEvents(new BlockBreak(), MasterPlugin.getMasterPlugin());
				pm.registerEvents(new BlockPlace(), MasterPlugin.getMasterPlugin());
				pm.registerEvents(new PlayerMove(), MasterPlugin.getMasterPlugin());
				pm.registerEvents(new EntityExplode(), MasterPlugin.getMasterPlugin());
				pm.registerEvents(new PlayerInteract(), MasterPlugin.getMasterPlugin());
				pm.registerEvents(new PlayerDropItem(), MasterPlugin.getMasterPlugin());
				
				theWord = BuildSubject.subjects.get(new Random().nextInt(BuildSubject.subjects.size()));
				
				for(Entity all : Bukkit.getWorld("map").getEntities()) {
					if(!(all instanceof Player)) {
						all.remove();
					}
				}
				
				for(int x = 0; x < Bukkit.getOnlinePlayers().size(); x++) {
					final Player player = (Player) Bukkit.getOnlinePlayers().toArray()[x];
					final int finalX = x;
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
						public void run() {
							player.setGameMode(GameMode.CREATIVE);
							new BuildArea(player, finalX);
						}
					}, x + 1);
				}
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The subject is: " + ChatColor.GOLD + "" + ChatColor.BOLD + theWord + ChatColor.GREEN + "" + ChatColor.BOLD + ". Good luck! You have 2 minutes.");
						
						TimerHandler.createTimer("buildTime", 2 * 60, 20, null, null, new Runnable() {
							public void run() {
								Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Stoping building! Lets review some builds!");
								
								BuildArea.areas.get(0).review();
							}
						});
						
						new ScoreboardManager();
					}
				}, Bukkit.getOnlinePlayers().size() + 2);
			}
		};
	}
	
	public void onDisable() { }
}
