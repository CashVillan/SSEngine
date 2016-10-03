package com.swingsword.ssengine.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.utils.BlockUtils;

public class BlockCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, final String[] arg3) {
		Player player = null;
		
		if(arg0 instanceof Player) {
			player = (Player) arg0;
		}
		
		if(arg2.equalsIgnoreCase("block")) {
			if(player != null) {
				if(arg3.length == 0) {
					player.openInventory(BlockUtils.getBlockInv(player, 0));
					
				} else {
					final Player finalPlayer = player;
					
					Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
						public void run() {
							UUID uuid = SQLManager.getSQL("global").getUUID(arg3[0]);
							
							if(uuid != null) {
								if(!BlockUtils.getBlocks(finalPlayer).contains(uuid)) {
									BlockUtils.confirmBlock(finalPlayer, arg3[0], uuid, null);
								} else {
									BlockUtils.removeBlock(finalPlayer, arg3[0], uuid);
								}
							} else {
								finalPlayer.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "Player not found.");
							}
						}
					});
				}
			}
		}

		return false;
	}
}
