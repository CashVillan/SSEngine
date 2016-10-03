package com.swingsword.ssengine.game.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.GameState;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.games.minestrike.game.DeathManager;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.rank.Rank;
import com.swingsword.ssengine.rank.RankManager;
import com.swingsword.ssengine.utils.ScoreboardUtils;

public class Team {
	
	private static List<Team> allTeams = new ArrayList<Team>();
	private static HashMap<String, Team> playerTeams = new HashMap<String, Team>();
	
	private List<Location> spawns = new ArrayList<Location>();
	private String teamName;
	public ChatColor color;
	
	public Team(String teamName, ChatColor color) {
		this.teamName = teamName.trim();
		this.color = color;
		
		allTeams.add(this);
		
		for(Rank all : RankManager.ranks) {
			if(ScoreboardUtils.getTeam(teamName + all.name) != null) {
				ScoreboardUtils.getTeam(teamName + all.name).unregister();
			}
			org.bukkit.scoreboard.Team team = ScoreboardUtils.createTeam(teamName + all.name);
			team.setPrefix(all.getRankDisplay() + color + "");
		}
		
		if(ScoreboardUtils.getTeam(teamName) != null) {
			ScoreboardUtils.getTeam(teamName).unregister();
		}
		org.bukkit.scoreboard.Team team = ScoreboardUtils.createTeam(teamName);
		team.setPrefix(color + "");
	}
	
	public void loadSpawns() {
		spawns = getSpawnsFromConfig();
	}
	
	public List<Location> getSpawnsFromConfig() {
		List<Location> locs = new ArrayList<Location>();
		if(GameManager.currentMap.getMapConfig() != null && GameManager.currentMap.getMapConfig().contains("Spawns." + teamName)) {
			for(String loc : GameManager.currentMap.getMapConfig().getStringList("Spawns." + teamName)) {
				locs.add(com.swingsword.ssengine.utils.LocationUtils.stringToLocation(loc.replace(".0", "")));
			}
		}
		
		return locs;
	}
	
	public String getName() {
		return teamName;
	}
	
	public List<Location> getSpawns() {
		return spawns;
	}
	
	public List<org.bukkit.scoreboard.Team> getScoreboardTeams() {
		List<org.bukkit.scoreboard.Team> teams = new ArrayList<org.bukkit.scoreboard.Team>();
		
		teams.add(ScoreboardUtils.getTeam(teamName));
		for(Rank all : RankManager.ranks) {
			teams.add(ScoreboardUtils.getTeam(teamName + all.name));
		}
		
		return teams;
	}
	
	public void add(final Player player) {
		if(hasTeam(player)) {
			getTeam(player).remove(player);
		}
		
		playerTeams.put(player.getName(), this);
		if (PlayerSessionManager.getSession(player) != null && PlayerSessionManager.getSession(player).getAccount().getMainRank() != null) {
			ScoreboardUtils.addGamemodeTeam(player, getName() + PlayerSessionManager.getSession(player).getAccount().getMainRank().name);
			
		} else {
			ScoreboardUtils.addGamemodeTeam(player, getName());
			
			Thread t = new Thread(new Runnable() {
				public void run() {
					while(player.isOnline() && (PlayerSessionManager.getSession(player) == null || PlayerSessionManager.getSession(player).getAccount().getMainRank() == null)) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}						
					}
					
					if(player.isOnline() && PlayerSessionManager.getSession(player).getAccount().getMainRank() != null) {						
						ScoreboardUtils.removeGamemodeTeam(player, getName());
						ScoreboardUtils.addGamemodeTeam(player, getName() + PlayerSessionManager.getSession(player).getAccount().getMainRank().name);
					}
				}
			});
			t.start();
		}
		
		if(GameManager.currentGame.teams.size() > 1) {
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Joined team '" + color + teamName + ChatColor.AQUA + "'.");
		}
		
		if(GameState.isState(GameState.IN_GAME)) {
			GameManager.currentGame.onTeamJoin(player, this);
		}
	}
	
	public void remove(Player player) {
		if(hasTeam(player)) {
			playerTeams.remove(player.getName());
			
			if(PlayerSessionManager.getSession(player) != null && PlayerSessionManager.getSession(player).getAccount().getMainRank() != null) {
				ScoreboardUtils.removeGamemodeTeam(player, getName() + PlayerSessionManager.getSession(player).getAccount().getMainRank().name);
			} else {
				ScoreboardUtils.removeGamemodeTeam(player, getName());
			}
		}
	}
	
	public static boolean hasTeam(Player player) {
		return playerTeams.containsKey(player.getName());
	}
	
	public static Team getTeam(Player player) {
		if(!hasTeam(player)) { 
			return null;
		}
		return playerTeams.get(player.getName());
	}
	
	public static Team getTeam(String name) {
		for (Team t : allTeams) {
			if (t.teamName.equalsIgnoreCase(name)) {
				return t;	
			}
		}
		return null;
	}
	
	public int getAlive() {
		int alive = getPlayers().size();
		
		for(Player all : getPlayers()) {
			if(DeathManager.dead.contains(all.getName())) {
				alive--;
			}
		}
		
		return alive;
	}
	
	public static List<Team> GetAllTeams() {
		return allTeams;
	}
	
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<Player>();
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			if(getTeam(all) != null && getTeam(all) == this) {
				players.add(all);
			}
		}
		
		return players;
	}
	
	public static void addToRandomTeam(Player player) {
		int i = new Random().nextInt(Team.GetAllTeams().size());
		
		if(Team.GetAllTeams().size() == 0) {
			Bukkit.shutdown();
		}
		
		Team bestTeam = null;
		for(Team all : GetAllTeams()) {
			if(bestTeam == null || all.getPlayers().size() < bestTeam.getPlayers().size()) {
				bestTeam = all;
			}
		}
			
		bestTeam.add(player);
	}
	
	public static void switchTeams(Team team1, Team team2) {
		List<Player> team1players = team1.getPlayers();
		List<Player> team2players = team2.getPlayers();
		
		for(Player teamPlayer : team1players) {
			team1.remove(teamPlayer);
			team2.add(teamPlayer);
			
			CSGOGame.resetPlayer(teamPlayer);
		}
		for(Player teamPlayer : team2players) {
			team2.remove(teamPlayer);
			team1.add(teamPlayer);
			
			CSGOGame.resetPlayer(teamPlayer);
		}
	}
	
	public static boolean sameTeam(Player player, Player player2) {
		if(Team.getTeam(player) != null && Team.getTeam(player2) != null) {
			return (Team.getTeam(player).getName().startsWith(Team.getTeam(player2).getName()));
		} else {
			return true;
		}
	}
}
