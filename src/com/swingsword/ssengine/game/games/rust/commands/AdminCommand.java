package com.swingsword.ssengine.game.games.rust.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.game.games.rust.guns.Ammo;
import com.swingsword.ssengine.game.games.rust.guns.Gun;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.ItemUtils;

public class AdminCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("admin")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (sender.isOp()) {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("guns")) {
							player.getInventory().addItem(Gun.getGun("9mm Pistol").toItemStack());
							player.getInventory().addItem(Gun.getGun("P250").toItemStack());
							player.getInventory().addItem(Gun.getGun("MP5A4").toItemStack());
							player.getInventory().addItem(Gun.getGun("M4").toItemStack());
							player.getInventory().addItem(Gun.getGun("Bolt Action Rifle").toItemStack());
							player.getInventory().addItem(Gun.getGun("Shotgun").toItemStack());
							player.getInventory().addItem(Gun.getGun("Wow").toItemStack());
							player.getInventory().addItem(Ammo.getAmmo("9mm Ammo").toItemStack(64));
							player.getInventory().addItem(Ammo.getAmmo("556 Ammo").toItemStack(64));
							player.getInventory().addItem(Ammo.getAmmo("Shotgun Shells").toItemStack(64));
							player.sendMessage(ChatColor.GREEN + "Items added!");
							return true;
						}
						if (args[0].equalsIgnoreCase("building")) {
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(264), 64, 0, ChatColor.WHITE + "Wood Foundation", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(266), 64, 0, ChatColor.WHITE + "Wood Pillar", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(281), 64, 0, ChatColor.WHITE + "Wood Wall", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(287), 64, 0, ChatColor.WHITE + "Wood Doorway", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(288), 64, 0, ChatColor.WHITE + "Wood Window", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(377), 64, 0, ChatColor.WHITE + "Wood Stairs", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(296), 64, 0, ChatColor.WHITE + "Wood Ceiling", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(336), 64, 0, ChatColor.WHITE + "Metal Foundation", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(338), 64, 0, ChatColor.WHITE + "Metal Pillar", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(353), 64, 0, ChatColor.WHITE + "Metal Wall", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(371), 64, 0, ChatColor.WHITE + "Metal Doorway", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(372), 64, 0, ChatColor.WHITE + "Metal Window", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(378), 64, 0, ChatColor.WHITE + "Metal Stairs", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(388), 64, 0, ChatColor.WHITE + "Metal Ceiling", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.CHEST, 64, 0, ChatColor.WHITE + "Large Wood Storage Box", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.CHEST, 64, 0, ChatColor.WHITE + "Wood Storage Box", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.getMaterial(405), 64, 0, ChatColor.WHITE + "Sleeping Bag", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.BED_BLOCK, 64, 0, ChatColor.WHITE + "Bed", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.STEP, 64, 0, ChatColor.WHITE + "Camp Fire", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.ANVIL, 64, 0, ChatColor.WHITE + "Furnace", null));
							player.getInventory().addItem(ItemUtils.createItem(Material.STONE_SLAB2, 64, 0, ChatColor.WHITE + "Workbench", null));
							player.sendMessage(ChatColor.GREEN + "Items added!");
							return true;
						}
						if(args[0].equalsIgnoreCase("doorinfo")) {
							Location loc = player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 10).getLocation();
							
							if (ConfigUtils.getConfig("data/doors").getConfigurationSection("doors").contains(LocationUtils.RealLocationToString(loc))) {
								ConfigurationSection door = ConfigUtils.getConfig("data/doors").getConfigurationSection("doors").getConfigurationSection(LocationUtils.RealLocationToString(loc));
								
								player.sendMessage("Owner: " + Bukkit.getOfflinePlayer(UUID.fromString(door.getString("owner"))).getName());
								player.sendMessage("Code: " + door.getString("code"));
								player.sendMessage("Unlocked by: ");
								for(String unlocked : door.getStringList("people")) {
									player.sendMessage(Bukkit.getOfflinePlayer(UUID.fromString(unlocked)).getName());
								}
								
							} else {
								player.sendMessage(ChatColor.RED + "Door not registered.");
							}
							return true;
						}
						return true;
					}
					sender.sendMessage(ChatColor.RED + "Invalid option.");
					return false;
				}
				sender.sendMessage(ChatColor.RED + "Usage: /admin <option>");
				return false;
			}
			sender.sendMessage(ChatColor.RED + "No Permission.");
		}
		return false;
	}

}
