package com.swingsword.ssengine.game.games.minestrike.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.swingsword.ssengine.game.games.minestrike.game.DeathManager;
import com.swingsword.ssengine.game.team.Team;

public class PlayerChat implements Listener {

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(DeathManager.dead.contains(event.getPlayer().getName())) {			
			event.setMessage(ChatColor.GRAY + "[Spectator] " + event.getMessage().replaceFirst("@", ""));
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(!DeathManager.dead.contains(all.getName())) {
					event.getRecipients().remove(all);
				}
			}
			
			return;
		}
		
		if(event.getMessage().startsWith("@")) {			
			event.setMessage(ChatColor.DARK_PURPLE + "[Team] " + event.getMessage().replaceFirst("@", ""));
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(!Team.getTeam(event.getPlayer()).equals(Team.getTeam(all))) {
					event.getRecipients().remove(all);
				}
			}
		}
	}
}
