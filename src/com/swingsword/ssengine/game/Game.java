package com.swingsword.ssengine.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.game.map.Map;
import com.swingsword.ssengine.game.map.MapType;
import com.swingsword.ssengine.game.team.Team;
import com.swingsword.ssengine.game.team.TeamConfig;
import com.swingsword.ssengine.server.ServerManager;
import com.swingsword.ssengine.stats.StatManager;
import com.swingsword.ssengine.utils.IntegerUtils;
import com.swingsword.ssengine.utils.ScoreboardUtils;
import com.swingsword.ssengine.utils.SpectatorUtils;

public abstract class Game {
	
	public GamePlugin gamePlugin;
	
	public int minPlayers;
	public int maxPlayers;
	public String packURL;
	
	public boolean rejoinTeam = false;
	public boolean balanceTeam = true;
	
	public boolean canJoinMidGame;
	public boolean endGameOnLessThanMinPlayers;
	public boolean hasLobby;
	public boolean alwaysDay;
	
	PreventionSet preventSet;
	public List<Team> teams;
	
	MapType mapType;
	Map map;
	
	public boolean hasStarted = false;
	
	@SuppressWarnings("deprecation")
	public Game(final GamePlugin gamePlugin, int minPlayers, int maxPlayers, boolean canJoinMidGame, boolean endGameOnLessThanMinPlayers, final boolean hasLobby, final boolean alwaysDay, PreventionSet preventSet, final TeamConfig teamConfig, final MapType mapType, final String time) {
		this.gamePlugin = gamePlugin;
		
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.canJoinMidGame = canJoinMidGame;
		this.endGameOnLessThanMinPlayers = endGameOnLessThanMinPlayers;
		this.preventSet = preventSet;
		this.mapType = mapType;
				
		this.hasLobby = hasLobby;
		this.alwaysDay = alwaysDay;
		
		final Game finalGame = this;
		
		Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(hasLobby) {
					Map.getMap("lobby", "lobby").createMap("lobby");
				}
				
				if(mapType.equals(MapType.DYNAMIC)) {
					/*if(!Main.plugin.getConfig().getString("loadedStaticGamemode").equals("null")) {
						System.out.println(ChatColor.RED + "Can't load dynamic gamemode on static server.");
						return;
					}*/
					
					if(GameManager.currentMapName == null) {
						createRandomMap(gamePlugin);
					} else {
						createMap(Map.getMap(gamePlugin.getName(), GameManager.currentMapName));
					}
					
				} else if(mapType.equals(MapType.STATIC)) {	
				    if (time != null && IntegerUtils.convertTime(time) != 0) {
				    	
					}
					
					if(MasterPlugin.getMasterPlugin().getConfig().getString("loadedStaticMap").equals("false")) {
						createRandomMap(gamePlugin);
						
						MasterPlugin.getMasterPlugin().getConfig().set("loadedStaticGamemode", gamePlugin.getName());
						MasterPlugin.getMasterPlugin().getConfig().set("loadedStaticMap", "true");
						MasterPlugin.getMasterPlugin().saveConfig();
						
					} else {
						loadMap(Map.getMaps(gamePlugin.getName()).get(0));
					}
				}
		
				GameManager.currentGame = finalGame;
				ServerManager.gamemode = gamePlugin.getName();
								
				if(teamConfig == null) {
					teams = Arrays.asList(new Team("Players", ChatColor.WHITE));
				} else {
					teams = teamConfig.teams;
				}
				
				for(Team all : teams) {
					all.loadSpawns();
				}
				
				onLoad();

				if(!hasLobby) {
					GameManager.currentGame.start();
					GameState.setState(GameState.IN_GAME);
				}
			}
		});
		
		if (alwaysDay) {
			Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					World map = Bukkit.getWorld("map");
					if (map != null) {
						map.setTime(8000L);
					}
				}
			}, 0L, 600L);
		}
	}
	
	private void createRandomMap(GamePlugin gamePlugin) {
		ArrayList<Map> maps = Map.getMaps(gamePlugin.getName());
		if(maps.size() > 0) {
			Map randomMap = maps.get(new Random().nextInt(maps.size()));
			
			createMap(randomMap);
			
		} else {
			System.out.println(ChatColor.RED + "Can't find any maps.");
			return;
		}
	}
	
	private void createMap(Map map) {
		map = map.createMap("map");
		GameManager.currentMap = map;
		
		if(mapType.equals(MapType.DYNAMIC)) {
			ServerManager.map = map.name;
		}
	}
	
	private void loadMap(Map map) {
		map = map.loadMap("map");
		GameManager.currentMap = map;
		
		if(mapType.equals(MapType.DYNAMIC)) {
			ServerManager.map = map.name;
		}
	}
	
	public abstract void onLoad();
	
	public void start() {
		hasStarted = true;
		GameManager.currentPreventSet = preventSet;
		
		com.swingsword.ssengine.scoreboard.ScoreboardManager.doTitleLoop = true;
		for(Player all : Bukkit.getOnlinePlayers()) {
			ScoreboardUtils.unload(all);
			
			if(Team.hasTeam(all)) {
				GameManager.currentGame.onTeamJoin(all, Team.getTeam(all));
			}
		}
		
		onStart();
	}
	public abstract void onStart();
	
	public void end() {
		GameState.setState(GameState.POST_GAME);
		
		onEnd();
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			StatManager.addStat(all, "g_games_played", 1);
			all.sendMessage(ChatColor.GRAY + "The game you played has concluded.");
			Channel.sendToServer(all, Channel.getBestHub());
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					all.kickPlayer(ChatColor.RED + "An issue occured! No worries though! You can just log back in.");
				}
				
				Bukkit.shutdown();
			}
		}, 20);
	}
	public abstract void onEnd();
	
	//
	
	public abstract void onPlayerJoin(Player player);
	
	public abstract void onPlayerQuit(Player player);
	
	public abstract void onTeamJoin(Player player, Team team);
	
	public static void resetPlayer(Player player) {
		SpectatorUtils.setSpectating(player, false);
		SpectatorUtils.resetVisibility(player);
		
		player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(false);
		
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.getInventory().clear();
	}
}
