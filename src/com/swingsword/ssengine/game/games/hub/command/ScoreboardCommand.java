package com.swingsword.ssengine.game.games.hub.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.games.hub.utils.HubScoreboard;

public class ScoreboardCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(sender instanceof Player) {
			((Player) sender).openInventory(HubScoreboard.getScoreboardInv((Player) sender));
		}
		return false;
	}
}