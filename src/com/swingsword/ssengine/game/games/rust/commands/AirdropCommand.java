package com.swingsword.ssengine.game.games.rust.commands;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.utils.AirdropUtils;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;

public class AirdropCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("airdrop")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("dropcrate")) {
						if(player.isOp()) {
							Location dropLoc = new Location(player.getWorld(), player.getTargetBlock(new HashSet<Material>(), 100).getX(), player.getTargetBlock(new HashSet<Material>(), 100).getY(), player.getTargetBlock(new HashSet<Material>(), 100).getZ());
							
							AirdropUtils.dropCrate(dropLoc);
							Bukkit.broadcastMessage(ChatColor.GREEN + "A supply plane is flying over the following location: X: " + dropLoc.getBlockX() + "; Y: " + dropLoc.getBlockY() + "; Z: " + dropLoc.getBlockZ());
						}
						
					} else if(args[0].equalsIgnoreCase("setpos1")) {
						if(player.isOp()) {
							Rust.getInstance().getLoadedMap().getMapConfig().set("pos1.world", player.getWorld().getName());
							Rust.getInstance().getLoadedMap().getMapConfig().set("pos1.x", player.getLocation().getBlockX());
							Rust.getInstance().getLoadedMap().getMapConfig().set("pos1.y", player.getLocation().getBlockY());
							Rust.getInstance().getLoadedMap().getMapConfig().set("pos1.z", player.getLocation().getBlockZ());
							Rust.getInstance().getLoadedMap().saveMapConfig();
							player.sendMessage(ChatColor.GREEN + "You have set pos1 successfully.");
						}
					
					} else if(args[0].equalsIgnoreCase("setpos2")) {
						if(player.isOp()) {
							if(Rust.getInstance().getLoadedMap().getMapConfig().getString("pos1.world") != null) {
								if(Rust.getInstance().getLoadedMap().getMapConfig().getString("pos1.world").equalsIgnoreCase(player.getWorld().getName())) {
									if (LocationUtils.XZbiggerThan(new Location(Bukkit.getWorld(Rust.getInstance().getLoadedMap().getMapConfig().getString("pos1.world")), Rust.getInstance().getLoadedMap().getMapConfig().getInt("pos1.x"), Rust.getInstance().getLoadedMap().getMapConfig().getInt("pos1.y"), Rust.getInstance().getLoadedMap().getMapConfig().getInt("pos1.z")), player.getLocation())) {
										Rust.getInstance().getLoadedMap().getMapConfig().set("pos2.world", player.getWorld().getName());
										Rust.getInstance().getLoadedMap().getMapConfig().set("pos2.x", player.getLocation().getBlockX());
										Rust.getInstance().getLoadedMap().getMapConfig().set("pos2.y", player.getLocation().getBlockY());
										Rust.getInstance().getLoadedMap().getMapConfig().set("pos2.z", player.getLocation().getBlockZ());
										Rust.getInstance().getLoadedMap().saveMapConfig();
										player.sendMessage(ChatColor.GREEN + "You have set pos2 successfully. Please reload to apply changes.");
										
									} else {
										player.sendMessage(ChatColor.RED + "The X and Z have to be bigger than the X and Z of pos1.");
									}
								} else {
									player.sendMessage(ChatColor.RED + "You have be in the same world as the location of pos1.");
								}
							} else {
								player.sendMessage(ChatColor.RED + "You have to define pos1 first.");
							}
						}
					}
				}
			}
		}
		return false;
	}

}
