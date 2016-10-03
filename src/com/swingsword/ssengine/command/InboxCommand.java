package com.swingsword.ssengine.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.utils.InboxUtils;

public class InboxCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		Player player = null;
		
		if(arg0 instanceof Player) {
			player = (Player) arg0;
		}
		
		if(player != null) {
			player.openInventory(InboxUtils.getInboxInv(player, 0));
		}
		
		return false;
	}
}
