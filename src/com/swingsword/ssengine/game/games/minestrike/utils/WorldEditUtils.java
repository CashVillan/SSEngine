package com.swingsword.ssengine.game.games.minestrike.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.swingsword.ssengine.game.games.minestrike.Minestrike;
import com.swingsword.ssengine.game.games.minestrike.zones.BombSite;
import com.swingsword.ssengine.game.games.minestrike.zones.BuyZone;

public class WorldEditUtils implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("createbuyarea")) {
			Selection s = Minestrike.plugin.we.getSelection(p);
			
			if(s == null || s.getMaximumPoint() == null || s.getMinimumPoint() == null) {
				sender.sendMessage("§cMake a WE selection first.");
				return true;
			}
			
			CuboidSelection cs = new CuboidSelection(s.getWorld(), s.getMinimumPoint(), s.getMaximumPoint());
			
			BuyZone.addBuyZone(cs);
			
			sender.sendMessage("§aYou have set a new buy zone! Reload/restart to apply changes.");
		}
			
		if(cmd.getName().equalsIgnoreCase("setbombsite")) {
			Selection s = Minestrike.plugin.we.getSelection(p);
			
			if(s == null || s.getMaximumPoint() == null || s.getMinimumPoint() == null) {
				sender.sendMessage("§cMake a WE selection first.");
				return true;
			}
			
			CuboidSelection cs = new CuboidSelection(s.getWorld(), s.getMinimumPoint(), s.getMaximumPoint());
			
			BombSite.addBombSite(cs);
			
			sender.sendMessage("§aYou have set the new bomb site! Reload/restart to apply changes.");
		}
		
		return false;
	}
}
