package com.swingsword.ssengine.game.team;

import java.util.List;

import org.bukkit.scoreboard.NameTagVisibility;

@SuppressWarnings("deprecation")
public class TeamConfig {

	public List<Team> teams;
	
	public TeamConfig(List<Team> teams, boolean friendlyFire, NameTagVisibility nametag) {
		this.teams = teams;
		
		for(Team team : teams) {
			for(org.bukkit.scoreboard.Team scoreboardTeam : team.getScoreboardTeams()) {
				scoreboardTeam.setAllowFriendlyFire(friendlyFire);
				scoreboardTeam.setNameTagVisibility(nametag);
			}
		}
	}
	
}
