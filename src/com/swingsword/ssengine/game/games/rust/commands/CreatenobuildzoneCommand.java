package com.swingsword.ssengine.game.games.rust.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.building.Building;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.utils.ConfigUtils;

public class CreatenobuildzoneCommand implements CommandExecutor {

	@SuppressWarnings({ "deprecation", "static-access" })
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("createnobuildzone")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.isOp()) {
					try {
						if (Rust.getInstance().we.getAPI().getSession(player).getRegion().getMinimumPoint() != null && Rust.getInstance().we.getAPI().getSession(player).getRegion().getMaximumPoint() != null) {
							com.sk89q.worldedit.Vector loc1 = Rust.getInstance().we.getAPI().getSession(player).getRegion().getMinimumPoint();
							com.sk89q.worldedit.Vector loc2 = Rust.getInstance().we.getAPI().getSession(player).getRegion().getMaximumPoint();

							CuboidRegion r = new CuboidRegion(loc1, loc2);
							Building.noBuildZones.add(r);

							FileConfiguration config = ConfigUtils.getConfig("zones");
							List<String> noBuild = config.getStringList("noBuildZones");
							if (noBuild == null) {
								noBuild = new ArrayList<String>();
							}
							noBuild.add(LocationUtils.locationToString(Rust.getInstance().we.getAPI().getSession(player).getRegion().getMinimumPoint()) + ";" + LocationUtils.locationToString(Rust.getInstance().we.getAPI().getSession(player).getRegion().getMaximumPoint()));
							config.set("noBuildZones", noBuild);
							ConfigUtils.saveConfig(config, "zones");

							player.sendMessage(ChatColor.GREEN + "Added noBuild region.");
							return true;
						}
						player.sendMessage(ChatColor.RED + "Make a WE selection.");
						return false;
					} catch (IncompleteRegionException e) {
						player.sendMessage(ChatColor.RED + "Make a WE selection.");
					}
					return false;
				}
				player.sendMessage(ChatColor.RED + "No permission.");
				return false;
			}
		}
		return false;
	}
}
