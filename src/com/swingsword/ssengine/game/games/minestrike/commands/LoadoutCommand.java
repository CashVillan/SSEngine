package com.swingsword.ssengine.game.games.minestrike.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.swingsword.ssengine.game.games.minestrike.utils.BuyUtils;
import com.swingsword.ssengine.utils.ItemUtils;

public class LoadoutCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
		
		 if (cmd.getName().equalsIgnoreCase("loadout")) {
			 Player player = (Player) sender;
			 
			 if(args.length == 0) {
				 Inventory inv = Bukkit.createInventory(null, 27, "Select team loadout");
				 
				 inv.setItem(11, ItemUtils.createItem(Material.BLAZE_ROD, 1, 0, ChatColor.DARK_AQUA + "CT", null));
				 inv.setItem(15, ItemUtils.createItem(Material.BLAZE_POWDER, 1, 0, ChatColor.RED + "T", null));
				 
				 player.openInventory(inv);
				 
			 } else if(args.length == 1) {
				 if(args[0].equalsIgnoreCase("ct") || args[0].equalsIgnoreCase("t")) {
					 Inventory inv = BuyUtils.getBuyMenu(player, args[0].toUpperCase(), args[0].toUpperCase() + " Loadout");
					 
					 player.openInventory(inv);
				 }
			 }
		 }
		return true;
	 }
}
