package com.swingsword.ssengine.game.games.rust.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.game.games.rust.utils.RadiationUtils;
import com.swingsword.ssengine.utils.ConfigUtils;

public class CreateradzoneCommand implements CommandExecutor {
	
	@SuppressWarnings({ "deprecation", "static-access" })
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("createradzone")) {
			if (sender instanceof Player) {
				if (sender.isOp()) {
					try {
						if (Rust.getInstance().we.getAPI().getSession((Player) sender).getRegion().getMinimumPoint() != null && Rust.getInstance().we.getAPI().getSession((Player) sender).getRegion().getMaximumPoint() != null) {
							FileConfiguration config = ConfigUtils.getConfig("zones");
							List<String> list = config.getStringList("radZones");
							list.add(LocationUtils.locationToString(Rust.getInstance().we.getAPI().getSession((Player) sender).getRegion().getMinimumPoint()) + ";" + LocationUtils.locationToString(Rust.getInstance().we.getAPI().getSession((Player) sender).getRegion().getMaximumPoint()));
							config.set("radZones", list);
							ConfigUtils.saveConfig(config, "zones");
							
							RadiationUtils.regions.clear();
							for (String all : Rust.plugin.getLoadedMap().getMapConfig().getStringList("radZones")) {
								Vector loc1 = LocationUtils.locationFromString(all.split(";")[0]);
								Vector loc2 = LocationUtils.locationFromString(all.split(";")[1]);

								CuboidRegion r = new CuboidRegion(loc1, loc2);
								RadiationUtils.regions.add(r);
							}
							sender.sendMessage(ChatColor.GREEN + "Added Radiation zone.");
							return true;
						}
						sender.sendMessage(ChatColor.RED + "Make a WE selection.");
						return false;
					} catch (IncompleteRegionException e) {
						sender.sendMessage(ChatColor.RED + "Make a WE selection.");
					}
					return false;
				}
				sender.sendMessage(ChatColor.RED + "No Permission!-");
				return false;
			}
		}
		return false;
	}
}
