package com.swingsword.ssengine.rank;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import com.swingsword.ssengine.utils.ScoreboardUtils;

public class Rank {

	public String name;
	private String display;
	public String altRank;
	public String joinMsg;
	public boolean staff;
	public List<String> perms;
	
	public Rank(String name, String display, String altRank, String joinMsg, boolean staff, List<String> perms) {
		this.name = name;
		this.display = display;
		this.altRank = altRank;
		this.joinMsg = joinMsg;
		this.staff = staff;
		this.perms = perms;

		RankManager.ranks.add(this);
		if (ScoreboardUtils.getTeam(name) != null) {
			ScoreboardUtils.getTeam(name).unregister();
		}
		Team team = ScoreboardUtils.createTeam(name);
		team.setPrefix(getRankDisplay() + ChatColor.RESET);
	}

	public Rank getAltRank() {
		if (altRank != null) {
			if (RankManager.getRank(altRank) != null) {
				return RankManager.getRank(altRank);
			}
		}
		return this;
	}
	
	public String getRankDisplay() {
		return display.replace("&", ChatColor.COLOR_CHAR + "") + " ";
	}
}
