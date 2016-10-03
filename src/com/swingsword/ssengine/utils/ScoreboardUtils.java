package com.swingsword.ssengine.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.GameState;
import com.swingsword.ssengine.game.threads.LobbyCounter;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.scoreboard.ScoreboardManager;

public class ScoreboardUtils {
	
	public static void load(Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		
		syncTeams(player);
	}
	
	public static void unload(Player player) {
		if(player.getScoreboard() != null && player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
			List<Team> teams = new ArrayList<Team>();
			for (Team all : player.getScoreboard().getTeams()) {
				teams.add(all);
			}
			for(Team all : teams) {
				all.unregister();
			}
			
			List<Objective> os = new ArrayList<Objective>();
			for (Objective all : player.getScoreboard().getObjectives()) {
				os.add(all);
			}
			for(Objective o : os) {
				o.unregister();
			}
		}
	}
	
	public static void registerObjective(Player player, String diplay, DisplaySlot slot) {
		if(player.getScoreboard().getObjective(slot) != null) {
			player.getScoreboard().getObjective(slot).unregister();
		}
		
		Objective o = player.getScoreboard().registerNewObjective(slot.name() + new Random().nextInt(1000), "dummy");
		o.setDisplayName(diplay);
		o.setDisplaySlot(slot);
	}
	
	//Set title
	
	public static void setTitle(Player player, String message) {
		if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
			player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(message);
		}
	}
	
	//Add score
	
	public static void addScore(Player player, String message, int row) {
		if(player.getScoreboard() != null && PlayerSessionManager.playerSession.get(player.getName()) != null) {
			if(message == null) {
				int spaces = Integer.parseInt((row + "").replace("-", ""));
				message = ChatColor.WHITE + "";
				for (int x = 0; x < spaces; x++) {
					message = message + " ";
				}
			}
			
			if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
				removeScore(player, row);
				removeScore(player, message);
				
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(message).setScore(row);
			}
		}
	}
	
	//Remove score

	public static void removeScore(Player player, String message) {
		if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
			for(String entry : player.getScoreboard().getEntries()) {
				if(message.equals(entry)) {
					player.getScoreboard().resetScores(entry);
				}
			}
		}
	}
	
	public static void removeScore(Player player, int row) {
		if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
			for(String entry : player.getScoreboard().getEntries()) {
				if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(entry).getScore() == row) {
					player.getScoreboard().resetScores(entry);
				}
			}
		}
	}
	
	public static void resetScores(Player player) {
		if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
			for(String entry : player.getScoreboard().getEntries()) {
				player.getScoreboard().resetScores(entry);
			}
		}
	}
	
	//Teams
	
	public static Team createTeam(String team) {
		if(Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team) == null) {
			return Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(team);
		} else {
			return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team);
		}		
	}
	
	public static Team getTeam(String team) {
		return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team);
	}
	
	public static void removeTeam(String team) {
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team).unregister();
	}
	
	public static void syncTeams(Player player) {
		if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
			for(Team all : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
				if(player.getScoreboard().getTeam(all.getName()) == null) {
					cloneTeam(player, all);
				}
				
				if(player.getScoreboard().getTeam(all.getName()).getSize() != all.getSize()) {
					updateTeamPlayers(player.getScoreboard().getTeam(all.getName()), all);
				}
			}
		}
	}
	
	public static void cloneTeam(Player player, Team all) {
		if (player.getScoreboard().getTeam(all.getName()) == null) {
			player.getScoreboard().registerNewTeam(all.getName());
			
			Team team = player.getScoreboard().getTeam(all.getName());
			team.setDisplayName(all.getDisplayName());
			team.setPrefix(all.getPrefix());
			team.setSuffix(all.getSuffix());
			team.setAllowFriendlyFire(all.allowFriendlyFire());
			team.setCanSeeFriendlyInvisibles(all.canSeeFriendlyInvisibles());
			team.setNameTagVisibility(all.getNameTagVisibility());
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void updateTeamPlayers(Team to, Team from) {
		ArrayList<OfflinePlayer> remove = new ArrayList<OfflinePlayer>();
		for(OfflinePlayer all : to.getPlayers()) {
			remove.add(all);
		}
		for(OfflinePlayer all : remove) {
			to.removePlayer(all);
		}
		
		for(OfflinePlayer all : from.getPlayers()) {
			to.addPlayer(all);
		}
	}
	
	public static void addGamemodeTeam(Player player, String team) {
		if(!ScoreboardManager.playerGamemodeTeams.containsKey(player)) {
			ScoreboardManager.playerGamemodeTeams.put(player, new ArrayList<String>());
		}
		
		ScoreboardManager.playerGamemodeTeams.get(player).add(team);
	}
	
	@SuppressWarnings("deprecation")
	public static void removeGamemodeTeam(Player player, String team) {
		if(ScoreboardManager.playerGamemodeTeams.containsKey(player)) {
			if(player.getScoreboard().getTeam(team) != null) {
				player.getScoreboard().getTeam(team).removePlayer(player);
			}
			
			ScoreboardManager.playerGamemodeTeams.get(player).remove(team);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void removeGamemodeTeams(Player player) {
		if(ScoreboardManager.playerGamemodeTeams.containsKey(player)) {
			List<String> teams = new ArrayList<String>();
			for (String all : ScoreboardManager.playerGamemodeTeams.get(player)) {
				if(player.getScoreboard().getTeam(all) != null) {
					teams.add(all);
				}
			}
			for(String all : teams) {
				if(player.getScoreboard().getTeam(all) != null) {
					player.getScoreboard().getTeam(all).removePlayer(player);
				}
			}
			
			ScoreboardManager.playerGamemodeTeams.remove(player);
		}
	}

	public static void loadLobbyScoreboard(Player player) {
		String title = ChatColor.WHITE + "" + ChatColor.BOLD + "Waiting for Players";
		if(LobbyCounter.Time != -1) {
			title = ChatColor.WHITE + "" + ChatColor.BOLD + "Starting in " + ChatColor.GREEN + "" + ChatColor.BOLD + (LobbyCounter.delay - LobbyCounter.Time) + " Seconds";
		}
		
		try {
			ScoreboardUtils.registerObjective(player, title, DisplaySlot.SIDEBAR);
			ScoreboardUtils.addScore(player, null, 5);
			ScoreboardUtils.addScore(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "Players", 4);
			ScoreboardUtils.addScore(player, Bukkit.getOnlinePlayers().size() + "/" + "10", 3);
			ScoreboardUtils.addScore(player, null, 2);
			ScoreboardUtils.addScore(player, ChatColor.GREEN + "" + ChatColor.BOLD + "Credits", 1);
			ScoreboardUtils.addScore(player, PlayerSessionManager.getSession(player).getAccount().getCache().get("credits") + "", 0);
		} catch (Exception e) { }
	}
	
	public static void loopScoreboardLoad() {
		com.swingsword.ssengine.scoreboard.ScoreboardManager.doTitleLoop = false;
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(GameManager.currentGame != null) {
						if(GameState.isState(GameState.IN_LOBBY)) {
							loadLobbyScoreboard(all);
						}
					}
				}
			}
		}, 20, 20);
	}
}
