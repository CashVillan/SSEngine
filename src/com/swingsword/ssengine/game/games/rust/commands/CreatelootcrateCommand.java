package com.swingsword.ssengine.game.games.rust.commands;

import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.game.games.rust.utils.LootUtils;
import com.swingsword.ssengine.utils.ConfigUtils;

public class CreatelootcrateCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("createlootcreate")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.isOp()) {
					if (player.getTargetBlock(new HashSet<Material>(), 50) != null && player.getTargetBlock(new HashSet<Material>(), 50).getType() == Material.CHEST) {
						player.getTargetBlock(new HashSet<Material>(), 50).setType(Material.TRAPPED_CHEST);
						FileConfiguration config = ConfigUtils.getConfig("zones");
						List<String> locs = config.getStringList("crates");
						locs.add(LocationUtils.RealLocationToString(player.getTargetBlock(new HashSet<Material>(), 50).getLocation()));
						config.set("crates", locs);
						ConfigUtils.saveConfig(config, "zones");

						LootUtils.createCrate(player.getTargetBlock(new HashSet<Material>(), 50).getLocation(), LootUtils.fillCrate(player.getTargetBlock(new HashSet<Material>(), 50).getLocation()), "Loot Crate");

						player.sendMessage(ChatColor.GREEN + "Loot crate created.");
						return true;
					}
					player.sendMessage(ChatColor.RED + "Look at a chest to create a loot crate.");
					return false;
				}
				player.sendMessage(ChatColor.RED + "No permission.");
				return false;
			}
		}
		return false;
	}
}
