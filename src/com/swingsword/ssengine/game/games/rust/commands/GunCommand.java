package com.swingsword.ssengine.game.games.rust.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.swingsword.ssengine.game.games.rust.guns.Gun;
import com.swingsword.ssengine.game.games.rust.guns.Guns;

public class GunCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("gun")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				
				if (Gun.isGun(player.getItemInHand())) {
					Gun gun = Gun.getGun(player.getItemInHand().getItemMeta().getDisplayName());
					
					if(gun != null) {
						Inventory equip = Bukkit.createInventory(player, 9, gun.getName() + " Equip Menu");
						
						Guns.playerEquip.put(player, equip);
						player.openInventory(equip);
					}
				}
				player.sendMessage(ChatColor.RED + "You need to hold a gun to use that command.");
				return false;
			}
		}
		if (cmd.getName().equalsIgnoreCase("f")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if(Guns.f.contains(player)) {
					Guns.f.remove(player);
					player.sendMessage(ChatColor.GRAY + "Disabled flashlight.");
				} else {
					Guns.f.add(player);
					player.sendMessage(ChatColor.GRAY + "Enabled flashlight.");
				}
			}
		}
		return false;
	}
}
