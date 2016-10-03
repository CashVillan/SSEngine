package com.swingsword.ssengine.game.games.minestrike.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.games.minestrike.Minestrike;
import com.swingsword.ssengine.game.games.minestrike.utils.SimpleLocation;

public class Command implements CommandExecutor {

		public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
			
			 if (cmd.getName().equalsIgnoreCase("setlocation")) {
				 Player p = (Player) sender;
				 
				 if(p.isOp()) {
					 if (args.length < 1) return true;
					 
					 //CT
					 if (args[0].equalsIgnoreCase("ct")) {
						 Integer spawnID = Integer.parseInt(args[1]);
	
						 Minestrike.plugin.getLoadedMap().getMapConfig().set("Spawns.CT.Spawn" + spawnID, new SimpleLocation(p.getLocation().getBlock().getLocation()).toString());
						 Minestrike.plugin.getLoadedMap().saveMapConfig();
						 
						 p.sendMessage(ChatColor.GREEN + "Set ct spawn " + spawnID + ".");
					 }
					 
					 //T
					 if (args[0].equalsIgnoreCase("t")) {
						 Integer spawnID = Integer.parseInt(args[1]);
	
						 Minestrike.plugin.getLoadedMap().getMapConfig().set("Spawns.T.Spawn" + spawnID, new SimpleLocation(p.getLocation().getBlock().getLocation()).toString());
						 Minestrike.plugin.getLoadedMap().saveMapConfig();
						 
						 p.sendMessage(ChatColor.GREEN + "Set t spawn " + spawnID + ".");
					 }
				 }
			 }
			return true;
		 }
	
}
